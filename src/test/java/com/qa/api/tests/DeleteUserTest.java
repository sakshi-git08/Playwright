package com.qa.api.tests;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import com.qa.api.tests.requestPojo.Users;
import com.qa.api.tests.responsePojo.UserResponseDTO;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;

public class DeleteUserTest {
    //1. create user id - Post - 201
    //2. delete user id = delete - 204
    //3. read user id - get - 404

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
    public void deleteUserTest() throws IOException {
        Users users = Users.builder()
                .name("Sakshi")
                .email(getRandomEmail())
                .gender("female")
                .status("active").build();
//        UserRequestDTO userRequestDTO = new UserRequestDTO("Sakshi", getRandomEmail(), "female", "active");
        APIResponse response = requestContext.post("https://gorest.co.in/public/v2/users",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Authorization", "Bearer bfe4e8fa723f803937107077237755584d0d50fa1e214ca1ae0d85497118f70c")
                        .setData(users));
        System.out.println("response status : " + response.status());
        Assert.assertEquals(response.status(), 201);
        Assert.assertEquals(response.statusText(), "Created");

        String responseText = response.text();
        System.out.println(responseText);

        //convert response text / json to pojo - deserialization
        ObjectMapper mapper = new ObjectMapper();
        UserResponseDTO actualUser = mapper.readValue(responseText, UserResponseDTO.class);
        System.out.println(actualUser.getEmail());
        Assert.assertNotNull(actualUser.getId());
        int userId = actualUser.getId();
        System.out.println("new user id is : " + userId);

        //delete call
        APIResponse apiDeleteResponse = requestContext.delete("https://gorest.co.in/public/v2/users/" + userId,
                RequestOptions.create()
                        .setHeader("Authorization", "Bearer bfe4e8fa723f803937107077237755584d0d50fa1e214ca1ae0d85497118f70c"));
        System.out.println(apiDeleteResponse.status());
        System.out.println(apiDeleteResponse.statusText());
        Assert.assertEquals(apiDeleteResponse.status(), 204);

        System.out.println("Delete User Response body -> " + apiDeleteResponse.text());

        //3. Get call
        APIResponse apiGetResponse = requestContext.get("https://gorest.co.in/public/v2/users/"+userId,
                RequestOptions.create()
                        .setHeader("Authorization", "Bearer bfe4e8fa723f803937107077237755584d0d50fa1e214ca1ae0d85497118f70c"));
        int statusCode = apiGetResponse.status();
        System.out.println("API Response status code : " + statusCode);
        Assert.assertEquals(statusCode, 404);
//        Assert.assertEquals(apiGetResponse.ok(), true);
        Assert.assertEquals(apiGetResponse.statusText(), "Not Found");
        Assert.assertTrue(apiGetResponse.text().contains("Resource not found"));
    }

}
