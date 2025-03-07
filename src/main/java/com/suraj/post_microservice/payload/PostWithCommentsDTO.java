package com.suraj.post_microservice.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PostWithCommentsDTO {
    private UUID postID;
    private String title;
    private String content;
    private PagedResponse comments;
}
