package com.mymall.service;

import org.springframework.web.multipart.MultipartFile;

public interface IFileService {
    public  String uplod(MultipartFile file, String path);
}
