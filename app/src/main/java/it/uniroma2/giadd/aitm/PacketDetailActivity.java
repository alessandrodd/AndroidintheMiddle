package it.uniroma2.giadd.aitm;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import it.uniroma2.giadd.aitm.adapters.PacketDetailPagerAdapter;
import it.uniroma2.giadd.aitm.utils.IpPacketDBHandler;

public class PacketDetailActivity extends AppCompatActivity {

    public static String SELECTED_PACKET_POSITION = "SELECTED_PACKET_POSITION";

    private PacketDetailPagerAdapter packetDetailPagerAdapter;
    private int selectedPacketPosition;


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
            selectedPacketPosition = getIntent().getIntExtra(SELECTED_PACKET_POSITION, 0);
        }
        IpPacketDBHandler dbHandler = new IpPacketDBHandler(this);
        Cursor cursor = dbHandler.getAllIpPacketsCursor();
        if (cursor != null) {
            packetDetailPagerAdapter = new PacketDetailPagerAdapter(this, getSupportFragmentManager(), cursor);
            viewPager.setAdapter(packetDetailPagerAdapter);
            viewPager.setCurrentItem(selectedPacketPosition);
            setTitle(packetDetailPagerAdapter.getPageTitle(viewPager.getCurrentItem()));
        }
    }


}
