package com.cornellappdev.android.eatery.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;

/** This class represents a single Cafeteria (either a cafe or a dining hall) */
public class CafeteriaModel implements Cloneable, Serializable, Comparable<CafeteriaModel> {
  private ArrayList<ArrayList<MealModel>> weeklyMenu = new ArrayList<ArrayList<MealModel>>();
  private ArrayList<String> payMethods;
  private ArrayList<String> searchedItems;
  private CafeModel cafeInfo = new CafeModel();
  private CafeteriaArea area;
  private Double lat;
  private Double lng;
  private String buildingLocation;
  private String closeTime;
  private String name;
  private String nickName;
  private boolean isDiningHall;
  private boolean isHardCoded;
  private boolean matchesFilter = true;
  private boolean matchesSearch = true;
  private boolean openPastMidnight = false;
  private int id;

  public enum CafeteriaArea {
    NORTH,
    CENTRAL,
    WEST;
  }

  public enum Status {
    OPEN,
    CLOSINGSOON,
    CLOSED;
  }

  /** ***************************************** SETTERS AND GETTERS */
  public ArrayList<String> getSearchedItems() {
    return searchedItems;
  }

  public void setSearchedItems(ArrayList<String> searchedItems) {
    this.searchedItems = searchedItems;
  }

  public String getCloseTime() {
    return closeTime;
  }

  public void setCloseTime(String closeTime) {
    this.closeTime = closeTime;
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

  public boolean matchesFilter() {
    return matchesFilter;
  }

  public void setMatchesFilter(boolean b) {
    matchesFilter = b;
  }

  public boolean matchesSearch() {
    return matchesSearch;
  }

  public void setMatchesSearch(boolean b) {
    matchesSearch = b;
  }

  public boolean getIsDiningHall() {
    return isDiningHall;
  }

  public void setIsDiningHall(boolean isDiningHall) {
    this.isDiningHall = isDiningHall;
  }

  public ArrayList<String> getPayMethods() {
    return payMethods;
  }

  public void setPayMethods(ArrayList<String> payMethods) {
    this.payMethods = payMethods;
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

  public Double getLng() {
    return lng;
  }

  public void setLng(Double lng) {
    this.lng = lng;
  }

  public Double getLat() {
    return lat;
  }

  public void setLat(Double lat) {
    this.lat = lat;
  }

  public boolean isOpenPastMidnight() {
    return openPastMidnight;
  }

  public void setOpenPastMidnight(boolean openPastMidnight) {
    this.openPastMidnight = openPastMidnight;
  }

  public CafeteriaArea getArea() {
    return area;
  }

  public void setArea(CafeteriaArea area) {
    this.area = area;
  }

  /** ***************************************** NORMAL METHODS */
  public String stringTo() {
    String info = String.format("Name/nickname: %s/%s", name, nickName);
    String locationString = String.format("Area: %s", area);
    String payMethodsString = String.format("Pay Methods: %s", payMethods.toString());
    String menuString = "";

    if (isDiningHall) {
      for (ArrayList<MealModel> meal : weeklyMenu) {
        for (MealModel mealIndiv : meal) {
          menuString = menuString + mealIndiv.stringTo();
        }
      }
    }

    return String.format(
        "%s\nlocationString%spayMethodsString\nMenu%s",
        info, locationString, payMethodsString, menuString);
  }

  public int indexOfCurrentDay() {
    if (!isDiningHall) return 0;

    Date now = new Date();
    for (int i = 0; i < weeklyMenu.size(); i++) {
      ArrayList<MealModel> day = weeklyMenu.get(i);
      if (day.size() > 0) {
        MealModel firstMeal = day.get(0);
        if (firstMeal.getStart().getDate() == now.getDate()) {
          return i;
        }
      }
    }

    return 0; // default case
  }

  public HashSet<String> getMealItems() {
    HashSet<String> items = new HashSet<String>();
    Date now = new Date();

    if (isOpen().equalsIgnoreCase("closed")) return items;
    if (isHardCoded || !isDiningHall) {
      items.addAll(cafeInfo.getCafeMenu());
      return items;
    }

    for (ArrayList<MealModel> day : weeklyMenu) {
      if (day.size() == 0) continue;

      MealModel firstMeal = day.get(0);
      if (firstMeal.getStart().getDate() != now.getDate()) continue;

      for (MealModel meal : day) {
        if (meal.getStart().before(now) && meal.getEnd().after(now)) {
          HashMap<String, ArrayList<String>> menu = meal.getMenu();
          for (ArrayList<String> vals : menu.values()) items.addAll(vals);
        }
      }
    }
    return items;
  }

  public String isOpen() {
    SimpleDateFormat timeFormat = new SimpleDateFormat("h:mmaa");
    SimpleDateFormat timeFormatDay = new SimpleDateFormat("MM/dd");
    boolean foundDay = false;
    Date now = new Date();
    Date now1 = now;
    if (isOpenPastMidnight() && now.getHours() < 3) {
      now1 = new Date(now.getTime() - 86400000);
    }
    if (isHardCoded) {
      int day = now.getDay();
      HashMap<Integer, ArrayList<Date>> hours = cafeInfo.getHoursH();
      if (hours.containsKey(day)) {
        foundDay = true;
        int startT = hours.get(day).get(0).getHours() * 60 + hours.get(day).get(0).getMinutes();
        int endT = hours.get(day).get(1).getHours() * 60 + hours.get(day).get(1).getMinutes();
        int curT = now.getHours() * 60 + now.getMinutes();
        if (curT >= startT && curT < endT) {
          closeTime = "Closes at " + timeFormat.format(hours.get(day).get(1));
          return "Open";
        } else if (curT < startT) {
          closeTime = "Opening at " + timeFormat.format(hours.get(day).get(0));
          return "Closed";
        }
      }
      for (int i = 1; i <= 6; i++) {
        int tempDay = day + i % 7;
        if (hours.containsKey(tempDay)) {
          Date openDate = new Date(now.getTime() + (86400000 * i));
          closeTime =
              String.format(
                  "Opening %s at %s",
                  timeFormatDay.format(openDate), timeFormat.format(hours.get(tempDay).get(0)));
          return "Closed";
        }
      }
      closeTime = " ";
      return "Closed";
    } else if (isDiningHall) {
      for (ArrayList<MealModel> day : weeklyMenu) {
        if (day.size() > 0) {
          MealModel firstMeal = day.get(0);
          if (firstMeal.getStart().getDate() == now.getDate()) {
            foundDay = true;
            for (MealModel meal : day) {
              if (meal.getStart().before(now) && meal.getEnd().after(now)) {
                closeTime = "Closes at " + timeFormat.format(meal.getEnd());
                return "Open";
              } else if (meal.getStart().after(now)) {
                closeTime = "Opening at " + timeFormat.format(meal.getStart());
                return "Closed";
              }
            }
          }
          if (foundDay) {
            closeTime =
                String.format(
                    "Opening %s at %s",
                    timeFormatDay.format(firstMeal.getStart()),
                    timeFormat.format(firstMeal.getStart()));
            return "Closed";
          }
        }
      }
      closeTime = " ";
      return "Closed";
    } else {
      HashMap<Date, ArrayList<Date>> hours = cafeInfo.getHours();
      Object[] objectArray = hours.keySet().toArray();
      Date[] hrs = Arrays.copyOf(objectArray, objectArray.length, Date[].class);
      Arrays.sort(hrs);
      for (Date day : hrs) {
        if (day.getDate() == now1.getDate()) {
          foundDay = true;
          ArrayList<Date> hour = hours.get(day);
          while (hour.size() > 1) {
            if (hour.get(0).before(now) && hour.get(1).after(now)) {
              closeTime = "Closes at " + timeFormat.format(hour.get(1));
              return "Open";
            } else if (hour.get(0).after(now)) {
              closeTime = "Opening at " + timeFormat.format(hour.get(0));
              return "Closed";
            }
            hour.remove(0);
            hour.remove(0);
          }
        }
        if (foundDay) {
          if (hours.get(day).size() > 1) {
            ArrayList<Date> hour = hours.get(day);
            closeTime =
                String.format(
                    "Opening %s at %s",
                    timeFormatDay.format(hour.get(0)), timeFormat.format(hour.get(0)));
            return "Closed";
          }
        }
      }
      closeTime = " ";
      return "Closed";
    }
  }

  public Status getCurrentStatus() {
    boolean foundDay = false;
    Date now = new Date();
    Date now1 = now;
    if (isOpenPastMidnight() && now.getHours() < 3) {
      now1 = new Date(now.getTime() - 86400000);
    }
    if (isHardCoded) {
      int day = now.getDay();
      HashMap<Integer, ArrayList<Date>> hours = cafeInfo.getHoursH();
      if (hours.containsKey(day)) {
        int startT = hours.get(day).get(0).getHours() * 60 + hours.get(day).get(0).getMinutes();
        int endT = hours.get(day).get(1).getHours() * 60 + hours.get(day).get(1).getMinutes();
        int curT = now.getHours() * 60 + now.getMinutes();

        if (curT >= startT && curT < endT) {
          Date closeTim = hours.get(day).get(1);
          return Status.OPEN;
        } else if (curT < startT) {
          return Status.CLOSED;
        }
      }
      return Status.CLOSED;
    } else if (isDiningHall) {
      for (ArrayList<MealModel> day : weeklyMenu) {
        if (day.size() > 0) {
          MealModel firstMeal = day.get(0);
          if (firstMeal.getStart().getDate() == now.getDate()) {
            foundDay = true;
            for (MealModel meal : day) {
              if (meal.getStart().before(now) && meal.getEnd().after(now)) {
                Date closeTim = meal.getEnd();
                if (closeTim.getTime() <= now.getTime() + (60000 * 30)) {
                  return Status.CLOSINGSOON;
                }
                return Status.OPEN;
              } else if (meal.getStart().after(now)) return Status.CLOSED;
            }
          }

          if (foundDay) return Status.CLOSED;
        }
      }
      closeTime = " ";
      return Status.CLOSED;
    } else {
      HashMap<Date, ArrayList<Date>> hours = cafeInfo.getHours();
      Object[] objectArray = hours.keySet().toArray();
      Date[] hrs = Arrays.copyOf(objectArray, objectArray.length, Date[].class);
      Arrays.sort(hrs);

      for (Date day : hrs) {
        if (day.getDate() == now1.getDate()) {
          foundDay = true;
          ArrayList<Date> hour = hours.get(day);

          while (hour.size() > 1) {
            Date eateryOpenTime = hour.remove(0);
            Date eateryCloseTime = hour.remove(0);

            if (eateryOpenTime.before(now) && eateryCloseTime.after(now)) {
              if (eateryCloseTime.getTime() <= now.getTime() + (60000 * 30)) {
                return Status.CLOSINGSOON;
              }
              return Status.OPEN;
            } else if (eateryOpenTime.after(now)) {
              return Status.CLOSED;
            }
          }
        }

        if (foundDay && hours.get(day).size() > 1) {
          ArrayList<Date> hour = hours.get(day);
          return Status.CLOSED;
        }
      }
      return Status.CLOSED;
    }
  }

  /** Compares the time of two CafeteriaModels */
  public int compareTo(CafeteriaModel cm) {
    if (cm.isOpen().equals(this.isOpen())) return this.getNickName().compareTo(cm.getNickName());
    if (this.isOpen().equals("Open") && cm.isOpen().equals("Closed")) return -1;
    return 1;
  }

  /** Comparator for sorting the list by CafeteriaModel's nickname */
  public static Comparator<CafeteriaModel> cafeNameComparator =
      new Comparator<CafeteriaModel>() {
        @Override
        public int compare(CafeteriaModel s1, CafeteriaModel s2) {
          String string1 = s1.getNickName().toUpperCase();
          String string2 = s2.getNickName().toUpperCase();

          if (string1.charAt(0) == '1') return -1;
          if (string2.charAt(0) == '1') return 1;
          return string1.compareTo(string2); // ascending order
        }
      };
}
