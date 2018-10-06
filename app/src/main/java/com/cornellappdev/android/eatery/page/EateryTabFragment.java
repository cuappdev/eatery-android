package com.cornellappdev.android.eatery.page;

import androidx.appcompat.widget.SearchView.OnQueryTextListener;
import androidx.fragment.app.Fragment;
import com.cornellappdev.android.eatery.page.EateryDataLoaded;

public abstract class EateryTabFragment extends Fragment implements OnQueryTextListener,
    EateryDataLoaded {
}
