package com.ericsson.oko.dataobject;

import com.ericsson.oko.entities.Repository;

public class Alert {

    private String cause;
    private Repository repository;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    private String name;

    public Alert(String name, String cause, Repository repository) {
        this.cause = cause;
        this.repository = repository;
        this.name = name;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }

    public Repository getRepository() {
        return repository;
    }

    public void setRepository(Repository repository) {
        this.repository = repository;
    }

    public Alert(){
        //default constructor for jackson data bind
    }
    @Override
    public String toString() {
        return "Alert{" +
                "cause='" + cause + '\'' +
                ", repository=" + repository +
                '}';
    }
}
