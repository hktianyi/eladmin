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

import me.zhengjie.modules.notary.domain.CaseInfo;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.zhengjie.modules.notary.service.CaseInfoService;
import me.zhengjie.modules.notary.domain.vo.CaseInfoQueryCriteria;
import me.zhengjie.modules.notary.mapper.CaseInfoMapper;
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
* @author rk
* @date 2024-06-07
**/
@Service
@RequiredArgsConstructor
public class CaseInfoService extends ServiceImpl<CaseInfoMapper, CaseInfo> {

    private final CaseInfoMapper caseInfoMapper;

    public PageResult<CaseInfo> queryAll(CaseInfoQueryCriteria criteria, Page<Object> page){
        return PageUtil.toPage(caseInfoMapper.findAll(criteria, page));
    }

    public List<CaseInfo> queryAll(CaseInfoQueryCriteria criteria){
        return caseInfoMapper.findAll(criteria);
    }

    @Transactional(rollbackFor = Exception.class)
    public void create(CaseInfo resources) {
        save(resources);
    }

    @Transactional(rollbackFor = Exception.class)
    public void update(CaseInfo resources) {
        CaseInfo caseInfo = getById(resources.getId());
        caseInfo.copy(resources);
        saveOrUpdate(caseInfo);
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(List<Integer> ids) {
        removeBatchByIds(ids);
    }

    public void download(List<CaseInfo> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (CaseInfo caseInfo : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("还款人姓名", caseInfo.getLoanName());
            map.put("还款人身份证号", caseInfo.getLoanIdcard());
            map.put("还款人电话", caseInfo.getLoanTel());
            map.put("还款人地址", caseInfo.getLoanAddress());
            map.put("还款人邮箱", caseInfo.getLoanMail());
            map.put("债权人", caseInfo.getCreditor());
            map.put("代理人", caseInfo.getAgentName());
            map.put("代理人身份证号", caseInfo.getAgentIdcard());
            map.put("预约时间", caseInfo.getAppointmentTime());
            map.put("公证员", caseInfo.getNotaryName());
            map.put(" userId",  caseInfo.getUserId());
            map.put("状态:0=代办，1=待重新预约，2=待公证，3=已完成", caseInfo.getStatus());
            map.put("卡号", caseInfo.getCardNo());
            map.put("实际欠款总金额", caseInfo.getDebtAmount());
            map.put("每月还款金额", caseInfo.getMonthlyRepayment());
            map.put("分期起始年份", caseInfo.getInstalmentBeginTime());
            map.put("前诉欠款截至时间", caseInfo.getProsecuteLimitTime());
            map.put("协商后欠款总额", caseInfo.getConsensusAmount());
            map.put("备注", caseInfo.getRemark());
            map.put(" createTime",  caseInfo.getCreateTime());
            map.put(" updateTime",  caseInfo.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
