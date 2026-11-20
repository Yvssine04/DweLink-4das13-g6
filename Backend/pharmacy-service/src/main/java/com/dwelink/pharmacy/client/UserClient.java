package com.dwelink.pharmacy.client;

import com.dwelink.pharmacy.client.dto.UserContactResponse;
import com.dwelink.pharmacy.client.dto.UserInternalResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "USER-SERVICE")
public interface UserClient {

    @GetMapping("/api/utilisateurs/{id}")
    UserInternalResponse getUserById(@PathVariable("id") Long id);

    @GetMapping("/api/utilisateurs/{id}/contact")
    UserContactResponse getUserContactById(@PathVariable("id") Long id);
}
