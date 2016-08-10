package it.uniroma2.giadd.aitm.managers;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import it.uniroma2.giadd.aitm.utils.PortUtils;

/**
 * Created by Alessandro Di Diego on 10/08/16.
 */

public class PacketSender {

    private static final String TAG = PacketSender.class.getName();

    public static void sendUDP(String destinationAddress, String payload) {
        if (payload.getBytes().length > 1500) {
            Log.e(TAG, "Payload is too big! Max is 1500 bytes");
            return;
        }
        byte[] message = payload.getBytes();
        DatagramSocket socket = null;
        try {
            DatagramPacket packet = new DatagramPacket(message, message.length, InetAddress.getByName(destinationAddress), PortUtils.getRandomNotReservedPort());
            socket = new DatagramSocket();
            socket.send(packet);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null)
                socket.close();
        }
    }
}
