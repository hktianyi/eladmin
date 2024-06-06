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

import me.zhengjie.annotation.Log;
import me.zhengjie.modules.notary.domain.Bank;
import me.zhengjie.modules.notary.service.BankService;
import me.zhengjie.modules.notary.domain.vo.BankQueryCriteria;
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
* @author rdbao
* @date 2024-06-06
**/
@RestController
@RequiredArgsConstructor
@Tag(name = "银行管理")
@RequestMapping("/api/bank")
public class BankController {

    private final BankService bankService;

    @Log("导出数据")
    @Operation(summary = "导出数据")
    @GetMapping(value = "/download")
    @PreAuthorize("@el.check('bank:list')")
    public void exportBank(HttpServletResponse response, BankQueryCriteria criteria) throws IOException {
        bankService.download(bankService.queryAll(criteria), response);
    }

    @GetMapping
    @Log("查询银行")
    @Operation(summary = "查询银行")
    @PreAuthorize("@el.check('bank:list')")
    public ResponseEntity<PageResult<Bank>> queryBank(BankQueryCriteria criteria, Page<Object> page){
        return new ResponseEntity<>(bankService.queryAll(criteria,page),HttpStatus.OK);
    }

    @PostMapping
    @Log("新增银行")
    @Operation(summary = "新增银行")
    @PreAuthorize("@el.check('bank:add')")
    public ResponseEntity<Object> createBank(@Validated @RequestBody Bank resources){
        bankService.create(resources);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping
    @Log("修改银行")
    @Operation(summary = "修改银行")
    @PreAuthorize("@el.check('bank:edit')")
    public ResponseEntity<Object> updateBank(@Validated @RequestBody Bank resources){
        bankService.update(resources);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @Log("删除银行")
    @Operation(summary = "删除银行")
    @PreAuthorize("@el.check('bank:del')")
    public ResponseEntity<Object> deleteBank(@RequestBody List<Integer> ids) {
        bankService.deleteAll(ids);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
