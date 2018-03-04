package com.example.jc.eatery_android.Model;

import java.io.Serializable;
import java.util.ArrayList;


/**
 * Created by JC on 2/15/18.
 * This represents a single Cafeteria (either a cafe or a dining hall)
 */

public class CafeteriaModel implements Serializable{

    //latitude+longitude for map(later)
    int id;
    String name;
    String nickName;
    boolean is_diningHall;
    CafeteriaArea area;
    ArrayList<String> pay_methods;
    String buildingLocation;
    ArrayList<ArrayList<MealModel>> weeklyMenu = new ArrayList<ArrayList<MealModel>>();
    CafeModel cafeInfo = new CafeModel();
    //TODO: Add methods to get if open or closed
    //TODO: Add methods to get next open or next close

    public String stringTo() {
        String info = "Name/nickName: " + name + "/" + nickName;
        String locationString = "Location: " +  ", Area: " + area;
        String payMethodsString = "Pay Methods: " + pay_methods.toString();
        String menuString = "";
        if (is_diningHall) {
            for (ArrayList<MealModel> meal : weeklyMenu) {
                for (MealModel mealIndiv : meal) {
                    menuString = menuString + mealIndiv.stringTo();
                }

            }
        } else {
            //menuString = cafeMenu.toString();
        }
        return info + "\n" + locationString + "\n" + payMethodsString + "\n" + "Menu" + "\n" + menuString;

    }
/*
    public boolean isOpen(){
        if(is_diningHall){
            for(ArrayList day: weeklyMenu){

                if(day.size()>0)
            }
        }
        else{

        }

    }

    public string closeTime(){


    }*/


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
    public String getBuildingLocation() {
        return buildingLocation;
    }

    public void setBuildingLocation(String buildingLocation) {
        this.buildingLocation = buildingLocation;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public CafeModel getCafeInfo() {
        return cafeInfo;
    }

    public void setCafeInfo(CafeModel cafeInfo) {
        this.cafeInfo = cafeInfo;
    }

    /*
    public Location getLocation() {
        return location;
    }

    public void setLocation(Double lat, Double lng) {
        this.location.setLongitude(lng);
        this.location.setLatitude(lat);
    }*/


    public CafeteriaArea getArea() {
        return area;
    }

    public void setArea(CafeteriaArea area) {
        this.area = area;
    }

    public enum CafeteriaArea {
        NORTH,
        CENTRAL,
        WEST;
    }

}

