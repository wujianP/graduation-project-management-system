package com.gms.gms.controller;


import com.gms.common.domain.GmsResponse;
import com.gms.common.domain.Meta;
import com.gms.common.exception.code.Code;
import com.gms.gms.domain.FileStorage;
import com.gms.gms.service.FileStorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.MvcUriComponentsBuilder;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author MrBird
 */
@Slf4j
@Validated
@RestController
@RequestMapping("file")
public class FileStorageController {
    @Autowired
    FileStorageService fileStorageService;

    @PostMapping("upload/{docId}")
    public GmsResponse upload(@RequestParam("file") MultipartFile file,@PathVariable("docId") String docId) {
        try {
            fileStorageService.save(file,docId);
            return new GmsResponse().addCodeMessage(new Meta(
                    Code.C200.getCode(),
                    Code.C200.getDesc(),
                    "文件上传成功：" + file.getOriginalFilename()));
        } catch (Exception e) {
            return new GmsResponse().addCodeMessage(new Meta(
                    Code.C400.getCode(),
                    Code.C400.getDesc(),
                    "无法上传文件：" + file.getOriginalFilename()));
        }
    }

    @GetMapping("files")
    public GmsResponse files() {
        List<FileStorage> files = fileStorageService.selectAllFiles();
        return new GmsResponse().addCodeMessage(new Meta(
                Code.C200.getCode(),
                Code.C200.getDesc(),
                "文件列表查询成功"),files);
    }

    //todo 修改函数
    //涉及到下载就不好用封装好的响应对象了，直接使用ResponseEntity就好
    @GetMapping("files/{filename:.+}/{handinId}")
    public ResponseEntity<Resource> getFile(@PathVariable("filename")String filename,String handinId){
        Resource file = fileStorageService.load(filename,handinId);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment;filename=\""+file.getFilename()+"\"")
                .body(file);
    }
}

