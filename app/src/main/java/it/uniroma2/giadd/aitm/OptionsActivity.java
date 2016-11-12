package it.uniroma2.giadd.aitm;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.net.NetworkInterface;
import java.util.List;

import it.uniroma2.giadd.aitm.utils.IpPacketDBHandler;
import it.uniroma2.giadd.aitm.utils.NetworkUtils;
import it.uniroma2.giadd.aitm.utils.PreferencesUtils;

/**
 * Created by Alessandro Di Diego on 14/08/16.
 */

public class OptionsActivity extends Activity {

    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);
        context = this;

        String[] optionsList = getResources().getStringArray(R.array.options_array);

        ListView optionsLV = (ListView) findViewById(R.id.list_options);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, optionsList);
        optionsLV.setAdapter(adapter);

        optionsLV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {
                switch (position) {
                    case 0:
                        changeInterface();
                        break;
                    case 1:
                        cleanCache();
                        break;
                }
            }
        });
    }

    private void changeInterface() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.change_network_interface));
        final List<NetworkInterface> interfaces = NetworkUtils.getNetworkInterfacesList();
        final String actions[] = new String[interfaces.size() + 1];
        actions[0] = context.getString(R.string.default_choice);
        int selected = 0;
        for (int i = 0; i < interfaces.size(); i++) {
            actions[i + 1] = interfaces.get(i).getName();
            if (PreferencesUtils.getInterfaceName(this) != null && PreferencesUtils.getInterfaceName(this).equals(interfaces.get(i).getName())) {
                selected = i + 1;
            }
        }

        builder.setSingleChoiceItems(actions, selected, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                if (item == 0)
                    PreferencesUtils.setInterfaceName(context, null);
                else PreferencesUtils.setInterfaceName(context, actions[item]);
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void cleanCache(){
        IpPacketDBHandler dbHandler = new IpPacketDBHandler(this);
        dbHandler.resetDatabase();
    }
}
