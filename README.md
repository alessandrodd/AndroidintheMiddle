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

    a) How to filter the specific traffic (specific port(s), packet fingerprint, AS Number...)
    b) Are messages transmitted in clear text?
    c) Are "Personal Status", avatar etc. transmitted in clear?
    d) Are there any constant IDs transmitted in clear (something to link an user to a specific id)?
    e) Are there any metadata in plan text?
    f) Does the app accepts self-signed certificate?
    g) Number of downloads
    h) App version 
    i) Link

Analyzed Messaging Apps:

    1) Whatsapp
        a) cidr (https://www.whatsapp.com/cidr.txt)
        b) No
        c) No
        d) No
        e) No (https://www.whatsapp.com/security/WhatsApp-Security-Whitepaper.pdf)
        f) No
        g) 1-5 billion
        h) 2.16.193 (30/09/2016)
        i) https://play.google.com/store/apps/details?id=com.whatsapp

    2) Telegram
        a) AS Number (AS62041)
        b) No
        c) No
        d) No
        e) No
        f) No
        g) 100-500 millions
        h) 3.12.0 (30/09/2016)
        i) https://play.google.com/store/apps/details?id=org.telegram.messenger

    3) Mxit
        a) Port Number (9119, 9229)
        b) Yes
        c) Yes
        d) Yes
        e) Yes
        f) No encryption at all
        g) 1-5 millions
        h) 7.3.0.0  (01/09/2016)
        i) https://play.google.com/store/apps/details?id=com.mxit.android

    4) VK
        a) AS Number (AS47541)
        b) No
        c) No
        d) No
        e) No
        f) No
        g) 100-500 millions
        h) 4.4.1 (01/10/2016)
        i) https://play.google.com/store/apps/details?id=com.vkontakte.android

    4) VK Chat
        a) AS Number (AS47541), fields "host" contains "api.vk.com"
        b) Yes (check URL parameters)
        c) yes (Also typing)
        d) Yes (uid sender)
        e) Yes (act
           key 
           access_token => OAuth2 access tokens 
           sig ) (https://vk.com/dev/methods for full description)
        f) No encryption at all
        g) 1-5 millions
        h) 1.2 (15/07/2016)
        i) https://play.google.com/store/apps/details?id=ru.st1ng.vk

    5) QQ
        a) *.qq.com, port 80 8080 8000 15000
        b) No
        c) No
        d) Yes (account ID (sender))
        e) No
        f) No HTTPS
        g) 10-50 millions
        h) 5.9.7 (30/09/2016)
        i) https://play.google.com/store/apps/details?id=com.tencent.mobileqq

    6) textPlus
        a) *.app.nextplus.me (mhs.app.nextplus.me ums.app.nextplus.me ces.app.nextplus.me vhs.app.nextplus.me xmpp.app.nextplus.me) *pic.nextplus.me ads.nexage.com, port 443 80
        b) No
        c) yes (During change of profile picture, is possible to see the link of the img)
        d) no
        e) No
        f) Yes, partially (unable to open the app when using a fake cert, but if the cert is provided during a chat session it's possible to completly sniff the communication, except for the images sent/received. Probabily the cert isn't checked for the domain xmpp.app.nextplus.me).
        g) 10-50 millions
        h) 6.2.1 (01/10/2016)
        i) https://play.google.com/store/apps/details?id=com.gogii.textplus

    7) Talkray
        a) *.amazonaws.com, port 8576 443
        b) No
        c) No
        d) No
        e) No
        f) No
        g) 10-50 millions
        h) 3.134 (04/10/2016)
        i) https://play.google.com/store/apps/details?id=com.talkray.client

    8) BBM
        a) *.blackberry.net, port 443
        b) No
        c) No
        d) No
        e) No
        f) No
        g) 100-500 millions
        h) 3.0.1.25 (03/10/2016)
        i) https://play.google.com/store/apps/details?id=com.bbm

    9) Instagram
        a) *.fbcdn.net *.facebook.com, port 443
        b) No
        c) No
        d) No
        e) No
        f) No
        g) 1-5 billion
        h) 9.4.5 (03/10/2016)
        i) https://play.google.com/store/apps/details?id=com.instagram.android
        
    10) Yahoo Messenger
        a) gw1.iris.vip.bf1.yahoo.com gw.iris.vip.bf1.yahoo.com ct1.ycs.vip.deb.yahoo.net, port 443
        b) No
        c) No
        d) No
        e) No
        f) No
        g) 50-100 millions
        h) 2.2.0 (04/10/2016)
        i) https://play.google.com/store/apps/details?id=com.yahoo.mobile.client.android.im
        
    11) Maaii
        a) *.amazonaws, port 443; http host cds-gateway.aviary.com
        b) No
        c) No
        d) No
        e) Yes, apiKey contentGroup osVersion platform resolution sdkVersion versionKey signature (see traffico_interessante.txt)
        f) No
        g) 1-5 millions
        h) 2.6.1.3 (20/06/2016)
        i) https://play.google.com/store/apps/details?id=com.maaii.maaii&hl=it
        
       12) GroupMe
        a) api.groupme.com *.amazonaws.com, port 443
        b) No
        c) No
        d) No
        e) No
        f) No
        g) 10-50 millions
        h) 5.6.1 (04/10/2016)
        i) https://play.google.com/store/apps/details?id=com.groupme.android
        
       12) KakaoTalk
        a) AS45991 AS10158
        b) No (LOCO protocol)
        c) No
        d) No
        e) No
        f) No
        g) 100-500 millions
        h) 5.8.5 (04/10/2016)
        i) https://play.google.com/store/apps/details?id=com.kakao.talk
        
       13) Nimbuzz
        a) AS51089, port 80 5222; HTTP "Host: nimbuzz.com.s3.amazonaws.com"; *.amazonaws.com, port 443
        b) Uses XMPP protocol with TLS encryption for text, HTTP for sending/receiving images, videos, contacts VCARD, audio file and message (all in clear)
        c) Avatar transmitted in clear text
        d) yes when changing avatar (user id), when sending image/video/contact/audio (sender user id and receiver user id), when contacting ads provider mobileads.nimbuzz.com (user id), when another user changes his avatar (user id and avatar), 
        e) yes, image/video/contact/audio sender username, image/video/contact/audio receiver username, image/video/contact filename, audio file path, user position (geographic coordinates) when logging in
        f) No
        g) 10-50 millions
        h) 4.6.0 (05/10/2016)
        i) https://play.google.com/store/apps/details?id=com.nimbuzz
        
       14) Hike
        a) *.amazonaws.com, port 443
        b) No
        c) No
        d) No
        e) Not in clear text, but you can build HTTP requests to stalk your Hike friends and check if a number is on Hike and with which username (check traffico_interessante.txt)
        f) Yes; using a self-signed certificate, everything is readable (messages, images, files, VCARD, audio, video)
        g) 50-100 millions
        h) 4.3.0.83 (05/10/2016)
        i) https://play.google.com/store/apps/details?id=com.bsb.hike
        
       15) Zalo
        a) ptr.vng.vn or AS38244, port 80 8080 3001
        b) No
        c) No (uses HTTPS to change profile informations)
        d) Yes, victim phone IMEI is transmitted in clear text
        e) Yes, victim phone IMEI and model 
        f) No
        g) 100-500 millions
        h) 3.1.2.r2 (06/10/2016)
        i) https://play.google.com/store/apps/details?id=com.zing.zalo
        
       16) imo
        a) AS36131
        b) 
        c) 
        d) 
        e) 
        f) 
        g) 100-500 millions
        h) 9.8.000000003241 (06/10/2016)
        i) https://play.google.com/store/apps/details?id=com.imo.android.imoim
        
        






Con l'arp spoofing redirigiamo il traffico di un target verso di noi. Con iptables e altri parametri del kernel, configuriamo il telefono come forwarder, ovvero se ci arrivano dei pacchetti che non sono destinati a noi li ridirigiamo verso la destinazione voluta. Con tcpdump siamo in grado di catturare in un file .pcap tutto il traffico [diretto a/ricevuto da] il target. Noi vogliamo però osservare in tempo reale il traffico catturato (ed eventualmente, in futuro, se avremo tempo e risorse, modificarlo). Tcpdump permette, oltre a catturare il traffico con eventuali filtri, di leggere un file pcap.

  Problema 1) tcpdump, per decodificare un file pcap, prima lo legge tutto e poi risponde. Per questo motivo, non è possibile dare in pasto a tcpdump un flusso con "tail" (ovvero un file che viene scritto di continuo e che ogni volta che ha un aggiornamento viene invocato).
  Problema 2) anche ammesso di riuscirci, comunque tcpdump risponderebbe in standard output con i pacchetti affilati uno dopo l'altro, toccherebbe a noi fare il lavoro duro di parsare i paccheti e prendere quello che ci interessa.
  
  Possibile soluzione 1) Creare del codice in C/C++ che sfrutti libpcap per decodificare di continuo un file pcap e fare un parser java (che però possiamo riutilizzare per modificare il traffico in realtime, se mai lo faremo)
  Possibile soluzione 2) Creare del codice in C/C++ che sfrutti libpcap per decodificare di continuo un file in modo che ogni pacchetto venga messo in una struttura C/C++ che distingua frame ethernet, header tcp/udp, payload etc.
  
  **Soluziona adottata**: parsing nativo del file pcap in strutture C e classi Java 
  
  
