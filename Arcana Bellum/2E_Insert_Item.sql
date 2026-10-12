CREATE OR REPLACE PROCEDURE insert_random_item_into_battle(
    p_bojid INT,
    p_predmetid INT DEFAULT NULL
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_instance_id INT;
    v_predmet_id INT;
    v_bonus_chance FLOAT;
    v_chosen_attrib INT;
    v_bonus_value INT;
    v_status TEXT;
    v_bonus_max INT;
    v_bonus_count INT := 0;

    v_cena_ap NUMERIC;
    v_dmg NUMERIC;
    v_armor NUMERIC;

    v_attribs TEXT := '';
    v_attrib_name TEXT;
BEGIN
    SELECT Stav INTO v_status
    FROM Boj
    WHERE BojID = p_bojid;

    IF v_status IS NULL THEN
        RAISE EXCEPTION 'BojID % neexistuje.', p_bojid;
    ELSIF v_status = 'Skončený' THEN
        RAISE NOTICE 'BojID % je ukončený. Predmet nebude pridaný.', p_bojid;
        RETURN;
    END IF;

    IF p_predmetid IS NULL THEN
        SELECT PredmetID INTO v_predmet_id
        FROM Predmet
        ORDER BY random()
        LIMIT 1;
    ELSE
        PERFORM 1 FROM Predmet WHERE PredmetID = p_predmetid;
        IF NOT FOUND THEN
            RAISE EXCEPTION 'PredmetID % neexistuje.', p_predmetid;
        END IF;
        v_predmet_id := p_predmetid;
    END IF;

    SELECT 
        ROUND((p.MinCenyAP + (p.MaxCenyAP - p.MinCenyAP) * random())::numeric, 2),
        ROUND((p.MinPoškodenia + (p.MaxPoškodenia - p.MinPoškodenia) * random())::numeric, 2),
        ROUND((p.MinBrnenia + (p.MaxBrnenia - p.MinBrnenia) * random())::numeric, 2)
    INTO v_cena_ap, v_dmg, v_armor
    FROM Predmet p
    WHERE p.PredmetID = v_predmet_id;

    INSERT INTO InštanciaPredmetu (
        PredmetID, 
        ModifikátorCenyAP, 
        ModifikátorPoškodenia, 
        ModifikátorBrnenia,
        BojID
    )
    VALUES (
        v_predmet_id,
        v_cena_ap,
        v_dmg,
        v_armor,
        p_bojid
    )
    RETURNING InštanciaPredmetuID INTO v_instance_id;

    WHILE v_bonus_count < 4 LOOP
        CASE v_bonus_count
            WHEN 0 THEN v_bonus_chance := 0.2; v_bonus_max := 25;
            WHEN 1 THEN v_bonus_chance := 0.17; v_bonus_max := 20;
            WHEN 2 THEN v_bonus_chance := 0.10; v_bonus_max := 15;
            WHEN 3 THEN v_bonus_chance := 0.03; v_bonus_max := 10;
        END CASE;

        IF random() < v_bonus_chance THEN
            SELECT AtribútID INTO v_chosen_attrib
            FROM Atribút
            ORDER BY random()
            LIMIT 1;

            v_bonus_value := floor(random() * v_bonus_max) + 1;

            INSERT INTO PredmetAtribút (InštanciaPredmetuID, AtribútID, Hodnota)
            VALUES (v_instance_id, v_chosen_attrib, v_bonus_value);

            SELECT Názov INTO v_attrib_name
            FROM Atribút
            WHERE AtribútID = v_chosen_attrib;

            v_attribs := v_attribs || format('%s: %s, ', v_attrib_name, v_bonus_value);

            v_bonus_count := v_bonus_count + 1;
        ELSE
            EXIT;
        END IF;
    END LOOP;

    IF length(v_attribs) > 0 THEN
        v_attribs := left(v_attribs, length(v_attribs) - 2);
    ELSE
        v_attribs := 'žiadne';
    END IF;

    RAISE NOTICE '✅ Predmet % bol pridaný do boja %, inštancia %. CenaAP: %, Poškodenie: %, Brnenie: %, Atribúty: [%]',
        v_predmet_id, p_bojid, v_instance_id, v_cena_ap, v_dmg, v_armor, v_attribs;
END;
$$;





select * from boj;
CALL insert_random_item_into_battle(1); -- bojID
CALL insert_random_item_into_battle(11, 18); -- bojID, PredmetID

select * from inštanciapredmetu;
select * from predmet;

