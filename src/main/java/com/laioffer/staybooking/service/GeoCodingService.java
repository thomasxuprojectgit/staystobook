package com.laioffer.staybooking.service;

import org.springframework.stereotype.Service;

import com.google.maps.GeoApiContext;
import com.google.maps.GeocodingApi;
import com.google.maps.errors.ApiException;
import com.google.maps.model.GeocodingResult;
import com.laioffer.staybooking.exception.GeoCodingException;
import com.laioffer.staybooking.exception.InvalidStayAddressException;
import com.laioffer.staybooking.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import java.io.IOException;

/**
 * use to get GeoCoding per address
 */
@Service
public class GeoCodingService {

    // GeoApiContext is from GoogleGeoCodingConfig(for basic setting)
    private GeoApiContext context;

    @Autowired
    public GeoCodingService(GeoApiContext context) {
        this.context = context;
    }

    // get lat and lon from google per address
    public Location getLatLng(Long id, String address) throws GeoCodingException {
        try {
            // get GeocodingResult per address first
            GeocodingResult result = GeocodingApi.geocode(context, address).await()[0];
            // if the result is just parietal match from Google
            // then throw error
            if (result.partialMatch) {
                throw new InvalidStayAddressException("Failed to find stay address");
            }

            // return the Location obj (include id and GeoPoint(Lat,Lon))
            return new Location(id, new GeoPoint(result.geometry.location.lat, result.geometry.location.lng));
        } catch (IOException | ApiException | InterruptedException e) {
            e.printStackTrace();
            throw new GeoCodingException("Failed to encode stay address");
        }
    }
}


