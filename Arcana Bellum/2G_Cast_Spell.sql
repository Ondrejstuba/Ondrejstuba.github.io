CREATE OR REPLACE PROCEDURE sp_cast_spell(
    p_caster_id INT,
    p_target_id INT,
    p_spell_id INT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_combat_id INT;
    v_effective_cost NUMERIC;
    v_caster_ap INT;
    v_d20_roll INT;
    v_hit_bonus NUMERIC;
    v_target_ac NUMERIC;
    v_damage NUMERIC;
    v_caster_class_id INT;
    v_target_class_id INT;
    v_spell_category_id INT;
    v_caster_attributes NUMERIC;
    v_damage_attributes NUMERIC;
    v_price_modifier NUMERIC := 1;
    v_damage_modifier NUMERIC := 1;
    v_armor_modifier NUMERIC := 1;
    v_target_health INT;
	v_caster_level INT;
    v_target_level INT;
    v_xp_gained NUMERIC;
    v_current_xp INT;
    v_next_xp INT;
	v_spell_learned    INT;   
	v_hit_text TEXT;
    v_active_count INTEGER;
BEGIN
	IF p_caster_id = p_target_id THEN
	    RAISE EXCEPTION 'Cannot cast spell on yourself';
	END IF;

	  PERFORM 1
	    FROM PostavaKúzlo
	   WHERE PostavaID = p_caster_id
	     AND KúzloID   = p_spell_id;
	  IF NOT FOUND THEN
	      RAISE EXCEPTION 'Postava % does not know spell %', p_caster_id, p_spell_id;
	  END IF;
	
    SELECT b.BojID INTO v_combat_id
    FROM ÚčastníkBoju ub
    JOIN Boj b ON ub.BojID = b.BojID
    WHERE ub.PostavaID = p_caster_id 
      AND ub.JeAktívny = TRUE 
      AND b.Stav = 'Aktívny';

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Caster is not in active combat';
    END IF;

    PERFORM 1 FROM ÚčastníkBoju 
    WHERE PostavaID = p_target_id 
      AND BojID = v_combat_id 
      AND JeAktívny = TRUE;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'Target is not in the same combat';
    END IF;

	WITH item_modifiers AS (
	    SELECT COALESCE(EXP(SUM(LN(ModifikátorCenyAP))), 1) AS mod_product
	    FROM InštanciaPredmetu
	    WHERE PostavaID = p_caster_id
	),
	raw_cost AS (
	    SELECT 
	        k.ZákladnáCena *
	        kk.ModifikátorAP *
	        (1 - (SUM(pa.Hodnota) / 100.0)) *
	        im.mod_product AS unrounded_cost
	    FROM Kúzlo k
	    JOIN KategóriaKúzla kk ON k.KategóriaKúzlaID = kk.KategóriaKúzlaID
	    JOIN AtribútKúzlo ak ON k.KúzloID = ak.KúzloID AND ak.Typ = 'ceny'
	    JOIN PostavaAtribút pa ON pa.AtribútID = ak.AtribútID AND pa.PostavaID = p_caster_id
	    CROSS JOIN item_modifiers im
	    WHERE k.KúzloID = p_spell_id
	    GROUP BY k.ZákladnáCena, kk.ModifikátorAP, im.mod_product
	)
	SELECT 
	    CASE 
	        WHEN unrounded_cost < 1 THEN 1  -- Min 1 AP
	        ELSE ROUND(unrounded_cost) 
	    END
	INTO v_effective_cost
	FROM raw_cost;

    SELECT AktuálneAP INTO v_caster_ap FROM Postava WHERE PostavaID = p_caster_id;

    IF v_caster_ap < v_effective_cost THEN
        RAISE EXCEPTION 'Not enough AP: Need %, has %', v_effective_cost, v_caster_ap;
    END IF;

    UPDATE Postava 
    SET AktuálneAP = AktuálneAP - v_effective_cost
    WHERE PostavaID = p_caster_id;

	RAISE NOTICE 'AP deducted: % - % = %', v_caster_ap, v_effective_cost, v_caster_ap - v_effective_cost;

    v_d20_roll := FLOOR(RANDOM() * 20) + 1;  

    SELECT 
        CASE t.Názov
            WHEN 'Križiak' THEN (pa_sila.Hodnota * 0.9) + (pa_con.Hodnota * 0.2) + 2
            WHEN 'Podvodník' THEN (pa_dex.Hodnota * 1.2) + (pa_int.Hodnota * 0.3) + 5
            WHEN 'Čarodejník' THEN (pa_int.Hodnota * 1.2) + (pa_dex.Hodnota * 0.1)
            WHEN 'Klerik' THEN (pa_sila.Hodnota * 0.6) + (pa_int.Hodnota * 0.4) + 1
            WHEN 'Žoldnier' THEN (pa_dex.Hodnota * 1.1) + (pa_sila.Hodnota * 0.5) + 1
            ELSE 0
        END
    INTO v_hit_bonus
    FROM Postava p
    JOIN Trieda t ON p.TriedaID = t.TriedaID
    LEFT JOIN PostavaAtribút pa_sila ON pa_sila.PostavaID = p.PostavaID AND pa_sila.AtribútID = 1
    LEFT JOIN PostavaAtribút pa_dex ON pa_dex.PostavaID = p.PostavaID AND pa_dex.AtribútID = 2
    LEFT JOIN PostavaAtribút pa_con ON pa_con.PostavaID = p.PostavaID AND pa_con.AtribútID = 3
    LEFT JOIN PostavaAtribút pa_int ON pa_int.PostavaID = p.PostavaID AND pa_int.AtribútID = 4
    WHERE p.PostavaID = p_caster_id;

    SELECT 
        3 + (1 + pa_dex.Hodnota/13.0) + (1 + pa_con.Hodnota / 15.0) + (t.BonusKBrneniu * (1 + p.level / 10.0)) + (FLOOR(RANDOM() * 10) + 1)
    INTO v_target_ac
    FROM Postava p
    JOIN Trieda t ON p.TriedaID = t.TriedaID
	LEFT JOIN PostavaAtribút pa_dex ON pa_dex.PostavaID = p.PostavaID AND pa_dex.AtribútID = 2  -- DEX
	LEFT JOIN PostavaAtribút pa_con ON pa_con.PostavaID = p.PostavaID AND pa_con.AtribútID = 3  -- INT    
	WHERE p.PostavaID = p_target_id;

    IF v_d20_roll = 1 THEN
	  v_damage := NULL;  -- skip
	ELSIF v_d20_roll = 20 OR (v_d20_roll + v_hit_bonus) >= v_target_ac THEN
        SELECT SUM(pa.Hodnota)
        INTO v_damage_attributes
        FROM AtribútKúzlo ak
        JOIN PostavaAtribút pa ON pa.AtribútID = ak.AtribútID
        WHERE ak.KúzloID = p_spell_id 
          AND ak.Typ = 'účinku'
          AND pa.PostavaID = p_caster_id;

		SELECT 
		    COALESCE(MAX(ip.ModifikátorPoškodenia), 1)
		INTO v_damage_modifier
		FROM InštanciaPredmetu ip
		WHERE ip.PostavaID = p_caster_id;
		
		SELECT 
		    COALESCE(MIN(ip_target.ModifikátorBrnenia), 1)
		INTO v_armor_modifier
		FROM InštanciaPredmetu ip_target
		WHERE ip_target.PostavaID = p_target_id;

        v_damage := (SELECT ZákladnéPoškodenie FROM Kúzlo WHERE KúzloID = p_spell_id)
                   * (1 + (v_damage_attributes / 150))
                   * v_damage_modifier
                   * v_armor_modifier;

		RAISE NOTICE 'Damage: base=%, attr mod=%, weapon mod=%, armor mod=% -> final=%',
	    (SELECT ZákladnéPoškodenie FROM Kúzlo WHERE KúzloID = p_spell_id),
	    (1 + (v_damage_attributes / 150)),
	    v_damage_modifier,
	    v_armor_modifier,
	    v_damage;

        UPDATE Postava
        SET AktuálneZdravie = GREATEST(AktuálneZdravie - v_damage, 0)
        WHERE PostavaID = p_target_id;
    END IF;

	SELECT AktuálneZdravie INTO v_target_health FROM Postava WHERE PostavaID = p_target_id;
    
    IF v_target_health <= 0 THEN

		WITH lost_items AS (
	            SELECT InštanciaPredmetuID 
	            FROM InštanciaPredmetu 
	            WHERE PostavaID = p_target_id
	        )
	        UPDATE PostavaAtribút pa
	        SET Hodnota = pa.Hodnota - COALESCE((
	            SELECT SUM(pd.Hodnota) 
	            FROM PredmetAtribút pd
	            WHERE pd.InštanciaPredmetuID IN (SELECT InštanciaPredmetuID FROM lost_items)
	              AND pd.AtribútID = pa.AtribútID
	        ), 0)
	        WHERE pa.PostavaID = p_target_id;

        UPDATE InštanciaPredmetu
        SET 
            PostavaID = NULL,
            BojID = v_combat_id
        WHERE PostavaID = p_target_id;

		UPDATE Postava
		SET AktuálnaVáhaInventára = 0
		WHERE PostavaID = p_target_id;

        UPDATE ÚčastníkBoju 
        SET JeAktívny = FALSE 
        WHERE PostavaID = p_target_id 
          AND BojID = v_combat_id;

		SELECT COUNT(*) INTO v_active_count
	    FROM ÚčastníkBoju
	    WHERE BojID = v_combat_id AND JeAktívny = TRUE;
	
	    IF v_active_count = 0 THEN
	        UPDATE Boj
	        SET Stav = 'Ukončený'
	        WHERE BojID = v_combat_id;
	
	        RAISE NOTICE 'Combat % has ended because no active participants remain.', v_combat_id;
	
	    ELSIF v_active_count = 1 THEN
	        UPDATE Boj
	        SET Stav = 'Neaktívny'
	        WHERE BojID = v_combat_id;
	
	        RAISE NOTICE 'Combat % is now inactive – only one active participant remains.', v_combat_id;
	
	    ELSE
	        UPDATE Boj
	        SET Stav = 'Aktívny'
	        WHERE BojID = v_combat_id;
	
	        RAISE NOTICE 'Combat % remains active – % participants still fighting.', v_combat_id, v_active_count;
	    END IF;

        INSERT INTO ZáznamBoju (
            BojID,
            ČísloKola,
            PostavaID,
            TypAkcie,
            CieľováPostavaID,
            Výsledok,
            Úspech
        )
        SELECT 
            v_combat_id,
            AktuálneKolo,
            p_target_id,        
            'Smrť', 
            NULL,              
            NULL,
            TRUE
        FROM Boj
        WHERE BojID = v_combat_id;

        SELECT Level INTO v_target_level FROM Postava WHERE PostavaID = p_target_id;
        SELECT Level, AktuálneEXP, NextLevelEXP 
        INTO v_caster_level, v_current_xp, v_next_xp 
        FROM Postava 
        WHERE PostavaID = p_caster_id;

       v_xp_gained := ROUND(
		    (v_target_level * 20 * 
		        (1 + GREATEST(0, (v_target_level - v_caster_level) * 0.05) *
		        (0.75 + RANDOM() * 0.50))  
		    )
		    * (0.90 + RANDOM() * 0.20)      
		);

        UPDATE Postava
        SET AktuálneEXP = AktuálneEXP + v_xp_gained
        WHERE PostavaID = p_caster_id
        RETURNING AktuálneEXP, NextLevelEXP INTO v_current_xp, v_next_xp;

        INSERT INTO ZáznamBoju (
            BojID,
            ČísloKola,
            PostavaID,
            TypAkcie,
            CieľováPostavaID,
            Výsledok,
            Úspech
        )
        SELECT 
            v_combat_id,
            AktuálneKolo,
            p_caster_id,
            '+EXP',
            p_target_id,
            v_xp_gained,
            TRUE
        FROM Boj
        WHERE BojID = v_combat_id;

        WHILE v_current_xp >= v_next_xp LOOP
            UPDATE Postava
            SET 
                Level = Level + 1,
                NextLevelEXP = ROUND(100 * POWER(Level + 2, 1.5))
            WHERE PostavaID = p_caster_id
            RETURNING Level, NextLevelEXP INTO v_caster_level, v_next_xp;

            UPDATE PostavaAtribút pa
            SET Hodnota = pa.Hodnota + ta.LevelUpHodnota
            FROM TriedaAtribút ta
            WHERE ta.TriedaID = (SELECT TriedaID FROM Postava WHERE PostavaID = p_caster_id)
              AND ta.AtribútID = pa.AtribútID
              AND pa.PostavaID = p_caster_id;

            INSERT INTO ZáznamBoju (
                BojID,
                ČísloKola,
                PostavaID,
                TypAkcie,
                Výsledok,
                Úspech
            )
            SELECT 
                v_combat_id,
                AktuálneKolo,
                p_caster_id,
                'Level Up',
                NULL,
                TRUE
            FROM Boj
            WHERE BojID = v_combat_id;

			WITH remaining AS (
			  SELECT k.KúzloID
			    FROM Kúzlo k
			   EXCEPT
			  SELECT pk.KúzloID
			    FROM PostavaKúzlo pk
			   WHERE pk.PostavaID = p_caster_id
			)
			INSERT INTO PostavaKúzlo (PostavaID, KúzloID)
			SELECT p_caster_id, r.KúzloID
			  FROM remaining r
			 ORDER BY random()
			 LIMIT 1
			RETURNING KúzloID INTO v_spell_learned;
			
			IF FOUND THEN
			  INSERT INTO ZáznamBoju (
			    BojID,
			    ČísloKola,
			    PostavaID,
			    TypAkcie,
			    KúzloID,
			    Úspech
			  )
			  VALUES (
			    v_combat_id,
			    (SELECT AktuálneKolo FROM Boj WHERE BojID = v_combat_id),
			    p_caster_id,
			    'Nové kúzlo',
			    v_spell_learned,
			    TRUE
			  );
			END IF;

            SELECT AktuálneEXP, NextLevelEXP 
            INTO v_current_xp, v_next_xp 
            FROM Postava 
            WHERE PostavaID = p_caster_id;
        END LOOP;
    END IF;


	IF v_d20_roll = 1 THEN
	v_hit_text := 'auto‑miss';
	ELSIF v_d20_roll = 20 THEN
    v_hit_text := 'critical hit!';
	ELSIF (v_d20_roll + v_hit_bonus) >= v_target_ac THEN
	v_hit_text := 'hit!';
	ELSE
	v_hit_text := 'miss';
	  END IF;

    INSERT INTO ZáznamBoju (
        BojID,
        ČísloKola,
        PostavaID,
        TypAkcie,
        KúzloID,
        CieľováPostavaID,
        VýsledokHoduKockou,
        Výsledok,
        Úspech
    )
    SELECT 
        v_combat_id,
        AktuálneKolo,
        p_caster_id,
        'Kúzlo',
        p_spell_id,
        p_target_id,
        v_d20_roll,
        COALESCE(v_damage, 0),
    	((v_d20_roll + v_hit_bonus) >= v_target_ac OR v_d20_roll = 20)
    FROM Boj
    WHERE BojID = v_combat_id;

	RAISE NOTICE
	    'Postava % zakúzlila kúzlo % na postavu % → % (roll % + bonus % vs AC %) , čím spôsobila % poškodenia a získala % skúseností.',
	    p_caster_id,
	    p_spell_id,
	    p_target_id,
	    v_hit_text,
	    v_d20_roll,
	    ROUND(v_hit_bonus::numeric,2),
	    v_target_ac,
	    COALESCE(v_damage,0),
	    COALESCE(v_xp_gained,0);

EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
$$;


CALL sp_cast_spell(14, 14, 25);  -- Postava, CielovaPostava, Kuzlo

SELECT * FROM "Účastníkboju" where bojid = 15;
SELECT * FROM záznamboju;
SELECT * FROM postava;
SELECT * FROM trieda;
SELECT * FROM inštanciapredmetu where postavaid = 4;
SELECT * FROM postavakúzlo;
SELECT * FROM atribút;
SELECT * FROM postavaatribút where postavaid = 1;
SELECT * FROM kategóriakúzla;
SELECT * FROM v_spellbook where postavaid = 14;

UPDATE inštanciapredmetu
SET PostavaID = NULL, BojID = 11;

UPDATE postava
SET aktuálnezdravie = 5, aktuálneap = 2000;

UPDATE "Účastníkboju"
SET jeaktívny = FALSE;






