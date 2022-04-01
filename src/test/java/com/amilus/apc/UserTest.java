package com.amilus.apc;

import com.amilus.apc.base.TestBase;
import com.amilus.apc.client.RestClient;
import com.amilus.apc.domain.dto.AddressDto;
import com.amilus.apc.domain.dto.CompanyDto;
import com.amilus.apc.domain.dto.GeoDto;
import com.amilus.apc.domain.dto.UserDto;
import com.amilus.apc.domain.resources.UserResource;
import com.amilus.apc.helpers.BasicHelper;
import com.amilus.apc.helpers.UserHelper;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import io.restassured.http.Method;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Description;

import java.util.List;
import java.util.Optional;

import static com.amilus.apc.reporting.ExtentTestManager.getTest;

@SpringBootTest
@ExtendWith(TestBase.class)
public class UserTest
{
    @Autowired
    RestClient restClient;

    @Autowired
    UserResource userResource;

    private UserHelper userHelper;
    private BasicHelper basicHelper;

    @SneakyThrows
    @ParameterizedTest
    @Description("Method Type: GET EndPoint: /users Data-Driven technique")
    @ValueSource(strings = {"Delphine", "Amil",""})
    void testUserNameExistence(String username)
    {
        getTest().getModel().setDescription("Search Username: <b>"+username+"</b> in <b>/users</b> existence in endpoint by data-driven..");
        getTest().assignCategory("UsersTest");
        basicHelper = new BasicHelper(restClient);
        basicHelper.callTheEndPoint(Method.GET.name(), "/users");
        basicHelper.verifyResponseCode(200);
        userHelper = new UserHelper(userResource);
        userHelper.generateUsersFromJson();
        userHelper.verifyUserByUsername(username);
    }

    @SneakyThrows
    @Test
    @Description("Method Type: Post EndPoint: /users")
    void testCreateNewUser()
    {
        getTest().getModel().setDescription("Test creating a new user..");
        getTest().assignCategory("UsersTest");
        getTest().info(MarkupHelper.createLabel("Now creating new user data object..", ExtentColor.BLUE));
        GeoDto geoDto = new GeoDto();
        geoDto.setLat("58.585858");
        geoDto.setLng("34.343434");

        CompanyDto companyDto = new CompanyDto();
        companyDto.setBs("technology and software");
        companyDto.setCatchPhrase("multilingual tech company");
        companyDto.setName("Mobiquity");

        AddressDto addressDto = new AddressDto();
        addressDto.setCity("Istanbul");
        addressDto.setStreet("Istanbul Street");
        addressDto.setSuite("Amil Suite");
        addressDto.setZipcode("34058");
        addressDto.setGeo(geoDto);

        UserDto userDto = new UserDto();
        userDto.setAddress(addressDto);
        userDto.setCompany(companyDto);
        userDto.setName("Amil Uslu");
        userDto.setUsername("amilus");
        userDto.setEmail("amilus@amilus.com");
        userDto.setPhone("+901234567890");
        userDto.setWebsite("http://amiluslu.com");

        userHelper = new UserHelper(userResource);
        userHelper.saveAndVerifyNewUser(userDto);

        basicHelper = new BasicHelper(restClient);
        basicHelper.callTheEndPoint(Method.GET.name(), "/users");
        basicHelper.verifyResponseCode(200);
        userHelper = new UserHelper(userResource);
        List<UserDto> lastUserList = userHelper.generateUsersFromJson();

        getTest().info(MarkupHelper.createLabel("After creating new user, Total Number of Users is: "+lastUserList.size(), ExtentColor.CYAN));
        Assert.assertEquals("Validation Of User Creation failed !!",11,lastUserList.size());
    }

    @SneakyThrows
    @ParameterizedTest
    @Description("Method Type: GET EndPoint: /users Data-Driven technique")
    @ValueSource(strings = {"Delphine"})
    void testUpdateUser(String username)
    {
        getTest().getModel().setDescription("Updating user info by username: <b>"+username+"</b>");
        getTest().assignCategory("UsersTest");
        getTest().info(MarkupHelper.createLabel("Now getting User ID of given user by username", ExtentColor.BLUE));
        basicHelper = new BasicHelper(restClient);
        basicHelper.callTheEndPoint(Method.GET.name(), "/users");
        basicHelper.verifyResponseCode(200);
        userHelper = new UserHelper(userResource);
        userHelper.generateUsersFromJson();
        Optional<UserDto> userDto = userHelper.getUserInfoByUsername(username);
        if(userDto.isPresent()){
            userDto.get().setEmail("asdfg@asfd.com");
            userDto = userHelper.updateAndVerifyUser(userDto);
            getTest().info("Update User Info: "+userDto);
        }
    }
}
