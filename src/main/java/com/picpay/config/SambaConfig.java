package com.picpay.config;

import jcifs.CIFSContext;
import jcifs.context.SingletonContext;
import jcifs.smb.NtlmPasswordAuthentication;
import jcifs.smb.NtlmPasswordAuthenticator;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
@Configuration
public class SambaConfig {
    private final SmbProperties smbProperties;

    public SambaConfig(SmbProperties smbProperties) {
        this.smbProperties = smbProperties;
    }

    public CIFSContext getCifsContext() {
        CIFSContext base = SingletonContext.getInstance();
        return base.withCredentials(new NtlmPasswordAuthenticator(smbProperties.getUsername(), smbProperties.getPassword()));
    }
    @Bean
    public RetryTemplate sambaRetryTemplate() {
        RetryTemplate retryTemplate = new RetryTemplate();

        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(3000);
        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);

        SimpleRetryPolicy retryPolicy = new SimpleRetryPolicy();

        retryPolicy.setMaxAttempts(3);
        retryTemplate.setRetryPolicy(retryPolicy);

        return new RetryTemplate();
    }

}
