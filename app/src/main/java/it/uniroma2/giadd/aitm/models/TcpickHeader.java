package it.uniroma2.giadd.aitm.models;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alessandro Di Diego on 07/09/16.
 */

public class TcpickHeader {

    private static final String re1 = "(\\d+)";    // Integer Number 1
    private static final String re2 = "(:)";    // Any Single Character 1
    private static final String re3 = "(\\d+)";    // Integer Number 2
    private static final String re4 = "(:)";    // Any Single Character 2
    private static final String re5 = "([+-]?\\d+\\.\\d+)(?![-+0-9\\.])";    // Float 1
    private static final String re6 = "(\\s+)";    // White Space 1
    private static final String re7 = "((?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))(?![\\d])";    // IPv4 IP Address 1
    private static final String re8 = "(:)";    // Any Single Character 3
    private static final String re9 = "((?:[A-z0-9_]+))";    // Variable Name 1
    private static final String re10 = "( )";    // White Space 2
    private static final String re11 = "((?:[A-z0-9_]+))";    // Word 1
    private static final String re12 = "( )";    // White Space 3
    private static final String re13 = "(>)";    // Any Single Character 4
    private static final String re14 = "( )";    // White Space 4
    private static final String re15 = "((?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))(?![\\d])";    // IPv4 IP Address 2
    private static final String re16 = "(:)";    // Any Single Character 5
    private static final String re17 = "((?:[A-z0-9_]+))";    // Variable Name 2
    private static final String re18 = "(\\s+)";    // White Space 5
    private static final String re19 = "(\\()";    // Any Single Character 6
    private static final String re20 = "(\\d+)";    // Integer Number 3
    private static final String re21 = "(\\))";    // Any Single Character 7
    private static final Pattern p = Pattern.compile(re1 + re2 + re3 + re4 + re5 + re6 + re7 + re8 + re9 + re10 + re11 + re12 + re13 + re14 + re15 + re16 + re17 + re18 + re19 + re20 + re21, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    int hours;
    int minutes;
    float seconds;
    String sourceIp;
    String sourcePort;
    String packetType;
    String destinationIp;
    String destinationPort;
    int packetLength;

    public TcpickHeader(int hours, int minutes, float seconds, String sourceIp, String sourcePort, String packetType, String destinationIp, String destinationPort, int packetLength) {
        this.hours = hours;
        this.minutes = minutes;
        this.seconds = seconds;
        this.sourceIp = sourceIp;
        this.sourcePort = sourcePort;
        this.packetType = packetType;
        this.destinationIp = destinationIp;
        this.destinationPort = destinationPort;
        this.packetLength = packetLength;
    }

    public static TcpickHeader tcpickHeaderFromString(String headerString) {
        Matcher m = p.matcher(headerString);
        if (m.find()) {
            try {
                return new TcpickHeader(Integer.valueOf(m.group(1)), Integer.valueOf(m.group(3)), Float.valueOf(m.group(5)), m.group(7), m.group(9), m.group(11), m.group(15), m.group(17), Integer.valueOf(m.group(20)));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public float getSeconds() {
        return seconds;
    }

    public void setSeconds(float seconds) {
        this.seconds = seconds;
    }

    public String getSourceIp() {
        return sourceIp;
    }

    public void setSourceIp(String sourceIp) {
        this.sourceIp = sourceIp;
    }

    public String getSourcePort() {
        return sourcePort;
    }

    public void setSourcePort(String sourcePort) {
        this.sourcePort = sourcePort;
    }

    public String getPacketType() {
        return packetType;
    }

    public void setPacketType(String packetType) {
        this.packetType = packetType;
    }

    public String getDestinationIp() {
        return destinationIp;
    }

    public void setDestinationIp(String destinationIp) {
        this.destinationIp = destinationIp;
    }

    public String getDestinationPort() {
        return destinationPort;
    }

    public void setDestinationPort(String destinationPort) {
        this.destinationPort = destinationPort;
    }

    public int getPacketLength() {
        return packetLength;
    }

    public void setPacketLength(int packetLength) {
        this.packetLength = packetLength;
    }
}