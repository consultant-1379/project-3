package com.ericsson.oko.entities;

import com.ericsson.oko.dtos.ApplicationDTO;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "APPLICATION")
public class Application {

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private long id;

    @Column(name="APP_NAME", length=50, nullable=false)
    private String name;

    @Column(name="PRODUCT", nullable=false)
    private String product;

    public Application() {
    }

    public Application(String name, String product) {
        this.name = name;
        this.product = product;
    }

    public void mergeWithDTO(ApplicationDTO data){
        this.name = data.getName() == null ? this.name : data.getName();
        this.product = data.getProduct() == null ? this.product : data.getProduct();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Application)) return false;
        Application that = (Application) o;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, Application.class);
    }

    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return "Application{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", product='" + product + '\'' +
                '}';
    }
}