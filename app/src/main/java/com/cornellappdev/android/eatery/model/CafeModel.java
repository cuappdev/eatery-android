package com.cornellappdev.android.eatery.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class CafeModel extends EateryBaseModel implements Serializable {
  public Status getStatus(){
    return Status.CLOSED;
  }
  public String getStatusMessage(){
    return "opening at";
  }



}
