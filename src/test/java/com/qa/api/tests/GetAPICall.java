package com.qa.api.tests;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.RequestOptions;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.Map;

public class GetAPICall {
    Playwright playwright;
    APIRequest request;
    APIRequestContext requestContext;

    @BeforeTest
    public void setup() {
        playwright = Playwright.create();
        request = playwright.request();
        requestContext = request.newContext();
    }

    @Test
    public void getUsersApiTest() throws IOException {
        APIResponse apiResponse = requestContext.get("https://gorest.co.in/public/v2/users");

        int statusCode = apiResponse.status();
        System.out.println("API Response status code : " + statusCode);
        Assert.assertEquals(statusCode, 200);
        Assert.assertEquals(apiResponse.ok(), true);

        String statusText = apiResponse.statusText();
        System.out.println("API Response status text : " + statusText);
        Assert.assertEquals(statusText, "OK");
//        System.out.println(apiResponse.body()); This will print the result in byte format not in string
        //We need to use Jackson bind Lib in order to convert it into jSon format.
        System.out.println("--------print api plain text -------------");
        System.out.println(apiResponse.text());
        System.out.println("--------print api json response -------------");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(apiResponse.body());
        //readTree method returns JsonNode
        String jsonPrettyResponse = jsonResponse.toPrettyString();
        System.out.println(jsonPrettyResponse);
        System.out.println("--------print api url -------------");
        System.out.println(apiResponse.url());
        System.out.println("--------print api response headers -------------");
        Map<String, String> headersMap = apiResponse.headers();
        System.out.println(headersMap);
        Assert.assertEquals(headersMap.get("content-type"), "application/json; charset=utf-8");
        Assert.assertEquals(headersMap.get("x-download-options"), "noopen");

    }

    @Test
    public void getSpecificUserTest() throws IOException {
        APIResponse apiResponse = requestContext.get("https://gorest.co.in/public/v2/users",
                RequestOptions.create()
                        .setQueryParam("gender", "female")
                        .setQueryParam("status", "inactive"));
        int statusCode = apiResponse.status();
        System.out.println("API Response status code : " + statusCode);
        Assert.assertEquals(statusCode, 200);
        Assert.assertEquals(apiResponse.ok(), true);

        String statusText = apiResponse.statusText();
        System.out.println("API Response status text : " + statusText);
        Assert.assertEquals(statusText, "OK");

        System.out.println("--------print api plain text -------------");
        System.out.println(apiResponse.text());
        System.out.println("--------print api json response -------------");
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonResponse = objectMapper.readTree(apiResponse.body());
        String jsonPrettyResponse = jsonResponse.toPrettyString();
        System.out.println(jsonPrettyResponse);
    }
}
