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

import cn.hutool.json.JSONUtil;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhengjie.annotation.Log;
import me.zhengjie.domain.QiniuConfig;
import me.zhengjie.domain.QiniuContent;
import me.zhengjie.domain.vo.QiniuQueryCriteria;
import me.zhengjie.service.QiNiuConfigService;
import me.zhengjie.service.QiniuContentService;
import me.zhengjie.utils.PageResult;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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

    private final QiniuContentService qiniuContentService;
    private final QiNiuConfigService qiNiuConfigService;

    private String bucketName = "";

    static OSS ossClient;

    private OSS getClient() throws ClientException {
        if (ossClient != null) return ossClient;
        synchronized (AliOSSController.class) {
            if (ossClient != null) return ossClient;
            QiniuConfig config = qiNiuConfigService.getConfig();
            bucketName = config.getBucket();
            DefaultCredentialProvider credentialsProvider = CredentialsProviderFactory.newDefaultCredentialProvider(config.getAccessKey(), config.getSecretKey());
            return new OSSClientBuilder().build(config.getZone(), credentialsProvider);
        }
    }

    @Operation(summary = "查询文件", hidden = true)
    @GetMapping
    public ResponseEntity<PageResult<QiniuContent>> queryQiNiu(QiniuQueryCriteria criteria, Page<Object> page) {
        return new ResponseEntity<>(qiniuContentService.queryAll(criteria, page), HttpStatus.OK);
    }

    @Operation(summary = "上传文件")
    @PostMapping
    public ResponseEntity<Object> upload(@RequestParam MultipartFile file) {
        try {
            String originName = file.getOriginalFilename();
            String filename = bucketName + "/" + UUID.randomUUID() + (originName != null && originName.lastIndexOf(".") > -1 ? originName.substring(originName.lastIndexOf(".")) : "");

            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, filename, file.getInputStream());

            // 如果需要上传时设置存储类型和访问权限，请参考以下示例代码。
            // ObjectMetadata metadata = new ObjectMetadata();
            // metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
            // metadata.setObjectAcl(CannedAccessControlList.Private);
            // putObjectRequest.setMetadata(metadata);

            PutObjectResult result = getClient().putObject(putObjectRequest);
            log.info("upload result: {}", JSONUtil.toJsonStr(result));
            Map<String, Object> map = new HashMap<>(3);
            map.put("data", filename);
            return new ResponseEntity<>(map, HttpStatus.OK);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Log("下载文件")
    @Operation(summary = "下载文件", hidden = true)
    @GetMapping(value = "/download/{id}")
    public ResponseEntity<Object> downloadQiNiu(@PathVariable Long id) {
        Map<String, Object> map = new HashMap<>(1);
        map.put("url", qiniuContentService.download(qiniuContentService.getById(id), qiNiuConfigService.getConfig()));
        return new ResponseEntity<>(map, HttpStatus.OK);
    }

    @Log("删除文件")
    @Operation(summary = "删除文件", hidden = true)
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<Object> deleteQiNiu(@PathVariable Long id) {
        qiniuContentService.delete(qiniuContentService.getById(id), qiNiuConfigService.getConfig());
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Log("删除多张图片")
    @Operation(summary = "删除多张图片", hidden = true)
    @DeleteMapping
    public ResponseEntity<Object> deleteAllQiNiu(@RequestBody Long[] ids) {
        qiniuContentService.deleteAll(ids, qiNiuConfigService.getConfig());
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
