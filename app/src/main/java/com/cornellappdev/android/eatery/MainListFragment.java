package com.cornellappdev.android.eatery;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.cornellappdev.android.eatery.model.EateryBaseModel;
import com.cornellappdev.android.eatery.model.enums.CampusArea;
import com.cornellappdev.android.eatery.model.enums.PaymentMethod;
import com.cornellappdev.android.eatery.presenter.MainListPresenter;
import com.cornellappdev.android.eatery.presenter.MainPresenter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static com.cornellappdev.android.eatery.MainActivity.PAYMENT_CARD;
import static com.cornellappdev.android.eatery.MainActivity.PAYMENT_SWIPE;
import static com.cornellappdev.android.eatery.model.enums.CampusArea.CENTRAL;
import static com.cornellappdev.android.eatery.model.enums.CampusArea.NORTH;
import static com.cornellappdev.android.eatery.model.enums.CampusArea.WEST;

public class MainListFragment extends Fragment
		implements MainListAdapter.ListAdapterOnClickHandler, View.OnClickListener {
	private MainListPresenter presenter;
	private MainPresenter mPresenter;
	private RecyclerView mRecyclerView;
	private MainListAdapter listAdapter;
	private Button northButton;
	private Button westButton;
	private Button centralButton;
	private Button swipesButton;
	private Button brbButton;
	private Set<Button> areaButtonsPressed;
	private Set<Button> paymentButtonsPressed;

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

		getActivity().setTitle("Eateries");
		setHasOptionsMenu(true);

		mRecyclerView = rootView.findViewById(R.id.cafe_list);
		presenter = new MainListPresenter();
		mPresenter = new MainPresenter(rootView);
		areaButtonsPressed = new HashSet<>();
		paymentButtonsPressed = new HashSet<>();

		// Set up recyclerview and corresponding listadapter
		mRecyclerView.setHasFixedSize(true);
		LinearLayoutManager layoutManager =
				new LinearLayoutManager(getContext(), LinearLayout.VERTICAL, false);
		mRecyclerView.setLayoutManager(layoutManager);

		listAdapter =
				new MainListAdapter(getContext(), this, presenter.getEateryList().size(), presenter.getEateryList());
		mRecyclerView.setAdapter(listAdapter);
		mRecyclerView.setVisibility(View.VISIBLE);

		// Set onCLick listeners
		northButton = rootView.findViewById(R.id.northButton);
		westButton = rootView.findViewById(R.id.westButton);
		centralButton = rootView.findViewById(R.id.centralButton);
		swipesButton = rootView.findViewById(R.id.swipes);
		brbButton = rootView.findViewById(R.id.brb);
		northButton.setOnClickListener(this);
		westButton.setOnClickListener(this);
		centralButton.setOnClickListener(this);
		swipesButton.setOnClickListener(this);
		brbButton.setOnClickListener(this);

		return rootView;
	}

	public void changeButtonColor(int textColor, int backgroundColor, Button button) {
		button.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), textColor));
		GradientDrawable bgShape = (GradientDrawable) button.getBackground();
		bgShape.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), backgroundColor));
	}

	public void getCurrentAreas() {
		HashSet<CampusArea> areaSet = new HashSet<>();
		for (Button button : areaButtonsPressed) {
			if (northButton.equals(button)) {
				areaSet.add(NORTH);
			} else if (westButton.equals(button)) {
				areaSet.add(WEST);
			} else if (centralButton.equals(button)) {
				areaSet.add(CENTRAL);
			}
		}
		presenter.setAreaSet(areaSet);
	}

	public void getCurrentPaymentTypes() {
		HashSet<PaymentMethod> paymentSet = new HashSet<>();
		for (Button button : paymentButtonsPressed) {
			if (brbButton.equals(button)) {
				paymentSet.add(PaymentMethod.BRB);
			} else if (swipesButton.equals(button)) {
				paymentSet.add(PaymentMethod.SWIPES);
			}
		}
		presenter.setPaymentSet(paymentSet);
	}

	private void handleAreaButtonPress(Button button, CampusArea area) {
		if (areaButtonsPressed.contains(button)) {
			changeButtonColor(R.color.blue, R.color.wash, button);
			areaButtonsPressed.remove(button);
		} else {
			changeButtonColor(R.color.white, R.color.blue, button);
			areaButtonsPressed.add(button);
		}
		presenter.setAreaButtonPressed(areaButtonsPressed);
		getCurrentAreas();
		presenter.filterCurrentList();
	}

	private void handlePaymentButtonPress(Button button, String payment) {
		if (paymentButtonsPressed.contains(button)) {
			changeButtonColor(R.color.blue, R.color.wash, button);
			paymentButtonsPressed.remove(button);
		} else {
			changeButtonColor(R.color.white, R.color.blue, button);
			paymentButtonsPressed.add(button);
		}
		presenter.setPaymentButtonPressed(paymentButtonsPressed);
		getCurrentPaymentTypes();
		presenter.filterCurrentList();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.northButton:
				handleAreaButtonPress(northButton, NORTH);
				break;
			case R.id.centralButton:
				handleAreaButtonPress(centralButton, CENTRAL);
				break;
			case R.id.westButton:
				handleAreaButtonPress(westButton, WEST);
				break;
			case R.id.swipes:
				handlePaymentButtonPress(swipesButton, PAYMENT_SWIPE);
				break;
			case R.id.brb:
				handlePaymentButtonPress(brbButton, PAYMENT_CARD);
				break;
		}

		ArrayList<EateryBaseModel> cafesToDisplay = presenter.getCafesToDisplay();
		Collections.sort(cafesToDisplay);
		listAdapter.setList(cafesToDisplay, cafesToDisplay.size(), null);
//		searchList = cafesToDisplay;
//		if (searchPressed) {
//			queryListener.onQueryTextChange(queryListener.query);
//		} else {
//			Collections.sort(cafesToDisplay);
//			listAdapter.setList(cafesToDisplay, cafesToDisplay.size(), null);
//		}
	}

	@Override
	public void onClick(int position, ArrayList<EateryBaseModel> list) {
		Intent intent = new Intent(getActivity(), MenuActivity.class);
		intent.putExtra("cafeInfo", list.get(position));
		intent.putExtra("locName", list.get(position).getNickName());
		startActivity(intent);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_map:
				Intent intent = new Intent(getActivity().getApplicationContext(), MapsActivity.class);
				startActivity(intent);
				return true;
			default:
				// The user's action was not recognized, and invoke the superclass to handle it.
				return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		getActivity().getMenuInflater().inflate(R.menu.menu_main, menu);
		final MenuItem searchItem = menu.findItem(R.id.action_search);
		SearchView searchView = (SearchView) searchItem.getActionView();
		getActivity().setTitle("Eateries");
		AutoCompleteTextView searchTextView =
				searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text);
		searchView.setMaxWidth(2000);
		try {
			Field mCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
			mCursorDrawableRes.setAccessible(true);
			mCursorDrawableRes.set(
					searchTextView,
					R.drawable
							.cursor); // This sets the cursor resource ID to 0 or @null which will make it visible
			// on white background
		} catch (Exception e) {
			// Don't do anything
		}
		searchView.setOnQueryTextListener(presenter);
	}


}
