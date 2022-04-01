package com.amilus.apc.domain.resources;

import com.amilus.apc.client.RestClient;
import com.amilus.apc.domain.dto.PostDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.restassured.http.Method;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.amilus.apc.reporting.ExtentTestManager.getTest;

@Component
public class PostsResource extends BaseResource
{
    private List<PostDto> posts;
    private List<Integer> postIds;
    private final RestClient restClient;

    @Autowired
    public PostsResource(RestClient restClient)
    {
        this.restClient = restClient;
    }

    /**
     * This method is used to parse JSON string receiving as an API response
     * and map it on the PostDto.class as List and saves it in the posts variable
     */
    public void parseJsonToPosts() throws JsonProcessingException
    {
        posts = mapFromJsonList(restClient.getResponseBody().asString(), PostDto.class);
    }

    public int getPostCount() {
        return postIds.size();
    }

    /**
     * This method is used to fetch post ids from the list of post
     */
    public void collectPostIds() {
        postIds = posts.stream().map(PostDto::getId).collect(Collectors.toList());
    }

    public Optional<PostDto> getFirstPostOfUserById(int userId) {
        Optional<PostDto> postDto =  posts.stream().filter(post -> post.getUserId().equals(userId)).findFirst();
        getTest().info("Posts: "+postDto);
        return  postDto;
    }

    public List<Integer> getPostIdList() {
        return postIds;
    }

    public PostDto createPosts(PostDto postDto) throws JsonProcessingException
    {
        restClient.initRestAPI();
        restClient.setBody(postDto);
        restClient.addHeader("Content-Type","application/json");
        restClient.sendHttpRequest(Method.POST,"/posts");
        return mapFromJson(restClient.getResponseBody().asString(),PostDto.class);
    }

    public PostDto updatePostObject(PostDto postDto) throws JsonProcessingException
    {
        restClient.initRestAPI();
        restClient.setBody(postDto);
        restClient.addHeader("Content-Type","application/json");
        restClient.sendHttpRequest(Method.PUT,"/posts/"+postDto.getId());
        return mapFromJson(restClient.getResponseBody().asString(),PostDto.class);
    }
}
