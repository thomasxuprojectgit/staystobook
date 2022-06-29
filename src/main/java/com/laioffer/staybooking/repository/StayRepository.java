package com.laioffer.staybooking.repository;

import com.laioffer.staybooking.model.Stay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.laioffer.staybooking.model.User;
import java.util.List;


/**
 * JpaRepository<Stay, Long>  CRUD to Stay, the primary key data type is Long
 * Spring Data will implement for us, no need to implement by us
 */
@Repository
public interface StayRepository extends JpaRepository<Stay, Long> {

    // JPA will convert method names to SQL language automatically
    // Spring Data will implement for us
    // find all stay for a host(user)
    // host is host in Stay class
    List<Stay> findByHost(User user);

    // find a stay per given id and user
    // Spring Data will implement for us
    // id is stay id in stay class, host is host in stay class
    // when need delete, we need to make sure the user is the owner, that this user has authority to delete stay per id
    // so only id not good enough, otherwise, other owner can delete my stay
    Stay findByIdAndHost(Long id, User host);

    // based on guest number needed and guest number available to further filter out some stay ids, then get the final list of stays
    List<Stay> findByIdInAndGuestNumberGreaterThanEqual(List<Long> ids, int guestNumber);

}
