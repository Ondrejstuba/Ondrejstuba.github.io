CREATE OR REPLACE PROCEDURE sp_enter_combat(
    p_postava_id INT,
    p_boj_id INT DEFAULT NULL
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_active_combat_id INT;
    v_new_combat_id INT;
    v_obratnost INT;
    v_inteligencia INT;
    v_trieda_modifier NUMERIC;
    v_max_ap INT;
    v_aktualne_kolo INT;
    v_participant_count INT;
BEGIN
    SELECT b.BojID INTO v_active_combat_id
    FROM ÚčastníkBoju ub
    JOIN Boj b ON b.BojID = ub.BojID
    WHERE ub.PostavaID = p_postava_id
      AND ub.JeAktívny = TRUE
      AND b.Stav != 'Skončený';

    IF v_active_combat_id IS NOT NULL THEN
        RAISE EXCEPTION 'Character is already in combat (ID: %)', v_active_combat_id;
    END IF;

    IF p_boj_id IS NOT NULL THEN
        PERFORM 1 FROM Boj WHERE BojID = p_boj_id AND Stav != 'Skončený';
        IF FOUND THEN
            IF EXISTS (SELECT 1 FROM ÚčastníkBoju 
                      WHERE BojID = p_boj_id AND PostavaID = p_postava_id) THEN
                UPDATE ÚčastníkBoju
                SET JeAktívny = TRUE
                WHERE BojID = p_boj_id AND PostavaID = p_postava_id;
            ELSE
                INSERT INTO ÚčastníkBoju (BojID, PostavaID, JeAktívny)
                VALUES (p_boj_id, p_postava_id, TRUE);
            END IF;
            v_active_combat_id := p_boj_id;
        ELSE
            INSERT INTO Boj (AktuálneKolo, Stav)
            VALUES (1, 'Aktívny')
            RETURNING BojID INTO v_new_combat_id;
            
            INSERT INTO ÚčastníkBoju (BojID, PostavaID, JeAktívny)
            VALUES (v_new_combat_id, p_postava_id, TRUE);
            
            v_active_combat_id := v_new_combat_id;
        END IF;
    ELSE
        INSERT INTO Boj (AktuálneKolo, Stav)
        VALUES (1, 'Aktívny')
        RETURNING BojID INTO v_new_combat_id;
        
        INSERT INTO ÚčastníkBoju (BojID, PostavaID, JeAktívny)
        VALUES (v_new_combat_id, p_postava_id, TRUE);
        
        v_active_combat_id := v_new_combat_id;
    END IF;

    SELECT COUNT(*) INTO v_participant_count
    FROM ÚčastníkBoju 
    WHERE BojID = v_active_combat_id AND JeAktívny = TRUE;

    UPDATE Boj SET
        Stav = CASE
            WHEN v_participant_count = 0 THEN 'Skončený'
            WHEN v_participant_count = 1 THEN 'Neaktívny'
            ELSE 'Aktívny'
        END
    WHERE BojID = v_active_combat_id;

    SELECT 
        pa_dex.Hodnota, 
        pa_int.Hodnota, 
        t.ModifikátorAP
    INTO 
        v_obratnost, 
        v_inteligencia, 
        v_trieda_modifier
    FROM Postava p
    JOIN Trieda t ON p.TriedaID = t.TriedaID
    LEFT JOIN PostavaAtribút pa_dex ON pa_dex.PostavaID = p.PostavaID AND pa_dex.AtribútID = 2
    LEFT JOIN PostavaAtribút pa_int ON pa_int.PostavaID = p.PostavaID AND pa_int.AtribútID = 4
    WHERE p.PostavaID = p_postava_id;

    v_max_ap := (v_obratnost + v_inteligencia) * v_trieda_modifier;

    UPDATE Postava
    SET AktuálneAP = v_max_ap
    WHERE PostavaID = p_postava_id;

    INSERT INTO ZáznamBoju (
        BojID,
        ČísloKola,
        PostavaID,
        TypAkcie,
        Výsledok
    )
    SELECT 
        v_active_combat_id,
        AktuálneKolo,
        p_postava_id,
        'Vstup do boja',
        v_max_ap
    FROM Boj
    WHERE BojID = v_active_combat_id;

    RAISE NOTICE 'Character % joined combat % (State: %, Participants: %)', 
        p_postava_id, 
        v_active_combat_id,
        (SELECT Stav FROM Boj WHERE BojID = v_active_combat_id),
        v_participant_count;
END;
$$;

CALL sp_enter_combat(14, 15); -- postava, boj
CALL sp_enter_combat(2, NULL); --postava (spravi novy boj/prida do nejakeho co prebieha)


UPDATE Účastníkboju set jeaktívny = FALSE where postavaid = 1;

SELECT * FROM "Účastníkboju";
SELECT * FROM záznamboju;
SELECT * FROM postava;
SELECT * FROM boj;










