package com.example.jc.eatery_android.NetworkUtils;

import com.example.jc.eatery_android.Model.CafeteriaModel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
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
            HashSet<Integer> diningHall = new HashSet<Integer>();
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


            for(int i=0; i<eateries.length();i++){
                CafeteriaModel cafeteriaModel = new CafeteriaModel();
                JSONObject child = eateries.getJSONObject(i);
                cafeteriaModel.setName(child.getString("name"));
                cafeteriaModel.setLattitude(child.getDouble("latitude"));
                cafeteriaModel.setLongitude(child.getDouble("longitude"));
                cafeteriaModel.setNickName(child.getString("nameshort"));
                JSONArray methods = child.getJSONArray("payMethods");
                ArrayList<String> payMethods = new ArrayList<String>();
                cafeteriaModel.setPay_methods(payMethods);
                for(int j=0; j< methods.length();j++){
                    JSONObject method = methods.getJSONObject(j);
                    cafeteriaModel.getPay_methods().add(method.getString("descrshort"));
                }
                if(diningHall.contains(child.getString("name"))){
                    cafeteriaModel.setIs_diningHall(true);
                }
                if(cafeteriaModel.getIs_diningHall()){
                    
                }


            }


            //Log.i("TAG",data.toString());

            return null;



        }catch(Exception e){
            e.printStackTrace();
            return null;
        }

    }
}
