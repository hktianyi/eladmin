package me.zhengjie.utils;

import java.util.List;

public record PageResult<T>(List<T> list, long total) {

}
