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

public class UpdateUserPutCall {
    //1. post: user id - 123

    //2. put: user id - /123

    //3. get: user id - /123

    //post call: create a user
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
        Assert.assertEquals(actualUser.getEmail(), users.getEmail());
        Assert.assertEquals(actualUser.getName(), users.getName());
        Assert.assertEquals(actualUser.getStatus(), users.getStatus());
        Assert.assertEquals(actualUser.getGender(), users.getGender());
        Assert.assertNotNull(actualUser.getId());

        int userId = actualUser.getId();
        System.out.println("new user id is : " + userId);

        //update active to inactive
        users.setStatus("inactive");
        users.setName("Sakshi Testing");

        System.out.println("----------------PUT Call---------------------");

        //2. Put call - update user
        APIResponse apiPutResponse = requestContext.put("https://gorest.co.in/public/v2/users/" + userId,
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Authorization", "Bearer bfe4e8fa723f803937107077237755584d0d50fa1e214ca1ae0d85497118f70c")
                        .setData(users));

        System.out.println(apiPutResponse.status() + " : " + apiPutResponse.statusText());
        Assert.assertEquals(apiPutResponse.status(), 200);

        String putResponseText = apiPutResponse.text();
        System.out.println("updated user :  "+  putResponseText);
        UserResponseDTO actualPutUser = mapper.readValue(putResponseText, UserResponseDTO.class);
        Assert.assertEquals(actualPutUser.getId(), userId);
        Assert.assertEquals(actualPutUser.getStatus(), users.getStatus());
        Assert.assertEquals(actualPutUser.getName(), users.getName());

        //3. Get the updated user with get call
        APIResponse apiGetResponse = requestContext.get("https://gorest.co.in/public/v2/users/"+ userId,
                RequestOptions.create()
                        .setHeader("Authorization", "Bearer bfe4e8fa723f803937107077237755584d0d50fa1e214ca1ae0d85497118f70c"));
        int statusCode = apiGetResponse.status();
        System.out.println("API Response status code : " + statusCode);
        Assert.assertEquals(statusCode, 200);
        Assert.assertEquals(apiGetResponse.ok(), true);

        String statusText = apiGetResponse.statusText();
        System.out.println("API Response status text : " + statusText);
        Assert.assertEquals(statusText, "OK");

        System.out.println("--------print api plain text -------------");
        System.out.println(apiGetResponse.text());
        String apiGetResponseText =  apiGetResponse.text();

        UserResponseDTO actualGetUser = mapper.readValue(apiGetResponseText, UserResponseDTO.class);
        Assert.assertEquals(actualGetUser.getId(), userId);
        Assert.assertEquals(actualGetUser.getStatus(), users.getStatus());
        Assert.assertEquals(actualGetUser.getName(), users.getName());

    }


}
