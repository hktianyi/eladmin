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
import me.zhengjie.modules.notary.domain.CaseFile;
import me.zhengjie.modules.notary.domain.vo.CaseFileQueryCriteria;
import me.zhengjie.modules.notary.mapper.CaseFileMapper;
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
public class CaseFileService extends ServiceImpl<CaseFileMapper, CaseFile> {

    private final CaseFileMapper caseFileMapper;

    public PageResult<CaseFile> queryAll(CaseFileQueryCriteria criteria, Page<Object> page) {
        return PageUtil.toPage(caseFileMapper.findAll(criteria, page));
    }

    public List<CaseFile> queryAll(CaseFileQueryCriteria criteria) {
        return caseFileMapper.findAll(criteria);
    }

    @Transactional(rollbackFor = Exception.class)
    public void create(CaseFile resources) {
        save(resources);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(CaseFile resources) {
        CaseFile caseFile = getById(resources.getId());
        caseFile.copy(resources);
        saveOrUpdate(caseFile);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(List<Integer> ids) {
        removeBatchByIds(ids);
    }

    public void download(List<CaseFile> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (CaseFile caseFile : all) {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("案件id", caseFile.getCaseId());
            map.put("文件名", caseFile.getFileName());
            map.put("文件类型", caseFile.getFileType());
            map.put("文件地址", caseFile.getFileUrl());
            map.put(" createTime", caseFile.getCreateTime());
            map.put(" udateTime", caseFile.getUdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
