package com.thoughtworks.training.zuul.feign;

import com.thoughtworks.training.zuul.model.User;
import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "user", url = "http://localhost:8081/api")
public interface UserFeign {
    @PostMapping("/validate")
    User validateUser(User user);

    @PostMapping("/login")
    User login(User user);
}
