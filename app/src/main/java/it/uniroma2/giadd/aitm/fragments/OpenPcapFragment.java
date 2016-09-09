package it.uniroma2.giadd.aitm.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.angads25.filepicker.controller.DialogSelectionListener;
import com.github.angads25.filepicker.model.DialogConfigs;
import com.github.angads25.filepicker.model.DialogProperties;
import com.github.angads25.filepicker.view.FilePickerDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import it.uniroma2.giadd.aitm.CaptureActivity;
import it.uniroma2.giadd.aitm.R;
import it.uniroma2.giadd.aitm.adapters.NetworkHostsAdapter;
import it.uniroma2.giadd.aitm.interfaces.SimpleClickListener;
import it.uniroma2.giadd.aitm.models.NetworkHost;
import it.uniroma2.giadd.aitm.services.ParsePcapService;
import it.uniroma2.giadd.aitm.tasks.NetworkHostScannerTask;
import it.uniroma2.giadd.aitm.utils.NetworkUtils;
import it.uniroma2.giadd.aitm.utils.RecyclerTouchListener;

/**
 * Created by Alessandro Di Diego
 */

public class OpenPcapFragment extends Fragment {

    private AppCompatButton openPcapButton;
    private FilePickerDialog dialog;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parentViewGroup,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_openpcap, parentViewGroup, false);
        openPcapButton = (AppCompatButton) rootView.findViewById(R.id.open_pcap_button);
        DialogProperties properties=new DialogProperties();
        properties.selection_mode=DialogConfigs.SINGLE_MODE;
        properties.selection_type=DialogConfigs.FILE_SELECT;
        properties.root=new File(Environment.getExternalStorageDirectory() + "/pcaps");
        properties.error_dir=new File(DialogConfigs.DEFAULT_DIR);
        properties.extensions=new String[]{"pcap","cap"};
        dialog = new FilePickerDialog(getContext(),properties);
        dialog.setDialogSelectionListener(new DialogSelectionListener() {
            @Override
            public void onSelectedFilePaths(String[] files) {
                //files is the array of the paths of files selected by the Application User.
                if(files!=null && files.length>0) {
                    Intent intent = new Intent(getContext(), ParsePcapService.class);
                    intent.putExtra(ParsePcapService.PCAP_PATH, files[0]);
                    getContext().startService(intent);
                    intent = new Intent(getContext(), CaptureActivity.class);
                    startActivity(intent);
                }
            }
        });

        openPcapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dialog!=null) dialog.show();
            }
        });


        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onPause() {
        super.onPause();
        if(dialog!=null) dialog.dismiss();
    }
}
