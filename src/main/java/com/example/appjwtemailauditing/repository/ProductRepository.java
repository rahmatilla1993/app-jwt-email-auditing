package com.example.appjwtemailauditing.repository;

import com.example.appjwtemailauditing.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Repository
@RepositoryRestResource(path = "product")
public interface ProductRepository extends JpaRepository<Product, UUID> {

}
