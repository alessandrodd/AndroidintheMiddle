package it.uniroma2.giadd.aitm.tasks;

import android.os.AsyncTask;
import android.util.Log;

import it.uniroma2.giadd.aitm.models.MyIpPacket;
import it.uniroma2.giadd.aitm.models.PcapParser;

/**
 * Created by Alessandro Di Diego on 10/08/16.
 */

/**
 * ----------> HIC SUNT LEONES <-------------
 * Things gets quite ugly here, but luckily the code is really short.
 * In the native code there is a function - pcap_loop - that has a callback function
 * as one of the arguments. This callback gets called synchronously and we don't want
 * to mix native threads with java threads etc.
 * So, what we do in the native code, is simply call an instance method of the same class
 * each time the callback is called. Ideally, the main loop should be executed in a separate
 * thread while the callback should be called on UI thread. So we override the instance
 * method onPacketParsed to make it call onProgressUpdate while we call parsePcapFile
 * from a background thread.
 * Moreover, since we have to mimic the tail command, each time the parsePcapFile ends,
 * an offset is returned thanks to wich at the next iteration there is no need to
 * restart from the beginning to parse the file.
 */

public class ParsePcapTask extends AsyncTask<Void, MyIpPacket, Long> {

    private static final String TAG = ParsePcapTask.class.getName();
    private PcapParser pcapParser;
    private String path;
    private long offset;

    public ParsePcapTask(String path) {
        this.path = path;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        pcapParser = new PcapParser() {
            @Override
            protected void onPacketParsed(MyIpPacket ipPacket) {
                super.onPacketParsed(ipPacket);
                onProgressUpdate(ipPacket);
            }
        };
    }


    @Override
    protected Long doInBackground(Void... voids) {
        // continuoos parsing with 1 sec polling
        while (!isCancelled()) {
            offset = pcapParser.parsePcapFile(path, offset);
            Log.d("DBG", "reached offset: " + offset);
            if (isCancelled() || offset < 0) break;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                return offset;
            }
        }
        return offset;
    }

    @Override
    protected void onProgressUpdate(MyIpPacket... values) {
        super.onProgressUpdate(values);
        /*
        MyIpPacket ipPacket = values[0];
        Log.d("DBG", "Pacchetto: " + ipPacket.toString());
        Log.d("DBG", "Data: ");
        Log.d("DBG", "------------START------------");
        byte[] bytes = ipPacket.getTransportLayerPacket().getData();
        String dataStr = "";
        for (byte b : bytes) {
            if ((b >= 32 && b <= 126) || b == 10 || b == 11 || b == 13) {
                dataStr += (char) b;
            } else {
                dataStr += ".";
            }
        }
        Log.d("DBG", dataStr);
        Log.d("DBG", "------------END------------");
        */
    }

    @Override
    protected void onPostExecute(Long offset) {
        super.onPostExecute(offset);
    }
}
