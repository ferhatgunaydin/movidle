package com.example.moovidle;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MovidleGame extends Application {
    private static final String CSV_FILE_PATH = "IMDB_Top250_Movies.csv";
    private static final int MAX_GUESSES = 5;
    private List<Movie> movies;
    private Movie selectedMovie;
    private int remainingGuesses;
    private Label titleLabel;
    private TextField guessTextField;
    private Button guessButton;
    private GridPane tilesPane; // Change the type to GridPane
    private Button restartButton;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        loadMoviesFromCSV();

        if (movies.isEmpty()) {
            System.err.println("No movies loaded from CSV.");
            return;
        }

        selectedMovie = getRandomMovie();
        remainingGuesses = MAX_GUESSES;

        titleLabel = new Label("Guess the Movie");
        guessTextField = new TextField();
        guessButton = new Button("Guess");
        tilesPane = new GridPane(); // Change the instantiation to GridPane
        restartButton = new Button("Restart");

        guessButton.setOnAction(e -> processGuess());
        restartButton.setOnAction(e -> restartGame());

        VBox mainLayout = new VBox(10);
        mainLayout.setPadding(new Insets(10));
        mainLayout.setAlignment(Pos.CENTER);
        mainLayout.getChildren().addAll(titleLabel, guessTextField, guessButton, tilesPane, restartButton);

        Scene scene = new Scene(mainLayout, 600, 400);
        primaryStage.setTitle("Movidle Game");
        primaryStage.setScene(scene);
        primaryStage.show();

        updateTiles();
    }

    private void loadMoviesFromCSV() {
        movies = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(CSV_FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(";");
                if (data.length >= 7) {
                    String title = data[1].trim();
                    String year = data[2].trim();
                    String genre = data[3].trim();
                    String origin = data[4].trim();
                    String director = data[5].trim();
                    String star = data[6].trim();

                    Movie movie = new Movie(title, year, genre, origin, director, star);
                    movies.add(movie);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Movie getRandomMovie() {
        Random random = new Random();
        int index = random.nextInt(movies.size());
        return movies.get(index);
    }

    private void processGuess() {
        String guess = guessTextField.getText().trim();

        if (!guess.isEmpty() && remainingGuesses > 0) {
            remainingGuesses--;
            if (guess.equalsIgnoreCase(selectedMovie.getTitle())) {
                displayWinMessage();
            } else {
                for (Movie movie : movies) {
                    if (movie.getTitle().equalsIgnoreCase(guess)) {
                        selectedMovie = movie;
                        break;
                    }
                }
                updateTiles();
                if (remainingGuesses == 0) {
                    displayGameOverMessage();
                }
            }
            guessTextField.clear();
        }
    }

    private void updateTiles() {
        tilesPane.getChildren().clear();

        int rowIndex = 0;
        for (MovieInfo info : selectedMovie.getMovieInfoList()) {
            Label infoLabel = new Label(info.getLabel() + ": " + info.getValue());
            infoLabel.setPadding(new Insets(5));
            infoLabel.setAlignment(Pos.CENTER);

            if (info.isMatched()) {
                infoLabel.setBackground(new Background(new BackgroundFill(Color.GREEN, CornerRadii.EMPTY, Insets.EMPTY)));
            } else {
                infoLabel.setBackground(new Background(new BackgroundFill(Color.RED, CornerRadii.EMPTY, Insets.EMPTY)));
            }

            tilesPane.add(infoLabel, 0, rowIndex++);
        }
    }







    private void displayWinMessage() {
        titleLabel.setText("You Win!");
        guessTextField.setDisable(true);
        guessButton.setDisable(true);
        restartButton.setVisible(true);
    }

    private void displayGameOverMessage() {
        titleLabel.setText("Game Over");
        guessTextField.setDisable(true);
        guessButton.setDisable(true);
        restartButton.setVisible(true);
    }

    private void restartGame() {
        selectedMovie = getRandomMovie();
        remainingGuesses = MAX_GUESSES;
        titleLabel.setText("Guess the Movie");
        guessTextField.setDisable(false);
        guessButton.setDisable(false);
        restartButton.setVisible(false);
        updateTiles();
    }

    private static class Movie {
        private String title;
        private String year;
        private String genre;
        private String origin;
        private String director;
        private String star;

        public Movie(String title, String year, String genre, String origin, String director, String star) {
            this.title = title;
            this.year = year;
            this.genre = genre;
            this.origin = origin;
            this.director = director;
            this.star = star;
        }

        public String getTitle() {
            return title;
        }

        public String getYear() {
            return year;
        }

        public String getGenre() {
            return genre;
        }

        public String getOrigin() {
            return origin;
        }

        public String getDirector() {
            return director;
        }

        public String getStar() {
            return star;
        }

        public List<MovieInfo> getMovieInfoList() {
            List<MovieInfo> infoList = new ArrayList<>();
            infoList.add(new MovieInfo("Title", title));
            infoList.add(new MovieInfo("Year", year));
            infoList.add(new MovieInfo("Genre", genre));
            infoList.add(new MovieInfo("Origin", origin));
            infoList.add(new MovieInfo("Director", director));
            infoList.add(new MovieInfo("Star", star));
            return infoList;
        }
    }

    private static class MovieInfo {
        private String label;
        private String value;
        private boolean matched;

        public MovieInfo(String label, String value) {
            this.label = label;
            this.value = value;
        }

        public String getLabel() {
            return label;
        }

        public String getValue() {
            return value;
        }

        public boolean isMatched() {
            return matched;
        }

        public void setMatched(boolean matched) {
            this.matched = matched;
        }
    }
}
