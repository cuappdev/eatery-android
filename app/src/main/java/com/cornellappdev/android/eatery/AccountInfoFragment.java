package com.cornellappdev.android.eatery;

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

public class AccountInfoFragment extends Fragment {

    private TextView brbText;
    private TextView laundryText;
    private TextView swipesText;
    private String mSessionId;
    private HistoryInfoAdapter mListAdapter;
    private ListView mHistoryView;
    private View infoHeader;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account_info, container, false);
        mHistoryView = rootView.findViewById(R.id.historylistview);

        infoHeader = inflater.inflate(R.layout.account_info_header, null);
        getActivity().setTitle("Account Info");
        brbText = infoHeader.findViewById(R.id.brbText);
        laundryText = infoHeader.findViewById(R.id.laundryText);
        swipesText = infoHeader.findViewById(R.id.swipesText);

        BrbInfoQuery.AccountInfo brbInfo = NetworkUtilities.getBrbInfo(MainActivity.sSessionId);
        BrbInfoModel model = JsonUtilities.parseBrbInfo(brbInfo);
        brbText.setText("BRBS: "+model.getBRBs());
        laundryText.setText("Meal swipes: "+model.getSwipes());
        swipesText.setText("Laundry: "+model.getLaundry());

        mListAdapter = new HistoryInfoAdapter(getContext(), R.layout.history_item, model.getHistory());
        mHistoryView.setAdapter(mListAdapter);
        mHistoryView.addHeaderView(infoHeader, null, false);
        return rootView;
    }
}
