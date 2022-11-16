package com.wingedtech.common.streams.check;

import com.wingedtech.common.constant.Requests;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping(Requests.API_DEVOPS + "/streams")
@RestController
@Slf4j
public class CheckingStreamResource {

    private final CheckingStreamService checkingStreamService;

    public CheckingStreamResource(CheckingStreamService checkingStreamService) {
        this.checkingStreamService = checkingStreamService;
    }

    @GetMapping("/report")
    public ResponseEntity<CheckingMessage> report(@RequestParam String message) {
        return ResponseEntity.ok(checkingStreamService.report(message));
    }
}
