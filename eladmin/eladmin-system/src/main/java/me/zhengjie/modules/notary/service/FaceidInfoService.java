/*
 *  Copyright 2019-2023 Zheng Jie
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
package me.zhengjie.modules.notary.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.notary.domain.FaceidInfo;
import me.zhengjie.modules.notary.domain.vo.FaceidInfoQueryCriteria;
import me.zhengjie.modules.notary.mapper.FaceidInfoMapper;
import me.zhengjie.utils.FileUtil;
import me.zhengjie.utils.PageResult;
import me.zhengjie.utils.PageUtil;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rk
 * @description 服务实现
 * @date 2024-06-07
 **/
@Service
@RequiredArgsConstructor
public class FaceidInfoService extends ServiceImpl<FaceidInfoMapper, FaceidInfo> {

    private final FaceidInfoMapper faceidInfoMapper;

    public PageResult<FaceidInfo> queryAll(FaceidInfoQueryCriteria criteria, Page<Object> page) {
        return PageUtil.toPage(faceidInfoMapper.findAll(criteria, page));
    }

    public List<FaceidInfo> queryAll(FaceidInfoQueryCriteria criteria) {
        return faceidInfoMapper.findAll(criteria);
    }

    @Transactional(rollbackFor = Exception.class)
    public void create(FaceidInfo resources) {
        save(resources);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(FaceidInfo resources) {
        FaceidInfo faceidInfo = getById(resources.getId());
        faceidInfo.copy(resources);
        saveOrUpdate(faceidInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(List<Integer> ids) {
        removeBatchByIds(ids);
    }

    public void download(List<FaceidInfo> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (FaceidInfo faceidInfo : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("caseid", faceidInfo.getCaseId());
            map.put("姓名", faceidInfo.getIdName());
            map.put("身份证号", faceidInfo.getIdCard());
            map.put("核验时间", faceidInfo.getFaceVerifyTime());
            map.put("核验结果", faceidInfo.getVerifyResult());
            map.put("核验通过率", faceidInfo.getVerifyRate());
            map.put("核验图片", faceidInfo.getFacePngUrl());
            map.put("核验视频", faceidInfo.getFaceVideoUrl());
            map.put(" createTime", faceidInfo.getCreateTime());
            map.put(" updateTime", faceidInfo.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
