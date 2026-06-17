package com.sagar.jewellery.repository;

import com.sagar.jewellery.model.Model3D;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface Model3DRepository extends MongoRepository<Model3D, String> {
    Optional<Model3D> findByProductId(String productId);
    void deleteByProductId(String productId);
}
