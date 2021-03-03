package com.testtask.myapp;

import java.util.List;

public interface CustomRepository {
    List<CompanyEntity> findCompaniesByPricesLimitTo(int limit);
    List<CompanyEntity> findCompaniesByVolumeLimitTo(int limit);
}
