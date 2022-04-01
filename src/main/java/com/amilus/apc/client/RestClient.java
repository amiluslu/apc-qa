package com.amilus.apc.client;

import com.amilus.apc.reporting.ExtentTestManager;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import static com.amilus.apc.reporting.ExtentTestManager.getTest;

@Component
public class RestClient
{
    @Value("${api.url}")
    private String apiUrl;

    private RequestSpecification requestSpecification;
    private ResponseBody responseBody;
    private Integer statusCode;
    private Response response;

    public void initRestAPI() {
        RestAssured.baseURI = apiUrl;
        requestSpecification = RestAssured.given();
        ExtentTestManager.getTest().info("Api URL: "+apiUrl);
    }

    public void sendHttpRequest(Method method, String endpoint) {
        getTest().info("Sending Http Request.. Method: <b>"+method+" </b> EndPoint: <b>"+endpoint+"</b>");
        response = requestSpecification.request(method, endpoint);
        setResponseBody(response.getBody());
        setStatusCode(response.getStatusCode());
        getTest().info("Response Code: "+response.getStatusCode());
    }

    public void setBody(Object body) {
        requestSpecification.body(body);
    }

    public void setQueryParam(String key, Object value) {
        requestSpecification.queryParam(key, value);
        getTest().info("Adding Key: "+key+" and Value: "+value+" to request specification..");
    }

    public void addHeader(String header, String value) {
        requestSpecification.header(header, value);
    }

    public ResponseBody getResponseBody() {
        return responseBody;
    }

    private void setResponseBody(ResponseBody responseBody) {
        this.responseBody = responseBody;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    private void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }

}
