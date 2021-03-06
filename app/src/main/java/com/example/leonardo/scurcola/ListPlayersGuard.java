package com.example.leonardo.scurcola;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

public class ListPlayersGuard extends AppCompatActivity {

    static final String GUARD = "GUARD";

    ArrayList<Player> playersNoGuard;
    RecyclerView myList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_players_guard);

        SharedPreferences prefs = getSharedPreferences("X", MODE_PRIVATE);
        String guardJSON = prefs.getString(GUARD, null);
        Type type = new TypeToken<ArrayList<Player>>(){}.getType();
        Gson gson = new Gson();

        if(guardJSON != null) {
            playersNoGuard = gson.fromJson(guardJSON, type);
        }


        // Get the players and remove the Clairvoyant
        Intent intent = this.getIntent();
        playersNoGuard = intent.getParcelableArrayListExtra("PLAYERS");

        Iterator<Player> i = playersNoGuard.iterator();
        while (i.hasNext()) {
            Player player = i.next(); // must be called before you can call i.remove()
            if(player.getCardName().equals("Guard")) {
                i.remove();
            }
        }

        myList = (RecyclerView) findViewById(R.id.playersNoGuard);
        myList.setLayoutManager(new LinearLayoutManager(this));
        CoursesAdapter adapter = new CoursesAdapter(playersNoGuard);
        myList.setAdapter(adapter);

        // RecyclerView with a click listener

        CoursesAdapter clickAdapter = new CoursesAdapter(playersNoGuard);

        clickAdapter.setOnEntryClickListener(new CoursesAdapter.OnEntryClickListener() {
            @Override
            public void onEntryClick(View view, int position) {
                // Stuff that will happen when a list item is clicked
                Intent intent = new Intent();
                intent.putExtra("pos", position);
                setResult(Activity.RESULT_OK, intent);
                finish();
            }
        });
        myList.setAdapter(clickAdapter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        SharedPreferences prefs = getSharedPreferences("X", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();

        String guardJSON = gson.toJson(playersNoGuard);
        editor.putString(GUARD, guardJSON);

        editor.putString("lastActivity", getClass().getName());
        editor.apply();
    }
}