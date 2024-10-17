package com.weather.controller;
import com.example.demo.WeatherApplication;
import com.weather.model.WeatherInfo;
import com.weather.service.GeocodingService;
import com.weather.service.WeatherService;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.http.HttpStatus.*;

@SpringBootTest(classes = WeatherApplication.class)
public class WeatherControllerTest {

    @Mock
    private GeocodingService geocodingService;

    @Mock
    private WeatherService weatherService;

    @InjectMocks
    private WeatherController weatherController;

    @Test
    void testGetWeatherInfo_CachedData() {
        // Mock the pincode and date
        String pincode = "123456";
        LocalDate date = LocalDate.now();

        // Mock the cached weather data
        WeatherInfo cachedWeatherInfo = new WeatherInfo(pincode, 0, 0, pincode);
        when(weatherService.getWeatherInfoFromDb(pincode, date)).thenReturn(Optional.of(cachedWeatherInfo));

        // Call the controller method
        ResponseEntity<?> response = weatherController.getWeatherInfo(pincode, date.toString());

        // Verify the interaction and response
        assertEquals(OK, response.getStatusCode());
        assertEquals(cachedWeatherInfo, response.getBody());
        verify(weatherService, times(1)).getWeatherInfoFromDb(pincode, date);
    }

    @Test
    void testGetWeatherInfo_FromApi() {
        // Mock the pincode and date
        String pincode = "123456";
        LocalDate date = LocalDate.now();
        double[] latLong = {10.0, 20.0};

        // Mock the weather API response
        WeatherInfo weatherInfo = new WeatherInfo(pincode, 0, 0, pincode);

        // Setup mock behavior
        when(weatherService.getWeatherInfoFromDb(pincode, date)).thenReturn(Optional.empty());
        when(geocodingService.getLatLongForPincode(pincode)).thenReturn(latLong);
        when(weatherService.getWeatherInfoFromApi(latLong[0], latLong[1], date)).thenReturn(weatherInfo);

        // Call the controller method
        ResponseEntity<?> response = weatherController.getWeatherInfo(pincode, date.toString());

        // Verify the interaction and response
        assertEquals(OK, response.getStatusCode());
        assertEquals(weatherInfo, response.getBody());
        verify(weatherService, times(1)).getWeatherInfoFromApi(latLong[0], latLong[1], date);
    }

    @Test
    void testGetWeatherInfo_InvalidPincode() {
        // Mock an invalid pincode
        String pincode = "invalid";
        LocalDate date = LocalDate.now();

        // Mock the geocoding service to return null for invalid pincode
        when(geocodingService.getLatLongForPincode(pincode)).thenReturn(null);

        // Call the controller method
        ResponseEntity<?> response = weatherController.getWeatherInfo(pincode, date.toString());

        // Verify the interaction and response
        assertEquals(BAD_REQUEST, response.getStatusCode());
        assertEquals("Invalid pincode.", response.getBody());
        verify(geocodingService, times(1)).getLatLongForPincode(pincode);
    }

    @Test
    void testGetWeatherInfo_ApiFailure() {
        // Mock the pincode and date
        String pincode = "123456";
        LocalDate date = LocalDate.now();
        double[] latLong = {10.0, 20.0};

        // Setup mock behavior
        when(weatherService.getWeatherInfoFromDb(pincode, date)).thenReturn(Optional.empty());
        when(geocodingService.getLatLongForPincode(pincode)).thenReturn(latLong);
        when(weatherService.getWeatherInfoFromApi(latLong[0], latLong[1], date)).thenReturn(null);

        // Call the controller method
        ResponseEntity<?> response = weatherController.getWeatherInfo(pincode, date.toString());

        // Verify the interaction and response
        assertEquals(INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Failed to fetch weather information.", response.getBody());
        verify(weatherService, times(1)).getWeatherInfoFromApi(latLong[0], latLong[1], date);
    }
}
