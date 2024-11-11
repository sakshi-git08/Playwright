package com.qa.api.tests;

import com.microsoft.playwright.*;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

public class ApiDisposeTest {
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
    public void disposeResponseTest(){
        APIResponse apiResponse = requestContext.get("https://gorest.co.in/public/v2/users");
        //Request-1:
        int statusCode = apiResponse.status();
        System.out.println("API Response status code : " + statusCode);
        Assert.assertEquals(statusCode, 200);
        Assert.assertEquals(apiResponse.ok(), true);

        String statusText = apiResponse.statusText();
        System.out.println("API Response status text : " + statusText);
        Assert.assertEquals(statusText, "OK");

        System.out.println("--------print api plain text -------------");
        System.out.println(apiResponse.text());

        apiResponse.dispose();//will dispose only response body but status code, url, status text will remain same
        try{
            System.out.println(apiResponse.text());
        }catch (PlaywrightException e){
            System.out.println("api response body is disposed");
        }
        System.out.println("--------print api plain text after dispose -------------");
//        System.out.println(apiResponse.text()); //Response has been disposed
        int statusCode1 = apiResponse.status();
        System.out.println("API Response status code : " + statusCode1);
        Assert.assertEquals(statusCode1, 200);
        String statusText1 = apiResponse.statusText();
        System.out.println("API Response status text : " + statusText1);
        Assert.assertEquals(statusText1, "OK");
        System.out.println("--------print api url -------------");
        System.out.println(apiResponse.url());

        //Request-2:
        APIResponse apiResponse1 = requestContext.get("https://reqres.in/api/users/2");
        System.out.println("get response body for 2nd request: " + apiResponse1.text());
        System.out.println("status code: " + apiResponse1.status());

        //requestContext dispose
        requestContext.dispose();
//        System.out.println("get response body for 1st request: " + apiResponse.text());
        System.out.println("get response body for 2nd request: " + apiResponse1.text());


    }

    @AfterTest
    public void tearDown(){
        playwright.close();
    }
}
