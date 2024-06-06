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
package me.zhengjie.modules.notary.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import me.zhengjie.utils.Pageable;

import java.sql.Timestamp;
import java.util.List;

/**
* @author rdbao
* @date 2024-06-06
**/
@Data
@Schema
public class BankQueryCriteria extends Pageable {

    @Schema(description = "客户名称")
    private String bankName;

    @Schema(description = "代理人姓名")
    private String agentName;

    @Schema(description = "代理人手机号")
    private String agentTel;

    private List<Timestamp> createTime;
}
