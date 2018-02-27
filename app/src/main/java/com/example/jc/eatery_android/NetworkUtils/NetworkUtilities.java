package com.example.jc.eatery_android.NetworkUtils;

import android.util.Log;

import com.example.jc.eatery_android.Model.CafeteriaModel;
import com.example.jc.eatery_android.Model.MealModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

/**
 * Created by JC on 2/15/18.
 */

public final class NetworkUtilities {
    private final static String URI = "https://now.dining.cornell.edu/api/1.0/dining/eateries.json";

    public static ArrayList<CafeteriaModel> getJson(){

        try {
            URL url = new URL(URI);
            BufferedReader reader = null;
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream stream = connection.getInputStream();
            InputStreamReader isw = new InputStreamReader(stream);
            reader = new BufferedReader(isw);

            StringBuffer buffer = new StringBuffer();

            String line = "";
            while ((line = reader.readLine())!=null){
                buffer.append(line);
            }

            String json = buffer.toString();
            JSONObject parentObject = new JSONObject(json);
            JSONObject data = parentObject.getJSONObject("data");
            JSONArray eateries = data.getJSONArray("eateries");
            ArrayList<CafeteriaModel> list = new ArrayList<>();
            HashSet<Integer> diningHall = new HashSet<>();
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
            for(int i=0; i<eateries.length();i++){
                CafeteriaModel cafeteriaModel = new CafeteriaModel();
                JSONObject child = eateries.getJSONObject(i);
                cafeteriaModel.setId(child.getInt("id"));
                cafeteriaModel.setName(child.getString("name"));
                cafeteriaModel.setNickName(child.getString("nameshort"));
                JSONArray methods = child.getJSONArray("payMethods");
                ArrayList<String> payMethods = new ArrayList<String>();
                ArrayList<ArrayList<MealModel>> weeklyMenu = new ArrayList<>();
                ArrayList<String> cafeItems = new ArrayList<>();
                cafeteriaModel.setPay_methods(payMethods);
                String area = child.getJSONObject("campusArea").getString("descrshort");
                if(area.equalsIgnoreCase("north")){
                    cafeteriaModel.setArea(CafeteriaModel.CafeteriaArea.NORTH);

                }
                else if(area.equalsIgnoreCase("west")){
                    cafeteriaModel.setArea(CafeteriaModel.CafeteriaArea.WEST);

                }
                else{
                    cafeteriaModel.setArea(CafeteriaModel.CafeteriaArea.CENTRAL);

                }
                for(int j=0; j< methods.length();j++){
                    JSONObject method = methods.getJSONObject(j);
                    cafeteriaModel.getPay_methods().add(method.getString("descrshort"));
                }
                if(diningHall.contains(child.getInt("id"))){
                    cafeteriaModel.setIs_diningHall(true);

                }
                if(cafeteriaModel.getIs_diningHall()){
                    JSONArray days = child.getJSONArray("operatingHours");
                    //operatingHours = one day
                    for(int k = 0; k< days.length(); k++){
                        ArrayList<MealModel> mealModelArray = new ArrayList<>();
                        JSONObject mealPeriods = days.getJSONObject(k);
                        String date = mealPeriods.getString("date");
                        JSONArray events = mealPeriods.getJSONArray("events");

                        //loops through each meal in one dining hall
                        for(int l =0; l<events.length(); l++ ){
                            MealModel mealModel = new MealModel();
                            JSONObject meal = events.getJSONObject(l); //created a single meal object(ie lunch)
                            mealModel.setDate(date);
                            mealModel.setStart(meal.getString("start"));
                            mealModel.setEnd(meal.getString("end"));
                            mealModel.setType(meal.getString("descr"));

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
                }
                else{
                    JSONArray diningItems = child.getJSONArray("diningItems");
                    for(int z = 0; z<diningItems.length(); z++ ){
                        JSONObject item = diningItems.getJSONObject(z);
                        cafeItems.add(item.getString("item"));
                        //Trillium does not have dining items so we will have to hard code it in later

                    }
                    cafeteriaModel.setCafeMenu(cafeItems);
                }
                list.add(cafeteriaModel);
            }



            return list;



        }catch(IOException e){
            e.printStackTrace();
            Log.i("model","IO error");
            return null;
        }
        catch(JSONException e){
            Log.i("model","JSON error");

            return null;

        }


    }
}
