package com.ericsson.oko;

import com.ericsson.oko.dataobject.Alert;
import com.ericsson.oko.dataobject.Summary;
import com.ericsson.oko.entities.Application;
import com.ericsson.oko.entities.Repository;
import org.apache.tomcat.jni.Local;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class PojoTests {

    private void assertAlertProperties(Alert a, String cause, Repository repository){
        assertEquals(a.getCause(), cause);
        assertEquals(a.getRepository(), repository);
    }

    private void assertSummaryProperties(Summary s, long buildCount, LocalDateTime lastBuilt, long failCount,
        long passCount, Repository repository){

        assertEquals(s.getBuildCount(),buildCount);
        assertEquals(s.getLastBuilt(),lastBuilt);
        assertEquals(s.getFailCount(),failCount);
        assertEquals(s.getPassCount(),passCount);
        assertEquals(s.getRepository(),repository);
    }

    @Test
    public void testCanCreateAlert(){
        String cause = "test cause";
        String name = "test name";
        Repository repo = new Repository("job1","gerrit_url",1);
        Alert createdAlert = new Alert(name,cause,repo);

        assertAlertProperties(createdAlert,createdAlert.getCause(),createdAlert.getRepository());
    }

    @Test
    public void testCanSetApplication(){
        String cause = "test cause";
        String name = "test name";
        Repository repo = new Repository("null","null",0);
        Alert createdAlert = new Alert(name,cause,repo);

        createdAlert.setCause("cause");
        createdAlert.setRepository(new Repository("job1","gerrit_url",1));

        assertAlertProperties(createdAlert,createdAlert.getCause(),createdAlert.getRepository());
    }

}
