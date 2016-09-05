# Android-in-the-Middle
Repository for Android in the Middle app
- ~~interazione con ROOT (richiesta permessi, check permessi) [Facile]~~
- ~~ricerca utenti sulla rete (ping degli host, host scanner) [Medio]~~
- ~~"avvelenamento" host [Facile-Difficile, a seconda se utilizziamo una libreria]~~
- ~~MitM dei pacchetti (solo lettura)~~ [Medio/Difficile]
- MitM dei pacchetti (modifica on-the-fly)[Medio/Difficile]
- creazione filtri ad hoc [Medio]
- ~~creazione Activity e servizi [Medio/Facile ma lungo]~~
- ~~creazione interfaccia [Facile-Medio a seconda di quanto vogliamo complicarci la vita]~~

TODO:
  compile tcpdump and iptables in order to support non-arm CPUs
  ~~implement C/C++ code that continuosly read and parse packet from opened .pcap file using libpcap~~


Con l'arp spoofing redirigiamo il traffico di un target verso di noi. Con iptables e altri parametri del kernel, configuriamo il telefono come forwarder, ovvero se ci arrivano dei pacchetti che non sono destinati a noi li ridirigiamo verso la destinazione voluta. Con tcpdump siamo in grado di catturare in un file .pcap tutto il traffico [diretto a/ricevuto da] il target. Noi vogliamo però osservare in tempo reale il traffico catturato (ed eventualmente, in futuro, se avremo tempo e risorse, modificarlo). Tcpdump permette, oltre a catturare il traffico con eventuali filtri, di leggere un file pcap.

  Problema 1) tcpdump, per decodificare un file pcap, prima lo legge tutto e poi risponde. Per questo motivo, non è possibile dare in pasto a tcpdump un flusso con "tail" (ovvero un file che viene scritto di continuo e che ogni volta che ha un aggiornamento viene invocato).
  Problema 2) anche ammesso di riuscirci, comunque tcpdump risponderebbe in standard output con i pacchetti affilati uno dopo l'altro, toccherebbe a noi fare il lavoro duro di parsare i paccheti e prendere quello che ci interessa.
  
  Possibile soluzione 1) Creare del codice in C/C++ che sfrutti libpcap per decodificare di continuo un file pcap e fare un parser java (che però possiamo riutilizzare per modificare il traffico in realtime, se mai lo faremo)
  Possibile soluzione 2) Creare del codice in C/C++ che sfrutti libpcap per decodificare di continuo un file in modo che ogni pacchetto venga messo in una struttura C/C++ che distingua frame ethernet, header tcp/udp, payload etc.
  
  Soluziona adottata: parsing nativo del file pcap in strutture C e classi Java 
