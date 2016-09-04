package it.uniroma2.giadd.aitm.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
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

import java.util.ArrayList;
import java.util.List;

import it.uniroma2.giadd.aitm.R;
import it.uniroma2.giadd.aitm.adapters.NetworkHostsAdapter;
import it.uniroma2.giadd.aitm.models.NetworkHost;
import it.uniroma2.giadd.aitm.tasks.NetworkHostScannerTask;
import it.uniroma2.giadd.aitm.utils.NetworkUtils;
import it.uniroma2.giadd.aitm.utils.RecyclerTouchListener;

/**
 * Created by Alessandro Di Diego
 */

public class ScannerFragment extends Fragment implements LoaderManager.LoaderCallbacks<List<NetworkHost>> {

    private NetworkHostsAdapter networkHostsAdapter;
    private ArrayList<NetworkHost> networkHosts;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parentViewGroup,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_scanner, parentViewGroup, false);


        networkHosts = new ArrayList<>();
        RecyclerView recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        networkHostsAdapter = new NetworkHostsAdapter(networkHosts);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(networkHostsAdapter);
        // we can enable optimizations if all item views are of the same height and width for significantly smoother scrolling
        recyclerView.setHasFixedSize(true);
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                NetworkHost networkHost = networkHosts.get(position);
                TargetFragment fragment = TargetFragment.newInstance(networkHost);
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction().setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                fragmentTransaction.replace(R.id.fragment_container, fragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        return rootView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Toolbar toolbar = (Toolbar) getActivity().findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.scanner_menu);
        MenuItem actionRefresh = toolbar.getMenu().findItem(R.id.action_refresh);
        actionRefresh.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                return onOptionsItemSelected(menuItem);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected
            case R.id.action_refresh:
                // check if wifi is connected
                if (NetworkUtils.checkActiveConnectionType(getContext()) != NetworkUtils.TYPE_WIFI) {
                    if (getView() != null) {
                        Snackbar snackbar = Snackbar
                                .make(getView(), R.string.error_scanner_not_connected_to_wifi, Snackbar.LENGTH_LONG);
                        snackbar.show();
                    }
                    break;
                }
                if (getView() != null) {
                    getView().findViewById(R.id.loading_circle).setVisibility(View.VISIBLE);
                }
                // id is fragment-unique so we can use 0
                getLoaderManager().restartLoader(0, null, this);
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<List<NetworkHost>> onCreateLoader(int id, Bundle args) {
        return new NetworkHostScannerTask(getContext());
    }

    @Override
    public void onLoadFinished(Loader<List<NetworkHost>> loader, List<NetworkHost> data) {
        networkHosts.removeAll(data);
        for (NetworkHost networkHost : networkHosts) {
            networkHost.setReachable(false);
        }
        networkHosts.addAll(0, data);
        networkHostsAdapter.notifyDataSetChanged();
        if (getView() != null) {
            getView().findViewById(R.id.loading_circle).setVisibility(View.GONE);
            TextView textView = (TextView) getView().findViewById(R.id.empty_list_textview);
            if (networkHosts.size() == 0) textView.setText(R.string.no_host_found);
            else textView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<NetworkHost>> loader) {

    }

    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

}
