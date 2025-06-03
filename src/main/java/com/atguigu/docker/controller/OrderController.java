package com.atguigu.docker.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * ClassName: OrderController
 * Package: com.atguigu.docker.controller
 * Description:
 *
 * @Author ljy
 * @Create 2025-05-31 오후 7:36
 * @Version 1.0
 */
@RestController
public class OrderController {

    @Value("${server.port}")
    private String port;

    @RequestMapping("/order/docker")
    public String helloDocker() {
        return "hello docker\t" + port + "\t" + UUID.randomUUID();
    }

    @RequestMapping("/order/index")
    public String index() {
        return "服务端口号:\t" + port + "\t" + UUID.randomUUID();
    }
}
