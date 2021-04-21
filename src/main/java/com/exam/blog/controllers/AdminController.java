package com.exam.blog.controllers;

import com.exam.blog.models.Blog;
import com.exam.blog.models.Comment;
import com.exam.blog.models.User;
import com.exam.blog.service.BlogService;
import com.exam.blog.service.CommentService;
import com.exam.blog.service.UserRepoImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping(value = {"/admin/"})
public class AdminController {

    private final UserRepoImpl userRepo;
    private final BlogService blogService;
    private final CommentService commentService;
    private final MainController mainController;

    @Autowired
    public AdminController(UserRepoImpl userRepo, BlogService blogService, CommentService commentService, MainController mainController) {
        this.userRepo = userRepo;
        this.blogService = blogService;
        this.commentService = commentService;
        this.mainController = mainController;
    }

    @GetMapping(value = "account/{id}")
    public String accountAdminInfo(@PathVariable(value = "id", required = false) Long idUser,
                              Model model) {
        User userDB = userRepo.getById(idUser);
        model.addAttribute("name", "Аккаунт " + " " + userDB.getLast_name());
        model.addAttribute("idAdmin", idUser);

        model.addAttribute("title", "Аккаунт " + userDB.getFirst_name() + " " + userDB.getLast_name());
        model.addAttribute("user", userDB);

        return "admin/admin-page";
    }

    @GetMapping("update/{id}")
    public String adminUpdateGet(@PathVariable(name = "id", required = false) Long idUser,
                                Model model){

        User userDB = userRepo.getById(idUser);

        model.addAttribute("name", userDB.getFirst_name() + " " + userDB.getLast_name());
        model.addAttribute("idUser" , idUser);
        model.addAttribute("title", "Страница редактирования данных АДМИНА.");
        model.addAttribute("user", userDB);
        model.addAttribute("idAdmin", 4);

        return "admin/admin-update_page";
    }

    @PostMapping("update")
    public String userUpdatePost(@ModelAttribute User user, Model model,
                                 @RequestParam(name = "image", required = false) MultipartFile foto)
            throws IOException {

        boolean bool = userRepo.uploadFotoImage(user, foto);

        User userDB = userRepo.getById(user.getId());
        user.setUsername(userDB.getUsername());
        user.setFirst_name(userDB.getFirst_name());
        user.setLast_name(userDB.getLast_name());

        model.addAttribute("name", "Аккаутн " + " " + userDB.getFirst_name());
        model.addAttribute("idUser" , user.getId());
        model.addAttribute("idAdmin", 4);

        if(userDB != null) {

            userRepo.update(user, bool);

            return "redirect:account/" + user.getId();
        } else {
            return "redirect:update";
        }
    }

    @GetMapping("blog/list/{id}")
    public String allBlogList(@PathVariable(value = "id", required = false) Long idAdmin,
                              @RequestParam(value = "data", required = false) String data,
                              Model model){
        User userDB = userRepo.getById(idAdmin);
        model.addAttribute("name", "Аккаунт " + userDB.getFirst_name());
        model.addAttribute("title", "Все блоги");
        model.addAttribute("idAdmin", idAdmin);
        if(data.equals("rating")) {
            model.addAttribute("blogList", blogService.getSortListBlogByRating());
            model.addAttribute("sort", data);
        }
        if(data.equals("alphabet")){
            model.addAttribute("blogList", blogService.getSortListBlogByAlphabet());
            model.addAttribute("sort", data);
        }
        if(data.equals("date")){
            model.addAttribute("blogList", blogService.getSortListBlogByDate());
            model.addAttribute("sort", data);
        }
        return "admin/admin-blog-list";
    }

    @GetMapping("all-accounts/{id}")
    public String allUsersList(@PathVariable(value = "id", required = false) Long idAdmin,
                               Model model){

        User userDB = userRepo.getById(idAdmin);
        model.addAttribute("name", "Аккаунт " + userDB.getFirst_name());
        model.addAttribute("title", "Все пользователи");
        model.addAttribute("idAdmin", idAdmin);
        model.addAttribute("userList", userRepo.sortUserListFirstName());

        return "admin/admin-all-users";
    }

    @GetMapping("all-accounts/{idAdmin}/{idUser}")
    public String allUsersList(@PathVariable(value = "idAdmin", required = false) Long idAdmin,
                               @PathVariable(value = "idUser", required = false) Long idUser,
                               Model model){

        User admin = userRepo.getById(idAdmin);
        User userDB = userRepo.getById(idUser);

        model.addAttribute("name", "Аккаунт " + admin.getFirst_name());
        model.addAttribute("title", "Аккаунт пользователя " + userDB.getFirst_name() + ' ' + userDB.getLast_name());
        model.addAttribute("idAdmin", idAdmin);
        model.addAttribute("user", userDB);

        return "admin/admin-user-page";
    }

    @GetMapping("user/all-blogs/{id_admin}/{id_user}")
    public String allBlogsUser(@PathVariable(value = "id_admin", required = false) Long idAdmin,
                               @PathVariable(value = "id_user", required = false) Long idUser,
                               Model model){

        User admin = userRepo.getById(idAdmin);
        User userDB = userRepo.getById(idUser);
        model.addAttribute("name", "Аккаунт " +admin.getFirst_name());
        model.addAttribute("title", "Все блоги пользователя :" + userDB.getUsername());
        model.addAttribute("user", userDB);
        model.addAttribute("idAdmin", idAdmin);
        model.addAttribute("blogList", blogService.getUserSortListBlogByRating(idUser));

        return "admin/admin-user-blogs";
    }

    @GetMapping("user/blog/{id_admin}/{id_user}/{id_blog}/{bool}")
    public String userBlog(@PathVariable(value = "id_admin", required = false) Long idAdmin,
                           @PathVariable(value = "id_user", required = false) Long idUser,
                           @PathVariable(value = "id_blog", required = false) Long idBlog,
                           @PathVariable(value = "bool", required = false) Boolean bool,
                           Model model){

        User adminDB = userRepo.getById(idAdmin);
        User userDB = userRepo.getById(idUser);

        model.addAttribute("name", "Аккаунт " + adminDB.getFirst_name());
        model.addAttribute("title", "Блог пользователя :" + userDB.getFirst_name() + " " + userDB.getLast_name());
        model.addAttribute("user", userDB);
        model.addAttribute("idAdmin", idAdmin);
        model.addAttribute("state", bool);
        model.addAttribute("blog", blogService.getById(idBlog));

        return "admin/admin-blog";
    }

    @GetMapping("delete/comment/{idAdmin}/{idComment}")
    public String deleteComment(@PathVariable(value = "idComment", required = false) Long idComment,
                                @PathVariable(value = "idAdmin", required = false) Long idAdmin){

        Comment commentDB = commentService.getById(idComment);
        commentService.delete(idComment);
        return "redirect:/admin/user/blog/" + idAdmin + "/" + commentDB.getUser().getId() +
                        "/" + commentDB.getBlog().getId() + "/" + true;
    }

    @GetMapping("delete/blog/{idBlog}")
    public String deleteBlog(@PathVariable(value = "idBlog", required = false) Long idBlog){

        Blog blogDB = blogService.getById(idBlog);
        User userDB = userRepo.getById(blogDB.getUser().getId());
        blogService.deleteBlog(userDB, blogDB);

        return "redirect:/admin/blog/list/4";
    }

    @GetMapping("delete/user/{idUser}")
    public String deleteUser(@PathVariable(value = "idUser", required = false) Long idUser){

        User userDB = userRepo.getById(idUser);
        for(Blog blog: userDB.getBlogs()) {
            Blog blogDB = blogService.getById(blog.getId());
            blogService.deleteAdminBlog(userDB, blogDB);
        }
        userRepo.delete(idUser);
        return "redirect:/admin/all-accounts/4";
    }

    @PostMapping("search/{idAdmin}")
    public String search(@RequestParam(name = "search",required = false) String search,
                         @PathVariable(name = "idAdmin", required = false) Long idAdmin,
                         Model model){
        List<User> users = userRepo.findUserBySearch(search);
        List<Blog> bloges = blogService.findBlogBySearch(search);
        List<Comment> comments = commentService.findCommentBySearch(search);

        User userDB = userRepo.getById(idAdmin);
        model.addAttribute("name", "Аккаунт " + " " + userDB.getLast_name());
        model.addAttribute("idAdmin", idAdmin);

        model.addAttribute("title", "Аккаунт " + userDB.getFirst_name() + " " + userDB.getLast_name());
        model.addAttribute("user", userDB);

        model.addAttribute("userList", users);
        model.addAttribute("blogList", bloges);
        model.addAttribute("comments", comments);
        if(users.size() == 0 && bloges.size()== 0 && comments.size() == 0)
            model.addAttribute("msg", "По вашему запросу ничего не найдено");
        return "admin/admin-search-page";
    }
}
