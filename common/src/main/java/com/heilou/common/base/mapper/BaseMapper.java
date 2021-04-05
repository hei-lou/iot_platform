package com.heilou.common.base.mapper;

import com.heilou.common.base.model.BaseModel;

/**
 * @author heilou
 * @version 1.0
 * @description:
 * @date 2021/3/29 17:38
 */
public interface BaseMapper {

    int deleteByPrimaryKey(String id);

    int insert(BaseModel baseModel);

    BaseModel selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(BaseModel baseModel);

    int updateByPrimaryKey(BaseModel baseModel);
}
