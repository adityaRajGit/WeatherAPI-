package com.weather.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.Map;

@Service
public class GeocodingService {

    private static final Map<String, double[]> cityCoordinates = new HashMap<>();
    private static final Map<String, double[]> pincodeCoordinates = new HashMap<>();

    static {
        cityCoordinates.put("New York", new double[]{40.7128, -74.0060});
        cityCoordinates.put("Los Angeles", new double[]{34.0522, -118.2437});
        
        pincodeCoordinates.put("10001", new double[]{40.7128, -74.0060});
        pincodeCoordinates.put("90001", new double[]{34.0522, -118.2437});
    }

    @Value("${openweather.api.key}")
    private String openWeatherApiKey;

    @Value("${openweather.geocoding.url}")
    private String openWeatherGeocodingUrl;

    private final RestTemplate restTemplate;

    public GeocodingService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    /**
     * Fetches latitude and longitude for a given pincode using OpenWeather's Geocoding API.
     * 
     * @param pincode the postal code for which to fetch the coordinates
     * @return a double array with [latitude, longitude] or null if not found
     */
    public double[] getLatLongForPincode(String pincode) {
        try {
            // Build the API request URL with the pincode and API key
            String url = UriComponentsBuilder.fromHttpUrl(openWeatherGeocodingUrl)
                    .queryParam("zip", pincode + ",IN")  // assuming the country is India (IN)
                    .queryParam("appid", openWeatherApiKey)
                    .toUriString();

            // Make the API call
            Map<String, Object>[] response = restTemplate.getForObject(url, Map[].class);

            if (response != null && response.length > 0) {
                // Extract latitude and longitude from the response
                Map<String, Object> locationData = response[0];
                double latitude = (double) locationData.get("lat");
                double longitude = (double) locationData.get("lon");

                return new double[]{latitude, longitude};
            }
        } catch (Exception e) {
            // Log the error (you can use a logging framework here)
            System.err.println("Error fetching geolocation data: " + e.getMessage());
        }
        return null;
    }

    public double[] getLatLongForCity(String city) {
        // TODO Auto-generated method stub
        return cityCoordinates.get(city);
    }
}
