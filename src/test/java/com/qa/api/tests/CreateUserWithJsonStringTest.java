package com.qa.api.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;


public class CreateUserWithJsonStringTest {
    static String emailId;
    Playwright playwright;
    APIRequest request;
    APIRequestContext requestContext;

    @BeforeTest
    public void setup() {
        playwright = Playwright.create();
        request = playwright.request();
        requestContext = request.newContext();
    }

    @AfterTest
    public void tearDown() {
        playwright.close();
    }

    public static String getRandomEmail(){
        emailId = "testemail"+System.currentTimeMillis()+"@gmail.com";
        return emailId;
    }

    @Test
    public void createUserTest() throws IOException {
//        Map<String, Object> data = new HashMap<>();
//        data.put("name", "Sakshi Aggarwal");
//        data.put("email", getRandomEmail());
//        data.put("gender", "female");
//        data.put("status", "active");

        String reqJsonBody = "{\n" +
                "    \"name\": \"yellow\",\n" +
                "    \"email\": \"yellow01@gmail.com\",\n" +
                "    \"gender\": \"female\",\n" +
                "    \"status\": \"active\"\n" +
                "}";
        APIResponse response = requestContext.post("https://gorest.co.in/public/v2/users",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Authorization", "Bearer bfe4e8fa723f803937107077237755584d0d50fa1e214ca1ae0d85497118f70c")
                        .setData(reqJsonBody));
        System.out.println("response status : " + response.status());
        Assert.assertEquals(response.status(), 201);
        Assert.assertEquals(response.statusText(), "Created");

        System.out.println(response.text());

        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonResponse = mapper.readTree(response.body());
        System.out.println(jsonResponse.toPrettyString());

        //capture id from jsonResponse
        String id = jsonResponse.get("id").asText();
        System.out.println("userId : " + id);

        //Get Call : fetch the same user by Id
        APIResponse getResponse = requestContext.get("https://gorest.co.in/public/v2/users/" + id,
                RequestOptions.create()
                        .setHeader("Authorization", "Bearer bfe4e8fa723f803937107077237755584d0d50fa1e214ca1ae0d85497118f70c"));
        System.out.println("response status : " + getResponse.status());
        Assert.assertEquals(getResponse.status(), 200);
        Assert.assertEquals(getResponse.statusText(), "OK");

        System.out.println(getResponse.text());
        Assert.assertTrue(getResponse.text().contains("yellow"));
//        Assert.assertTrue(getResponse.text().contains(emailId));


    }
}
