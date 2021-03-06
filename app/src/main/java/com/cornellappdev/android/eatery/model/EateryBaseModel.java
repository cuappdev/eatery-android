package com.cornellappdev.android.eatery.model;

import android.content.Context;

import com.cornellappdev.android.eatery.AllEateriesQuery;
import com.cornellappdev.android.eatery.model.enums.CampusArea;
import com.cornellappdev.android.eatery.model.enums.PaymentMethod;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

public abstract class EateryBaseModel implements Serializable, Comparable<EateryBaseModel> {

    boolean mOpenPastMidnight = false;
    int mId;
    private String mBuildingLocation, mName, mNickName, mPhoneNumber;
    private String mImageUrl;
    private String mReserveUrl;
    private ArrayList<String> mSearchedItems;
    private List<String> mExceptions;
    private @NotNull List<AllEateriesQuery.ExpandedMenu> mExpandedMenu;
    private boolean matchesFilter = true;
    private boolean mMatchesSearch = true;
    private boolean mIsGet = false;
    private CampusArea mArea;
    private Double mLatitude, mLongitude;
    private List<PaymentMethod> mPayMethods;

    public String getImageURL() {
        return mImageUrl;
    }

    // Implemented Getters
    public String getName() {
        return mName;
    }

    public String getNickName() {
        return mNickName;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public CampusArea getArea() {
        return mArea;
    }

    public double getLatitude() {
        return mLatitude;
    }

    public double getLongitude() {
        return mLongitude;
    }

    public String getBuildingLocation() {
        return mBuildingLocation;
    }

    public ArrayList<String> getSearchedItems() {
        return mSearchedItems;
    }

    public List<String> getExceptions() {
        return mExceptions;
    }

    public String getReserveUrl() {return mReserveUrl;}

    public boolean getIsGet() {return mIsGet;}

    public List<AllEateriesQuery.ExpandedMenu> getExpandedMenu() {return mExpandedMenu;}

    public void setSearchedItems(ArrayList<String> searchedItems) {
        this.mSearchedItems = searchedItems;
    }

    public void setMatchesSearch(boolean b) {
        mMatchesSearch = b;
    }

    public void setMatchesFilter(boolean b) {
        matchesFilter = b;
    }

    public boolean matchesSearch() {
        return mMatchesSearch;
    }

    public boolean matchesFilter() {
        return matchesFilter;
    }

    // Abstract Getters
    public abstract Status getCurrentStatus();

    public abstract HashSet<String> getMealItems();

    /**
     * This method returns a LocalDateTime. If the eatery has a current status of open or closing
     * soon
     * then the time returned represents the closing time. If the eatery is closed the time returned
     * represents the opening time. This method can return null in the case when there is no known
     * next time for opening.
     */
    public abstract LocalDateTime getChangeTime();

    public boolean isOpen() {
        Status status = getCurrentStatus();
        return status == Status.OPEN || status == Status.CLOSINGSOON;
    }

    public boolean hasPaymentMethod(PaymentMethod method) {
        return mPayMethods.contains(method);
    }

    public void parseEatery(Context context, boolean hardcoded, AllEateriesQuery.Eatery eatery) {
        mName = eatery.name();
        mBuildingLocation = eatery.location();
        mNickName = eatery.nameShort();
        mLatitude = eatery.coordinates().latitude();
        mLongitude = eatery.coordinates().longitude();
        mPhoneNumber = eatery.phone();
        mImageUrl = eatery.imageUrl();
        mReserveUrl = eatery.reserveUrl();
        mIsGet = eatery.isGet();
        mExceptions = eatery.exceptions();

        List<PaymentMethod> paymentMethods = new ArrayList<>();
        if (eatery.paymentMethods().brbs()) {
            paymentMethods.add(PaymentMethod.fromShortDescription("debit"));
        }
        if (eatery.paymentMethods().cash()) {
            paymentMethods.add(PaymentMethod.fromShortDescription("cash"));
        }
        if (eatery.paymentMethods().cornellCard()) {
            paymentMethods.add(PaymentMethod.fromShortDescription("cornellcard"));
        }
        if (eatery.paymentMethods().credit()) {
            paymentMethods.add(PaymentMethod.fromShortDescription("credit"));
        }
        if (eatery.paymentMethods().mobile()) {
            paymentMethods.add(PaymentMethod.fromShortDescription("mobile"));
        }
        if (eatery.paymentMethods().swipes()) {
            paymentMethods.add(PaymentMethod.fromShortDescription("swipes"));
        }
        mPayMethods = paymentMethods;

        mArea = CampusArea.fromShortDescription(eatery.campusArea().descriptionShort());
    }

    /**
     * Compare the time of two EateryModel
     **/
    public int compareTo(@NonNull EateryBaseModel cm) {
        if (isOpen() && cm.isOpen()) {
            return this.getNickName().compareTo(cm.getNickName());
        } else if (isOpen() && !cm.isOpen()) {
            return -1;
        } else if (!isOpen() && cm.isOpen()) {
            return 1;
        }
        return 0;
    }

    public enum Status {
        OPEN("Open"),
        CLOSINGSOON("Closing"),
        CLOSED("Closed");
        private String displayName;

        Status(String displayName) {
            this.displayName = displayName;
        }

        @Override @NonNull
        public String toString() {
            return displayName;
        }
    }
}
