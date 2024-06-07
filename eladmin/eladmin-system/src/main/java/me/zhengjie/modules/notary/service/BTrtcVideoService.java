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
import me.zhengjie.modules.notary.domain.BTrtcVideo;
import me.zhengjie.modules.notary.domain.vo.BTrtcVideoQueryCriteria;
import me.zhengjie.modules.notary.mapper.BTrtcVideoMapper;
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
* @description 服务实现
* @author rk
* @date 2024-06-07
**/
@Service
@RequiredArgsConstructor
public class BTrtcVideoService extends ServiceImpl<BTrtcVideoMapper, BTrtcVideo> {

    private final BTrtcVideoMapper bTrtcVideoMapper;

    public PageResult<BTrtcVideo> queryAll(BTrtcVideoQueryCriteria criteria, Page<Object> page){
        return PageUtil.toPage(bTrtcVideoMapper.findAll(criteria, page));
    }

    public List<BTrtcVideo> queryAll(BTrtcVideoQueryCriteria criteria){
        return bTrtcVideoMapper.findAll(criteria);
    }

    @Transactional(rollbackFor = Exception.class)
    public void create(BTrtcVideo resources) {
        save(resources);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(BTrtcVideo resources) {
        BTrtcVideo bTrtcVideo = getById(resources.getId());
        bTrtcVideo.copy(resources);
        saveOrUpdate(bTrtcVideo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(List<Integer> ids) {
        removeBatchByIds(ids);
    }

    public void download(List<BTrtcVideo> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (BTrtcVideo bTrtcVideo : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("caseid", bTrtcVideo.getCaseId());
            map.put("视频名称", bTrtcVideo.getVideoName());
            map.put("视频url", bTrtcVideo.getVideoUrl());
            map.put(" createTime",  bTrtcVideo.getCreateTime());
            map.put(" updateTime",  bTrtcVideo.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
