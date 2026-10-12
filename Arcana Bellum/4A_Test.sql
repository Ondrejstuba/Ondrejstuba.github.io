--vytvorenie postav
CALL sp_create_character('TestovaciaPostava3', 1);
CALL sp_create_character('TestovaciaPostava4', 2);
SELECT * FROM v_character_inventory WHERE CharacterName = 'TestovaciaPostava3';
SELECT * FROM postavaatribút WHERE PostavaID = (SELECT postavaid FROM postava WHERE meno = 'TestovaciaPostava3');
SELECT * FROM postava where meno = 'TestovaciaPostava3';
SELECT * FROM inštanciapredmetu WHERE PostavaID = (SELECT postavaid FROM postava WHERE meno = 'TestovaciaPostava3');

SELECT * FROM v_character_inventory WHERE CharacterName = 'TestovaciaPostava4';
SELECT * FROM postavaatribút WHERE PostavaID = (SELECT postavaid FROM postava WHERE meno = 'TestovaciaPostava4');
SELECT * FROM postava where meno = 'TestovaciaPostava4';
SELECT * FROM inštanciapredmetu WHERE PostavaID = (SELECT postavaid FROM postava WHERE meno = 'TestovaciaPostava4');

SELECT * FROM postava order by postavaid;

--budeme pouzivat ich id
SELECT postavaid FROM postava where meno = 'TestovaciaPostava3';
SELECT postavaid FROM postava where meno = 'TestovaciaPostava4';

--ake kuzla pozna dana postava? 
SELECT * FROM v_spellbook WHERE PostavaID = 17;
SELECT * FROM v_spellbook WHERE PostavaID = 18;

--pokus o kuzlo mimo boja
DO $$ 
DECLARE 
    p_postava_id_1 INT := (SELECT postavaid FROM postava WHERE meno = 'TestovaciaPostava3');
    p_postava_id_2 INT := (SELECT postavaid FROM postava WHERE meno = 'TestovaciaPostava4');
    p_kuzlo_id INT := 23; 
BEGIN
    CALL sp_cast_spell(p_postava_id_1, p_postava_id_2, p_kuzlo_id);
END $$;

--vstup do boja
SELECT * FROM v_combat_state;
DO $$ 
DECLARE 
    p_postava_id_1 INT := (SELECT postavaid FROM postava WHERE meno = 'TestovaciaPostava3');
    p_postava_id_2 INT := (SELECT postavaid FROM postava WHERE meno = 'TestovaciaPostava4');
    p_boj_id INT := 16;
BEGIN
    CALL sp_enter_combat(p_postava_id_1, p_boj_id);
    CALL sp_enter_combat(p_postava_id_2, p_boj_id);
END $$;
SELECT * FROM v_combat_state;

--kuzlo
DO $$ 
DECLARE 
    p_postava_id_1 INT := (SELECT postavaid FROM postava WHERE meno = 'TestovaciaPostava3');
    p_postava_id_2 INT := (SELECT postavaid FROM postava WHERE meno = 'TestovaciaPostava4');
    p_kuzlo_id INT := 23; 
BEGIN
    CALL sp_cast_spell(p_postava_id_1, p_postava_id_2, p_kuzlo_id);
END $$;


--predmet co moze zobrat
CALL insert_random_item_into_battle(16, 1); 
CALL sp_loot_item(17, 440);
SELECT * FROM v_character_inventory WHERE CharacterName = 'TestovaciaPostava4';

--predmet co nemoze zobrat
CALL insert_random_item_into_battle(16, 38); 
CALL sp_loot_item(17, 444);
SELECT * FROM v_character_inventory WHERE CharacterName = 'TestovaciaPostava4';

--pridanie nahodneho predmetu
CALL insert_random_item_into_battle(16); 

--nove kolo len ked uz postavy nemaju dost ap
CALL sp_reset_round(16); 

--nove kolo vzdy 
CALL sp_reset_round(16, TRUE); 

--vidime ze maju max ap znova
SELECT * FROM postava where meno = 'TestovaciaPostava3';
SELECT * FROM postava where meno = 'TestovaciaPostava4';

--plus ze je nove kolo vidime tu
SELECT * FROM boj;

--pokus o regeneraciu v boji
CALL sp_rest(18); 

--odchod z boja teda
CALL sp_leave_combat(18);
CALL sp_rest(18, 0.1); 
CALL sp_rest(18, 0.9); 
CALL sp_rest(18); 

--pokus o zobratie predmetu v boji, ked postava v boji nie je
CALL sp_loot_item(18, 445);

SELECT * FROM záznamboju;




