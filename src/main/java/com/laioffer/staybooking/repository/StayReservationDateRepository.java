package com.laioffer.staybooking.repository;

import com.laioffer.staybooking.model.Stay;
import com.laioffer.staybooking.model.StayReservedDate;
import com.laioffer.staybooking.model.StayReservedDateKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;


import java.time.LocalDate;
import java.util.List;
import java.util.Set;

/**
 * use to search MySQL database and get reserved stay ids to avoid double reserve
 * JpaRepository<StayReservedDate, StayReservedDateKey> StayReservedDate is table, StayReservedDateKey is primary key type
 */
@Repository
public interface StayReservationDateRepository extends JpaRepository<StayReservedDate, StayReservedDateKey> {

    // search list of stay ids that has been reserved between startData and endDate
    //  @Query(value = "SELECT srd.id.stay_id FROM StayReservedDate srd WHERE srd.id.stay_id IN ?1 AND srd.id.date BETWEEN ?2 AND ?3 GROUP BY srd.id.stay_id")
    //  used to customize search from database
    //  StayReservedDate is table represents by srd
    //  ?1 is first input List<Long> stayIds
    //   srd.id.date BETWEEN ?2 AND ?3 (date is between LocalDate startDate, LocalDate endDate)
    //   GROUP BY srd.id.stay_id, no need to return same stay id by the same date
    @Query(value = "SELECT srd.id.stay_id FROM StayReservedDate srd WHERE srd.id.stay_id IN ?1 AND srd.id.date BETWEEN ?2 AND ?3 GROUP BY srd.id.stay_id")
    Set<Long> findByIdInAndDateBetween(List<Long> stayIds, LocalDate startDate, LocalDate endDate);

    List<StayReservedDate> findByStay(Stay stay);

}

