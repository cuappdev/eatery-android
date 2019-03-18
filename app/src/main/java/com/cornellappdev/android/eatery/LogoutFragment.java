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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;

import com.cornellappdev.android.eatery.presenter.AccountPresenter;

public class LogoutFragment extends Fragment {

    private Button mLogoutButton;
    private CheckBox mSaveInfoCheck;
    private RelativeLayout mAboutArea;
    private AccountPresenter mAccountPresenter = new AccountPresenter();
    // re-using the same kind of presenter

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_logout, container, false);
        mLogoutButton = rootView.findViewById(R.id.logout);
        mSaveInfoCheck = rootView.findViewById(R.id.saveCheckInfo);

        mAccountPresenter.setContext(getContext());
        String[] loginInfo = mAccountPresenter.readSavedCredentials();
        if (loginInfo != null) {
            mAccountPresenter.setSaveCredentials(true);
        } else {
            mAccountPresenter.setSaveCredentials(false);
        }
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
                ((MainActivity) getActivity()).setLoginInstance(loginFragment);
                // Reset the current login instance of MainActivity so upon navigating back it
                // doesn't
                // have all the stored information from before (netid, password)
                transaction
                        .replace(R.id.frame_fragment_holder, loginFragment)
                        .commit();
            }
        });

        mAboutArea = rootView.findViewById(R.id.about_segway);
        mAboutArea.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // About area has been clicked, navigate to the AboutFragment page
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                AboutFragment aboutFragment = new AboutFragment();
                transaction
                        .replace(R.id.frame_fragment_holder, aboutFragment)
                        .addToBackStack(null)
                        .commit();
            }
        });

        setHasOptionsMenu(true);
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.back:
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.menu_with_back, menu);
    }
}
