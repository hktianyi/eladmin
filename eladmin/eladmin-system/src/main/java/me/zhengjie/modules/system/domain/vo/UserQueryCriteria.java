/*
 *  Copyright 2019-2020 Zheng Jie
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
package me.zhengjie.modules.system.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import me.zhengjie.utils.Pageable;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Zheng Jie
 * @date 2018-11-23
 */
@Data
@Schema
public class UserQueryCriteria extends Pageable {

    @Schema(description = "用户ID")
    private Long id;

    @Schema(hidden = true)
    private Set<Long> deptIds = new HashSet<>();

    @Schema(description = "模糊搜索", hidden = true)
    private String blurry;

    @Schema(hidden = true)
    private Boolean enabled;

    @Schema(hidden = true)
    private Long deptId;

    @Schema(hidden = true)
    private Long roleId;

    private List<Timestamp> createTime;

    @Schema(hidden = true)
    private Long offset;

    @Schema(description = "账号")
    private String username;

    @Schema(description = "名称")
    private String idName;

    @Schema(description = "手机号")
    private String phone;
}
