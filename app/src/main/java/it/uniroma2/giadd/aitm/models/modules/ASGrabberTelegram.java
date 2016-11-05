package it.uniroma2.giadd.aitm.models.modules;

import android.content.Context;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.giadd.aitm.R;
import it.uniroma2.giadd.aitm.interfaces.AutonomousSystemGrabber;
import it.uniroma2.giadd.aitm.interfaces.OnAutonomousSystemGrabbedListener;
import it.uniroma2.giadd.aitm.models.dto.Prefix;
import it.uniroma2.giadd.aitm.models.dto.RipeAnnouncedPrefix;

/**
 * Created by Alessandro Di Diego on 01/10/16.
 */

public class ASGrabberTelegram implements AutonomousSystemGrabber {

    private static final String TELEGRAM_AS = "AS62041";

    @Override
    public void getRoutingPrefixes(final Context context, final OnAutonomousSystemGrabbedListener listener) {
        Ion.with(context)
                .load(STAT_RIPE_ANNOUNCED_PREFIXES_BY_AS_URI + TELEGRAM_AS).as(RipeAnnouncedPrefix.class)
                .setCallback(new FutureCallback<RipeAnnouncedPrefix>() {
                    @Override
                    public void onCompleted(Exception e, RipeAnnouncedPrefix result) {
                        if (e != null) {
                            listener.onError(context.getString(R.string.error_retrieve_announced_prefixes));
                            return;
                        }
                        if (result == null || result.getData() == null || result.getData().getPrefixes() == null) {
                            listener.onError(context.getString(R.string.error_unable_parse_prefixes));
                            return;
                        }
                        List<Prefix> prefixes = result.getData().getPrefixes();
                        ArrayList<String> stringPrefixes = new ArrayList<String>();
                        for (Prefix prefix : prefixes) {
                            stringPrefixes.add(prefix.getPrefix());
                        }
                        listener.onSuccess(stringPrefixes);
                    }
                });
    }
}
