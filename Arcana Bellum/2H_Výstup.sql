CREATE OR REPLACE PROCEDURE sp_leave_combat(
    p_postava_id INT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_boj_id INT;
    v_participant_count INT;
BEGIN
    SELECT ub.BojID
    INTO v_boj_id
    FROM ÚčastníkBoju ub
    JOIN Boj b ON b.BojID = ub.BojID
    WHERE ub.PostavaID = p_postava_id
      AND ub.JeAktívny = TRUE
      AND b.Stav != 'Skončený';

    IF v_boj_id IS NULL THEN
        RAISE EXCEPTION 'Character % is not in an active combat.', p_postava_id;
    END IF;

    UPDATE ÚčastníkBoju
    SET JeAktívny = FALSE
    WHERE PostavaID = p_postava_id AND BojID = v_boj_id;

    SELECT COUNT(*) INTO v_participant_count
    FROM ÚčastníkBoju
    WHERE BojID = v_boj_id AND JeAktívny = TRUE;

    UPDATE Boj
    SET Stav = CASE
        WHEN v_participant_count = 0 THEN 'Skončený'
        WHEN v_participant_count = 1 THEN 'Neaktívny'
        ELSE 'Aktívny'
    END
    WHERE BojID = v_boj_id;

    INSERT INTO ZáznamBoju (
        BojID,
        ČísloKola,
        PostavaID,
        TypAkcie,
        Výsledok
    )
    SELECT 
        v_boj_id,
        AktuálneKolo,
        p_postava_id,
        'Odchod z boja',
        0
    FROM Boj
    WHERE BojID = v_boj_id;

    RAISE NOTICE 'Character % left combat % (New state: %, Remaining participants: %)', 
        p_postava_id, 
        v_boj_id,
        (SELECT Stav FROM Boj WHERE BojID = v_boj_id),
        v_participant_count;
END;
$$;

CALL sp_leave_combat(14);

SELECT * FROM "Účastníkboju";
SELECT * FROM záznamboju;
SELECT * FROM postava;
SELECT * FROM boj;


