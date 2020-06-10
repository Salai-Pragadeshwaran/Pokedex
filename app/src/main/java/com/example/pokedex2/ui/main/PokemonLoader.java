package com.example.pokedex2.ui.main;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PokemonLoader extends AsyncTaskLoader<List<Pokemon>> {


    private static final String LOG_TAG = PokemonLoader.class.getName();
    private String mUrl;

    public PokemonLoader(@NonNull Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Nullable
    @Override
    public List<Pokemon> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        String jsonResponse = QueryList.fetchData(mUrl);
        List<Pokemon> pokemons = new ArrayList<>();

        if (MainFragment.publicPokeURL == null) {
            try {

                // TODO: Parse the response given by the SAMPLE_JSON_RESPONSE string and
                JSONObject pokemonData = new JSONObject(jsonResponse);
                JSONArray results;
                if (pokemonData.has("results")) {
                    results = pokemonData.getJSONArray("results");
                } else {
                    results = pokemonData.getJSONArray("forms");
                }
                for (int i = 0; i < results.length(); i++) {
                    JSONObject poke = results.getJSONObject(i);
                    String pokeName = poke.getString("name");
                    String pokeUrl;
                    if (pokemonData.has("sprites")) {
                        JSONObject sprites = pokemonData.getJSONObject("sprites");
                        pokeUrl = sprites.getString("front_default");
                        pokemons.add(new Pokemon(pokeName, pokeUrl));
                    } else {
                        pokeUrl = poke.getString("url");
                        pokeUrl = pokeUrl.substring("https://pokeapi.co/api/v2/pokemon/".length()
                                , pokeUrl.length() - 1);
                        pokemons.add(new Pokemon(pokeName,
                                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + pokeUrl + ".png"));
                    }

                }

            } catch (JSONException e) {
                Log.e("QueryUtils", "Problem parsing the JSON results", e);
            }
        } else if (MainFragment.publicFromFragment == "type") {
            // for Types
            try {

                // TODO: Parse the response given by the SAMPLE_JSON_RESPONSE string and
                JSONObject pokemonData = new JSONObject(jsonResponse);
                JSONArray results;
                if (pokemonData.has("pokemon")) {
                    results = pokemonData.getJSONArray("pokemon");
                } else {
                    results = pokemonData.getJSONArray("forms");
                }
                for (int i = 0; i < results.length(); i++) {
                    JSONObject poke = results.getJSONObject(i).getJSONObject("pokemon");
                    String pokeName = poke.getString("name");
                    String pokeUrl;
                    if (pokemonData.has("sprites")) {
                        JSONObject sprites = pokemonData.getJSONObject("sprites");
                        pokeUrl = sprites.getString("front_default");
                        pokemons.add(new Pokemon(pokeName, pokeUrl));
                    } else {
                        pokeUrl = poke.getString("url");
                        pokeUrl = pokeUrl.substring("https://pokeapi.co/api/v2/pokemon/".length()
                                , pokeUrl.length() - 1);
                        pokemons.add(new Pokemon(pokeName,
                                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + pokeUrl + ".png"));
                    }

                }

            } catch (JSONException e) {
                Log.e("QueryUtils", "Problem parsing the JSON results", e);
            }
        } else if (MainFragment.publicFromFragment == "region") {
            // for Types
            try {

                // TODO: Parse the response given by the SAMPLE_JSON_RESPONSE string and
                JSONObject pokemonData = new JSONObject(jsonResponse);
                JSONArray pokedexes = pokemonData.getJSONArray("pokedexes");
                String url2 = pokedexes.getJSONObject(pokedexes.length() - 1).getString("url");
                pokemonData = new JSONObject(QueryList.fetchData(url2));
                JSONArray results;
                if (pokemonData.has("pokemon_entries")) {
                    results = pokemonData.getJSONArray("pokemon_entries");
                } else {
                    results = pokemonData.getJSONArray("forms");
                }
                for (int i = 0; i < results.length(); i++) {
                    JSONObject poke = results.getJSONObject(i).getJSONObject("pokemon_species");
                    String pokeName = poke.getString("name");
                    String pokeUrl;
                    if (pokemonData.has("sprites")) {
                        JSONObject sprites = pokemonData.getJSONObject("sprites");
                        pokeUrl = sprites.getString("front_default");
                        pokemons.add(new Pokemon(pokeName, pokeUrl));
                    } else {
                        pokeUrl = poke.getString("url");
                        pokeUrl = pokeUrl.substring("https://pokeapi.co/api/v2/pokemon-species/".length()
                                , pokeUrl.length() - 1);
                        pokemons.add(new Pokemon(pokeName,
                                "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/" + pokeUrl + ".png"));
                    }

                }

            } catch (JSONException e) {
                Log.e("QueryUtils", "Problem parsing the JSON results", e);
            }
        }


        return pokemons;
    }
}
