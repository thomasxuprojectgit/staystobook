package com.laioffer.staybooking.repository;

import com.laioffer.staybooking.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * extend JpaRepository that we can use JPA API to CRUD to communicate with database
 * we just need to use the methods defined under JpaRepository
 * <User, String> first is target table, second is primary key type
 * Spring Data will auto create implementations of AuthorityRepository
 * when @Repository and extends JpaRepository
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

}
