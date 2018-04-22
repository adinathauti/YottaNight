package com.example.user.rr;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

/**
 * Created by navneet on 23/7/16.
 */
public class GetNearbyPlacesData extends AsyncTask<Object, String, String> {

    String googlePlacesData;
    GoogleMap mMap;
    String url;

    @Override
    protected String doInBackground(Object... params) {
        try {
            Log.d("GetNearbyPlacesData", "doInBackground entered");
            mMap = (GoogleMap) params[0];
            url = (String) params[1];
            DownloadUrl downloadUrl = new DownloadUrl();
            googlePlacesData = downloadUrl.readUrl(url);
            Log.d("GooglePlacesReadTask", "doInBackground Exit");
        } catch (Exception e) {
            Log.d("GooglePlacesReadTask", e.toString());
        }
        return googlePlacesData;
    }

    @Override
    protected void onPostExecute(String result) {
        Log.d("GooglePlacesReadTask", "onPostExecute Entered");
        List<HashMap<String, String>> nearbyPlacesList = null;
        DataParser dataParser = new DataParser();
        nearbyPlacesList =  dataParser.parse(result);
        ShowNearbyPlaces(nearbyPlacesList);
        Log.d("GooglePlacesReadTask", "onPostExecute Exit");
    }

    private void ShowNearbyPlaces(List<HashMap<String, String>> nearbyPlacesList) {
        for (int i = 0; i < nearbyPlacesList.size(); i++) {
            try {
                Log.d("onPostExecute", "Entered into showing locations");
                MarkerOptions markerOptions = new MarkerOptions();
                HashMap<String, String> googlePlace = nearbyPlacesList.get(i);
                double lat = Double.parseDouble(googlePlace.get("lat"));
                double lng = Double.parseDouble(googlePlace.get("lng"));
                String placeName = googlePlace.get("place_name");
                String vicinity = googlePlace.get("rating");
                //String pid = googlePlace.get("pid");
                //String rev = pullDetails(pid);
                //Log.d("review_var", rev);
                LatLng latLng = new LatLng(lat, lng);
                markerOptions.position(latLng);
                markerOptions.title(placeName + " : " + vicinity);
                //markerOptions.snippet(rev);
                float alpha = Float.parseFloat(googlePlace.get("rating"));
                Log.d("ColorTags", "Current_alpha=" + alpha);
                //alpha = alpha/5;
                //markerOptions.alpha(alpha);

                if (alpha < 2.5) {
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
                    Log.d("ColorTags", "low=" + alpha);
                } else if ((alpha >= 2.5) && (alpha < 3.5)) {
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
                    Log.d("ColorTags", "med=" + alpha);
                } else {
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
                    Log.d("ColorTags", "hi=" + alpha);
                }
                mMap.addMarker(markerOptions);
                //move map camera
                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
            }
            catch(Exception e){Log.d("err", e.toString());}
        }
    }

    private void pullDetails(String pid)
    {   String ret_str = "";
        try {
            Log.d("detail_trace", pid);
            StringBuilder googleDetailsUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/details/json?");
            googleDetailsUrl.append("&placeid=" + pid);
            googleDetailsUrl.append("&key=" + "AIzaSyAwSBMPhOcxj48gPXFopMVsdZd1Qi2vO-g");
            String str_url = (String)googleDetailsUrl.toString();
            //Log.d("detail_trace", googleDetailsUrl.toString());
            DownloadUrl du = new DownloadUrl();
            String details_str = du.readUrl(str_url);
            JSONObject resp = new JSONObject(details_str);
            JSONObject rev = resp.getJSONArray("reviews").getJSONObject(0);
            Log.d("review_vars", rev.toString());
            ret_str = rev.getString("text");
            Log.d("fin_trace", ret_str);
        }
        catch(Exception e)
        {
            Log.d("detail_trace", e.toString());
            //return "No reviews found";
        }
        //return ret_str;
    }
}
