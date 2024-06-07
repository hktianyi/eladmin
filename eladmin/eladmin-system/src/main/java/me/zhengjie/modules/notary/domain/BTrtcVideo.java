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
* @author rk
* @date 2024-06-07
**/
@Data
@TableName("b_trtc_video")
public class BTrtcVideo implements Serializable {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "id")
    private Integer id;

    @Schema(description = "caseid")
    private String caseId;

    @Schema(description = "视频名称")
    private String videoName;

    @Schema(description = "视频url")
    private String videoUrl;

    @Schema(description = "createTime")
    private Timestamp createTime;

    @Schema(description = "updateTime")
    private Timestamp updateTime;

    public void copy(BTrtcVideo source){
        BeanUtil.copyProperties(source,this, CopyOptions.create().setIgnoreNullValue(true));
    }
}
