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
import me.zhengjie.modules.notary.domain.BTrtcRoom;
import me.zhengjie.modules.notary.service.BTrtcRoomService;
import me.zhengjie.modules.notary.domain.vo.BTrtcRoomQueryCriteria;
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
@Tag(name = "TrtcRoom管理")
@RequestMapping("/api/bTrtcRoom")
public class BTrtcRoomController {

    private final BTrtcRoomService bTrtcRoomService;

    @Log("导出数据")
    @Operation(summary = "导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('bTrtcRoom:list')")
    public void exportBTrtcRoom(HttpServletResponse response, BTrtcRoomQueryCriteria criteria) throws IOException {
        bTrtcRoomService.download(bTrtcRoomService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询TrtcRoom")
    @Operation(summary = "查询TrtcRoom")
    @PreAuthorize("@el.check('bTrtcRoom:list')")
    public ResponseEntity<PageResult<BTrtcRoom>> queryBTrtcRoom(BTrtcRoomQueryCriteria criteria, Page<Object> page){
        return new ResponseEntity<>(bTrtcRoomService.queryAll(criteria,page),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增TrtcRoom")
    @Operation(summary = "新增TrtcRoom")
    @PreAuthorize("@el.check('bTrtcRoom:add')")
    public ResponseEntity<Object> createBTrtcRoom(@Validated @RequestBody BTrtcRoom resources){
        bTrtcRoomService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改TrtcRoom")
    @Operation(summary = "修改TrtcRoom")
    @PreAuthorize("@el.check('bTrtcRoom:edit')")
    public ResponseEntity<Object> updateBTrtcRoom(@Validated @RequestBody BTrtcRoom resources){
        bTrtcRoomService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除TrtcRoom")
    @Operation(summary = "删除TrtcRoom")
    @PreAuthorize("@el.check('bTrtcRoom:del')")
    public ResponseEntity<Object> deleteBTrtcRoom(@RequestBody List<Integer> ids) {
        bTrtcRoomService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
