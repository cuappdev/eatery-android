package com.example.jc.eatery_android.Model;

import android.util.Log;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;


/**
 * Created by JC on 2/15/18.
 * This represents a single Cafeteria (either a cafe or a dining hall)
 */

public class CafeteriaModel implements Serializable{

    //latitude+longitude for map(later)
    boolean isHardCoded;
    int id;
    String name;
    String nickName;
    boolean is_diningHall;
    CafeteriaArea area;
    ArrayList<String> pay_methods;
    String buildingLocation;
    ArrayList<ArrayList<MealModel>> weeklyMenu = new ArrayList<ArrayList<MealModel>>();
    CafeModel cafeInfo = new CafeModel();

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    String closeTime; //This field is only updated if the location is open and the method isOpen is called
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

    public int indexOfCurrentDay(){
        Date now = new Date();
        if(is_diningHall){
            for(int i=0; i< weeklyMenu.size(); i++){
                ArrayList<MealModel> day = weeklyMenu.get(i);
                if(day.size()>0){
                    MealModel firstMeal = day.get(0);
                    if(firstMeal.getStart().getDate()==now.getDate()){
                        return i;
                        }
                    }
                }
            }
        return 0;
    }

    public HashSet<String> getMealItems(){
        HashSet<String> items = new HashSet<String>();
        Date now = new Date();
        if(isOpen().equalsIgnoreCase("closed")){
            return items;
        }
        else if(isHardCoded || !is_diningHall){
            items.addAll(cafeInfo.getCafeMenu());

        }
        else{
            for(ArrayList<MealModel> day: weeklyMenu){
                if(day.size()>0){
                    MealModel firstMeal = day.get(0);
                    if(firstMeal.getStart().getDate()==now.getDate()){
                        for(MealModel meal: day){
                            if(meal.getStart().before(now)&& meal.getEnd().after(now)){
                                HashMap<String, ArrayList<String>> menu =meal.getMenu();
                                for(ArrayList<String> vals: menu.values()) {
                                    items.addAll(vals);
                                }
                            }
                        }
                    }
                }
            }
        }
        return items;
    }

    public String isOpen(){
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mmaa");
        SimpleDateFormat timeFormatDay = new SimpleDateFormat("MM/dd");
        boolean foundDay = false;
        Date now = new Date();
        if(isHardCoded){
            int day = now.getDay();
            HashMap<Integer, ArrayList<Date>> hours = cafeInfo.getHoursH();
            if(hours.containsKey(day)){
                foundDay = true;
                int startT = hours.get(day).get(0).getHours() + hours.get(day).get(0).getMinutes();
                int endT = hours.get(day).get(1).getHours() + hours.get(day).get(1).getMinutes();
                int curT = now.getHours() + now.getMinutes();
                if(curT>=startT && curT<endT){
                    closeTime = "Closing at "+ timeFormat.format(hours.get(day).get(1));
                    return "Open";
                }
                else if(curT< startT){
                    closeTime = "Opening at " + timeFormat.format(hours.get(day).get(0));
                    return "Closed";
                }

            }
            for(int i=1; i<=6; i++){
                int tempDay = day + i % 7;
                if(hours.containsKey(tempDay)){
                    Date openDate = new Date(now.getTime() +(86400000*i));
                    closeTime = "Opening " + timeFormatDay.format(openDate) + " at " + timeFormat.format(hours.get(tempDay).get(0));
                    return "Closed";
                }
            }
            closeTime = " ";
            return "Closed";

        }
        else if(is_diningHall){
            for(ArrayList<MealModel> day: weeklyMenu){
                if(day.size()>0){
                    MealModel firstMeal = day.get(0);
                    if(firstMeal.getStart().getDate()==now.getDate()){
                        foundDay = true;
                        for(MealModel meal: day){
                            if(meal.getStart().before(now)&& meal.getEnd().after(now)){
                                closeTime = "Closing at "+ timeFormat.format(meal.getEnd());
                                return "Open";
                            }
                            else if(meal.getStart().after(now)){
                                closeTime = "Opening at " + timeFormat.format(meal.getStart());
                                return "Closed";
                            }

                        }
                    }
                    if(foundDay){
                        Log.i("test", timeFormatDay.format(firstMeal.getStart()));
                        closeTime = "Opening " + timeFormatDay.format(firstMeal.getStart()) + " at " + timeFormat.format(firstMeal.getStart());
                        return "Closed";
                    }
                }
            }
            closeTime = " ";
            return "Closed";
        }
        else{
            HashMap<Date, ArrayList<Date>> hours = cafeInfo.getHours();
            Object[] objectArray = hours.keySet().toArray();
            Date[] hrs = Arrays.copyOf(objectArray, objectArray.length, Date[].class);
            //Date[] hrs = (Date[])hours.keySet().toArray();
            Arrays.sort(hrs);
            for(Date day: hrs){
                if(day.getDate() == now.getDate()){
                    foundDay = true;
                    ArrayList<Date> hour = hours.get(day);
                    while(hour.size()>1){
                        if(hour.get(0).before(now) && hour.get(1).after(now)){
                            closeTime = "Closing at "+ timeFormat.format(hour.get(1));
                            return "Open";
                        }
                        else if(hour.get(0).after(now)){
                            closeTime = "Opening at " + timeFormat.format(hour.get(0));
                            return "Closed";
                        }
                        hour.remove(0);
                        hour.remove(0);
                    }
                }
                if(foundDay){
                    if(hours.get(day).size()>1){
                        ArrayList<Date> hour = hours.get(day);
                        closeTime = "Opening " + timeFormatDay.format(hour.get(0)) + " at " + timeFormat.format(hour.get(0));
                        return "Closed";
                    }
                }
            }
            closeTime = " ";
            return "Closed";

        }

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

    public boolean isHardCoded() {
        return isHardCoded;
    }

    public void setHardCoded(boolean hardCoded) {
        isHardCoded = hardCoded;
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
    /*
    public enum CafeteriaStatus{
        OPEN,
        CLOSED,
        CLOSINGSOON
    }*/

}

