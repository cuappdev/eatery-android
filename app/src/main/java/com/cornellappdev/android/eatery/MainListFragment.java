package com.cornellappdev.android.eatery;

import android.content.Intent;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
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
	private MainListPresenter mListPresenter;
	private MainPresenter mPresenter;
	private RecyclerView mRecyclerView;
	private MainListAdapter mListAdapter;
	private Button mNorthButton;
	private Button mWestButton;
	private Button mCentralButton;
	private Button mSwipesButton;
	private Button mBrbButton;
	private Set<Button> mAreaButtonsPressed;
	private Set<Button> mPaymentButtonsPressed;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {

		// Inflate the layout for this fragment
		View rootView = inflater.inflate(R.layout.fragment_main_list, container, false);

		getActivity().setTitle("Eateries");
		setHasOptionsMenu(true);

		mRecyclerView = rootView.findViewById(R.id.cafe_list);
		mListPresenter = new MainListPresenter();
		mPresenter = new MainPresenter();
		mAreaButtonsPressed = new HashSet<>();
		mPaymentButtonsPressed = new HashSet<>();

		// Set up recyclerview and corresponding listadapter
		mRecyclerView.setHasFixedSize(true);
		LinearLayoutManager layoutManager =
				new LinearLayoutManager(getContext(), LinearLayout.VERTICAL, false);
		mRecyclerView.setLayoutManager(layoutManager);

		mListAdapter =
				new MainListAdapter(getContext(), this, mListPresenter.getEateryList().size(), mListPresenter.getEateryList());
		mRecyclerView.setAdapter(mListAdapter);
		mRecyclerView.setVisibility(View.VISIBLE);

		// Set onCLick listeners
		mNorthButton = rootView.findViewById(R.id.northButton);
		mWestButton = rootView.findViewById(R.id.westButton);
		mCentralButton = rootView.findViewById(R.id.centralButton);
		mSwipesButton = rootView.findViewById(R.id.swipes);
		mBrbButton = rootView.findViewById(R.id.brb);
		mNorthButton.setOnClickListener(this);
		mWestButton.setOnClickListener(this);
		mCentralButton.setOnClickListener(this);
		mSwipesButton.setOnClickListener(this);
		mBrbButton.setOnClickListener(this);

		return rootView;
	}


	public void changeButtonColor(int textColor, int backgroundColor, Button button) {
		button.setTextColor(ContextCompat.getColor(getActivity().getApplicationContext(), textColor));
		GradientDrawable bgShape = (GradientDrawable) button.getBackground();
		bgShape.setColor(ContextCompat.getColor(getActivity().getApplicationContext(), backgroundColor));
	}

	public void getCurrentAreas() {
		HashSet<CampusArea> areaSet = new HashSet<>();
		for (Button button : mAreaButtonsPressed) {
			if (mNorthButton.equals(button)) {
				areaSet.add(NORTH);
			} else if (mWestButton.equals(button)) {
				areaSet.add(WEST);
			} else if (mCentralButton.equals(button)) {
				areaSet.add(CENTRAL);
			}
		}
		mListPresenter.setAreaSet(areaSet);
	}

	public void getCurrentPaymentTypes() {
		HashSet<PaymentMethod> paymentSet = new HashSet<>();
		for (Button button : mPaymentButtonsPressed) {
			if (mBrbButton.equals(button)) {
				paymentSet.add(PaymentMethod.BRB);
			} else if (mSwipesButton.equals(button)) {
				paymentSet.add(PaymentMethod.SWIPES);
			}
		}
		mListPresenter.setPaymentSet(paymentSet);
	}

	private void handleAreaButtonPress(Button button, CampusArea area) {
		if (mAreaButtonsPressed.contains(button)) {
			changeButtonColor(R.color.blue, R.color.wash, button);
			mAreaButtonsPressed.remove(button);
		} else {
			changeButtonColor(R.color.white, R.color.blue, button);
			mAreaButtonsPressed.add(button);
		}
		mListPresenter.setAreaButtonPressed(mAreaButtonsPressed);
		getCurrentAreas();
		mListPresenter.filterCurrentList();
	}

	private void handlePaymentButtonPress(Button button, String payment) {
		if (mPaymentButtonsPressed.contains(button)) {
			changeButtonColor(R.color.blue, R.color.wash, button);
			mPaymentButtonsPressed.remove(button);
		} else {
			changeButtonColor(R.color.white, R.color.blue, button);
			mPaymentButtonsPressed.add(button);
		}
		mListPresenter.setPaymentButtonPressed(mPaymentButtonsPressed);
		getCurrentPaymentTypes();
		mListPresenter.filterCurrentList();
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.northButton:
				handleAreaButtonPress(mNorthButton, NORTH);
				break;
			case R.id.centralButton:
				handleAreaButtonPress(mCentralButton, CENTRAL);
				break;
			case R.id.westButton:
				handleAreaButtonPress(mWestButton, WEST);
				break;
			case R.id.swipes:
				handlePaymentButtonPress(mSwipesButton, PAYMENT_SWIPE);
				break;
			case R.id.brb:
				handlePaymentButtonPress(mBrbButton, PAYMENT_CARD);
				break;
		}

		ArrayList<EateryBaseModel> cafesToDisplay = mListPresenter.getCafesToDisplay();
		Collections.sort(cafesToDisplay);
		mListAdapter.setList(cafesToDisplay, cafesToDisplay.size(), null);
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

		searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
			@Override
			public boolean onQueryTextSubmit(String s) {
                searchView.clearFocus();
				return true;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
                mPresenter.setQuery(newText);
                mPresenter.filterCurrentList();
                ArrayList<EateryBaseModel> cafesToDisplay = mPresenter.getCurrentList();
                mListAdapter.setList(cafesToDisplay, cafesToDisplay.size(), null);
                return true;
			}
		});
	}


}
