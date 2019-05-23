package com.gsy.crawler.util;

import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;
import tk.mybatis.mapper.common.base.delete.DeleteByPrimaryKeyMapper;
import tk.mybatis.mapper.common.base.insert.InsertSelectiveMapper;
import tk.mybatis.mapper.common.base.select.SelectByPrimaryKeyMapper;
import tk.mybatis.mapper.common.base.update.UpdateByPrimaryKeySelectiveMapper;

/**
 * Created By Gsy on 2019/5/1
 */
public interface CommonMapper<T> extends Mapper<T>, SelectByPrimaryKeyMapper<T>, UpdateByPrimaryKeySelectiveMapper<T>, DeleteByPrimaryKeyMapper<T>, InsertSelectiveMapper<T> {
    //TODO
    //FIXME 特别注意，该接口不能被扫描到，否则会出错
}