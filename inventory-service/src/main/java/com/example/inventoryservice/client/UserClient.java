package com.example.inventoryservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@FeignClient(name = "user-service", path = "/api/users")
public interface UserClient {

	Logger log = LoggerFactory.getLogger(UserClient.class);


    @GetMapping("/{id}")
    Object getUserById(@PathVariable("id") Long id);
}