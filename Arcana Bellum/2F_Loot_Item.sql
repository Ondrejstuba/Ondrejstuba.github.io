CREATE OR REPLACE PROCEDURE sp_loot_item(
    p_postava_id       INT,
    p_instanacne_item  INT  
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_current_health        INT;
    v_current_ap            INT;
    v_class_id              INT;
    v_current_inventory     NUMERIC;
    v_sila                  INT;
    v_vydrz                 INT;
    v_modifier_inventar     NUMERIC;
    v_max_inventory         NUMERIC;
	v_character_boj_id      INT;

    v_boj_id                INT;
    v_item_predmet_id       INT;
    v_item_weight           NUMERIC;

    v_allowed_class_exists  BOOLEAN;
    
    v_cislo_kola            INT;
    
    rec                     RECORD;  

BEGIN

    SELECT AktuálneZdravie, AktuálneAP, TriedaID, AktuálnaVáhaInventára
      INTO v_current_health, v_current_ap, v_class_id, v_current_inventory
      FROM Postava
     WHERE PostavaID = p_postava_id;
     
    IF v_current_health <= 0 THEN
        RAISE EXCEPTION 'Character % is dead and cannot loot items', p_postava_id;
    END IF;

    SELECT BojID, PredmetID
      INTO v_boj_id, v_item_predmet_id
      FROM InštanciaPredmetu
     WHERE InštanciaPredmetuID = p_instanacne_item
       AND PostavaID IS NULL;
       
    IF NOT FOUND THEN
        RAISE EXCEPTION 'Item instance % is not available for looting', p_instanacne_item;
    END IF;

    SELECT ub.BojID 
    INTO v_character_boj_id
    FROM ÚčastníkBoju ub
    JOIN Boj b ON ub.BojID = b.BojID
    WHERE ub.PostavaID = p_postava_id
      AND ub.JeAktívny = TRUE
      AND (b.Stav = 'Aktívny' or b.Stav = 'Neaktívny');
      
    IF NOT FOUND THEN
        RAISE EXCEPTION 'Character is not in combat';
    END IF;
    
    IF v_boj_id != v_character_boj_id THEN
        RAISE EXCEPTION 'Item is in combat % but character is in combat %', 
            v_boj_id, v_character_boj_id;
    END IF;
    
    SELECT Váha
      INTO v_item_weight
      FROM Predmet
     WHERE PredmetID = v_item_predmet_id;
     
    SELECT 
        (SELECT Hodnota FROM PostavaAtribút WHERE PostavaID = p_postava_id AND AtribútID = 1),
        (SELECT Hodnota FROM PostavaAtribút WHERE PostavaID = p_postava_id AND AtribútID = 3)
      INTO v_sila, v_vydrz;
      
    SELECT ModifikátorInventára
      INTO v_modifier_inventar
      FROM Trieda
     WHERE TriedaID = v_class_id;
     
    v_max_inventory := (v_sila + v_vydrz) * v_modifier_inventar;
    
    IF (v_current_inventory + v_item_weight) > v_max_inventory THEN
        RAISE EXCEPTION 'Not enough inventory capacity: current weight (%) + item weight (%) > max (%).',
            v_current_inventory, v_item_weight, v_max_inventory;
    END IF;
    
    IF v_current_ap < v_item_weight THEN
        RAISE EXCEPTION 'Not enough AP (% available) to loot item weighing %',
            v_current_ap, v_item_weight;
    END IF;

    SELECT EXISTS (
        SELECT 1 FROM PredmetTrieda
         WHERE PredmetID = v_item_predmet_id
           AND TriedaID = v_class_id
    ) INTO v_allowed_class_exists;
    
    IF NOT v_allowed_class_exists THEN
        RAISE EXCEPTION 'Item % is not allowed for character class %', v_item_predmet_id, v_class_id;
    END IF;

    UPDATE Postava
       SET AktuálneAP = AktuálneAP - v_item_weight,
           AktuálnaVáhaInventára = AktuálnaVáhaInventára + v_item_weight
     WHERE PostavaID = p_postava_id;
     
    UPDATE InštanciaPredmetu
       SET PostavaID = p_postava_id,
           BojID = NULL
     WHERE InštanciaPredmetuID = p_instanacne_item;
     
    FOR rec IN 
        SELECT AtribútID, Hodnota
          FROM PredmetAtribút 
         WHERE InštanciaPredmetuID = p_instanacne_item
    LOOP
        UPDATE PostavaAtribút
           SET Hodnota = Hodnota + rec.Hodnota
         WHERE PostavaID = p_postava_id
           AND AtribútID = rec.AtribútID;
    END LOOP;
     
    BEGIN
        SELECT AktuálneKolo
          INTO v_cislo_kola
          FROM Boj
         WHERE BojID = v_boj_id;
    EXCEPTION WHEN OTHERS THEN
        v_cislo_kola := NULL;
    END;
    
    INSERT INTO ZáznamBoju (
         BojID,
         ČísloKola,
         PostavaID,
         TypAkcie,
         InštanciaPredmetuID,
         Výsledok,
		 Úspech
    )
    VALUES (
         v_boj_id,
         v_cislo_kola,
         p_postava_id,
         'Zobratie predmetu',
         p_instanacne_item,
		 NULL,
         TRUE
    );

	RAISE NOTICE 'Item % successfully looted by character %.', p_instanacne_item, p_postava_id;

    
EXCEPTION
    WHEN OTHERS THEN
        ROLLBACK;
        RAISE;
END;
$$;



CALL sp_loot_item(15, 456);  -- CharacterID, ItemInstanceID


SELECT * FROM záznamboju;
SELECT * FROM boj;
SELECT * FROM Účastníkboju;
SELECT * FROM postava;
SELECT * FROM inštanciapredmetu;
SELECT * FROM predmetatribút;
SELECT * FROM postavaatribút where postavaid = 1;

UPDATE inštanciapredmetu
SET bojid = 11, postavaid = NULL;

UPDATE postava
SET aktuálnezdravie = 100





UPDATE postava
SET aktuálneAP = 100;