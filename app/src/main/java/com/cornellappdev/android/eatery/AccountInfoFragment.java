package com.cornellappdev.android.eatery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.cornellappdev.android.eatery.model.BrbInfoModel;
import com.cornellappdev.android.eatery.util.MoneyUtil;

public class AccountInfoFragment extends Fragment {

    private TextView mSwipesLabel;
    private TextView mBrbLabel;
    private TextView mCityBucksLabel;
    private TextView mLaundryLabel;
    private HistoryInfoAdapter mListAdapter;
    private ListView mHistoryView;
    private View mInfoHeader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account_info, container, false);
        mHistoryView = rootView.findViewById(R.id.historylistview);
        mInfoHeader = inflater.inflate(R.layout.account_info_header, null);
        mSwipesLabel = mInfoHeader.findViewById(R.id.swipesValue);
        mBrbLabel = mInfoHeader.findViewById(R.id.brbValue);
        mCityBucksLabel = mInfoHeader.findViewById(R.id.cityBucksValue);
        mLaundryLabel = mInfoHeader.findViewById(R.id.laundryValue);

        BrbInfoModel model = Repository.getInstance().getBrbInfoModel();
        String outputText = "";
        if (model.getSwipes() == 1) {
            outputText = model.getSwipes() + " meal left";
        } else {
            outputText = model.getSwipes() + " meals left";
        }
        mSwipesLabel.setText(outputText);
        mBrbLabel.setText(MoneyUtil.toMoneyString(model.getBRBs()));
        mCityBucksLabel.setText(MoneyUtil.toMoneyString(model.getCityBucks()));
        mLaundryLabel.setText(MoneyUtil.toMoneyString(model.getLaundry()));

        mListAdapter = new HistoryInfoAdapter(getContext(), R.layout.history_item,
                model.getHistory());
        mHistoryView.setAdapter(mListAdapter);
        mHistoryView.addHeaderView(mInfoHeader, null, false);

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout_button:
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                LogoutFragment logoutFragment = new LogoutFragment();
                // Upon clicking the settings button, redirect the logout fragment page
                transaction
                        .replace(R.id.frame_fragment_holder, logoutFragment)
                        .addToBackStack(null)
                        .commit();
                return true;
            default:
                // The user's action was not recognized, and invoke the superclass to handle it
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_about, menu);
        getActivity().setTitle("Account Info");

    }
}

