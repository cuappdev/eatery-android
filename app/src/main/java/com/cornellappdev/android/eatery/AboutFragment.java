package com.cornellappdev.android.eatery;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.cornellappdev.android.eatery.model.EateryBaseModel;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {

	private TextView mFeedbackText;
	private TextView mWebsiteText;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_about, container, false);
		getActivity().setTitle("About");

		mFeedbackText = rootView.findViewById(R.id.feedbackText);
		mWebsiteText = rootView.findViewById(R.id.websiteText);

		mFeedbackText.setMovementMethod(LinkMovementMethod.getInstance());
		mWebsiteText.setMovementMethod(LinkMovementMethod.getInstance());

		mWebsiteText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Uri uri = Uri.parse(getResources().getString(R.string.cornell_website_url));
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}
		});

		mFeedbackText.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Uri uri = Uri.parse(getResources().getString(R.string.feedback_form_url));
				Intent intent = new Intent(Intent.ACTION_VIEW, uri);
				startActivity(intent);
			}
		});

		return rootView;
	}
}
