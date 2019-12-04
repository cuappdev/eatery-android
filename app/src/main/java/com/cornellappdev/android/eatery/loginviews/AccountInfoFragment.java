package com.cornellappdev.android.eatery.loginviews;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.cornellappdev.android.eatery.R;
import com.cornellappdev.android.eatery.Repository;
import com.cornellappdev.android.eatery.model.BrbInfoModel;
import com.cornellappdev.android.eatery.util.MoneyUtil;

/**
 * This class is the redirected page after LoginFragment and OnboardingLoginFragment, and displays
 * a user's account info along with a list of past purchases
 */
public class AccountInfoFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_account_info, container, false);
        ListView historyView = rootView.findViewById(R.id.purchase_history);
        View infoHeader = inflater.inflate(R.layout.account_info_header, null);
        TextView swipesLabel = infoHeader.findViewById(R.id.swipesValue);
        TextView brbLabel = infoHeader.findViewById(R.id.brbValue);
        TextView cityBucksLabel = infoHeader.findViewById(R.id.cityBucksValue);
        TextView laundryLabel = infoHeader.findViewById(R.id.laundryValue);

        BrbInfoModel model = Repository.getInstance().getBrbInfoModel();
        String outputText;
        if (model.getSwipes() == 1) {
            outputText = model.getSwipes() + " meal left";
        } else {
            outputText = model.getSwipes() + " meals left";
        }
        swipesLabel.setText(outputText);
        brbLabel.setText(MoneyUtil.toMoneyString(model.getBRBs()));
        cityBucksLabel.setText(MoneyUtil.toMoneyString(model.getCityBucks()));
        laundryLabel.setText(MoneyUtil.toMoneyString(model.getLaundry()));
        HistoryInfoAdapter mListAdapter = new HistoryInfoAdapter(getContext(),
                R.layout.history_item,
                model.getHistory());
        historyView.setAdapter(mListAdapter);
        historyView.addHeaderView(infoHeader, null, false);

        if (getActivity() != null) {
            getActivity().setTitle("Account Info");
        }
        setHasOptionsMenu(true);
        ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (bar != null) {
            // Remove the back button
            bar.setDisplayHomeAsUpEnabled(false);
        }
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.logout_button && getFragmentManager() != null) {
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            LogoutFragment logoutFragment = new LogoutFragment();
            // Upon clicking the settings button, redirect to the logout fragment page
            transaction.replace(R.id.frame_fragment_holder, logoutFragment)
                    .addToBackStack(null)
                    .commit();
            return true;
        } else { // The user's action was not recognized, and invoke the superclass to handle it
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        if (getActivity() != null) {
            getActivity().getMenuInflater().inflate(R.menu.menu_about, menu);
        }
    }
}

