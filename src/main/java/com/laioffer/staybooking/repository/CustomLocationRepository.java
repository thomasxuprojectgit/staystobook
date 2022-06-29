package com.laioffer.staybooking.repository;

import java.util.List;

/**
 * Used to implement our customized code to search(by distance) in ElasticSearch database
 */
public interface CustomLocationRepository {

    // Given latitude, longtitude and distance, get the list of stay id
    List<Long> searchByDistance(double lat, double lon, String distance);
}
