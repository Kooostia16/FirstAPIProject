package com.testtask.myapp.repositories;

import com.testtask.myapp.CompanyEntity;

import java.util.List;

public interface CustomRepository {
    List<Object[]> findCompaniesByPricesLimitTo(int limit);
    List<Object[]> findCompaniesByVolumeLimitTo(int limit);
    void deleteCompanyWithSymbol(String name);
}
