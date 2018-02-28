package com.example.jc.eatery_android.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by abdullahislam on 2/18/18.
 */

public class MealModel implements Serializable{


    private String date;
    private String start;
    private String end;
    private String type;
    private HashMap<String, ArrayList<String>> menu;

    public MealModel(){

    }

    public String stringTo() {
        String info = type + " on " + date.toString() + " from: " + start + " to: " + end + "\n";
        for (HashMap.Entry<String, ArrayList<String>> entry : menu.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            info = info + " " + key + ": " + value.toString() + "\n";
        }
        return info;
    }






    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public HashMap<String, ArrayList<String>> getMenu() {
        return menu;
    }

    public void setMenu(HashMap<String, ArrayList<String>> menu) {
        this.menu = menu;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

}
