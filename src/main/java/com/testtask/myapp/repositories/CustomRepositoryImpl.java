package com.testtask.myapp.repositories;

import com.testtask.myapp.CompanyEntity;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class CustomRepositoryImpl implements CustomRepository{
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    public List<Object[]> findCompaniesByPricesLimitTo(int limit) {
        return entityManager.createQuery("SELECT ce.id, ce.companyName, ce.price, ce.volume, ce.symbol, MAX(ce.date) AS date FROM CompanyEntity ce GROUP BY ce.companyName, ce.id ORDER BY ce.price DESC").setMaxResults(limit).getResultList();
    }

    @Override
    public List<Object[]> findCompaniesByVolumeLimitTo(int limit) {
//        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
//        CriteriaQuery<CompanyEntity> cq = cb.createQuery(CompanyEntity.class);
//        Root<CompanyEntity> root1 = cq.from(CompanyEntity.class);
//        return new ArrayList<Object[]>();
        return entityManager.createQuery("SELECT ce.id, ce.companyName, ce.price, ce.volume, ce.symbol, MAX(ce.date) AS date FROM CompanyEntity ce GROUP BY ce.companyName, ce.id ORDER BY ce.volume DESC").setMaxResults(limit).getResultList();
    }

    @Override
    public void deleteCompanyWithSymbol(String name) {
        entityManager.createQuery("DELETE from CompanyEntity ce WHERE ce.symbol="+name);
    }


}
