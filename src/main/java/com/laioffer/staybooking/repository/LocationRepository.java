package com.laioffer.staybooking.repository;

import com.laioffer.staybooking.model.Location;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * Used to connect to and use ElasticSearch database (CRUD) (extends ElasticsearchRepository, not JpaRepository)
 * ElasticsearchRepository<Location, Long>: Location is table, long is primary key type
 */
@Repository
public interface LocationRepository extends ElasticsearchRepository<Location, Long>, CustomLocationRepository {

}

