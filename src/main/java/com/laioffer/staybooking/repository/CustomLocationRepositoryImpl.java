package com.laioffer.staybooking.repository;

import com.laioffer.staybooking.model.Location;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.GeoDistanceQueryBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Used to realize customized search in ElasticSearch
 */
public class CustomLocationRepositoryImpl implements CustomLocationRepository {

    // default to search in 50km
    private final String DEFAULT_DISTANCE = "50";

    // Spring generated ElasticsearchOperations already(for basic operation of ElasticSearch in it's database),
    // not need to write ourselves
    private ElasticsearchOperations elasticsearchOperations;

    @Autowired
    public CustomLocationRepositoryImpl(ElasticsearchOperations elasticsearchOperations) {
        this.elasticsearchOperations = elasticsearchOperations;
    }

    // Search per latitude, longitude and distance
    @Override
    public List<Long> searchByDistance(double lat, double lon, String distance) {
        // if not given distance, give default distance
        if (distance == null || distance.isEmpty()) {
            distance = DEFAULT_DISTANCE;
        }

        // Use a QueryBuilder to build a Query(for search criteria)
        NativeSearchQueryBuilder queryBuilder = new NativeSearchQueryBuilder();

        // given latitude, longitude, distance and distance unit, we set the Query
        queryBuilder.withFilter(new GeoDistanceQueryBuilder("geoPoint").point(lat, lon).distance(distance, DistanceUnit.KILOMETERS));

        // based on query, we get the results (It is a SearchHits obj which include Location obj)
        SearchHits<Location> searchResult = elasticsearchOperations.search(queryBuilder.build(), Location.class);
        List<Long> locationIDs = new ArrayList<>();

        // iteration each SearchHit obj from SearchHits obj
        for (SearchHit<Location> hit : searchResult.getSearchHits()) {
            // get stay id of each search hit, add to final list
            locationIDs.add(hit.getContent().getId());
        }
        return locationIDs;
    }
}

