package com.picpay.service;

import com.picpay.config.SambaConfig;
import com.picpay.config.SmbProperties;
import jcifs.CIFSContext;
import jcifs.smb.SmbException;
import jcifs.smb.SmbFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.RetryContext;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.List;

@Service
public class SambaUseCase {
    private final RetryTemplate sambaRetryTemplate;
    private final SambaConfig config;
    private final SmbProperties smbProperties;

    Logger logger = LoggerFactory.getLogger(SambaUseCase.class);

    public SambaUseCase(RetryTemplate sambaRetryTemplate, SambaConfig config, SmbProperties smbProperties) {
        this.sambaRetryTemplate = sambaRetryTemplate;
        this.config = config;
        this.smbProperties = smbProperties;
    }

    public List<SmbFile> smbProtocol() {
        return  sambaRetryTemplate.execute(context -> {
            CIFSContext authed = config.getCifsContext();
            try(SmbFile f = new SmbFile(smbProperties.getUrlSharedPath(), authed)){

                return getSmbFileList(f);

            } catch (MalformedURLException e) {
                try {
                    throw new IOException(e);
                } catch (IOException ex) {
                    throw new RuntimeException(ex.getMessage());
                }
            } catch (SmbException e) {
                throw new RuntimeException(e.getMessage());
            }
        }, this :: sambaFileExecutionErrorHandler);
    }

    private List<SmbFile> getSmbFileList(SmbFile f) throws SmbException {
        var files = f.listFiles();

        if (files != null && files.length > 0) {
           logger.info("Files in the shared folder:");
            for (SmbFile file : files) {

                var dateMod = file.getDate();

                logger.info(file.getName() + " - " + dateMod);
            }
        }
        assert files != null;
        return List.of(files);
    }


    private List<SmbFile> sambaFileExecutionErrorHandler(RetryContext retryContext)  {

        logger.error("Samba operation failed after " + retryContext.getRetryCount() + " attempts.");
        logger.error("Last attempted operation: " + retryContext.getLastThrowable().getMessage());

        return Collections.emptyList();
    }
}
