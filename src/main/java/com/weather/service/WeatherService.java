package com.weather.service;

import com.weather.model.WeatherInfo;
import com.weather.repository.WeatherInfoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class WeatherService {

    private static final Map<String, String> weatherData = new HashMap<>();

    static {
        weatherData.put("New York", "Sunny, 25°C");
        weatherData.put("Los Angeles", "Cloudy, 20°C");
    }

    @Value("${openweather.api.key}")
    private String openWeatherApiKey;

    @Value("${openweather.weather.url}")
    private String openWeatherApiUrl;

    private final RestTemplate restTemplate;
    private final WeatherInfoRepository weatherInfoRepository;

    public WeatherService(RestTemplate restTemplate, WeatherInfoRepository weatherInfoRepository) {
        this.restTemplate = restTemplate;
        this.weatherInfoRepository = weatherInfoRepository;
    }

    /**
     * Retrieves weather information for a given pincode and date. Optimizes by checking the database first.
     * 
     * @param pincode the postal code to look up weather for
     * @param latitude2    the date for which weather information is requested
     * @return WeatherInfo the weather information retrieved either from the database or an external API
     */
    public WeatherInfo getWeatherInfo(String pincode, double latitude2, double latitude, WeatherInfo weatherInfo) {
        // Step 1: Check if weather info for the pincode and date already exists in the database
        Optional<WeatherInfo> existingWeatherInfo = weatherInfoRepository.findByPincodeAndDate(pincode, latitude2);
        if (existingWeatherInfo.isPresent()) {
            return existingWeatherInfo.get();
        }

        // Step 2: If not in database, fetch weather data from the OpenWeather API
        String url = UriComponentsBuilder.fromHttpUrl(openWeatherApiUrl)
                .queryParam("lat", latitude)
                .queryParam("lon", weatherInfo)
                .queryParam("appid", openWeatherApiKey)
                .queryParam("units", "metric")  // metric units for temperature (Celsius)
                .toUriString();

        Map<String, Object> response = restTemplate.getForObject(url, Map.class);

        if (response != null) {
            // Extract necessary weather data
            Map<String, Object> mainData = (Map<String, Object>) response.get("main");
            double temperature = (double) mainData.get("temp");
            int humidity = (int) mainData.get("humidity");

            Map<String, Object> weatherDetails = ((List<Map<String, Object>>) response.get("weather")).get(0);
            String weatherDescription = (String) weatherDetails.get("description");

            // Step 3: Create and save new WeatherInfo
            WeatherInfo newWeatherInfo = new WeatherInfo(
                    pincode,
                    latitude,
                    weatherInfo,
                    temperature,
                    humidity,
                    weatherDescription,
                    latitude2
            );

            weatherInfoRepository.save(newWeatherInfo);

            return newWeatherInfo;
        }

        return null;
    }

    public String getWeatherInfoFromDb(String pincode, LocalDate requestedDate, Object city) {
        return weatherData.get(city);
    }

    public String getWeatherInfoFromApi(double latitude, double longitude, LocalDate requestedDate, Object city) {
        return weatherData.get(city);
        
    }
}
