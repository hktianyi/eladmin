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

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import me.zhengjie.modules.notary.domain.IdentityInfo;
import me.zhengjie.modules.notary.domain.vo.IdentityInfoQueryCriteria;
import me.zhengjie.modules.notary.mapper.IdentityInfoMapper;
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
 * @author rdbao
 * @description 服务实现
 * @date 2024-06-06
 **/
@Service
@RequiredArgsConstructor
public class IdentityInfoService extends ServiceImpl<IdentityInfoMapper, IdentityInfo> {

    private final IdentityInfoMapper identityInfoMapper;

    public PageResult<IdentityInfo> queryAll(IdentityInfoQueryCriteria criteria, Page<Object> page) {
        return PageUtil.toPage(identityInfoMapper.findAll(criteria, page));
    }

    public IdentityInfo queryByUserId(Long userId) {
        return identityInfoMapper.selectOne(Wrappers.<IdentityInfo>lambdaQuery().eq(IdentityInfo::getUserId, userId));
    }

    public List<IdentityInfo> queryAll(IdentityInfoQueryCriteria criteria) {
        return identityInfoMapper.findAll(criteria);
    }

    @Transactional(rollbackFor = Exception.class)
    public void create(IdentityInfo resources) {
        save(resources);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(IdentityInfo resources) {
        IdentityInfo identityInfo = getById(resources.getId());
        identityInfo.copy(resources);
        saveOrUpdate(identityInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(List<Long> ids) {
        removeBatchByIds(ids);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteByUserId(Long userId) {
        identityInfoMapper.deleteByUserId(userId);
    }

    public void download(List<IdentityInfo> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (IdentityInfo identityInfo : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put(" userId", identityInfo.getUserId());
            map.put("姓名", identityInfo.getName());
            map.put("身份证号", identityInfo.getCode());
            map.put("手机号", identityInfo.getPhone());
            map.put("签章", identityInfo.getSealUrl());
            map.put("身份证人像面", identityInfo.getIdCardUrlA());
            map.put("身份证国徽面", identityInfo.getIdCardUrlB());
            map.put("状态0=无效，1=有效", identityInfo.getStatus());
            map.put(" createTime", identityInfo.getCreateTime());
            map.put(" updateTime", identityInfo.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
