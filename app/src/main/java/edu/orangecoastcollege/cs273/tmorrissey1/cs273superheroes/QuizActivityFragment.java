package edu.orangecoastcollege.cs273.tmorrissey1.cs273superheroes;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;



/**
 * A placeholder fragment containing a simple view.
 */
public class QuizActivityFragment extends Fragment {

    private static final String TAG = "Superheroes Activity";

    private static  final int SUPERHEROES_IN_QUIZ = 10;
    private static final int TOTAL_SUPERHEROES = 25;


    private ArrayList<Superhero> quizButtonList;
    private List<Superhero> quizSuperheroesList;
    private Set<String> quizTypeSet;
    private String correctAnswer;
    private int totalGuesses;
    private int correctAnswers;
    private int guessRows;
    private SecureRandom random;
    private Handler handler;

    private TextView questionNumberTextView;
    private ImageView superheroImageView;
    private LinearLayout[] guessLinearLayouts;
    private TextView answerTextView;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        quizButtonList = QuizActivity.allSuperHeroes;
        quizSuperheroesList = new ArrayList<>();

        random = new SecureRandom();
        handler = new Handler();



        questionNumberTextView = (TextView) view.findViewById(R.id.questionNumberTextView);
        superheroImageView = (ImageView) view.findViewById(R.id.superHeroImageView);
        guessLinearLayouts = new LinearLayout[4];
        guessLinearLayouts[0] = (LinearLayout) view.findViewById(R.id.row1LinearLayout);
        guessLinearLayouts[1] = (LinearLayout) view.findViewById(R.id.row2LinearLayout);
        guessLinearLayouts[2] = (LinearLayout) view.findViewById(R.id.row3LinearLayout);
        guessLinearLayouts[3] = (LinearLayout) view.findViewById(R.id.row4LinearLayout);
        answerTextView = (TextView) view.findViewById(R.id.answerTextView);

        // Configure listeners for the guess buttons
        for (LinearLayout row : guessLinearLayouts) {
            int childCount = row.getChildCount();

            for (int column = 0; column < childCount; ++column) {
                Button button = (Button) row.getChildAt(column);
                button.setOnClickListener(guessButtonListener);
            }
        }

        questionNumberTextView.setText(getString(R.string.question, 1, SUPERHEROES_IN_QUIZ));
        return view;
    }

    public void updateGuessRows(SharedPreferences sharedPreferences) {
        // Get number of guess rows that should be displayed
        String choices = sharedPreferences.getString(QuizActivity.CHOICES, null);
        guessRows = Integer.parseInt(choices) / 2;

        // Hide all guess button linear layouts
        for (LinearLayout layout : guessLinearLayouts)
            layout.setVisibility(View.GONE);

        // Display appropriate guess button LinearLayouts
        for (int row = 0; row < guessRows; ++row)
            guessLinearLayouts[row].setVisibility(View.VISIBLE);
    }

    public void updateQuizType(SharedPreferences sharedPreferences) {
        quizTypeSet = sharedPreferences.getStringSet(QuizActivity.QUIZ_TYPE, null);
    }

    public void resetQuiz() {
        quizButtonList.clear();

        // Reset correctAnswers, totalGuesses, and clear quizSuperheroesList
        correctAnswers = 0;
        totalGuesses = 0;
        quizSuperheroesList.clear();

        int superheroCounter = 1;

        while (superheroCounter <= SUPERHEROES_IN_QUIZ) {
            int randomIndex = random.nextInt(TOTAL_SUPERHEROES);

            // Get random file name
            Superhero singleHero = QuizActivity.allSuperHeroes.get(randomIndex);

            // If the quiz type is enabled and hasn't already been chosen
            if (!quizSuperheroesList.contains(singleHero)) {
                quizSuperheroesList.add(singleHero);
                ++superheroCounter;
            }
        }

        loadNextSuperhero();
    }

    private void loadNextSuperhero() {
        // Get file name of next superhero and remove it from the list
        Superhero nextSuperHero = quizSuperheroesList.remove(0);
        correctAnswer = nextSuperHero.getName();
        answerTextView.setText("");

        // Display the current question number
        questionNumberTextView.setText(getString(R.string.question,
                (correctAnswers + 1), SUPERHEROES_IN_QUIZ));

        // Use AssetManager to load next image from assets folder
        AssetManager assets = getActivity().getAssets();

        // Get an InputStream to the asset representing the next flag and try to use the InputStream
        try (InputStream stream = assets.open("images/" + nextSuperHero.getImageName())){

            // Load the assets as a drawable and display on superheroImageView
            Drawable imageOfSuperhero = Drawable.createFromStream(stream, nextSuperHero.getImageName());
            superheroImageView.setImageDrawable(imageOfSuperhero);
        }

        catch (IOException e) {
            Log.e(TAG, "Error loading " + nextSuperHero.getImageName(), e);
        }

        // Shuffle file names
        Collections.shuffle(quizButtonList);

        // Put the correct answers at the end of the file name list
        int correct = quizButtonList.indexOf(nextSuperHero);
        quizButtonList.add(quizButtonList.remove(correct));

        // Add 2, 4, 6, or 8 guess buttons based on the value of guessRows
        for (int row = 0; row < guessRows; ++row) {
            int columnCount = guessLinearLayouts[row].getChildCount();

            // Place buttons in currentTableRow
            for (int column = 0; column < columnCount; ++column) {
                // Get reference to button to configure
                Button newGuessButton = (Button) guessLinearLayouts[row].getChildAt(column);
                newGuessButton.setEnabled(true);

                // Get country name and set it as newGuessButton's text
                String buttonText = quizButtonList.get((row * 2) + column).getName();
                newGuessButton.setText(buttonText);
            }
        }
        // Randomly replace one button with the correct answer
        int row = random.nextInt(guessRows);
        int column = random.nextInt(2);
        LinearLayout randomRow = guessLinearLayouts[row];
        ((Button) randomRow.getChildAt(column)).setText(correctAnswer);
    }

    private View.OnClickListener guessButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Button guessButton = ((Button) v);
            String guess = guessButton.getText().toString();

            ++totalGuesses;

            if (guess.equals(correctAnswer)) {
                ++correctAnswers;

                // Display correct answer in green text
                answerTextView.setText(correctAnswer + "!");
                answerTextView.setTextColor(getResources().getColor(R.color.correct_answer,
                        getContext().getTheme()));

                // disable all guess buttons
                disableButtons();

                // If the user has correctly identified SUPERHEROES_IN_QUIZ flags
                if (correctAnswers == SUPERHEROES_IN_QUIZ) {
                    // DialogFragment to display quiz stats and start new quiz

                    DialogFragment quizResults = new DialogFragment() {
                        // Create an AlertDialog and return it
                        @Override
                        public Dialog onCreateDialog(Bundle bundle) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage(getString(R.string.results, totalGuesses,
                                    (1000.0 / (double) totalGuesses)));

                            // "Reset Quiz" button
                            builder.setPositiveButton(R.string.reset_quiz,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            resetQuiz();
                                        }
                                    });
                            // Return the AlertDialog
                            return builder.create();
                        }
                    };



                    quizResults.setCancelable(false);
                    quizResults.show(getFragmentManager(), "quiz results");
                }
                else {
                    // Answer is correct but quiz is not over
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadNextSuperhero();
                        }
                    }, 2000); // 2000 milliseconds for 2 second delay
                }
            }
            else { // Answer was incorrect

                // Display incorrect in red
                answerTextView.setText(R.string.incorrect_answer);
                answerTextView.setTextColor(getResources().getColor(R.color.incorrect_answer,
                        getContext().getTheme()));
                guessButton.setEnabled(false);
            }
        }
    };



    private void disableButtons() {
        for (int i = 0; i < guessRows; ++i) {
            LinearLayout guessRow = guessLinearLayouts[i];
            int childCount = guessRow.getChildCount();

            for (int j = 0; j < childCount; ++j)
                guessRow.getChildAt(j).setEnabled(false);
        }
    }



}

