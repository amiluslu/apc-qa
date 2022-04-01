package com.amilus.apc;

import com.amilus.apc.base.TestBase;
import com.amilus.apc.client.RestClient;
import com.amilus.apc.domain.dto.PostDto;
import com.amilus.apc.domain.resources.PostsResource;
import com.amilus.apc.domain.resources.UserResource;
import com.amilus.apc.helpers.BasicHelper;
import com.amilus.apc.helpers.PostHelper;
import com.amilus.apc.helpers.UserHelper;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import io.restassured.http.Method;
import lombok.SneakyThrows;
import org.junit.Assert;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static com.amilus.apc.reporting.ExtentTestManager.getTest;


@SpringBootTest
@ExtendWith(TestBase.class)
public class PostsTest
{
    @Autowired
    RestClient restClient;

    @Autowired
    UserResource userResource;

    @Autowired
    PostsResource postsResource;

    private UserHelper userHelper;
    private BasicHelper basicHelper;
    private PostHelper postHelper;

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"Delphine"})
    void testGivenUserPostsCount(String username)
    {
        // Now getting User ID of given user by username
        getTest().getModel().setDescription("Test Posts Count of given username: <b>"+username+"</b>");
        getTest().assignCategory("PostsTest");
        getTest().info(MarkupHelper.createLabel("Now getting User ID of given user by username", ExtentColor.INDIGO));
        basicHelper = new BasicHelper(restClient);
        basicHelper.callTheEndPoint(Method.GET.name(), "/users");
        basicHelper.verifyResponseCode(200);

        userHelper = new UserHelper(userResource);
        userHelper.generateUsersFromJson();
        userHelper.getUserIdByUsername(username);

        // Getting posts of user by User ID
        getTest().info(MarkupHelper.createLabel("Getting posts of user by User ID", ExtentColor.LIME));
        postHelper = new PostHelper(postsResource,userResource,restClient);
        postHelper.callPostsOfUserById(Method.GET.name(),"/posts","userId");
        postHelper.getPostsCollection();
        postHelper.verifyCountOfUserPosts(10);
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(ints = {583458})
    void testPostsWithWrongUserId(int userId)
    {
        getTest().getModel().setDescription("Test Posts with wrong userId: "+userId);
        getTest().assignCategory("PostsTest");
        basicHelper = new BasicHelper(restClient);
        basicHelper.callTheEndPoint(Method.GET.name(), "/posts?userId="+userId);
        basicHelper.verifyResponseCode(200);
        basicHelper.verifyResponseIsEmptyList();
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"583458"})
    void testPostsWithUserIdNegativeTest(String userId)
    {
        getTest().getModel().setDescription("Test Posts with wrong userId: "+userId);
        getTest().assignCategory("PostsTest-Negative Tests");
        basicHelper = new BasicHelper(restClient);
        basicHelper.callTheEndPoint(Method.GET.name(), "/posts?userId="+userId);
        basicHelper.verifyResponseCode(200);
        basicHelper.verifyResponseIsEmptyList();
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"Delphine"})
    void testCreatePosts(String username)
    {
        getTest().getModel().setDescription("Test creating a new Posts by username: <b>"+username+"</b>");
        getTest().assignCategory("PostsTest");
        getTest().info(MarkupHelper.createLabel("Now getting User ID of given user by username", ExtentColor.BLUE));
        basicHelper = new BasicHelper(restClient);
        basicHelper.callTheEndPoint(Method.GET.name(), "/users");
        basicHelper.verifyResponseCode(200);

        userHelper = new UserHelper(userResource);
        userHelper.generateUsersFromJson();
        int userId = userHelper.getUserIdByUsername(username);

        getTest().info(MarkupHelper.createLabel("Now creating a post by user..", ExtentColor.BROWN));
        PostDto postDto = new PostDto();
        postDto.setBody("Lorem Body");
        postDto.setTitle("Ipsum Title");
        postDto.setUserId(userId);

        postHelper = new PostHelper(postsResource,userResource,restClient);
        postHelper.saveAndVerifyNewPost(postDto);

        basicHelper.callTheEndPoint(Method.GET.name(), "/posts");
        postHelper = new PostHelper(postsResource,userResource,restClient);
        postHelper.getPostsCollection();
        int lastPostCount = postHelper.getTotalPostCount();
        getTest().info(MarkupHelper.createLabel("After creating new posts, Total Number of Posts is: "+lastPostCount, ExtentColor.CYAN));
        Assert.assertEquals("Validation Of Posts Creation failed !!",101,lastPostCount);
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"Delphine"})
    void testUpdatePost(String username)
    {
        getTest().getModel().setDescription("Test updating first Post of username: <b>"+username+"</b>");
        getTest().assignCategory("PostsTest");
        getTest().info(MarkupHelper.createLabel("Now getting User ID of given user by username", ExtentColor.BLUE));
        basicHelper = new BasicHelper(restClient);
        basicHelper.callTheEndPoint(Method.GET.name(), "/users");
        basicHelper.verifyResponseCode(200);

        userHelper = new UserHelper(userResource);
        userHelper.generateUsersFromJson();
        int userId = userHelper.getUserIdByUsername(username);

        getTest().info(MarkupHelper.createLabel("Now getting a first post by user..", ExtentColor.BROWN));
        postHelper = new PostHelper(postsResource,userResource,restClient);
        postHelper.callPostsOfUserById(Method.GET.name(),"/posts","userId");
        postHelper.getPostsCollection();
        Optional<PostDto> postDto = postHelper.getFirstPostOfUserById(userId);
        if(postDto.isPresent()){
            postDto.get().setBody("Updated Body");
            postDto.get().setTitle("Updated Title");
            postDto = postHelper.updateAndVerifyPost(postDto);
            getTest().info("Update Post Info: "+postDto);
        }
    }
}
