package com.weather.controller;

import com.weather.model.WeatherInfo;
import com.weather.service.GeocodingService;
import com.weather.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Optional;

@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    @Autowired
    private GeocodingService geocodingService;

    @Autowired
    private WeatherService weatherService;

    /**
     * Get weather information based on pincode and date.
     * 
     * @param pincode the postal code to get weather for
     * @param date the date for which weather info is requested
     * @return ResponseEntity with weather info or error message
     */
    @GetMapping
    public ResponseEntity<?> getWeatherInfo(
            @RequestParam String pincode,
            @RequestParam(name = "for_date") String date) {

        try {
            // Parse the date
            LocalDate requestedDate = LocalDate.parse(date);

            // Check if weather data is already available in DB for the pincode and date
            Optional<WeatherInfo> cachedWeatherInfo = weatherService.getWeatherInfoFromDb(pincode, requestedDate);

            if (cachedWeatherInfo.isPresent()) {
                // Return cached weather information
                return ResponseEntity.ok(cachedWeatherInfo.get());
            } else {
                // Fetch lat/long for the pincode
                double[] latLong = geocodingService.getLatLongForPincode(pincode);

                if (latLong == null) {
                    return ResponseEntity.badRequest().body("Invalid pincode.");
                }

                double latitude = latLong[0];
                double longitude = latLong[1];

                // Fetch weather information from OpenWeather API
                WeatherInfo weatherInfo = weatherService.getWeatherInfoFromApi(latitude, longitude, requestedDate);

                if (weatherInfo == null) {
                    return ResponseEntity.status(500).body("Failed to fetch weather information.");
                }

                // Save the weather info to the database
                weatherService.getWeatherInfo(pincode, latitude, longitude, weatherInfo);

                // Return the fetched weather information
                return ResponseEntity.ok(weatherInfo);
            }

        } catch (Exception e) {
            // Handle errors and return a proper response
            return ResponseEntity.status(500).body("Error processing the request: " + e.getMessage());
        }
    }
    @GetMapping("/{city}")
public ResponseEntity<?> getWeatherInfoByCity(@PathVariable String city) {
    try {
        // Fetch lat/long for the city
        double[] latLong = geocodingService.getLatLongForCity(city);

        if (latLong == null) {
            return ResponseEntity.badRequest().body("Invalid city.");
        }

        double latitude = latLong[0];
        double longitude = latLong[1];

        // Fetch weather information from OpenWeather API
        WeatherInfo weatherInfo = weatherService.getWeatherInfoFromApi(latitude, longitude, LocalDate.now());

        if (weatherInfo == null) {
            return ResponseEntity.status(500).body("Failed to fetch weather information.");
        }

        // Return the fetched weather information
        return ResponseEntity.ok(weatherInfo);

    } catch (Exception e) {
        // Handle errors and return a proper response
        return ResponseEntity.status(500).body("Error processing the request: " + e.getMessage());
    }
}
}
