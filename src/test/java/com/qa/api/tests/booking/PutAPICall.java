package com.qa.api.tests.booking;

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

public class PutAPICall {
    Playwright playwright;
    APIRequest request;
    APIRequestContext requestContext;
    private static String tokenId = null;

    @BeforeTest
    public void setup() throws IOException {
        playwright = Playwright.create();
        request = playwright.request();
        requestContext = request.newContext();

        String reqTokenJsonBody = "{\n" +
                "    \"username\" : \"admin\",\n" +
                "    \"password\" : \"password123\"\n" +
                "}";
        APIResponse response = requestContext.post("https://restful-booker.herokuapp.com/auth",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setData(reqTokenJsonBody));
        ObjectMapper mapper = new ObjectMapper();
        JsonNode jsonResponse = mapper.readTree(response.body());
        System.out.println(jsonResponse.toPrettyString());

        //capture id from jsonResponse
        tokenId = jsonResponse.get("token").asText();
        System.out.println("token id is : " + tokenId);
    }

    @AfterTest
    public void tearDown() {
        playwright.close();
    }

    @Test
    public void updateBooking() {

        String bookingJson = "{\n" +
                "    \"firstname\" : \"Sakshi\",\n" +
                "    \"lastname\" : \"Aggarwal\",\n" +
                "    \"totalprice\" : 789,\n" +
                "    \"depositpaid\" : true,\n" +
                "    \"bookingdates\" : {\n" +
                "        \"checkin\" : \"2018-01-01\",\n" +
                "        \"checkout\" : \"2019-01-01\"\n" +
                "    },\n" +
                "    \"additionalneeds\" : \"Breakfast\"\n" +
                "}";
        APIResponse apiPutResponse = requestContext.put("https://restful-booker.herokuapp.com/booking/1",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Cookie", "token=" + tokenId)
                        .setData(bookingJson));
        System.out.println(apiPutResponse.url());
        System.out.println(apiPutResponse.status() + " : " + apiPutResponse.statusText());
//        Assert.assertEquals(apiPutResponse.status(), 200);
        System.out.println(apiPutResponse.text());

        //delete:
        APIResponse apiDeleteResponse = requestContext.delete("https://restful-booker.herokuapp.com/booking/1834", RequestOptions.create()
                .setHeader("Content-Type", "application/json")
                .setHeader("Cookie", "token=" + tokenId));
        Assert.assertEquals(apiDeleteResponse.status(), 201);
        Assert.assertEquals(apiDeleteResponse.statusText(), "Created");
        System.out.println(apiDeleteResponse.text());



    }
}
