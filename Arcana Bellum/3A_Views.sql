CREATE OR REPLACE VIEW v_combat_state AS
SELECT 
    b.BojID,
    b.AktuálneKolo,
    b.Stav,
    ub.PostavaID,
    p.AktuálneAP,
    p.AktuálneZdravie
FROM Boj b
JOIN "Účastníkboju" ub 
    ON b.BojID = ub.BojID
JOIN Postava p 
    ON ub.PostavaID = p.PostavaID
WHERE ub.JeAktívny = TRUE;

-- ============================================================================================================

CREATE OR REPLACE VIEW v_most_damage AS
SELECT 
    z.PostavaID,
    SUM(z.Výsledok) AS TotalDamage,
    ROW_NUMBER() OVER(ORDER BY SUM(z.Výsledok) DESC) AS DamageRank
FROM Záznamboju z
WHERE z.TypAkcie IN ('Kúzlo', 'Útok')
GROUP BY z.PostavaID
ORDER BY TotalDamage DESC;

-- ============================================================================================================

CREATE OR REPLACE VIEW v_combat_damage AS
SELECT 
    BojID,
    SUM(Výsledok) AS TotalCombatDamage
FROM Záznamboju
WHERE TypAkcie IN ('Kúzlo', 'Útok')
GROUP BY BojID;

-- ============================================================================================================

CREATE OR REPLACE VIEW v_strongest_characters AS
SELECT 
    p.PostavaID,
    p.AktuálneZdravie,
    p.Level,
    COALESCE(d.TotalDamage, 0) AS TotalDamage,
    ((p.AktuálneZdravie + COALESCE(d.TotalDamage, 0)) * p.Level) AS PerformanceScore
FROM Postava p
LEFT JOIN (
    SELECT 
        z.PostavaID, 
        SUM(z.Výsledok) AS TotalDamage
    FROM Záznamboju z
    WHERE z.TypAkcie IN ('Kúzlo', 'Útok')
    GROUP BY z.PostavaID
) d 
    ON p.PostavaID = d.PostavaID
ORDER BY PerformanceScore DESC;

-- ============================================================================================================

CREATE OR REPLACE VIEW v_spell_statistics AS
SELECT 
    k.KúzloID,
    k.Názov AS SpellName,
    kk.Názov AS SpellCategory,
    COUNT(*) AS UsageCount,
    SUM(z.Výsledok) AS TotalSpellDamage,
    AVG(z.Výsledok) AS AverageSpellDamage
FROM Záznamboju z
JOIN Kúzlo k 
    ON k.KúzloID = z.KúzloID
JOIN KategóriaKúzla kk 
    ON k.KategóriaKúzlaID = kk.KategóriaKúzlaID
WHERE z.TypAkcie = 'Kúzlo'
GROUP BY k.KúzloID, k.Názov, kk.Názov;

-- ============================================================================================================

CREATE OR REPLACE VIEW v_combat_log AS
SELECT 
    z.ZáznamBojuID,
    z.BojID,
    b.AktuálneKolo,
    z.PostavaID,
    p.Meno AS CharacterName,
    z.TypAkcie,
    z.KúzloID,
    k.Názov AS SpellName,
    z.InštanciaPredmetuID,
    z.CieľováPostavaID,
    cp.Meno AS TargetName,
    z.VýsledokHoduKockou,
    z.Výsledok,
    z.Úspech
FROM Záznamboju z
LEFT JOIN Boj b 
    ON z.BojID = b.BojID
LEFT JOIN Postava p 
    ON z.PostavaID = p.PostavaID
LEFT JOIN Kúzlo k 
    ON z.KúzloID = k.KúzloID
LEFT JOIN Postava cp
    ON z.CieľováPostavaID = cp.PostavaID
ORDER BY z.BojID, b.AktuálneKolo, z.ZáznamBojuID;

-- ============================================================================================================

CREATE OR REPLACE VIEW v_inventory_status AS
SELECT 
    p.PostavaID,
    p.Meno,
    p.AktuálnaVáhaInventára,
    ((pa_sila.Hodnota + pa_vydrz.Hodnota) * t.ModifikátorInventára) AS MaxInventory,
    ROUND((p.AktuálnaVáhaInventára::NUMERIC / ((pa_sila.Hodnota + pa_vydrz.Hodnota) * t.ModifikátorInventára)) * 100, 2) AS InventoryUsagePercentage
FROM Postava p
JOIN Trieda t ON p.TriedaID = t.TriedaID
JOIN PostavaAtribút pa_sila 
    ON p.PostavaID = pa_sila.PostavaID AND pa_sila.AtribútID = 1 
JOIN PostavaAtribút pa_vydrz 
    ON p.PostavaID = pa_vydrz.PostavaID AND pa_vydrz.AtribútID = 3  
ORDER BY InventoryUsagePercentage DESC;

-- ============================================================================================================


CREATE OR REPLACE VIEW v_character_inventory AS
SELECT 
    p.PostavaID,
    p.Meno AS CharacterName,
    ip.InštanciaPredmetuID,
    pr.Názov AS ItemName,
    pr.Váha AS ItemWeight,
    ip.ModifikátorCenyAP AS PriceModifier,
    ip.ModifikátorPoškodenia AS DamageModifier,
    ip.ModifikátorBrnenia AS ArmorModifier,
    COALESCE(STRING_AGG(pa.Atribútid || ' +' || pa.Hodnota, ', '), 'Žiadny') AS AttributeBonus
FROM Postava p
JOIN InštanciaPredmetu ip 
    ON p.PostavaID = ip.PostavaID
JOIN Predmet pr 
    ON ip.PredmetID = pr.PredmetID
LEFT JOIN PredmetAtribút pa 
    ON ip.InštanciaPredmetuID = pa.InštanciaPredmetuID
GROUP BY 
    p.PostavaID, p.Meno, ip.InštanciaPredmetuID,
    pr.Názov, pr.Váha, ip.ModifikátorCenyAP, 
    ip.ModifikátorPoškodenia, ip.ModifikátorBrnenia
ORDER BY p.PostavaID, pr.Názov;

-- ============================================================================================================

CREATE OR REPLACE VIEW v_experience_progress AS
SELECT 
    p.PostavaID,
    p.Meno AS CharacterName,
    p.Level,
    p.AktuálneEXP,
    p.NextLevelEXP,
    COALESCE(SUM(z.Výsledok), 0) AS TotalXPGained,
    COUNT(z.ZáznamBojuID) AS NumberOfXPEvents,
    ROUND((p.AktuálneEXP::NUMERIC / p.NextLevelEXP) * 100, 2) AS XPProgressPercentage
FROM Postava p
LEFT JOIN Záznamboju z 
    ON p.PostavaID = z.PostavaID 
   AND z.TypAkcie = '+EXP'
GROUP BY p.PostavaID, p.Meno, p.Level, p.AktuálneEXP, p.NextLevelEXP
ORDER BY level DESC, aktuálneexp DESC;

-- ============================================================================================================

CREATE OR REPLACE VIEW v_loot_events AS
SELECT
    z.ZáznamBojuID,
    z.BojID,
    z.ČísloKola,
    z.PostavaID,
    p.Meno AS CharacterName,
    z.InštanciaPredmetuID,
    ip.PredmetID,
    pr.Názov AS ItemName,
    pr.Váha AS ItemWeight
FROM ZáznamBoju z
JOIN Postava p 
    ON z.PostavaID = p.PostavaID
JOIN InštanciaPredmetu ip 
    ON z.InštanciaPredmetuID = ip.InštanciaPredmetuID
JOIN Predmet pr 
    ON ip.PredmetID = pr.PredmetID
WHERE z.TypAkcie = 'Zobratie predmetu'
ORDER BY záznambojuid;
ORDER BY z.BojID, z.ČísloKola;

-- ============================================================================================================

-- ===========================================================================
-- 💎 View: v_item_instance_attributes
-- 📋 Description: Lists each item instance with its base data and 
--     any associated attribute bonuses from PredmetAtribút.
-- ===========================================================================

CREATE OR REPLACE VIEW v_item_instance_attributes AS
SELECT 
    ip.InštanciaPredmetuID,
    pr.PredmetID,  
    pr.Názov AS ItemName,
    pr.Váha AS ItemWeight,
    ip.ModifikátorCenyAP AS PriceModifier,
    ip.ModifikátorPoškodenia AS DamageModifier,
    ip.ModifikátorBrnenia AS ArmorModifier,
    COALESCE(STRING_AGG(pa.Atribútid || ' +' || pa.Hodnota, ', '), 'Žiadny') AS AttributeBonuses
FROM InštanciaPredmetu ip
JOIN Predmet pr ON ip.PredmetID = pr.PredmetID
LEFT JOIN PredmetAtribút pa ON ip.InštanciaPredmetuID = pa.InštanciaPredmetuID
GROUP BY 
    ip.InštanciaPredmetuID,
    pr.PredmetID,  -- Include PredmetID in the GROUP BY
    pr.Názov,
    pr.Váha,
    ip.ModifikátorCenyAP,
    ip.ModifikátorPoškodenia,
    ip.ModifikátorBrnenia
ORDER BY ip.InštanciaPredmetuID;

-- ============================================================================================================

DROP VIEW IF EXISTS v_spellbook;
CREATE OR REPLACE VIEW v_spellbook AS
SELECT 
    pk.PostavaID,
    p.Meno AS CharacterName,
    k.KúzloID,
    k.Názov AS SpellName,
    kk.Názov AS SpellCategory,
    
    -- EXACT spell cost calculation (attributes only)
    (k.ZákladnáCena * kk.ModifikátorAP * 
     (1 - COALESCE((
        SELECT SUM(pa.Hodnota) / 100
        FROM AtribútKúzlo ak
        JOIN PostavaAtribút pa ON ak.AtribútID = pa.AtribútID
        WHERE ak.KúzloID = k.KúzloID 
          AND ak.Typ = 'ceny'
          AND pa.PostavaID = pk.PostavaID
     ), 0))) AS EffectiveCost,
    
    k.ZákladnéPoškodenie AS BaseDamage,
    

    
    -- Attributes affecting the spell
    COALESCE((
        SELECT STRING_AGG(a.Skratka || ' (cost)', ', ')
        FROM AtribútKúzlo ak
        JOIN Atribút a ON ak.AtribútID = a.AtribútID
        WHERE ak.KúzloID = k.KúzloID AND ak.Typ = 'ceny'
    ), 'None') AS CostAttributes,
    
    COALESCE((
        SELECT STRING_AGG(a.Skratka || ' (effect)', ', ')
        FROM AtribútKúzlo ak
        JOIN Atribút a ON ak.AtribútID = a.AtribútID
        WHERE ak.KúzloID = k.KúzloID AND ak.Typ = 'účinku'
    ), 'None') AS EffectAttributes,
    
    -- Usage statistics
    COALESCE((
        SELECT COUNT(*) 
        FROM ZáznamBoju z 
        WHERE z.PostavaID = pk.PostavaID 
          AND z.KúzloID = k.KúzloID
    ), 0) AS TimesUsed,
    
    COALESCE((
        SELECT AVG(z.Výsledok)
        FROM ZáznamBoju z 
        WHERE z.PostavaID = pk.PostavaID 
          AND z.KúzloID = k.KúzloID
          AND z.Úspech = TRUE
    ), 0) AS AverageDamageDealt
    
FROM PostavaKúzlo pk
JOIN Postava p ON pk.PostavaID = p.PostavaID
JOIN Kúzlo k ON pk.KúzloID = k.KúzloID
JOIN KategóriaKúzla kk ON k.KategóriaKúzlaID = kk.KategóriaKúzlaID;

-- ============================================================================================================
-- ============================================================================================================
-- ============================================================================================================

SELECT * FROM v_combat_state;
SELECT * FROM v_most_damage;
SELECT * FROM v_combat_damage;
SELECT * FROM v_strongest_characters;
SELECT * FROM v_spell_statistics;
SELECT * FROM v_combat_log;
SELECT * FROM v_inventory_status;
SELECT * FROM v_character_inventory;
SELECT * FROM v_experience_progress;
SELECT * FROM v_loot_events;
SELECT * FROM v_item_instance_attributes;
SELECT * FROM v_spellbook WHERE PostavaID = 1;

SELECT * FROM  postavaatribút where PostavaID = 10;
SELECT * FROM  inštanciapredmetu where PostavaID = 10;


