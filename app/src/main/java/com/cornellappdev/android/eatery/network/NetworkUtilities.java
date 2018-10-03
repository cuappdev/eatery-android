package com.cornellappdev.android.eatery.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public final class NetworkUtilities {
  private static final String DINING_URI = "https://now.dining.cornell.edu/api/1.0/dining/eateries.json";

  public static String getJSON() {
    try {
      URL url = new URL(DINING_URI);
      BufferedReader reader = null;
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setRequestMethod("GET");

      connection.connect();

      InputStream stream = connection.getInputStream();
      InputStreamReader isw = new InputStreamReader(stream);
      reader = new BufferedReader(isw);

      StringBuffer buffer = new StringBuffer();

      String line = "";
      while ((line = reader.readLine()) != null) {
        buffer.append(line);
      }

      String json = buffer.toString();
      return json;
    } catch (IOException e) {
      e.printStackTrace();
      return null;
    }
  }
}
