package com.heilou.common.base.service;

import com.heilou.common.base.model.BaseModel;

public interface BaseService {

    /**
     * @description:  删除记录
     * @param: id
     * @return: int
     * @author heilou
     * @date: 2021/3/29 17:42
     */
    int deleteByPrimaryKey(String id);

    /**
     * @description: 插入记录
     * @param: id
     * @return: int
     * @author heilou
     * @date: 2021/3/29 17:42
     */
    String insert(BaseModel baseModel);

    /**
     * @description: 根据id获取记录
     * @param: id
     * @return: int
     * @author heilou
     * @date: 2021/3/29 17:42
     */
    BaseModel selectByPrimaryKey(String id);

    /**
     * @description: 根据id更新不为空的字段
     * @param: id
     * @return: int
     * @author heilou
     * @date: 2021/3/29 17:42
     */
    int updateByPrimaryKeySelective(BaseModel baseModel);

    /**
     * @description: 根据id更新全部字段
     * @param: id
     * @return: int
     * @author heilou
     * @date: 2021/3/29 17:42
     */
    int updateByPrimaryKey(BaseModel baseModel);

}
