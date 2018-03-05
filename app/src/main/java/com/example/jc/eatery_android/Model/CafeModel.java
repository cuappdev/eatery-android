package com.example.jc.eatery_android.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by abdullahislam on 3/2/18.
 */

public class CafeModel implements Serializable{

    ArrayList<String> cafeMenu = new ArrayList();
    HashMap<Date, ArrayList<Date>> hours;
    public CafeModel(){
    }

    public ArrayList<String> getCafeMenu() {
        return cafeMenu;
    }

    public void setCafeMenu(ArrayList<String> cafeMenu) {
        this.cafeMenu = cafeMenu;
    }

    public HashMap<Date, ArrayList<Date>> getHours() {
        return hours;
    }

    public void setHours(HashMap<Date, ArrayList<Date>> hours) {
        this.hours = hours;
    }

}
