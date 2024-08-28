package com.ericsson.oko;

import com.ericsson.oko.dataobject.Alert;
import com.ericsson.oko.dataobject.Summary;
import com.ericsson.oko.entities.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = OkoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RepositoryControllerTests {
    private static final String baseUrl = "http://localhost:8080/api/v1/repository";

    @Autowired
    WebApplicationContext webApplicationContext;

    @Autowired
    ApplicationRepo applicationRepo;

    private Repository repo;
    private Application testApp;

    private void assertRepositoryProps(Repository r, String jobName, String gerritUrl, long applicationId){
        assertEquals(r.getJobName(), jobName);
        assertEquals(r.getGerritUrl(), gerritUrl);
        assertEquals(r.getApplicationId(), applicationId);
    }

    private <T> HttpEntity<T> makeJsonHttpEntity(T o){
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        return new HttpEntity<>(o, headers);
    }

    @Test
    @Order(1)
    public void testPostingNewRepository(){
        testApp = applicationRepo.addApplication(new Application("fake name", "fake product"));
        var template = new RestTemplate();
        Repository repository = new Repository("ActivityServiceUI_Acceptance", "gerritUrl", testApp.getId());
        var response = template.exchange(
                baseUrl,
                HttpMethod.POST,
                makeJsonHttpEntity(repository),
                Repository.class);
        repo = response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(repo.getId() > 0);
        assertRepositoryProps(response.getBody(), repository.getJobName(), repository.getGerritUrl(), repository.getApplicationId());
    }

    @Test
    @Order(2)
    public void testGettingRepository(){
        var template = new RestTemplate();
        var response = template.exchange(
                baseUrl + "/" + repo.getId(),
                HttpMethod.GET,
                makeJsonHttpEntity(null),
                Repository.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody(), repo);
        assertRepositoryProps(response.getBody(), repo.getJobName(), repo.getGerritUrl(), repo.getApplicationId());
    }

    @Test()
    @Order(3)
    public void test400ResponseToBadId(){
        assertThrows(HttpClientErrorException.NotFound.class, () ->
                new RestTemplate().exchange(
                        baseUrl + "/100",
                        HttpMethod.GET,
                        makeJsonHttpEntity(null),
                        Repository.class)
        );
    }

    @Test
    @Order(4)
    public void testPartialPutToSetRepositoryData(){
        repo.setGerritUrl("new gerritUrl");
        new RestTemplate().exchange(
                baseUrl + "/" + repo.getId(),
                HttpMethod.PUT,
                makeJsonHttpEntity(repo),
                Repository.class);
        var response  = new RestTemplate().exchange(
                baseUrl + "/" + repo.getId(),
                HttpMethod.GET,
                makeJsonHttpEntity(null),
                Repository.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody(), repo);
        assertEquals(response.getBody().getGerritUrl(), repo.getGerritUrl());
    }

    @Test
    @Order(5)
    public void testGettingListOfRepositories(){
        var response = new RestTemplate().exchange(
                baseUrl + "/byApp/" + testApp.getId(),
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Repository>>(){});
        System.out.println(response.getBody());
        assertTrue(response.getBody().contains(repo));
    }

    @Test
    @Order(6)
    public void testGettingRepositoryAlerts() {
        var template = new RestTemplate();
        var response = template.exchange(
                baseUrl + "/" + repo.getId() + "/alerts",
                HttpMethod.GET,
                makeJsonHttpEntity(null),
                new ParameterizedTypeReference<List<Alert>>() {
                });
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(7)
    public void testGettingRepositorySummaries(){
        var template = new RestTemplate();
        var response = template.exchange(
                baseUrl + "/" + repo.getId() + "/summary" ,
                HttpMethod.GET,
                makeJsonHttpEntity(null),
                Summary.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(8)
    public void testDeleteRepository(){
        new RestTemplate().delete(baseUrl + "/" + repo.getId());
        assertThrows(HttpClientErrorException.NotFound.class, () ->
                new RestTemplate().exchange(
                        baseUrl + repo.getId(),
                        HttpMethod.GET,
                        makeJsonHttpEntity(null),
                        Repository.class)
        );
    }
}
