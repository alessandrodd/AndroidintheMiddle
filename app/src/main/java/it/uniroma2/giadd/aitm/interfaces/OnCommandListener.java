package it.uniroma2.giadd.aitm.interfaces;

import eu.chainfire.libsuperuser.Shell;

public interface OnCommandListener extends Shell.OnCommandLineListener{
    void onShellError(int exitCode);
}
