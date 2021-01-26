package com.intergence.hgsrest.opennms;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by stephen on 17/03/2015.
 */

@RunWith(SpringJUnit4ClassRunner.class )
@ContextConfiguration(locations={"/spring/appContext-opennmsrest-buildDatabase.xml"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class BuildTestNmsDatabase {

   @Test
    public void buildDatabase() {
        // Tis all done in the Spring
    }


}
