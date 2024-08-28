package com.ericsson.oko.entities;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.util.List;

@Repository
public class ApplicationRepo {

    @Autowired
    private EntityManager entityManager;

    @Transactional
    public Application addApplication(Application app){
        entityManager.persist(app);
        entityManager.flush();
        return app;
    }

    public Application getApplication(long id){
        return entityManager.find(Application.class, id);
    }

    public List<Application> getAll(){
        return entityManager.createQuery("SELECT a FROM Application a", Application.class).getResultList();
    }

    @Transactional
    public Application updateApplication(Application app){
        entityManager.merge(app);
        entityManager.flush();
        return app;
    }

    @Transactional
    public void removeApplication(Application app){
        entityManager.remove(entityManager.contains(app) ? app : entityManager.merge(app));
        entityManager.flush();
    }

    public <EntityType> TypedQuery<EntityType> runQuery(String q, Class<EntityType> c){
        return entityManager.createQuery(q, c);
    }

    public List<Application> getListByProperty(String property, Object value){
        TypedQuery<Application> q = entityManager.createQuery(
                String.format("select a from Application a where a.%s = ?1", property),
                Application.class);
        q.setParameter(1, value);
        return q.getResultList();
    }
}