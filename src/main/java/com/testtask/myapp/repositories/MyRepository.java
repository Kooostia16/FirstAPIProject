package com.testtask.myapp.repositories;

import com.testtask.myapp.CompanyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MyRepository extends JpaRepository<CompanyEntity,Integer>, CustomRepository {
}
