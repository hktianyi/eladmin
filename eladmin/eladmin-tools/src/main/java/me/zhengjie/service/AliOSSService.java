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
package me.zhengjie.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import com.aliyun.oss.model.OSSObject;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.aliyuncs.exceptions.ClientException;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.qiniu.common.QiniuException;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.model.FileInfo;
import com.qiniu.util.Auth;
import lombok.RequiredArgsConstructor;
import me.zhengjie.domain.QiniuConfig;
import me.zhengjie.domain.QiniuContent;
import me.zhengjie.domain.vo.QiniuQueryCriteria;
import me.zhengjie.exception.BadRequestException;
import me.zhengjie.mapper.QiniuContentMapper;
import me.zhengjie.rest.AliOSSController;
import me.zhengjie.utils.FileUtil;
import me.zhengjie.utils.PageResult;
import me.zhengjie.utils.PageUtil;
import me.zhengjie.utils.QiNiuUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * @author Zheng Jie
 * @date 2018-12-31
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "qiNiu")
public class AliOSSService extends ServiceImpl<QiniuContentMapper, QiniuContent> {

    private final QiniuContentMapper qiniuContentMapper;
    private final QiNiuConfigService qiNiuConfigService;

    @Value("${qiniu.max-size}")
    private Long maxSize;

    private String bucketName = "";

    private OSS ossClient;

    private OSS getClient() throws ClientException {
        if (ossClient != null) return ossClient;
        synchronized (AliOSSController.class) {
            if (ossClient != null) return ossClient;
            QiniuConfig config = qiNiuConfigService.getConfig();
            bucketName = config.getBucket();
            DefaultCredentialProvider credentialsProvider = CredentialsProviderFactory.newDefaultCredentialProvider(config.getAccessKey(), config.getSecretKey());
            ossClient = new OSSClientBuilder().build("https://" + config.getZone() + ".aliyuncs.com", credentialsProvider);
            return ossClient;
        }
    }

    public PageResult<QiniuContent> queryAll(QiniuQueryCriteria criteria, Page<Object> page) {
        return PageUtil.toPage(qiniuContentMapper.findAll(criteria, page));
    }

    public List<QiniuContent> queryAll(QiniuQueryCriteria criteria) {
        return qiniuContentMapper.findAll(criteria);
    }

    @Transactional(rollbackFor = Exception.class)
    public QiniuContent upload(MultipartFile file, QiniuConfig qiniuConfig) {
        FileUtil.checkSize(maxSize, file.getSize());
        if (qiniuConfig.getId() == null) {
            throw new BadRequestException("请先添加相应配置，再操作");
        }

        try {
            String originName = file.getOriginalFilename();
            String key = bucketName + "/" + UUID.randomUUID() + (originName != null && originName.lastIndexOf(".") > -1 ? originName.substring(originName.lastIndexOf(".")) : "");
            if (qiniuContentMapper.findByKey(key) != null) {
                key = QiNiuUtil.getKey(key);
            }
            // 创建PutObjectRequest对象。
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, key, file.getInputStream());

            // 如果需要上传时设置存储类型和访问权限，请参考以下示例代码。
            // ObjectMetadata metadata = new ObjectMetadata();
            // metadata.setHeader(OSSHeaders.OSS_STORAGE_CLASS, StorageClass.Standard.toString());
            // metadata.setObjectAcl(CannedAccessControlList.Private);
            // putObjectRequest.setMetadata(metadata);
            PutObjectResult result = getClient().putObject(putObjectRequest);
            QiniuContent content = qiniuContentMapper.findByKey(FileUtil.getFileNameNoEx(key));
            if (content == null) {
                //存入数据库
                QiniuContent qiniuContent = new QiniuContent();
                qiniuContent.setSuffix(FileUtil.getExtensionName(key));
                qiniuContent.setBucket(qiniuConfig.getBucket());
                qiniuContent.setType(qiniuConfig.getType());
                qiniuContent.setKey(qiniuConfig.getZone());
                qiniuContent.setUrl(key);
                qiniuContent.setSize(FileUtil.getSize(Integer.parseInt(String.valueOf(file.getSize()))));
                save(qiniuContent);
            }
            return content;
        } catch (Exception e) {
            throw new BadRequestException(e.getMessage());
        }
    }

    public String download(QiniuContent content, QiniuConfig config) {
        String finalUrl;
        String type = "公开";
        if (type.equals(content.getType())) {
            finalUrl = config.getHost() + content.getUrl();
        } else {
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.HOUR_OF_DAY, 1);
                finalUrl = getClient().generatePresignedUrl(config.getBucket(), content.getUrl(), calendar.getTime()).toString();
            } catch (ClientException e) {
                throw new RuntimeException(e);
            }
        }
        return finalUrl;
    }

    public String download(String path, QiniuConfig config) {
        String finalUrl;
        String type = "公开";
        if (type.equals(config.getType())) {
            finalUrl = config.getHost() + path;
        } else {
            try {
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.HOUR_OF_DAY, 1);
                finalUrl = getClient().generatePresignedUrl(config.getBucket(), path, calendar.getTime()).toString();
            } catch (ClientException e) {
                throw new RuntimeException(e);
            }
        }
        return finalUrl;
    }

    @Transactional(rollbackFor = Exception.class)
    public void delete(QiniuContent content, QiniuConfig config) {
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(QiNiuUtil.getRegion(config.getZone()));
        Auth auth = Auth.create(config.getAccessKey(), config.getSecretKey());
        BucketManager bucketManager = new BucketManager(auth, cfg);
        try {
            bucketManager.delete(content.getBucket(), content.getKey() + "." + content.getSuffix());
        } catch (QiniuException ex) {
            ex.printStackTrace();
        } finally {
            removeById(content);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void synchronize(QiniuConfig config) {
        if (config.getId() == null) {
            throw new BadRequestException("请先添加相应配置，再操作");
        }
        //构造一个带指定Zone对象的配置类
        Configuration cfg = new Configuration(QiNiuUtil.getRegion(config.getZone()));
        Auth auth = Auth.create(config.getAccessKey(), config.getSecretKey());
        BucketManager bucketManager = new BucketManager(auth, cfg);
        //文件名前缀
        String prefix = "";
        //每次迭代的长度限制，最大1000，推荐值 1000
        int limit = 1000;
        //指定目录分隔符，列出所有公共前缀（模拟列出目录效果）。缺省值为空字符串
        String delimiter = "";
        //列举空间文件列表
        BucketManager.FileListIterator fileListIterator = bucketManager.createFileListIterator(config.getBucket(), prefix, limit, delimiter);
        while (fileListIterator.hasNext()) {
            //处理获取的file list结果
            QiniuContent qiniuContent;
            FileInfo[] items = fileListIterator.next();
            for (FileInfo item : items) {
                if (qiniuContentMapper.findByKey(FileUtil.getFileNameNoEx(item.key)) == null) {
                    qiniuContent = new QiniuContent();
                    qiniuContent.setSize(FileUtil.getSize(Integer.parseInt(String.valueOf(item.fsize))));
                    qiniuContent.setSuffix(FileUtil.getExtensionName(item.key));
                    qiniuContent.setKey(FileUtil.getFileNameNoEx(item.key));
                    qiniuContent.setType(config.getType());
                    qiniuContent.setBucket(config.getBucket());
                    qiniuContent.setUrl(config.getHost() + "/" + item.key);
                    save(qiniuContent);
                }
            }
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(Long[] ids, QiniuConfig config) {
        for (Long id : ids) {
            delete(getById(id), config);
        }
    }

    public void downloadList(List<QiniuContent> queryAll, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (QiniuContent content : queryAll) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("文件名", content.getKey());
            map.put("文件类型", content.getSuffix());
            map.put("空间名称", content.getBucket());
            map.put("文件大小", content.getSize());
            map.put("空间类型", content.getType());
            map.put("创建日期", content.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
