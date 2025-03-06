package com.suraj.post_microservice.controller;

import com.suraj.post_microservice.entity.Post;
import com.suraj.post_microservice.payload.CommentDTO;
import com.suraj.post_microservice.payload.PostDTO;
import com.suraj.post_microservice.payload.PostWithCommentsDTO;
import com.suraj.post_microservice.service.PostService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("api/v1/posts")
public class PostController {

    private final PostService postService;

    public PostController(PostService postService) {
        this.postService = postService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Post> getPostById(@PathVariable UUID id) {
        return new ResponseEntity<>(postService.getPostById(id), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PostDTO> createPost(@RequestBody PostDTO postDTO) {
        return new ResponseEntity<>(postService.createPost(postDTO), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<PostDTO> updatePost(@PathVariable UUID id, @RequestBody PostDTO post) {
        return new ResponseEntity<>(postService.updatePost(id, post), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deletePost(@PathVariable UUID id) {
        return new ResponseEntity<>(postService.deletePost(id), HttpStatus.OK);
    }

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentDTO>> getAllComments(@PathVariable UUID postId) {
        return new ResponseEntity<>(postService.getAllComments(postId), HttpStatus.OK);
    }

    @GetMapping("/{postId}/with-comments")
    public ResponseEntity<PostWithCommentsDTO> getPostWithComments(@PathVariable UUID postId) {
        return new ResponseEntity<>(postService.getPostWithComments(postId), HttpStatus.OK);
    }
}
