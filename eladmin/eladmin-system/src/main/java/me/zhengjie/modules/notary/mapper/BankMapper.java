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
package me.zhengjie.modules.notary.mapper;

import me.zhengjie.modules.notary.domain.Bank;
import me.zhengjie.modules.notary.domain.vo.BankQueryCriteria;
import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
* @author rdbao
* @date 2024-06-06
**/
@Mapper
public interface BankMapper extends BaseMapper<Bank> {

    IPage<Bank> findAll(@Param("criteria") BankQueryCriteria criteria, Page<Object> page);

    List<Bank> findAll(@Param("criteria") BankQueryCriteria criteria);
}