# Plaanisepp

Plaanisepp on kaardipõhine töölauarakendus sündmusala, inventari ja elektrivajaduse planeerimiseks. See aitab ühel meeskonnal koostada arusaadava plaani, kus ruumiline paigutus, inventar, elektriseadmed, elektrikapid ja kaablid on omavahel seotud.

Rakendus on alguse saanud pannkoogihommiku planeerimise vajadusest, kuid seda arendatakse üldisemaks eri tüüpi sündmuste töövahendiks. Tegemist on aktiivselt arendatava prototüübiga, mille kasutusmugavust kontrollitakse päris ürituste plaanidel.

## Milleks seda kasutada saab?

- valida olemasolev aluskaart, laadida oma pilt või määrata ala Maa- ja Ruumiameti päriskaardilt;
- paigutada kaardile telke, elektrikappe, alajaotuskilpe, alasid, jooni, aedu, markereid, tekste ja muid objekte;
- määrata objektidele nimed, grupid, värvid, mõõdud, märkmed, nähtavuse ja lukustuse;
- planeerida telkide, alade ja joonte inventari ning näha koondkoguseid;
- arvutada aiaridade, aiakivide ja kaablijuppide vajadust;
- lisada seadmeid ning ühendada need konkreetsete elektrikappide ja väljunditega;
- vaadata väljundite, elektrikappide ja alajaotuskilpide koormust ning vaba võimsust;
- kujundada kaablite tegelikku trajektoori ja mõõta kaardil vahemaid;
- kasutada eraldi korraldaja- ja tehnikavaadet;
- eksportida plaan TXT- või PDF-aruandena ning kaardipildina.

## Kuidas alustada?

1. Paigalda Plaanisepp GitHubi [Releases](https://github.com/KaldaM/plaanisepp/releases) lehelt või käivita see arenduskeskkonnast.
2. Käivitamisel vali olemasolev plaan või loo uus. Uue plaani dialoogis määra nimi, vajaduse korral festival või sündmus ning aluskaart.
3. Vali tööriistaribalt objekti tüüp, vajuta **Lisa** ja klõpsa soovitud asukohal. Alade, joonte ja aiaridade puhul lisa punktid järjest.
4. Vali objekt kaardil või külgpaneeli jaotisest **Objektid**. Objekti andmeid saab muuta jaotises **Valitud objekt** või objekti paremklõpsumenüüst.
5. Korraldajavaates on kaardil ja külgpaneelil ainult sündmuse paigutuse jaoks vajalik info. Elektri ja kaablite haldamiseks lülita menüüst **Vaade → Tehnikavaade**.
6. Salvesta töö `.pplan` failina ja jaga vajaduse korral TXT- või PDF-eksporti.

Rakendusesisene **Abi → Alustamise juhend** annab sama töövoo kohta lühema samm-sammulise ülevaate.

## Olulisemad töövood

### Kaart ja objektid

Kaardil saab suumida, nihutada, vahetada tavakaardi ja ortofoto vahel ning kasutada kõrglahutusega georefereeritud aluskaarte. Objekte saab valida kaardilt või nimekirjast, otsida, kopeerida, kleepida, mitmikvalida, liigutada, pöörata, lukustada ja peita. `Ctrl + Z` ning `Ctrl + Alt + Z` võimaldavad plaanimuudatusi tagasi võtta ja uuesti teha.

### Inventar ja aiad

Inventari vaade koondab aiad, aiakivid, telgid, lauad, pingid, telgiraskused, kaablid ja muud kasutaja määratud inventariread. Koguseid saab muuta `− / +` nuppudega ning iga rea vajaduse saab avada objektide kaupa. Aiarida koosneb standardsetest füüsilistest lõikudest ja võib moodustada avatud, hargneva või suletud aiavõrgu.

### Elekter ja kaablid

Telk, ala või joon võib sisaldada seadmeid ja olla ühendatud konkreetse elektrikapi konkreetse väljundiga. Alajaotuskilp saab olla korraga tarbija ja järgmiste väljundite allikas. Elektri kokkuvõte näitab koormust, vaba võimsust ja ülekoormust; kaablite puhul saab märkida pikkusjupid ning kujundada tee vahepunkte.

### Korraldaja- ja tehnikavaade

Korraldajavaade käivitub esmakordsel kasutamisel vaikimisi ning peidab tehnilise elektriinfo. Tehnikavaade taastab elektrikapid, kaablid ja elektri külgpaneelid. Viimati kasutatud vaade säilib lokaalselt. Külgpaneeli jaotisi saab kasutaja järjestada, peita ja vaikejärjestusse taastada.

## Failivorming ja ühilduvus

`.pplan` on üks kaasaskantav ZIP-pakett, mis sisaldab plaani andmeid ning kasutaja valitud kaardipilte. Praegune sisemine vorminguversioon on **25**. Pakett säilitab muu hulgas objektid, grupid, nähtavuse, inventari, elektriühendused, kaablitrajektoorid, aluskaardid ja checklisti.

Rakendus avab ka vanemad versioonita ning varasemad `.pplan` vormingud. Vana faili ei muudeta avamisel; järgmine salvestamine viib selle praegusesse vormingusse. Rakendus ei ava endast uuema vorminguga faili enne, kui programm on uuendatud.

## Paigaldamine tavakasutajale

GitHub Release sisaldab Windowsi EXE-paigaldajat, Fedora RPM-i, Linuxi iseseisva rakenduse arhiivi ja `SHA256SUMS` kontrollsummasid. Pakendid sisaldavad vajalikku Java runtime'i, mistõttu tavakasutaja ei pea paigaldama Javat, Gradle'it ega lähtekoodi.

### Windows

Windowsi paigaldaja seob `.pplan` failid Plaaniseppaga, lisab rakenduse Start-menüüsse ja paigaldab rakenduse koos Java runtime'iga.

### Fedora Linux

Fedora RPM-i saab paigaldada näiteks nii:

```bash
sudo dnf install ./plaanisepp-<versioon>-1.x86_64.rpm
```

Paigaldatud rakenduse saab eemaldada käsuga `sudo dnf remove plaanisepp`. `.pplan` faili saab avada failihalduris topeltklõpsuga.

## Arendajale

Projekt koosneb kahest Gradle'i moodulist:

- `planner-core` sisaldab domeenimudelit, geomeetriat, inventari, elektriarvutusi ja `.pplan` salvestusloogikat;
- `planner-gui` sisaldab JavaFX-i kasutajaliidest, kaardivaadet, dialooge ja eksporti.

Tehnoloogiad:

- Java 25;
- JavaFX 26.0.2;
- Gradle Wrapper;
- Apache PDFBox 2.0.31;
- JUnit 5.

Käivita rakendus Linuxis või macOS-is:

```bash
./gradlew :planner-gui:run
```

Windowsis:

```powershell
.\gradlew.bat :planner-gui:run
```

Käivita testid:

```bash
./gradlew test
```

Puhas CI-kontroll kasutab `./gradlew clean test --no-daemon` ning GitHub Actions käivitub iga push'i ja pull request'i korral.

## Dokumentatsioon ja edasine areng

- [Projekti ülevaade](docs/PROJEKTI_ULEVAADE.md) kirjeldab tausta, arhitektuuri, failivormingut ja seniseid etappe.
- [Arengukava](docs/ARENDUSPLAAN.md) on projekti aktiivne tööjärjekord ning eristab tehtud, pooleliolevaid ja madala prioriteediga ideid.

Lähim praktiline eesmärk on stabiliseerida korraldajate põhiteekond päriskasutuseks: plaani loomine, objektide paigutamine ja muutmine, inventar, nähtavus, salvestamine ning PDF-i jagamine. Suuremad tulevikuideed, nagu pilvehoidla, kasutajakontod ja festivalideülene inventari optimeerimine, jäävad sellest etapist väljapoole.

## Avaldamine

Uus GitHub Release luuakse versiooniga samanimelise tag'i push'imisel. Näiteks `v0.7.1` puhul peab Gradle'i versioon olema `0.7.1` ja tag `v0.7.1`. Release-workflow kontrollib versiooni, ehitab platvormipaketid ning lisab kontrollsummad.
