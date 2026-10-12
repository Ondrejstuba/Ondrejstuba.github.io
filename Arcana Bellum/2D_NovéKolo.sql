CREATE OR REPLACE PROCEDURE sp_reset_round(
    p_boj_id       INT,
    p_force_reset  BOOLEAN DEFAULT FALSE
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_min_required_ap NUMERIC := 2;  
    v_all_depleted    BOOLEAN := TRUE;
    v_postava_id      INT;
    v_obratnost       INT;
    v_inteligencia    INT;
    v_trieda_modifier NUMERIC;
    v_new_ap          INT;
BEGIN
    IF NOT EXISTS (SELECT 1 FROM Boj WHERE BojID = p_boj_id AND Stav = 'Aktívny') THEN
        RAISE EXCEPTION 'Battle % is not active or does not exist', p_boj_id;
    END IF;

    IF NOT p_force_reset THEN
        FOR v_postava_id IN
            SELECT PostavaID
            FROM ÚčastníkBoju
            WHERE BojID = p_boj_id AND JeAktívny = TRUE
        LOOP
            SELECT AktuálneAP
            INTO   v_new_ap
            FROM   Postava
            WHERE  PostavaID = v_postava_id;

            IF v_new_ap >= v_min_required_ap THEN
                v_all_depleted := FALSE;
                EXIT;
            END IF;
        END LOOP;
    END IF;

    IF v_all_depleted OR p_force_reset THEN
        UPDATE Boj
        SET AktuálneKolo = AktuálneKolo + 1
        WHERE BojID = p_boj_id;

		RAISE NOTICE 'New round % started for battle %.', (SELECT AktuálneKolo FROM Boj WHERE BojID = p_boj_id), p_boj_id;

        FOR v_postava_id IN
            SELECT PostavaID
            FROM ÚčastníkBoju
            WHERE BojID = p_boj_id AND JeAktívny = TRUE
        LOOP
            SELECT 
                COALESCE(pa_dex.Hodnota, 0),
                COALESCE(pa_int.Hodnota, 0),
                t.ModifikátorAP
            INTO 
                v_obratnost, 
                v_inteligencia, 
                v_trieda_modifier
            FROM Postava p
            JOIN Trieda t ON p.TriedaID = t.TriedaID
            LEFT JOIN PostavaAtribút pa_dex 
                  ON pa_dex.PostavaID = p.PostavaID AND pa_dex.AtribútID = 2
            LEFT JOIN PostavaAtribút pa_int 
                  ON pa_int.PostavaID = p.PostavaID AND pa_int.AtribútID = 4
            WHERE p.PostavaID = v_postava_id;

            v_new_ap := (v_obratnost + v_inteligencia) * v_trieda_modifier;

            UPDATE Postava
            SET AktuálneAP = v_new_ap
            WHERE PostavaID = v_postava_id;

            INSERT INTO ZáznamBoju (
                BojID, ČísloKola, PostavaID, TypAkcie, Výsledok
            ) VALUES (
                p_boj_id,
                (SELECT AktuálneKolo FROM Boj WHERE BojID = p_boj_id),
                v_postava_id,
                'Nové kolo',
                v_new_ap
            );
        END LOOP;
	ELSE 
	    RAISE NOTICE 'No new round created for battle %. Some characters still have enough AP.', p_boj_id;
    END IF;
END;
$$;


CALL sp_reset_round(11); -- BojID
CALL sp_reset_round(11, TRUE); -- BojID, Bez debaty spravi nove kolo aj ked nemaju nedostatok ap


SELECT * FROM záznamboju;
SELECT * FROM boj;
SELECT * FROM Účastníkboju;
SELECT * FROM postava;


UPDATE postava
SET aktuálneAP = 1;