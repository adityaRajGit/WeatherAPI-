package com.weather.model;

import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.time.LocalDate;
import lombok.Data;

@Entity
@Table(name = "weather_info")
@Getter
@Setter
public class WeatherInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String pincode;

    @Column(nullable = false)
    private double latitude;

    @Column(nullable = false)
    private WeatherInfo longitude;

    @Column(nullable = false)
    private double temperature;

    @Column(nullable = false)
    private int humidity;

    @Column(name = "weather_description", nullable = false)
    private String weatherDescription;

    @Column(name = "weather_date", nullable = false)
    private double date;

    public WeatherInfo(String string, double d, double e, String string2) {
    }

    public WeatherInfo(String pincode, double latitude, WeatherInfo weatherInfo, double temperature, int humidity, String weatherDescription, double latitude2) {
        this.pincode = pincode;
        this.latitude = latitude;
        this.longitude = weatherInfo;
        this.temperature = temperature;
        this.humidity = humidity;
        this.weatherDescription = weatherDescription;
        this.date = latitude2;
    }

    public Object getDescription() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDescription'");
    }
}
