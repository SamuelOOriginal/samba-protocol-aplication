package com.picpay.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class SmbProperties {
    private final String username;
    private final String password;
    private final String urlSharedPath;

    public SmbProperties(@Value("${smb.username}") final String username,
                         @Value("${smb.password}") final String password,
                         @Value("${smb.urlSharedPath}") final String urlSharedPath) {
        this.username = username;
        this.password = password;
        this.urlSharedPath = urlSharedPath;
    }

}
