package com.wingedtech.common.util;

import com.wingedtech.common.autoconfigure.multitenancy.MultiTenancyConfiguration;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.env.Environment;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = MultiTenancyConfiguration.class)
@ActiveProfiles("dbmigrations")
public class DbMigrationUtilsTest {

    @Autowired
    Environment environment;

    @Test
    public void loadDataFromEnvironment() {
        final List<Car> cars = DbMigrationUtils.loadDataListFromEnvironment(environment, "cars", Car.class, false);
        assertThat(cars).isNotEmpty();
        assertThat(cars.get(0).getName()).isEqualTo("Focus");
    }
    static class Car {
        private String name;
        private String brand;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getBrand() {
            return brand;
        }

        public void setBrand(String brand) {
            this.brand = brand;
        }
    }
}
