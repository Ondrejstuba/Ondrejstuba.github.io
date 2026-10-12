CREATE OR REPLACE PROCEDURE sp_rest(
    p_postava_id INT,
    p_healing_factor NUMERIC DEFAULT 1.0
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_in_combat BOOLEAN;
    v_trieda_id INT;
    v_bonus_brnenia INT;
    v_trieda_nazov VARCHAR(255);
    v_sila INT;
    v_obratnost INT;
    v_vydrz INT;
    v_inteligencia INT;
    v_max_zdravie INT;
    v_max_ap INT;
    v_current_health INT;
    v_current_ap INT;
    v_healed_amount INT;
    v_new_health INT;
BEGIN
    IF p_healing_factor < 0 OR p_healing_factor > 1 THEN
        RAISE EXCEPTION 'Healing factor must be between 0 and 1';
    END IF;

    SELECT EXISTS (
        SELECT 1
        FROM ÚčastníkBoju ub
        JOIN Boj b ON ub.BojID = b.BojID
        WHERE ub.PostavaID = p_postava_id
          AND ub.JeAktívny = TRUE
          AND b.Stav = 'Aktívny'
    ) INTO v_in_combat;

    IF v_in_combat THEN
        RAISE EXCEPTION 'Cannot rest while in active combat';
    END IF;

    SELECT p.TriedaID, t.Názov, t.BonusKBrneniu
    INTO v_trieda_id, v_trieda_nazov, v_bonus_brnenia
    FROM Postava p
    JOIN Trieda t ON p.TriedaID = t.TriedaID
    WHERE p.PostavaID = p_postava_id;

    SELECT 
        COALESCE((SELECT Hodnota FROM PostavaAtribút WHERE PostavaID = p_postava_id AND AtribútID = 1), 0),
        COALESCE((SELECT Hodnota FROM PostavaAtribút WHERE PostavaID = p_postava_id AND AtribútID = 2), 0),
        COALESCE((SELECT Hodnota FROM PostavaAtribút WHERE PostavaID = p_postava_id AND AtribútID = 3), 0),
        COALESCE((SELECT Hodnota FROM PostavaAtribút WHERE PostavaID = p_postava_id AND AtribútID = 4), 0)
    INTO v_sila, v_obratnost, v_vydrz, v_inteligencia;

    CASE v_trieda_nazov
        WHEN 'Križiak' THEN
            v_max_zdravie := (v_bonus_brnenia + (v_vydrz * 3) + (v_sila * 1.2) * 2;
        WHEN 'Podvodník' THEN
            v_max_zdravie := (v_bonus_brnenia + (v_vydrz * 2) + (v_obratnost * 1.5)) * 2;
        WHEN 'Čarodejník' THEN
            v_max_zdravie := (v_bonus_brnenia + (v_vydrz * 1.5) + (v_inteligencia * 1.3)) * 2;
        WHEN 'Klerik' THEN
            v_max_zdravie := (v_bonus_brnenia + (v_vydrz * 2.2) + (v_inteligencia * 0.4) + (v_sila * 0.3)) * 2;
        WHEN 'Žoldnier' THEN
            v_max_zdravie := (v_bonus_brnenia + (v_vydrz * 3) + v_obratnost) * 2;
        ELSE
            RAISE EXCEPTION 'Unknown class %', v_trieda_nazov;
    END CASE;

    SELECT (v_obratnost + v_inteligencia) * t.ModifikátorAP
    INTO v_max_ap
    FROM Trieda t
    WHERE t.TriedaID = v_trieda_id;

    SELECT AktuálneZdravie, AktuálneAP
    INTO v_current_health, v_current_ap
    FROM Postava
    WHERE PostavaID = p_postava_id;

    v_healed_amount := ROUND((v_max_zdravie - v_current_health) * p_healing_factor);
    v_new_health := LEAST(v_current_health + v_healed_amount, v_max_zdravie);

    UPDATE Postava
    SET AktuálneZdravie = v_new_health,
        AktuálneAP = v_max_ap
    WHERE PostavaID = p_postava_id;

    INSERT INTO ZáznamBoju (PostavaID, TypAkcie, Výsledok, Úspech)
    VALUES (p_postava_id, 'Odpočinok', v_healed_amount, TRUE);

    RAISE NOTICE 'Healed % HP (factor: %). New health: %/%', 
        v_healed_amount, p_healing_factor, v_new_health, v_max_zdravie;

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
$$;

CALL sp_rest(3, 0.5);  -- Postava, regeneracny faktor
CALL sp_rest(3);  -- Postava, regeneracny faktor 1

SELECT * FROM "Účastníkboju";
SELECT * FROM záznamboju;
SELECT * FROM postava;




UPDATE postava
SET aktuálnezdravie = 10;

UPDATE "Účastníkboju"
SET jeaktívny = FALSE;





