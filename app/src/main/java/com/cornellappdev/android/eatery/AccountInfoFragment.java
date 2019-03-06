package com.cornellappdev.android.eatery;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.cornellappdev.android.eatery.model.BrbInfoModel;
import com.cornellappdev.android.eatery.network.JsonUtilities;
import com.cornellappdev.android.eatery.network.NetworkUtilities;
import com.cornellappdev.android.eatery.util.MoneyUtil;

public class AccountInfoFragment extends Fragment {

    private TextView mSwipesLabel;
    private TextView mBrbLabel;
    private TextView mCityBucksLabel;
    private TextView mLaundryLabel;
    private String mSessionId;
    private HistoryInfoAdapter mListAdapter;
    private ListView mHistoryView;
    private View mInfoHeader;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account_info, container, false);
        mHistoryView = rootView.findViewById(R.id.historylistview);

        mInfoHeader = inflater.inflate(R.layout.account_info_header, null);
        getActivity().setTitle("Account Info");
        mSwipesLabel = mInfoHeader.findViewById(R.id.swipesValue);
        mBrbLabel = mInfoHeader.findViewById(R.id.brbValue);
        mCityBucksLabel = mInfoHeader.findViewById(R.id.cityBucksValue);
        mLaundryLabel = mInfoHeader.findViewById(R.id.laundryValue);

        BrbInfoModel model = Repository.getInstance().getBrbInfoModel();
        if(model.getSwipes() == 1) {
            mSwipesLabel.setText(model.getSwipes() + " meal left");
        } else {
            mSwipesLabel.setText(model.getSwipes() + " meals left");
        }
        mBrbLabel.setText(MoneyUtil.toMoneyString(model.getBRBs()));
        mCityBucksLabel.setText(MoneyUtil.toMoneyString(model.getCityBucks()));
        mLaundryLabel.setText(MoneyUtil.toMoneyString(model.getLaundry()));

        mListAdapter = new HistoryInfoAdapter(getContext(), R.layout.history_item, model.getHistory());
        mHistoryView.setAdapter(mListAdapter);
        mHistoryView.addHeaderView(mInfoHeader, null, false);

        return rootView;
    }

}
