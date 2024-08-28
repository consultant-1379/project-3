package com.ericsson.oko.controllers;

import com.ericsson.oko.dataobject.Alert;
import com.ericsson.oko.dataobject.Summary;
import com.ericsson.oko.dtos.RepositoryDTO;
import com.ericsson.oko.entities.*;
import com.ericsson.oko.ingest.JenkinsHandler;
import com.ericsson.oko.ingest.SummaryEngine;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping(
        value = "/api/v1/repository"
)
@CrossOrigin(origins = "*")
public class RepositoryController {

    private static final class Constants{
        public static final String NOT_FOUND_TYPE_NAME = "Repository";
    }

    @Autowired
    private ApplicationRepo applicationRepo;

    @Autowired
    private RepositoryRepo repositoryRepo;

    @Autowired
    private SummaryEngine summaryEngine;

    @Autowired
    JenkinsHandler jenkinsHandler;

    private void assertJenkinsJobExists(String jobName){
        if(!jenkinsHandler.jobExists(jobName)){
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    String.format("Could not find jenkins job: %s", jobName)
            );
        }
    }

    @PostMapping(produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Repository createRepository(@RequestBody RepositoryDTO data){
        Repository repository = new Repository();
        ControllerUtils.assertNotNull("Null fields invalid for Repository creation",
                data.getApplicationId(),
                data.getGerritUrl(),
                data.getJobName());
        ControllerUtils.assertFound(applicationRepo.getApplication(data.getApplicationId()),
                String.format(
                        ControllerUtils.getNotFoundById(),
                        ApplicationController.getNoFoundTypeName(), data.getApplicationId()));
        repository.mergeWithDTO(data);
        assertJenkinsJobExists(repository.getJobName());
        repositoryRepo.addRepository(repository);
        return repository;
    }

    @GetMapping(value = "/{id}/alerts", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public List<Alert> getRepositoryAlerts(@PathVariable(name = "id") long id) throws IOException {
        Repository repo = repositoryRepo.getRepository(id);
        ControllerUtils.assertFound(repo, String.format(
                ControllerUtils.getNotFoundById(), Constants.NOT_FOUND_TYPE_NAME, id));
        return summaryEngine.getAlerts(repositoryRepo.getRepository(id));
    }

    @GetMapping(value = "/{id}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public Repository getRepository(@PathVariable(name = "id") long id){
        var repository = repositoryRepo.getRepository(id);
        ControllerUtils.assertFound(repository,
                String.format(ControllerUtils.getNotFoundById(), Constants.NOT_FOUND_TYPE_NAME, id));
        return repository;
    }

    @PutMapping(value = "/{id}", produces = "application/json", consumes = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public Repository postRepository(@PathVariable(name = "id") long id, @RequestBody RepositoryDTO data){
        var repository = repositoryRepo.getRepository(id);
        ControllerUtils.assertFound(repository, String.format(
                ControllerUtils.getNotFoundById(), Constants.NOT_FOUND_TYPE_NAME, id));
        repository.mergeWithDTO(data);
        ControllerUtils.assertFound(applicationRepo.getApplication(repository.getApplicationId()),
                String.format(
                        ControllerUtils.getNotFoundById(),
                        ApplicationController.getNoFoundTypeName(), repository.getApplicationId()));
        assertJenkinsJobExists(repository.getJobName());
        repositoryRepo.updateRepository(repository);
        return repository;
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public @ResponseBody void deleteRepository(@PathVariable(name = "id") long id){
        var repository = repositoryRepo.getRepository(id);
        ControllerUtils.assertFound(repository, String.format(
                ControllerUtils.getNotFoundById(), Constants.NOT_FOUND_TYPE_NAME, id));
        repositoryRepo.removeRepository(repository);
    }

    @GetMapping(value = "/{id}/summary", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public Summary getSummary(@PathVariable(name = "id") long id) throws IOException {
        var repository = repositoryRepo.getRepository(id);
        ControllerUtils.assertFound(repository, String.format(
                ControllerUtils.getNotFoundById(), Constants.NOT_FOUND_TYPE_NAME, id));
        return summaryEngine.getSummary(repository);
    }

    @GetMapping(value = "/byApp/{id}", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public List<Repository> getRepositoryList(@PathVariable(name = "id") long id){
        List<Repository> repositoryList= repositoryRepo.getListByProperty("applicationId", id);
        for(Repository repo: repositoryList)
            ControllerUtils.assertFound(repo, String.format(
                    ControllerUtils.getNotFoundById(), Constants.NOT_FOUND_TYPE_NAME, id));
        return repositoryList;
    }

    @GetMapping(value = "/suggestions", produces = "application/json")
    @ResponseStatus(HttpStatus.OK)
    public List<String> getJobNameSuggestions(@RequestParam(name="s") String segment) throws IOException {
        return jenkinsHandler.getJobNameSuggestions(segment);
    }

    public static final String getNotFoundTypename(){
        return Constants.NOT_FOUND_TYPE_NAME;
    }
}
