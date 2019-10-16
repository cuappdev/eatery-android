package com.cornellappdev.android.eatery.presenter;

import android.content.Context;

import com.cornellappdev.android.eatery.Repository;
import com.cornellappdev.android.eatery.model.BrbInfoModel;
import com.cornellappdev.android.eatery.network.GetLoginUtilities;
import com.cornellappdev.android.eatery.util.AccountManagerUtil;

public class AccountPresenter {

    private Repository rInstance = Repository.getInstance();
    private boolean isLoggingIn;
    private String mNetID = "";
    private String mPassword = "";

    public void setNetID(String netid) {
        this.mNetID = netid;
    }

    public void setPassword(String password) {
        this.mPassword = password;
    }

    public boolean isLoggedIn() {
        return rInstance.getBrbInfoModel() != null;
    }

    public boolean isLoggingIn() {
        return this.isLoggingIn;
    }

    public void setLoggingIn(boolean b) {
        this.isLoggingIn = b;
    }

    public void setBrbModel(BrbInfoModel model) {
        rInstance.setBrbInfoModel(model);
    }

    public void resetLoginJS() {
        GetLoginUtilities.resetLoginAbility(mNetID, mPassword);
    }

    public String[] readSavedCredentials(Context c) {
        return AccountManagerUtil.readSavedCredentials(c);
    }

    public void outputCredentialsToFile(Context c) {
        AccountManagerUtil.outputCredentialsToFile(this.mNetID, this.mPassword, c);
    }

    public void eraseSavedCredentials(Context c) {
        AccountManagerUtil.eraseSavedCredentials(c);
    }
}


