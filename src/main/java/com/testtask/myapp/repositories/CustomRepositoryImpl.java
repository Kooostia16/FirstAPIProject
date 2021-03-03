package com.testtask.myapp.repositories;

import com.testtask.myapp.CompanyEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class CustomRepositoryImpl implements CustomRepository{
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<CompanyEntity> findCompaniesByPricesLimitTo(int limit) {
        return entityManager.createQuery("SELECT ce FROM CompanyEntity ce ORDER BY ce.price DESC",
                CompanyEntity.class).setMaxResults(limit).getResultList();
    }

    @Override
    public List<CompanyEntity> findCompaniesByVolumeLimitTo(int limit) {
        return entityManager.createQuery("SELECT ce FROM CompanyEntity ce ORDER BY ce.volume",
                CompanyEntity.class).setMaxResults(limit).getResultList();
    }

    @Override
    public void deleteCompanyWithName(String name) {
        entityManager.createQuery("DELETE from CompanyEntity ce WHERE ce.companyName="+name);
    }


}
