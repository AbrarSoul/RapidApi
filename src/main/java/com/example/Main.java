package com.example;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main extends Application {

    private Label weatherLabel = new Label("Fetching weather data...");

    @Override
    public void start(Stage stage) {
        VBox vbox = new VBox(weatherLabel);
        Scene scene = new Scene(vbox, 400, 250);  // Adjusting height to fit date/time info

        stage.setTitle("Real-Time Weather Tracker");
        stage.setScene(scene);
        stage.show();

        // Fetch the weather data from the API
        fetchWeatherData();
    }

    private void fetchWeatherData() {
        OkHttpClient client = new OkHttpClient();
        String apiKey = "2f17f9a68c986a3990aa8dc73e434d8d";  // Replace with your OpenWeatherMap API key
        String url = "https://api.openweathermap.org/data/2.5/weather?q=Tampere,FI&appid=" + apiKey + "&units=metric";

        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();

                    // Parse the JSON data
                    JSONObject jsonObject = new JSONObject(responseData);
                    String cityName = jsonObject.getString("name");  // Get the city name
                    JSONObject main = jsonObject.getJSONObject("main");
                    String temperature = main.get("temp").toString();
                    String feelsLike = main.get("feels_like").toString();
                    String humidity = main.get("humidity").toString();

                    String weatherDescription = jsonObject
                            .getJSONArray("weather")
                            .getJSONObject(0)
                            .getString("description");

                    // Get the current date and time
                    LocalDateTime currentDateTime = LocalDateTime.now();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                    String formattedDateTime = currentDateTime.format(formatter);

                    // Format the output including city name and current date/time
                    String output = String.format(
                            "City: %s\nWeather: %s\nTemperature: %s\u00B0C\nFeels Like: %s\u00B0C\nHumidity: %s%%\nDate/Time: %s",
                            cityName, capitalize(weatherDescription), temperature, feelsLike, humidity, formattedDateTime
                    );

                    // Update the JavaFX UI with the formatted weather data
                    javafx.application.Platform.runLater(() -> weatherLabel.setText(output));
                }
            }
        });
    }

    private String capitalize(String input) {
        if (input == null || input.length() == 0) return input;
        return input.substring(0, 1).toUpperCase() + input.substring(1);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
