package com.example.jc.eatery_android.Model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by abdullahislam on 3/2/18.
 */

public class CafeModel {

    ArrayList<String> cafeMenu = new ArrayList();
    HashMap<String, ArrayList<String>> hours;
    public CafeModel(){
    }

    public ArrayList<String> getCafeMenu() {
        return cafeMenu;
    }

    public void setCafeMenu(ArrayList<String> cafeMenu) {
        this.cafeMenu = cafeMenu;
    }

    public HashMap<String, ArrayList<String>> getHours() {
        return hours;
    }

    public void setHours(HashMap<String, ArrayList<String>> hours) {
        this.hours = hours;
    }

}
