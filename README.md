version# Android-in-the-Middle
Repository for Android in the Middle app
- ~~interazione con ROOT (richiesta permessi, check permessi)~~ [Facile]
- ~~ricerca utenti sulla rete (ping degli host, host scanner)~~ [Medio]
- ~~"avvelenamento" host~~ [Facile-Difficile, a seconda se utilizziamo una libreria]
- ~~MitM e interpretazione dei pacchetti (solo lettura)~~ [Difficile]
- ~~creazione Activity e servizi~~ [Medio/Facile ma lungo]
- ~~creazione interfaccia~~ [Facile-Medio a seconda di quanto vogliamo complicarci la vita]
- creazione filtri ad hoc [Medio]

TODO:
  - ~~implement C/C++ code that continuosly read and parse packet from opened .pcap file using libpcap~~
  - compile tcpdump and iptables in order to support non-arm CPUs
  - use tshark instead of tcpdump
  - tcp proxy (modify packets on-the-fly)
  - use advanced https fingerprint (https://github.com/LeeBrotherston/tls-fingerprinting)
  - extract images, files etc.


What to check for each messaging app:

    a) Filter rules, i.e. how to filter the specific traffic (specific port(s), packet fingerprint, AS Number...)
    b) Are messages transmitted in clear text? [Victim, Contact]
    c) Are files transmitted in clear text? Which types? [Victim, Contact]
    d) Are "Personal Status", avatar etc. transmitted in clear? [Victim, Contact]
    e) Are there any constant IDs transmitted in clear (something to link an user to a specific id)? [Victim, Contact]
    f) Are there any metadata in plan text?
    g) Does the app accepts self-signed certificate?
    h) Number of downloads
    i) App version 
    j) Link

Analyzed Messaging Apps:

    1) Whatsapp
        a) cidr (https://www.whatsapp.com/cidr.txt), port 80 443 4244 5222 5223 5228 5242 
        b) No
        c) No
        d) No
        e) No
        f) No (https://www.whatsapp.com/security/WhatsApp-Security-Whitepaper.pdf)
        g) No
        h) 1-5 billion
        i) 2.16.193 (30/09/2016)
        j) https://play.google.com/store/apps/details?id=com.whatsapp

    2) Telegram
        a) AS62041
        b) No
        c) No
        d) No
        e) No
        f) No
        g) No
        h) 100-500 millions
        i) 3.12.0 (30/09/2016)
        j) https://play.google.com/store/apps/details?id=org.telegram.messenger

    3) Mxit
        a) port 9119 9229
        b) Yes, both
        c) Yes, images, both
        d) Yes, both
        e) Yes, both
        f) Yes, Victim's phone number (ms), both complete profile (gender, full name, relationship, lastmodified, birthdate...)
        g) No encryption at all
        h) 1-5 millions
        i) 7.3.0.0  (01/09/2016)
        j) https://play.google.com/store/apps/details?id=com.mxit.android

    4) VK
        a) AS47541
        b) No
        c) No
        d) No
        e) No
        f) No
        g) No
        h) 100-500 millions
        i) 4.4.1 (01/10/2016)
        j) https://play.google.com/store/apps/details?id=com.vkontakte.android

    4) VK Chat
        a) AS47541, fields "host" contains "api.vk.com"
        b) Yes, both (check URL parameters)
        c) Yes images, both; map location, both; document, Contact
        d) Yes (Also typing), both
        e) Yes (sender uid, both), https://vk.com/id[UID] to identify the user
        f) Yes (act
           key 
           access_token => OAuth2 access tokens 
           sig ) (https://vk.com/dev/methods for full description), user position (geographical coordinates),
        g) No encryption at all
        h) 1-5 millions
        i) 1.2 (15/07/2016)
        j) https://play.google.com/store/apps/details?id=ru.st1ng.vk

    5) QQ
        a) *.qq.com, port 80 8080 8000 15000
        b) No
        c) No
        d) No
        e) Yes (account ID, Victim)
        f) No
        g) No HTTPS
        h) 10-50 millions
        i) 5.9.7 (30/09/2016)
        j) https://play.google.com/store/apps/details?id=com.tencent.mobileqq

    6) textPlus
        a) *.app.nextplus.me (mhs.app.nextplus.me ums.app.nextplus.me ces.app.nextplus.me vhs.app.nextplus.me xmpp.app.nextplus.me) *pic.nextplus.me ads.nexage.com, port 443 80
        b) No
        c) No
        d) Yes (During change of profile picture, is possible to see the link of the img, Victim)
        e) No
        f) No
        g) Yes, partially (unable to open the app when using a fake cert, but if the cert is provided during a chat session it's possible to completly sniff the communication, except for the images sent/received. Probabily the cert isn't checked for the domain xmpp.app.nextplus.me).
        h) 10-50 millions
        i) 6.2.1 (01/10/2016)
        j) https://play.google.com/store/apps/details?id=com.gogii.textplus

    7) Talkray
        a) *.amazonaws.com, port 8576 443
        b) No
        c) No
        d) No
        e) No
        f) No
        g) No
        h) 10-50 millions
        i) 3.134 (04/10/2016)
        j) https://play.google.com/store/apps/details?id=com.talkray.client

    8) BBM
        a) *.blackberry.net, port 443
        b) No
        c) No
        d) No
        e) No
        f) No
        g) No
        h) 100-500 millions
        i) 3.0.1.25 (03/10/2016)
        j) https://play.google.com/store/apps/details?id=com.bbm

    9) Instagram
        a) *.fbcdn.net *.facebook.com, port 443
        b) No
        c) No
        d) No
        e) No
        f) No
        g) No
        h) 1-5 billion
        i) 9.4.5 (03/10/2016)
        j) https://play.google.com/store/apps/details?id=com.instagram.android
        
    10) Yahoo Messenger
        a) AS26101 (maybe only gw1.iris.vip.bf1.yahoo.com gw.iris.vip.bf1.yahoo.com ct1.ycs.vip.deb.yahoo.net), port 443
        b) No
        c) No
        d) No
        e) No
        f) No
        g) No
        h) 50-100 millions
        i) 2.2.0 (04/10/2016)
        j) https://play.google.com/store/apps/details?id=com.yahoo.mobile.client.android.im
        
    11) Maaii
        a) *.amazonaws.com, port 443; http host cds-gateway.aviary.com
        b) No
        c) No
        d) No
        e) No
        f) Yes, apiKey contentGroup osVersion platform resolution sdkVersion versionKey signature (see traffico_interessante.txt)
        g) No
        h) 1-5 millions
        i) 2.6.1.3 (20/06/2016)
        j) https://play.google.com/store/apps/details?id=com.maaii.maaii&hl=it
        
       12) GroupMe
        a) api.groupme.com *.amazonaws.com, port 443
        b) No
        c) No
        d) No
        e) No
        f) No
        g) No
        h) 10-50 millions
        i) 5.6.1 (04/10/2016)
        j) https://play.google.com/store/apps/details?id=com.groupme.android
        
       12) KakaoTalk
        a) AS45991 AS10158
        b) No (LOCO protocol)
        c) No
        d) No
        e) No
        f) No
        g) No
        h) 100-500 millions
        i) 5.8.5 (04/10/2016)
        j) https://play.google.com/store/apps/details?id=com.kakao.talk
        
       13) Nimbuzz
        a) AS51089, port 80 5222; HTTP "Host: nimbuzz.com.s3.amazonaws.com"; *.amazonaws.com, port 443
        b) No (uses XMPP protocol with TLS encryption for text)
        c) Yes (HTTP for sending/receiving images, videos, contacts VCARD, audio file and message, both)
        d) Yes (Avatar transmitted in clear text, both)
        e) Yes (when changing avatar user id, Victim; when sending image/video/contact/audio, both; when contacting ads provider mobileads.nimbuzz.com (user id), Victim; when another user changes his avatar (user id and avatar), contact) 
        f) Yes, image/video/contact filename (as found in the device), audio file path (as found in the device), user position (geographic coordinates) when logging in
        g) No
        h) 10-50 millions
        i) 4.6.0 (05/10/2016)
        j) https://play.google.com/store/apps/details?id=com.nimbuzz
        
       14) Hike
        a) *.amazonaws.com, port 443
        b) No
        c) No
        d) No
        e) No
        f) Not in clear text, but you can build HTTP requests to stalk your Hike friends and check if a number is on Hike and with which username (check traffico_interessante.txt)
        g) Yes; using a self-signed certificate, everything is readable (messages, images, files, VCARD, audio, video)
        h) 50-100 millions
        i) 4.3.0.83 (05/10/2016)
        j) https://play.google.com/store/apps/details?id=com.bsb.hike
        
       15) Zalo
        a) ptr.vng.vn or AS38244, port 80 8080 3001 443
        b) No
        c) No
        d) Yes, Contact shared photo, Victim's friend avatar, victim's friend cover photo (uses HTTPS to change Victim's profile informations)
        e) Yes, Victim phone IMEI is transmitted in clear text
        f) Yes, Victim phone IMEI and model
        g) No
        h) 100-500 millions
        i) 3.1.2.r2 (06/10/2016)
        j) https://play.google.com/store/apps/details?id=com.zing.zalo
        
       16) imo
        a) AS36131, port 443 5222 5223 5228
        b) No
        c) No
        d) No
        e) No
        f) No
        g) No
        h) 100-500 millions
        i) 9.8.000000003241 (06/10/2016)
        j) https://play.google.com/store/apps/details?id=com.imo.android.imoim
        
        






Con l'arp spoofing redirigiamo il traffico di un target verso di noi. Con iptables e altri parametri del kernel, configuriamo il telefono come forwarder, ovvero se ci arrivano dei pacchetti che non sono destinati a noi li ridirigiamo verso la destinazione voluta. Con tcpdump siamo in grado di catturare in un file .pcap tutto il traffico [diretto a/ricevuto da] il target. Noi vogliamo però osservare in tempo reale il traffico catturato (ed eventualmente, in futuro, se avremo tempo e risorse, modificarlo). Tcpdump permette, oltre a catturare il traffico con eventuali filtri, di leggere un file pcap.

  Problema 1) tcpdump, per decodificare un file pcap, prima lo legge tutto e poi risponde. Per questo motivo, non è possibile dare in pasto a tcpdump un flusso con "tail" (ovvero un file che viene scritto di continuo e che ogni volta che ha un aggiornamento viene invocato).
  Problema 2) anche ammesso di riuscirci, comunque tcpdump risponderebbe in standard output con i pacchetti affilati uno dopo l'altro, toccherebbe a noi fare il lavoro duro di parsare i paccheti e prendere quello che ci interessa.
  
  Possibile soluzione 1) Creare del codice in C/C++ che sfrutti libpcap per decodificare di continuo un file pcap e fare un parser java (che però possiamo riutilizzare per modificare il traffico in realtime, se mai lo faremo)
  Possibile soluzione 2) Creare del codice in C/C++ che sfrutti libpcap per decodificare di continuo un file in modo che ogni pacchetto venga messo in una struttura C/C++ che distingua frame ethernet, header tcp/udp, payload etc.
  
  **Soluziona adottata**: parsing nativo del file pcap in strutture C e classi Java 
  
  
