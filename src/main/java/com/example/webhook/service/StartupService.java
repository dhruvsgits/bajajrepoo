package com.example.webhook.service;

import com.example.webhook.model.WebhookResponse;
import com.example.webhook.repo.PaymentRepository;
import com.example.webhook.repo.EmployeeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class StartupService {

    private final RestTemplate restTemplate;

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private com.example.webhook.service.DepartmentRepository departmentRepository;

    public StartupService() {
        this.restTemplate = new RestTemplate();
    }

    @PostConstruct
    public void runOnStartup() {
        String generationUrl = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("name", "John Doe");
        requestBody.put("regNo", "REG12328");  // Example regNo
        requestBody.put("email", "john@example.com");

        // Send POST request to generate the webhook URL and token
        ResponseEntity<WebhookResponse> response = restTemplate.exchange(
                generationUrl,
                HttpMethod.POST,
                new HttpEntity<>(requestBody),
                WebhookResponse.class
        );

        if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
            WebhookResponse body = response.getBody();
            String webhookUrl = body.getWebhookUrl();
            String accessToken = body.getAccessToken();

            // Solve SQL problem and send solution
            String finalQuery = solveHighestSalaryQuery();
            sendSolutionToWebhook(webhookUrl, accessToken, finalQuery);
        } else {
            System.out.println("Failed to generate webhook.");
        }
    }

    private String solveHighestSalaryQuery() {
        return "SELECT p.AMOUNT AS SALARY, " +
                "CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS NAME, " +
                "TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE, " +
                "d.DEPARTMENT_NAME " +
                "FROM PAYMENTS p " +
                "JOIN EMPLOYEE e ON p.EMP_ID = e.EMP_ID " +
                "JOIN DEPARTMENT d ON e.DEPARTMENT = d.DEPARTMENT_ID " +
                "WHERE DAY(p.PAYMENT_TIME) != 1 " +
                "AND p.AMOUNT = ( " +
                "    SELECT MAX(AMOUNT) " +
                "    FROM PAYMENTS " +
                "    WHERE DAY(PAYMENT_TIME) != 1 " +
                ") LIMIT 1";
    }

    private void sendSolutionToWebhook(String webhookUrl, String accessToken, String finalQuery) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + accessToken);

        Map<String, String> body = new HashMap<>();
        body.put("finalQuery", finalQuery);

        HttpEntity<Map<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<String> response = restTemplate.postForEntity(webhookUrl, request, String.class);
        System.out.println("Submission status: " + response.getStatusCode());
        System.out.println("Response body: " + response.getBody());
    }
}
