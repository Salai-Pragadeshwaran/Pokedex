package com.example.pokedex2.ui.items;

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

public class ItemLoader extends AsyncTaskLoader<List<Item>> {


    private static final String LOG_TAG = ItemLoader.class.getName();

    /**
     * Query URL
     */
    private String mUrl;

    public ItemLoader(@NonNull Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public List<Item> loadInBackground() {
        if (mUrl == null) {
            return null;
        }


        String jsonResponse = QueryList.fetchData(mUrl);
        List<Item> items = new ArrayList<>();

        try {

            // TODO: Parse the response given by the SAMPLE_JSON_RESPONSE string and
            JSONObject itemData = new JSONObject(jsonResponse);
            JSONArray results;
            if (itemData.has("sprites")) {
                JSONObject sprites = itemData.getJSONObject("sprites");
                String itemUrl = sprites.getString("default");
                String itemName = itemData.getString("name");
                items.add(new Item(itemName, itemUrl));
                return items;

            }
            results = itemData.getJSONArray("results");
            for (int i = 0; i < results.length(); i++) {
                JSONObject item = results.getJSONObject(i);
                String itemName = item.getString("name");


                items.add(new Item(itemName,
                        "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/items/" + itemName + ".png"));


            }


        } catch (JSONException e) {

            Log.e("QueryUtils", "Problem parsing the JSON results", e);
        }


        return items;
    }
}
