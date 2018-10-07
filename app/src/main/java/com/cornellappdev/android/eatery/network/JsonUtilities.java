package com.cornellappdev.android.eatery.network;

import android.content.Context;
import com.cornellappdev.android.eatery.model.CafeModel;
import com.cornellappdev.android.eatery.model.CafeteriaModel;
import com.cornellappdev.android.eatery.model.MealModel;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class JsonUtilities {
  public final static int MILLISECONDS_PER_DAY = 86400000;

  /** Reads JSON text with meal details from file */
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
    }
    return json;
  }

  /** Returns list of all eateries (dining halls, cafes, and external eateries not in main JSON) */
  public static ArrayList<CafeteriaModel> parseJson(String json, Context mainContext) {
    try {
      SimpleDateFormat mealTimeFormat = new SimpleDateFormat("yyyy-MM-dd hh:mmaa");
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      SimpleDateFormat mealHourFormat = new SimpleDateFormat("hh:mmaa");

      ArrayList<CafeteriaModel> finalList = new ArrayList<>();

      // Parse external eateries from hardcoded JSON
      JSONObject hardCodedData =
          new JSONObject(loadJSONFromAsset(mainContext, "externalEateries.json"));
      JSONArray hardCodedEateries = hardCodedData.getJSONArray("eateries");
      for (int i = 0; i < hardCodedEateries.length(); i++) {
        CafeteriaModel cafeteriaModel = new CafeteriaModel();
        cafeteriaModel.setHardCoded(true);

        // Parse basic info for nondining, external eateries
        JSONObject basicInfo = hardCodedEateries.getJSONObject(i);
        cafeteriaModel.setName(basicInfo.getString("name"));
        cafeteriaModel.setNickName(basicInfo.getString("nameshort"));
        cafeteriaModel.setIsDiningHall(false);
        cafeteriaModel.setBuildingLocation(basicInfo.getString("location"));
        Double lng = basicInfo.getDouble("longitude");
        Double lat = basicInfo.getDouble("latitude");
        cafeteriaModel.setLng(lng);
        cafeteriaModel.setLat(lat);

        // Find geographical area for eatery
        String area = basicInfo.getJSONObject("campusArea").getString("descrshort");
        if (area.equalsIgnoreCase("north")) {
          cafeteriaModel.setArea(CafeteriaModel.CafeteriaArea.NORTH);
        } else if (area.equalsIgnoreCase("west")) {
          cafeteriaModel.setArea(CafeteriaModel.CafeteriaArea.WEST);
        } else {
          cafeteriaModel.setArea(CafeteriaModel.CafeteriaArea.CENTRAL);
        }

        // Parse payment methods available at eatery
        JSONArray methods = basicInfo.getJSONArray("payMethods");
        ArrayList<String> payMethods = new ArrayList<String>();
        for (int j = 0; j < methods.length(); j++) {
          JSONObject method = methods.getJSONObject(j);
          payMethods.add(method.getString("descrshort"));
        }
        cafeteriaModel.setPayMethods(payMethods);

        CafeModel cafe = new CafeModel();
        // Parse cafe items available at eatery
        ArrayList<String> hardCodedCafeItems = new ArrayList<>();
        JSONArray diningItems = basicInfo.getJSONArray("diningItems");
        for (int z = 0; z < diningItems.length(); z++) {
          JSONObject item = diningItems.getJSONObject(z);
          hardCodedCafeItems.add(item.getString("item"));
        }
        cafe.setCafeMenu(hardCodedCafeItems);

        // cafeHoursHardCoded object contains <Int: corresponding to day> : <Date object: daily
        // hours>
        HashMap<Integer, ArrayList<Date>> cafeHoursHardCoded =
            new HashMap<Integer, ArrayList<Date>>();
        // operatingHours object contains operating times for a single day
        JSONArray operatingHours = basicInfo.getJSONArray("operatingHours");
        for (int c = 0; c < operatingHours.length(); c++) {
          // Find start and end operating times for cafe
          String days = operatingHours.getJSONObject(c).getString("weekday");
          JSONArray events = operatingHours.getJSONObject(c).getJSONArray("events");

          String start = events.getJSONObject(0).getString("start");
          String end = events.getJSONObject(0).getString("end");
          // TODO(lesley): someone please add reasoning for why the constant is 6
          if (start.length() == 6) {
            start = "0" + start;
          }
          if (end.length() == 6) {
            end = "0" + end;
          }

          // First entry represents opening time, second entry represents closing time
          ArrayList<Date> dailyHours = new ArrayList<Date>();
          dailyHours.add(mealHourFormat.parse(start));
          dailyHours.add(mealHourFormat.parse(end));

          // Accounts for all the different ways the JSON lists the hours by day
          if (days.equalsIgnoreCase("monday-thursday")) {
            cafeHoursHardCoded.put(1, dailyHours);
            cafeHoursHardCoded.put(2, dailyHours);
            cafeHoursHardCoded.put(3, dailyHours);
            cafeHoursHardCoded.put(4, dailyHours);

          } else if (days.equalsIgnoreCase("monday-friday")) {
            cafeHoursHardCoded.put(1, dailyHours);
            cafeHoursHardCoded.put(2, dailyHours);
            cafeHoursHardCoded.put(3, dailyHours);
            cafeHoursHardCoded.put(4, dailyHours);
            cafeHoursHardCoded.put(5, dailyHours);
          } else {
            if (days.equalsIgnoreCase("sunday")) {
              cafeHoursHardCoded.put(0, dailyHours);
            } else if (days.equalsIgnoreCase("monday")) {
              cafeHoursHardCoded.put(1, dailyHours);
            } else if (days.equalsIgnoreCase("tuesday")) {
              cafeHoursHardCoded.put(2, dailyHours);
            } else if (days.equalsIgnoreCase("wednesday")) {
              cafeHoursHardCoded.put(3, dailyHours);
            } else if (days.equalsIgnoreCase("thursday")) {
              cafeHoursHardCoded.put(4, dailyHours);
            } else if (days.equalsIgnoreCase("friday")) {
              cafeHoursHardCoded.put(5, dailyHours);
            } else if (days.equalsIgnoreCase("saturday")) {
              cafeHoursHardCoded.put(6, dailyHours);
            }
          }
        }
        cafe.setHoursH(cafeHoursHardCoded);
        cafeteriaModel.setCafeInfo(cafe);
        finalList.add(cafeteriaModel);
      }

      // Parse eateries from main source
      JSONObject parentObject = new JSONObject(json);
      JSONObject data = parentObject.getJSONObject("data");
      JSONArray eateries = data.getJSONArray("eateries");

      // Hardcoded IDs of dining halls from main JSON, found by inspection
      HashSet<Integer> diningHallIds =
          new HashSet<>(Arrays.asList(31, 25, 26, 27, 29, 3, 20, 4, 5, 30));

      // Parse through each eatery in main JSON
      for (int i = 0; i < eateries.length(); i++) {
        // Parse basic info for dining halls
        CafeteriaModel cafeteriaModel = new CafeteriaModel();
        cafeteriaModel.setHardCoded(false);
        JSONObject individualEatery = eateries.getJSONObject(i);
        cafeteriaModel.setId(individualEatery.getInt("id"));
        cafeteriaModel.setName(individualEatery.getString("name"));
        cafeteriaModel.setBuildingLocation(individualEatery.getString("location"));
        cafeteriaModel.setNickName(individualEatery.getString("nameshort"));
        Double lng = individualEatery.getDouble("longitude");
        Double lat = individualEatery.getDouble("latitude");
        cafeteriaModel.setLng(lng);
        cafeteriaModel.setLat(lat);

        // Parse payment methods available at eatery
        JSONArray methods = individualEatery.getJSONArray("payMethods");
        ArrayList<String> payMethods = new ArrayList<String>();
        for (int j = 0; j < methods.length(); j++) {
          JSONObject method = methods.getJSONObject(j);
          payMethods.add(method.getString("descrshort"));
        }
        cafeteriaModel.setPayMethods(payMethods);

        // Find geographical area for eatery
        String area = individualEatery.getJSONObject("campusArea").getString("descrshort");
        if (area.equalsIgnoreCase("north")) {
          cafeteriaModel.setArea(CafeteriaModel.CafeteriaArea.NORTH);
        } else if (area.equalsIgnoreCase("west")) {
          cafeteriaModel.setArea(CafeteriaModel.CafeteriaArea.WEST);
        } else {
          cafeteriaModel.setArea(CafeteriaModel.CafeteriaArea.CENTRAL);
        }

        // weeklyMenu represents all the meals within one week for a single eatery
        // Note that an eatery can either have a weeklyMenu or cafeItems, but not both
        ArrayList<ArrayList<MealModel>> weeklyMenu = new ArrayList<>();
        ArrayList<String> cafeItems = new ArrayList<>();

        if (diningHallIds.contains(individualEatery.getInt("id"))) {
          cafeteriaModel.setIsDiningHall(true);
        }

        if (cafeteriaModel.getIsDiningHall()) {
          JSONArray operatingHours = individualEatery.getJSONArray("operatingHours");
          // Note(lesley): A single operatingHours object contains operating times for a single day
          for (int k = 0; k < operatingHours.length(); k++) {
            // mealModelArray contains all mealModel objects for a single eatery
            ArrayList<MealModel> mealModelArray = new ArrayList<>();
            JSONObject mealPeriods = operatingHours.getJSONObject(k);
            String date = mealPeriods.getString("date");
            JSONArray events = mealPeriods.getJSONArray("events");
            // Iterates through each meal in one dining hall
            for (int l = 0; l < events.length(); l++) {
              MealModel mealModel = new MealModel();
              // Meal object represents a single meal at the eatery (ie. lunch)
              JSONObject meal = events.getJSONObject(l);

              // Find start and end operating times for meal
              String startTime = meal.getString("start");
              String endTime = meal.getString("end");
              // TODO(lesley): someone please add reasoning for why the constant is 6
              if (startTime.length() == 6) {
                startTime = "0" + startTime;
              }
              if (endTime.length() == 6) {
                endTime = "0" + endTime;
              }
              String start = String.format("%s %s", date, startTime);
              String end = String.format("%s %s", date, endTime);
              mealModel.setStart(mealTimeFormat.parse(start));
              mealModel.setEnd(mealTimeFormat.parse(end));

              mealModel.setType(meal.getString("descr"));

              // mealMenu object contains <str: station> : <Arraylist of food items>
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

          // Parse cafe items available at eatery
          JSONArray diningItems = individualEatery.getJSONArray("diningItems");
          for (int z = 0; z < diningItems.length(); z++) {
            JSONObject item = diningItems.getJSONObject(z);
            cafeItems.add(item.getString("item"));
          }

          // trillium is missing cafe items data in JSON, requiring hardcoded items
          if (cafeteriaModel.getName().equalsIgnoreCase("trillium")) {
            String trilliumItems[] = {
              "Starbucks Coffees",
              "Pepsi Beverages",
              "Breakfast Menu",
              "Salads",
              "Soup",
              "Chili",
              "Personal Pizzas",
              "Burgers",
              "Chicken Tenders",
              "Quesadillas",
              "Burritos",
              "Tacos",
              "Hot Wraps",
              "Bok Choy",
              "Fried Rice",
              "Lo Mein",
              "Baked Goods"
            };
            for (String item : trilliumItems) cafeItems.add(item);
          }
          cafe.setCafeMenu(cafeItems);

          // cafeHours contains <Date object: unique date> : <daily hours with opening and closing
          // time>
          HashMap<Date, ArrayList<Date>> cafeHours = new HashMap<Date, ArrayList<Date>>();
          // Note(lesley): A single operatingHours object contains operating times for a single day
          JSONArray operatingHours =
              individualEatery.getJSONArray(
                  "operatingHours"); // a single operating hour is a single day
          for (int c = 0; c < operatingHours.length(); c++) {
            String date = operatingHours.getJSONObject(c).getString("date");
            Date dateFinal = dateFormat.parse(date);

            // First entry represents opening time, second entry represents closing time
            ArrayList<Date> dailyHours = new ArrayList<Date>();
            JSONArray events = operatingHours.getJSONObject(c).getJSONArray("events");
            while (events.length() != 0) {
              String startTime = events.getJSONObject(0).getString("start");
              String endTime = events.getJSONObject(0).getString("end");
              events.remove(0);
              events.remove(0);
              // TODO(lesley): someone please add reasoning for why the constant is 6
              if (startTime.length() == 6) {
                startTime = "0" + startTime;
              }
              if (endTime.length() == 6) {
                endTime = "0" + endTime;
              }
              String start = String.format("%s %s", date, startTime);
              String end = String.format("%s %s", date, endTime);

              Date cafeStartTime = mealTimeFormat.parse(start);
              Date cafeEndTime = mealTimeFormat.parse(end);

              if (cafeEndTime.before(cafeStartTime)) {
                cafeEndTime = new Date(cafeEndTime.getTime() + MILLISECONDS_PER_DAY);
                cafeteriaModel.setOpenPastMidnight(true);
              }
              dailyHours.add(cafeStartTime);
              dailyHours.add(cafeEndTime);
            }
            cafeHours.put(dateFinal, dailyHours);
          }
          cafe.setHours(cafeHours);
          cafeteriaModel.setCafeInfo(cafe);
        }
        finalList.add(cafeteriaModel);
      }
      return finalList;
    } catch (JSONException | ParseException e) {
      e.printStackTrace();
      return null;
    }
  }
}
