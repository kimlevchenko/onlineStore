package ru.skypro.homework.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skypro.homework.dto.Comment;

import java.util.List;

@RestController
@RequestMapping("/ads/{adId}/comments")
@CrossOrigin(value = "http://localhost:3000")
public class CommentController {

    private static final Logger logger = LoggerFactory.getLogger(CommentController.class);

    @GetMapping()
    public ResponseEntity<?> getComments(@PathVariable int adId) {
        logger.info("The get all ad comments method is called.");
        return ResponseEntity.ok(List.of(new Comment()));
    }

    @PostMapping("/{commentId}")
    public ResponseEntity<?> addComment(@PathVariable int adId, @PathVariable int commentId, String text) {
        logger.info("The comment creation method is called.");
        return ResponseEntity.ok(new Comment());
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<?> updateComment(@PathVariable int adId, @PathVariable int commentId, String text) {
        logger.info("The comment update method is called.");
        return ResponseEntity.ok(new Comment());
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable int adId, @PathVariable int commentId) {
        logger.info("The comment delete method is called.");
        return ResponseEntity.ok(new Comment());
    }

}
