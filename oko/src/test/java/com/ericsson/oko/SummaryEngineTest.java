package com.ericsson.oko;

import com.ericsson.oko.dataobject.Summary;
import com.ericsson.oko.entities.Application;
import com.ericsson.oko.entities.Repository;
import com.ericsson.oko.entities.RepositoryRepo;
import com.ericsson.oko.ingest.SummaryEngine;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SummaryEngineTest {

    private static final String testJobName = "common-cm-router-policy_PreCodeReview";

    @Autowired
    SummaryEngine engine;

    @Autowired
    RepositoryRepo repositoryRepo;

    @Test
    @Order(1)
    public void testGetRepositorySummary() throws IOException {
        Repository r = new Repository();
        r.setJobName(testJobName);
        var summary = engine.getSummary(r);
        assertNotNull(summary);
    }

    @Test
    @Order(2)
    public void testGetRepositoryAlerts() throws IOException {
        Repository r = new Repository();
        r.setJobName(testJobName);
        var alerts = engine.getAlerts(r);
        assertNotNull(alerts);
    }

    @Test
    @Order(3)
    public void testGetApplicationReport() throws IOException {
        Application a = new Application();
        a.setId(0);
        Repository r = new Repository(testJobName, "fake gerrit url", a.getId());
        repositoryRepo.addRepository(r);
        var report = engine.getReport(a);
        assertNotNull(report);
        assertTrue(report.stream().map(Summary::getRepository).collect(Collectors.toList()).contains(r));
    }

    @Test
    @Order(4)
    public void testGetApplicationAlerts() throws IOException {
        Application a = new Application();
        a.setId(0);
        var alerts = engine.getAlerts(a);
        assertNotNull(alerts);
        System.out.println(alerts);
    }
}
