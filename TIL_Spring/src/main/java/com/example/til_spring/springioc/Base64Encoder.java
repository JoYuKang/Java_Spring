package com.example.til_spring.springioc;

import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class Base64Encoder implements IEncoder {

    public String encode(String encode) {
        return Base64.getEncoder().encodeToString(encode.getBytes(StandardCharsets.UTF_8));
    }
    
}
