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
import it.uniroma2.giadd.aitm.models.modules.ImoASGrabber;
import it.uniroma2.giadd.aitm.models.modules.KakaoTalkASGrabber;
import it.uniroma2.giadd.aitm.models.modules.MitmModule;
import it.uniroma2.giadd.aitm.models.modules.NimbuzzASGrabber;
import it.uniroma2.giadd.aitm.models.modules.SniffBbmModule;
import it.uniroma2.giadd.aitm.models.modules.SniffGroupMeModule;
import it.uniroma2.giadd.aitm.models.modules.SniffHikeModule;
import it.uniroma2.giadd.aitm.models.modules.SniffImoModule;
import it.uniroma2.giadd.aitm.models.modules.SniffInstagramModule;
import it.uniroma2.giadd.aitm.models.modules.SniffKakaoTalkModule;
import it.uniroma2.giadd.aitm.models.modules.SniffMaaiModule;
import it.uniroma2.giadd.aitm.models.modules.SniffMxitModule;
import it.uniroma2.giadd.aitm.models.modules.SniffNimbuzzModule;
import it.uniroma2.giadd.aitm.models.modules.SniffQqModule;
import it.uniroma2.giadd.aitm.models.modules.SniffTalkrayModule;
import it.uniroma2.giadd.aitm.models.modules.SniffTelegramModule;
import it.uniroma2.giadd.aitm.models.modules.SniffTextPlusModule;
import it.uniroma2.giadd.aitm.models.modules.SniffVkModule;
import it.uniroma2.giadd.aitm.models.modules.SniffWhatsAppModule;
import it.uniroma2.giadd.aitm.models.modules.SniffYahooModule;
import it.uniroma2.giadd.aitm.models.modules.SniffZaloModule;
import it.uniroma2.giadd.aitm.models.modules.TelegramASGrabber;
import it.uniroma2.giadd.aitm.models.modules.TextPlusASGrabber;
import it.uniroma2.giadd.aitm.models.modules.VkASGrabber;
import it.uniroma2.giadd.aitm.models.modules.WhatsappASGrabber;
import it.uniroma2.giadd.aitm.models.modules.YahooASGrabber;
import it.uniroma2.giadd.aitm.models.modules.ZaloASGrabber;
import it.uniroma2.giadd.aitm.services.SniffService;
import it.uniroma2.giadd.aitm.utils.FileUtilities;

/**
 * Created by Alessandro Di Diego
 */

/** TODO: awful, horrible code; optimize this class (RecyclerView instead of tens of buttons for example)
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
            final MitmModule module;
            final AutonomousSystemGrabber grabber;
            switch (view.getId()) {
                case R.id.button_whatsapp:
                    filenameString = SniffWhatsAppModule.PREFIX + filenameString;
                    module = new SniffWhatsAppModule();
                    grabber = new WhatsappASGrabber();
                    break;
                case R.id.button_telegram:
                    filenameString = SniffTelegramModule.PREFIX + filenameString;
                    module = new SniffTelegramModule();
                    grabber = new TelegramASGrabber();
                    break;
                case R.id.button_mxit:
                    filenameString = SniffMxitModule.PREFIX + filenameString;
                    module = new SniffMxitModule();
                    grabber = null;
                    break;
                case R.id.button_vk:
                    filenameString = SniffVkModule.PREFIX + filenameString;
                    module = new SniffVkModule();
                    grabber = new VkASGrabber();
                    break;
                case R.id.button_qq:
                    filenameString = SniffQqModule.PREFIX + filenameString;
                    module = new SniffQqModule();
                    grabber = null;
                    break;
                case R.id.button_textplus:
                    filenameString = SniffTextPlusModule.PREFIX + filenameString;
                    module = new SniffTextPlusModule();
                    grabber = new TextPlusASGrabber();
                    break;
                case R.id.button_talkray:
                    filenameString = SniffTalkrayModule.PREFIX + filenameString;
                    module = new SniffTalkrayModule();
                    grabber = null;
                    break;
                case R.id.button_bbm:
                    filenameString = SniffBbmModule.PREFIX + filenameString;
                    module = new SniffBbmModule();
                    grabber = null;
                    break;
                case R.id.button_instagram:
                    filenameString = SniffInstagramModule.PREFIX + filenameString;
                    module = new SniffInstagramModule();
                    grabber = new TelegramASGrabber();
                    break;
                case R.id.button_yahoo_messenger:
                    filenameString = SniffYahooModule.PREFIX + filenameString;
                    module = new SniffYahooModule();
                    grabber = new YahooASGrabber();
                    break;
                case R.id.button_maai:
                    filenameString = SniffMaaiModule.PREFIX + filenameString;
                    module = new SniffMaaiModule();
                    grabber = null;
                    break;
                case R.id.button_groupme:
                    filenameString = SniffGroupMeModule.PREFIX + filenameString;
                    module = new SniffGroupMeModule();
                    grabber = null;
                    break;
                case R.id.button_kakaotalk:
                    filenameString = SniffKakaoTalkModule.PREFIX + filenameString;
                    module = new SniffKakaoTalkModule();
                    grabber = new KakaoTalkASGrabber();
                    break;
                case R.id.button_nimbuzz:
                    filenameString = SniffNimbuzzModule.PREFIX + filenameString;
                    module = new SniffNimbuzzModule();
                    grabber = new NimbuzzASGrabber();
                    break;
                case R.id.button_hike:
                    filenameString = SniffHikeModule.PREFIX + filenameString;
                    module = new SniffHikeModule();
                    grabber = null;
                    break;
                case R.id.button_zalo:
                    filenameString = SniffZaloModule.PREFIX + filenameString;
                    module = new SniffZaloModule();
                    grabber = new ZaloASGrabber();
                    break;
                case R.id.button_imo:
                    filenameString = SniffImoModule.PREFIX + filenameString;
                    module = new SniffImoModule();
                    grabber = new ImoASGrabber();
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
