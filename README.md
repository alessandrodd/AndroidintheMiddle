# Android-in-the-Middle
Repository for Android in the Middle app
- interazione con ROOT (richiesta permessi, check permessi) [Facile]
- ricerca utenti sulla rete (ping degli host, host scanner) [Medio]
- "avvelenamento" host [Facile-Difficile, a seconda se utilizziamo una libreria]
- MitM dei pacchetti (ovvero il riuscire a leggere pacchetti provenienti da un host, leggere e modificarli a piacere, inoltrarli alla vera destinazione) [Difficile]
- creazione filtri ad hoc [Medio]
- creazione Activity e servizi [Medio/Facile ma lungo]
- creazione interfaccia [Facile-Medio a seconda di quanto vogliamo complicarci la vita]

TODO:
  compile tcpdump and iptables in order to support non-arm CPUs
  implement C/C++ code that continuosly read and parse packet from opened .pcap file using libpcap 
