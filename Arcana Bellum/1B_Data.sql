INSERT INTO Atribút (AtribútID, Názov, Skratka) VALUES
(1, 'Sila', 'STR'),
(2, 'Obratnosť', 'DEX'),
(3, 'Výdrž', 'CON'),
(4, 'Inteligencia', 'INT');

INSERT INTO KategóriaKúzla (KategóriaKúzlaID, Názov, ModifikátorAP) VALUES
(1, 'Ohňová mágia', 0.90),
(2, 'Ľadová mágia', 0.85),
(3, 'Blesková mágia', 1.50),
(4, 'Prírodná mágia', 0.60),
(5, 'Boj z blízka', 0.50);

INSERT INTO Trieda (TriedaID, Názov, ModifikátorAP, ModifikátorInventára, BonusKBrneniu) VALUES
(1, 'Križiak', 0.8, 1.9, 8),
(2, 'Podvodník', 1.2, 1.2, 3),
(3, 'Čarodejník', 1.5, 0.7, -4),
(4, 'Klerik', 1.0, 1.5, 10),
(5, 'Žoldnier', 1.1, 3.0, 5);

-- Insert Class Attributes
INSERT INTO TriedaAtribút (TriedaID, AtribútID, HodnotaStart, LevelUpHodnota) VALUES
-- Križiak
(1, 1, 10, 3),  -- Sila
(1, 2, 5, 1),    -- Obratnosť
(1, 3, 12, 2),   -- Výdrž
(1, 4, 4, 1),    -- Inteligencia
-- Podvodník
(2, 1, 6, 2),
(2, 2, 14, 3),
(2, 3, 8, 1),
(2, 4, 5, 2),
-- Čarodejník
(3, 1, 4, 1),
(3, 2, 6, 2),
(3, 3, 8, 1),
(3, 4, 14, 3),
-- Klerik
(4, 1, 12, 2),
(4, 2, 8, 1),
(4, 3, 12, 2),
(4, 4, 4, 2),
-- Žoldnier
(5, 1, 8, 3),
(5, 2, 13, 3),
(5, 3, 10, 1),
(5, 4, 6, 1);

INSERT INTO Kúzlo (KúzloID, Názov, ZákladnáCena, ZákladnéPoškodenie, KategóriaKúzlaID) VALUES
-- Ohňová mágia
(1, 'Ohnivá guľa', 20, 10, 1),
(2, 'Plameňový vír', 35, 50, 1),
(3, 'Vulkánový výbuch', 45, 70, 1),
(4, 'Láva', 30, 55, 1),
(5, 'Hydra', 50, 90, 1),
-- Ľadová mágia
(6, 'Ľadová strela', 15, 20, 2),
(7, 'Zmrazenie', 20, 15, 2),
(8, 'Snehová búrka', 30, 40, 2),
(9, 'Kryštálový šíp', 40, 60, 2),
(10, 'Zamrznutie', 60, 80, 2),
-- Blesková mágia
(11, 'Blesk', 25, 40, 3),
(12, 'Hromová päsť', 30, 55, 3),
(13, 'Elektrická sieť', 20, 60, 3),
(14, 'Búrkový meč', 35, 80, 3),
(15, 'Skrat', 100, 100, 3),
-- Prírodná mágia
(16, 'Rastúce ostne', 20, 15, 4),
(17, 'Toxický oblak', 15, 10, 4),
(18, 'Zvuk lesa', 30, 20, 4),
(19, 'Skalné vlny', 35, 25, 4),
(20, 'Zemetrasenie', 40, 45, 4),
-- Boj z blízka
(21, 'Skrytý úder', 10, 25, 5),
(22, 'Zúrivá rana', 25, 60, 5),
(23, 'Štítový náraz', 15, 20, 5),
(24, 'Vírový útok', 30, 45, 5),
(25, 'Smrteľný úder', 40, 100, 5);

INSERT INTO AtribútKúzlo (KúzloID, AtribútID, Typ) VALUES
-- Ohnivá guľa 
(1, 4, 'ceny'),
(1, 4, 'účinku'),
-- Plameňový vír
(2, 4, 'ceny'),
(2, 4, 'účinku'),
-- Vulkánový výbuch
(3, 4, 'ceny'),
(3, 4, 'účinku'),
-- Láva
(4, 3, 'ceny'),
(4, 4, 'účinku'),
-- Hydra
(5, 4, 'ceny'),
(5, 4, 'účinku'),
-- Ľadová strela
(6, 4, 'ceny'),
(6, 4, 'účinku'),
-- Zmrazenie
(7, 4, 'ceny'),
(7, 4, 'účinku'),
-- Snehová búrka
(8, 4, 'ceny'),
(8, 4, 'účinku'),
-- Kryštálový šíp
(9, 4, 'ceny'),
(9, 4, 'účinku'),
-- Zamrznutie
(10, 3, 'ceny'),
(10, 3, 'účinku'),
-- Blesk
(11, 4, 'ceny'),
(11, 4, 'účinku'),
-- Hromová päsť
(12, 1, 'ceny'),
(12, 1, 'účinku'),
-- Elektrická sieť
(13, 4, 'ceny'),
(13, 4, 'účinku'),
-- Búrkový meč
(14, 1, 'ceny'),
(14, 1, 'účinku'),
-- Skrat
(15, 1, 'ceny'),
(15, 2, 'účinku'),
-- Rastúce ostne
(16, 3, 'ceny'),
(16, 3, 'účinku'),
-- Toxický oblak
(17, 4, 'ceny'),
(17, 4, 'účinku'),
-- Zvuk lesa
(18, 4, 'ceny'),
(18, 3, 'účinku'),
-- Skalné vlny
(19, 4, 'ceny'),
(19, 3, 'účinku'),
-- Zemetrasenie
(20, 2, 'ceny'),
(20, 2, 'účinku'),
-- Skrytý úder
(21, 1, 'ceny'),
(21, 1, 'účinku'),
-- Zúrivá rana
(22, 1, 'ceny'),
(22, 1, 'účinku'),
-- Štítový náraz
(23, 1, 'ceny'),
(23, 1, 'účinku'),
-- Vírový útok
(24, 2, 'ceny'),
(24, 2, 'účinku'),
-- Smrteľný úder
(25, 1, 'ceny'),
(25, 1, 'účinku');

INSERT INTO Predmet (PredmetID, Názov, Váha, Typ_predmetu, MinCenyAP, MaxCenyAP, MinPoškodenia, MaxPoškodenia, MinBrnenia, MaxBrnenia) VALUES
-- Zbrane
(1, 'Oceľový meč', 5, 'zbraň', 1.00, 1.00, 1.05, 1.24, 1.00, 1.00),
(2, 'Dlhý meč', 8, 'zbraň', 1.00, 1.00, 1.45, 1.95, 1.00, 1.00),
(3, 'Kopija', 6, 'zbraň', 1.00, 1.00, 1.20, 1.90, 1.00, 1.00),
(4, 'Bojová palica', 4, 'zbraň', 1.00, 1.00, 1.04, 1.20, 1.00, 1.00),
(5, 'Halapartňa', 9, 'zbraň', 1.00, 1.00, 1.30, 1.70, 1.00, 1.00),
(6, 'Krátky meč', 4, 'zbraň', 1.00, 1.00, 1.07, 1.65, 1.00, 1.00),
(7, 'Dýka', 2, 'zbraň', 1.00, 1.00, 1.05, 1.95, 1.00, 1.00),
(8, 'Bojová sekera', 6, 'zbraň', 1.00, 1.00, 1.15, 1.80, 1.00, 1.00),
(9, 'Ťažké kladivo', 14, 'zbraň', 1.00, 1.00, 2.00, 4.40, 1.00, 1.00),
(10, 'Rituálny nôž', 3, 'zbraň', 1.00, 1.00, 1.10, 2.40, 1.00, 1.00),
(11, 'Tŕňový prútik', 1, 'zbraň', 1.00, 1.00, 1.10, 1.90, 1.00, 1.00),
(12, 'Prútik noci', 10, 'zbraň', 1.00, 1.00, 1.30, 2.50, 1.00, 1.00),
(13, 'Zlaté kladivo', 22, 'zbraň', 1.00, 1.00, 3.00, 6.90, 1.00, 1.00),
(14, 'Zlatý meč', 13, 'zbraň', 1.00, 1.00, 1.95, 2.99, 1.00, 1.00),
(15, 'Zlatá kopija', 9, 'zbraň', 1.00, 1.00, 1.80, 3.00, 1.00, 1.00),
(16, 'Zlatá sekera', 12, 'zbraň', 1.00, 1.00, 2.00, 3.55, 1.00, 1.00),
(17, 'Ohnivá dýka', 5, 'zbraň', 1.00, 1.00, 1.75, 3.29, 1.00, 1.00),
(18, 'Meč paladina', 18, 'zbraň', 1.00, 1.00, 3.20, 5.90, 1.00, 1.00),
-- Brnenie
(19, 'Kožená zbroj', 8, 'brnenie', 1.00, 1.00, 1.00, 1.00, 0.80, 0.95),
(20, 'Plátová zbroj', 15, 'brnenie', 1.00, 1.00, 1.00, 1.00, 0.45, 0.80),
(21, 'Reťazová zbroj', 12, 'brnenie', 1.00, 1.00, 1.00, 1.00, 0.50, 0.75),
(22, 'Dračia koža', 10, 'brnenie', 1.00, 1.00, 1.00, 1.00, 0.58, 0.80),
(23, 'Tkaná zbroj', 6, 'brnenie', 1.00, 1.00, 1.00, 1.00, 0.60, 0.95),
(24, 'Elfská zbroj', 7, 'brnenie', 1.00, 1.00, 1.00, 1.00, 0.40, 0.85),
(25, 'Temná zbroj', 13, 'brnenie', 1.00, 1.00, 1.00, 1.00, 0.35, 0.65),
(26, 'Kráľovská zbroj', 14, 'brnenie', 1.00, 1.00, 1.00, 1.00, 0.33, 0.55),
(27, 'Vlčia kožušina', 9, 'brnenie', 1.00, 1.00, 1.00, 1.00, 0.48, 0.78),
(28, 'Zbroj paladina', 16, 'brnenie', 1.00, 1.00, 1.00, 1.00, 0.30, 0.60),
(29, 'Elfský plášť', 2, 'brnenie', 1.00, 1.00, 1.00, 1.00, 0.50, 0.77),
(30, 'Plášť noci', 1, 'brnenie', 1.00, 1.00, 1.00, 1.00, 0.35, 0.75),
(31, 'Zlaté brnenie', 20, 'brnenie', 1.00, 1.00, 1.00, 1.00, 0.20, 0.45),
(32, 'Ľadová zbroj', 15, 'brnenie', 1.00, 1.00, 1.00, 1.00, 0.42, 0.60),
(33, 'Zbroj paladina', 22, 'brnenie', 1.00, 1.00, 1.00, 1.00, 0.25, 0.47),
(34, 'Brnenia Aresa', 25, 'brnenie', 1.00, 1.00, 1.00, 1.00, 0.15, 0.40),
(35, 'Plášť času', 6, 'brnenie', 1.00, 1.00, 1.00, 1.00, 0.40, 0.50),
(36, 'Závoj boja', 12, 'brnenie', 1.00, 1.00, 1.00, 1.00, 0.42, 0.55),
-- Privesky
(37, 'Amulet lux', 1, 'privesok', 0.88, 0.98, 1.00, 1.00, 1.00, 1.00),
(38, 'Prsteň mága', 0, 'privesok', 0.70, 0.85, 1.00, 1.00, 1.00, 1.00),
(39, 'Talizman sily', 1, 'privesok', 0.85, 0.98, 1.00, 1.00, 1.00, 1.00),
(40, 'Kameň šťastia', 1, 'privesok', 0.59, 0.89, 1.00, 1.00, 1.00, 1.00),
(41, 'Zväzok kúziel', 4, 'privesok', 0.49, 0.79, 1.00, 1.00, 1.00, 1.00),
(42, 'Šperk hrdinu', 2, 'privesok', 0.90, 0.95, 1.00, 1.00, 1.00, 1.00),
(43, 'Amulet tieňov', 3, 'privesok', 0.62, 0.80, 1.00, 1.00, 1.00, 1.00),
(44, 'Runový prsteň', 0, 'privesok', 0.80, 0.95, 1.00, 1.00, 1.00, 1.00),
(45, 'Relikvia zla', 1, 'privesok', 0.85, 0.95, 1.00, 1.00, 1.00, 1.00),
(46, 'Hadí jazyk', 3, 'privesok', 0.60, 0.85, 1.00, 1.00, 1.00, 1.00),
(47, 'Dračie oko', 5, 'prívesok', 0.20, 0.65, 1.00, 1.00, 1.00, 1.00),
(48, 'Minca šťastia', 0, 'prívesok', 0.44, 0.70, 1.00, 1.00, 1.00, 1.00),
(49, 'Dych noci', 1, 'privesok', 0.30, 0.50, 1.00, 1.00, 1.00, 1.00),
(50, 'Zväzok kliatob', 4, 'privesok', 0.35, 0.55, 1.00, 1.00, 1.00, 1.00),
(51, 'Krvavý amulet', 5, 'privesok', 0.45, 0.65, 1.00, 1.00, 1.00, 1.00),
(52, 'Vlčí zub', 3, 'prívesok', 0.42, 0.68, 1.00, 1.00, 1.00, 1.00),
(53, 'Prívesok Slnka', 4, 'prívesok', 0.25, 0.64, 1.00, 1.00, 1.00, 1.00),
(54, 'Tieňový prsťen', 2, 'prívesok', 0.20, 0.30, 1.00, 1.00, 1.00, 1.00),
(55, 'Prsťen boja', 12, 'prívesok', 0.38, 0.70, 1.00, 1.00, 1.00, 1.00);

INSERT INTO PredmetTrieda (PredmetID, TriedaID) VALUES
-- Zbrane
(1, 1), (1, 2), (1, 5),   -- Oceľový meč
(2, 1), (2, 2), (2, 5),    -- Dlhý meč
(3, 1), (3, 2), (3, 5),    -- Kopija
(4, 1), (4, 2), (4, 3), (4, 4), (4, 5),  -- Bojová palica
(5, 1), (5, 2), (5, 5),    -- Halapartňa
(6, 1), (6, 2), (6, 3), (6, 4), (6, 5),  -- Krátky meč
(7, 1), (7, 2), (7, 3), (7, 4), (7, 5),  -- Dýka
(8, 1), (8, 2), (8, 4), (8, 5),  -- Bojová sekera
(9, 1), (9, 4), (9, 5),    -- Ťažké kladivo
(10, 2),                   -- Rituálny nôž
(11, 3),                   -- Tŕňový prútik
(12, 3),                   -- Prútik noci
(13, 4),                   -- Zlaté kladivo
(14, 1),                   -- Zlatý meč
(15, 5),                   -- Zlatá kopija
(16, 1), (16, 5),          -- Zlatá sekera
(17, 2),                   -- Ohnivá dýka
(18, 1), (18, 2), (18, 4), (18, 5),  -- Meč paladina
-- Brnenie
(19, 1), (19, 2), (19, 3), (19, 4), (19, 5),  -- Kožená zbroj
(20, 1), (20, 5),         -- Plátová zbroj
(21, 1),                  -- Reťazová zbroj
(22, 1), (22, 2), (22, 3), (22, 4), (22, 5),  -- Dračia koža
(23, 1), (23, 2), (23, 3), (23, 4), (23, 5),  -- Tkaná zbroj
(24, 1), (24, 2), (24, 4), (24, 5),  -- Elfská zbroj
(25, 1), (25, 4), (25, 5),  -- Temná zbroj
(26, 1),                   -- Kráľovská zbroj
(27, 1), (27, 4), (27, 5),  -- Vlčia kožušina
(28, 1), (28, 5),         -- Zbroj paladina
(29, 3),                  -- Elfský plášť
(30, 2),                  -- Plášť noci
(31, 5),                  -- Zlaté brnenie
(32, 3), (32, 4),         -- Ľadová zbroj
(33, 4),                  -- Zbroj paladina
(34, 1),                  -- Brnenia Aresa
(35, 3),                  -- Plášť času
(36, 1), (36, 2), (36, 3), (36, 4), (36, 5),  -- Závoj boja
-- Privesky
(37, 1), (37, 2), (37, 3), (37, 4), (37, 5),  -- Amulet lux
(38, 3),                  -- Prsteň mága
(39, 2), (39, 3), (39, 4), (39, 5),  -- Talizman sily
(40, 2), (40, 3), (40, 4),  -- Kameň šťastia
(41, 3), (41, 4),         -- Zväzok kúziel
(42, 3), (42, 4),         -- Šperk hrdinu
(43, 3), (43, 4),         -- Amulet tieňov
(44, 2), (44, 3), (44, 4), (44, 5),  -- Runový prsteň
(45, 1), (45, 2), (45, 3), (45, 4), (45, 5),  -- Relikvia zla
(46, 3),                  -- Hadí jazyk
(47, 3),                  -- Dračie oko
(48, 2),                  -- Minca šťastia
(49, 3), (49, 4),         -- Dych noci
(50, 3), (50, 5),         -- Zväzok kliatob
(51, 1), (51, 3), (51, 5),  -- Krvavý amulet
(52, 1), (52, 4), (52, 5),  -- Vlčí zub
(53, 4),                  -- Prívesok Slnka
(54, 3),                  -- Tieňový prsťen
(55, 1);                  -- Prsťen boja
