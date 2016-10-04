package edu.orangecoastcollege.cs273.tmorrissey1.cs273superheroes;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by Travis on 10/4/2016.
 */

public class JSONLoader {

    /**
     * Loads JSON data from a file in the assets directory.
     * @param context The activity from which the data is loaded.
     * @throws IOException If there is an error reading from the JSON file.
     */
    public static ArrayList<Superhero> loadJSONFromAsset(Context context) throws IOException {
        ArrayList<Superhero> allSuperHeroes = new ArrayList<>();
        String json = null;
        InputStream is = context.getAssets().open("cs273superheroes.json");
        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();
        json = new String(buffer, "UTF-8");

        try {
            JSONObject jsonRootObject = new JSONObject(json);
            JSONArray allSuperherosJSON = jsonRootObject.getJSONArray("CS273Superheroes");
            int numberOfSuperheroes = allSuperherosJSON.length();

            for (int i = 0; i < numberOfSuperheroes; i++) {
                JSONObject superheroJSON = allSuperherosJSON.getJSONObject(i);

                Superhero superhero = new Superhero();
                superhero.setUsername(superheroJSON.getString("Username"));
                superhero.setName(superheroJSON.getString("Name"));
                superhero.setSuperpower(superheroJSON.getString("Superpower"));
                superhero.setOneThing(superheroJSON.getString("OneThing"));
                superhero.setImageName(superheroJSON.getString("ImageName"));

                allSuperHeroes.add(superhero);
            }
        } catch (JSONException e) {
            Log.e("Superheroes", e.getMessage());
        }

        return allSuperHeroes;
    }
}
