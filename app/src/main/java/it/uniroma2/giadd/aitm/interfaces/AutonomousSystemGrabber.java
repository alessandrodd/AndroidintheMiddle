package it.uniroma2.giadd.aitm.interfaces;


import android.content.Context;

public interface AutonomousSystemGrabber {
    String STAT_RIPE_ANNOUNCED_PREFIXES_BY_AS_URI = "https://stat.ripe.net/data/announced-prefixes/data.json?resource=";

    void getRoutingPrefixes(Context context, OnAutonomousSystemGrabbedListener listener);
}
