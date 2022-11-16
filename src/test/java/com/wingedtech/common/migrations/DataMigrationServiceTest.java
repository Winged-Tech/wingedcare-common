package com.wingedtech.common.migrations;


import com.wingedtech.common.multitenancy.Tenant;
import com.wingedtech.common.util.counter.SuccessFailCounter;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DataProcessorServiceConfiguration.class, DataMigrationConfiguration.class})
@ActiveProfiles("migrations")
public class DataMigrationServiceTest {

    public static final int TOTAL_ITEMS = 28;
    @Autowired
    private DataMigrationService dataMigrationService;

    @Test
    public void testLimit() {
        SuccessFailCounter successFailCounter = dataMigrationService.getServiceByNameWithException("item-without-limit").startProcess();
        assertThat(successFailCounter.getTotal()).isEqualTo(TOTAL_ITEMS);

        successFailCounter = dataMigrationService.getServiceByNameWithException("item-with-limit").startProcess();
        assertThat(successFailCounter.getTotal()).isEqualTo(20);

    }

    @Test
    public void testAsync() {
        dataMigrationService.getServiceByNameWithException("item-without-limit").startProcessAsync(Tenant.getCurrentTenantIdOrMaster());
    }

    @Test
    public void testParallel() {
        SuccessFailCounter successFailCounter = dataMigrationService.getServiceByNameWithException("item-parallel").startProcess();
        assertThat(successFailCounter.getTotal()).isEqualTo(TOTAL_ITEMS);
    }
}
