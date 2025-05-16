// SimActivationController.java
package com.telstra.simactivation.controller;

import com.telstra.simactivation.model.ActuatorResponse;
import com.telstra.simactivation.model.SimActivationRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/activate-sim")
public class SimActivationController {

    private final RestTemplate restTemplate = new RestTemplate();

    @PostMapping
    public ResponseEntity<String> activateSim(@RequestBody SimActivationRequest request) {
        // Prepare actuator payload
        String actuatorUrl = "http://localhost:8444/actuate";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Prepare minimal JSON for actuator
        String jsonPayload = "{\"iccid\":\"" + request.getIccid() + "\"}";
        HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);

        try {
            ResponseEntity<ActuatorResponse> response = restTemplate.postForEntity(
                    actuatorUrl, entity, ActuatorResponse.class);

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                boolean success = response.getBody().isSuccess();
                System.out.println("Activation status for ICCID " + request.getIccid() + ": " + success);
                return ResponseEntity.ok("Activation success: " + success);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                        .body("Failed to receive proper response from actuator");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Exception occurred: " + e.getMessage());
        }
    }
}
