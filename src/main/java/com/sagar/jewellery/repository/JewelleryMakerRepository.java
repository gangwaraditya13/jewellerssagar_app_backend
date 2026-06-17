package com.sagar.jewellery.repository;

import com.sagar.jewellery.model.JewelleryMaker;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JewelleryMakerRepository extends MongoRepository<JewelleryMaker, String> {
    Optional<JewelleryMaker> findByEmail(String email);
    boolean existsByEmail(String email);
    List<JewelleryMaker> findByIsApproved(boolean isApproved);
}
