package com.example.wolf.hw4fix;

import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GetRoute extends AsyncTask<String, Void, PolylineOptions> {

    @Override
    protected PolylineOptions doInBackground(String... str_url) {
        PolylineOptions rectOptions = new PolylineOptions(); // Closes the polyline.
        try{
            JSONObject jsonObject = getJSONObjectFromURL(str_url[0]);

            int pointCount = jsonObject.getJSONObject("response").getJSONArray("route").
                    getJSONObject(0).getJSONArray("leg").
                    getJSONObject(0).getJSONArray("maneuver").length();
            Log.e("IIII","IIII - length: "+pointCount);


            double latitude=0;
            double longitude=0;
            for (int i = 0; pointCount>i; i++){

                latitude = jsonObject.getJSONObject("response").getJSONArray("route").
                        getJSONObject(0).getJSONArray("leg").
                        getJSONObject(0).getJSONArray("maneuver").
                        getJSONObject(i).getJSONObject("position").getDouble("latitude");
                longitude = jsonObject.getJSONObject("response").getJSONArray("route").
                        getJSONObject(0).getJSONArray("leg").
                        getJSONObject(0).getJSONArray("maneuver").
                        getJSONObject(i).getJSONObject("position").getDouble("longitude");

                Log.e("IIII","IIII - latitude: "+latitude);

                Log.e("IIII","IIII - longitude: "+longitude);

                rectOptions.add(new LatLng(latitude, longitude));
            }

            rectOptions.color(Color.RED);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return rectOptions;
    }

    public JSONObject getJSONObjectFromURL(String urlString) throws IOException, JSONException {
        HttpURLConnection urlConnection = null;
        URL url = new URL(urlString);
        urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.setReadTimeout(10000 /* milliseconds */ );
        urlConnection.setConnectTimeout(15000 /* milliseconds */ );
        urlConnection.setDoOutput(true);
        urlConnection.connect();

        BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()));
        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line + "\n");
        }
        br.close();

        String jsonString = sb.toString();

        return new JSONObject(jsonString);
    }

}
