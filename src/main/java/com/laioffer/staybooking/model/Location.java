package com.laioffer.staybooking.model;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.GeoPointField;

import java.io.Serializable;

/**
 * Location of an address
 * Include GeoPoint from google API and an id
 * This meta data will be saved to elastic search database
 * @Document(indexName = "loc"): @Document is connect to elastic search database. Databse's name is "loc"
 */
@Document(indexName = "loc")
public class Location implements Serializable {
    private static final long serialVersionUID = 1L;

    // correspond to stay id in Stay table
    // @Id: primary key
    // Field is same as column to SQL
    @Id
    @Field(type = FieldType.Long)
    private Long id;

    // @GeoPointField: special type of field
    // Field is same as column to SQL
    @GeoPointField
    private GeoPoint geoPoint;

    public Location(Long id, GeoPoint geoPoint) {
        this.id = id;
        this.geoPoint = geoPoint;
    }

    public Long getId() {
        return id;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }
}

