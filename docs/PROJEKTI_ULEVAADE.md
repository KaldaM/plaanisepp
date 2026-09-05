# Plaanisepp: eesmärgid, areng ja hetkeseis

- Dokumendi viimane sisuline uuendus: 5. september 2026
- Koodi viimane dokumenteeritud commit: `d3efb47` (`Reuse object group controls for cables`, 5. september 2026)
- Projekti versioon: `0.7.2`

## 1. Dokumendi eesmärk

See dokument on projekti ühine lähtepunkt kolmel otstarbel:

1. anda bakalaureusetöö jaoks ülevaade probleemi kujunemisest, valikutest ja tehtud tööst;
2. säilitada projekti mälu ka siis, kui senine arendusvestlus pole enam kättesaadav;
3. aidata uuel arendajal või uuel vestlusel kiiresti aru saada, mis on valmis, mis pooleli ja mida tasub järgmisena teha.

Dokumenti tuleks uuendada pärast suurema funktsionaalse terviku valmimist või enne pikemat arenduspausi. Commitide ajalugu jääb detailseks tehniliseks logiks; see fail selgitab muudatuste tähendust ja omavahelisi seoseid.

## 2. Projekti taust

Projekt sai alguse Pythonis loodud pannkoogihommiku planeerijast, mille põhiprogramm oli sisuliselt ühes failis. Java versiooni loomise eesmärk ei olnud ainult olemasoleva programmi ümberkirjutamine, vaid selle valdkonna modelleerimine objektorienteeritult ning sellise struktuuri loomine, mida saaks kasvatada märksa suuremaks ürituste planeerimise rakenduseks.

Struktuurilise eeskujuna kasutati varasemat tarkvaratehnika Java projekti, kus domeeniloogika ja kasutajaliides olid eristatavad. Pannkoogihommik on esimene päriselt kasutatav juhtum: piisavalt konkreetne, et kasutusprobleemid tuleksid kiiresti välja, kuid piisavalt mitmekesine, et katsetada kaarti, objekte, elektriühendusi, kaableid, salvestamist ja eksporti.

Pikemas vaates on projekt bakalaureusetöö prototüüp ja õppematerjal. Praegune töölauarakendus aitab välja selgitada:

- milliseid mõisteid ürituse alaplaani domeen vajab;
- millised töövood on kaardipõhises kasutajaliideses loomulikud;
- kuidas siduda ruumiline plaan elektrivõrgu andmetega;
- millised andmed peavad säilima ja olema teistele eksporditavad;
- mida tuleks suuremas süsteemis arhitektuuriliselt teisiti teha.

## 3. Algsed eesmärgid

### 3.1 Plaanisepa põhieesmärk

Rakendus peab võimaldama koostada ürituse alaplaani, kus kasutaja saab:

- valida või laadida aluskaardi;
- paigutada kaardile telke ja elektrikappe;
- lisada telkidesse voolu vajavaid seadmeid;
- ühendada telgi konkreetse elektrikapi konkreetse väljundiga;
- näha väljundite ja kappide koormust ning vaba võimsust;
- eristada telke värvi ja grupi järgi;
- kujundada kaablite tegelikke trajektoore;
- salvestada töö ning hiljem samast kohast jätkata;
- eksportida plaan teistele arusaadavasse vormi.

Oluline lõppnõue on see, et tavakasutaja ei peaks rakenduse käivitamiseks paigaldama IntelliJ IDEA-t, Javat ega kasutama käsurida. Selle jaoks on olemas sihtplatvormil loodavad Windowsi EXE- ja Fedora RPM-paigaldajad ning iseseisvad rakendusepildid, mis sisaldavad rakenduse enda Java runtime'i. Windowsi ja Linuxi paketid koostatakse ning kontrollitakse eraldi, sest `jpackage` loob paketi sellel platvormil, kus build käivitatakse.

### 3.2 Bakalaureusetöö suurem visioon

Plaanisepa kohal on laiem ürituste tehnilise ja korraldusliku planeerimise süsteemi idee.

Alus võiks olla kas:

- kaardilt valitud ja lukustatud geograafiline asukoht;
- kasutaja laaditud skeem või pilt, mille mõõtkava kalibreeritakse teadaoleva vahemaa järgi.

Plaanile kavandatud tehnilised objektid:

- elektriallikad ja elektrikilbid eri tüüpi ning eri arvu väljunditega;
- telgid muudetava suuruse, pöörde, värvi, seadmete ja elektriühendustega;
- alajaotuskilbid, mis on korraga tarbijad ja uued vooluallikad;
- kõlarid suuna, tüübi, võimsuse ja vooluvajadusega;
- eraldiseisvad elektritarbijad;
- 230 V, 16 A, 32 A ja 63 A kaablid muudetava trajektoori ning kaablijuppide loeteluga;
- helipuldid ja XLR-kaablid;
- vabalt seadistatavad kujundid ja märkmed.

Korraldajale kavandatud objektid ja võimalused:

- vabakujulised alad ehk polygonid, näiteks publiku- või toitlustusalad;
- vabakujulised jooned ehk polüjooned, näiteks aiad ja liikumistrajektoorid;
- WC, turva, saun või tünnisaun, infotelk ning start/finiš;
- tekstikastid;
- liikmed koos ülesannetega;
- sponsoraiad, bännerid ja lipud;
- lihtne korraldajavaade ning eraldi tehniline vaade.

Veel kaugemad ideed:

- Tartu linna aluskaartide või linnavõrgu andmete otsene kasutamine;
- plaani ainult vaatamiseks mõeldud veebivaade;
- kasutajakontod ja organisatsioonid;
- festivalide ning nende alaplaanide kaustastruktuur;
- administraatori- ja muutmisõigused;
- erilahendus pubiralli trajektooride planeerimiseks;
- hele ja tume kujundus.

Need ei ole praeguse prototüübi lubatud funktsioonid, vaid suurema süsteemi ulatus, mille jaoks prototüüp teadmisi kogub.

## 4. Arenduspõhimõtted

Arendus on toimunud teadlikult väikeste sammudena. Üks kasutaja jaoks kontrollitav muudatus tehakse valmis, käivitatakse, proovitakse päris plaanil ning commititakse eraldi. Selline tööviis on seni andnud 364 commiti ja võimaldab näha, miks iga funktsioon lisati või ümber tehti.

Olulisemad kujunenud põhimõtted:

- kasutaja päris töövoog on olulisem kui esialgne tehniline lahendus;
- juba salvestatud vanad plaanid peavad pärast andmemudeli täiendamist edasi avanema;
- sagedased tegevused peavad vajama võimalikult vähe dialooge ja kinnitusi;
- harva muudetavad plaaniülesed valikud kuuluvad eraldi „Plaani andmed” dialoogi;
- kaardil peab saama objekte valida ja muuta ilma külgpaneeli tarbetu hüppamise või ülerahvastamiseta;
- lukustus kaitseb objekti asukohta, kuid ei tohi keelata selle andmete muutmist;
- automaatne rakendamine sobib lihtsatele tekstiväljadele, kuid ohtlik tegevus vajab kinnitust;
- kaardil olev tekst peab olema loetav nii tavakaardil kui ortofotol;
- keerukad kujundid luuakse punktide järjestikuse lisamisega ning neid muudetakse pärast loomist samade punktide kaudu.

## 5. Tehniline ülesehitus

### 5.1 Tehnoloogiad

- Java 25 LTS
- JavaFX 26.0.2
- Gradle Wrapper ja mitme mooduliga Gradle'i projekt
- Apache PDFBox 2.0.31 PDF-raportite loomiseks
- JUnit Jupiter 5.10.2 testide taristuna

Java 25 valiti praeguse pika toe versioonina. JavaFX 26 kasutamine võimaldab säilitada ajakohasema töölauaintegratsiooni, sealhulgas parandused Linuxi KDE murdskaleerimisele ja modaaldialoogide avamisel maksimeeritud akna suuruse säilimisele. JavaFX 26 nõuab vähemalt Java 24 runtime'i; projekt kasutab ühtse arendus- ja käituskeskkonna jaoks Java 25 tööriistaahelat.

### 5.2 Moodulid

`planner-core` sisaldab rakenduse domeenimudelit ja teenuseid:

- plaani objektid ja nende ühised omadused;
- elektriallikad, väljundid, tarbijad ja ühendused;
- koormuste kokkuvõtted;
- `.pplan` faili salvestamine ja avamine.

`planner-gui` sisaldab JavaFX-i kasutajaliidest:

- tööriistariba, külgpaneelid ja dialoogid;
- kaardi renderdamine, suumimine ja nihutamine;
- objektide ning nende siltide interaktsioonid;
- mõõdulint ja trajektooride redaktorid;
- TXT-, PNG- ja PDF-eksport.

Sama mooduli Gradle'i pakendamisülesanded loovad Java 25 `jpackage` abil platvormipõhised iseseisvad rakendusepildid ning Windowsi EXE- ja Fedora RPM-paigaldajad. `PlaaniseppLauncher` on pakendatud rakenduste käivitusklass ja edastab avamisel saadud `.pplan` failitee olemasolevale rakenduse laadimisvoole.

`planner-gui` sõltub Gradle'is tavapäraselt `planner-core` moodulist. Core kompileeritakse eraldi teegiks ning selle lähtekoode ei kaasata GUI moodulisse teist korda.

Ekspordi- ja kaabliloogikat on peamisest kasutajaliidese klassist juba eraldi abiklassidesse tõstetud. Plaanifaili lugemise ja kirjutamise käivitamine ning aktiivse faili ja viimati kasutatud kausta seisund on koondatud `PlanFileSession` klassi. Salvestamata muudatuste seisund ning sellest tuletatud akna pealkiri ja salvestusoleku tekst asuvad `PlanDocumentState` klassis. Plaanifaili valimise ja salvestamata muudatuste kinnitamise JavaFX-i dialoogid asuvad `PlanFileDialogs` klassis, plaani üldandmete sisestusdialoog `PlanSettingsDialog` klassis ning uue objekti omaduste sisestamine `PlacementDetailsDialog` klassis. Markerite ikoonide JavaFX-kujundid loob `MarkerIconFactory`. Sellest hoolimata on `PlaaniseppApp` endiselt väga suur ning vajab edasise kasvu eel vaadeteks, kontrolleriteks ja tööriistadeks jagamist.

### 5.3 Olulisemad domeeniklassid

| Klass | Vastutus |
| --- | --- |
| `EventPlan` | Plaani nimi, kaart, mõõtkava, objektid, vooluühendused ja kihtide nähtavus |
| `PlannerObject` | Kõigi objektide ID, nimi, asukoht, grupp, märkmed, lukustus ja nimesilt |
| `Tent` | Mõõtmed, pööre, värv, seadmed ja summaarne vooluvajadus |
| `PowerSource` | Elektrikapp ja selle väljundite loetelu |
| `DistributionPanel` | Korraga tarbija ja vooluallikas, mille koormus tuleneb allavoolu ühendustest |
| `PowerOutlet` | Nimi, ühenduse tüüp ja lubatud võimsus |
| `PowerConnection` | Püsiv ID, allikas, tarbija, väljund, vaiketoite roll, kaabli tüüp, märkmed, jupid ja trajektoor |
| `Equipment` | Püsiv ID, nimi, vooluvajadus ja valikuline viide vaiketoidet asendavale vooluühendusele |
| `EquipmentContainer` | Ühine leping seadmeid hoidvatele ja voolu tarbivatele objektidele |
| `PowerConnectable` | Ühine leping tarbija vooluvajadusele ja muudetavale ühenduspunktile |
| `CustomObject` | Ristkülik või ring muudetava suuruse, pöörde, värvi ja läbipaistvusega |
| `TextObject` | Mitmerealine tekstikast, pealkiri, värv ja kirjasuurus |
| `MarkerObject` | Ikooniga objekt, näiteks WC, saun, liige, turva või start/finiš |
| `AreaObject` | Punktidest koosnev värviline ja läbipaistev ala |
| `LineObject` | Punktidest koosnev vabakujuline joon |

### 5.4 Elektrimudel

Voolutarbijad on `Tent`, `AreaObject` ja `LineObject`. Nende seadmete võimsused liidetakse ning objekt ühendatakse ühe elektrikapi ühe konkreetse väljundiga. Väljundi koormus arvutatakse selle külge ühendatud objektide võimsustest. Kõigil kolmel tarbijatüübil saab kaardil määrata füüsilise vooluühenduse punkti.

Toetatud ühendused ja algsed vaikemahud:

| Ühendus | Vaikemaht |
| --- | ---: |
| 230 V tavapesa | 3500 W |
| 16 A tööstusvool | 11000 W |
| 32 A tööstusvool | 22000 W |
| 63 A tööstusvool | 43500 W |

Need väärtused on planeerimise praktilised vaikeväärtused, mitte elektriprojekti asendus. Kasutaja saab iga väljundi nime, tüüpi ja mahtu muuta. Ülekoormus tuuakse kokkuvõttes nähtavalt esile.

### 5.5 Salvestusvorming

Plaan salvestatakse ühe `.pplan` failina. Praegune vorminguversioon on `26` ja faili sisemine kuju on ZIP-pakett:

| Paketi kirje | Sisu |
| --- | --- |
| `manifest.properties` | Paketi vorming, versioon ja sisufailide viited |
| `plan.properties` | Plaani struktureeritud andmed |
| `assets/map.png` või `assets/map.jpg` | Kasutaja valitud kaart, kui plaan seda kasutab |

Plaaniandmetes salvestatakse muu hulgas:

- plaani nimi, kaardi viide ja mõõtkava;
- siltide kirjasuurused;
- kõik objektid ning nende tüübipõhised omadused;
- grupid, lukud, märkmed ja nimesiltide asukohad;
- elektriväljundid ja vooluühendused;
- kaablite trajektoorid, märkmed, jupid ja sildiasukohad;
- kihtide, kaablitüüpide, gruppide ja üksikobjektide nähtavus.

Kasutaja laaditud PNG- või JPEG-kaart lisatakse paketti binaarfailina, mitte Base64 tekstina. Projektiga kaasas olev vaikekaart jääb `classpath:` viiteks ja seda paketis ei dubleerita. Pärast edukat salvestamist saab plaani uuesti salvestada ka siis, kui algne kasutaja kaardifail on ümber nimetatud, teisaldatud või kustutatud.

Versioonita ja versioon 1 `.pplan` failid on tavalised Java properties-failid ning versioon 2 on esimene ZIP-paketivorming. Hilisemad versioonid on lisanud muu hulgas seadme- ja ühendusepõhise elektrimudeli, checklisti, aiavõrgud, inventari, seotud tekstid, aluskaardid ning korraldajavaate andmed. Kõik varasemad versioonid avanevad endiselt. Vana faili avamine seda ei muuda; järgmine salvestamine kirjutab faili praeguse vorminguversiooniga. Rakendus keeldub endast uuema vormingu avamisest ja palub kasutajal rakendust uuendada. Vigane või poolik pakett valideeritakse enne plaani kasutuselevõttu ning ebaõnnestunud salvestus ei kirjuta olemasolevat sihtfaili osaliselt üle.

### 5.6 Tähtsamad teenused ja GUI komponendid

| Komponent | Vastutus |
| --- | --- |
| `PlanFileService` | Praeguse versiooni 26 pakettide kirjutamine, varasemate pakettide valideeritud lugemine ning vanade versioonita ja versioon 1 failide lugemine |
| `PlanFactory` | Uue plaani algseisu loomine |
| `PowerSummaryService` | Elektrikappide koormuse ja vaba võimsuse arvutamine |
| `GeometryCalculator` | Joonte pikkuse ning kujundite pindala ja ümbermõõdu arvutamine |
| `PlanFileSession` | Plaanifaili laadimise ja salvestamise vahendamine ning aktiivse faili ja viimati kasutatud kausta seisund |
| `PlanDocumentState` | Salvestamata muudatuste seisund ning akna pealkirja ja salvestusoleku teksti moodustamine |
| `PlanFileDialogs` | Plaanifaili avamis- ja salvestusdialoogid ning salvestamata muudatuste valiku küsimine |
| `PlanSettingsDialog` | Plaani nime, mõõtkava, sildisuuruste ja kaardipildi valikute sisestusdialoog |
| `PlacementDetailsDialog` | Uue objekti tüübiomaduste sisestamine ja sisendi valideerimine enne kaardile paigutamist |
| `MarkerIconFactory` | Markeriliikidele vastavate JavaFX-ikoonide loomine |
| `PlaaniseppApp` | Rakenduse põhivaade, tööriistad, dialoogid ja kasutaja tegevuste sidumine mudeliga |
| `PlaaniseppLauncher` | Pakendatud rakenduse käivitamine ja käsurealt või failiseosest saadud plaanitee edastamine |
| Gradle'i `package*` ülesanded | Platvormipõhiste rakendusepiltide ja paigaldajate loomine koos runtime'i, JavaFX-i, ikoonide ning failiseostega |
| `CablePathHelper`, `CableRouteEditor`, `CableRouteGeometry` | Voolukaabli tee moodustamine ning vahepunktide muutmise loogika |
| `CableDisplayHelper`, `CablePolylineHelper` | Kaablite visuaalne esitus ja reaalajas uuendamine |
| `ReportTextExporter`, `PdfReportExporter` | Teksti- ja PDF-aruannete loomine |
| `MapImageSnapshotter` | Kaardivaate ettevalmistamine pildi- ja PDF-ekspordiks |
| `ExportOptionsDialog`, `ExportFileChooser`, `ExportFileNames` | Ekspordi valikud, sihtfail ja ühtsed failinimed |

### 5.7 Platvormiülene seis

- Projekt kasutab Java 25, JavaFX-i, Gradle'it ning `java.nio.file` API-t; rakenduse domeeniloogika ei sõltu teadlikult Windowsi-spetsiifilisest API-st.
- Repository sisaldab Gradle Wrapperi käivitajaid nii Windowsile kui Unixilaadsetele süsteemidele.
- `planner-gui` sõltub `planner-core` moodulist Gradle'i projektisõltuvuse kaudu; core kompileeritakse eraldi teegiks.
- Kasutaja eelistused salvestatakse Java `Preferences` API kaudu, mille tegeliku asukoha valib operatsioonisüsteem.
- Rakenduse käivitamist ja põhilisi JavaFX-i töövooge on kontrollitud nii Linuxis kui Windowsis; platvormipakettide lõplik kontroll tuleb teha iga release'i järel sihtsüsteemil.
- GitHub Actions loob release'i tag'i põhjal Windowsi EXE-paigaldaja, Fedora RPM-i ja Linuxi rakendusearhiivi koos rakendusega kaasas oleva Java runtime'iga.

## 6. Praeguseks saavutatud funktsionaalsus

### 6.1 Plaan ja aluskaart

- Rakendus avaneb käivitusvaates, kus saab valida hiljutise plaani või luua uue; vaikimisi kasutatav korraldajavaade säilib lokaalselt.
- Kasutada saab projektiga kaasas olevat tavakaarti ja ortofotot.
- Kasutaja saab laadida oma PNG- või JPEG-kaardi.
- Plaanile saab anda nime; nimi on rakenduses nähtav.
- Mõõtkava saab sisestada pikslite arvuna meetri kohta või määrata mõõdulindi järgi.
- Mõõtkava muutmisel uuenevad olemasolevate mõõdulintide näidud.
- Kaarti saab suumida, nihutada ja taastada 100% suurusele.
- Suumitud kaardi kõik servad on ligipääsetavad.

### 6.2 Objektide lisamine ja haldamine

- Telgid, elektrikapid, tavaobjektid, tekstikastid, ikoonmarkerid, jooned ja alad.
- Lisamisel küsitakse samas dialoogis objekti nimi, grupp ning tüübile vajalikud põhiomadused.
- Objekti lisamine talub hiire väikest liikumist, et kõrge DPI ei muudaks klõpsu kogemata kaardi lohistamiseks.
- Objekte saab valida kaardilt või objektide nimekirjast.
- Nimekirjast valitud objekt tuuakse kaardil nähtavale ja selle juurde saab vaate tsentreerida.
- Objekte saab liigutada, dubleerida ja kinnitusega kustutada.
- Lukustatud objekti ei saa liigutada ega kustutada, kuid selle andmeid saab muuta.
- Objekte saab gruppidesse määrata ja gruppide nähtavust muuta.
- Objekti nimesilti saab eraldi peita, lohistada ja vaikeasukohta taastada.
- Kõik objektisildid saab plaani kihina korraga välja lülitada.
- Siltidel on kontrastne taust, et need oleksid loetavad ka ortofotol.

### 6.3 Telgid ja seadmed

- Telgi laius, pikkus, pööre ja värv on muudetavad; läbipaistvust saab määrata slideriga.
- Telki saab lisada nime ja võimsusega seadmeid ning neid eemaldada.
- Telgi vooluvajadus arvutatakse seadmete summana.
- Telgi dubleerimisel kopeeritakse seadmed, kuid mitte elektriühendus.

### 6.4 Elektrikapid ja ühendused

- Kappi saab lisada eri tüüpi väljundeid.
- Väljundil on muudetav nimi, ühenduse tüüp ja võimsus.
- Ühendatud väljundi muutmisel või eemaldamisel kaitsevad hoiatused olemasolevaid seoseid.
- Telgi, ala või joone saab ühendada konkreetse väljundiga külgpaneelist või valida kapi otse kaardilt.
- Kui sobiva tüübiga väljund on üheselt valitav, saab süsteem selle automaatselt määrata.
- Valikus näidatakse ainult konkreetses kapis päriselt olemas olevaid ühendusetüüpe ja väljundeid.
- Koormust arvestatakse väljundi, mitte ainult kogu kapi tasemel.
- Ülekoormatud väljundid on kokkuvõttes nähtavad.

### 6.5 Voolukaablid

- Ühendatud tarbija ja kapi vahel kuvatakse kaabel.
- Kaabli tüüp tuleneb valitud elektriühendusest.
- Kaablile saab lisada vahepunkte ning kujundada tegeliku trajektoori.
- Vahepunkte saab reaalajas lohistada, lõigule lisada ja paremklõpsuga eemaldada.
- Punktide muutmine ei nihuta samal ajal kaardivaadet.
- Kaabli tegelik pikkus arvutatakse mõõtkava järgi.
- Valitud ühenduse tarbijapoolset ühenduspunkti saab kaardil lohistada; kaabel ja pikkus uuenevad juba lohistamise ajal.
- Ühenduspunkt paikneb objekti suhtes, liigub objektiga kaasa ning selle saab paremklõpsuga keskpunkti lähtestada.
- Eraldi saab märkida olemasolevate kaablijuppide kombinatsiooni, näiteks `20 m + 10 m + 10 m`.
- Kaablisildid on lühikesed, lohistatavad, peidetavad ja lähtestatavad.
- Kaableid saab filtreerida 230 V, 16 A, 32 A ja 63 A tüübi järgi.
- Kokkuvõte koondab kaablite pikkused ja jupid tüübi kaupa.

### 6.6 Vabakujulised jooned ja alad

- Joon luuakse kaardile järjest punkte lisades.
- Ala luuakse vähemalt kolmest järjest lisatud punktist.
- Loomise ajal kuvatakse kujuneva objekti eelvaade.
- Loomise saab lõpetada nupu või Enter-klahviga ja katkestada Escape-klahviga.
- Olemasolevaid punkte saab lohistada ja paremklõpsuga eemaldada.
- Punktide vahel kuvatakse väiksemad vahepunktid; nende lohistamisel lisatakse kujundisse uus pärispunkt.
- Lukustatud joone või ala geomeetriat muuta ei saa.
- Ala värvi saab muuta ning läbipaistvust saab määrata slideriga.
- Joone värvi ja paksust saab määrata nii loomisel kui hiljem; paksust muudetakse slideriga.

Jooned ja alad kasutavad telgiga sama seadmete ning vooluühenduste mudelit. Nende geomeetria ja elektriühenduste JavaFX-i hiirekäitumine vajab endiselt terviklikku käsitsi regressioonikontrolli.

### 6.7 Korraldaja objektid

- Tavaobjekti saab näidata ristküliku või ringina ning muuta selle mõõtmeid, pööret ja värvi; läbipaistvust saab määrata slideriga.
- Tekstiobjektil on rasvases kirjas pealkiri, mitmerealised märkmed, värv ja slideriga muudetav kirjasuurus.
- Plaani objekti- ja kaablisiltide üldist kirjasuurust saab muuta slideritega „Plaani andmed” dialoogis.
- Markerid kasutavad teksti asemel eristatavaid ikoone.
- Olemas on vähemalt WC, turva, start/finiš, saun/tünnisaun ja liikme tüübid.
- Objektide nimekiri näitab objektide värve ning toimib seetõttu ka lihtsa legendina.

### 6.8 Kihid, külgpaneel ja kokkuvõtted

- Objektitüüpe, silte, kaableid, kaablitüüpe ja gruppe saab eraldi näidata või peita.
- Kihtide nähtavus säilib plaani salvestamisel ja avamisel.
- Kõik kihid saab korraga sisse või välja lülitada.
- Objektiloend asub detailide kohal ning selle kõrgust saab lohistades muuta.
- Objektiloendi kasutaja valitud kõrgus jäetakse rakenduste vahel meelde.
- Külgpaneel kuvab ainult valitud objektitüübile asjakohaseid välju.
- Telgi, ala ja joone seadmed ning kapi väljundid paiknevad vastava objekti detailide juures.
- Voolu-, kaabli- ja grupikokkuvõtteid saab ükshaaval sisse ja välja lülitada.

### 6.9 Salvestamine ja eksport

- Uue tühja plaani loomine.
- Plaani salvestamine, „Salvesta kui” ja olemasoleva `.pplan` faili avamine.
- Viimati kasutatud faili ja kausta meelespidamine.
- Salvestamata muudatuste nähtav olek.
- Enne uue plaani loomist, teise plaani avamist või rakenduse sulgemist pakutakse muudatuste salvestamist.
- Varem loodud plaanifailid on püsinud uute versioonidega avatavad.
- Uued failid salvestatakse `formatVersion=28` ZIP-pakettidena; versioonita vanad failid loetakse versiooniks 1, toetatud vanemad vormingud teisendatakse lugemisel ning uuema tundmatu versiooni avamine lõpetatakse selge veateatega.
- Tekstiraporti eksport.
- Kaardipildi eksport PNG-na valitava ulatusega.
- PDF-eksport koos ühes dialoogis valitavate sisu- ja kompaktsusvalikutega.
- Eksporditavate failide nimed tuletatakse ühtlaselt plaani nimest.

## 7. Arenduse kronoloogia

Allolev ajajoon koondab 364 commitist tähenduslikud etapid. Täpne muudatuste loetelu on käsuga `git log --reverse --oneline`.

### 1. juuli 2026: alus ja esimene töötav vertikaallõige

- Loodi Gradle'i mitme mooduliga JavaFX-i projekt.
- Eraldati domeenimudel ja kasutajaliides.
- Lisati objektide detailpaneel, telgi värv ja seadmed.
- Loodi esimene vooluallika valik ja tarbijate kokkuvõte.
- Lisati kaardipildi laadimine ning vaikimisi ortofoto.

### 2. juuli 2026: põhiplaneerija valmimine

- Lisati kustutamine, lukustamine, telgi mõõtmed ja pööre.
- Valmis mõõdulint, suum ning nende esimesed parandused.
- Lisati elektriühendused, kaablipikkus ja kaardilt kapi valimine.
- Lisati grupid, kaardisildid ja grupifiltrid.
- Nähtavusseaded hakkasid plaanis säilima.
- Lisati tekstieksport, plaani nimi ja uus tühi plaan.
- Telke ja kappe sai hakata kaardiklikiga lisama.
- Elektrikapi mudel arenes konkreetsete nimetatud väljundite, ühendusetüüpide ja ülekoormuse arvestuseni.
- Lisati turvalisem salvestamata muudatuste käsitlus.

### 3.–4. juuli 2026: üldobjektid ja külgpaneeli korrastamine

- Parandati eestikeelse teksti kodeeringut.
- Lisati üldised ruudu- ja ringikujulised objektid.
- Nende välimus, suurus ja pööre muutusid seadistatavaks.
- Külgpaneel jagati tüübipõhisteks ja kokkupandavateks osadeks.
- Kokkuvõtteid sai eraldi sisse ja välja lülitada.

### 6. juuli 2026: kaablite esimene tervik

- Elektriühendused muutusid kaardil nähtavateks kaabliteks.
- Lisati kaabli- ja sildikihid, legend ning valiku esiletõstmine.
- Lisati kaablimärkmed ja kaablijuppide kirjeldus.
- Tekstiväljade muudatused hakkasid sobivates kohtades automaatselt rakenduma.
- Tööriistariba ja salvestusolek muudeti selgemaks.
- Kaablikokkuvõte hakkas arvutama pikkusi ja koondama juppe tüübi järgi.

### 12.–13. juuli 2026: päris kaablitrajektoor ja mõõtkava

- Lisati 63 A tööstusvool.
- Kaablile lisati muudetavad vahepunktid.
- Punktide lohistamine muutus reaalajas nähtavaks ning punktide lisamine ja eemaldamine mugavaks.
- Kaablimärkmed ja pikkuse märkmed eraldati.
- Lukustatud objektide kustutamine blokeeriti.
- Lisati plaani muudetav mõõtkava ja mõõdulindi järgi kalibreerimine.
- Plaani nimi, kaart ja mõõtkava viidi „Plaani andmed” dialoogi.

### 17. juuli 2026: lisamisvoog, markerid ja sildid

- Objektide lisamine koondati ühtsesse töövoogu.
- Enne paigutamist sai ühes dialoogis valida nime, grupi, värvi ja tüübipõhised omadused.
- Kõrge DPI-ga hiire väike liikumine ei hakanud enam klõpsamist rikkuma.
- Tekstiobjekt eraldati tavaobjektist.
- Lisati ikoonmarkerid ning markeritüüpide vaikevärvid.
- Kaardi- ja kaablisildid tehti loetavamaks ning lohistatavaks.

### 20.–21. juuli 2026: objektide haldus, eksport ja koodi tükeldamine

- Lisati objektide dubleerimine ja tüübipõhised kihid.
- Valmis värvide ning peidetud olekuga objektide nimekiri.
- Nimekirja kõrgus muutus kasutaja poolt lohistatavaks ja püsivaks.
- Lisati nimesiltide üldine kiht ja tekstiobjekti kirjasuurus.
- Plaani seadistustesse lisati objekti- ja kaablisiltide kirjasuurused.
- Valmis PNG-, täiustatud TXT- ja PDF-eksport.
- Ekspordi ning kaablite kuvamise loogikat hakati `PlaaniseppApp` klassist eraldi klassidesse tõstma.

### 24. juuli 2026: vabakujulised jooned ja alad

- Loodi ala- ja jooneobjektide domeenimudelid ning salvestamine.
- Lisati vastavad kaardikihid ja renderdamine.
- Punktide järjestikuse klõpsamisega lisamisvoog asendas ebaloomuliku valmis algkujundi.
- Lisati punktide lohistamine, eemaldamine ja vahepunktide kaudu juurde tekitamine.
- Pärast loomist sai muuta joone värvi.

### 14.–15. august 2026: mõõdud, üldistatud elektritarbijad ja failivormingu alus

- Joontele lisati pikkus ning aladele, ringidele ja ristkülikutele pindala ning ümbermõõt; mõõdud kuvatakse ka objektide nimekirjas.
- Telkide ja tavaobjektide läbipaistvus, joone paksus ning teksti- ja sildisuurused muudeti slideritega seadistatavaks.
- `Tent`, `AreaObject` ja `LineObject` ühendati ühise `EquipmentContainer` ning `PowerConnectable` mudeli alla.
- Ala ja joone seadmed, vooluvajadus, elektriühendused, kaablid, kokkuvõtted ja eksport üldistati telgiga samale loogikale.
- Tarbijatele lisati salvestatav ja kaardil lohistatav vooluühenduse punkt.
- `.pplan` versioon 1 sai selge `formatVersion` välja, versioonita failide tagasiühilduva lugemise ja uuema vormingu kontrolli.
- `planner-gui` hakkas core'i lähtekoodi uuesti kompileerimise asemel sõltuma korrektselt `planner-core` moodulist.

### 16. august 2026: Java uuendus ja kaasaskantav plaanipakett

- Projekt viidi Java 25 LTS-i ja JavaFX 26 peale ning põhilist käitumist kontrolliti Linuxis ja Windowsis.
- `.pplan` versioon 2 muudeti ZIP-paketiks, mis sisaldab struktureeritud plaaniandmeid ja kasutaja valitud kaardipilti.
- Versioonita ja versioon 1 properties-failide lugemine säilitati; vana fail uuendatakse alles kasutaja järgmisel salvestamisel.
- Paketile lisati manifesti, sisekirjete, suuruste, kaardipildi ja vorminguversiooni kontroll ning olemasolevat faili kaitsev ajutise faili kaudu salvestamine.
- Automaattestid katavad v1 migratsiooni, kaardiga ja kaardita v2 paketi, PNG- ja JPEG-kaardi, tundmatu tulevase versiooni ning valitud vigased paketid.

### 17. august 2026: iseseisva Linuxi rakendusepildi alus

- `planner-gui` moodulisse lisati Gradle'i ülesanne `packageLinuxAppImage`, mis loob `jpackage` abil iseseisva rakendusekausta.
- Rakendusepilt sisaldab Java 25 runtime'i, JavaFX-i, core'i ja kõiki muid käitusaegseid sõltuvusi; sihtarvutisse ei ole vaja eraldi Javat ega Gradle'it.
- Mitte-modulaarse rakenduse jaoks lisati eraldi käivitusklass, JavaFX kaasatakse runtime'i päris moodulitena ning runtime piirati rakenduse kasutatavate Java moodulitega.
- Lisati Fedora RPM-i koostamise ülesanne, mis loob menüükirjega iseseisva paigalduspaketi. Paigaldamine, menüüst käivitamine ja eemaldamine kontrolliti päris Fedora süsteemis.
- Rakendus oskab käivitamisel saadud `.pplan` failitee kohe avada. RPM registreerib vastava MIME-tüübi ja failiseose, et plaani saaks failihalduris topeltklõpsuga avada.
- Rakendusele, RPM-i menüükirjele ja `.pplan` failitüübile lisati ühine läbipaistva taustaga projektiikoon.
- Fedora ikooniteema jaoks paigaldatakse rakenduse- ja MIME-ikoon standardsesse hicolor-teemasse mitmes mõõdus. RPM-i pakendamisetapile antakse rakenduseikoon uuesti ette, et `jpackage` ei asendaks seda Java vaikeikooniga, ning uuendusejärgne `%posttrans` samm taastab töölauaregistreeringud pärast vana paketi eemaldusskripti lõppu.

### 18. august 2026: Windowsi iseseisev rakendus ja paigaldaja

- Lisati `packageWindowsAppImage`, mis loob Windowsis rakendusepildi koos Java 25 runtime'i, JavaFX-i, core'i ja muude käitusaegsete sõltuvustega.
- Lisati `packageWindowsInstaller`, mis loob Java 25 `jpackage` ja ametliku WiX Toolset 4 abil EXE-paigaldaja.
- Paigaldaja lisab rakenduse Start-menüüsse ja registreerib `.pplan` failitüübi koos eraldi plaaniikooniga; failiseose avamiskäsk kasutab olemasolevat `PlaaniseppLauncher` klassi.
- Windowsi jaoks lisati mitut mõõtu sisaldavad ICO-failid rakendusele, paigaldajale ja plaanifailile. Linuxi PNG-ikoonid ning pakendamisülesanded jäid eraldi ja muutmata.
- Kontrolliti rakendusepildi käivitumist, EXE-paigaldaja loomist ja paigaldamist, Start-menüü otseteed, ikoone, versioon 1 ning versioon 2 `.pplan` failide avamist Windowsi failiseose kaudu ja rakenduse puhast eemaldamist.

### 21. august 2026: Plaanisepa elektrimudeli laiendamine

- Seadmetele ja füüsilistele vooluühendustele lisati püsivad ID-d.
- Ühel tarbival objektil võib olla üks vaiketoide ja mitu alternatiivset ühendust ning seade võib kasutada vaiketoidet või viidata konkreetsele ühendusele.
- Koormusarvutus jagab seadmete võimsuse nende tegelike ühenduste ja allikate vahel.
- `.pplan` versioon 3 lisas ühenduse vaiketoite rolli, seadmepõhiste ühenduseviidete ja alajaotuskilpide säilitamise; v1 ja v2 avanevad tagasiühilduvalt.
- `.pplan` versioon 4 lisas üksikobjekti nähtavuse; versioonita ning v1–v3 objektid laaditakse vaikimisi nähtavana.
- Alajaotuskilbi saab objektitüüpide valikust kaardile lisada; sama detailipaneel võimaldab hallata nii kilbi väljundeid kui ka selle ülesvoolu toidet.
- Automaattestid katavad v2 migratsiooni, v3 ringreisi, mitme ühenduse koormusjaotuse ja vigase ühenduseviite turvalise lähtestamise.

### 26. august 2026: sirgete aiaridade planeerimine

- Lisati eraldi aiaridade domeenimudel, mille aluseks on aedade arv, ühe lõigu füüsiline pikkus ja rea suund.
- Aiarida luuakse kahel kaardiklõpsul ning kuvatakse üksikute standardpikkusega lõikude ja kogupikkusena.
- Külgpaneel võimaldab aedade arvu, ühe aia pikkust ja suunda täpselt muuta.
- Valitud rea otspunkte saab lohistada jäiga reana: lõpp-punkt pöörab rida ning alguspunkti lohistamisel jääb senine lõpp paigale. Üksikute aedade pikkus ei muutu.
- `.pplan` versioon 10 säilitab aiaridade geomeetria jagatud ühenduspunktide võrguna ning tekstiaruanne lisab aia inventari kokkuvõtte. Versioon 9 suunatud jätkuseosed migreeritakse avamisel samasse võrgumudelisse.
- Aiavõrk võib olla avatud, hargnev või suletud, mistõttu ristkülikul ja ringil pole kunstlikku esimest ega viimast rida. Ükskõik millise rea keskelt lohistamine nihutab kogu ühendatud võrku.

## 8. Kasutajatestides tehtud olulisemad õppetunnid

Projekti väärtus ei ole ainult funktsioonide arvus. Korduv päris kasutamine tõi välja mitu üldistatavat disainiõppetundi:

- Kaardi klõpsu ja lohistamise eristamiseks on vaja liikumislävendit; vastasel juhul sõltub töökindlus hiire DPI-st.
- Suum peab säilitama ligipääsu kogu sisule, mitte ainult suurendama vaadet keskpunkti ümber.
- Ajutine tööriist peab pärast oma tegevuse lõpetamist välja lülituma, et järgmine klõps ei teeks ootamatut muudatust.
- Pidevalt nähtav külgpaneel ei tohi sisaldada iga objektitüübi kõiki välju korraga.
- Ühe objekti lisamiseks ei tohiks kasutaja läbida mitut järjestikust dialoogi.
- Kaardi ortofoto nõuab siltidelt tausta või muud kontrasti tagavat lahendust.
- Suurte ja tihedate plaanide korral peavad sildid olema peidetavad ning ümberpaigutatavad.
- Vabakujulise geomeetria lõigule klõpsamine lisab liiga kergesti kogemata punkti; väiksemad lohistatavad vahepunktid annavad kavatsusest selgema signaali.
- Lukustamine tähendab asukoha kaitsmist, mitte kogu objekti muutumatuks tegemist.
- Salvestusvormingu tagasiühilduvus on kasutaja usalduse jaoks keskne funktsioon, mitte kõrvaline tehniline detail.

Need tähelepanekud sobivad bakalaureusetöös kasutajakeskse iteratiivse arenduse näideteks.

## 9. Pooleliolev ja järgmised tööd

### 9.1 Vahetu jätkamiskoht

`v0.7.2` seisus on kasutusel `.pplan` versioon 28; kõigi varasemate toetatud vormingute lugemine säilib. Lisaks Windowsi ja Fedora pakenditele, kaardipõhisele sündmuseplaneerimisele, elektri- ja kaablivõrgule, inventari kokkuvõttele ning korraldajavaatele on täiendatud aedade järjestikust loomist, objektide loendi kihistusrežiimi ja kaablite käsitlemist iseseisvalt valitavate kihtidena. Kaableid saab otsida, kihistada, peita, lukustada ja mitmikvalikuna muuta ilma ühendatud telki valimata. Rakenduse nimi on **Plaanisepp** ning ajaloolised ühilduvusidentifikaatorid säilivad. Täpsem tööjärjekord ja vastuvõtukriteeriumid on failis `docs/ARENDUSPLAAN.md`.

### 9.2 Kvaliteet ja arhitektuur

- Hoida `.pplan` paketi lugemine ja kirjutamine `planner-core` teenuses; JavaFX-i pildikuvamine jääb `planner-gui` vastutuseks.
- Jagada väga suur `PlaaniseppApp` järk-järgult väiksemateks vaate-, kontrolleri- ja tööriistaklassideks.
- Laiendada automaatteste eelkõige vigaste failide, kasutajaliidese sündmuste ja ekspordi regressioonide suunas.
- Kontrollida käsitsi ühenduspunkti muutmist telgil, alal ja joonel, objekti või geomeetria liigutamist ning salvestamise järel taastumist.
- Lisada ootamatute failivigade jaoks logimine, säilitades kasutajale lühikesed ja arusaadavad veateated.

### 9.3 Platvormiülene kontroll ja väljastamine

- GitHub Actions kontrollib push'e ja pull request'e Linuxi Java 25 `clean test` töövooga; versioonisildi põhine workflow ehitab Linuxi ja Windowsi levituspaketid ning avaldab need GitHub Release'is.
- Kontrollida iga väljalaske järel Linuxis JavaFX-i kaardivaadet, failidialooge, kasutaja eelistusi ning TXT-, PNG- ja PDF-eksporti.
- Hoida Windowsi ja Linuxi `jpackage` sisendid ning väljundid eraldi, et ühe platvormi pakendamine ei rikuks teist.
- Kontrollida paketti arvutis, kus Javat ega arenduskeskkonda pole paigaldatud, ning korrata uuendamise ja `.pplan` failiseose põhivoogu.
- Täiendada paigaldamise ja uuendamise juhiseid vastavalt Fedora ning Windowsi tegelikule kasutajakogemusele.
- Allkirjastada avalikult levitatav Windowsi paigaldaja usaldusväärse koodisigneerimise sertifikaadiga.

### 9.4 Suurema süsteemi funktsioonid

Pärast korraldajate põhivoo stabiliseerimist võib kaaluda järgmisi suuremaid samme:

- visuaalne ümberkujundus ja parem ligipääsetavus tihedate plaanide korral;
- märkused ja kommentaarid, mis sobivad mitme korraldaja koostööks;
- täiendavad helitehnika ja PA-seadmete eelseadistused;
- pilves talletamine, jagamine ja ajakohaste plaanide import organisatsiooni sees;
- veebis vaadatav avaldatud plaan;
- autentimine, organisatsioonid, festivalid, kaustad ja õigused;
- XLR-kaablite ning keerukamate helisüsteemide modelleerimine.

Veebivaade ja organisatsioonid tähendavad tõenäoliselt eraldi serverit, andmebaasi ja veebiklienti. Neid ei ole mõistlik praegusesse JavaFX-i klassi otse juurde kasvatada; bakalaureusetöö arhitektuur peaks käsitlema töölauarakendust ühe võimaliku kliendina.

## 10. Teadaolevad piirangud ja riskid

- Automaattestid katavad geomeetriat, seadmemudelit, salvestamise tagasiühilduvust, vooluarvutust, kaabli otspunkte ja tekstiaruannet, kuid kasutajaliidese sündmuste testikate on endiselt piiratud.
- Peamine JavaFX-i rakendusklass on liiga suur ja koondab veel palju erinevaid vastutusi.
- Vanemate vormingute lugemine ja migratsioon on automaattestidega kaetud kuni praeguse `.pplan` vorminguni 26. Eri kaardipildivormingute ja platvormide kombinatsioone tuleb regressioonide vältimiseks edaspidi siiski korrata.
- Vanad versioonita ning versioon 1–3 failid võivad viidata algsele kaardifailile absoluutse või platvormipõhise teega; kaart peab vana faili esmakordsel avamisel veel kättesaadav olema, et järgmine salvestamine saaks selle uude paketti lisada.
- Kõiki keerukate JavaFX-i sündmuste ja kõrge resolutsiooni jõudlusjuhte ei kata automaattest; valitud objektide, kaartide ja ekspordi põhivoogu tuleb kontrollida päris rakenduses.
- Fedora RPM-i ja Windowsi EXE-paigaldaja paigaldamine, menüüst käivitamine, `.pplan` failiseos, ikoonid ja eemaldamine on kontrollitud. JavaFX-i Linuxi failidialoog ei kuva kohandatud MIME-ikooni, kuigi Dolphin ja süsteemi failiseos seda teevad.
- Windowsi kohalik arenduspaigaldaja ei ole digitaalselt allkirjastatud ning võib seetõttu avalikul levitamisel kuvada SmartScreeni hoiatuse.
- Kõiki platvormipõhiseid failidialooge ja eksportide äärejuhte ei ole Linuxis ega Windowsis veel süstemaatiliselt kontrollitud.
- Tartu kaardiandmete import sõltub võrguühendusest ja GIS-teenuse kättesaadavusest; imporditud püsivoolukilbi lisafailid jäävad praegu välisteks linkideks.
- Rakendusel ei ole veel veebivaadet, kasutajakontosid, õigusi ega keskset andmehoidlat.
- Aiavahend toetab sirgeid jäiku ridu ja nende jagatud ühenduspunktidega võrke. Eraldi aiasegmentide identiteet ning automaatne vabakujulise kõvera aedadeks jaotamine ei ole praegu rakendatud ega ole põhivoo prioriteet.

## 11. Soovituslik tööjärjekord

1. Kontrolli süstemaatiliselt korraldaja põhivoogu: uue plaani loomine, kaardi valimine, objektide ja gruppide haldus, külgpaneeli peitmine/taastamine, inventar, salvestamine ja uuesti avamine ning korraldajavaate PDF.
2. Kontrolli tehnikuvaate põhivoogu: püsivoolukilpide import, kaablid, ühenduspunktid, voolu kokkuvõte ning vaate eelistuse taastumine pärast taaskäivitust.
3. Paranda ainult päris kasutustestis leitud vead ja tee iga paranduse järel väike regressioonikontroll; ära lisa samasse sammu uut suurt funktsiooni.
4. Laienda automaatteste nendele piiridele, kus käsitsi korduvad vead: valik, lohistamine, nähtavus/lukustus, salvestamise migratsioon ja eksport.
5. Tee pärast põhivoo stabiilsust sihitud UI- ja ligipääsetavusparandused ning alles seejärel väike refaktor, mis toetab konkreetset kasutusjuhtu.
6. Hoia pilv, kasutajakontod, festivalideülene koondinventar ja muud suured süsteemifunktsioonid praegu teadlikult ootel.

## 12. Uue arendusvestluse alustamise juhis

Uue vestluse alguses kasuta võimalikult täpset lähteinfot, et pikk ajalugu ei sunniks juba tehtud töid uuesti planeerima:

> Jätkame Plaaniseppa arendamist kataloogis `/home/matteus/Projects/pannkoogihommiku-planeerija-java`.
>
> Loe esmalt `README.md`, `docs/ARENDUSPLAAN.md` ja `docs/PROJEKTI_ULEVAADE.md`. Kontrolli tegelikku seisu käsuga `git status --short`, viimaseid committe ja tage ning vaata Gradle'i tegelikku versiooni. Ära eelda dokumentide põhjal, et vana või juba tehtud funktsioon on veel tegemata; kui dokument ja kood erinevad, uuenda dokumenti või käsitle koodi tegeliku allikana.
>
> Projekt kasutab Java 25 ja JavaFX 26.0.2 ning on jaotatud mooduliteks `planner-core` ja `planner-gui`. Hoia domeeniloogika ja `.pplan` faili lugemine/kirjutamine core'is ning JavaFX-i vaated GUI-s. Praegune failivorming on 23 ja vanemate vormingute lugemine peab säilima. Ära kustuta, lähtesta ega kirjuta üle minu olemasolevaid muudatusi.
>
> Töömeetod: tee üks kasutaja poolt kontrollitav muudatus korraga, kasuta failide muutmiseks `apply_patch`-i, säilita eestikeelne kasutajaliides ning ütle enne suuremat muudatust lühidalt, mida kontrollid. Ära paku uuesti juba tehtud arengukava punkte. Kui probleem on piisavalt selge, rakenda parandust kohe, mitte ära piirdu ainult plaaniga.
>
> Praegune esimene töö on korraldaja põhivoo süstemaatiline regressioonikontroll: uue plaani loomine ja päriskaardilt aluskaardi valimine, objektide ja gruppide haldus, külgpaneeli sektorite peitmine ja vaikejärjestuse taastamine, inventar, salvestamine/uuesti avamine ning korraldajavaate PDF. Seejärel kontrolli tehnikuvaates püsivoolukilpide importi, kaableid, ühenduspunkte, voolu kokkuvõtet ja vaate eelistuse taastumist. Paranda leitud vead väikeste eraldi sammudena; ära alusta uut suurt süsteemifunktsiooni enne, kui põhivoog on stabiilne.
>
> Kontrolli pärast muudatusi vähemalt `./gradlew test --no-daemon` ja `git diff --check`. Lõpus anna kokkuvõte muudetud failidest, tehtud kontrollidest, võimalikest lahtistest kohtadest ning soovituslikust ingliskeelsest commiti nimest. Commiti, tage ega GitHubi push'i ära tee ilma minu eraldi soovita.

## 13. Dokumendi hooldamine

Pärast iga suuremat etappi tuleks uuendada vähemalt:

- dokumendi kuupäeva ja viimase commiti viidet;
- saavutatud funktsionaalsuse peatükki;
- pooleliolevate tööde ning piirangute nimekirja;
- soovituslikku tööjärjekorda;
- uue vestluse vahetut jätkamiskohta.

Commitide üksikasju ei ole vaja siia ükshaaval kopeerida. Eesmärk on säilitada otsused, põhjendused, tervikpilt ja järgmine selge tegevus.
