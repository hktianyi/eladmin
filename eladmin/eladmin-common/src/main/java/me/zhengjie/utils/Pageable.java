package me.zhengjie.utils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Schema(description = "分页参数")
public class Pageable {
    @Schema(description = "当前页码")
    private int current = 1;
    @Schema(description = "每页条数")
    private int size = 10;

    public Page<Object> buildPage() {
        return Page.of(current, size);
    }
}
