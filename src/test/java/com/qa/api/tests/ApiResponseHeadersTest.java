package com.qa.api.tests;

import com.microsoft.playwright.APIRequest;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.HttpHeader;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

public class ApiResponseHeadersTest {
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
    public void tearDown(){
        playwright.close();
    }

    @Test
    public void getHeadersTest(){
        APIResponse apiResponse = requestContext.get("https://gorest.co.in/public/v2/users");
        int statusCode = apiResponse.status();
        System.out.println("API Response status code : " + statusCode);
        Assert.assertEquals(statusCode, 200);

        Map<String, String> headersMap = apiResponse.headers();
        headersMap.forEach((k,v) -> System.out.println(k + " : " + v));
        System.out.println("total response headers: " + headersMap.size());
        Assert.assertEquals(headersMap.get("server"), "cloudflare");

        //using list:
        System.out.println("-----Using List-----");
       List<HttpHeader> headersList = apiResponse.headersArray();
       for(HttpHeader e : headersList){
           System.out.println(e.name + " " + e.value);
       }
    }
}
