package it.uniroma2.giadd.aitm.adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

import it.uniroma2.giadd.aitm.fragments.PacketDetailFragment;
import it.uniroma2.giadd.aitm.models.MyIpPacket;

/**
 * Created by Alessandro Di Diego on 09/09/16.
 */

public class PacketDetailPagerAdapter extends FragmentStatePagerAdapter {
    private List<MyIpPacket> packetList;

    public PacketDetailPagerAdapter(FragmentManager fm, List<MyIpPacket> packetList) {
        super(fm);
        this.packetList = packetList;
    }

    @Override
    public Fragment getItem(int i) {
        Fragment fragment = new PacketDetailFragment();
        Bundle args = new Bundle();
        // Our object is just an integer :-P
        args.putParcelable(PacketDetailFragment.SELECTED_PACKET_KEY, packetList.get(i));
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public int getCount() {
        if (packetList == null) return 0;
        return packetList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "IP Packet " + (position + 1) + "/" + getCount();
    }
}
