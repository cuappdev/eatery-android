package com.example.jc.eatery_android.Model;

import java.util.ArrayList;

/**
 * Created by JC on 2/15/18.
 */

public class CafeteriaModel {

    //latitude+longitude for map(later)

    double lattitude;
    double longitude;
    String name;
    String nickName;
    boolean is_diningHall;
    ArrayList<String> pay_methods;
    ArrayList<ArrayList<MealModel>> weeklyMenu = new ArrayList<ArrayList<MealModel>>();
    CafeteriaArea area;

    public double getLattitude() {
        return lattitude;
    }

    public void setLattitude(double lattitude) {
        this.lattitude = lattitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public boolean getIs_diningHall() {
        return is_diningHall;
    }

    public void setIs_diningHall(boolean is_diningHall) {
        this.is_diningHall = is_diningHall;
    }

    public ArrayList<String> getPay_methods() {
        return pay_methods;
    }

    public void setPay_methods(ArrayList<String> pay_methods) {
        this.pay_methods = pay_methods;
    }

    public ArrayList<ArrayList<MealModel>> getWeeklyMenu() {
        return weeklyMenu;
    }

    public void setWeeklyMenu(ArrayList<ArrayList<MealModel>> weeklyMenu) {
        this.weeklyMenu = weeklyMenu;
    }

    public CafeteriaModel(){

    }

    public CafeteriaModel(double lattitude, double longitude, String name, String nickName,
                          boolean is_diningHall,ArrayList<String> pay_methods,ArrayList<ArrayList<MealModel>> weeklyMenu  ){
        this.lattitude = lattitude;
        this.longitude = longitude;
        this.name = name;
        this. nickName = nickName;
        this.is_diningHall = is_diningHall;
        this.pay_methods = pay_methods;
        this.weeklyMenu = weeklyMenu;
        this.area = area;
    }

}

