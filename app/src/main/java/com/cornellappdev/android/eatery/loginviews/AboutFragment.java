package com.cornellappdev.android.eatery.loginviews;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.cornellappdev.android.eatery.R;

/**
 * This class can be reached from LogoutFragment, and describes the gist of Eatery and provides
 * a link to AppDev's website
 */
public class AboutFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        if (getActivity() != null) {
            getActivity().setTitle("About");
        }
        TextView feedbackText = rootView.findViewById(R.id.feedbackText);
        TextView websiteText = rootView.findViewById(R.id.websiteText);
        feedbackText.setMovementMethod(LinkMovementMethod.getInstance());
        websiteText.setMovementMethod(LinkMovementMethod.getInstance());

        websiteText.setOnClickListener((View v) -> {
            Uri uri = Uri.parse(getResources().getString(R.string.cornell_website_url));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });
        feedbackText.setOnClickListener((View v) -> {
            Uri uri = Uri.parse(getResources().getString(R.string.feedback_form_url));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            startActivity(intent);
        });

        setHasOptionsMenu(true);

        ActionBar bar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home && getActivity() != null) {
            getActivity().onBackPressed();
            return true;
        } else { // The user's action was not recognized, and invoke the superclass to handle it.
            return super.onOptionsItemSelected(item);
        }
    }
}