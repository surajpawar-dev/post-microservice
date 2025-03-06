package com.suraj.post_microservice.payload;

import java.util.UUID;

public record PostDTO(UUID id, String title, String content) {
}
