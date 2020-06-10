package com.example.pokedex2.ui.Location;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import com.example.pokedex2.ui.main.QueryList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LocationLoader extends AsyncTaskLoader<List<String>> {

    private static final String LOG_TAG = LocationLoader.class.getName();

    private String mUrl;

    public LocationLoader(@NonNull Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public List<String> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        String jsonResponse = QueryList.fetchData(mUrl);
        List<String> locations = new ArrayList<>();


        try {

            // TODO: Parse the response given by the SAMPLE_JSON_RESPONSE string and
            JSONObject pokemonData = new JSONObject(jsonResponse);
            JSONArray results;
            if (pokemonData.has("results")) {
                results = pokemonData.getJSONArray("results");
            } else {
                String pokeName = pokemonData.getString("name");
                locations.add(pokeName);
                return locations;
            }
            for (int i = 0; i < results.length(); i++) {
                JSONObject poke = results.getJSONObject(i);
                String pokeName = poke.getString("name");
                if (pokemonData.has("sprites")) {
                    locations.add(pokeName);
                } else {

                    locations.add(pokeName);
                }

            }

        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the JSON results", e);
        }

        return locations;
    }
}
