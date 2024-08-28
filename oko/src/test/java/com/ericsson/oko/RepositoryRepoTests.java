package com.ericsson.oko;


import com.ericsson.oko.entities.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.Id;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RepositoryRepoTests {

    @Autowired
    private RepositoryRepo rRepo;
    private com.ericsson.oko.entities.Repository r;

    private void assertRepositoryProperties(Repository r, String jobName, String gerritUrl, long id){
        assertEquals(r.getJobName(), jobName);
        assertEquals(r.getGerritUrl(), gerritUrl);
        assertEquals(r.getId(), id);
    }

    @Test
    @Order(1)
    public void testCanCreateRepository(){
        String jobName = "jobName"; String gerritUrl = "gerritUrl";
        long id = 1;
        r = rRepo.addRepository(new Repository(jobName, gerritUrl, id));
        assertRepositoryProperties(r, jobName, gerritUrl, r.getId());
    }

    @Test
    @Order(2)
    public void testCanRetrieveRepository(){
        assertEquals(r, rRepo.getRepository(r.getId()));
    }

    @Test
    @Order(3)
    public void testCanDeleteRepository(){
        rRepo.removeRepository(r);
        assertEquals(null, rRepo.getRepository(r.getId()));
    }

    @Test
    @Order(4)
    public void testCanUpdateRepository(){
        Repository update = new Repository("jobName", "gerritUrl", 1);
        rRepo.addRepository(update);
        update.setGerritUrl("Updated gerritUrl");
        rRepo.updateRepository(update);
        assertEquals(update.getGerritUrl(), rRepo.getRepository(update.getId()).getGerritUrl());
    }
}
