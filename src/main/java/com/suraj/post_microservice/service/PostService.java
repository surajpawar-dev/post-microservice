package com.suraj.post_microservice.service;

import com.suraj.post_microservice.entity.Post;
import com.suraj.post_microservice.exception.ResourceNotFoundException;
import com.suraj.post_microservice.payload.CommentDTO;
import com.suraj.post_microservice.payload.PagedResponse;
import com.suraj.post_microservice.payload.PostDTO;
import com.suraj.post_microservice.payload.PostWithCommentsDTO;
import com.suraj.post_microservice.repository.PostRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

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
                () -> new ResourceNotFoundException("Post not found with id: " + id)
        );
    }

    public PostDTO updatePost(UUID id, PostDTO updatedPost) {
        Post existingPost = postRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
        existingPost.setTitle(updatedPost.getTitle());
        existingPost.setContent(updatedPost.getContent());
        return mapper.map(postRepo.save(existingPost), PostDTO.class);
    }

    public String deletePost(UUID id) {
        if (!postRepo.existsById(id)) {
            throw new ResourceNotFoundException("Post not found with id: " + id);
        }
        postRepo.deleteById(id);
        return "Post deleted with id: " + id;
    }

//    public PagedResponse getAllComments(UUID postID, Pageable pageable) {
//        String url = commentServiceUrl + "/post/" + postID;
//        PagedResponse response = restTemplate.getForObject(url, PagedResponse.class);
//        return response;
//    }

    public PagedResponse getComments(UUID postId, int page, int size, String sortBy, String sortOrder) {
        String url = UriComponentsBuilder.fromUriString(commentServiceUrl + "/post/" + postId)
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("sort", sortBy + "," + sortOrder)
                .toUriString();

        return restTemplate.getForObject(url, PagedResponse.class);
    }


    public PostWithCommentsDTO getPostWithComments(UUID postID, UUID postId, int page, int size, String sortBy, String sortOrder) {
        // 🔹 1. Get Post from DB
        Post post = postRepo.findById(postID)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + postID));

        // 🔹 2. Get Comments from Comment Microservice
        String url = UriComponentsBuilder.fromUriString(commentServiceUrl + "/post/" + postId)
                .queryParam("page", page)
                .queryParam("size", size)
                .queryParam("sort", sortBy + "," + sortOrder)
                .toUriString();

        PagedResponse pagedResponse = restTemplate.getForObject(url, PagedResponse.class);

        // 🔹 3. Wrap into DTO and Return
        return new PostWithCommentsDTO(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                pagedResponse
        );
    }
}
