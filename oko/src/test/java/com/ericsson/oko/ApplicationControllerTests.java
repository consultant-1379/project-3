package com.ericsson.oko;

import com.ericsson.oko.dataobject.Alert;
import com.ericsson.oko.dataobject.Summary;
import com.ericsson.oko.entities.Application;
import com.ericsson.oko.entities.Repository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(classes = OkoApplication.class, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApplicationControllerTests {
    private static final String baseUrl = "http://localhost:8080/api/v1/application";

    @Autowired
    WebApplicationContext webApplicationContext;

    private Application app;

    private void assertApplicationProps(Application a, String name, String product) {
        assertEquals(a.getName(), name);
        assertEquals(a.getProduct(), product);

    }

    private <T> HttpEntity<T> makeJsonHttpEntity(T o) {
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Accept", MediaType.APPLICATION_JSON.toString());
        return new HttpEntity<>(o, headers);
    }

    @Test
    @Order(1)
    public void testPostingNewApplication() {
        var template = new RestTemplate();
        Map<String, String> props = new HashMap<>() {{
            put("name", "test app 1");
            put("product", "ENM");
        }};
        var response = template.exchange(
                baseUrl,
                HttpMethod.POST,
                makeJsonHttpEntity(props),
                Application.class);
        app = response.getBody();
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertTrue(app.getId() > 0);
        assertApplicationProps(response.getBody(), props.get("name"), props.get("product"));
    }

    @Test
    @Order(2)
    public void testGettingApplication() {
        var template = new RestTemplate();
        var response = template.exchange(
                baseUrl + "/" + app.getId(),
                HttpMethod.GET,
                makeJsonHttpEntity(null),
                Application.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody(), app);
        assertApplicationProps(response.getBody(), app.getName(), app.getProduct());
    }

    @Test
    @Order(2)
    public void testGettingAllApplications() {
        var response = new RestTemplate().exchange(
                baseUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<List<Application>>() {
                });
        System.out.println(response.getBody());
        assertTrue(response.getBody().contains(app));
    }

    @Test()
    @Order(3)
    public void test400ResponseToBadId() {
        assertThrows(HttpClientErrorException.NotFound.class, () ->
                new RestTemplate().exchange(
                        baseUrl + "/100",
                        HttpMethod.GET,
                        makeJsonHttpEntity(null),
                        Application.class)
        );
    }

    @Test
    @Order(4)
    public void testPartialPutToSetApplicationData() {
        String newProduct = "updated test product info";
        Map props = new HashMap<String, String>() {{
            put("product", newProduct);
        }};
        new RestTemplate().exchange(
                baseUrl + "/" + app.getId(),
                HttpMethod.PUT,
                makeJsonHttpEntity(props),
                Application.class);
        var response = new RestTemplate().exchange(
                baseUrl + "/" + app.getId(),
                HttpMethod.GET,
                makeJsonHttpEntity(null),
                Application.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(response.getBody(), app);
        assertEquals(response.getBody().getProduct(), newProduct);
    }

    @Test
    @Order(5)
    public void testGettingApplicationAlerts() {
        var template = new RestTemplate();
        var response = template.exchange(
                baseUrl + "/" + app.getId() + "/alerts",
                HttpMethod.GET,
                makeJsonHttpEntity(null),
                new ParameterizedTypeReference<List<Alert>>() {
                });
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(6)
    public void testGettingApplicationSummaries(){
        var template = new RestTemplate();
        var response = template.exchange(
                baseUrl + "/" + app.getId() + "/report" ,
                HttpMethod.GET,
                makeJsonHttpEntity(null),
                new ParameterizedTypeReference<List<Summary>>() {
                });
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    @Test
    @Order(7)
    public void testDeleteApplication() {
        new RestTemplate().delete(baseUrl + "/" + app.getId());
        assertThrows(HttpClientErrorException.NotFound.class, () ->
                new RestTemplate().exchange(
                        baseUrl + app.getId(),
                        HttpMethod.GET,
                        makeJsonHttpEntity(null),
                        Application.class)
        );
    }
}




