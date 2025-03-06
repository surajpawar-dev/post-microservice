package com.suraj.post_microservice.service;

import com.suraj.post_microservice.entity.Post;
import com.suraj.post_microservice.exception.ResourceNotFound;
import com.suraj.post_microservice.payload.CommentDTO;
import com.suraj.post_microservice.payload.PostDTO;
import com.suraj.post_microservice.payload.PostWithCommentsDTO;
import com.suraj.post_microservice.repository.PostRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class PostService {

    @Value("${comment.service.url}")
    private String commentServiceUrl; // Load from properties/yml


    private final PostRepository postRepo;
    private final RestTemplate restTemplate;
    private final ModelMapper mapper;


    public PostService(PostRepository postRepo, RestTemplate restTemplate, ModelMapper mapper) {
        this.postRepo = postRepo;
        this.restTemplate = restTemplate;
        this.mapper = mapper;
    }

    public PostDTO createPost(PostDTO postDTO) {
        Post post = mapper.map(postDTO, Post.class);
        return mapper.map(postRepo.save(post), PostDTO.class);
    }

    public Post getPostById(UUID id) {
        return postRepo.findById(id).orElseThrow(
                () -> new ResourceNotFound("Post not found with id: " + id)
        );
    }

    public PostDTO updatePost(UUID id, PostDTO updatedPost) {
        Post existingPost = postRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFound("Post not found with id: " + id));
        existingPost.setTitle(updatedPost.title());
        existingPost.setContent(updatedPost.content());
        return mapper.map(postRepo.save(existingPost), PostDTO.class);
    }

    public String deletePost(UUID id) {
        if (!postRepo.existsById(id)) {
            throw new ResourceNotFound("Post not found with id: " + id);
        }
        postRepo.deleteById(id);
        return "Post deleted with id: " + id;
    }

    public List<CommentDTO> getAllComments(UUID postID) {
        String url = commentServiceUrl + "/posts/" + postID + "/comments";
        CommentDTO[] comments = restTemplate.getForObject(url, CommentDTO[].class);
        return comments != null ? Arrays.asList(comments) : List.of();
    }

    public PostWithCommentsDTO getPostWithComments(UUID postID) {
        // 🔹 1. Get Post from DB
        Post post = postRepo.findById(postID)
                .orElseThrow(() -> new ResourceNotFound("Post not found with id: " + postID));

        // 🔹 2. Get Comments from Comment Microservice
        String url = commentServiceUrl + "/posts/" + postID + "/comments";
        CommentDTO[] comments = restTemplate.getForObject(url, CommentDTO[].class);

        // 🔹 3. Wrap into DTO and Return
        return new PostWithCommentsDTO(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                comments != null ? Arrays.asList(comments) : List.of()
        );
    }
}
