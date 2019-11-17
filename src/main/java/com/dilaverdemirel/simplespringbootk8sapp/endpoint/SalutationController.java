package com.dilaverdemirel.simplespringbootk8sapp.endpoint;

import com.dilaverdemirel.simplespringbootk8sapp.dto.SalutationDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author dilaverd - 12.11.2019
 */
@RestController
public class SalutationController {

    @GetMapping(path = "/hello/{name}", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<SalutationDTO> hello(@PathVariable("name") String name) {
        return new ResponseEntity(new SalutationDTO(String.format("Hello %s!", name)), HttpStatus.OK);
    }
}
