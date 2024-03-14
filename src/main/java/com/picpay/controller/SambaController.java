package com.picpay.controller;

import com.picpay.service.SambaUseCase;
import jcifs.smb.SmbFile;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
public class SambaController {

    private final SambaUseCase sambaUseCase;

    @GetMapping("")
    public List<SmbFile> listFiles() {
        return sambaUseCase.smbProtocol();
    }
}
