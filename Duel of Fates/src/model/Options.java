package model;

import java.text.DateFormat.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Trieda Options obsahuje rôzne možnosti otázok pre hru.
 */

public class Options {
    public static final Choice[] options1 = {
            new Choice("Sahara", " Aka je najvacsia pust na svete?", new PickItUpAction(), true),
            new Choice("Gobi", " Aka je najvacsia pust na svete?", new LeaveItAction(), false)
    };

    public static final Choice[] options2 = {
            new Choice("Pravda", " Titanic sa potopil v roku 1911.", new PickItUpAction(), false),
            new Choice("Nepravda", " Titanic sa potopil v roku 1911.", new LeaveItAction(), true)
    };
    
    public static final Choice[] options3 = {
            new Choice("Nepravda", " Pacificky ocean je najvacsi na svete.", new PickItUpAction(), false),
            new Choice("Pravda", " Pacificky ocean je najvacsi na svete.", new LeaveItAction(), true)
    };
    
    
    public static final Choice[] options4 = {
    	    new Choice("c² = a² + b² - 2abcos(C)", "Eulerova formula je...", new PickItUpAction(), true),
    	    new Choice("c² = a² * b² - 2abcos(C)", "You encounter a dark alley. Will you take the risk and explore it?", new LeaveItAction(), false)
    	};

    	public static final Choice[] options5 = {
    	    new Choice("Nepravda", "Rovnik prechadza cez viacej zeme ako vody.", new PickItUpAction(), true),
    	    new Choice("Pravda", "Rovnik prechadza cez viacej zeme ako vody.", new LeaveItAction(), false)
    	};

    	public static final Choice[] options6 = {
    	    new Choice("Pravda", "Fibonaucciho postupnost sa zacina cislami 1, 1, 2, 3, 5, 8", new PickItUpAction(), false),
    	    new Choice("Nepravda", "Fibonaucciho postupnost sa zacina cislami 1, 1, 2, 3, 5, 8", new LeaveItAction(), true)
    	};

    	public static final Choice[] options7 = {
    			new Choice("Nepravda", "Je možné vytvoriť štvorec, ktorý je zložený z piatich menších štvorcov, pričom všetky menšie štvorce majú rovnakú plochu?", new PickItUpAction(), true),
    		    new Choice("Pravda", "Je možné vytvoriť štvorec, ktorý je zložený z piatich menších štvorcov, pričom všetky menšie štvorce majú rovnakú plochu?", new LeaveItAction(), false)
    	};

    	public static final Choice[] options8 = {
    			new Choice("Ano", "Bol Ľudovít XIV. francúzsky kráľ počas francúzskej revolúcie?", new PickItUpAction(), true),
    		    new Choice("Nie", "Bol Ľudovít XIV. francúzsky kráľ počas francúzskej revolúcie?", new LeaveItAction(), false)
    	};

    	public static final Choice[] options9 = {
    			new Choice("Nepravda", "Bola Helena z Troje dcérou kráľa Meneláa?", new PickItUpAction(), true),
    		    new Choice("Pravda", "Bola Helena z Troje dcérou kráľa Meneláa?", new LeaveItAction(), false)
    	};

    	public static final Choice[] options10 = {
    			new Choice("Žralok veľrybí", "Najväčší druh žraloka je...", new PickItUpAction(), true),
    		    new Choice("Žralok kráľovský", "Najväčší druh žraloka je...", new LeaveItAction(), false)
    	};
    	
    	public static final Choice[] options11 = {
    			 new Choice("Ano", "Je slon najväčším suchozemským cicavcom na svete?", new PickItUpAction(), true),
    			 new Choice("Nie", "Je slon najväčším suchozemským cicavcom na svete?", new LeaveItAction(), false)
    	};
    	
    	public static final Choice[] options12 = {
    		    new Choice("Ano", "Je Kanadský dolar oficiálnou menou Kanady?", new PickItUpAction(), true),
    		    new Choice("Nie", "Je Kanadský dolar oficiálnou menou Kanady?", new LeaveItAction(), false)
    		};
    	
    	public static final Choice[] options13 = {
    		    new Choice("Ano", "Bola Laika prvým živým tvorom, ktorý bol vyslaný do vesmíru?", new PickItUpAction(), true),
    		    new Choice("Nie", "Bola Laika prvým živým tvorom, ktorý bol vyslaný do vesmíru?", new LeaveItAction(), false)
    		};
    	
    	public static final Choice[] options14 = {
    		    new Choice("Nie", "Bol bývalý prezident USA Abraham Lincoln známy ako strýko Tom?", new PickItUpAction(), true),
    		    new Choice("Áno", "Bol bývalý prezident USA Abraham Lincoln známy ako strýko Tom?", new LeaveItAction(), false)
    		};

    	public static final Choice[] options15 = {
    		    new Choice("Áno", "Je K2 druhou najvyššou horou na svete?", new PickItUpAction(), true),
    		    new Choice("Nie", "Je K2 druhou najvyššou horou na svete?", new LeaveItAction(), false)
    		};
    	
    	public static final Choice[] options16 = {
    		    new Choice("Volga", "Najdlhšia rieka v Európe je...", new PickItUpAction(), true),
    		    new Choice("Dunaj", "Najdlhšia rieka v Európe je...", new LeaveItAction(), false)
    		};
    	
    	public static final Choice[] options17 = {
    		    new Choice("7", "Littleov zákon je možné aplikovať na akúkoľvek časť obchodu, ako napríklad konkrétny oddelenie alebo pokladňové linky. \n"
    		    		+ "Majiteľ obchodu určí, že počas pracovných hodín zhruba 84 zákazníkov za hodinu vykoná nákup a každý z týchto zákazníkov strávi \n"
    		    		+ "priemerný čas 5 minút v rade na pokladni. V akejkoľvek čase počas pracovných hodín je zhruba koľko zákazníkov v priemere čaká v \n"
    		    		+ "rade na pokladni, aby vykonali nákup v obchode Good Deals?", new PickItUpAction(), true),
    		    new Choice("6", "Littleov zákon je možné aplikovať na akúkoľvek časť obchodu, ako napríklad konkrétny oddelenie alebo pokladňové linky. \n"
    		    		+ "Majiteľ obchodu určí, že počas pracovných hodín zhruba 84 zákazníkov za hodinu vykoná nákup a každý z týchto zákazníkov strávi \n"
    		    		+ "priemerný čas 5 minút v rade na pokladni. V akejkoľvek čase počas pracovných hodín je zhruba koľko zákazníkov v priemere čaká v \n"
    		    		+ "rade na pokladni, aby vykonali nákup v obchode Good Deals?", new LeaveItAction(), false)
    		};
    	
    	public static final Choice[] options18 = {
    		    new Choice("4", "Ak 1=5, 2=3, 3=3, 4=5, 5=3, potom, 6=?", new PickItUpAction(), true),
    		    new Choice("3", "Ak 1=5, 2=3, 3=3, 4=5, 5=3, potom, 6=?", new LeaveItAction(), false)
    	};
    	
    	public static final Choice[] options19 = {
    		    new Choice("pred 41 rokmi", "Sally má 54 rokov a jej matka má 80 rokov, pred koľkými rokmi bol vek Sallyinej matky \n "
    		    		+ "rovnaký ako jej vek násobený tromi?", new PickItUpAction(), true),
    		    new Choice("pred 25 rokmi", "Sally má 54 rokov a jej matka má 80 rokov, pred koľkými rokmi bol vek Sallyinej matky \n "
    		    		+ "rovnaký ako jej vek násobený tromi?", new LeaveItAction(), false)
    	};
    	
    	public static final Choice[] options20 = {
    		    new Choice("10 20 30", "Ktoré 3 čísla majú rovnakú odpoveď, či sú sčítané alebo násobené spolu?", new PickItUpAction(), false),
    		    new Choice("2 1 3", "Ktoré 3 čísla majú rovnakú odpoveď, či sú sčítané alebo násobené spolu?", new LeaveItAction(), true)
    	};
    	
    	public static final Choice[] options21 = {
    		    new Choice("Ano", "Je Mesiac najbližšia planéta k Zemi?", new PickItUpAction(), false),
    		    new Choice("Nie", "Je Mesiac najbližšia planéta k Zemi?", new LeaveItAction(), true)
    		};

    		public static final Choice[] options22 = {
    		    new Choice("Nepravda", "Váhu vesmírneho šatstva národných rúk zabezpečuje gravitácia Zeme.", new PickItUpAction(), false),
    		    new Choice("Pravda", "Váhu vesmírneho šatstva národných rúk zabezpečuje gravitácia Zeme.", new LeaveItAction(), true)
    		};

    		public static final Choice[] options23 = {
    		    new Choice("Pravda", "Čierna diera je miesto s tak veľkou gravitáciou, že ani svetlo nemôže uniknúť.", new PickItUpAction(), true),
    		    new Choice("Nepravda", "Čierna diera je miesto s tak veľkou gravitáciou, že ani svetlo nemôže uniknúť.", new LeaveItAction(), false)
    		};
    		
    		public static final Choice[] options24 = {
    			    new Choice("Pravda", "Pluto je najväčším planétom v našej slnečnej sústave.", new PickItUpAction(), false),
    			    new Choice("Nepravda", "Pluto je najväčším planétom v našej slnečnej sústave.", new LeaveItAction(), true)
    			};

    			public static final Choice[] options25 = {
    			    new Choice("Ano", "Hlavné mesto Ruska je Šanghaj.", new PickItUpAction(), false),
    			    new Choice("Nie", "Hlavné mesto Ruska je Šanghaj.", new LeaveItAction(), true)
    			};

    			public static final Choice[] options26 = {
    			    new Choice("Nepravda", "Praha je hlavným mestom Česka.", new PickItUpAction(), false),
    			    new Choice("Pravda", "Praha je hlavným mestom Česka.", new LeaveItAction(), true)
    			};
    			
    			public static final Choice[] options27 = {
    				    new Choice("Nepravda", "Vulkán Mount Everest sa nachádza v Južnej Amerike.", new PickItUpAction(), true),
    				    new Choice("Pravda", "Vulkán Mount Everest sa nachádza v Južnej Amerike.", new LeaveItAction(), false)
    				};

    				public static final Choice[] options28 = {
    				    new Choice("Ano", "Čínska veľkosťka je najväčším psím plemenom na svete.", new PickItUpAction(), false),
    				    new Choice("Nie", "Čínska veľkosťka je najväčším psím plemenom na svete.", new LeaveItAction(), true)
    				};

    				public static final Choice[] options29 = {
    				    new Choice("Pravda", "Amazonka je najdlhšia rieka na svete.", new PickItUpAction(), true),
    				    new Choice("Nepravda", "Amazonka je najdlhšia rieka na svete.", new LeaveItAction(), false)
    				};
    				
    				public static final Choice[] options30 = {
    					    new Choice("Apollo 11", "Ktorá misia NASA prvá dosiahla povrch Mesiacu?", new PickItUpAction(), true),
    					    new Choice("Apollo 13", "Ktorá misia NASA prvá dosiahla povrch Mesiacu?", new LeaveItAction(), false),
    					};
    				
    				public static final Choice[] options31 = {
    					    new Choice("Australopithecus afarensis", "Ktorý druh človeka je známy svojimi náležitými fosíliami známymi ako \"Lucy\"?", new PickItUpAction(), true),
    					    new Choice("Homo habilis", "Ktorý druh človeka je známy svojimi náležitými fosíliami známymi ako \"Lucy\"?", new LeaveItAction(), false),
    					};
    				
    				public static final Choice[] options32 = {
    					    new Choice("Grónsko", "Ktorý ostrov je najväčší na svete?", new PickItUpAction(), true),
    					    new Choice("Nový Zéland", "Ktorý ostrov je najväčší na svete?", new LeaveItAction(), false),
    					};
    				
    				public static final Choice[] options33 = {
    					    new Choice("Meč", "Ktorá zbraň bola tradičnou zbraňou japonských samurajov?", new PickItUpAction(), true),
    					    new Choice("Luk", "Ktorá zbraň bola tradičnou zbraňou japonských samurajov?", new LeaveItAction(), false),
    					};
    				
    				public static final Choice[] options34 = {
    					    new Choice("Nepravda", "Anglický jazyk má najviac hovoriacich na svete.", new PickItUpAction(), true),
    					    new Choice("Pravda", "Anglický jazyk má najviac hovoriacich na svete.", new LeaveItAction(), false)
    					};
    				
    				public static final Choice[] options35 = {
    					    new Choice("Ano", "Galileo Galilei bol prvým vedcom, ktorý použil teleskop na pozorovanie vesmíru.", new PickItUpAction(), false),
    					    new Choice("Nie", "Galileo Galilei bol prvým vedcom, ktorý použil teleskop na pozorovanie vesmíru.", new LeaveItAction(), true)
    					};
    				
    				public static final Choice[] options36 = {
    					    new Choice("Ano", "Grafén je najtenším materiálom na svete.", new PickItUpAction(), true),
    					    new Choice("Nie", "Grafén je najtenším materiálom na svete.", new LeaveItAction(), false)
    					};
    				
    				public static final Choice[] options37 = {
    					    new Choice("Mammalia", "Cicavec po latínsky je...", new PickItUpAction(), true),
    					    new Choice("Mollusca", "Cicavec po latínsky je...", new LeaveItAction(), false)
    					};
    				
    				public static final Choice[] options38 = {
    					    new Choice("Mollusca", "Mäkkýše po latínsky sú...", new PickItUpAction(), true),
    					    new Choice("Amoebina", "Mäkkýše po latínsky sú...", new LeaveItAction(), false)
    					};
    				
    				public static final Choice[] options39 = {
    					    new Choice("Peptidová", "Ktorá väzba sa nachádza v bielkovinách?", new PickItUpAction(), true),
    					    new Choice("N-glykozidová", "Ktorá väzba sa nachádza v bielkovinách?", new LeaveItAction(), false)
    					};
    				
    				public static final Choice[] options40 = {
    					    new Choice("Ano", "Mars má dva mesiace.", new PickItUpAction(), false),
    					    new Choice("Nie", "Mars má dva mesiace.", new LeaveItAction(), true)
    					};
    				
    				public static final Choice[] options41 = {
    					    new Choice("Ortuť", "Ktorá z možností je kov?", new PickItUpAction(), false),
    					    new Choice("Sodík", "Ktorá z možností je kov?", new LeaveItAction(), true)
    					};
    				
    				public static final Choice[] options42 = {
    					    new Choice("Archaikum", "Ako sa nazývajú treťohory?", new PickItUpAction(), false),
    					    new Choice("Kenozoikum", "Ako sa nazývajú treťohory?", new LeaveItAction(), true)
    					};
    				
    				public static final Choice[] options43 = {
    					    new Choice("Lítium", "Ktorý kov farbí plameň na fialovo?", new PickItUpAction(), false),
    					    new Choice("Draslík", "Ktorý kov farbí plameň na fialovo?", new LeaveItAction(), true)
    					};
    				
    				public static final Choice[] options44 = {
    					    new Choice("Hydrolymfa", "Aká telová tekutina sa nachádza v cicavcoch?", new PickItUpAction(), false),
    					    new Choice("Tkanivový mok", "Aká telová tekutina sa nachádza v cicavcoch?", new LeaveItAction(), true)
    					};
    				
    				public static final Choice[] options45 = {
    					    new Choice("Kapusta", "Ktorá rastlina je jednoklíčnolistová?", new PickItUpAction(), false),
    					    new Choice("Tulipán", "Ktorá rastlina je jednoklíčnolistová?", new LeaveItAction(), true)
    					};
    				
    				public static final Choice[] options46 = {
    					    new Choice("Plynová energia", "Ktorý druh energie je najviac využívaný vo svete?", new PickItUpAction(), false),
    					    new Choice("Jadrová energia", "Ktorý druh energie je najviac využívaný vo svete?", new LeaveItAction(), true)
    					};

    					public static final Choice[] options47 = {
    					    new Choice("2000", "Ktorý rok bol prvým rokom 21. storočia?", new PickItUpAction(), false),
    					    new Choice("2001", "Ktorý rok bol prvým rokom 21. storočia?", new LeaveItAction(), true)
    					};

    					public static final Choice[] options48 = {
    					    new Choice("Sydney", "Aké je hlavné mesto Austrálie?", new PickItUpAction(), false),
    					    new Choice("Canberra", "Aké je hlavné mesto Austrálie?", new LeaveItAction(), true)
    					};

    					public static final Choice[] options49 = {
    					    new Choice("Venuša", "Ktorá planéta je najbližšia k Slnku?", new PickItUpAction(), false),
    					    new Choice("Merkúr", "Ktorá planéta je najbližšia k Slnku?", new LeaveItAction(), true)
    					};

    					public static final Choice[] options50 = {
    					    new Choice("Štruktúra DNA", "Ktorý vedecký objav bol považovaný za najdôležitejší v 20. storočí?", new PickItUpAction(), false),
    					    new Choice("Relativita", "Ktorý vedecký objav bol považovaný za najdôležitejší v 20. storočí?", new LeaveItAction(), true)
    					};

    					public static final Choice[] options51 = {
    					    new Choice("Kanada", "Ktorý štát má najdlhšiu pobrežnú čiaru na svete?", new PickItUpAction(), false),
    					    new Choice("Rusko", "Ktorý štát má najdlhšiu pobrežnú čiaru na svete?", new LeaveItAction(), true)
    					};

    					public static final Choice[] options52 = {
    					    new Choice("7", "Koľko kontinentov existuje na svete?", new PickItUpAction(), false),
    					    new Choice("6", "Koľko kontinentov existuje na svete?", new LeaveItAction(), true)
    					};

    					public static final Choice[] options53 = {
    					    new Choice("Vodík", "Ktorý prvok je najľahší v periodickom systéme prvkov?", new PickItUpAction(), false),
    					    new Choice("Hélium", "Ktorý prvok je najľahší v periodickom systéme prvkov?", new LeaveItAction(), true)
    					};

    					public static final Choice[] options54 = {
    					    new Choice("Kolumbia", "Ktorá krajina je najväčším producentom kávy na svete?", new PickItUpAction(), false),
    					    new Choice("Brazília", "Ktorá krajina je najväčším producentom kávy na svete?", new LeaveItAction(), true)
    					};
    					
    					public static final Choice[] options55 = {
    						    new Choice("Liechtenštajnsko", "Ktorá je najmenšou krajinou na svete?", new PickItUpAction(), false),
    						    new Choice("Vatikán", "Ktorá je najmenšou krajinou na svete?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options56 = {
    						    new Choice("Ázia", "Ktorý kontinent má najväčšiu populáciu?", new PickItUpAction(), true),
    						    new Choice("Afrika", "Ktorý kontinent má najväčšiu populáciu?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options57 = {
    						    new Choice("Francúzsko", "Ktorá krajina bola prvou, ktorá zaviedla pošty?", new PickItUpAction(), false),
    						    new Choice("Veľká Británia", "Ktorá krajina bola prvou, ktorá zaviedla pošty?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options58 = {
    						    new Choice("Slovensko", "Ktorá krajina je domovom najväčšieho počtu hradov a zámkov na obyvateľa?", new PickItUpAction(), false),
    						    new Choice("Česká republika", "Ktorá krajina je domovom najväčšieho počtu hradov a zámkov na obyvateľa?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options59 = {
    						    new Choice("6 500 km", "Aká je približná vzdialenosť medzi New Yorkom a Parížom v priamke?", new PickItUpAction(), false),
    						    new Choice("5 800 km", "Aká je približná medzi New Yorkom a Parížom v priamke?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options60 = {
    						    new Choice("Nie", "Je Kalahari najväčšou piesočnou púšťou v Afrike?", new PickItUpAction(), true),
    						    new Choice("Ano", "Je Kalahari najväčšou piesočnou púšťou v Afrike?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options61 = {
    						    new Choice("Aljaška", "Ktorý americký štát je najväčší podľa územia?", new PickItUpAction(), true),
    						    new Choice("Texas", "Ktorý americký štát je najväčší podľa územia?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options62 = {
    						    new Choice("Západné", "Na ktorom pobreží sa nachádza Šanghaj, najväčšie mesto Číny?", new PickItUpAction(), false),
    						    new Choice("Východné", "Na ktorom pobreží sa nachádza Šanghaj, najväčšie mesto Číny?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options63 = {
    						    new Choice("Ano", "Je Rímskokatolícka cirkev najväčšou kresťanskou denomináciou na svete?", new PickItUpAction(), true),
    						    new Choice("Nie", "Je Rímskokatolícka cirkev najväčšou kresťanskou denomináciou na svete?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options64 = {
    						    new Choice("Rusko", "Ktorá krajina bola prvou, ktorá zaviedla zimné pneumatiky na autá?", new PickItUpAction(), false),
    						    new Choice("Kanada", "Ktorá krajina bola prvou, ktorá zaviedla zimné pneumatiky na autá?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options65 = {
    						    new Choice("Afrika", "Ktorý kontinent má najdlhšiu rieku na svete, Amazonku?", new PickItUpAction(), false),
    						    new Choice("Amerika", "Ktorý kontinent má najdlhšiu rieku na svete, Amazonku?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options66 = {
    						    new Choice("Šanghaj", "Aké je hlavné mesto Číny?", new PickItUpAction(), false),
    						    new Choice("Peking", "Aké je hlavné mesto Číny?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options67 = {
    						    new Choice("2012", "V ktorom roku sa konali letné olympijské hry v Riu de Janeiro?", new PickItUpAction(), false),
    						    new Choice("2016", "V ktorom roku sa konali letné olympijské hry v Riu de Janeiro?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options68 = {
    						    new Choice("Kaukaz", "Ktoré pohorie oddeluje Európu od Ázie?", new PickItUpAction(), false),
    						    new Choice("Ural", "Ktoré pohorie oddeluje Európu od Ázie?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options69 = {
    						    new Choice("Austrália", "Ktorý kontinent je tiež známy ako \"Kontinent dolu hlavou\"?", new PickItUpAction(), true),
    						    new Choice("Antarktída", "Ktorý kontinent je tiež známy ako \"Kontinent dolu hlavou\"?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options70 = {
    						    new Choice("13", "Koľko znamení zverokruhu existuje?", new PickItUpAction(), false),
    						    new Choice("12", "Koľko znamení zverokruhu existuje?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options71 = {
    						    new Choice("3", "Koľko básnických dekád má Danteova Božská komédia?", new PickItUpAction(), false),
    						    new Choice("4", "Koľko básnických dekád má Danteova Božská komédia?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options72 = {
    						    new Choice("Ano", "Bol Ázerbájdžan niekedy súčasťou ZSSR?", new PickItUpAction(), true),
    						    new Choice("Nie", "Bol Ázerbájdžan niekedy súčasťou ZSSR?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options73 = {
    						    new Choice("Meterológia", "Ako sa nazýva veda študujúca počasie?", new PickItUpAction(), true),
    						    new Choice("Weterológia", "Ako sa nazýva veda študujúca počasie?.", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options74 = {
    						    new Choice("8 849", "Aký je najvyšší bod v Himalájach? (v metroch)", new PickItUpAction(), false),
    						    new Choice("8 848", "Aký je najvyšší bod v Himalájach? (v metroch)", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options75 = {
    						    new Choice("Volkswagen", "Ktorá automobilka produkuje model Beetle?", new PickItUpAction(), true),
    						    new Choice("Ford", "Ktorá automobilka produkuje model Beetle?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options76 = {
    						    new Choice("6 371 km", "Aký je polomer Zeme v kilometroch?", new PickItUpAction(), true),
    						    new Choice("6 378 km", "Aký je polomer Zeme v kilometroch?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options77 = {
    						    new Choice("Approximately", "Ako sa po anglicky povie slovo približne?", new PickItUpAction(), true),
    						    new Choice("Between", "Ako sa po anglicky povie slovo približne?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options78 = {
    						    new Choice("24", "Koľko písmen je v anglickej abecede?", new PickItUpAction(), false),
    						    new Choice("26", "Koľko písmen je v anglickej abecede?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options79 = {
    						    new Choice("Kancelár", "Kto je hlavou nemeckej vlády?", new PickItUpAction(), true),
    						    new Choice("Premiér", "Kto je hlavou nemeckej vlády?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options80 = {
    						    new Choice("Way of Milk", "Ako sa po anglicky volá naša galaxia?", new PickItUpAction(), false),
    						    new Choice("Milky Way", "Ako sa po anglicky volá naša galaxia?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options81 = {
    						    new Choice("Ano", "Je Kuba ostrov?", new PickItUpAction(), true),
    						    new Choice("Nie", "Je Kuba ostrov?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options82 = {
    						    new Choice("S", "Ako sa označuje síla vo fyzike?", new PickItUpAction(), false),
    						    new Choice("F", "Ako sa označuje síla vo fyzike?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options83 = {
    						    new Choice("Banská Bystrica", "Medzi najstaršie mestá na Slovensku patrí...", new PickItUpAction(), false),
    						    new Choice("Zvolen", "Medzi najstaršie mestá na Slovensku patrí...", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options84 = {
    						    new Choice("Bratislava, Košice, Žilina, Trenčín", "4 najväčšie železničné uzly Slovenska sú...", new PickItUpAction(), false),
    						    new Choice("Bratislava, Košice, Žilina, Zvolen", "4 najväčšie železničné uzly Slovenska sú...", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options85 = {
    						    new Choice("9", "Koľko národných parkov má Slovensko?", new PickItUpAction(), true),
    						    new Choice("6", "Koľko národných parkov má Slovensko?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options86 = {
    						    new Choice("Bratislava", "V ktorom slovenskom meste sa nachádza najväčší rímskokatolícky kostol na Slovensku?", new PickItUpAction(), false),
    						    new Choice("Košice", "V ktorom slovenskom meste sa nachádza najväčší rímskokatolícky kostol na Slovensku?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options87 = {
    						    new Choice("3.14159265358", "Aká je hodnota matematickej konštanty označovanej ako \"π\" (pi)?", new PickItUpAction(), true),
    						    new Choice("3.14124875656", "Aká je hodnota matematickej konštanty označovanej ako \\\"π\\\" (pi)?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options88 = {
    						    new Choice("16", "Koľko je hodnota 2 na 4. mocninu?", new PickItUpAction(), true),
    						    new Choice("8", "Koľko je hodnota 2 na 4. mocninu?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options89 = {
    						    new Choice("127", "Koľko je hodnota 3 na 4. mocninu?", new PickItUpAction(), false),
    						    new Choice("81", "Koľko je hodnota 3 na 4. mocninu?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options90 = {
    						    new Choice("625", "Koľko je hodnota 5 na 4. mocninu?", new PickItUpAction(), true),
    						    new Choice("225", "Koľko je hodnota 5 na 4. mocninu?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options91 = {
    						    new Choice("1296", "Koľko je hodnota 6 na 4. mocninu?", new PickItUpAction(), true),
    						    new Choice("2026", "Koľko je hodnota 6 na 4. mocninu?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options92 = {
    						    new Choice("1296", "Koľko je hodnota 2 na 14. mocninu?", new PickItUpAction(), false),
    						    new Choice("16384", "Koľko je hodnota 2 na 14. mocninu?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options93 = {
    						    new Choice("65536", "Koľko je hodnota 4 na 8. mocninu?", new PickItUpAction(), true),
    						    new Choice("16384", "Koľko je hodnota 4 na 8. mocninu?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options94 = {
    						    new Choice("x = 2", "V riešení rovnice 3x - 4 = -10, aká je hodnota pre x?", new PickItUpAction(), false),
    						    new Choice("x = -2", "V riešení rovnice 3x - 4 = -10, aká je hodnota pre x?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options95 = {
    						    new Choice("x = 8", "V riešení rovnice 2(x + 3) = 22, aká je hodnota pre x?", new PickItUpAction(), true),
    						    new Choice("x = 7", "V riešení rovnice 2(x + 3) = 22, aká je hodnota pre x?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options96 = {
    						    new Choice("x = -10", "Riešte rovnicu: 2(x + 4) - 5 = x - 7", new PickItUpAction(), false),
    						    new Choice("x = 3", "Riešte rovnicu: 2(x + 4) - 5 = x - 7", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options97 = {
    						    new Choice("x = 3, x = 1/2", "Riešte rovnicu: 2x^2 - 7x + 3 = 0", new PickItUpAction(), true),
    						    new Choice("x = 3, x = 1", "Riešte rovnicu: 2x^2 - 7x + 3 = 0", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options98 = {
    						    new Choice("nemá riešenie", "Riešte rovnicu: log2(x + 3) = log2(x - 1)", new PickItUpAction(), true),
    						    new Choice("x = 2", "Riešte rovnicu: log2(x + 3) = log2(x - 1)", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options99 = {
    						    new Choice("x = sqrt(6) + 2", "Riešte rovnicu: sqrt(2x + 3) = x - 1", new PickItUpAction(), true),
    						    new Choice("x = 5", "Riešte rovnicu: sqrt(2x + 3) = x - 1", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options100 = {
    						    new Choice("11", "Koľko prvočísel je v intervale od 1 do 30?", new PickItUpAction(), false),
    						    new Choice("10", "Koľko prvočísel je v intervale od 1 do 30?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options101 = {
    						    new Choice("120", "Koľko rôznych spôsobov existuje, ako vybrať 3 rôzne položky zo zbierky 10 položiek?", new PickItUpAction(), false),
    						    new Choice("720", "Koľko rôznych spôsobov existuje, ako vybrať 3 rôzne položky zo zbierky 10 položiek?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options102 = {
    						    new Choice("14.7 metrov", "Aký je obsah trojuholníka so stranami dlhými 5, 6 a 7 metrov?", new PickItUpAction(), false),
    						    new Choice("14 metrov", "Aký je obsah trojuholníka so stranami dlhými 5, 6 a 7 metrov?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options103 = {
    						    new Choice("6", "Koľko je 3!, čo predstavuje faktoriál čísla 3?", new PickItUpAction(), true),
    						    new Choice("9", "Koľko je 3!, čo predstavuje faktoriál čísla 3?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options104 = {
    						    new Choice("362880", "Koľko je 9!, čo predstavuje faktoriál čísla 9?", new PickItUpAction(), true),
    						    new Choice("22952", "Koľko je 9!, čo predstavuje faktoriál čísla 9?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options105= {
    				    new Choice("14", "Ak je obvod štvorca 56 cm, aká je jeho strana?", new PickItUpAction(), true),
    				    new Choice("12", "Ak je obvod štvorca 56 cm, aká je jeho strana?", new LeaveItAction(), false)
    				};
    					
    					public static final Choice[] options106 = {
    						    new Choice("26", "Ak je log₂(x) = 5, koľko je hodnota x?", new PickItUpAction(), false),
    						    new Choice("32", "Ak je log₂(x) = 5, koľko je hodnota x?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options107 = {
    						    new Choice("3/2", "Ak je 4^(x-1) = 2, koľko je hodnota x?", new PickItUpAction(), true),
    						    new Choice("2", "Ak je 4^(x-1) = 2, koľko je hodnota x?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options108 = {
    						    new Choice("17", "Ak je obvod štvorca 68 cm, aká je jeho strana?", new PickItUpAction(), true),
    						    new Choice("12", "Ak je obvod štvorca 68 cm, aká je jeho strana?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options109 = {
    						    new Choice("25", "Koľko je 5!, čo predstavuje faktoriál čísla 5?", new PickItUpAction(), false),
    						    new Choice("120", "Koľko je 5!, čo predstavuje faktoriál čísla 5?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options110 = {
    						    new Choice("50 rokov", "Ak je otec o 20 rokov starší ako jeho syn, a súčet ich vekov je 80 rokov, koľko má otec rokov?", new PickItUpAction(), true),
    						    new Choice("60 rokov", "Ak je otec o 20 rokov starší ako jeho syn, a súčet ich vekov je 80 rokov, koľko má otec rokov?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options111 = {
    						    new Choice("30 rokov", "Ak je otec o 20 rokov starší ako jeho syn, a súčet ich vekov je 80 rokov, koľko má syn rokov?", new PickItUpAction(), true),
    						    new Choice("20 rokov", "Ak je otec o 20 rokov starší ako jeho syn, a súčet ich vekov je 80 rokov, koľko má syn rokov?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options112 = {
    						    new Choice("56 rokov", "Ak je otec o 20 rokov starší ako jeho syn, a súčet ich vekov je 92 rokov, koľko má otec rokov?", new PickItUpAction(), true),
    						    new Choice("62 rokov", "Ak je otec o 20 rokov starší ako jeho syn, a súčet ich vekov je 92 rokov, koľko má otec rokov?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options113 = {
    						    new Choice("36 rokov", "Ak je otec o 20 rokov starší ako jeho syn, a súčet ich vekov je 92 rokov, koľko má syn rokov?", new PickItUpAction(), true),
    						    new Choice("42 rokov", "Ak je otec o 20 rokov starší ako jeho syn, a súčet ich vekov je 92 rokov, koľko má syn rokov", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options114 = {
    						    new Choice("150 centov", "Ak tri vajcia stoja 60 centov, koľko stojí päť vajec?", new PickItUpAction(), false),
    						    new Choice("120 centov", "Ak tri vajcia stoja 60 centov, koľko stojí šesť vajec?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options115 = {
    						    new Choice("84 stromov", "Ak je jeden záhradník schopný zasadiť 12 stromov za deň, \n "
    						    		+ "koľko stromov zasadia šiesti záhradníci za deň?", new PickItUpAction(), false),
    						    new Choice("72 stromov", "Ak je jeden záhradník schopný zasadiť 12 stromov za deň, \n"
    						    		+ "koľko stromov zasadia šiesti záhradníci za deň?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options116 = {
    						    new Choice("100", "Koľko rokov trvala 100 ročná vojna?", new PickItUpAction(), false),
    						    new Choice("116", "Koľko rokov trvala 100 ročná vojna?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options117 = {
    						    new Choice("Palindrom", "Ako sa volá slovo, ktoré je rovnaké, keď ho čítame odzadu?", new PickItUpAction(), true),
    						    new Choice("Anagram", "Ako sa volá slovo, ktoré je rovnaké, keď ho čítame odzadu?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options118 = {
    						    new Choice("30. augusta", "Ktorého dňa sa v Slovenskej republike oslavuje výročie SNP?", new PickItUpAction(), false),
    						    new Choice("29. augusta", "Ktorého dňa sa v Slovenskej republike oslavuje výročie SNP?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options119 = {
    						    new Choice("Prešov", "Ktoré mesto má väčšiu rozlohu?", new PickItUpAction(), false),
    						    new Choice("Zvolen", "Ktoré mesto má väčšiu rozlohu?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options120 = {
    						    new Choice("Žilina", "Ktoré mesto má väčšiu rozlohu?", new PickItUpAction(), false),
    						    new Choice("Zvolen", "Ktoré mesto má väčšiu rozlohu?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options121 = {
    						    new Choice("Bardejov", "Ktoré mesto má väčšiu rozlohu?", new PickItUpAction(), false),
    						    new Choice("Zvolen", "Ktoré mesto má väčšiu rozlohu?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options122 = {
    						    new Choice("Košice", "Ktoré mesto má väčší počet obyvateľov?", new PickItUpAction(), true),
    						    new Choice("Prešov", "Ktoré mesto má väčší počet obyvateľov?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options123 = {
    						    new Choice("Martin", "Ktoré mesto má väčší počet obyvateľov?", new PickItUpAction(), true),
    						    new Choice("Lučenec", "Ktoré mesto má väčší počet obyvateľov?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options124 = {
    						    new Choice("Ružomberok", "Ktoré mesto má väčší počet obyvateľov?", new PickItUpAction(), true),
    						    new Choice("Šala", "Ktoré mesto má väčší počet obyvateľov?", new LeaveItAction(), false)
    						};
    				
    					public static final Choice[] options125 = {
    						    new Choice("Zvolen", "Ktoré mesto má väčší počet obyvateľov?", new PickItUpAction(), true),
    						    new Choice("Humenné", "Ktoré mesto má väčší počet obyvateľov?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options126 = {
    						    new Choice("Zvolen", "Ktoré mesto má väčší počet obyvateľov?", new PickItUpAction(), true),
    						    new Choice("Nové Zámky", "Ktoré mesto má väčší počet obyvateľov?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options127 = {
    						    new Choice("Nitra", "Ktoré mesto má väčší počet obyvateľov?", new PickItUpAction(), true),
    						    new Choice("Banská Bystrica", "Ktoré mesto má väčší počet obyvateľov?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options128 = {
    						    new Choice("Banská Bystrica", "Ktoré slovenské mesto je považované za geografické centrum Slovenska?", new PickItUpAction(), false),
    						    new Choice("Kremnica", "Ktoré slovenské mesto je považované za geografické centrum Slovenska?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options129 = {
    						    new Choice("Trnava", "V ktorom slovenskom meste sa nachádza prvá univerzita na Slovensku?", new PickItUpAction(), false),
    						    new Choice("Bratislava", "V ktorom slovenskom meste sa nachádza prvá univerzita na Slovensku?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options130 = {
    						    new Choice("Katolícka univerzita v Ružomberku", "Ktorá z univerzít na Slovensku je staršia?", new PickItUpAction(), false),
    						    new Choice("Technická univerzita vo Zvolene", "Ktorá z univerzít na Slovensku je staršia?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options131 = {
    						    new Choice("Univerzita Pavla Jozefa Šafárika v Košiciach", "Ktorá z univerzít na Slovensku je staršia?", new PickItUpAction(), false),
    						    new Choice("Technická univerzita vo Zvolene", "Ktorá z univerzít na Slovensku je staršia?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options132 = {
    						    new Choice("Nitra", "Ktoré mesto má vyššiu nadmorskú výšku: Nitra alebo Trenčín?", new PickItUpAction(), false),
    						    new Choice("Trenčín", "Ktoré mesto má vyššiu nadmorskú výšku: Nitra alebo Trenčín?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options133 = {
    						    new Choice("Prešov", "Ktoré mesto je východnejšie?", new PickItUpAction(), true),
    						    new Choice("Košice", "Ktoré mesto je východnejšie?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options134 = {
    						    new Choice("Topoľčany", "Ktoré mesto je východnejšie?", new PickItUpAction(), true),
    						    new Choice("Trenčín", "Ktoré mesto je východnejšie?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options135 = {
    						    new Choice("Trnava", "Ktoré mesto leží bližšie k hlavnému mesta?", new PickItUpAction(), true),
    						    new Choice("Nitra", "Ktoré mesto leží bližšie k hlavnému mesta?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options136 = {
    						    new Choice("Zvolen", "Ktoré mesto leží bližšie k hlavnému mesta?", new PickItUpAction(), true),
    						    new Choice("Banská Bystrica", "Ktoré mesto leží bližšie k hlavnému mesta?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options137 = {
    						    new Choice("Nové Zámky", "Ktoré mesto leží bližšie k hlavnému mesta?", new PickItUpAction(), true),
    						    new Choice("Levice", "Ktoré mesto leží bližšie k hlavnému mesta?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options138 = {
    						    new Choice("Vrútky", "Ktoré mesto je východnejšie?", new PickItUpAction(), true),
    						    new Choice("Púchov", "Ktoré mesto je východnejšie?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options139 = {
    						    new Choice("Brezno", "Ktoré mesto je východnejšie?", new PickItUpAction(), true),
    						    new Choice("Veľký Krtíš", "Ktoré mesto je východnejšie?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options140 = {
    						    new Choice("4", "Koľko železničných tratí prechádza mestom Zvolen?", new PickItUpAction(), true),
    						    new Choice("2", "Koľko železničných tratí prechádza mestom Zvolen?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options141 = {
    						    new Choice("2", "Koľko železničných tratí prechádza mestom Banská Bystrica?", new PickItUpAction(), true),
    						    new Choice("5", "Koľko železničných tratí prechádza mestom Banská Bystrica?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options142 = {
    						    new Choice("2", "Koľko železničných tratí prechádza mestom Poprad?", new PickItUpAction(), true),
    						    new Choice("4", "Koľko železničných tratí prechádza mestom Poprad?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options143 = {
    						    new Choice("Košice", "Ktoré mesto má viac železničných staníc a zástavok?", new PickItUpAction(), false),
    						    new Choice("Bratislava", "Ktoré mesto má viac železničných staníc a zástavok?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options144 = {
    						    new Choice("Martin", "Ktoré mesto má viac železničných staníc a zástavok?", new PickItUpAction(), false),
    						    new Choice("Vrútky", "Ktoré mesto má viac železničných staníc a zástavok?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options145 = {
    						    new Choice("Dunaj", "Ktorá rieka je 2. najdlhšia na Slovensku?", new PickItUpAction(), false),
    						    new Choice("Hron", "Ktorá rieka je 2. najdlhšia na Slovensku?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options146 = {
    						    new Choice("Dunaj", "Ktorá rieka je najdlhšia na Slovensku?", new PickItUpAction(), false),
    						    new Choice("Váh", "Ktorá rieka je najdlhšia na Slovensku?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options147 = {
    						    new Choice("Košice", "V ktorom meste sa nachádza Hlavná železničná stanica?", new PickItUpAction(), false),
    						    new Choice("Bratislava", "VV ktorom meste sa nachádza Hlavná železničná stanica?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options148 = {
    						    new Choice("Košice", "V ktorom meste sa nachádza Osobná železničná stanica?", new PickItUpAction(), false),
    						    new Choice("Zvolen", "VV ktorom meste sa nachádza Osobná železničná stanica?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options149 = {
    						    new Choice("Košice", "V ktorom meste sa križuje viac diaľníc?", new PickItUpAction(), true),
    						    new Choice("Banská Bystrica", "V ktorom meste sa križuje viac diaľníc?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options150 = {
    						    new Choice("Zvolen", "V ktorom meste sa križuje viac diaľníc?", new PickItUpAction(), true),
    						    new Choice("Banská Bystrica", "V ktorom meste sa križuje viac diaľníc?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options151 = {
    						    new Choice("90 km/h", "Aká je maximálna povolená rýchlosť pre osobné vozidlá mimo obce na cestách v Slovenskej republike?", new PickItUpAction(), true),
    						    new Choice("100 km/h", "Aká je maximálna povolená rýchlosť pre osobné vozidlá mimo obce na cestách v Slovenskej republike?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options152 = {
    						    new Choice("50 km/h", "Aká je maximálna povolená rýchlosť pre osobné vozidlá v obciach na cestách v Slovenskej republike?", new PickItUpAction(), true),
    						    new Choice("60 km/h", "Aká je maximálna povolená rýchlosť pre osobné vozidlá v obciach na cestách v Slovenskej republike?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options153 = {
    						    new Choice("1", "Koľko percent alkoholu v krvi je maximálna povolená hranica pre vodičov do 24 rokov v Slovenskej republike?", new PickItUpAction(), false),
    						    new Choice("0", "Koľko percent alkoholu v krvi je maximálna povolená hranica pre vodičov do 24 rokov v Slovenskej republike?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options154 = {
    						    new Choice("STK", "Ako sa skracuje označenie pre povinnú technickú kontrolu vozidiel v Slovenskej republike?", new PickItUpAction(), true),
    						    new Choice("EK", "Ako sa skracuje označenie pre povinnú technickú kontrolu vozidiel v Slovenskej republike?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options155 = {
    						    new Choice("Volkswagen", "Ktorý z uvedených výrobcov automobilov je nemecký?", new PickItUpAction(), true),
    						    new Choice("Renault", "Ktorý z uvedených výrobcov automobilov je nemecký?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options156 = {
    						    new Choice("Google", "Ktorá z uvedených spoločností je najväčším poskytovateľom internetových vyhľadávačov?", new PickItUpAction(), true),
    						    new Choice("Microsoft", "Ktorá z uvedených spoločností je najväčším poskytovateľom internetových vyhľadávačov?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options157 = {
    						    new Choice("Kanada", "Ktorá z uvedených krajín je najväčším vývozcom javorového sirupu?", new PickItUpAction(), true),
    						    new Choice("Austrália", "Ktorá z uvedených krajín je najväčším vývozcom javorového sirupu?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options158 = {
    						    new Choice("Merkúr", "Ktorá planéta v slnečnej sústave je druhá najmenšia?", new PickItUpAction(), false),
    						    new Choice("Mars", "Ktorá planéta v slnečnej sústave je druhá najmenšia?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options159 = {
    						    new Choice("50", "Ak chcete kúpiť niečo za 60 €, ale zlacnelo to o 10%. Koľko budete musieť zaplatiť?", new PickItUpAction(), false),
    						    new Choice("54", "Ak chcete kúpiť niečo za 60 €, ale zlacnelo to o 10%. Koľko budete musieť zaplatiť?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options160 = {
    						    new Choice("35", "Ak chcete kúpiť niečo za 40 €, ale zlacnelo to o 12.5%. Koľko budete musieť zaplatiť?", new PickItUpAction(), true),
    						    new Choice("36", "Ak chcete kúpiť niečo za 40 €, ale zlacnelo to o 12.5%. Koľko budete musieť zaplatiť?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options161 = {
    						    new Choice("50", "Ak chcete kúpiť niečo za 60 €, ale zlacnelo to o 20%. Koľko budete musieť zaplatiť?", new PickItUpAction(), false),
    						    new Choice("48", "Ak chcete kúpiť niečo za 60 €, ale zlacnelo to o 20%. Koľko budete musieť zaplatiť?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options162 = {
    						    new Choice("74", "Ak chcete kúpiť niečo za 90 €, ale zlacnelo to o 20%. Koľko budete musieť zaplatiť?", new PickItUpAction(), false),
    						    new Choice("72", "Ak chcete kúpiť niečo za 90 €, ale zlacnelo to o 20%. Koľko budete musieť zaplatiť?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options163 = {
    						    new Choice("126.5", "Ak chcete kúpiť niečo za 150 €, ale zlacnelo to o 15%. Koľko budete musieť zaplatiť?", new PickItUpAction(), false),
    						    new Choice("127.5", "Ak chcete kúpiť niečo za 150 €, ale zlacnelo to o 15%. Koľko budete musieť zaplatiť?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options164 = {
    						    new Choice("55.4", "Ak chcete kúpiť niečo za 90 €, ale zlacnelo to o 38%. Koľko budete musieť zaplatiť?", new PickItUpAction(), false),
    						    new Choice("55.8", "Ak chcete kúpiť niečo za 90 €, ale zlacnelo to o 38%. Koľko budete musieť zaplatiť?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options165 = {
    						    new Choice("35", "Ak máte 50 cukríkov a dáte 40% kamarátom, koľko cukríkov vám zostane?", new PickItUpAction(), false),
    						    new Choice("30", "Ak máte 50 cukríkov a dáte 40% kamarátom, koľko cukríkov vám zostane?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options166 = {
    						    new Choice("117", "Ak chcete zo 195 kníh predať 40%, koľko kníh zostane?", new PickItUpAction(), true),
    						    new Choice("105", "Ak chcete zo 195 kníh predať 40%, koľko kníh zostane?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options167 = {
    						    new Choice("ATP", "Aký druh energie sa získava v krebsovom cykle?", new PickItUpAction(), true),
    						    new Choice("GTP", "Aký druh energie sa získava v krebsovom cykle?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options168 = {
    						    new Choice("30", "Ak cena niečoho bola 20 € a zvýšila sa o 25%, koľko to teraz stojí?", new PickItUpAction(), false),
    						    new Choice("25", "Ak cena niečoho bola 20 € a zvýšila sa o 25%, koľko to teraz stojí?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options169 = {
    						    new Choice("34", "Ak cena niečoho bola 30 € a zvýšila sa o 13%, koľko to teraz cca stojí?", new PickItUpAction(), false),
    						    new Choice("33", "Ak cena niečoho bola 30 € a zvýšila sa o 13%, koľko to teraz cca stojí?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options170 = {
    						    new Choice("66", "Ak cena niečoho bola 60 € a zvýšila sa o 10%, koľko teraz stojí?", new PickItUpAction(), true),
    						    new Choice("63", "Ak cena niečoho bola 60 € a zvýšila sa o 10%, koľko teraz stojí?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options171 = {
    						    new Choice("123", "Na výlet vydala skupina turistov neznámu sumu peňazí. Počas obeda každý z turistov \n"
    						    		+ "vypil tri veľké pivá za 12 €, dva poháre vína za 8 € a zjedol štyri porcie hranolčekov za 6 €. \n"
    						    		+ "Potom im zostalo dokopy 21 €. Koľko peňazí mali na začiatku?", new PickItUpAction(), false),
    						    new Choice("97", "Na výlet vydala skupina turistov neznámu sumu peňazí. Počas obeda každý z turistov \n"
    						    		+ "vypil tri veľké pivá za 12 €, dva poháre vína za 8 € a zjedol štyri porcie hranolčekov za 6 €. \n"
    						    		+ "Potom im zostalo dokopy 21 €. Koľko peňazí mali na začiatku?", new LeaveItAction(), true)
    						};
    					
    					public static final Choice[] options172 = {
    						    new Choice("2/3", "Mám tri rovnaké mince. Na dvoch minciach je hlava a na jednej je znak písmena 'X'. \n"
    						    		+ "Ak vyberiem jednu mincu náhodne a hodím ju, aká je pravdepodobnosť, že padne hlava?", new PickItUpAction(), true),
    						    new Choice("2/5", "Mám tri rovnaké mince. Na dvoch minciach je hlava a na jednej je znak písmena 'X'. \n"
    						    		+ "Ak vyberiem jednu mincu náhodne a hodím ju, aká je pravdepodobnosť, že padne hlava?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options173 = {
    						    new Choice("Nie", "Je možné vytvoriť trojuholník s uhlopriečkami 2, 3 a 6?", new PickItUpAction(), true),
    						    new Choice("Ano", "Je možné vytvoriť trojuholník s uhlopriečkami 2, 3 a 6?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options174 = {
    						    new Choice("10", "Ak jedna bájna bytost vysadí jedno bájne vajce za 1 bájný deň, \n"
    						    		+ "koľko bájných vajec vysadí 5 bájnych  bytostí za 12 dní od dnes \n"
    						    		+ "(ale bájny deň nastane len každú stredu a dnes je pondelok)?", new PickItUpAction(), true),
    						    new Choice("12", "Ak jedna bájna bytost vysadí jedno bájne vajce za 1 bájný deň, \n"
    						    		+ "koľko bájných vajec vysadí 5 bájnych  bytostí za 12 dní od dnes \n "
    						    		+ "(ale bájny deň nastane len každú stredu a dnes je pondelok)?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options175 = {
    						    new Choice("25", "Ak jedna bájna bytost vysadí jedno bájne vajce za 1 bájný deň,\n "
    						    		+ "koľko bájných vajec vysadí 35 bájnych  bytostí za 12 dní od dnes\n "
    						    		+ "(ale bájny deň nastane len každú stredu a dnes je pondelok)?\n", new PickItUpAction(), true),
    						    new Choice("18", "Ak jedna bájna bytost vysadí jedno bájne vajce za 1 bájný deň, \n"
    						    		+ "koľko bájných vajec vysadí 35 bájnych  bytostí za 12 dní od dnes \n "
    						    		+ "(ale bájny deň nastane len každú stredu a dnes je pondelok)?", new LeaveItAction(), false)
    						};
    					
    					public static final Choice[] options176 = {
    						    new Choice("68", "V jednej bájnej krajine existujú tri druhy bájnych bytostí, ktoré vysádzajú bájne vajcia. \n"
    						    		+ "Prvý druh vysádza vajcia len v stredu, druhý vysádza vajcia len v piatok a tretí druh vysádza vajcia každý pondelok a nedeľu. \n"
    						    		+ "Dnes je sobota a do konca mesiaca zostáva 26 dní. Ak prvý druh bytostí vysádza 2 vajcia za každý bájný deň,\n "
    						    		+ "druhý druh vysádza 3 vajcia za každý bájný deň a tretí druh vysádza len jedno vajce za každý bájný deň,\n "
    						    		+ "koľko bájnych vajec sa ešte vysadí do konca mesiaca? \\n"
    						    		+ "Počet bytostí prvého druhu je 3, druhého 4, tretí je len 1.", new PickItUpAction(), true),
    						    new Choice("74", "V jednej bájnej krajine existujú tri druhy bájnych bytostí, ktoré vysádzajú bájne vajcia.\n "
    						    		+ "Prvý druh vysádza vajcia len v stredu, druhý vysádza vajcia len v piatok a tretí druh vysádza vajcia každý pondelok a nedeľu. \n"
    						    		+ "Dnes je sobota a do konca mesiaca zostáva 26 dní. Ak prvý druh bytostí vysádza 2 vajcia za každý bájný deň, \n"
    						    		+ "druhý druh vysádza 3 vajcia za každý bájný deň a tretí druh vysádza len jedno vajce za každý bájný deň, \n"
    						    		+ "koľko bájnych vajec sa ešte vysadí do konca mesiaca? \n"
    						    		+ "Počet bytostí prvého druhu je 3, druhého 4, tretí je len 1.", new LeaveItAction(), false)
    						};
    					
    					
    				

    				    // Zoznam použitých možností, cize tie, ktore uz zatial pouzit nechceme znova
    				    private static final List<Integer> usedOptions = new ArrayList<>();

    				    /**
    				     * Metóda vráti náhodné možnosti otázok, pričom zaručuje, že každá možnosť bude použitá aspoň raz.
    				     * @return Pole náhodných možností otázok.
    				     */
    				    public static Choice[] getRandomOptions() {
    				        Random random = new Random();
    				        int randomNumber;
 /*
    				        // Ak boli všetky možnosti použité aspoň raz, resetujeme zoznam použitých možností
    				        if (usedOptions.size() == 176) {
    				            usedOptions.clear();
    				        }

    				        // Generujeme náhodnú možnosť, ktorá ešte nebola použitá
    				        do {
    				            randomNumber = random.nextInt(175) + 1; // Add 1 to ensure number is between 1 and 176
    				        } while (usedOptions.contains(randomNumber));

    				        // Pridáme vybranú možnosť do zoznamu použitých možností
    				        usedOptions.add(randomNumber);

    				        // Získame možnosti zodpovedajúce náhodne vygenerovanému číslu
    				        String optionsName = "options" + randomNumber;
    				        try {
    				            java.lang.reflect.Field field = Options.class.getDeclaredField(optionsName);
    				            Choice[] options = (Choice[]) field.get(null);
    				            return options;
    				        } catch (NoSuchFieldException | IllegalAccessException e) {
    				            e.printStackTrace();
    				        }
*/
    				        // Ak niečo zlyhá, vrátime predvolené možnosti
    				        //return new Choice[174];
    				        
    				        return options28;
    				    }
    				}




    
