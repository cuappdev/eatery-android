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
    private Context mCurrentContext;

    public void setNetID(String netid) {
        this.mNetID = netid;
    }

    public void setContext(Context c) {
        this.mCurrentContext = c;
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

    public boolean getSaveCredentials() {
        return rInstance.getSaveCredentials();
    }

    public void setSaveCredentials(boolean b) {
        rInstance.setSaveCredentials(b);
    }

    public void setBrbModel(BrbInfoModel model) {
        rInstance.setBrbInfoModel(model);
    }

    public void resetLoginJS() {
        GetLoginUtilities.resetLoginAbility(mNetID, mPassword);
    }

    public String[] readSavedCredentials() {
        return AccountManagerUtil.readSavedCredentials(mCurrentContext);
    }

    public void outputCredentialsToFile() {
        AccountManagerUtil.outputCredentialsToFile(this.mNetID, this.mPassword,
                this.mCurrentContext);
    }

    public void eraseSavedCredentials() {
        AccountManagerUtil.eraseSavedCredentials(this.mCurrentContext);
    }
}


