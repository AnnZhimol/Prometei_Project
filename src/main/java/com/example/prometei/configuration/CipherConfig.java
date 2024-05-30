package com.example.prometei.configuration;

import com.example.prometei.utils.CipherUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class CipherConfig {
    @Autowired
    private Environment env;

    @PostConstruct
    public void init() {
        CipherUtil.ALGORITHM = env.getProperty("cipher.util.alg");
        CipherUtil.TRANSFORMATION = env.getProperty("cipher.util.transformation");
        CipherUtil.KEY = env.getProperty("cipher.util.key");
    }
}