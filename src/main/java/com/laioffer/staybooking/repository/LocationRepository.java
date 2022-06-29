package com.laioffer.staybooking.repository;

import com.laioffer.staybooking.model.Location;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * Used to connect to and use ElasticSearch database (CRUD) (extends ElasticsearchRepository, not JpaRepository)
 * ElasticsearchRepository<Location, Long>: Location is table, long is primary key type
 *
 * LocationRepository extends ElasticsearchRepository instead of JpaRepository since Elastcisearch has a different query
 * implementation than MySQL. But similar to JpaRepository, LocationRepository also provides some basic query functions
 * like find(), save() and delete(). But since our service needs to support search based on Geolocation, we need to
 * implement the search function ourselves.
 *
 * We extends CustomLocationRepository here and put customized method inside CustomLocationRepository instead of put
 * customized method in here because we can tell the custom method are not from ElasticsearchRepository
 */
@Repository
public interface LocationRepository extends ElasticsearchRepository<Location, Long>, CustomLocationRepository {

}

