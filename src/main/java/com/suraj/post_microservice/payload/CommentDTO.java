package com.suraj.post_microservice.payload;

import java.util.UUID;

public record CommentDTO(UUID id, String comment) { }
