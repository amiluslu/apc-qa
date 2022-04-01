package com.amilus.apc.helpers;

import com.amilus.apc.domain.dto.UserDto;
import com.amilus.apc.domain.resources.UserResource;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static com.amilus.apc.reporting.ExtentTestManager.getTest;

public class UserHelper
{
    private UserResource userResource;

    @Autowired
    public UserHelper(UserResource userResource) {
        this.userResource = userResource;
    }

    // verify and validate username
    public void verifyUserByUsername(String username) {
        getTest().info("Searching Expected Username: <b>"+username+"</b> Fetching Result: <b>"+userResource.isUserFound(username)+"</b>");
        Assert.assertTrue("Validation of existence failed!! Username: <b>"+ username + "</b> not found in system..", userResource.isUserFound(username));
    }

    public int getUserIdByUsername(String username) {
        getTest().info("Fetching User Id by Username: "+username);
        return userResource.fetchUserIdByUsername(username);
    }

    public List<UserDto> generateUsersFromJson() throws JsonProcessingException
    {
        getTest().info("Generating User Data Object from JSON response..");
        return userResource.parseJsonToUsers();
    }

    public UserDto saveAndVerifyNewUser(UserDto userDto) throws JsonProcessingException
    {
        getTest().info("Saving new user..");
        UserDto newUser = userResource.postUserObject(userDto);
        getTest().info("New user id: <b>"+newUser.getId()+"</b> New Username: <b>"+newUser.getUsername()+"</b>");
        Assert.assertNotNull("Error occured when adding new user !!",newUser.getId());
        return newUser;
    }

    public Optional<UserDto> getUserInfoByUsername(String username) {
        getTest().info("Fetching User Id by Username: "+username);
        return userResource.fetchUserByUsername(username);
    }

    public Optional<UserDto> updateAndVerifyUser(Optional<UserDto> userDto) throws JsonProcessingException
    {
        getTest().info(MarkupHelper.createLabel("Updating User Email Information", ExtentColor.ORANGE));
        userDto = Optional.ofNullable(userResource.updateUserObject(userDto.get()));
        Assert.assertNotNull(userDto.get().getId());
        return userDto;
    }
}
