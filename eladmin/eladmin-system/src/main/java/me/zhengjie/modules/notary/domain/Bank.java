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
package me.zhengjie.modules.notary.domain;

import lombok.Data;
import cn.hutool.core.bean.BeanUtil;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.hutool.core.bean.copier.CopyOptions;
import java.sql.Timestamp;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
* @description /
* @author rdbao
* @date 2024-06-06
**/
@Data
@TableName("b_bank")
public class Bank implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "id")
    private Integer id;

    @Schema(description = "银行名称")
    private String bankName;

    @Schema(description = "代理人姓名")
    private String agentName;

    @Schema(description = "代理人身份证号")
    private String agentIdCode;

    @Schema(description = "代理人手机号")
    private String agentTel;

    @Schema(description = "代理人签章")
    private String agentSealUrl;

    @Schema(description = "状态0=无效，1=有效")
    private Integer status;

    @Schema(description = "createTime")
    private Timestamp createTime;

    @Schema(description = "updateTime")
    private Timestamp updateTime;

    public void copy(Bank source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}