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

import me.zhengjie.modules.notary.domain.Bank;
import me.zhengjie.utils.FileUtil;
import lombok.RequiredArgsConstructor;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import me.zhengjie.modules.notary.service.BankService;
import me.zhengjie.modules.notary.domain.vo.BankQueryCriteria;
import me.zhengjie.modules.notary.mapper.BankMapper;
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
public class BankServiceImpl extends ServiceImpl<BankMapper, Bank> implements BankService {

    private final BankMapper bankMapper;

    @Override
    public PageResult<Bank> queryAll(BankQueryCriteria criteria, Page<Object> page){
        return PageUtil.toPage(bankMapper.findAll(criteria, page));
    }

    @Override
    public List<Bank> queryAll(BankQueryCriteria criteria){
        return bankMapper.findAll(criteria);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(Bank resources) {
        save(resources);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Bank resources) {
        Bank bank = getById(resources.getId());
        bank.copy(resources);
        saveOrUpdate(bank);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteAll(List<Integer> ids) {
        removeBatchByIds(ids);
    }

    @Override
    public void download(List<Bank> all, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (Bank bank : all) {
            Map<String,Object> map = new LinkedHashMap<>();
            map.put("银行名称", bank.getBankName());
            map.put("代理人姓名", bank.getAgentName());
            map.put("代理人身份证号", bank.getAgentIdCode());
            map.put("代理人手机号", bank.getAgentTel());
            map.put("代理人签章", bank.getAgentSealUrl());
            map.put("状态0=无效，1=有效", bank.getStatus());
            map.put(" createTime",  bank.getCreateTime());
            map.put(" updateTime",  bank.getUpdateTime());
            list.add(map);
        }
        FileUtil.downloadExcel(list, response);
    }
}
