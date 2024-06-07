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
package me.zhengjie.modules.notary.rest;

import io.swagger.v3.oas.annotations.Hidden;
import me.zhengjie.annotation.Log;
import me.zhengjie.modules.notary.domain.BTrtcVideo;
import me.zhengjie.modules.notary.service.BTrtcVideoService;
import me.zhengjie.modules.notary.domain.vo.BTrtcVideoQueryCriteria;
import lombok.RequiredArgsConstructor;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.IOException;
import javax.servlet.http.HttpServletResponse;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import me.zhengjie.utils.PageResult;

/**
* @author rk
* @date 2024-06-07
**/
@Hidden
@RestController
@RequiredArgsConstructor
@Tag(name = "TrtcVideo管理")
@RequestMapping("/api/bTrtcVideo")
public class BTrtcVideoController {

    private final BTrtcVideoService bTrtcVideoService;

    @Log("导出数据")
    @Operation(summary = "导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('bTrtcVideo:list')")
    public void exportBTrtcVideo(HttpServletResponse response, BTrtcVideoQueryCriteria criteria) throws IOException {
        bTrtcVideoService.download(bTrtcVideoService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询TrtcVideo")
    @Operation(summary = "查询TrtcVideo")
    @PreAuthorize("@el.check('bTrtcVideo:list')")
    public ResponseEntity<PageResult<BTrtcVideo>> queryBTrtcVideo(BTrtcVideoQueryCriteria criteria, Page<Object> page){
        return new ResponseEntity<>(bTrtcVideoService.queryAll(criteria,page),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增TrtcVideo")
    @Operation(summary = "新增TrtcVideo")
    @PreAuthorize("@el.check('bTrtcVideo:add')")
    public ResponseEntity<Object> createBTrtcVideo(@Validated @RequestBody BTrtcVideo resources){
        bTrtcVideoService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改TrtcVideo")
    @Operation(summary = "修改TrtcVideo")
    @PreAuthorize("@el.check('bTrtcVideo:edit')")
    public ResponseEntity<Object> updateBTrtcVideo(@Validated @RequestBody BTrtcVideo resources){
        bTrtcVideoService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除TrtcVideo")
    @Operation(summary = "删除TrtcVideo")
    @PreAuthorize("@el.check('bTrtcVideo:del')")
    public ResponseEntity<Object> deleteBTrtcVideo(@RequestBody List<Integer> ids) {
        bTrtcVideoService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
