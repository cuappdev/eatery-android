package com.cornellappdev.android.eatery.loginviews;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.cornellappdev.android.eatery.MainActivity;
import com.cornellappdev.android.eatery.R;
import com.cornellappdev.android.eatery.model.enums.CacheType;
import com.cornellappdev.android.eatery.presenter.AccountPresenter;
import com.cornellappdev.android.eatery.util.InternalStorage;

import java.io.IOException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

/**
 * This fragment is the page reached upon clicking the gear icon in the upper right of
 * AccountInfoFragment. Upon clicking logout, it redirects the user to LoginFragment, and
 * also has a redirect to an about page (AboutFragment)
 */
public class LogoutFragment extends Fragment {

    private Button mLogoutButton;
    private RelativeLayout mAboutArea;
    // re-using the same kind of presenter
    private MainActivity mainActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_logout, container, false);
        mLogoutButton = rootView.findViewById(R.id.logout);
        mainActivity = (MainActivity) getActivity();

        mLogoutButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mainActivity.setAccountPresenterBrbInfo(null);
                mainActivity.eraseAccountPresenterJS();
                try {
                    InternalStorage.writeObject(getContext(), CacheType.BRB, null);
                } catch (IOException e) {
                    e.printStackTrace();
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
        getActivity().onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
