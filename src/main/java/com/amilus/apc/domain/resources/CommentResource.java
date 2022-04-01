package com.amilus.apc.domain.resources;

import com.amilus.apc.client.RestClient;
import com.amilus.apc.domain.dto.CommentDto;
import com.amilus.apc.utils.ValidationUtils;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.http.Method;
import org.junit.Assert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.amilus.apc.reporting.ExtentTestManager.getTest;


@Component
public class CommentResource extends BaseResource
{
    private List<CommentDto> comments = new ArrayList<>();
    private final RestClient restClient;

    @Autowired
    public CommentResource(RestClient restClient)
    {
        this.restClient = restClient;
    }

    /**
     * This method is used to parse JSON string receiving as an API response
     * and map it on the CommentDto.class as List and append it into the comments
     */

    public void parseJsonToComments() throws JsonProcessingException
    {
        getTest().info("Generating Comments Data Object from JSON response..");
        comments.addAll(mapFromJsonList(restClient.getResponseBody().asString(), CommentDto.class));
    }

    public int getCommentCount() {
        return comments.size();
    }

    /**
     * This method is used for fetching all the invalid email addresses form comments
     */
    public List<String> getInvalidEmailAddressList() {
        return comments.stream().map(CommentDto::getEmail).filter(email -> !ValidationUtils.checkEmailFormat(email)).
                collect(Collectors.toList());
    }

    public Optional<CommentDto> getFirstCommentOfPostById(int postId){
        Optional<CommentDto> commentDto =  comments.stream().filter(comment -> comment.getPostId() == (postId)).findFirst();
        getTest().info("Comment: "+commentDto);
        return commentDto;
    }

    public CommentDto postComment(CommentDto commentDto) throws JsonProcessingException
    {
        restClient.initRestAPI();
        restClient.setBody(commentDto);
        restClient.addHeader("Content-Type","application/json");
        restClient.sendHttpRequest(Method.POST,"/comments");
        return mapFromJson(restClient.getResponseBody().asString(),CommentDto.class);
    }

    public CommentDto updateCommentObject(CommentDto commentDto) throws JsonProcessingException
    {
        restClient.initRestAPI();
        restClient.setBody(commentDto);
        restClient.addHeader("Content-Type","application/json");
        restClient.sendHttpRequest(Method.PUT,"/comments/"+commentDto.getId());
        Assert.assertEquals(200,restClient.getStatusCode().intValue());
        return mapFromJson(restClient.getResponseBody().asString(),CommentDto.class);
    }
}
