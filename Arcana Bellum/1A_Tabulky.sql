-- 1. Create Tables
CREATE TABLE Atribút (
    AtribútID SERIAL PRIMARY KEY,
    Názov VARCHAR(50) NOT NULL,
    Skratka VARCHAR(10)
);

CREATE TABLE Trieda (
    TriedaID SERIAL PRIMARY KEY,
    Názov VARCHAR(255) NOT NULL,
    ModifikátorAP DECIMAL(5,2) NOT NULL,
    ModifikátorInventára DECIMAL(5,2) NOT NULL,
    BonusKBrneniu INT NOT NULL
);

CREATE TABLE KategóriaKúzla (
    KategóriaKúzlaID SERIAL PRIMARY KEY,
    Názov VARCHAR(255) NOT NULL,
    ModifikátorAP DECIMAL(5,2) NOT NULL
);

CREATE TABLE Predmet (
    PredmetID SERIAL PRIMARY KEY,
    Názov VARCHAR(255) NOT NULL,
    Váha INT NOT NULL CHECK (Váha >= 0),
    Typ_predmetu VARCHAR(50) NOT NULL,
    MinCenyAP DECIMAL(5,2),
    MaxCenyAP DECIMAL(5,2),
    MinPoškodenia DECIMAL(5,2),
    MaxPoškodenia DECIMAL(5,2),
    MinBrnenia DECIMAL(5,2),
    MaxBrnenia DECIMAL(5,2)
);

CREATE TABLE Boj (
    BojID SERIAL PRIMARY KEY,
    AktuálneKolo INT NOT NULL CHECK (AktuálneKolo >= 1),
    Stav VARCHAR(50) NOT NULL CHECK (Stav IN ('Aktívny', 'Neaktívny', 'Skončený')) DEFAULT 'Aktívny'
);

CREATE TABLE Kúzlo (
    KúzloID SERIAL PRIMARY KEY,
    Názov VARCHAR(255) NOT NULL,
    ZákladnáCena INT NOT NULL,
    ZákladnéPoškodenie INT NOT NULL,
    KategóriaKúzlaID INT NOT NULL REFERENCES KategóriaKúzla(KategóriaKúzlaID)
);

CREATE TABLE Postava (
    PostavaID SERIAL PRIMARY KEY,
    Meno VARCHAR(255) NOT NULL,
    TriedaID INT NOT NULL REFERENCES Trieda(TriedaID),
    AktuálneZdravie INT NOT NULL CHECK (AktuálneZdravie >= 0),
    AktuálneAP INT NOT NULL CHECK (AktuálneAP >= 0),
    AktuálnaVáhaInventára INT NOT NULL CHECK (AktuálnaVáhaInventára >= 0) DEFAULT 0,
    Level INT NOT NULL CHECK (Level >= 1),
    AktuálneEXP INT NOT NULL CHECK (AktuálneEXP >= 0) DEFAULT 0,
    NextLevelEXP INT NOT NULL CHECK (NextLevelEXP >= 0) DEFAULT 100 
);

CREATE TABLE InštanciaPredmetu (
    InštanciaPredmetuID SERIAL PRIMARY KEY,
    PredmetID INT NOT NULL REFERENCES Predmet(PredmetID),
    BojID INT REFERENCES Boj(BojID),
    PostavaID INT REFERENCES Postava(PostavaID),
    ModifikátorCenyAP DECIMAL(5,2) NOT NULL,
    ModifikátorPoškodenia DECIMAL(5,2) NOT NULL,
    ModifikátorBrnenia DECIMAL(5,2) NOT NULL,
    CHECK (NUM_NULLS(BojID, PostavaID) = 1)
);

CREATE TABLE PredmetTrieda (
    PredmetID INT NOT NULL REFERENCES Predmet(PredmetID),
    TriedaID INT NOT NULL REFERENCES Trieda(TriedaID),
    PRIMARY KEY (PredmetID, TriedaID)
);

CREATE TABLE AtribútKúzlo (
    KúzloAtribútID SERIAL PRIMARY KEY,
    KúzloID INT NOT NULL REFERENCES Kúzlo(KúzloID),
    AtribútID INT NOT NULL REFERENCES Atribút(AtribútID),
    Typ VARCHAR(10) NOT NULL CHECK (Typ IN ('ceny', 'účinku')),
    UNIQUE (KúzloID, AtribútID, Typ)
);

CREATE TABLE PostavaKúzlo (
    PostavaID INT NOT NULL REFERENCES Postava(PostavaID),
    KúzloID INT NOT NULL REFERENCES Kúzlo(KúzloID),
    PRIMARY KEY (PostavaID, KúzloID)
);

CREATE TABLE ÚčastníkBoju (
    BojID INT NOT NULL REFERENCES Boj(BojID),
    PostavaID INT NOT NULL REFERENCES Postava(PostavaID),
    JeAktívny BOOLEAN NOT NULL DEFAULT TRUE,
    PRIMARY KEY (BojID, PostavaID)
);

CREATE TABLE TriedaAtribút (
    TriedaID INT NOT NULL REFERENCES Trieda(TriedaID),
    AtribútID INT NOT NULL REFERENCES Atribút(AtribútID),
    HodnotaStart INT NOT NULL,
    LevelUpHodnota INT NOT NULL,
    PRIMARY KEY (TriedaID, AtribútID)
);

CREATE TABLE PostavaAtribút (
    AtribútPostavaID SERIAL PRIMARY KEY,
    PostavaID INT NOT NULL REFERENCES Postava(PostavaID),
    AtribútID INT NOT NULL REFERENCES Atribút(AtribútID),
    Hodnota INT NOT NULL,
    UNIQUE (PostavaID, AtribútID)
);

CREATE TABLE PredmetAtribút (
    BonusID SERIAL PRIMARY KEY,
    InštanciaPredmetuID INT NOT NULL REFERENCES InštanciaPredmetu(InštanciaPredmetuID),
    AtribútID INT NOT NULL REFERENCES Atribút(AtribútID),
    Hodnota INT NOT NULL CHECK (Hodnota BETWEEN 1 AND 25)
);

CREATE TABLE ZáznamBoju (
    ZáznamBojuID SERIAL PRIMARY KEY,
    BojID INT REFERENCES Boj(BojID),
    ČísloKola INT,
    PostavaID INT NOT NULL REFERENCES Postava(PostavaID),
    TypAkcie VARCHAR(50) NOT NULL CHECK (TypAkcie IN (
        'Kúzlo', 'Zobratie predmetu', 'Odpočinok', 
        'Vstup do boja', 'Smrť', 'Level Up', 'Nové kolo', '+EXP', 'Nové kúzlo', 'Odchod z boja'
    )),
    KúzloID INT REFERENCES Kúzlo(KúzloID),
    InštanciaPredmetuID INT REFERENCES InštanciaPredmetu(InštanciaPredmetuID),
    CieľováPostavaID INT REFERENCES Postava(PostavaID),
    VýsledokHoduKockou INT CHECK (VýsledokHoduKockou BETWEEN 1 AND 20),
    Výsledok INT,
    Úspech BOOLEAN,
    ČasZáznamu TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


-- 2. Indexy
CREATE INDEX idx_účastník_boj_bojid ON ÚčastníkBoju(BojID);
CREATE INDEX idx_účastník_boj_postavaid ON ÚčastníkBoju(PostavaID);
CREATE INDEX idx_záznam_boj_bojid ON ZáznamBoju(BojID);
CREATE INDEX idx_záznam_boj_postavaid ON ZáznamBoju(PostavaID);
CREATE INDEX idx_záznam_boj_cieľpostavaid ON ZáznamBoju(CieľováPostavaID);
CREATE INDEX idx_inštancia_predmet_bojid ON InštanciaPredmetu(BojID);
CREATE INDEX idx_inštancia_predmet_postavaid ON InštanciaPredmetu(PostavaID);
CREATE INDEX idx_postava_atribút_postavaid ON PostavaAtribút(PostavaID);
CREATE INDEX idx_trieda_atribút_triedaid ON TriedaAtribút(TriedaID);
CREATE INDEX idx_predmet_trieda_predmetid ON PredmetTrieda(PredmetID);
CREATE INDEX idx_kúzlo_kategória ON Kúzlo(KategóriaKúzlaID);
CREATE INDEX idx_postava_trieda ON Postava(TriedaID);
CREATE INDEX idx_zaznam_typakcie ON ZáznamBoju(TypAkcie);    




