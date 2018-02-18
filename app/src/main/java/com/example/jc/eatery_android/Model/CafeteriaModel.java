package com.example.jc.eatery_android.Model;

import android.location.Location;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by JC on 2/15/18.
 */

public class CafeteriaModel {

    //latitude+longitude for map(later)

    String lattitude;
    String longitude;
    String name;
    String nickName;
    boolean is_diningHall;
    ArrayList<String> pay_methods;
    ArrayList<ArrayList<MealModel>> weeklyMenu = new ArrayList<ArrayList<MealModel>>();

    public CafeteriaModel(String lattitude, String longitude, String name, String nickName,
                          boolean is_diningHall,ArrayList<String> pay_methods,ArrayList<ArrayList<MealModel>> weeklyMenu  ){
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.name = name;
        this. nickName = nickName;
        this.is_diningHall = is_diningHall;
        this.pay_methods = pay_methods;
        this.weeklyMenu = weeklyMenu;
    }






}
