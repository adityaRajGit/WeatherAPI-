package com.weather.repository;

import com.weather.model.WeatherInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface WeatherInfoRepository extends JpaRepository<WeatherInfo, Long> {

    /**
     * Finds weather information by pincode and date.
     * 
     * @param pincode the postal code for which weather data is needed
     * @param latitude2    the specific date for which weather data is needed
     * @return Optional containing WeatherInfo if found, or empty otherwise
     */
    Optional<WeatherInfo> findByPincodeAndDate(String pincode, double latitude2);
}
