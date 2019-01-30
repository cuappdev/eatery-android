package com.cornellappdev.android.eatery;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cornellappdev.android.eatery.presenter.MainListPresenter;

public class MainListFragment extends Fragment {
	private MainListPresenter presenter;

	public MainListFragment() {
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_main_list, container, false);

		presenter = new MainListPresenter(rootView);
		Log.d("TAG-mainlist", presenter.getEateryList().toString());
		return rootView;
	}

}
