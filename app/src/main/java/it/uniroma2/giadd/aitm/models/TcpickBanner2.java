package it.uniroma2.giadd.aitm.models;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Alessandro Di Diego on 07/09/16.
 */

public class TcpickBanner2 {

    private static final String re1 = "(\\[)";    // Any Single Character 1
    private static final String re2 = "((?:[A-z][A-z]+))";    // Word 1
    private static final String re3 = "(\\])";    // Any Single Character 2
    private static final String re4 = "( )";    // White Space 1
    private static final String re5 = "((?:(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?))(?![\\d])";    // IPv4 IP Address 1
    private static final String re6 = "(:)";    // Any Single Character 3
    private static final String re7 = "((?:[A-z0-9_]*))";    // Variable Name 1
    private static final String re8 = "( )";    // White Space 2
    private static final String re9 = "(\\d+)";    // Any Digit 1
    private static final String re10 = "(:)";    // Any Single Character 4
    private static final String re11 = "(\\d+)";    // Any Digit 2
    private static final String re12 = "( )";    // White Space 3
    private static final String re13 = "(\\()";    // Any Single Character 5
    private static final String re14 = "(\\d+)";    // Any Digit 3
    private static final String re15 = "(\\))";    // Any Single Character 6
    private static final Pattern p = Pattern.compile(re1 + re2 + re3 + re4 + re5 + re6 + re7 + re8 + re9 + re10 + re11 + re12 + re13 + re14 + re15, Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    private String destination;
    private String destinationIp;
    private String destinationPort;
    private long offsetBefore;
    private long offsetAfter;
    private long segmentLength;

    public TcpickBanner2(String destination, String destinationIp, String destinationPort, long offsetBefore, long offsetAfter, long segmentLength) {
        this.destination = destination;
        this.destinationIp = destinationIp;
        this.destinationPort = destinationPort;
        this.offsetBefore = offsetBefore;
        this.offsetAfter = offsetAfter;
        this.segmentLength = segmentLength;
    }

    public static TcpickBanner2 tcpickHeaderFromString(String headerString) {
        Matcher m = p.matcher(headerString);
        if (m.find()) {
            try {
                return new TcpickBanner2(m.group(2), m.group(5), m.group(7), Long.valueOf(m.group(9)), Long.valueOf(m.group(11)), Long.valueOf(m.group(14)));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
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

    public long getOffsetBefore() {
        return offsetBefore;
    }

    public void setOffsetBefore(long offsetBefore) {
        this.offsetBefore = offsetBefore;
    }

    public long getOffsetAfter() {
        return offsetAfter;
    }

    public void setOffsetAfter(long offsetAfter) {
        this.offsetAfter = offsetAfter;
    }

    public long getSegmentLength() {
        return segmentLength;
    }

    public void setSegmentLength(long segmentLength) {
        this.segmentLength = segmentLength;
    }
}