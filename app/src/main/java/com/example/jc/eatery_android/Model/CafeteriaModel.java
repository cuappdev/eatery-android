package com.example.jc.eatery_android.Model;

import android.location.Location;

import java.util.ArrayList;

/**
 * Created by JC on 2/15/18.
 */

public class CafeteriaModel {

    //latitude+longitude for map(later)



    int id;
    String name;
    String nickName;
    boolean is_diningHall;
    CafeteriaArea area;
    ArrayList<String> pay_methods;
    Location location = new Location("");
    ArrayList<String> cafeMenu= new ArrayList();
    ArrayList<ArrayList<MealModel>> weeklyMenu = new ArrayList<>();




    public CafeteriaModel(){

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Double lat, Double lng) {
        this.location.setLongitude(lng);
        this.location.setLatitude(lat);
    }

    public String stringTo(){
        String info = "Name/nickName: " + name + "/" + nickName;
        String locationString = "Location: " + location.toString() + ", Area: " + area ;
        String payMethodsString = "Pay Methods: " + pay_methods.toString();
        String menuString = "";
        if(is_diningHall){
            for(ArrayList<MealModel> meal:weeklyMenu ){
                for(MealModel mealIndiv : meal){
                    menuString = menuString + mealIndiv.stringTo();
                }

            }
        }
        else{
            menuString = cafeMenu.toString();
        }
        return info + "\n" + locationString + "\n" + payMethodsString + "\n" +"Menu" + "\n" + menuString;

    }

    public ArrayList<String> getCafeMenu() {
        return cafeMenu;
    }

    public void setCafeMenu(ArrayList<String> cafeMenu) {
        this.cafeMenu = cafeMenu;
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

