package it.uniroma2.giadd.aitm.utils;

/**
 * Created by Alessandro Di Diego on 07/09/16.
 */

public class TcpickParseUtils {

    public static String byteArrayToReadableString(byte[] data) {
        if (data != null) {
            String dataStr = "";
            for (byte b : data) {
                if ((b >= 32 && b <= 126) || b == 10 || b == 11 || b == 13) {
                    dataStr += (char) b;
                } else {
                    dataStr += ".";
                }
            }
            return dataStr;
        }
        return "";
    }
}
