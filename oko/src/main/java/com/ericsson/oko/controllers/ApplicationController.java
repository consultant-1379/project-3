package com.ericsson.oko.controllers;

import com.ericsson.oko.dataobject.Alert;
import com.ericsson.oko.dataobject.Summary;
import com.ericsson.oko.entities.*;
import com.ericsson.oko.ingest.SummaryEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(
        value = "/api/v1/application"
)
@CrossOrigin(origins = "*")
public class ApplicationController {

    private static final class Constants{
        public static final String NAME_PROP_NAME = "name";
        public static final String PRODUCT_PROP_NAME = "product";
        public static final String NO_FOUND_TYPE_NAME = "Application";
    }

    @Autowired
    private SummaryEngine summaryEngine;

    @Autowired
    private ApplicationRepo applicationRepo;

    @PostMapping(value="", produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Application createApplication(@RequestBody Map<String, String> properties){
        ControllerUtils.assertPropertiesList(properties, Constants.NAME_PROP_NAME, Constants.PRODUCT_PROP_NAME);
        Application a = new Application(
                properties.get(Constants.NAME_PROP_NAME),
                properties.get(Constants.PRODUCT_PROP_NAME));
        applicationRepo.addApplication(a);
        return a;
    }

    @GetMapping(value="", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public List<Application> getApplications(){
        return applicationRepo.getAll();
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public Application getApplication(@PathVariable(name = "id") long id){
        Application app = applicationRepo.getApplication(id);
        ControllerUtils.assertFound(app, String.format(
                ControllerUtils.getNotFoundById(), Constants.NO_FOUND_TYPE_NAME, id));
        return app;
    }

    @GetMapping(value = "/{id}/alerts", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public List<Alert> getApplicationAlerts(@PathVariable(name = "id") long id) throws IOException {
        Application app = applicationRepo.getApplication(id);
        ControllerUtils.assertFound(app, String.format(
                ControllerUtils.getNotFoundById(), Constants.NO_FOUND_TYPE_NAME, id));
        return summaryEngine.getAlerts(applicationRepo.getApplication(id));
    }

    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public Application postApplication(@PathVariable(name = "id") long id, @RequestBody Map<String, String> props){
        Application app = applicationRepo.getApplication(id);
        ControllerUtils.assertFound(app, String.format(
                ControllerUtils.getNotFoundById(), Constants.NO_FOUND_TYPE_NAME, id));
        app.setName(props.getOrDefault(Constants.NAME_PROP_NAME, app.getName()));
        app.setProduct(props.getOrDefault(Constants.PRODUCT_PROP_NAME, app.getProduct()));
        applicationRepo.updateApplication(app);
        return app;
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody void deleteApplication(@PathVariable(name = "id") long id){
        Application app = applicationRepo.getApplication(id);
        ControllerUtils.assertFound(app, String.format(
                ControllerUtils.getNotFoundById(), Constants.NO_FOUND_TYPE_NAME, id));
        applicationRepo.removeApplication(app);
    }

    @GetMapping(value = "/{id}/report", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public List<Summary> getSummaries(@PathVariable(name = "id") long id) throws IOException {
        Application app = applicationRepo.getApplication(id);
        ControllerUtils.assertFound(app, String.format(
                ControllerUtils.getNotFoundById(), Constants.NO_FOUND_TYPE_NAME, id));
        return summaryEngine.getReport(app);
    }

    public static final String getNamePropName(){
        return Constants.NAME_PROP_NAME;
    }

    public static final String getProductPropName(){
        return Constants.PRODUCT_PROP_NAME;
    }

    public static final String getNoFoundTypeName(){
        return Constants.NO_FOUND_TYPE_NAME;
    }
}
