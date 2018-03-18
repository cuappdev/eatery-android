package com.example.jc.eatery_android.NetworkUtils;

import android.content.Context;

import com.example.jc.eatery_android.Model.CafeModel;
import com.example.jc.eatery_android.Model.CafeteriaModel;
import com.example.jc.eatery_android.Model.MealModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by JC on 3/2/18.
 */

public final class JsonUtilities {


    public static String loadJSONFromAsset(Context context, String fileName) {
        String json = null;
        try {
            InputStream is = context.getAssets().open(fileName);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    public static ArrayList<CafeteriaModel> parseJson(String json, Context mainContext){
        try {
            SimpleDateFormat mealTime = new SimpleDateFormat("yyyy-MM-dd hh:mmaa");
            SimpleDateFormat dateF = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat mealTimeH = new SimpleDateFormat("hh:mmaa");

            ArrayList<CafeteriaModel> list = new ArrayList<>();

            //parsing hardcoded json
            JSONObject hardCodedData = new JSONObject(loadJSONFromAsset(mainContext, "externalEateries.json"));
            JSONArray eateriesH = hardCodedData.getJSONArray("eateries");
            for(int i=0; i<eateriesH.length(); i++){
                ArrayList<String> cafeItemsH = new ArrayList<>();
                CafeteriaModel cafeteriaModel = new CafeteriaModel();
                cafeteriaModel.setHardCoded(true);
                JSONObject basicInfo = eateriesH.getJSONObject(i);
                cafeteriaModel.setName(basicInfo.getString("name"));
                cafeteriaModel.setNickName(basicInfo.getString("nameshort"));
                cafeteriaModel.setIs_diningHall(false);
                cafeteriaModel.setBuildingLocation(basicInfo.getString("location"));

                String area = basicInfo.getJSONObject("campusArea").getString("descrshort");
                if (area.equalsIgnoreCase("north")) {
                    cafeteriaModel.setArea(CafeteriaModel.CafeteriaArea.NORTH);
                } else if (area.equalsIgnoreCase("west")) {
                    cafeteriaModel.setArea(CafeteriaModel.CafeteriaArea.WEST);
                } else {
                    cafeteriaModel.setArea(CafeteriaModel.CafeteriaArea.CENTRAL);
                }

                JSONArray methods = basicInfo.getJSONArray("payMethods");
                ArrayList<String> payMethods = new ArrayList<String>();
                for (int j = 0; j < methods.length(); j++) {
                    JSONObject method = methods.getJSONObject(j);
                    payMethods.add(method.getString("descrshort"));
                }
                cafeteriaModel.setPay_methods(payMethods);

                CafeModel cafe = new CafeModel();
                JSONArray diningItems = basicInfo.getJSONArray("diningItems");
                for (int z = 0; z < diningItems.length(); z++) {
                    JSONObject item = diningItems.getJSONObject(z);
                    cafeItemsH.add(item.getString("item"));

                }
                cafe.setCafeMenu(cafeItemsH);
                HashMap<String, ArrayList<Date>> cafeHoursH = new HashMap<String, ArrayList<Date>>();

                JSONArray operatingHours = basicInfo.getJSONArray("operatingHours"); //a single operating hour is a single day
                for (int c = 0; c < operatingHours.length(); c++) {
                    String days = operatingHours.getJSONObject(c).getString("weekday");
                    JSONArray events = operatingHours.getJSONObject(c).getJSONArray("events");
                    ArrayList<Date> hours = new ArrayList<Date>();
                    String start = events.getJSONObject(0).getString("start");
                    String end = events.getJSONObject(0).getString("end");
                    if(start.length()==6){
                        start = "0"+start;
                    }
                    if(end.length()==6){
                        end = "0"+end;
                    }
                    hours.add(mealTimeH.parse(start));
                    hours.add(mealTimeH.parse(end));

                    //Log.i("test", dateFinal.toString());
                    if(days.equalsIgnoreCase("monday-thursday")){
                        cafeHoursH.put("Monday",hours);
                        cafeHoursH.put("Tuesday",hours);
                        cafeHoursH.put("Wednesday",hours);
                        cafeHoursH.put("Thursday",hours);

                    }
                    else if(days.equalsIgnoreCase("monday-friday")){
                        cafeHoursH.put("Monday",hours);
                        cafeHoursH.put("Tuesday",hours);
                        cafeHoursH.put("Wednesday",hours);
                        cafeHoursH.put("Thursday",hours);
                        cafeHoursH.put("Friday",hours);
                    }
                    else{
                        cafeHoursH.put(days, hours);
                    }
                }
                cafe.setHoursH(cafeHoursH);
                cafeteriaModel.setCafeInfo(cafe);
                list.add(cafeteriaModel);
            }

            //Begin parsing of non hard coded json
            JSONObject parentObject = new JSONObject(json);
            JSONObject data = parentObject.getJSONObject("data");
            JSONArray eateries = data.getJSONArray("eateries");
            HashSet<Integer> diningHall = new HashSet<>();
            //Id's of the dining halls
            diningHall.add(31);
            diningHall.add(25);
            diningHall.add(26);
            diningHall.add(27);
            diningHall.add(29);
            diningHall.add(3);
            diningHall.add(20);
            diningHall.add(4);
            diningHall.add(5);
            diningHall.add(30);




            //got rid of location
            for (int i = 0; i < eateries.length(); i++) {
                CafeteriaModel cafeteriaModel = new CafeteriaModel();
                cafeteriaModel.setHardCoded(false);
                JSONObject child = eateries.getJSONObject(i);
                cafeteriaModel.setId(child.getInt("id"));
                cafeteriaModel.setName(child.getString("name"));
                cafeteriaModel.setBuildingLocation(child.getString("location"));
                cafeteriaModel.setNickName(child.getString("nameshort"));
                JSONArray methods = child.getJSONArray("payMethods");
                ArrayList<String> payMethods = new ArrayList<String>();
                ArrayList<ArrayList<MealModel>> weeklyMenu = new ArrayList<>();
                ArrayList<String> cafeItems = new ArrayList<>();
                String area = child.getJSONObject("campusArea").getString("descrshort");
                if (area.equalsIgnoreCase("north")) {
                    cafeteriaModel.setArea(CafeteriaModel.CafeteriaArea.NORTH);
                } else if (area.equalsIgnoreCase("west")) {
                    cafeteriaModel.setArea(CafeteriaModel.CafeteriaArea.WEST);
                } else {
                    cafeteriaModel.setArea(CafeteriaModel.CafeteriaArea.CENTRAL);
                }
                for (int j = 0; j < methods.length(); j++) {
                    JSONObject method = methods.getJSONObject(j);
                    payMethods.add(method.getString("descrshort"));
                }
                cafeteriaModel.setPay_methods(payMethods);
                if (diningHall.contains(child.getInt("id"))) {
                    cafeteriaModel.setIs_diningHall(true);
                }
                if (cafeteriaModel.getIs_diningHall()) {
                    JSONArray days = child.getJSONArray("operatingHours");
                    //operatingHours = one day
                    for (int k = 0; k < days.length(); k++) {
                        ArrayList<MealModel> mealModelArray = new ArrayList<>();
                        JSONObject mealPeriods = days.getJSONObject(k);
                        String date = mealPeriods.getString("date");
                        JSONArray events = mealPeriods.getJSONArray("events");
                        //loops through each meal in one dining hall
                        for (int l = 0; l < events.length(); l++) {
                            MealModel mealModel = new MealModel();
                            JSONObject meal = events.getJSONObject(l); //created a single meal object(ie lunch)
                            String startTime = meal.getString("start");
                            String endTime = meal.getString("end");
                            if(startTime.length()==6){
                                startTime = "0"+startTime;
                            }
                            if(endTime.length()==6){
                                endTime = "0"+endTime;
                            }
                            String start = date + " " + startTime;
                            String end = date + " " + endTime;
                            mealModel.setStart(mealTime.parse(start));
                            mealModel.setEnd(mealTime.parse(end));
                            mealModel.setType(meal.getString("descr"));
                            //Log.i("test", mealModel.getStart().toString());
                            //Log.i("test", mealModel.getEnd().toString());
                            //mealMenu = hashmap of items in single meal
                            HashMap<String, ArrayList<String>> mealMenu = new HashMap<>();
                            JSONArray menu = meal.getJSONArray("menu");
                            for (int m = 0; m < menu.length(); m++) {
                                JSONObject stations = menu.getJSONObject(m);
                                String category = stations.getString("category");
                                ArrayList<String> itemsArray = new ArrayList<>();
                                JSONArray items = stations.getJSONArray("items");
                                for (int n = 0; n < items.length(); n++) {
                                    itemsArray.add(items.getJSONObject(n).getString("item"));
                                }
                                mealMenu.put(category, itemsArray);
                            }
                            mealModel.setMenu(mealMenu);
                            mealModelArray.add(mealModel);
                        }
                        weeklyMenu.add(mealModelArray);
                    }
                    cafeteriaModel.setWeeklyMenu(weeklyMenu);
                } else {
                    CafeModel cafe = new CafeModel();

                    JSONArray diningItems = child.getJSONArray("diningItems");
                    for (int z = 0; z < diningItems.length(); z++) {
                        JSONObject item = diningItems.getJSONObject(z);
                        cafeItems.add(item.getString("item"));

                    }
                    cafe.setCafeMenu(cafeItems);
                    HashMap<Date, ArrayList<Date>> cafeHours = new HashMap<Date, ArrayList<Date>>();

                    JSONArray operatingHours = child.getJSONArray("operatingHours"); //a single operating hour is a single day
                    for (int c = 0; c < operatingHours.length(); c++) {
                        String date = operatingHours.getJSONObject(c).getString("date");
                        Date dateFinal = dateF.parse(date);
                        //Log.i("test", dateFinal.toString());
                        ArrayList<Date> hours = new ArrayList<Date>();

                        JSONArray events = operatingHours.getJSONObject(c).getJSONArray("events");
                        if (events.length() != 0) {
                            String startTime = events.getJSONObject(0).getString("start");
                            String endTime = events.getJSONObject(0).getString("end");
                            if(startTime.length()==6){
                                startTime = "0"+startTime;
                            }
                            if(endTime.length()==6){
                                endTime = "0"+endTime;
                            }
                            String start = date + " " + startTime;
                            String end = date + " " + endTime;
                            hours.add(mealTime.parse(start));
                            hours.add(mealTime.parse(end));

                        }
                        cafeHours.put(dateFinal, hours);

                    }
                    cafe.setHours(cafeHours);
                    cafeteriaModel.setCafeInfo(cafe);
                }
                list.add(cafeteriaModel);
            }
            return list;
        }catch(JSONException e){
            e.printStackTrace();
            return null;
        }
        catch(ParseException e){
            e.printStackTrace();
            return null;
        }

    }
}
