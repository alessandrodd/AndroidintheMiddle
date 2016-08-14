package it.uniroma2.giadd.aitm.models;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by Alessandro Di Diego on 13/08/16.
 */

public abstract class MitmModule {

    private static final String COMMAND_KERNEL_ROUTING = "echo <0or1> > /proc/sys/net/ipv4/ip_forward";

    protected List<String> commands = new LinkedList<>();
    protected boolean dumpToFile = false;

    protected MitmModule(List<String> additionalCommands) {
        if (additionalCommands != null)
            commands.addAll(additionalCommands);
    }

    protected MitmModule() {
    }

    protected void addKernelRoutingCommand(boolean enabled) {
        if (enabled)
            commands.add(COMMAND_KERNEL_ROUTING.replaceAll("<0or1>", "1"));
        else
            commands.add(COMMAND_KERNEL_ROUTING.replaceAll("<0or1>", "0"));
    }

    public List<String> getCommands() {
        return commands;
    }

    public boolean isDumpToFile() {
        return dumpToFile;
    }

    public void setDumpToFile(boolean dumpToFile) {
        this.dumpToFile = dumpToFile;
    }


}
