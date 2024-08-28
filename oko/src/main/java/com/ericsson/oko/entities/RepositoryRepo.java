package com.ericsson.oko.entities;

import org.springframework.beans.factory.annotation.Autowired;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@org.springframework.stereotype.Repository
public class RepositoryRepo {
    @Autowired
    EntityManager entityManager;

    @Transactional
    public Repository addRepository(Repository repository){
        entityManager.persist(repository);
        entityManager.flush();
        return repository;
    }

    public Repository getRepository(long id){
        return entityManager.find(Repository.class, id);
    }

    @Transactional
    public Repository updateRepository(Repository repository){
        entityManager.merge(repository);
        entityManager.flush();
        return repository;
    }

    @Transactional
    public void removeRepository(Repository repository){
        entityManager.remove(entityManager.contains(repository) ? repository : entityManager.merge(repository));
        entityManager.flush();
    }

    public <EntityType> TypedQuery<EntityType> runQuery(String q, Class<EntityType> c){
        return entityManager.createQuery(q, c);
    }

    public List<Repository> getListByProperty(String property, Object value){
        TypedQuery<Repository> r = entityManager.createQuery(
                String.format("select r from Repository r where r.%s = ?1", property),
                Repository.class);
        r.setParameter(1, value);
        return r.getResultList();
    }
}