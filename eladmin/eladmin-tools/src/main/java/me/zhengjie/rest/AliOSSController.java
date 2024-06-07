/*
 *  Copyright 2019-2020 Zheng Jie
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package me.zhengjie.rest;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.Log;
import me.zhengjie.base.ResponseWrap;
import me.zhengjie.domain.QiniuContent;
import me.zhengjie.domain.vo.QiniuQueryCriteria;
import me.zhengjie.service.AliOSSService;
import me.zhengjie.service.QiNiuConfigService;
import me.zhengjie.utils.PageResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * 阿里云存储
 *
 * @author Tianyi
 * @date 2018/09/28 6:55:53
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/alioss")
@Tag(name = "阿里云存储管理")
public class AliOSSController {

    private final AliOSSService aliOSSService;
    private final QiNiuConfigService qiNiuConfigService;

    @Operation(summary = "查询文件", hidden = true)
    @GetMapping
    public ResponseEntity<PageResult<QiniuContent>> query(QiniuQueryCriteria criteria, Page<Object> page) {
        return new ResponseEntity<>(aliOSSService.queryAll(criteria, page), HttpStatus.OK);
    }

    @Operation(summary = "上传文件")
    @PostMapping
    public ResponseWrap<Object> upload(@RequestParam MultipartFile file) {
        return ResponseWrap.ok(aliOSSService.upload(file, qiNiuConfigService.getConfig()));
    }

    @Log("下载文件")
    @Operation(summary = "下载文件")
    @GetMapping(value = "/download")
    public ResponseWrap<String> download(@RequestParam String path) {
        return ResponseWrap.ok(aliOSSService.download(path, qiNiuConfigService.getConfig()));
    }

    @Log("下载文件")
    @Operation(summary = "下载文件", hidden = true)
    @GetMapping(value = "/download/{id}")
    public ResponseWrap<String> download(@PathVariable Long id) {
        return ResponseWrap.ok(aliOSSService.download(aliOSSService.getById(id), qiNiuConfigService.getConfig()));
    }

//    @Log("删除文件")
//    @Operation(summary = "删除文件", hidden = true)
//    @DeleteMapping(value = "/{id}")
//    public ResponseEntity<Object> deleteQiNiu(@PathVariable Long id) {
//        aliOSSService.delete(aliOSSService.getById(id), qiNiuConfigService.getConfig());
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
//
//    @Log("删除多张图片")
//    @Operation(summary = "删除多张图片", hidden = true)
//    @DeleteMapping
//    public ResponseEntity<Object> deleteAllQiNiu(@RequestBody Long[] ids) {
//        aliOSSService.deleteAll(ids, qiNiuConfigService.getConfig());
//        return new ResponseEntity<>(HttpStatus.OK);
//    }
}
