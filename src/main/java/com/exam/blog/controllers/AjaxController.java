package com.exam.blog.controllers;

import com.exam.blog.models.Blog;
import com.exam.blog.models.Comment;
import com.exam.blog.models.User;
import com.exam.blog.service.BlogService;
import com.exam.blog.service.CommentService;
import com.exam.blog.service.UserRepoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@Transactional
@RequestMapping("/")
public class AjaxController {

    private final BlogService blogService;
    private final UserRepoImpl userRepo;
    private final CommentService commentService;


    @Autowired
    public AjaxController(BlogService blogService, UserRepoImpl userRepo, CommentService commentService) {
        this.blogService = blogService;
        this.userRepo = userRepo;
        this.commentService = commentService;
    }

    @GetMapping(value = "ajax/rating")
    public ResponseEntity<Float> ratingBlog(@RequestParam(value = "idBlog", required = false) String id_blog,
                                            @RequestParam(value = "rating", required = false) String str_rating,
                                            Model model) {

        Long idBlog = Long.valueOf(id_blog);
        float rating = Float.valueOf(str_rating);
        Blog blogDB = blogService.getById(idBlog);
        if (blogDB != null) {
            if (blogDB.getRating() == null) {
                blogDB.setRating(rating);
            } else {
                Float ratingDB = (blogDB.getRating() + rating) / 2;
                blogDB.setRating(ratingDB);
            }
            blogService.update(blogDB);
        }

        model.addAttribute("blog", blogService.getById(idBlog));

        return ResponseEntity.ok(blogDB.getRating());
    }

    @GetMapping(value = "ajax/ban/blog")
    public ResponseEntity<Boolean> banBlog(@RequestParam(value = "idBlog", required = false) String id_blog,
                                            @RequestParam(value = "bunBlog", required = false) String str_ban) {
        Long idBlog = Long.valueOf(id_blog);
        Boolean banBlog = Boolean.valueOf(str_ban);

        Blog blogDB = blogService.getById(idBlog);
        blogDB.setBan_blog(banBlog);
        blogService.update(blogDB);

        return ResponseEntity.ok(banBlog);
    }

    @GetMapping(value = "ajax/ban/user")
    public ResponseEntity<Boolean> banUser(@RequestParam(value = "idUser", required = false) String id_user,
                                           @RequestParam(value = "bunUser", required = false) String str_ban) {
        Long idUser = Long.valueOf(id_user);
        Boolean banUser = Boolean.valueOf(str_ban);

        User userDB = userRepo.getById(idUser);
        userDB.setBan_user(banUser);
        userRepo.update(userDB,false);

        return ResponseEntity.ok(banUser);
    }

    @GetMapping(value = "ajax/ban/comment")
    public ResponseEntity<Boolean> banComment(@RequestParam(value = "idComment", required = false) String id_comment,
                                              @RequestParam(value = "bunComment", required = false) String str_ban) {
        Long idComment = Long.valueOf(id_comment);
        Boolean banComment = Boolean.valueOf(str_ban);

        Comment commentDB = commentService.getById(idComment);
        commentDB.setBanComment(banComment);
        commentService.update(commentDB);

        return ResponseEntity.ok(banComment);
    }

//    @GetMapping(value = "ajax/comment")
//    public ResponseEntity<Comment> addCommentBlog(@RequestParam(value = "idBlog", required = false) String idBlog,
//                                                        @RequestParam(value = "name", required = false) String username,
//                                                        @RequestParam(value = "comment", required = false) String comment,
//                                                        @RequestParam(value = "idUser", required = false) String idUser,
//                                                        Model model) {
//
//        System.out.println(Long.valueOf(idBlog) + " " + username + " " + comment + " " + Long.valueOf(idUser));
//        Blog blogDB = blogService.getById(Long.valueOf(idBlog));
//        Comment commentUser = new Comment();
//        commentUser.setBlog(blogDB);
//        commentUser.setDateCreateComment(LocalDateTime.now());
//        commentUser.setText(comment);
//        commentUser.setBanComment(false);
//        commentUser.setUser(userRepo.getById(Long.valueOf(idUser)));
//        commentService.save(commentUser);
//
//        System.out.println(ResponseEntity.ok(commentUser));
//
//        return ResponseEntity.ok().body(commentUser);
//    }
}