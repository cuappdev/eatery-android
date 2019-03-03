package com.cornellappdev.android.eatery;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
    private ArrayAdapter mListAdapter;
    private ListView mHistoryView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account_info, container, false);
        getActivity().setTitle("Account Info");
        brbText = rootView.findViewById(R.id.brbText);
        laundryText = rootView.findViewById(R.id.laundryText);
        swipesText = rootView.findViewById(R.id.swipesText);
        mHistoryView = rootView.findViewById(R.id.historylistview);
        BrbInfoQuery.AccountInfo brbInfo = NetworkUtilities.getBrbInfo(MainActivity.sSessionId);
        BrbInfoModel model = JsonUtilities.parseBrbInfo(brbInfo);
        brbText.setText("BRBS: "+model.getBRBs());
        laundryText.setText("Meal swipes: "+model.getSwipes());
        swipesText.setText("Laundry: "+model.getLaundry());

        Log.i("MODEL LENGTH", model.getHistory().size()+"");
        mListAdapter = new ArrayAdapter(getContext(), R.layout.history_item, R.id.menu_title, model.getHistory());
        mHistoryView.setAdapter(mListAdapter);
        return rootView;
    }
}
