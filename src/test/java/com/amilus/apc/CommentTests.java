package com.amilus.apc;

import com.amilus.apc.base.TestBase;
import com.amilus.apc.client.RestClient;
import com.amilus.apc.domain.dto.CommentDto;
import com.amilus.apc.domain.resources.CommentResource;
import com.amilus.apc.domain.resources.PostsResource;
import com.amilus.apc.domain.resources.UserResource;
import com.amilus.apc.helpers.BasicHelper;
import com.amilus.apc.helpers.CommentHelper;
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
public class CommentTests
{
    @Autowired
    RestClient restClient;

    @Autowired
    UserResource userResource;

    @Autowired
    PostsResource postsResource;

    @Autowired
    CommentResource commentResource;

    private UserHelper userHelper;
    private BasicHelper basicHelper;
    private PostHelper postHelper;
    private CommentHelper commentHelper;

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"Delphine"})
    void testEachMailAddressOfComments(String username)
    {
        // Now getting User ID of given user by username
        getTest().getModel().setDescription("Test each Email Address of Comments which posts written by username: <b>"+username+"</b>");
        getTest().assignCategory("CommentTest");
        getTest().info(MarkupHelper.createLabel("Now getting User ID of given user by username", ExtentColor.BLUE));
        basicHelper = new BasicHelper(restClient);
        basicHelper.callTheEndPoint(Method.GET.name(),"/users" );
        basicHelper.verifyResponseCode(200);

        userHelper = new UserHelper(userResource);
        userHelper.generateUsersFromJson();
        userHelper.getUserIdByUsername(username);

        // Getting posts of user by User ID
        getTest().info(MarkupHelper.createLabel("Getting posts of user by userId", ExtentColor.BROWN));
        postHelper = new PostHelper(postsResource,userResource,restClient);
        postHelper.callPostsOfUserById(Method.GET.name(),"/posts","userId");
        postHelper.getPostsCollection();

        //Getting comments of posts
        getTest().info(MarkupHelper.createLabel("Getting comments of posts", ExtentColor.CYAN));
        commentHelper = new CommentHelper(commentResource,postsResource,restClient);
        commentHelper.getCommentsOfGivenPost(Method.GET.name(), "/comments", "postId");
        commentHelper.verifyEmailAddressFormatInEachComment();
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"Delphine"})
    void testCreateNewComment(String username)
    {
        getTest().getModel().setDescription("Test creating a new Comment of first post by username: <b>"+username+"</b>");
        getTest().assignCategory("CommentTest");
        getTest().info(MarkupHelper.createLabel("Now getting User ID of given user by username", ExtentColor.BLUE));

        basicHelper = new BasicHelper(restClient);
        basicHelper.callTheEndPoint(Method.GET.name(), "/users");
        basicHelper.verifyResponseCode(200);

        userHelper = new UserHelper(userResource);
        userHelper.generateUsersFromJson();
        userHelper.getUserIdByUsername(username);

        getTest().info(MarkupHelper.createLabel("Now getting first post of user..", ExtentColor.BROWN));
        postHelper = new PostHelper(postsResource,userResource,restClient);
        postHelper.callPostsOfUserById(Method.GET.name(),"/posts","userId");
        postHelper.getPostsCollection();
        commentHelper = new CommentHelper(commentResource,postsResource,restClient);

        getTest().info(MarkupHelper.createLabel("Creating Comment data object.. ", ExtentColor.BROWN));
        CommentDto commentDto = new CommentDto();
        commentDto.setEmail("amilus@amilus.com");
        commentDto.setName("Comment Name");
        commentDto.setBody("Comment Body");
        commentDto.setPostId(commentHelper.getFirstPostIdFromCollection());

        commentHelper.saveAndVerifyNewComment(commentDto);

        basicHelper.callTheEndPoint(Method.GET.name(), "/comments");
        commentHelper = new CommentHelper(commentResource,postsResource,restClient);
        int lastCommentCount = commentHelper.getTotalCommentCount();
        getTest().info(MarkupHelper.createLabel("After creating new comment, Total Number of Comments is: "+lastCommentCount, ExtentColor.CYAN));
        Assert.assertEquals("Validation Of Comment Creation failed !!",501,lastCommentCount);
    }

    @SneakyThrows
    @ParameterizedTest
    @ValueSource(strings = {"Delphine"})
    void testUpdateComment(String username)
    {
        getTest().getModel().setDescription("Test Updating Comment of first post by username <b>"+username+"</b>");
        getTest().assignCategory("CommentTest");
        getTest().info(MarkupHelper.createLabel("Now getting User ID of given user by username", ExtentColor.BLUE));
        basicHelper = new BasicHelper(restClient);
        basicHelper.callTheEndPoint(Method.GET.name(),"/users" );
        basicHelper.verifyResponseCode(200);

        userHelper = new UserHelper(userResource);
        userHelper.generateUsersFromJson();
        userHelper.getUserIdByUsername(username);

        // Getting posts of user by User ID
        getTest().info(MarkupHelper.createLabel("Getting posts of user by userId", ExtentColor.BROWN));
        postHelper = new PostHelper(postsResource,userResource,restClient);
        postHelper.callPostsOfUserById(Method.GET.name(),"/posts","userId");
        postHelper.getPostsCollection();

        //Getting comments of posts
        getTest().info(MarkupHelper.createLabel("Now getting first post of user", ExtentColor.CYAN));
        commentHelper = new CommentHelper(commentResource,postsResource,restClient);
        commentHelper.getCommentsOfGivenPost(Method.GET.name(), "/comments", "postId");
        Optional<CommentDto> commentDto = commentHelper.getFirstCommentOfPost();
        if(commentDto.isPresent()){
            commentDto.get().setBody("Update Comment Body");
            commentDto.get().setName("Update Comment Name");
            commentDto.get().setEmail("updateemail@Comment.com");
            commentDto = commentHelper.updateAndVerifyComment(commentDto);
            getTest().info("Updated Comment Info: "+commentDto);
        }
    }
}
