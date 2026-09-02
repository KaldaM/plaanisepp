# Rakenduse edasine arendusplaan

- Koostatud: 20. august 2026
- Lähtepunkt: commit `e53fb3a` (`Extract marker icon factory from application`)

## Eesmärk

Rakendus ei ole enam ainult pannkoogihommiku töövahend. Edasine arendus peab toetama eri organisatsioonide ja eri tüüpi ürituste alaplaanide koostamist, säilitades olemasolevate `.pplan` failide avatavuse ning elektri- ja kaabliplaneerimise tugevused.

## Tööjärjekord ja hetkeseis

1. **Tehtud:** alajaotuskilbid, seadmete vaiketoide ja seadmepõhised toitevalikud. Elektrikilpide ja alajaotuskilpide värvi ning kaardil kuvatavat suurust saab muuta; alajaotuskilbil on elektrikapist eristuv oranž vaikevärv.
2. **Tehtud:** interaktiivne elektri kokkuvõte ja väljundite koormusribad.
3. **Tehtud:** külgpaneeli ja objektide kontekstimenüüd, üksikobjektide nähtavus ning külgpaneeli jaotiste järjestamine.
4. **Tehtud:** objektide kiirotsing kahekordse Shift-klahviga.
5. **Tehtud:** suumiliugur, 100% taastamine ja `Alt + hiirerull`.
6. **Tehtud:** salvestamise ja plaanihalduse klahvikombinatsioonid.
7. **Tehtud:** hiljutiste plaanide ja uue plaani loomise avavaade.
8. **Tehtud:** plaani muudatuste tagasivõtmine ja uuestitegemine.
9. **Tehtud:** plaanipõhine checklist ja soovituste kontrollnimekiri.
10. **Tehtud:** automaatsed Linuxi ja Windowsi paketid, GitHub Releases, versioonikontroll ning kontrollsummaga allalaadimine.
11. **Pooleli:** objektieelseadistuste alus ja Red Bull DJ Truck on tehtud; järgmised PA-seadmed ning kinnitatud tegelikud elektrivajadused on lisamata.
12. **Pooleli:** sirged ja murtud ühendatud aiavõrgud, aiaringi generaator, Inventari jaotise esimene etapp, automaatselt arvutatavad aiakivid koos aiakogumiku `− / +` paranduste ja aiata lisakogusega ning telkide, alade ja kujuobjektide vabalt määratav inventar on tehtud. Lisatud on ka objektita lisainventar koos `− / +` juhtimise, märkmete, salvestamise ja raportiga. Lisamata on inventariliikide üldised automaatse ja käsitsi koguseparanduse koondread. Üksiklõikude täielik identiteet on samuti lisamata. Eraldi kaaregeneraator on madala prioriteediga, sest olemasolevat aiarida saab ühenduspunkte liigutades hõlpsalt kaareks vormida.
13. **Tehtud:** rippvalikud, kaardilt vooluallika valimine, automaatne rakendamine ja objektide kiirklahvid.
14. **Tehtud:** kogu kaardi geomeetriat kaitsev paigutuslukustus.
15. **Tegemata:** kõrglahutusega ja täpselt joondatud aluskaartide hankimise töövoog.
16. **Pooleli:** tehnikakihita korraldajavaate esimene etapp on tehtud. Rakendus käivitub esmakordsel kasutamisel korraldajavaates ning jätab lokaalselt meelde viimati kasutatud korraldaja-/tehnikuvaate. PDF-eksport kasutab aktiivset kaardivaadet; korraldajavaates jäetakse välja voolu- ja kaabliinfo ning kilpidest loodud tekstobjektid. Valitav objektide legend sisaldab kõiki kaardil nähtavaid objekte samas grupijaotuses nagu külgpaneel, koos joondatud värvinäidise, tüübi, nime ja mõõdu-/koguseinfoga. Aiavõrgud kuvatakse raporti grupiloendis ühe tervikobjektina ning voolu kokkuvõttes on kilbi plokid ja koormustaseme värvilised ribad. Kuvaprofiilid ja kommentaarid on lisamata.
17. **Tegemata:** terviklik visuaalse keele uuendus.
18. **Peaaegu tehtud:** interaktiivne pööramine, mitmikvalik, valikukast ja põhilised ühistoimingud töötavad; ühise grupi ja nimesildi nähtavuse hulgi muutmine on teostatud ning viimased mitmikvaliku mugavused ootavad käsitsi kontrolli.
19. **Jätkuv töö:** `PlaaniseppApp` refaktoreerimine väikeste funktsioonipõhiste sammudena.

## Järgmise töökorra märkmed — 31. august 2026

- **Tehtud:** aiata aiakivid on ühtlustatud muu objektita inventariga. Need lisatakse „Lisa inventar” kaudu ja koondatakse „Aiakivid” harusse; sama lisamisvoog toetab objektita aedu, mis liidetakse „Aiad” koondkogusesse.
- **Tehtud:** kaabliinventari rea ning kaardil oleva kaabli paremklõpsumenüüst saab muuta konkreetse kaabli märkust ja kaablitükke. Kaablitükid on interaktiivne `− / +` loend: 2, 5, 10 ja 20 m read on alati valitavad, muu pikkuse saab lisada käsitsi ning nullkogusega tükke ei kuvata kaardil ega koondis. Kaardisilt kasutab kompaktset vormingut, näiteks `2x5 + 10`.
- **Tehtud:** üleliigne „Paigutus lukus” nupp on tööriistaribalt eemaldatud. Paigutuslukustus säilib menüüs ja kiirklahvina ning aktiivne olek kuvatakse tööriista olekureal.
- **Tehtud:** mõõdulint ühendab järjest kaardil märgitud punktid üheks murdjooneks. Iga lõik näitab oma pikkust, viimase punkti juures kuvatakse kogu lindi pikkus ning hiire liikumise ajal on näha järgmise lõigu ja uue kogupikkuse eelvaade. `Enter`, viimase punkti topeltklõps või `Escape` lõpetab aktiivse lindi ja lülitab mõõtmistööriista välja. Valmis lindi paremklõpsumenüüst saab selle trajektoori punktide lohistamiseks uuesti avada või lindi eemaldada; kõik mõõdud saab endiselt korraga puhastada.
- **Tehtud:** kaabli trajektoori muutmise nupud on tööriistaribalt eemaldatud. Trajektoori muutmine, punktide lisamine ja režiimi lõpetamine on kättesaadavad kaabli paremklõpsumenüü ning `Escape`-klahvi kaudu.

Rakenduse nimeks valiti 20. augustil 2026 **Plaanisepp**. Nimi kirjeldab plaanide meistrit ja seostub ka 1927. aastal talletatud Lõuna-Eesti nimekujuga „Plaani sepp”.

## Teostatud 25. augustil 2026

Tänase töö täielik kokkuvõte põhineb päeva commit'idel `e32eac2` kuni `8d8384e`.

- Külgpaneeli põhijaotiste järjestust saab paremklõpsumenüüst muuta, vaikejärjestuse taastada ja kasutaja eelistustes säilitada.
- Rakendusele lisati laiendatav menüüriba. Uue plaani loomine, avamine, salvestamine, „Salvesta kui”, ekspordid ja plaani andmed viidi tööriistaribalt „Fail” menüüsse; senised klahvikombinatsioonid säilisid.
- Menüüriba „Redigeeri” menüü koondab undo/redo ning valitud objekti muutmise, kopeerimise, kleepimise, lukustamise, peitmise ja kustutamise. Menüü tegevuste tekst ja lubatud olek arvestavad aktiivset objekti, lõikelauda, ajalugu ning paigutuslukku.
- Menüüriba „Vaade” menüü võimaldab taastada 100% suumi, lülitada paigutuslukku ning muuta põhiliste kaardikihtide nähtavust. Menüü valikud püsivad sünkroonis külgpaneeli ja tööriistariba samade juhtelementidega.
- Menüüriba „Abi” menüüs on rakendusesisene klahvikombinatsioonide ülevaade ning Plaanisepa versiooni ja kasutatavat Java versiooni näitav teabeaken.
- Kaabli trajektoori saab kaabli paremklõpsumenüüst muutma hakata, punkte lisada ja lohistada ning muutmise selgelt lõpetada.
- Objektide kiirotsing avaneb kahe kiire `Shift`-vajutusega, saab kohe klaviatuurifookuse ning toetab valimist, tsentreerimist, esiletõstmist ja korrektset katkestamist.
- Suumimiseks lisati liugur ja `Alt + hiirerull`; hiirega suumimisel jääb kursori all olev kaardipunkt paigale.
- Lisati salvestamise kiirklahvid `Ctrl + S` ja `Ctrl + Shift + S` ning plaanihalduse kiirklahvid `Ctrl + N` ja `Ctrl + O`.
- Lisati hiljutiste plaanide püsiloend, rakenduse käivitusdialoog ning uue plaani loomise dialoog koos plaaniandmete ja kaardi valimisega.
- Lisati piiratud undo/redo ajalugu ja plaani hetktõmmised. `Ctrl + Z` ning `Ctrl + Alt + Z` taastavad plaanimuudatusi, lohistamine moodustab ühe tervikliku ajalookirje ning tööriistariba nupud näitavad keelatud olekuga, kui tagasi- või edasiliikumine pole võimalik.
- Arengukava täiendati visuaalse uuenduse, checklist'ide, aedade tööriista, versioonide allalaadimise, aluskaartide, korraldajavaate ja kommenteerimise ideedega.
- Objekti grupi määramine muudeti redigeeritavaks rippmenüüks: valida saab olemasoleva grupi või sisestada uue grupinime.
- Objekti paremklõpsu „Muuda” ja `Alt + Enter` avavad eraldi muutmisdialoogi, mis kasutab sama täielikku andmevaadet ja rakendusloogikat nagu külgpaneel.
- Lisati valitud objekti kiirklahvid `Ctrl + L` lukustamiseks, `Ctrl + H` peitmiseks ja `Delete` kustutamiseks.
- Objekti dubleerimise nupp asendati kopeerimise ja kleepimisega: töötavad `Ctrl + C`, `Ctrl + V` ning kaardi ja objekti paremklõpsumenüüd.
- Objektitüüpide lisamiseks lisati kiirklahvid `Ctrl + Shift + 1…8`.
- Läbipaistvuse muutmine annab lohistamise ajal kaardil vahetu eelvaate.
- Objekti lisamise dialoogi lisati „Näita nime” valik, selle eelmine väärtus jäetakse meelde ning valitud objekti nimi kuvatakse ka üldiselt peidetud nimesiltide korral.
- Lisati kogu kaardi geomeetriat kaitsev paigutuslukk, mis jätab andmete muutmise võimalikuks, kuid takistab objektide, kaablite, siltide ja punktide kogemata liigutamist.
- Objekti omaduste eraldi „Rakenda muudatused” nupp eemaldati; sobivad muudatused rakenduvad nüüd automaatselt.
- Vooluallika saab valida otse kaardilt. Väljundi valik näitab kõiki valitud kapi väljundeid koos nende koormuse ja vaba võimsusega, mitte ainult varem sobitatud ühendustüüpi.
- Vooluühenduste haldus muudeti ühendusepõhiseks: külgpaneelil saab valida põhi- või alternatiivühenduse ning muuta selle kappi, väljundit, kaablitükke, märkmeid ja silti.
- Alternatiivühenduse saab luua, eemaldada või põhiühenduseks määrata otse külgpaneelilt. Seadme toide rakendub valimisel kohe.
- Seadmete lisamine ja muutmine viidi dialoogi, kus saab korraga määrata nime, võimsuse ning põhi- või alternatiivtoite. Muutmine ja eemaldamine asuvad seadme paremklõpsumenüüs.
- Seadmete loend näitab tegelikku toiteallikat, väljundi vaba võimsust, koormusprotsenti ja värvilist koormusriba. Vooluallika rippmenüü näitab sama kapi kogukoormuse kohta.
- Kaablisildi nähtavust saab määrata iga ühenduse kohta eraldi ja valitud kaabli silt jääb nähtavaks ka peidetud siltide korral. Kaabli vähem kasutatavad andmed asuvad vaikimisi suletud „Kaabli lisainfo” jaotises.
- `.pplan` failivormingu versioon 5 salvestab ühendusepõhise kaablisildi nähtavuse ning vanemad plaanid jäävad avatavaks.
- Alajaotuskilbi koormus jõuab ülesvoolu kapi konkreetse väljundi ja kapi kogukoormuse arvestusse.
- Plaanipõhine kasutaja checklist võimaldab lisada vabatekstilisi ülesandeid, märkida neid tehtuks, ümber nimetada, järjestada ja kustutada. Kirjed osalevad undo/redo ajaloos ning säilivad `.pplan` versioon 6 failis; vanemate plaanide checklist on vaikimisi tühi.
- Checklist'i soovituste loendis on tavapärased ürituse osad, mille saab märkida tehtuks või ebaoluliseks. Soovituste olek säilib `.pplan` versioon 7 failis ja osaleb undo/redo ajaloos.

## Teostatud 26. augustil 2026

- Lisati „Vaade” menüüst lülitatav korraldajavaade. See peidab kaablid, elektrikapid ja alajaotuskilbid kaardilt, elektriandmed külgpaneelilt ning tehnilised objektitüübid lisamisvalikutest, muutmata plaaniandmeid või varasemaid kihivalikuid.
- Lisati aiaridade esimene etapp: kahe kaardipunktiga saab luua sirge rea, mis ümardatakse lähima täisarvu 3,5 m aialõikudeni. Kaart, külgpaneeli kokkuvõte ning TXT-raport kuvavad vajalike aedade arvu. `.pplan` versioon 10 säilitab read jagatud ühenduspunktide võrguna, mis võib olla avatud, hargnev või suletud ilma kunstliku esimese reata.

## Teostatud 27. augustil 2026

- Kõigile sobivatele objektitüüpidele lisati loomulik lohistatav pööramispunkt, püsiv pöördenurk ja kohene eelvaade. Telgid, ristkülikud, tekstid, jooned, alad ning ühendatud aiavõrgud pöörduvad oma õige keskpunkti ümber.
- Ühtlustati objektide läbipaistvuse muutmine ning valitud, väga läbipaistvate objektide leidmiseks lisati tagasihoidlik valiku esiletõste.
- Joone-, ala- ja aiapunktide lohistamisel uuenevad kuju, lõikude keskpunktid, pööramisgeomeetria ja valiku esiletõste otse liigutamise ajal.
- Lisati `Ctrl`-klõpsuga mitmikvalik kaardil ja objektide külgpaneelil ning `Ctrl`-lohistamisega valikukast.
- Mitmikvalikuga töötavad kopeerimine ja kleepimine suhtelist paigutust säilitades, kustutamine koos undo võimalusega, peitmine ja kuvamine, lukustamine ja lukust vabastamine, liigutamine ning ühise keskpunkti ümber pööramine.
- Lukustatud objekti sisaldava valiku geomeetriamuudatus blokeeritakse tervikuna ning suure valiku pööramispunkt hoitakse nähtava kaardiala lähedal.
- Külgpaneeli kontekstimenüü säilitab mitmikvaliku ja rakendab ühistoimingu kogu valikule.
- Valiku nähtavust parandati külgpaneeli tugevama valikuoleku, kaardi ühise katkendliku piirjoone ja aktiivsete objektide arvu näiduga.
- Külgpaneelile lisati `Ctrl + Shift + M1` nähtavate objektiridade vahemikuvalik ning `Ctrl + M1` grupipäisel valib grupi nähtavad ja otsingule vastavad objektid. Need viimased mugavused ootavad veel käsitsi kontrolli.
- Valmistati ette ja avaldati versioon `v0.2.0`, mis sisaldab automaatselt loodud Linuxi ja Windowsi paigalduspakette ning SHA-256 kontrollsummasid.

## 1. Rakenduse nimi

### Nimeotsus

**Plaanisepp** sobib pannkoogihommikust laiemale sündmuste planeerimisele ning katab nii kaardi, objektide, elektri kui ka kaablite tervikuks vormimise. Nimi kasutab ainult ASCII-tähti, on hõlpsasti käänatav ja sobib rakenduse, paigaldaja ning võimaliku tulevase veebiteenuse nimeks.

Kasutajale nähtav nimi, pakendinimed ja Java paketid muudetakse Plaanisepa järgi. Tagasiühilduvuse tõttu säilivad eraldi ajalooliste tehniliste identifikaatoritena `.pplan` sisemised vormingutunnused, MIME-tüüp, eelistuste asukoht `/ee/matteus/pannukas/gui` ja Windowsi uuenduse UUID.

### Ümbernimetamise ulatus

Nime muutmisel tuleb eraldi kontrollida:

- rakenduse akna pealkiri ja kasutajale kuvatavad tekstid;
- README ja projekti dokumentatsioon;
- Windowsi rakendusepilt ning EXE-paigaldaja;
- Linuxi rakendusepilt, RPM, töölauakirje ja paketinimi;
- rakenduse ning `.pplan` failitüübi ikoonid;
- Java `Preferences` võtmete säilimine;
- paigaldatud vana versiooni uuendamine ja eemaldamine.

`.pplan` laiend on piisavalt üldine ja selle muutmiseks praegu vajadust ei ole. Java paketid ja repository nimi ei pea kasutajale nähtava nimega samas commit'is muutuma.

## 2. Alajaotuskilbid ja seadmepõhine voolujaotus

See on sügis-eelse väljalaske kriitiline funktsioon ning tuleb teostada enne elektri külgpaneeli visuaalset ümberkujundamist.

### Kasutusjuht

Ühe telgi, ala või joone seadmed ei pruugi kõik saada voolu samast kohast. Objektil on vaiketoide, mida kasutavad automaatselt kõik seadmed, millele ei ole määratud erandit. Üksikule seadmele saab valida teise elektrikapi või alajaotuskilbi väljundi.

Näide:

```text
Kohvitelk
├── vaiketoide: alajaotuskilp A / väljund 1
├── kohvimasin       1800 W   kasutab vaiketoidet
├── veekeetja        2000 W   kasutab vaiketoidet
└── külmik            500 W   püsivoolukilp B / väljund 3
```

Alajaotuskilp on korraga tarbija ja vooluallikas: sellel on üks ülesvoolu toide ning mitu allavoolu väljundit. Koormus peab liikuma läbi kogu ahela üles põhikilbini.

### Soovituslik domeenimudel

Füüsiline toide ja seadme elektriline määrang tuleb käsitleda eraldi:

- igal `Equipment` elemendil on püsiv ID;
- objektil on vaikimisi toite viide;
- seade kas pärib objekti vaiketoite või viitab teisele toitele;
- üks füüsiline toide ühendab ühe allika väljundi ühe tarbiva objekti või alajaotuskilbiga ning sisaldab kaabli tüüpi, trajektoori, märkmeid ja silti;
- mitu sama objekti seadet võivad kasutada sama füüsilist toidet ilma kattuvate kaablijoonte dubleerimiseta;
- alajaotuskilbil on ülesvoolu toide ja oma väljundid;
- koormusarvutus summeerib seadmed nende tegeliku toite järgi ning arvutab alajaotuskilbi sisendkoormuse rekursiivselt.

Praegune `PowerConnection` seob ühe allika terve tarbiva objektiga, sisaldab samal ajal ka kaabliandmeid ning sellel on edasise mudeli eeltingimusena püsiv ID. Enne muutmist tuleb otsustada, kas see areneb füüsilise toite mudeliks või asendatakse selgemalt nimetatud mudeliga. Eelistatud on eraldi füüsilise toite mõiste, millele seadmed viitavad.

### Tagasiühilduvus

- Versioonita ja versioon 1 failid peavad edasi avanema.
- Praeguse versioon 2 faili üks ühendus teisendatakse laadimisel objekti vaiketoiteks ning kõik seadmed pärivad selle.
- Vana faili avamine ei tohi faili automaatselt ümber kirjutada.
- Soovituslik on kirjutada uus mudel `.pplan` versioonina 3, sest vanem rakendus ei oskaks seadmepõhiseid ühendusi säilitada ja võiks need vaikides kaotada.
- Versioon 3 võib säilitada sama ZIP-paketistruktuuri, lisades plaaniandmetesse seadmete ID-d, toited ja alajaotuskilbid.

### Ohutusreeglid

- Alajaotuskilpi ei saa ühendada iseendaga.
- Toiteahelas ei tohi tekkida tsüklit.
- Puuduv või kustutatud allikas, väljund, toide või seade peab andma kontrollitava tulemuse, mitte katkestama plaani avamist.
- Väljundi koormus peab arvestama ainult selle kaudu tegelikult toidetavaid seadmeid.
- Üks seade ei tohi korraga kasutada mitut toidet, kuni mitmefaasilise või redundantse toite jaoks pole eraldi nõuet.

### Teostusetapid

1. Kirjeldada uued domeenitüübid ja migratsioonireeglid automaattestidega.
2. Lisada seadmetele püsivad ID-d ning vaiketoite ja erandi lahendamine.
3. Lisada alajaotuskilp ja tsüklivaba üles-/allavoolu koormusarvutus. Domeenimudel, rekursiivne arvutus ja `.pplan` v3 salvestamine on teostatud. Alajaotuskilbi saab kasutajaliidesest kaardile lisada, selle väljundeid hallata ja olemasolevate vooluvalikutega ülesvoolu ühendada. Kasutajaliides eristab puuduvat allikat või tarbijat, iseühendust, tsüklit ja sobiva väljundi puudumist.
4. Laiendada `.pplan` lugemist ja kirjutamist koos versioon 1/2 migratsioonitestidega.
5. Lisada kasutajaliideses objekti vaiketoide ning seadmereal valitav erand. Teostatud: seadmete loend näitab kasutatavat toidet, valitud seadme saab suunata objekti alternatiivühendusele ning alternatiivühendusi saab lisada ja eemaldada.
6. Uuendada kaablite joonistamine, kokkuvõtted, TXT/PDF-eksport ja külgpaneel. Teostatud: kaart, külgpaneeli kokkuvõte ning TXT/PDF-raport arvestavad kõiki alternatiivühendusi ja tähistavad need seadme erandina.

Teise etapi eeldustest on teostatud seadme ja vooluühenduse püsivad ID-d. `Equipment` saab domeenimudelis kasutada objekti vaiketoidet või viidata erandina kindlale `PowerConnection` ID-le. Ühel tarbival objektil võib olla üks vaiketoide ja mitu alternatiivset füüsilist ühendust. `EventPlan` lubab määrata ainult samale tarbivale objektile kuuluva ühenduse, jagab seadmete koormuse tegelike ühenduste vahel ning lähtestab eemaldatud või vigased ühenduseviited vaiketoitele. `.pplan` versioon 3 salvestab mitme ühenduse rollid ja seadmepõhised valikud; versioon 1 ja 2 plaanid migreeritakse laadimisel senise nähtava jaotusega vaiketoitele.

### Vastuvõtukriteeriumid

- Kõik seadmed kasutavad vaikimisi objekti vaiketoidet.
- Üksiku seadme saab suunata teise kilbi konkreetsele väljundile ja hiljem tagasi vaiketoitele.
- Sama objekti eri seadmed jagunevad vähemalt kahe allika vahel õigete võimsustega.
- Alajaotuskilbi sisendkoormus võrdub selle väljundite kaudu toidetava kogukoormusega.
- Põhikilbi koormus sisaldab temast sõltuvate alajaotuskilpide koormust.
- Tsüklilise kilbiahela loomine blokeeritakse selge eestikeelse veateatega.
- Vana v1 ja v2 plaan avaneb sama nähtava elektrijaotusega nagu enne.
- Uus plaan salvestub, avaneb uuesti ning säilitab seadmete erandid, kaablid ja kilbiahela.

## 3. Elektri külgpaneel ja koormusribad

### Kasutusloogika

Praegune tekstikujuline voolukokkuvõte asendatakse või täiendatakse hierarhilise interaktiivse vaatega:

```text
Elektrikapp
└── Pistikupesa 1                         2300 / 3500 W
    [██████████████████░░░░░░░░░░░░]     66%
    ├── Kohvitelk                         1800 W
    └── Valgustus                          500 W
```

Iga elektrikapi juures kuvatakse selle väljundid. Iga väljundi juures kuvatakse kasutatud ja lubatud võimsus, protsent ning värviline koormusriba. Väljundi all kuvatakse sellega ühendatud tarbijad.

Esialgsed värvipiirid:

- alla 70%: roheline;
- 70–89%: kollane;
- 90–100%: oranž;
- üle 100%: punane.

Piirid tuleb enne teostamist kinnitada ning hoida ühes taaskasutatavas arvutus- või kujunduskomponendis.

### Interaktsioonid

- Elektrikapi või tarbija real klõpsamine valib vastava objekti.
- Enter või topeltklõps viib objekti kaardil nähtavale.
- Valitud objekt tõstetakse kaardil selgelt esile.
- Ülekoormatud väljund peab olema eristatav ka ainult teksti ja ikooni järgi, mitte üksnes värviga.
- Olemasolevad `Vool`, `Kaablid` ja `Grupid` filtrid peavad säilitama oma tähenduse või saama selge asenduse.

### Vastuvõtukriteeriumid

- Koormus vastab `PowerSummaryService` arvutustele.
- Nullmahuga ja ühendusteta väljundid ei põhjusta jagamist nulliga ega katkist riba.
- Täpselt 100% ei kuvata ülekoormusena; üle 100% kuvatakse punaselt.
- Tarbija valimine töötab telgi, ala ja joone puhul.
- Vana tekstiraport, PDF-eksport ja `.pplan` vorming ei muutu.

Elektri külgpaneeli põhivaade on teostatud: iga kapi ja väljundi real kuvatakse protsent ning täituvuse järgi roheline, kollane, oranž või punane riba. Kapi, väljundi või tarbija real klõpsamine valib vastava kaardiobjekti ning topeltklõps viib selle juurde kaardil. Kapi ja väljundi alamread saab eraldi kokku pakkida ning nende avatud või suletud olek säilib kokkuvõtte värskendamisel.

Objektide külgpaneel kasutab samuti kokkupakitavat hierarhiat: objektid on rühmitatud gruppide alla ning grupipäises saab sama linnukesega muuta kogu grupi nähtavust kaardil. Eraldi grupifiltrite jaotist enam ei ole. Objekti valimisel märgitakse mõlemas loendis objekt või kokkupakitud harus selle lähim nähtav vanem. Kokkupakitud paneele ega harusid automaatselt ei avata ning loendit keritakse ainult siis, kui märgitav rida pole juba nähtaval.

## 4. Objekti kiirotsing

### Kasutusloogika

- Kaks kiiret Shift-klahvi vajutust avavad otsingu, sarnaselt IntelliJ IDEA otsingule.
- Otsing kasutab olemasoleva objektinimekirja nime, tüübi ja grupi filtreerimisloogikat.
- Nooleklahvid muudavad valikut, Enter valib objekti ja Escape sulgeb otsingu.
- Enter tsentreerib kaardi objektile ning kuvab selle ümber lühikese pulseeriva kontrastse raami.
- Otsingu avamisel jäetakse meelde objektipaneeli eelnev avatud või minimeeritud olek ning pärast otsingu lõpetamist taastatakse see.

### Tehnilised piirid

- Kahekordse Shift-klahvi tuvastus peab töötama kogu rakenduse ulatuses.
- Tavaline Shift-klahvi kasutamine tekstiväljades ei tohi otsingut kogemata avada.
- Korduv Enter või kiiresti järjest tehtud otsing ei tohi jätta vanu esiletõste animatsioone kaardile.
- Peidetud objekti otsingutulemus peab näitama, et objekt või selle kiht on peidetud; nähtavust ei tohi vaikimisi püsivalt muuta.

### Vastuvõtukriteeriumid

- Otsing leiab objekti nime, tüübi ja grupi järgi.
- Enter viib nähtava objekti juurde ja esiletõste kaob automaatselt.
- Escape ei muuda valitud objekti.
- Objektipaneeli eelnev olek taastub.

Objekti kiirotsing on teostatud olemasoleva objektide nimekirja põhjal. Kaks kiiret `Shift`-vajutust avavad otsingu, nooleklahvid liiguvad tulemuste vahel, `Enter` valib ja tsentreerib objekti ning kuvab pulseeriva kontrastse rõnga. `Escape` taastab varasema otsinguteksti ja paneelioleku; tekstiväljas kasutatud Shift kiirotsingut ei käivita.

## 5. Suumi kasutuskogemus

### Kasutusloogika

- Tööriistariba `+` ja `-` nupud asendatakse suumiliuguriga.
- Liuguri kõrval säilib täpne protsendinäit ja kiire 100% taastamise võimalus.
- `Alt + hiirerull üles` suurendab ning `Alt + hiirerull alla` vähendab suumi.
- Tavaline hiirerull jätkab kaardiala kerimist.

Rakenduse praegune suumivahemik 25–400% säilib. Liugur peab kasutama sama `setZoom` loogikat, et suumitud kaardi servad jääksid ligipääsetavaks.

### Vastuvõtukriteeriumid

- Liugur, protsendinäit, 100% taastamine ja `Alt + hiirerull` püsivad omavahel sünkroonis.
- Suum ei lähe alla 25% ega üle 400%.
- Tavaline kerimine ja kaardi lohistamine säilitavad praeguse käitumise.
- Windowsi ja Linuxi klaviatuuri- ning hiirekäitumist kontrollitakse eraldi.

Suumiliugur, protsendinäiduga 100% taastamise nupp ja `Alt + hiirerull` on teostatud olemasoleva `setZoom` loogika põhjal. Hiirerulliga suumimisel jääb kursori all olev kaardipunkt kursori alla; tavaline hiirerull jääb kaardiala kerimiseks.

## 6. Käivitusekraan ja uue plaani loomine

### Kasutusloogika

Rakenduse tavalisel käivitamisel kuvatakse avavaade, kus saab avada hiljuti kasutatud plaane, valida kettalt muu `.pplan` faili või alustada uut plaani. Käsureaargumendina või failil topeltklõpsates avatud plaan peab jätkuvalt avanema otse, ilma avavaate vaheetapita.

„Uus plaan” avab lühikese loomise vaate, kus saab kohe:

- sisestada plaani nime ja muud plaaniandmed;
- valida kasutatava kaardipildi;
- määrata vajaduse korral mõõtkava;
- kinnitada plaani loomise ja liikuda redaktorisse.

Ükski väli ei pea olema kohustuslik. Puuduva nime korral kasutatakse selget vaikenime, näiteks „Uus plaan”. Valimata kaardi korral luuakse tühi kaardiala ning mõõtkava ja muud tehnilised väärtused kasutavad rakenduse olemasolevaid vaikeväärtusi. Hiljutiste plaanide loendis puuduv või teisaldatud fail ei tohi käivitamist katkestada ning selle saab loendist eemaldada.

### Vastuvõtukriteeriumid

- Hiljutine olemasolev plaan avaneb ühe valikuga.
- Kettalt saab avada plaani, mida hiljutiste loendis pole.
- Uue plaani saab luua nii täielikult täidetud andmetega kui ka ainult vaikeväärtustega.
- Kaardipilt lisatakse uue plaani loomisel sama turvalise laadimisloogikaga nagu praeguses plaaniandmete dialoogis.
- Faili topeltklõps ja `.pplan` käsureaargument avavad plaani otse.
- Versioonita ning versioon 1–3 plaanide avamine jääb muutmata.

Hiljutiste plaanide püsiloend ja käivitusdialoog on teostatud. Edukalt avatud või salvestatud fail tõstetakse kuni kümmet kirjet sisaldava loendi algusesse, duplikaadid eemaldatakse ning enam mitte olemasolevad failid puhastatakse. Tavakäivitusel saab avada hiljutise või muu plaani või alustada uut; failiargumendiga käivitus avaneb jätkuvalt otse. Uue plaani loomisel saab kohe määrata nime, mõõtkava, sildisuurused ja kaardi või valida kaardita plaani. Tühjad nimi ja mõõtkava asendatakse vaikeväärtustega.

## 7. Külgpaneeli kohandamine ja kontekstimenüüd

### Külgpaneeli järjestus

Kasutaja saab muuta külgpaneeli põhijaotiste järjestust. Näiteks saab tõsta „Voolu kokkuvõtte” või „Kaardi kihid” enda töövoo järgi üles- või allapoole. Valitud järjestus säilitatakse rakenduse eelistustes, mitte `.pplan` failis, sest see on kasutaja töökeskkonna, mitte konkreetse plaani omadus. Järjestuse taastamiseks peab olema vaikejärjestuse taastamise võimalus.

Külgpaneeli põhijaotiste järjestamine on teostatud jaotise paremklõpsumenüü kaudu. Jaotist saab liigutada üles või alla, vaikejärjestuse taastada ning valitud järjestus säilib rakenduse eelistustes.

### Kaardi kontekstimenüü

Kaardi tühjal kohal tehtud paremklõps avab menüü „Lisa”, mille alammenüüst saab valida lisatava objektitüübi. Valitud objekt luuakse kohe paremklõpsu asukohta. Lisamine peab kasutama samu vaikeväärtusi, valideerimist ja objektiandmete dialoogi nagu olemasolev tööriistariba kaudu lisamine.

Kaardi tühja ala „Lisa” menüü on teostatud. Punktobjekt luuakse pärast andmete kinnitamist kohe paremklõpsu asukohta; joone ja ala puhul saab sellest esimene punkt ning ülejäänud kuju märkimine jätkub olemasoleva töövooga.

Kaardil oleva objekti paremklõps avab objektimenüü järgmiste põhitegevustega:

- „Muuda” valib objekti ja avab külgpaneelil jaotise „Valitud objekt”;
- „Peida” või „Kuva” muudab ainult selle objekti nähtavust;
- „Kustuta” kasutab sama kinnitust ja seoste puhastamist nagu olemasolev kustutamistegevus.

Objektil tehtud paremklõps ei tohi samal ajal avada kaardi tühja ala „Lisa” menüüd ega alustada objekti lohistamist.

Kaardiobjekti ja objektide nimekirja kontekstimenüü „Muuda” ning „Kustuta” tegevused on teostatud. „Muuda” avab valitud objekti jaotise ning „Kustuta” kasutab olemasolevat lukustuse, kinnituse ja seoste puhastamise töövoogu.

„Muuda” avab objekti andmed eraldi dialoogis ning sama tegevuse klahvikombinatsioon on `Alt + Enter`. Dialoog kasutab külgpaneeli „Valitud objekt” jaotisega sama andmevaadet ja rakendusloogikat, mistõttu nime, grupi, visuaalsete omaduste, elektriühenduste ning seadmete valideerimine ei saa kahes vaates lahkneda. Dialoogi sulgemisel taastub külgpaneeli varasem avatud või suletud olek.

### Kaabli kontekstimenüü

Kaardil oleva kaabli paremklõps peab pakkuma tegevust „Muuda trajektoori”. See valib konkreetse vooluühenduse, tõstab kaabli selgelt esile ning käivitab olemasoleva kaablipunktide lisamise ja eemaldamise töövoo. Lahendus peab eristama sama tarbija vaike- ja alternatiivühendusi, et muudetaks just seda kaablit, millel paremklõps tehti. Nii saab trajektoori muuta ilma tööriistariba „Kaabli punkt” nuppu kasutamata.

Kaabli „Muuda trajektoori” kontekstimenüü on teostatud. Valitud ühendus tõstetakse esile, selle olemasolevaid punkte saab lohistada ning kaardile lisatavad punktid seotakse konkreetse kaabliga ka siis, kui tarbijal on mitu vooluühendust. Muutmise saab lõpetada tööriistaribalt, sama kaabli kontekstimenüüst või `Escape`-klahviga.

### Objektide nimekirja tegevused ja nähtavus

„Objektid” külgpaneelil lisatakse iga objekti juurde eraldi nähtavuse valik. Objekti tegelik nähtavus sõltub nii grupi kui ka objekti valikust: peidetud grupp peidab kõik oma objektid, kuid grupi uuesti kuvamisel jäävad üksikult peidetud objektid peidetuks.

Objektide nimekirja objekti paremklõps pakub vähemalt tegevusi „Muuda”, „Peida” või „Kuva” ja „Kustuta”. Need peavad kasutama täpselt sama rakendusloogikat nagu kaardil oleva objekti kontekstimenüü, et eri menüüdes ei tekiks erinevat käitumist.

Üksikobjekti nähtavus on plaani osa ja peab säilima `.pplan` salvestamisel. Vormingu muutmisel tuleb säilitada versioonita ning versioon 1–3 failide avatavus; vanadest failidest laaditud objektid on vaikimisi nähtavad.

Üksikobjekti nähtavus on teostatud `.pplan` versioonis 4. Objekti saab peita või kuvada nii objektirea linnukese kui ka kontekstimenüü kaudu; grupi ja objektitüübi nähtavus jäävad eraldi kõrgema taseme filtriteks.

### Vastuvõtukriteeriumid

- Külgpaneeli jaotised saab ümber järjestada ning järjestus säilib rakenduse taaskäivitamisel.
- Vaikejärjestuse taastamine töötab sõltumata kasutaja varasemast järjestusest.
- Kaardi tühjal kohal saab paremklõpsuga lisada iga toetatud objektitüübi täpsesse valitud asukohta.
- Kaardil ja objektide nimekirjas olevad „Muuda”, „Peida/Kuva” ja „Kustuta” tegevused annavad sama tulemuse.
- Ühe objekti peitmine ei muuda sama grupi teiste objektide nähtavust.
- Grupi peitmine ja uuesti kuvamine ei kaota objektide individuaalseid nähtavusvalikuid.
- Peidetud objekti nähtavus säilib pärast plaani salvestamist ja uuesti avamist.
- Objekti kustutamisel puhastatakse selle elektri-, kaabli- ja muud seosed olemasolevate reeglite järgi.

## 8. Salvestamise klahvikombinatsioonid

- `Ctrl + S` käivitab tavalise salvestamise ja avab faili valiku ainult juhul, kui plaanil pole veel failinime.
- `Ctrl + Shift + S` käivitab alati „Salvesta kui” tegevuse.
- Klahvikombinatsioonid peavad kasutama täpselt samu salvestusmeetodeid, dialooge ja eestikeelseid veateateid nagu tööriistariba nupud.
- Otsetee ei tohi käivituda teist korda dialoogi või muu modaalse akna sees.

Salvestamise klahvikombinatsioonid on teostatud põhiakna kiirklahvidena ning kasutavad tööriistariba nuppudega samu salvestusmeetodeid.

Lisaks on teostatud `Ctrl + N` uue plaani loomiseks, `Ctrl + O` plaani avamiseks ja `Ctrl + Shift + P` plaani andmete muutmiseks. Kõik otseteed kasutavad vastavate tööriistariba nuppudega samu tegevusi.

## 9. Muudatuste tagasivõtmine

- `Ctrl + Z` võtab tagasi viimase plaani muutnud tegevuse.
- `Ctrl + Alt + Z` teeb viimati tagasi võetud tegevuse uuesti.
- Ajalugu peab hõlmama vähemalt objektide lisamist, kustutamist, liigutamist ja andmete muutmist ning kaabli trajektoori muutmist.
- Faili avamine ja uue plaani loomine alustavad uut tühja ajalugu; salvestamine ise ei lisa ajalukku uut sammu.
- Tagasivõtmine ja uuestitegemine peavad uuendama kaarti, külgpaneele, voolukokkuvõtet ja salvestamata muudatuste olekut ühe tervikuna.
- Tööriistaribal või muus püsivalt nähtavas kohas peavad olema hiirega kasutatavad undo- ja redo-nupud.
- Nuppude keelatud olek peab näitama, kui vastavas suunas pole enam võimalik ajaloos liikuda.
- Nuppude kohtspikrid peavad kuvama tegevuse nime ja vastava klahvikombinatsiooni.

Piiratud undo/redo seisundiajalugu ja mälus töötav plaani hetktõmmise teenus on teostatud ning automaattestidega kaetud. Hetktõmmis kasutab sama teisendust nagu `.pplan` teenus, taastab objektid ja elektriseosed sõltumatu mudelina ning väldib sama pakitud kaardipildi korduvat hoidmist. Plaanimuudatused on seotud `Ctrl + Z` ja `Ctrl + Alt + Z` klahvikombinatsioonidega; tekstiväljas jääb `Ctrl + Z` teksti muutmise käsuks. Faili avamine ja uue plaani loomine alustavad uut ajalugu ning salvestatud seisundisse naasmine taastab puhta oleku. Objekti ja nimesildi lohistamine kasutab tehingut, mille esimene liikumine loob ajalookirje ning järgmised liikumised asendavad sama kirje lõppseisundit; tulemus ei sõltu JavaFX-i hiire vabastamise sündmuse jõudmisest ümber joonistatud sõlmeni. Ala-, joone-, kaabli- ja ühenduspunkti lohistamine salvestatakse ühe sammuna hiire vabastamisel. Undo/redo säilitab võimaluse korral aktiivse tööriista, sealhulgas kaablipunktide lisamise režiimi; eemaldatud ühendusele viitav režiim lõpetatakse turvaliselt.

Tööriistaribal on hiirega kasutatavad undo- ja redo-nupud koos klahvikombinatsioonide kohtspikritega. Nuppude lubatud olek uueneb plaanimuudatuse, lohistamise, undo, redo, salvestamise, faili avamise ja uue plaani loomise järel ning näitab kohe, kas vastavas suunas saab ajaloos liikuda.

## 10. Checklistid

### Kasutaja checklist

Iga plaan saab kasutaja hallatava kontrollnimekirja, kuhu saab lisada vabatekstilisi ülesandeid, neid ümber nimetada, järjestada, tehtuks märkida ja kustutada. Checklist, kirjete järjestus ja tehtud olek kuuluvad plaaniandmete hulka ning peavad `.pplan` failis säilima. Vormingu järgmise versiooni lisamisel säilib versioonita ning versioon 1–6 plaanide avamine; vanades plaanides on checklist vaikimisi tühi.

Kasutaja checklist on teostatud külgpaneeli järjestatava jaotisena. Kirjeid saab lisada tekstiväljalt, tehtud olekut muuta linnukesega ning ümbernimetamist, järjestamist ja kustutamist hallata kirje paremklõpsumenüüst. Andmed salvestatakse `.pplan` versioon 6 vormingus ja osalevad olemasolevas undo/redo ajaloos. Soovituste checklist jääb eraldi järgmiseks etapiks.

### Soovituste checklist

Eraldi soovituste loend aitab kontrollida, kas tavapärased alaplaani osad on läbi mõeldud. Esialgne soovitusloend sisaldab vähemalt järgmisi kirjeid:

- Tehnikatelk;
- Infotelk;
- Merch;
- Emergency exit;
- PA;
- Redla auto;
- Osalejate telk;
- Esmaabi.

Soovituse saab märkida tehtuks või ebaoluliseks. Kui soovitus vastab olemasolevale objektieelseadistusele, saab selle juurest alustada objekti lisamist, kuid rakendus ei tohi ainult nime sarnasuse põhjal automaatselt väita, et soovitus on täidetud. Soovitusloend peab tulevikus toetama eri üritusetüüpide malle.

Soovituste põhiloend ning tehtud ja ebaolulise oleku plaanipõhine salvestamine on teostatud. Objektieelseadistusest lisamise alustamine ja eri üritusetüüpide soovitusmallid jäävad hilisemaks etapiks.

## 11. Versioonitud paigalduspaketid ja vanade versioonide allalaadimine

GitHub Releasesis avaldatakse iga väljalaske juurde versioonitud Windowsi ja Linuxi paigalduspaketid. Nii saab kasutaja paigaldada rakenduse ilma lähtekoodi või `main` haru kloonimata ning vajaduse korral laadida alla varasema versiooni, näiteks vana ja uue kujunduse võrdlemiseks.

Rakendusse lisatakse vaade või link „Versioonid”, mis kuvab vähemalt praeguse versiooni, uusima saadaoleva versiooni ja GitHub Releasesi allalaadimislehe. Kui rakendus hakkab tulevikus pakette ise alla laadima, peab see:

- valima õige operatsioonisüsteemi ja arhitektuuri paketi;
- kuvama enne allalaadimist täpse versiooni ja faili;
- kontrollima faili terviklust avaldatud kontrollsummaga;
- mitte käivitama paigaldajat kasutaja selge kinnituseta;
- jätma alles võimaluse laadida teadlikult alla varasem versioon.

Tag'ipõhine GitHub Actionsi Release-töövoog on teostatud ja kontrollitud väljalaskega `v0.1.1`: see ehitab GitHubi Windowsi runneril EXE-paigaldaja ning Linuxi runneril RPM-i ja iseseisva rakendusepildi arhiivi. Tag peab kattuma Gradle'i versiooniga ning töövoog lisab Release'i juurde ka `SHA256SUMS` kontrollsummad. Windowsi koostamisel paigaldatakse WiX 4.0.6 koos jpackage'i nõutud UI- ja Util-laiendustega.

Rakenduse menüüs **Abi → Versioonid** kuvatakse paigaldatud ja uusim avaldatud versioon ning säilib GitHub Releases'i lehe avamise võimalus. Paigaldatud versiooniga rakendus kontrollib käivitumisel taustal, kas GitHub Releasesis on uuem versioon. Sobiva paketi leidmisel saab kasutaja valida allalaadimise asukoha; fail salvestatakse esmalt ajutiselt, kontrollitakse Release'i avaldatud SHA-256 summaga ja tehakse alles siis kasutatavaks. Paigaldaja või allalaadimiskausta avamine toimub ainult kasutaja eraldi valikul.

## 12. PA-süsteemi objektieelseadistused

Tulevase PA-planeerimise jaoks lisatakse objektieelseadistused, mis loovad kaardile kohe õigete mõõtude ja tüüpilise elektrivajadusega objektid. Esimeste erieelseadistuste hulka kuulub **Red Bull DJ Truck** mõõtudega **6 × 2,2 m**. Selle elektrivajadus lisatakse pärast kasutatava konfiguratsiooni tegelike tehniliste andmete kinnitamist ning jääb objekti loomisel muudetavaks.

Kõlarite ja muu PA-tehnika eelseadistustel peab samuti olema sisseehitatud vaikimisi elektrivajadus. Mudel peab eristama seadme tüüpilist vaikeväärtust plaanis kasutaja määratud tegelikust väärtusest, et eelseadistuse uuendamine ei kirjutaks olemasolevate plaanide käsitsi muudetud võimsusi üle.

Eelseadistused peavad kasutama sama objektide, seadmete ja elektriühenduste mudelit nagu käsitsi loodud objektid. Need ei tohi luua eraldi, ainult kaardil nähtavat paralleelmudelit.

Esimene eelseadistus on teostatud: **Red Bull DJ Truck** lisatakse tavalise telgiobjektina vaikimisi 6 × 2,2 m mõõtudega ja 1000 W „DJ Trucki põhitoite” seadmega. Võimsus on esialgne testväärtus ning tuleb tegelike tehniliste andmete selgumisel üle kontrollida. Nime, grupi, värvi, mõõte ja seadme võimsust saab muuta nagu tavalisel telgil. DJ Trucki eelseadistus säilib pärast salvestamist, avamist ja kopeerimist ning kaardil on sellel sinine täide, punane ääris ja „DJ” tähis.

## 13. Aedade planeerimise tööriist

Ürituse inventari planeerimiseks lisatakse eraldi aiaridade tööriist. Aed ei ole tavaline vabapikkusega joon, vaid koosneb vaikimisi **3,5 m** pikkustest füüsilistest aialõikudest. Plaan peab näitama nii aiaketi geomeetriat kui ka selleks vajalike aedade täpset kogust.

### Domeenimudel

- aiarida koosneb järjestatud jäikadest aialõikudest ja nende ühenduspunktidest;
- lõigu vaikepikkus on 3,5 m, kuid väärtus jääb vajaduse korral muudetavaks eri inventaritüüpide jaoks;
- iga lõik on eraldi identifitseeritav, et seda saaks ühendada, lahti ühendada, eemaldada või teise ritta tõsta;
- ühendatud lõigud säilitavad liigutamisel oma tegeliku pikkuse ja ühenduse naaberlõikudega;
- lahti ühendatud lõigust või alamreast saab iseseisev aiarida;
- `.pplan` salvestab lõikude pikkused, järjestuse, ühendused ja kuju tagasiühilduva uue vorminguversioonina.

### Kasutamine kaardil

- kasutaja määrab aiaraja alguse ja soovitud suuna või murrupunktid;
- rakendus paigutab rajale 3,5 m lõigud ning näitab, mitu füüsilist aeda on vaja;
- otsapunkti lohistamine pöörab või paigutab ühendatud jäiga lõigu ümber ilma selle pikkust muutmata;
- ühenduspunkti muutmisel säilitavad kõrvalolevad lõigud pikkuse ja rakendus lahendab ühendatud ahela uue kuju;
- terve rea saab korraga ümber paigutada või pöörata;
- kontekstimenüüst saab valitud ühenduse lahti võtta, read uuesti ühendada, lõigu lisada või eemaldada;
- undo/redo käsitleb ühte lohistamist, ühendamist või lahtiühendamist ühe tegevusena.

Keeruliste kujude jaoks toetatakse ringigeneraatorit. Ringi puhul sisestab kasutaja raadiuse. Kuna 3,5 m sirged aiad ainult lähendavad ringjoont, näitab rakendus enne loomist lõikude arvu, tekkiva hulknurga tegelikku raadiust ja kõrvalekallet soovitud raadiusest. Sissepääsuava ei arvutata generaatoris eraldi: kasutaja ühendab valmis ringi sobivast punktist lahti ning paigutab vajalikud aiad olemasolevate muutmistööriistadega ümber. Eraldi kaaregeneraatorit praegu ei planeerita, sest sirget aiarida saab ühenduspunkte liigutades piisavalt kiiresti kaareks vormida.

Sirge ja murtud aiaraja põhietapp on teostatud. Aiarida on tavalise joone asemel omaette plaaniobjekt, mis säilitab lõikude arvu, ühe lõigu pikkuse ja kaks jagatavat ühenduspunkti. Kahe kaardiklõpsu vaheline pikkus ümardatakse lähima täisarvu 3,5 m lõikudeni; ridu saab otspunktidest ühendada ja lahti võtta ning terve ühendatud võrk liigub ühe tervikuna. Jagatud ühenduspunkti lohistamisel deformeerub võrk ilma ühe rea alguseks määramiseta ja iga aiarida säilitab oma füüsilise pikkuse. Ühendatud võrk kuvatakse objektinimekirjas ühe loogilise aiarajana ning selle nimi, grupp, värv, lukustus, märkmed, sildiseade ja kustutamine rakenduvad kogu võrgule. Sisemisele füüsilise aia piirile saab lisada uue ühenduspunkti ning ühenduspunkti eemaldamisel ühendatakse selle kaks naaberpunkti otse. Kaardi koguse- ja pikkusesildid on eraldi kihina peidetavad. Üksikute 3,5 m lõikude täielik eraldi identiteet ning kaaregeneraator jäävad järgmistesse etappidesse.

Aiaringi generaatori esimene etapp on teostatud. „Aiaring” on eraldi lisamistüüp: dialoogis määratakse soovitud raadius ning enne kinnitamist kuvatakse 3,5 m aedade arv, tegelik hulknurga raadius ja kõrvalekalle. Kaardil märgitakse seejärel ringi keskpunkt ning generaator loob suletud ühendatud aiavõrgu. Funktsioon ootab käsitsi kontrolli. Üksikute 3,5 m lõikude täielik eraldi identiteet jääb järgmistesse etappidesse.

### Inventarikokkuvõte

Inventarikokkuvõtte esimene etapp on teostatud ning ootab käsitsi kontrolli. Külgpaneel, TXT-raport ja PDF-raport kasutavad sama aedade koguarvu ja kogupikkust. Ühendatud aiavõrk kuvatakse ühe loogilise objektina, kuid selle kõik füüsilised lõigud lähevad kogusesse.

Külgpaneelil on eraldi „Inventari” jaotis, mis on nähtav ka korraldajavaates. Seal kuvatakse ühendatud aiavõrgud tervikobjektide kaupa, telkide kogus ning objektidele määratud päris inventar. Telkide haru avaneb nimede, mõõtmete ja märkustega ning märkust saab samas muuta. Ristkülikuid ja ringe endid inventariesemetena ei loetleta, sest need võivad tähistada näiteks alasid. Iga inventarinimetus avaneb noolega detailvaateks, kus kuvatakse seda vajavad telgid, alad ja objektid koos koguste ning märkustega. Iga allikobjekti kogust saab samas vaates `− / +` nuppudega muuta. Aia-, aiakivi-, telgi-, objektipõhise inventari ja kaabliridade paremklõps võimaldab muuta vastavat kuvatavat märkust ning avada seotud objekti või kaabli kaardil; peidetud allikad on loendis märgistatud ja vaade uueneb kohe. Tavavaates lisandub kokkupandav kaabliinventar. „Voolu kokkuvõte” sisaldab ainult elektrikoormusi. Objektivaadet dubleeriv gruppide ja objektide loend ning aiasektoreid eraldi näidanud pikkusejaotus eemaldati.

Inventari refaktori esimene etapp on teostatud: aedade tervikvõrgud, telgid ja objektidele määratud inventari kogused ning allikobjektide jaotus arvutatakse JavaFX-ist sõltumatus `InventorySummaryService` teenuses. Kaabliinventari pikkusmärkmed, tükid, tüübi koondid ja alternatiivühendused arvutab eraldi testitud `CableInventorySummaryService`. Külgpaneel ja TXT/PDF-raport kasutavad nii aedade tervikvõrkude jaoks sama `FenceInventoryService` tulemust kui ka kaabliinventari jaoks sama arvutus- ja tekstivormindusloogikat. See vähendab `PlaaniseppApp` vastutust ning loob aluse hilisemale üldisele inventarimudelile.

Voolukokkuvõtte refaktor on samuti teostatud. `PowerHierarchyService` koostab ühe testitud allika-, väljundi-, tarbija- ja seadmepuu koos ühendamata tarbijatega. Seda kasutavad nii külgpaneel, TXT/PDF-raport kui ka lihtne `PowerSummaryService`, mistõttu kapi ja väljundi koormusi ning seadmepõhiseid alternatiivühendusi ei arvutata enam eri vaadetes eraldi. TXT/PDF-raporti osad on eraldatud väikestesse vormindajatesse: `PlanOverviewTextFormatter`, `PowerReportTextFormatter`, `CableReportTextFormatter`, `FenceReportTextFormatter` ja `ObjectReportTextFormatter`. `ReportTextExporter` määrab nüüd ainult osade järjekorra ja kasutaja valitud ulatuse. `CableInventorySummaryService` oskab koostada kokkuvõtte otse tervest plaanist, nii et raport ja külgpaneel ei ehita enam kaabliarvutuse sisendit kumbki eraldi. Plaani üldandmete, gruppide ja tekstimärkmete raportiväljund on kaetud regressioonitestidega.

### Elektrikilpide visuaalne eristus

Elektrikilpide ja alajaotuskilpide muutmisvaates saab muuta objekti värvi ning kaardil kuvatavat suurust. Alajaotuskilbi oranž vaikevärv eristab seda elektrikapi sinisest vaikevärvist, kuid mõlemad on muudetavad. Muudatused rakenduvad kohe kaardil, säilivad `.pplan` versioon 11 failis, kopeeritakse koos objektiga ning osalevad undo/redo ajaloos.

### Planeeritud inventarimudel

- **Tehtud: aiakivid arvutatakse aia geomeetriast automaatselt.** Iga füüsilise aia kahe lõigu vahel ning iga vaba otspunkti juures on üks aiakivi. Kui ühes ühenduspunktis kohtuvad kolm, neli või rohkem aeda, läheb inventari siiski ainult üks aiakivi. Suletud N lõiguga ring vajab seega N aiakivi ja avatud N lõiguga ahel N + 1 aiakivi. Igal aiakogumikul on oma `− / +` parandus, mis võimaldab näiteks otspunktikivi ära jätta. Aiata aiakivid lisatakse „Lisa inventar” kaudu ja koondatakse sama „Aiakivid” haru kogusesse.
- **Tehtud: telgiraskused, lauad ja pingid** on objektipõhise inventari kiirvalikud. Telgile, alale ja kujuobjektile saab lisada ka vabalt nimetatud inventariridu koos koguse ja märkusega; kogused liidetakse automaatselt kogu plaani inventari ning TXT- ja PDF-raportisse.
- **Tehtud: kujuobjektid, telgid ja alad kasutavad sama üldist objektipõhist inventarimudelit**, et uusi inventariliike ei peaks eraldi koodi sisse ehitama. Inventar kopeerub objektiga, kustutamisel kaob koondist, osaleb undo/redo ajaloos ning säilib `.pplan` versioon 14 failis.
- Inventarikirjel on vähemalt nimetus, kogus ja vajaduse korral märkus. Objekti kopeerimisel kopeeritakse selle inventar kaasa; kustutamisel kaob selle panus koondinventarist; muudatused osalevad undo/redo ajaloos ja säilivad `.pplan` failis.
- **Tehtud:** inventari koondist saab lisada ka objektita lisainventari, sealhulgas aedu, aiakive, telke, laudu ja telgiraskusi. Sisseehitatud inventariliigid liidetakse oma põhiharude kogustesse. Kirjel on nimetus, kogus ja märkus; kogust saab muuta `− / +` nuppudega, kirjet muuta või eemaldada ning see säilib `.pplan` versioon 16 failis ja jõuab TXT/PDF-raportisse.

### Käsitsi koguseparandused

Aiakivide esimene parandusetapp on teostatud. „Aiakivid” inventarirea pealkiri näitab kogusummat ning selle noole all kuvatakse iga aiakogumiku automaatne kogus, käsitsi parandus, lõppkogus ja `− / +` nupud. Aiata aiakivid kuvatakse sama haru lisainventarina ning nende kogust saab samuti `− / +` nuppudega muuta. Lõppkogus ei saa langeda alla nulli. Korrigeeritud kogus kuvatakse ka aia objektireal ja kaardi kogusesildil. Valitud aiakogumiku vaates kuvatakse terve kogumiku aedade arv, kogupikkus ja aiakivide arv; seal saab aiakivide parandust muuta ning selle kogumiku kogusesildi eraldi peita või näidata. Peidetud kogusesilt on kogumiku valimise ajal siiski nähtav nagu nimesilt ja kaablisilt. Aiarea ja aiaringi loomisdialoogis saab määrata ühe aia pikkuse ning kogusesildi nähtavuse; viimati kasutatud väärtused jäetakse meelde. Need väärtused säilivad `.pplan` failis, osalevad undo/redo ajaloos, liiguvad aiavõrgu kopeerimisel kaasa ning jõuavad TXT- ja PDF-raportisse.

- Iga Inventari jaotises kuvatava liigi juures näidatakse selgelt kolm väärtust: **automaatselt arvutatud**, **käsitsi parandus** ja **lõplik kogus**.
- Kasutaja saab iga liigi kogust käsitsi suurendada või vähendada. Näited: kõlarite stabiliseerimiseks lisatud telgiraskused, tegelikust lahendusest tulenevalt kaks aiakivi vähem või telgi ja alaga sidumata lauad ning pingid.
- Parandus ei kirjuta automaatselt arvutatud väärtust üle, vaid salvestatakse eraldi pluss- või miinusväärtusena. Nii jääb nähtavaks, millest lõplik kogus tekkis, ning plaani muutmisel saab automaatset osa turvaliselt uuesti arvutada.
- Negatiivne parandus ei tohi muuta lõplikku kogust alla nulli. Parandust peab saama nullida ning võimaluse korral lisada sellele lühikese põhjenduse.
- Käsitsi parandused on plaanipõhised, säilivad salvestamisel, osalevad undo/redo ajaloos ning jõuavad TXT- ja PDF-raportisse.
- Üldine sama põhimõttega parandussüsteem telgiraskustele, laudadele, pinkidele ja tulevastele inventariliikidele on planeeritud hilisemaks tööks.
- **Tehtud:** inventari koondharu paremklõpsust saab luua kaardi nähtava ala keskele tekstiobjekti, mis sisaldab haru pealkirja, kogusummat ja allikaridu. Üldise inventariliigi (nt „Lauad”) tekst on sünkroonitav ning uueneb nii objekti- kui ka lisainventari koguse ja märkuse muutumisel. Objekti paremklõpsust saab luua eraldi teksti märkmetest, selle objekti inventarist või elektri- ja alajaotuskilbi väljunditest. Seotud tekst pärib objekti grupi ning loomisel saab valida püsiva nime ja sisu sünkroonimise ning objekti juurde viitava joone. Inventaritekst uueneb objekti nime või inventariridade muutmisel ja kilbi tekst väljundite ning nende koormuse muutmisel. Viitejoone allikapoolset kinnituspunkti saab kaardil lohistada. Seos, allikatüüp, sünkroonimine, joone valik ja kinnituspunkt säilivad `.pplan` versioon 20 failis ning neid saab tekstiobjekti vaates sisse või välja lülitada. Seotud teksti paremklõpsumenüüst saab allikaseose katkestada ja jätta praeguse nime ning sisu tavaliseks staatiliseks tekstiks. Objektide paremklõpsumenüü kuvab sisupõhiseid tekstitoiminguid ainult sobival tüübil: väljunditeksti kilpidel, inventariteksti inventariga objektidel, märkmeteksti märkmetega objektidel ning seose katkestamist seotud tekstobjektil.
- **Tehtud: versioonikontrolli uuenduse põhinupp on „Uuenda”.** Sobiv paigalduspakett laaditakse kontrollitud ajutisse asukohta ning Linuxis käivitatakse see eraldi süsteemi `xdg-open` protsessina, et paigaldaja avamine ei sõltuks JavaFX-i töölauaintegraatsioonist ega põhjustaks rakenduse sulgemisel native-crash'i. RPM-i paigaldamisel jälgib eraldi taustaprotsess käivitusfaili muutumist ja käivitab uue Plaaniseppa automaatselt pärast paigalduse lõppu.

### Vastuvõtukriteeriumid

- sirge ja murtud aiarida koosnevad tegeliku pikkusega lõikudest ning kogus on kontrollitav;
- ühendatud lõigu liigutamine ei muuda selle pikkust ega tekita ühendusse nähtamatut vahet;
- rea saab valitud ühenduskohast lahti võtta ja hiljem uuesti ühendada;
- 8 m raadiusega ringi saab luua suletud aiavõrguna ning rakendus kuvab enne kinnitamist vajaliku aedade arvu ja tegeliku raadiuse;
- aiaridade salvestamine, avamine, undo/redo ja raportitesse lisamine säilitavad sama geomeetria ning inventarikoguse;
- aiakivide arv vastab unikaalsete füüsiliste otspunktide ja ühenduspunktide arvule ka hargnevas aiavõrgus;
- telgi, ala ja kujuobjekti inventar liitub koondisse ning kopeerimine, kustutamine ja salvestamine säilitavad õiged kogused;
- käsitsi pluss- ja miinusparandus on automaatsest kogusest eristatav ning lõplik kogus ei lange alla nulli;
- versioonita ning varasemate `.pplan` versioonide avamine jääb muutmata.

## 14. Kiirem objektitöö ja ühtne rakendamisloogika

### Omaduste valimine

- Grupi määramise tekstiväli asendatakse muudetava rippvalikuga, mis pakub plaanis juba kasutatavaid gruppe, kuid lubab sisestada ka uue grupi nime.
- Vooluallika ja väljundi määramise juures lisatakse tegevus „Vali kaardilt”. See käivitab ajutise valikurežiimi, tõstab esile ainult sobivad elektrikapid ja alajaotuskilbid ning seob valitud allika pärast väljundi kinnitamist. `Escape` katkestab valiku muutust tegemata.
- Läbipaistvuse liugur peab näitama tulemust kaardil kohe lohistamise ajal ning selle kõrval peab alati olema nähtav arvuline protsent. Üks lohistamine salvestatakse undo-ajaloos ühe muudatusena.
- Kui nimesildid on üldiselt peidetud, kuvatakse valitud objekti nimesilt ajutiselt seni, kuni objekt on valitud. See ei muuda plaani ega kihi püsivat nähtavusseadet.

### Automaatse rakendamise reegel

Valitud objekti eraldi „Rakenda muudatused” nupp eemaldatakse, sest see muudab külgpaneeli kasutamise aeglasemaks ja ebaselgemaks. Kõik väljad rakenduvad automaatselt:

- ühe väärtusega otsesed juhtelemendid, näiteks lukustus, nähtavus, värv ja läbipaistvus, annavad kaardil kohese eelvaate;
- teksti- ja arvuväli kinnitub Enteriga või väljalt lahkudes;
- omavahel seotud vooluvalikud rakenduvad siis, kui moodustub täielik ja kehtiv kombinatsioon;
- ajutiselt vigast arvuväärtust ei kirjutata mudelisse ning kasutajale kuvatakse selge valideerimisteade;
- ühe välja redigeerimine või üks liugurilohistamine moodustab ühe undo-sammu;
- teise objekti valimine ei tohi pooleliolevat korrektset väljamuudatust kaotada;
- sama omadus ei tohi mõnes vaates rakenduda kohe ja teises alles nupuga.

### Klahvikombinatsioonid

- `Ctrl + L` lülitab valitud objekti lukustuse sisse või välja.
- `Ctrl + H` peidab valitud objekti või kuvab selle uuesti.
- `Delete` kustutab valitud objekti, kasutades sama kinnitust ja seoste puhastamist nagu kontekstimenüü.
- `Ctrl + Shift + 1` alustab telgi lisamist.
- `Ctrl + Shift + 2` alustab elektrikapi lisamist.
- `Ctrl + Shift + 3` alustab alajaotuskilbi lisamist.
- `Ctrl + Shift + 4` alustab üldobjekti lisamist.
- `Ctrl + Shift + 5` alustab teksti lisamist.
- `Ctrl + Shift + 6` alustab markeri lisamist.
- `Ctrl + Shift + 7` alustab joone lisamist.
- `Ctrl + Shift + 8` alustab ala lisamist.
- `Ctrl + Shift + 9` reserveeritakse aedade tööriistale.

Kõik lisamisotseteed peavad käivitama sama töövoo nagu tööriistariba ja kontekstimenüü. Vastavad kombinatsioonid kuvatakse menüüs ja kohtspikrites. Kui lisatavaid tüüpe tuleb üle üheksa, kasutatakse muudetavat keskset otseteede registrit, mitte raskesti meeldejäävaid mitmeastmelisi numbrikombinatsioone. Tekstiväljas kirjutamine ega modaalne dialoog ei tohi kaardi otseteid käivitada.

### Vastuvõtukriteeriumid

- olemasoleva grupi saab valida ning uue grupi saab samas väljas luua;
- vooluallika saab valida kaardilt ilma sobimatut objekti kogemata ühendamata;
- kohesed ja kinnitamist vajavad muudatused on enne kasutamist visuaalselt eristatavad;
- kõik otseteed annavad sama tulemuse nagu vastav hiirega käivitatud tegevus;
- läbipaistvuse lohistamine ja objekti kustutamine moodustavad kumbki ühe undo-sammu;
- peidetud nimesilt ilmub ainult valitud objektile ega muuda salvestatud kihiseadeid.

## 15. Paigutuslukustus ehk turvaline vaatamisrežiim

Rakendusse lisatakse kogu kaardi paigutust kaitsev lüliti. See on objekti enda lukustusest kõrgema taseme ajutine töörežiim, mille eesmärk on võimaldada plaani turvaliselt uurida ja andmeid parandada ilma geomeetriat kogemata muutmata.

Paigutuslukustuse ajal jääb võimalikuks:

- objektide valimine kaardilt ja külgpaneelilt;
- nime, grupi, märkmete, värvi, läbipaistvuse, seadmete ja muude mittegeomeetriliste andmete muutmine;
- kihtide, gruppide, objektide ja nimesiltide nähtavuse muutmine;
- otsing, kokkuvõtete kasutamine, suumimine, kerimine ja eksport;
- kommentaaride lugemine ning tulevikus nendele vastamine.

Paigutuslukustuse ajal blokeeritakse vähemalt:

- objektide lohistamine;
- nimesiltide ja kaablisiltide lohistamine;
- joone-, ala- ja tulevaste aiapunktide lisamine, eemaldamine ning lohistamine;
- kaablipunktide lisamine, eemaldamine ja lohistamine;
- objektide, kaablite ja kujude lisamine, kleepimine ning kustutamine;
- kaabli trajektoori ja muu kaardigeomeetria muutmise režiimid.

Režiim peab olema tööriistaribal selgelt nähtav ning selle aktiivsus ei tohi sõltuda sellest, milline objekt on valitud. Blokeeritud tegevus ei tohi vaikselt ebaõnnestuda: keelatud nupud, menüükirjed ja hiirekursor peavad näitama, et paigutus on kaitstud. Objektide olemasolevad individuaalsed lukud säilivad eraldi ning neid ei kirjutata režiimi sisse- või väljalülitamisel üle.

Eelistatud on hoida paigutuslukku kasutaja rakenduseelistusena, mitte plaani püsiva omadusena. Nii ei muuda turvalise vaatamise sisselülitamine `.pplan` faili ega tekita salvestamata muudatust. Enne teostamist tuleb otsustada, kas rakendus taastab lukustatud režiimi ka järgmisel käivitamisel; turvalisem vaikevalik on see taastada.

### Vastuvõtukriteeriumid

- lukustatud paigutusega saab kaardil objekte valida ilma neid ühegi piksligi võrra liigutamata;
- külgpaneelil saab muuta valitud objekti teksti-, värvi- ja muid mittegeomeetrilisi andmeid;
- ükski geomeetriat muutev nupp, kontekstimenüü, kiirklahv ega hiiretoiming ei lähe lukust mööda;
- kaardi suumimine, kerimine, otsing ja objektile liikumine töötavad edasi;
- režiimi väljalülitamisel taastuvad kõik muutmistööriistad ja objektide individuaalsed lukud endises olekus;
- režiimi lülitamine ei muuda plaani ega lisa undo-sammu.

## 16. Kõrglahutusega ja joondatud aluskaardid

Ekraanipildi tegemise asemel lisatakse aluskaardi hankimise töövoog, milles kasutaja märgib eelvaates soovitud ristkülikukujulise ala ning valib väljundi mõõdu või eraldusvõime. Hankimise etapp võib kasutada kaarditeenuse koordinaate ja projektsiooni, kuid Plaanisepp ühendab saadud paanid üheks rasterpildiks ning jätkab redaktoris olemasoleva pikslipõhise loogikaga. Valitud ala tegeliku ulatuse põhjal saab arvutada ja pakkuda ka pikslite arvu meetri kohta.

Eelistatud andmeallikad on ametlikud WMS-, WMTS- või TMS-teenused, mis lubavad kindla ulatuse, projektsiooni, pildimõõdu ja kihi küsimist. Suure ala või teenuse pildimõõdu piirangu korral laaditakse ala paanidena ning ühendatakse kadudeta. Lahendus peab piirama mõistlikult lõpppildi mõõtmeid ja mälukasutust ning näitama enne allalaadimist hinnangulist failisuurust.

Tartu või riikliku kaarditeenuse puhul peab saama sama täpse ruumilise ulatuse, projektsiooni ja pikslimõõduga hankida vähemalt:

- ortofoto;
- puhta alus- või tehnilise kaardi.

Need salvestatakse eraldi, kuid täpselt kohakuti kaardikihtidena, mille vahel saab nähtavust vahetada. `.pplan` pakett säilitab kasutatud pildid ning võimaluse korral ka allika nime, kihi tunnuse, hankimise aja, ulatuse ja nõutud allikaviite. Tartu Geoarhiivi konkreetsete kihtide, teenuse otspunktide ja kasutustingimuste sobivus tuleb enne teostamist eraldi kinnitada.

**Tehtud olemasolevate kaartide jaoks:** kaardivaate paremas ülanurgas on alati ligipääsetav „Tavakaart / Ortofoto” lüliti. See vahetab kaasasolevaid samasse ulatusse joondatud kaarte, näitab aktiivset valikut ning muudatus osaleb plaani salvestamises ja undo/redo ajaloos.

**Esimene päriskaardi etapp tehtud:** Fail-menüüst ja uue plaani loomise aknast saab avada Maa- ja Ruumiameti kaardi eelvaate, valida ala kaarti lohistades ja suumides ning määrata pikslite arvu meetri kohta. Vaikimisi kasutatakse Tartu tänavavalgustuse rakendusega sama puhast halltoonides aluskaarti; soovi korral saab valida värvilise põhikaardi. Programm arvutab valitud ala järgi automaatselt optimaalse eraldusvõime, näitab enne laadimist ala ja väljundi mõõtmeid, küsib sama EPSG:3301 ulatuse ja pikslimõõduga aluskaardi ning ortofoto, määrab plaani mõõtkava automaatselt ja salvestab mõlemad pildid `.pplan` faili. Teenuse ühe päringu piirangust suuremad pildid laaditakse kuni 4000 × 4000 px paanidena ja õmmeldakse pikslitäpselt kokku. Allalaaditud paariga vahetab kaardil olev lüliti kohaliku aluskaardi ja ortofoto vahel. Lõpppilt on piiratud 8000 pikslile ja 40 megapikslile; otsing ning põhjalikum allikametaandmestik jäävad järgmisse etappi.

Ka rakenduse Tartu vaikekaardi tavakaart ja ortofoto laaditakse nüüd sama Maa- ja Ruumiameti API kaudu ning salvestatakse georefereeritud paarina plaani. Rakendusega kaasas olevad PNG-d jäävad ainult võrguühenduseta varuvariandiks.

Kaardi muutmine asub Plaani andmete dialoogis. Päriskaardi valik avaneb seal nupust (mitte uue plaani järel käivitatava linnukesena) ning Fail-menüüs eraldi dubleerivat käsku enam pole. Kaardiala valiku eelvaates saab vahetada tavakaardi ja ortofoto vahel; eelvaate võrgupäringud toimuvad taustal ning lohistamisel liigub olemasolev kaart kohe kursoriga kaasa.

Olemasoleva georefereeritud plaani kaardiala saab Plaani andmetest uuesti avada, suurendada, vähendada või nihutada. Objektide, kujupunktide, aedade ühenduste ja kaablitrajektooride pikslikoordinaadid teisendatakse vana ning uue EPSG:3301 ulatuse vahel, mistõttu säilib nende tegelik asukoht. Pärast kaardi loomist või ala muutmist imporditakse valitud alale jäävad uued Tartu püsivoolukilbid automaatselt; varem imporditud sama nimega kilpe ei dubleerita.

Kõrge eraldusvõimega raster ei muuda kaardikirju ega Plaanisepa objekte ekraanile mahutamisel mikroskoopiliseks: kartograafiline aluskaart hangitakse loetava sümbolitihedusega ja skaleeritakse lõppresolutsiooni, objektide fondid, sildid, jooned, aiad, kaablid ning markerid kasutavad px/m suhtest tuletatud visuaalset kordajat. Olemasolevale plaanile suurema px/m kaardi määramisel skaleeritakse ka objektide pikslikoordinaadid, säilitades nende asukoha ja meetermõõdud.

Visuaalse kordaja teine etapp arvestab ka kaardivaate zoomi. Siltidel, tekstidel, joontel ja sümbolitel on välja suumimisel loetavuse miinimum ning sisse suumimisel piiratud maksimum; valikuäärised, kujupunktid, kaabli kinnituspunktid ja pööramiskäepidemed säilitavad stabiilse ekraanisuuruse. „Automaatne optimaalne” on ümber nimetatud „Automaatne (suurim lubatud)” valikuks, sest see määrab ainult 8000 px / 40 MP piiridesse mahtuva rastereraldusvõime, mitte kujunduslikku optimumi.

Kõrglahutusega kaardi jõudluse jaoks dekodeeritakse aluskaardi raster ühe korra ja seda taaskasutatakse redraw'de vahel. Zoom rakendub kohe olemasoleva kaardikihi transformatsioonina ning zoomitundlike elementide täpsem redraw tehakse lühikese viitega. Üksikute objektide ja kujualade tavalisel lohistamisel nihutatakse olemasolevat JavaFX sõlme ning kogu kaardivaade ehitatakse uuesti alles lohistamise lõpus.

Georefereeritud Tartu aluskaardiga saab Fail-menüüst importida valitud alale jäävad Tartu linna avaliku tänavavalgustuse andmekihi püsivoolukilbid. EPSG:3301 koordinaadid teisendatakse aluskaardi pikslikoordinaatideks; elektrikilbi objektile tulevad teenusest nimi, lähte-ID ning olemasolu korral mark, märkus, maksimumvool ja nimivool. Tundmatuid väljundeid või võimsusi ei oletata ning kordusimport jätab sama nimega olemasolevad elektrikilbid vahele.

Google Mapsi ekraanipilte ega kaardipaanide kopeerimist ei kasutata selle töövoo alusena. Kui Google'i ametlikku staatilise kaardi teenust üldse toetada, tuleb arvestada selle pildimõõdu, autentimise, arvelduse, kuvamise ja säilitamise tingimustega. Esmane prototüüp tehakse avatuma ametliku WMS/WMTS teenusega.

### Vastuvõtukriteeriumid

- kasutaja saab eelvaates märkida soovitud ala ja näeb enne kinnitamist lõppresolutsiooni;
- ortofoto ja tehniline kaart katavad piksel-piksli haaval identse ala;
- imporditud plaan kasutab edasi praegust pikslipõhist joonistus- ja mõõtkavaloogikat;
- suur ala laaditakse kontrollitult paanidena ning liiga suur väljund blokeeritakse selge teatega;
- kaardi allikas ja nõutud viide on plaanis või ekspordis säilitatavad;
- aluskaardid avanevad `.pplan` paketist ka internetiühenduseta.

## 17. Korraldajavaade

Korraldajavaade on sama plaani lihtsustatud kuvaprofiil kasutajatele, kes ei tegele elektri ega tehnikaga. See ei kustuta ega teisenda plaani andmeid.

- külgpaneelilt peidetakse voolu kokkuvõte ning kaablite ja elektri omadused;
- kaardil ei kuvata kaableid, elektrikappe, alajaotuskilpe ega muid ainult tehnikavaate kihte;
- „Lisa” menüüst ja kiirklahvidest eemaldatakse tehnikaga seotud objektitüübid;
- korraldajavaatest tavavaatesse naasmine taastab kõik tehnikaandmed ja nende varasema nähtavuse;
- vaate aktiivsus on kasutaja eelistus või jagamisel valitav kuvaprofiil, mitte plaaniandmeid hävitav muudatus;
- korraldajale tehtavates eksportides saab sama filtrit teadlikult kasutada.

Korraldajavaate esimene etapp on teostatud ajutise kuvarežiimina. Alati tehnilisteks loetakse praegu kaablid, elektrikapid ja alajaotuskilbid; telgid ja muud üldobjektid jäävad nähtavaks ka siis, kui neil on elektriandmed. Režiim ei salvesta plaani ega muuda kihtide varasemaid valikuid. Kuvaprofiili püsiv eelistus, kasutaja täpsustatav tüübiloend ja ekspordi eraldi korraldajaprofiil jäävad hilisemaks.

## 18. Kommentaarid ja ülevaatus

Plaanile lisatakse kommentaarikiht, mille kaudu saab küsimusi ja parandusi siduda täpse koha või objektiga.

- objekti kontekstimenüüst lisatud kommentaar seotakse objekti püsiva ID-ga ning liigub koos objektiga;
- kaardi tühjal kohal lisatud kommentaar salvestab oma asukoha ja kuvatakse väikese kommentaarimarkerina;
- avatud kommentaariga objektil on selge, kuid kaarti mitte varjav märk või ümbris;
- eraldi külgpaneel loetleb kõik avatud, lahendatud ja arhiveeritud kommentaarid ning valitud kommentaar viib vastava objekti või punktini;
- kommentaari saab märkida tehtuks, mis muudab selle lahendatuks, ning seejärel arhiveerida;
- kommentaarile saab vastata ning vastused kuvatakse ajaliselt järjestatud lõimena;
- kommentaar säilitab teksti, loomise ja muutmise aja, oleku ning võimaluse korral kirjutaja nime;
- kommentaaride markerite ja ümbriste nähtavust saab kihina sisse või välja lülitada;
- lahendamine, vastamine ja asukoha muutmine peavad töötama undo/redo ning `.pplan` salvestamisega.

Objekti kustutamisel ei tohi sellega seotud kommentaarid vaikides kaduda. Kasutajale pakutakse kas kommentaaride arhiveerimist või nende muutmist samas asukohas olevateks kaardikommentaarideks. Kommentaaride lisamine nõuab uut tagasiühilduvat `.pplan` vorminguversiooni; vanemates plaanides on kommentaaride loend tühi.

### Vastuvõtukriteeriumid

- kommentaari saab lisada nii objektile kui ka tühjale kaardikohale;
- kommentaariloendi kirje viib alati õige objekti või asukohani;
- vastused, tehtud olek ja arhiiv säilivad pärast salvestamist ja uuesti avamist;
- tehnilise kihi peitmine ei peida sellele lisatud avatud kommentaari kommentaariloendist;
- kommentaari visuaalne tähis on arusaadav ka värvieristuseta;
- versioonita ning varasemate `.pplan` versioonide avamine jääb muutmata.

## 19. Visuaalse kasutajaliidese uuendamine

Rakendusele tuleb kujundada ühtne ja tänapäevane visuaalne keel. Praegused JavaFX-i vaikestiilid asendatakse järk-järgult Plaanisepa enda stiiliga, säilitades seejuures selguse, töökiiruse ja eri ekraanisuuruste toe.

### Kujunduse põhialused

- määrata rakenduse värvipalett, tüpograafia, vahed, nurgaraadiused ja komponentide olekud;
- kujundada ühtlaselt tööriistariba, nupud, sisestusväljad, külgpaneeli jaotised, nimekirjad, kontekstimenüüd ja dialoogid;
- eristada selgelt esmased, teisesed, ohtlikud, aktiivsed ja keelatud tegevused;
- asendada sobivates kohtades tekstirohked tööriistariba tegevused arusaadavate ikoonide, siltide ja kohtspikritega;
- säilitada hea loetavus Linuxi ja Windowsi erinevate DPI- ning süsteemiskaalade korral;
- arvestada klaviatuurifookuse, kontrasti ja muude ligipääsetavuse nõuetega;
- vältida kaardiala vähendamist pelgalt dekoratiivsete elementide arvelt.

### Teostusviis

Esimese sammuna luuakse keskne JavaFX CSS-teema ja väike korduvkasutatavate stiiliklasside kogum. Muudatus tehakse vaadete kaupa, et olemasolevat käitumist oleks võimalik iga sammu järel võrrelda ja testida.

FXML-vaateid saab visuaalselt kujundada Gluon Scene Builderiga ning IntelliJ IDEA saab Scene Builderi välise tööriistana avada. Scene Builderit kasutatakse eelkõige uute või selgelt eraldatud vaadete, näiteks käivitusvaate ja dialoogide puhul. Kogu praegust programmiliselt ehitatud kasutajaliidest ei kirjutata ühe suure muudatusena FXML-i ümber; FXML-i viiakse ainult need osad, mille hooldatavus sellest päriselt paraneb.

### Vastuvõtukriteeriumid

- põhiaken ja dialoogid kasutavad sama värvi-, vahe- ja komponendisüsteemi;
- kõik tegevused jäävad kasutatavaks nii hiire kui ka klaviatuuriga;
- fookuse, hõljumise, vajutamise, valiku ja keelatud olekud on selgelt nähtavad;
- kujundus töötab vähemalt Linuxi ja Windowsi tavapäraste süsteemiskaaladega;
- visuaalne uuendus ei muuda plaaniandmeid, `.pplan` vormingut ega olemasolevaid töövooge;
- enne suuremat ümberkujundamist säilitatakse võrdluspildid peamistest vaadetest.

## 20. Arenduspõhimõtted

- Iga ülaltoodud tervik tehakse eraldi väikeste commit'ide jadana.
- Domeeniarvutused jäävad `planner-core` moodulisse ja JavaFX-i esitlus `planner-gui` moodulisse.
- Olemasolevaid versioonita, versioon 1 ja versioon 2 `.pplan` faile peab saama edasi avada.
- Kasutajaliidese muudatus ei tohi vaikimisi muuta plaani salvestatud andmeid.
- Automaatkontrollid tehakse taustal; graafiline käsitsi kontroll antakse kasutajale lühikese kontrollnimekirjana.
- Pärast iga sammu käivitatakse `./gradlew clean test` ja `git diff --check`.

## 21. Objekti pööramine ja mitme objekti valimine

**Seis 27. augusti õhtul:** põhifunktsioonid on teostatud ja käsitsi kontrollitud. Allolev kirjeldus säilib funktsiooni nõuete ning järelejäänud töö piiritlemiseks.

### Interaktiivne pööramine

Objekti või objektirea kontekstimenüüst „Pööra” alustades kuvatakse objekti kõrval lohistatav pööramispunkt. Pööramispunkti liigutamine muudab objekti nurka hiire asukoha järgi ning jätab objekti keskpunkti paigale. Sama töövoog peab olema kättesaadav ka objektide külgpaneeli kontekstimenüüst. Pööramise lõpetab hiirenupu vabastamine või `Escape`; pööramise alustamiseks lisatakse kiirklahv `Ctrl + R`, kui see ei ole mõne olemasoleva tegevusega vastuolus.

Pööramispunkt peab skaleeruma koos kaardiga, olema piisavalt nähtav ka väikese mõõtkava korral ning mitte muutuma objekti püsivaks eraldi alamobjektiks. Üks pööramisliigutus moodustab ühe undo-sammu. Paigutuslukustus või objekti enda lukustus blokeerib pööramise, kuid ei tohi takistada objekti valimist ega andmete muutmist.

### Mitme objekti valimine

`Ctrl`-klahvi all hoides kaardil või objektide külgpaneelil tehtud klõps lisab objekti valikusse või eemaldab selle sealt. Tavaline klõps alustab uue ühe objekti valiku. Mitme valiku korral on ühistoimingud vähemalt kopeerimine, kleepimine, kustutamine, lukustamine või lukustusest vabastamine, peitmine või kuvamine, nimesildi nähtavuse muutmine ja ühise grupi määramine.

Ühistoiming peab arvestama objektitüüpi ja lukustust:

- lukustatud objekte ei kustutata ega muudeta enne nende teadlikku lukustusest vabastamist;
- geomeetriat muutvad tegevused peavad kas rakenduma ainult sobivatele valitud objektidele või olema selgelt keelatud, mitte osaliselt ja vaikides;
- eri tüüpi objektide puhul kuvatakse ainult omadused, millel on kõigile valitutele sama tähendus;
- kopeerimine loob igast valitud objektist eraldi koopia ning säilitab nende omavahelise suhtelise paigutuse;
- mitme objekti valik on kasutajaliidese olek ega muuda `.pplan` vormingut.

### Valikukast

Kui kasutaja hoiab `Ctrl`-i all ja lohistab kaardil vasaku hiirenupuga, kuvatakse valikukast. Kastiga lõikuvad või täielikult kaetud nähtavad objektid lisatakse valikusse; täpne reegel tuleb valida nii, et aiaridade ja teiste väikeste objektide valimine oleks etteaimatav. Valikukast ei tohi käivituda aktiivse lisamis-, mõõtmis-, kaabli- või pööramistööriista ajal ning tavaline hiirega kaardi lohistamine peab säilitama senise käitumise.

Valitud objektid peavad olema kaardil ja külgpaneelil ühtemoodi märgatavad. `Escape` tühistab poolelioleva valikukasti ning mitme valiku tühistamiseks saab teha tavalise klõpsu tühjal kaardialal. Kõik valikupõhised tegevused peavad olema kasutatavad ka klaviatuurita hiirega ning nende olekud peavad olema nähtavad.

Valitud objektid on külgpaneelis tugevamalt eristatavad, kaardil ümbritseb mitmikvalikut ühine tagasihoidlik piirjoon ning loendi all kuvatakse aktiivsete objektide arv. Külgpaneelis valib `Ctrl + Shift + M1` viimati valitud objektirea ja klõpsatud objektirea vahele jäävad nähtavad objektid. `Ctrl + M1` grupipäisel valib grupi parajasti nähtavad ja otsingule vastavad objektid ka siis, kui grupp on kokku pakitud. Need 27. augustil lisatud mugavused ootavad veel käsitsi kontrolli.

### Järelejäänud töö

- kontrollida käsitsi grupipäise valikut, vahemikuvalikut, valitud objektide arvu ning kaardi ja külgpaneeli esiletõsteid;
- kontrollida käsitsi kogu valikule ühise grupi ja nimesildi nähtavuse määramist;
- kontrollida käsitsi `Escape`-klahvi ning tühjal kaardil klõpsamisega mitmikvaliku lõpetamist.

### Vastuvõtukriteeriumid

- pööramispunkt muudab valitud objekti nurka loomulikult ja jätab keskpunkti paigale;
- pööramine töötab nii kaardil kui ka külgpaneeli kontekstimenüüst alustades;
- `Ctrl`-klõpsuga saab valida ja valikust eemaldada eri tüüpi objekte;
- lukustatud objektide puhul ei teki vaikset osalist ühistoimingut;
- valitud objektide kopeerimine säilitab nende suhtelise paigutuse;
- valikukast võimaldab välja zoomitud kaardil valida aiaridade ja teiste väikeste objektide kogumeid;
- valik, pööramine ja ühistoimingud säilitavad undo/redo ning olemasolevate `.pplan` failide käitumise.
