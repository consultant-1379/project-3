package com.ericsson.oko.dtos;

public class ApplicationDTO {
    private String name = null;
    private String product = null;

    ApplicationDTO(){
        this.name = null;
        this.product = null;
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
        return "ApplicationDTO{" +
                "name='" + name + '\'' +
                ", object='" + product + '\'' +
                '}';
    }
}
