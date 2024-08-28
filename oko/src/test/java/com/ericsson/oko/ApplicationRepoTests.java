package com.ericsson.oko;

import com.ericsson.oko.entities.*;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ApplicationRepoTests {

    @Autowired
    private ApplicationRepo appRepo;
    private Application a;

    private void assertApplicationProperties(Application a, long id,  String name, String product){
        assertEquals(a.getId(), id);
        assertEquals(a.getName(), name);
        assertEquals(a.getProduct(), product);
    }

    @Test
    @Order(1)
    public void testCanCreateApplication(){
        String name = "name", product = "product";
        a = appRepo.addApplication(new Application(name, product));
        assertApplicationProperties(a, a.getId(), name, product);
    }

    @Test
    @Order(2)
    public void testCanRetrieveApplication(){
        assertEquals(a, appRepo.getApplication(a.getId()));
    }

    @Test
    @Order(3)
    public void testCanDeleteApplication(){
        appRepo.removeApplication(a);
        assertEquals(null, appRepo.getApplication(a.getId()));
    }

    @Test
    @Order(4)
    public void testCanUpdateApplication(){
        Application update = new Application("App", "Product");
        appRepo.addApplication(update);
        update.setProduct("Updated Product");
        appRepo.updateApplication(update);
        assertEquals(update.getProduct(), appRepo.getApplication(update.getId()).getProduct());
    }
}