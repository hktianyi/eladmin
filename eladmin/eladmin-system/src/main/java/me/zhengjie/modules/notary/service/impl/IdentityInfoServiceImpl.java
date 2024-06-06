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
package me.zhengjie.modules.notary.service.impl;

import me.zhengjie.modules.notary.domain.IdentityInfo;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.zhengjie.modules.notary.service.IdentityInfoService;
import me.zhengjie.modules.notary.domain.vo.IdentityInfoQueryCriteria;
import me.zhengjie.modules.notary.mapper.IdentityInfoMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import me.zhengjie.utils.PageUtil;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import me.zhengjie.utils.PageResult;

/**
* @description 服务实现
* @author rdbao
* @date 2024-06-06
**/
@Service
@RequiredArgsConstructor
public class IdentityInfoServiceImpl extends ServiceImpl<IdentityInfoMapper, IdentityInfo> implements IdentityInfoService {

    private final IdentityInfoMapper identityInfoMapper;

    @Override
    public PageResult<IdentityInfo> queryAll(IdentityInfoQueryCriteria criteria, Page<Object> page){
        return PageUtil.toPage(identityInfoMapper.findAll(criteria, page));
    }

    @Override
    public List<IdentityInfo> queryAll(IdentityInfoQueryCriteria criteria){
        return identityInfoMapper.findAll(criteria);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(IdentityInfo resources) {
        save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(IdentityInfo resources) {
        IdentityInfo identityInfo = getById(resources.getId());
        identityInfo.copy(resources);
        saveOrUpdate(identityInfo);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(List<Integer> ids) {
        removeBatchByIds(ids);
    }

    @Override
    public void download(List<IdentityInfo> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (IdentityInfo identityInfo : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put(" userId",  identityInfo.getUserId());
            map.put("姓名", identityInfo.getName());
            map.put("身份证号", identityInfo.getCode());
            map.put("手机号", identityInfo.getPhone());
            map.put("签章", identityInfo.getSealUrl());
            map.put("身份证人像面", identityInfo.getIdCardUrlA());
            map.put("身份证国徽面", identityInfo.getIdCardUrlB());
            map.put("状态0=无效，1=有效", identityInfo.getStatus());
            map.put(" createTime",  identityInfo.getCreateTime());
            map.put(" updateTime",  identityInfo.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}