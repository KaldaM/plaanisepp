# Rakenduse edasine arendusplaan

- Koostatud: 20. august 2026
- Lähtepunkt: commit `e53fb3a` (`Extract marker icon factory from application`)

## Eesmärk

Rakendus ei ole enam ainult pannkoogihommiku töövahend. Edasine arendus peab toetama eri organisatsioonide ja eri tüüpi ürituste alaplaanide koostamist, säilitades olemasolevate `.pplan` failide avatavuse ning elektri- ja kaabliplaneerimise tugevused.

## Soovituslik tööjärjekord

1. lisada enne sügist alajaotuskilbid ning seadmete vaiketoide ja seadmepõhised toitevalikud;
2. kujundada elektri kokkuvõte uue mudeli põhjal interaktiivseks ning lisada väljundite koormusribad;
3. lisada külgpaneeli ja objektide kontekstimenüüd, üksikobjektide nähtavus ning külgpaneeli jaotiste järjestamine;
4. lisada objektide kiirotsing kahekordse Shift-klahviga;
5. asendada suumi `+` ja `-` nupud liuguriga ning lisada `Alt + hiirerull`;
6. lisada rakenduse käivitamisel hiljutiste plaanide ja uue plaani loomise avavaade;
7. jätkata `PlaaniseppApp` refaktoreerimist väikeste, funktsioonidega seotud sammudena.

Rakenduse nimeks valiti 20. augustil 2026 **Plaanisepp**. Nimi kirjeldab plaanide meistrit ja seostub ka 1927. aastal talletatud Lõuna-Eesti nimekujuga „Plaani sepp”.

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

Edaspidi peab „Muuda” saama avada objekti andmed eraldi dialoogis, mis sisaldab sisuliselt samu välju ja tegevusi nagu praegune „Valitud objekt” jaotis. Nii saab kasutaja valida kompaktsema külgpaneeli töövoo ilma objekti muutmise võimalusi kaotamata. Dialoog ja külgpaneel peavad kasutama ühist rakendusloogikat, et valideerimine, elektriühendused ja muudatused ei hakkaks eri vaadetes erinevalt käituma.

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

## 8. Arenduspõhimõtted

- Iga ülaltoodud tervik tehakse eraldi väikeste commit'ide jadana.
- Domeeniarvutused jäävad `planner-core` moodulisse ja JavaFX-i esitlus `planner-gui` moodulisse.
- Olemasolevaid versioonita, versioon 1 ja versioon 2 `.pplan` faile peab saama edasi avada.
- Kasutajaliidese muudatus ei tohi vaikimisi muuta plaani salvestatud andmeid.
- Automaatkontrollid tehakse taustal; graafiline käsitsi kontroll antakse kasutajale lühikese kontrollnimekirjana.
- Pärast iga sammu käivitatakse `./gradlew clean test` ja `git diff --check`.
