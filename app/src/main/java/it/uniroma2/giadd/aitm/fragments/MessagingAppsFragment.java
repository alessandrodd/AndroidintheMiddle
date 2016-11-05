package it.uniroma2.giadd.aitm.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import it.uniroma2.giadd.aitm.CaptureActivity;
import it.uniroma2.giadd.aitm.R;
import it.uniroma2.giadd.aitm.interfaces.AutonomousSystemGrabber;
import it.uniroma2.giadd.aitm.interfaces.OnAutonomousSystemGrabbedListener;
import it.uniroma2.giadd.aitm.models.NetworkHost;
import it.uniroma2.giadd.aitm.models.modules.ASGrabberImo;
import it.uniroma2.giadd.aitm.models.modules.ASGrabberKakaoTalk;
import it.uniroma2.giadd.aitm.models.modules.ModuleMitm;
import it.uniroma2.giadd.aitm.models.modules.ASGrabberNimbuzz;
import it.uniroma2.giadd.aitm.models.modules.ModuleSniffBbm;
import it.uniroma2.giadd.aitm.models.modules.ModuleSniffGroupMe;
import it.uniroma2.giadd.aitm.models.modules.ModuleSniffHike;
import it.uniroma2.giadd.aitm.models.modules.ModuleSniffImo;
import it.uniroma2.giadd.aitm.models.modules.ModuleSniffInstagram;
import it.uniroma2.giadd.aitm.models.modules.ModuleSniffKakaoTalk;
import it.uniroma2.giadd.aitm.models.modules.ModuleSniffMaai;
import it.uniroma2.giadd.aitm.models.modules.ModuleSniffMxit;
import it.uniroma2.giadd.aitm.models.modules.ModuleSniffNimbuzz;
import it.uniroma2.giadd.aitm.models.modules.ModuleSniffQq;
import it.uniroma2.giadd.aitm.models.modules.ModuleSniffTalkray;
import it.uniroma2.giadd.aitm.models.modules.ModuleSniffTelegram;
import it.uniroma2.giadd.aitm.models.modules.ModuleSniffTextPlus;
import it.uniroma2.giadd.aitm.models.modules.ModuleSniffVk;
import it.uniroma2.giadd.aitm.models.modules.ModuleSniffWhatsApp;
import it.uniroma2.giadd.aitm.models.modules.ModuleSniffYahoo;
import it.uniroma2.giadd.aitm.models.modules.ModuleSniffZalo;
import it.uniroma2.giadd.aitm.models.modules.ASGrabberTelegram;
import it.uniroma2.giadd.aitm.models.modules.ASGrabberTextPlus;
import it.uniroma2.giadd.aitm.models.modules.ASGrabberVk;
import it.uniroma2.giadd.aitm.models.modules.ASGrabberWhatsapp;
import it.uniroma2.giadd.aitm.models.modules.ASGrabberYahoo;
import it.uniroma2.giadd.aitm.models.modules.ASGrabberZalo;
import it.uniroma2.giadd.aitm.services.SniffService;
import it.uniroma2.giadd.aitm.utils.FileUtilities;

/**
 * Created by Alessandro Di Diego
 */

/** TODO: awful, horrible, boilerplate code; optimize this class (RecyclerView instead of tens of buttons for example)
 */

public class MessagingAppsFragment extends Fragment {

    private static final String TAG = MessagingAppsFragment.class.getName();
    private static final String HOST_KEY = "HOST_KEY";

    private NetworkHost host;

    public static MessagingAppsFragment newInstance(NetworkHost networkHost) {
        MessagingAppsFragment myFragment = new MessagingAppsFragment();
        Bundle args = new Bundle();
        args.putParcelable(HOST_KEY, networkHost);
        myFragment.setArguments(args);
        return myFragment;
    }

    public View.OnClickListener buttonManager = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            final AlertDialog.Builder popDialog = new AlertDialog.Builder(getContext());
            final EditText fileNameEditText = new EditText(getContext());
            Date now = new Date(); // java.util.Date, NOT java.sql.Date or java.sql.Timestamp!
            String filenameString = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(now);
            popDialog.setTitle(R.string.title_set_capture_filename);
            popDialog.setView(fileNameEditText);
            final ModuleMitm module;
            final AutonomousSystemGrabber grabber;
            switch (view.getId()) {
                case R.id.button_whatsapp:
                    filenameString = ModuleSniffWhatsApp.PREFIX + filenameString;
                    module = new ModuleSniffWhatsApp();
                    grabber = new ASGrabberWhatsapp();
                    break;
                case R.id.button_telegram:
                    filenameString = ModuleSniffTelegram.PREFIX + filenameString;
                    module = new ModuleSniffTelegram();
                    grabber = new ASGrabberTelegram();
                    break;
                case R.id.button_mxit:
                    filenameString = ModuleSniffMxit.PREFIX + filenameString;
                    module = new ModuleSniffMxit();
                    grabber = null;
                    break;
                case R.id.button_vk:
                    filenameString = ModuleSniffVk.PREFIX + filenameString;
                    module = new ModuleSniffVk();
                    grabber = new ASGrabberVk();
                    break;
                case R.id.button_qq:
                    filenameString = ModuleSniffQq.PREFIX + filenameString;
                    module = new ModuleSniffQq();
                    grabber = null;
                    break;
                case R.id.button_textplus:
                    filenameString = ModuleSniffTextPlus.PREFIX + filenameString;
                    module = new ModuleSniffTextPlus();
                    grabber = new ASGrabberTextPlus();
                    break;
                case R.id.button_talkray:
                    filenameString = ModuleSniffTalkray.PREFIX + filenameString;
                    module = new ModuleSniffTalkray();
                    grabber = null;
                    break;
                case R.id.button_bbm:
                    filenameString = ModuleSniffBbm.PREFIX + filenameString;
                    module = new ModuleSniffBbm();
                    grabber = null;
                    break;
                case R.id.button_instagram:
                    filenameString = ModuleSniffInstagram.PREFIX + filenameString;
                    module = new ModuleSniffInstagram();
                    grabber = new ASGrabberTelegram();
                    break;
                case R.id.button_yahoo_messenger:
                    filenameString = ModuleSniffYahoo.PREFIX + filenameString;
                    module = new ModuleSniffYahoo();
                    grabber = new ASGrabberYahoo();
                    break;
                case R.id.button_maai:
                    filenameString = ModuleSniffMaai.PREFIX + filenameString;
                    module = new ModuleSniffMaai();
                    grabber = null;
                    break;
                case R.id.button_groupme:
                    filenameString = ModuleSniffGroupMe.PREFIX + filenameString;
                    module = new ModuleSniffGroupMe();
                    grabber = null;
                    break;
                case R.id.button_kakaotalk:
                    filenameString = ModuleSniffKakaoTalk.PREFIX + filenameString;
                    module = new ModuleSniffKakaoTalk();
                    grabber = new ASGrabberKakaoTalk();
                    break;
                case R.id.button_nimbuzz:
                    filenameString = ModuleSniffNimbuzz.PREFIX + filenameString;
                    module = new ModuleSniffNimbuzz();
                    grabber = new ASGrabberNimbuzz();
                    break;
                case R.id.button_hike:
                    filenameString = ModuleSniffHike.PREFIX + filenameString;
                    module = new ModuleSniffHike();
                    grabber = null;
                    break;
                case R.id.button_zalo:
                    filenameString = ModuleSniffZalo.PREFIX + filenameString;
                    module = new ModuleSniffZalo();
                    grabber = new ASGrabberZalo();
                    break;
                case R.id.button_imo:
                    filenameString = ModuleSniffImo.PREFIX + filenameString;
                    module = new ModuleSniffImo();
                    grabber = new ASGrabberImo();
                    break;
                default:
                    module = null;
                    grabber = null;
            }
            fileNameEditText.setText(filenameString);
            popDialog.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    String insertedString = fileNameEditText.getText().toString();
                    if (FileUtilities.getInvalidCharacter(insertedString) != null && getView() != null) {
                        Snackbar.make(getView(), getString(R.string.error_illegal_character) + " " + FileUtilities.getInvalidCharacter(insertedString), Snackbar.LENGTH_SHORT).show();
                        return;
                    }
                    final String desiredPcapPath = Environment.getExternalStorageDirectory() + "/pcaps" + "/" + insertedString + ".pcap";
                    if (grabber != null) {
                        grabber.getRoutingPrefixes(getContext(), new OnAutonomousSystemGrabbedListener() {
                            @Override
                            public void onSuccess(List<String> prefixes) {
                                module.setDumpPath(desiredPcapPath);
                                module.setNets(prefixes);
                                module.setTarget(host.getIp());
                                module.initialize(getContext());
                                Intent i = new Intent(getContext(), SniffService.class);
                                i.putExtra(SniffService.MITM_MODULE, module);
                                getContext().startService(i);
                                i = new Intent(getContext(), CaptureActivity.class);
                                startActivity(i);
                            }

                            @Override
                            public void onError(String message) {
                                Snackbar.make(view, message, Snackbar.LENGTH_LONG).show();
                            }
                        });
                    } else if (module != null) {
                        module.setDumpPath(desiredPcapPath);
                        module.setTarget(host.getIp());
                        module.initialize(getContext());
                        Intent i = new Intent(getContext(), SniffService.class);
                        i.putExtra(SniffService.MITM_MODULE, module);
                        getContext().startService(i);
                        i = new Intent(getContext(), CaptureActivity.class);
                        startActivity(i);
                    }
                }
            });
            popDialog.create();
            popDialog.show();
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            host = bundle.getParcelable(HOST_KEY);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parentViewGroup,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_messaging_app, parentViewGroup, false);
        CardView whatsappButton = (CardView) rootView.findViewById(R.id.button_whatsapp);
        CardView telegramButton = (CardView) rootView.findViewById(R.id.button_telegram);
        CardView mxitButton = (CardView) rootView.findViewById(R.id.button_mxit);
        CardView vkButton = (CardView) rootView.findViewById(R.id.button_vk);
        CardView qqButton = (CardView) rootView.findViewById(R.id.button_qq);
        CardView textplusButton = (CardView) rootView.findViewById(R.id.button_textplus);
        CardView talkrayButton = (CardView) rootView.findViewById(R.id.button_talkray);
        CardView bbmButton = (CardView) rootView.findViewById(R.id.button_bbm);
        CardView instagramButton = (CardView) rootView.findViewById(R.id.button_instagram);
        CardView yahooMessengerButton = (CardView) rootView.findViewById(R.id.button_yahoo_messenger);
        CardView maaiButton = (CardView) rootView.findViewById(R.id.button_maai);
        CardView groupmeButton = (CardView) rootView.findViewById(R.id.button_groupme);
        CardView kakaotalkButton = (CardView) rootView.findViewById(R.id.button_kakaotalk);
        CardView nimbuzzButton = (CardView) rootView.findViewById(R.id.button_nimbuzz);
        CardView hikeButton = (CardView) rootView.findViewById(R.id.button_hike);
        CardView zaloButton = (CardView) rootView.findViewById(R.id.button_zalo);
        CardView imoButton = (CardView) rootView.findViewById(R.id.button_imo);
        if (host != null) {
            whatsappButton.setOnClickListener(buttonManager);
            telegramButton.setOnClickListener(buttonManager);
            mxitButton.setOnClickListener(buttonManager);
            vkButton.setOnClickListener(buttonManager);
            qqButton.setOnClickListener(buttonManager);
            textplusButton.setOnClickListener(buttonManager);
            talkrayButton.setOnClickListener(buttonManager);
            bbmButton.setOnClickListener(buttonManager);
            instagramButton.setOnClickListener(buttonManager);
            yahooMessengerButton.setOnClickListener(buttonManager);
            maaiButton.setOnClickListener(buttonManager);
            groupmeButton.setOnClickListener(buttonManager);
            kakaotalkButton.setOnClickListener(buttonManager);
            nimbuzzButton.setOnClickListener(buttonManager);
            hikeButton.setOnClickListener(buttonManager);
            zaloButton.setOnClickListener(buttonManager);
            imoButton.setOnClickListener(buttonManager);

        } else Log.e(TAG, "Error: host cannot be null!");
        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            host = savedInstanceState.getParcelable(HOST_KEY);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(HOST_KEY, host);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

}
