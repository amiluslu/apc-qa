package com.amilus.apc.helpers;

import com.amilus.apc.client.RestClient;
import com.amilus.apc.domain.dto.PostDto;
import com.amilus.apc.domain.resources.PostsResource;
import com.amilus.apc.domain.resources.UserResource;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.http.Method;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.amilus.apc.reporting.ExtentTestManager.getTest;

public class PostHelper
{
    private PostsResource postsResource;
    private UserResource userResource;
    private RestClient restClient;

    @Autowired
    public PostHelper(PostsResource postsResource, UserResource userResource, RestClient restClient) {
        this.postsResource = postsResource;
        this.userResource = userResource;
        this.restClient = restClient;
    }

    // verify and validate of user's posts count
    public void verifyCountOfUserPosts(int count) {
        getTest().info("Expected Posts Count: "+count +" Actual Posts Count: "+ postsResource.getPostCount());
        Assert.assertEquals("Count of user's post is not " + count, count, postsResource.getPostCount());
    }

    public int getTotalPostCount(){
        return postsResource.getPostCount();
    }
    //It collects all posts of Id's in a list
    public void getPostsCollection() throws JsonProcessingException
    {
        getTest().info("Generating Posts Data Object from JSON response..");
        postsResource.parseJsonToPosts();
        postsResource.collectPostIds();
    }

    // It takes userId parameter and gets all posts
    public void callPostsOfUserById(String method, String endPoint, String parameterValue) {
        restClient.initRestAPI();
        restClient.setQueryParam(parameterValue, String.valueOf(userResource.getUserId()));
        restClient.sendHttpRequest(Method.valueOf(method), endPoint);
    }

    public PostDto saveAndVerifyNewPost(PostDto postDto) throws JsonProcessingException
    {
        getTest().info("Saving new posts..");
        PostDto newPost = postsResource.createPosts(postDto);
        getTest().info("New posts id: <b>"+newPost.getId()+"</b> New Posts User Id: <b>"+newPost.getUserId()+"</b>");
        Assert.assertNotNull("Error occured when adding new posts !!",newPost.getId());
        return newPost;
    }

    public Optional<PostDto> getFirstPostOfUserById(int userId) {
        getTest().info("Fetching First Post of User By Id: "+userId);
        return postsResource.getFirstPostOfUserById(userId);
    }

    public Optional<PostDto> updateAndVerifyPost(Optional<PostDto> postDto) throws JsonProcessingException
    {
        getTest().info(MarkupHelper.createLabel("Updating Posts Title & Body..", ExtentColor.ORANGE));
        postDto = Optional.ofNullable(postsResource.updatePostObject(postDto.get()));
        Assert.assertNotNull(postDto.get().getId());
        return postDto;
    }

}
