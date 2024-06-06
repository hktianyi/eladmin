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
package me.zhengjie.modules.notary.domain.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.Objects;

/**
 * @author Zheng Jie
 * @date 2018-11-22
 */
@Getter
@Setter
public class NotaryDTO implements Serializable {

    @Schema(description = "ID")
    private Long id;

    @Schema(description = "角色", hidden = true)
    @JsonIgnore
    private Long roleId;

    @NotBlank
    @Schema(description = "名称")
    private String username;

    @NotBlank
    @Schema(description = "电话号码")
    private String phone;

    @Schema(description = "身份证姓名")
    private String idName;

    @Schema(description = "身份证号")
    private String idCode;

    @Schema(description = "签章URL")
    private String sealUrl;

    @Schema(description = "密码")
    private String password;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NotaryDTO user = (NotaryDTO) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(username, user.username);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, username);
    }
}
