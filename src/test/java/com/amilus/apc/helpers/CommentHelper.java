package com.amilus.apc.helpers;

import com.amilus.apc.client.RestClient;
import com.amilus.apc.domain.dto.CommentDto;
import com.amilus.apc.domain.resources.CommentResource;
import com.amilus.apc.domain.resources.PostsResource;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.http.Method;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

import static com.amilus.apc.reporting.ExtentTestManager.getTest;

public class CommentHelper
{
    private CommentResource commentResource;
    private PostsResource postsResource;
    private RestClient restClient;

    @Autowired
    public CommentHelper(CommentResource commentResource, PostsResource postsResource, RestClient restClient){
        this.commentResource = commentResource;
        this.postsResource = postsResource;
        this.restClient = restClient;
    }

    public void getCommentsOfGivenPost(String method, String endPoint, String paramValue) throws JsonProcessingException
    {
        for (Integer id : postsResource.getPostIdList()) {
            restClient.initRestAPI();
            restClient.setQueryParam(paramValue, id);
            restClient.sendHttpRequest(Method.valueOf(method), endPoint);
            commentResource.parseJsonToComments();
        }
    }

    public int getTotalCommentCount() throws JsonProcessingException
    {
        commentResource.parseJsonToComments();
        return commentResource.getCommentCount();
    }

    public void verifyEmailAddressFormatInEachComment() {
        getTest().info("Verifying Email Address Format in each comment");
        Assert.assertTrue("Email Addresses are not in proper format: " + commentResource.getInvalidEmailAddressList(),
                commentResource.getInvalidEmailAddressList().isEmpty());
    }

    public CommentDto saveAndVerifyNewComment(CommentDto commentDto) throws JsonProcessingException
    {
        getTest().info("Saving new comment..");
        CommentDto newComment = commentResource.postComment(commentDto);
        getTest().info("New Comment id: <b>"+newComment.getId()+"</b> Post Id: <b>"+newComment.getPostId()+"</b>");
        Assert.assertNotNull("Error occured when adding new comment !!",newComment.getId());
        return newComment;
    }

    public int getFirstPostIdFromCollection(){
        return postsResource.getPostIdList().get(0);
    }

    public Optional<CommentDto> getFirstCommentOfPost() throws JsonProcessingException
    {
        getTest().info(MarkupHelper.createLabel("Updating Below Comment Informations...", ExtentColor.ORANGE));
        return commentResource.getFirstCommentOfPostById(postsResource.getPostIdList().get(0));
    }

    public Optional<CommentDto> updateAndVerifyComment(Optional<CommentDto> commentDto) throws JsonProcessingException
    {
        commentDto = Optional.ofNullable(commentResource.updateCommentObject(commentDto.get()));
        Assert.assertNotNull(commentDto.get().getId());
        return commentDto;
    }
}
