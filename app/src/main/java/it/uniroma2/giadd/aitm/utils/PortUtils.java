package it.uniroma2.giadd.aitm.utils;

import java.util.Random;

/**
 * Created by Alessandro Di Diego on 10/08/16.
 */

public class PortUtils {

    private static final int MIN_NOT_RESERVED_PORT = 49152;
    private static final int MAX_NOT_RESERVED_PORT = 65535;

    public static int getRandomNotReservedPort() {
        Random random = new Random();
        return random.nextInt((MAX_NOT_RESERVED_PORT - MIN_NOT_RESERVED_PORT) + 1) + MIN_NOT_RESERVED_PORT;
    }
}
