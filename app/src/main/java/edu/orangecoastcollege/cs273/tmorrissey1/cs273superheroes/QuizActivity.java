package edu.orangecoastcollege.cs273.tmorrissey1.cs273superheroes;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;


public class QuizActivity extends AppCompatActivity {

    public static final String CHOICES = "pref_numberOfChoices";
    public static final String QUIZ_TYPE = "pref_quizType";

    private boolean phoneDevice = true;
    private boolean preferencesChanged = true;

    /**
     * Perform initialization of all fragments and loaders.
     * @param savedInstanceState Last saved instance state
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        PreferenceManager.setDefaultValues(this, R.xml.preferences, false);
        PreferenceManager.getDefaultSharedPreferences(this).
                registerOnSharedPreferenceChangeListener(preferenceChangeListener);


        // determine screen size
        int screenSize = getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK;

        // if device is tablet
        if (screenSize == Configuration.SCREENLAYOUT_SIZE_LARGE ||
                screenSize == Configuration.SCREENLAYOUT_SIZE_XLARGE)
            phoneDevice = false;

        if (phoneDevice)
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (preferencesChanged) {
            QuizActivityFragment quizFragment = (QuizActivityFragment)
                    getFragmentManager().findFragmentById(R.id.quizFragment);

            quizFragment.updateGuessRows(PreferenceManager.getDefaultSharedPreferences(this));
            quizFragment.updateQuizType(PreferenceManager.getDefaultSharedPreferences(this));
            quizFragment.resetQuiz();

            preferencesChanged = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        int orientation = getResources().getConfiguration().orientation;

        if (orientation == Configuration.ORIENTATION_PORTRAIT) {

            // Inflate the menu; this adds items to the action bar if it is present.
            getMenuInflater().inflate(R.menu.menu_quiz, menu);

            return true;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent preferencesIntent = new Intent(this, SettingsActivity.class);
        startActivity(preferencesIntent);

        return super.onOptionsItemSelected(item);
    }

    private SharedPreferences.OnSharedPreferenceChangeListener preferenceChangeListener =
            new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    preferencesChanged = true;

                    QuizActivityFragment quizFragment = (QuizActivityFragment)
                            getFragmentManager().findFragmentById(R.id.quizFragment);

                    if (key.equals(CHOICES)) { // number of choices to display changed
                        quizFragment.updateGuessRows(sharedPreferences);
                        quizFragment.resetQuiz();
                    }

                    else if (key.equals(QUIZ_TYPE)) {
                        int quizType = Integer.parseInt(sharedPreferences.getString(
                                QuizActivity.QUIZ_TYPE, "0"));

                        quizFragment.updateQuizType(sharedPreferences);
                        quizFragment.resetQuiz();
                    }
                    Toast.makeText(QuizActivity.this, R.string.restarting_quiz,
                            Toast.LENGTH_SHORT).show();
                }

            };
}
