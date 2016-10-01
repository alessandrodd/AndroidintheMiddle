package it.uniroma2.giadd.aitm.interfaces;


import android.content.Context;

import java.util.List;

public interface OnAutonomousSystemGrabbedListener {
    void onSuccess(List<String> prefixes);

    void onError(String message);
}
