package com.amilus.apc.domain.resources;

import com.amilus.apc.client.RestClient;
import com.amilus.apc.domain.dto.UserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

import static com.amilus.apc.reporting.ExtentTestManager.getTest;

@Component
public class UserResource extends BaseResource
{
    private List<UserDto> users;
    private Integer userId;
    private final RestClient restClient;

    @Autowired
    public UserResource(RestClient restClient)
    {
        this.restClient = restClient;
    }

    /**
     * This method is used to parse JSON string receiving as an API response
     * and map it on the UserDto.class as List and saves it in the users variable
     * @return
     */
    public List<UserDto> parseJsonToUsers() throws JsonProcessingException
    {
        users = mapFromJsonList(restClient.getResponseBody().asString(), UserDto.class);
        return users;
    }

    /**
     * This method checks if the user is found in the list of users
     */
    public boolean isUserFound(String userName) {
        return users.stream().anyMatch(user -> userName.equalsIgnoreCase(user.getUsername()));
    }

    /**
     * This method fetch the user id and saves it in the userId variable
     */
    public int fetchUserIdByUsername(String userName) {
        userId = users.stream().filter(user -> user.getUsername().equalsIgnoreCase(userName)).
                findFirst().map(UserDto::getId).get();
        getTest().info("User Id: "+userId);
        return userId;
    }

    public Optional<UserDto> fetchUserByUsername(String userName) {
        Optional<UserDto> userDto = users.stream().filter(user -> user.getUsername().equalsIgnoreCase(userName)).
                findFirst();
        getTest().info("User: "+userDto);
        return userDto;
    }

    public Integer getUserId() {
        getTest().info("User Id: "+userId);
        return userId;
    }

    public UserDto postUserObject(UserDto userDto) throws JsonProcessingException
    {
        restClient.initRestAPI();
        restClient.setBody(userDto);
        restClient.addHeader("Content-Type","application/json");
        restClient.sendHttpRequest(Method.POST,"/users");
        return mapFromJson(restClient.getResponseBody().asString(),UserDto.class);
    }

    public UserDto updateUserObject(UserDto userDto) throws JsonProcessingException
    {
        restClient.initRestAPI();
        restClient.setBody(userDto);
        restClient.addHeader("Content-Type","application/json");
        restClient.sendHttpRequest(Method.PUT,"/users/"+userDto.getId());
        return mapFromJson(restClient.getResponseBody().asString(),UserDto.class);
    }
}
