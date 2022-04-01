package com.amilus.apc.domain.dto;

import lombok.Data;

@Data
public class CommentDto
{
    private int postId;
    private int id;
    private String name;
    private String email;
    private String body;
}
