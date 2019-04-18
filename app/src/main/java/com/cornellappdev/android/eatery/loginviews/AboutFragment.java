package com.cornellappdev.android.eatery.loginviews;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
        getActivity().setTitle("About");
        TextView feedbackText = rootView.findViewById(R.id.feedbackText);
        TextView websiteText = rootView.findViewById(R.id.websiteText);
        feedbackText.setMovementMethod(LinkMovementMethod.getInstance());
        websiteText.setMovementMethod(LinkMovementMethod.getInstance());

        websiteText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(getResources().getString(R.string.cornell_website_url));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        feedbackText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(getResources().getString(R.string.feedback_form_url));
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });

        setHasOptionsMenu(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        } else { // The user's action was not recognized, and invoke the superclass to handle it.
            return super.onOptionsItemSelected(item);
        }
    }
}