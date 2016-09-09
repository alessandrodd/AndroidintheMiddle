package it.uniroma2.giadd.aitm;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import java.util.List;

import it.uniroma2.giadd.aitm.adapters.PacketDetailPagerAdapter;
import it.uniroma2.giadd.aitm.models.MyIpPacket;

public class PacketDetailActivity extends AppCompatActivity {

    public static String PACKETLIST_KEY = "PACKETLIST_KEY";
    public static String SELECTED_PACKET_NUMBER_KEY = "SELECTED_PACKET_NUMBER_KEY";

    private PacketDetailPagerAdapter packetDetailPagerAdapter;
    private List<MyIpPacket> ipPacketList;
    private int selectedPacket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_packet_detail);
        // setting toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                if (packetDetailPagerAdapter != null)
                    setTitle(packetDetailPagerAdapter.getPageTitle(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        if (getIntent() != null) {
            ipPacketList = getIntent().getParcelableArrayListExtra(PACKETLIST_KEY);
            selectedPacket = getIntent().getIntExtra(SELECTED_PACKET_NUMBER_KEY, 0);
        }

        if (ipPacketList != null) {
            packetDetailPagerAdapter = new PacketDetailPagerAdapter(getSupportFragmentManager(), ipPacketList);
            viewPager.setAdapter(packetDetailPagerAdapter);
            viewPager.setCurrentItem(selectedPacket);
            setTitle(packetDetailPagerAdapter.getPageTitle(selectedPacket));
        }
    }


}
