package com.amilus.apc.helpers;

import com.amilus.apc.client.RestClient;
import com.amilus.apc.utils.ValidationUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.http.Method;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import static com.amilus.apc.reporting.ExtentTestManager.getTest;


public class BasicHelper
{
    private RestClient restClient;

    @Autowired
    public BasicHelper(RestClient restClient) {
        this.restClient = restClient;
    }

    public void callTheEndPoint(String method, String endpoint) {
        restClient.initRestAPI();
        restClient.sendHttpRequest(Method.valueOf(method), endpoint);
    }

    public void verifyResponseCode(Integer statusCode) {
        getTest().info("Expected Status Code: "+statusCode +" Actual Status Code: "+ restClient.getStatusCode());
        Assert.assertEquals("Response code is not " + statusCode,
                statusCode, restClient.getStatusCode());
    }

    public void verifyResponseIsEmptyList() throws JsonProcessingException
    {
        getTest().info("Expected Response is Empty -- Actual Response: "+restClient.getResponseBody().asString());
        Assert.assertTrue("Response is not empty",
                ValidationUtils.ifBodyEmpty(restClient.getResponseBody().asString()));
    }
}
