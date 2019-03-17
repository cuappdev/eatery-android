package com.cornellappdev.android.eatery.presenter;

import android.content.Context;
import android.util.Log;

import com.cornellappdev.android.eatery.Repository;
import com.cornellappdev.android.eatery.model.BrbInfoModel;
import com.cornellappdev.android.eatery.model.EateryBaseModel;
import com.cornellappdev.android.eatery.network.GetLoginUtilities;
import com.cornellappdev.android.eatery.util.AccountManagerUtil;

import java.util.ArrayList;

public class AccountPresenter {

    private Repository rInstance = Repository.getInstance();
    private boolean isLoggingIn;
    private String netID = "";
    private String password = "";
    private Context currentContext;

    public void setNetID(String netid) {
        this.netID = netid;
    }

    public void setContext(Context c) {
        this.currentContext = c;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isLoggedIn() {
        return rInstance.getBrbInfoModel()!=null;
    }

    public void setLoggingIn(boolean b) {
        this.isLoggingIn = b;
    }

    public boolean isLoggingIn() {
        return this.isLoggingIn;
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

    public void resetLoginJson() {
        GetLoginUtilities.resetLoginAbility(netID, password);
    }
    public String[] readSavedCredentials() {
        return AccountManagerUtil.readSavedCredentials(currentContext);
    }

    public void outputCredentialsToFile() {
        AccountManagerUtil.outputCredentialsToFile(this.netID, this.password,
               this.currentContext);
    }

    public void eraseSavedCredentials() {
        AccountManagerUtil.eraseSavedCredentials(this.currentContext);
    }
}


