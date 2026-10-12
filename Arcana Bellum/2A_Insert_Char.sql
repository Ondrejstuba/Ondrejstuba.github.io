CREATE OR REPLACE FUNCTION assign_random_spells(
    p_postavaid  INT
)
RETURNS VOID AS $$
BEGIN
  
    INSERT INTO PostavaKúzlo (PostavaID, KúzloID)
    SELECT
        p_postavaid,
        k.KúzloID
    FROM Kúzlo k
    ORDER BY RANDOM()
    LIMIT 5;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION insert_instance_with_optional_bonus(
    p_predmetid  INT,
    p_postavaid  INT
)
RETURNS VOID AS $$
DECLARE
    v_instance_id INT;
    v_bonus_chance FLOAT;
    v_chosen_attrib INT;
    v_bonus_value INT;
    v_item_weight NUMERIC;
BEGIN
    INSERT INTO InštanciaPredmetu (PredmetID, PostavaID, ModifikátorCenyAP, ModifikátorPoškodenia, ModifikátorBrnenia)
    SELECT 
        p.PredmetID,
        p_postavaid,
        p.MinCenyAP + (p.MaxCenyAP - p.MinCenyAP) * random(),
        p.MinPoškodenia + (p.MaxPoškodenia - p.MinPoškodenia) * random(),
        p.MinBrnenia + (p.MaxBrnenia - p.MinBrnenia) * random()
    FROM Predmet p
    WHERE p.PredmetID = p_predmetid
    RETURNING InštanciaPredmetuID INTO v_instance_id;

    SELECT Váha INTO v_item_weight
    FROM Predmet
    WHERE PredmetID = p_predmetid;

    UPDATE Postava
    SET AktuálnaVáhaInventára = COALESCE(AktuálnaVáhaInventára, 0) + v_item_weight
    WHERE PostavaID = p_postavaid;

    v_bonus_chance := random();
    IF v_bonus_chance < 0.20 THEN
        SELECT AtribútID
        INTO v_chosen_attrib
        FROM Atribút
        ORDER BY random()
        LIMIT 1;

        v_bonus_value := floor(random() * 10) + 1;

        INSERT INTO PredmetAtribút (InštanciaPredmetuID, AtribútID, Hodnota)
        VALUES (v_instance_id, v_chosen_attrib, v_bonus_value);

        INSERT INTO PostavaAtribút (PostavaID, AtribútID, Hodnota)
        VALUES (p_postavaid, v_chosen_attrib, v_bonus_value)
        ON CONFLICT (PostavaID, AtribútID)
        DO UPDATE
          SET Hodnota = PostavaAtribút.Hodnota + EXCLUDED.Hodnota;
    END IF;
END;
$$ LANGUAGE plpgsql;





CREATE OR REPLACE PROCEDURE sp_assign_class_attributes(p_postavaid INT)
LANGUAGE plpgsql AS $$
DECLARE
  v_classid INT;
BEGIN
  SELECT TriedaID INTO v_classid
    FROM Postava
   WHERE PostavaID = p_postavaid;

  INSERT INTO PostavaAtribút (PostavaID, AtribútID, Hodnota)
  SELECT
    p_postavaid,
    ta.AtribútID,
    ta.HodnotaStart
  FROM TriedaAtribút ta
  WHERE ta.TriedaID = v_classid;
END;
$$;

CREATE OR REPLACE PROCEDURE sp_assign_starting_items(p_postavaid INT)
LANGUAGE plpgsql AS $$
DECLARE
  v_classid INT;
BEGIN
  SELECT TriedaID INTO v_classid
    FROM Postava
   WHERE PostavaID = p_postavaid;

  IF v_classid = 1 THEN  -- Križiak
    PERFORM insert_instance_with_optional_bonus(1,  p_postavaid); -- Oceľový meč
    PERFORM insert_instance_with_optional_bonus(19, p_postavaid); -- Kožená zbroj

  ELSIF v_classid = 2 THEN  -- Podvodník
    PERFORM insert_instance_with_optional_bonus(10, p_postavaid); -- Rituálny nôž

  ELSIF v_classid = 3 THEN  -- Čarodejník
    PERFORM insert_instance_with_optional_bonus(4,  p_postavaid); -- Bojová palica
    PERFORM insert_instance_with_optional_bonus(39, p_postavaid); -- Talizman sily

  ELSIF v_classid = 4 THEN  -- Klerik
    PERFORM insert_instance_with_optional_bonus(8,  p_postavaid); -- Bojová sekera
    PERFORM insert_instance_with_optional_bonus(39, p_postavaid); -- Talizman sily

  ELSIF v_classid = 5 THEN  -- Žoldnier
    PERFORM insert_instance_with_optional_bonus(5,  p_postavaid); -- Halapartňa
    PERFORM insert_instance_with_optional_bonus(23, p_postavaid); -- Tkaná zbroj
  END IF;
END;
$$;


CREATE OR REPLACE PROCEDURE sp_create_character(
    p_name    TEXT,
    p_classid INT
)
LANGUAGE plpgsql
AS $$
DECLARE
    v_new_id   INT;
    v_bonus    NUMERIC;
    v_modap    NUMERIC;
    v_str      NUMERIC;
    v_dex      NUMERIC;
    v_con      NUMERIC;
    v_intel    NUMERIC;
    v_hp       NUMERIC;
    v_ap       NUMERIC;
BEGIN
    INSERT INTO Postava (
      Meno,
      TriedaID,
      AktuálneZdravie,
      AktuálneAP,
      AktuálnaVáhaInventára,
      Level,
      AktuálneEXP,
      NextLevelEXP
    ) VALUES (
      p_name,
      p_classid,
      0,    
      0,   
      0,   
      1,
      0,
      ROUND(100 * POWER(2, 1.5))
    )
    RETURNING PostavaID INTO v_new_id;

    CALL sp_assign_class_attributes(v_new_id);
    CALL sp_assign_starting_items     (v_new_id);
    PERFORM assign_random_spells       (v_new_id);

    SELECT BonusKBrneniu, ModifikátorAP
      INTO v_bonus, v_modap
    FROM Trieda
    WHERE TriedaID = p_classid;

    SELECT
      MAX(CASE WHEN AtribútID = 1 THEN Hodnota END),
      MAX(CASE WHEN AtribútID = 2 THEN Hodnota END),
      MAX(CASE WHEN AtribútID = 3 THEN Hodnota END),
      MAX(CASE WHEN AtribútID = 4 THEN Hodnota END)
    INTO v_str, v_dex, v_con, v_intel
    FROM PostavaAtribút
    WHERE PostavaID = v_new_id;

    IF p_classid = 1 THEN          -- Križiak
        v_hp := v_bonus + (v_con * 3    + v_str  * 1.2);
    ELSIF p_classid = 2 THEN      -- Podvodník
        v_hp := v_bonus + (v_con * 2    + v_dex  * 1.5);
    ELSIF p_classid = 3 THEN      -- Čarodejník
        v_hp := v_bonus + (v_con * 1.5  + v_intel * 1.3);
    ELSIF p_classid = 4 THEN      -- Klerik
        v_hp := v_bonus + (v_con * 2.2  + v_intel * 0.4 + v_str * 0.3);
    ELSE                          -- Žoldnier (5)
        v_hp := v_bonus + (v_con * 3    + v_dex  * 1.0);
    END IF;

    v_ap := (v_dex + v_intel) * v_modap;

    UPDATE Postava
    SET
      AktuálneZdravie    = ROUND(v_hp)::INT,
      AktuálneAP         = ROUND(v_ap)::INT
    WHERE PostavaID = v_new_id;

    RAISE NOTICE 'Created % (ID=%): HP=%, AP=%',
                 p_name, v_new_id, ROUND(v_hp), ROUND(v_ap);
END;
$$;

CALL sp_create_character('Zoro5', 5);  -- meno, trieda



select * from inštanciapredmetu;
select * from postava;
select * from postavaatribút where postavaid = 14;
select * from postavakúzlo where postavaid = 9;
select * from predmetatribút;
select * from inštanciapredmetu;


