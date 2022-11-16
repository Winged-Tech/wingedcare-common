package com.wingedtech.common.migrations;

import com.wingedtech.common.constant.Requests;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotBlank;

@RestController
@RequestMapping(Requests.API_DEVOPS + "/migrations")
@AllArgsConstructor
public class DataMigrationResource {

    private final DataMigrationService dataMigrationService;

    @GetMapping("/run/{name}")
    public void runMigrations(@PathVariable(name = "name") @NotBlank String name) {
        dataMigrationService.runMigration(name);
    }
}
