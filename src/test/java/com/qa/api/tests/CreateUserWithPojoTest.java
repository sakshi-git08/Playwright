package com.qa.api.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import com.qa.api.tests.requestPojo.UserRequestDTO;
import com.qa.api.tests.responsePojo.UserResponseDTO;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;

public class CreateUserWithPojoTest {
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

    public static String getRandomEmail() {
        emailId = "testemail" + System.currentTimeMillis() + "@gmail.com";
        return emailId;
    }

    @Test
    public void createUserTest() throws IOException {
        UserRequestDTO userRequestDTO = new UserRequestDTO("Sakshi", getRandomEmail(), "female", "active");
        APIResponse response = requestContext.post("https://gorest.co.in/public/v2/users",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Authorization", "Bearer bfe4e8fa723f803937107077237755584d0d50fa1e214ca1ae0d85497118f70c")
                        .setData(userRequestDTO));
        System.out.println("response status : " + response.status());
        Assert.assertEquals(response.status(), 201);
        Assert.assertEquals(response.statusText(), "Created");

        String responseText = response.text();
        System.out.println(responseText);

        //convert response text / json to pojo - deserialization
        ObjectMapper mapper = new ObjectMapper();
        UserResponseDTO actualUser = mapper.readValue(responseText, UserResponseDTO.class);
        System.out.println(actualUser.getEmail());
        Assert.assertEquals(actualUser.getEmail(), userRequestDTO.getEmail());
        Assert.assertEquals(actualUser.getName(), userRequestDTO.getName());
        Assert.assertEquals(actualUser.getStatus(), userRequestDTO.getStatus());
        Assert.assertEquals(actualUser.getGender(), userRequestDTO.getGender());
        Assert.assertNotNull(actualUser.getId());
    }
}
