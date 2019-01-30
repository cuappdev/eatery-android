package com.cornellappdev.android.eatery;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.cornellappdev.android.eatery.model.EateryBaseModel;
import com.cornellappdev.android.eatery.presenter.MainListPresenter;

import java.util.ArrayList;

public class MainListFragment extends Fragment
		implements MainListPresenter.MainListView, MainListAdapter.ListAdapterOnClickHandler {
	private MainListPresenter presenter;
	private RecyclerView mRecyclerView;
	private MainListAdapter listAdapter;

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
		mRecyclerView = rootView.findViewById(R.id.cafe_list);
		presenter = new MainListPresenter(rootView);
		mRecyclerView.setHasFixedSize(true);
		LinearLayoutManager layoutManager =
				new LinearLayoutManager(getContext(), LinearLayout.VERTICAL, false);
		mRecyclerView.setLayoutManager(layoutManager);

		listAdapter =
				new MainListAdapter(getContext(), this, presenter.getEateryList().size(), presenter.getEateryList());
		mRecyclerView.setAdapter(listAdapter);
		mRecyclerView.setVisibility(View.VISIBLE);
//		Log.d("TAG-mainlist", presenter.getEateryList().toString());
		return rootView;
	}

	@Override
	public void changeButtonColor(int textColor, int backgroundColor, Button button){}

	@Override
	public void filterClick(View view){}

	@Override
	public void onClick(int position, ArrayList<EateryBaseModel> list) {
		Toast.makeText(getContext(), "CLICK", Toast.LENGTH_SHORT).show();
//		Intent intent = new Intent(this, MenuActivity.class);
//		intent.putExtra("testData", list);
//		intent.putExtra("cafeInfo", list.get(position));
//		intent.putExtra("locName", list.get(position).getNickName());
//		startActivity(intent);
	}
}
