package com.testtask.myapp.repositories;

import com.testtask.myapp.CompanyEntity;

import java.util.List;

public interface CustomRepository {
    List<CompanyEntity> findCompaniesByPricesLimitTo(int limit);
    List<CompanyEntity> findCompaniesByVolumeLimitTo(int limit);
    void deleteCompanyWithName(String name);
}
