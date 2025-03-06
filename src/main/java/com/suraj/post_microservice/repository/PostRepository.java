package com.suraj.post_microservice.repository;

import com.suraj.post_microservice.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PostRepository extends JpaRepository<Post, UUID> {
}