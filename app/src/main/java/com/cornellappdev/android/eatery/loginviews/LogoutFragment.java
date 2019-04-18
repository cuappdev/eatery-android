package com.cornellappdev.android.eatery.loginviews;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

/**
 * This fragment is the page reached upon clicking the gear icon in the upper right of
 * AccountInfoFragment. Upon clicking logout, it redirects the user to LoginFragment, and
 * also has a redirect to an about page (AboutFragment)
 */
import com.cornellappdev.android.eatery.MainActivity;
import com.cornellappdev.android.eatery.R;
import com.cornellappdev.android.eatery.presenter.AccountPresenter;

public class LogoutFragment extends Fragment {

    private Button mLogoutButton;
    private CheckBox mSaveInfoCheck;
    private RelativeLayout mAboutArea;
    // re-using the same kind of presenter
    private AccountPresenter mAccountPresenter = new AccountPresenter();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_logout, container, false);
        mLogoutButton = rootView.findViewById(R.id.logout);
        mAccountPresenter.setContext(getContext());
        String[] loginInfo = mAccountPresenter.readSavedCredentials();
        mAccountPresenter.setSaveCredentials(loginInfo != null);

        mSaveInfoCheck = rootView.findViewById(R.id.saveCheckInfo);
        mSaveInfoCheck.setChecked(mAccountPresenter.getSaveCredentials());
        mSaveInfoCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mAccountPresenter.setSaveCredentials(isChecked);
            }
        });
        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mAccountPresenter.setBrbModel(null);
                if (!mAccountPresenter.getSaveCredentials()) {
                    mAccountPresenter.setContext(getContext());
                    mAccountPresenter.eraseSavedCredentials();
                }
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                LoginFragment loginFragment = new LoginFragment();
                // Reset the current login instance of MainActivity so upon navigating back so it
                // doesn't have filled in text inputs
                ((MainActivity) getActivity()).setLoginInstance(loginFragment);
                transaction.replace(R.id.frame_fragment_holder, loginFragment).commit();
            }
        });
        mAboutArea = rootView.findViewById(R.id.about_segway);
        mAboutArea.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // About area has been clicked, navigate to the AboutFragment page
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                AboutFragment aboutFragment = new AboutFragment();
                transaction.replace(R.id.frame_fragment_holder, aboutFragment).addToBackStack(null)
                        .commit();
            }
        });

        setHasOptionsMenu(true);
        getActivity().setTitle("Account Info");
        // Enable the back button in the actionbar menu
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (!mAccountPresenter.getSaveCredentials()) {
                    // If the saveInfo checkbox is not checked, erase all data upon going back
                    mAccountPresenter.setContext(getContext());
                    mAccountPresenter.eraseSavedCredentials();
                }
                getActivity().onBackPressed();
                return true;
            default:
                // The user's action was not recognized, and invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }
}
