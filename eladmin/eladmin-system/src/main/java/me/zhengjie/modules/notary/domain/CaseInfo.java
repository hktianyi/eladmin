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
import java.math.BigDecimal;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
* @description /
* @author rk
* @date 2024-06-07
**/
@Data
@TableName("b_case_info")
public class CaseInfo implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "id")
    private Long id;

    @Schema(description = "bank_id")
    private Long bankId;

    @Schema(description = "订单编号")
    private String caseNo;

    @Schema(description = "还款人姓名")
    private String loanName;

    @Schema(description = "还款人身份证号")
    private String loanIdcard;

    @Schema(description = "还款人电话")
    private String loanTel;

    @Schema(description = "还款人地址")
    private String loanAddress;

    @Schema(description = "还款人邮箱")
    private String loanMail;

    @Schema(description = "债权人")
    private String creditor;

    @Schema(description = "代理人")
    private String agentName;

    @Schema(description = "代理人身份证号")
    private String agentIdcard;

    @Schema(description = "预约时间")
    private Timestamp appointmentTime;

    @Schema(description = "公证员")
    private String notaryName;

    @Schema(description = "userId")
    private Long userId;

    @Schema(description = "状态:0=代办，1=待重新预约，2=待公证，3=已完成")
    private Integer status;

    @Schema(description = "卡号")
    private String cardNo;

    @Schema(description = "实际欠款总金额")
    private BigDecimal debtAmount;

    @Schema(description = "每月还款金额")
    private BigDecimal monthlyRepayment;

    @Schema(description = "分期起始年份")
    private String instalmentBeginTime;

    @Schema(description = "前诉欠款截至时间")
    private Timestamp prosecuteLimitTime;

    @Schema(description = "协商后欠款总额")
    private BigDecimal consensusAmount;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "createTime")
    private Timestamp createTime;

    @Schema(description = "updateTime")
    private Timestamp updateTime;

    public void copy(CaseInfo source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
