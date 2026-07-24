package com.interview.backend.controller;

import com.interview.backend.entity.User;
import com.interview.backend.service.UserService;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController                     // ① 标记这是 REST 接口层
@RequestMapping("/users")           // ② 所有接口路径都以 /users 开头
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {   // ③ Spring 自动把 Service 传进来
        this.userService = userService;
    }

    // 查全部
    @GetMapping                     // ④ GET /users
    public List<User> list() {
        return userService.listAll();
    }

    // 查一个
    @GetMapping("/{id}")            // ⑤ GET /users/5 → PathVariable 取到 5
    public User getById(@PathVariable Integer id) {
        return userService.getById(id);
    }

    // 新增
    @PostMapping                    // ⑥ POST /users，请求体的 JSON → User 对象
    public User create(@RequestBody User user) {
        return userService.create(user);
    }

    // 修改
    @PutMapping                     // ⑦ PUT /users，请求体的 JSON → User 对象
    public User update(@RequestBody User user) {
        return userService.update(user);
    }

    // 删除
    @DeleteMapping("/{id}")         // ⑧ DELETE /users/5
    public void delete(@PathVariable Integer id) {
        userService.delete(id);
    }
}
