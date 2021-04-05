package com.heilou.common.base.service.impl;

import com.heilou.common.utils.IDUtil;
import com.heilou.common.base.mapper.BaseMapper;
import com.heilou.common.base.model.BaseModel;
import com.heilou.common.base.service.BaseService;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * @author heilou
 * @version 1.0
 * @description:
 * @date 2021/3/29 17:38
 */
public abstract  class BaseServiceImpl implements BaseService {


    public abstract BaseMapper getMapper();

    @Override
    @Transactional
    public int deleteByPrimaryKey(String id) {
        return getMapper().deleteByPrimaryKey(id);
    }

    @Override
    @Transactional
    public String insert(BaseModel baseModel) {
        String id = IDUtil.getSnowFlakeId();
        baseModel.setId(id);
        baseModel.setCreateTime(new Date());
        baseModel.setUpdateTime(new Date());
        getMapper().insert(baseModel);
        return id;
    }

    @Override
    public BaseModel selectByPrimaryKey(String id) {
        return getMapper().selectByPrimaryKey(id);
    }

    @Override
    @Transactional
    public int updateByPrimaryKeySelective(BaseModel baseModel) {
        return getMapper().updateByPrimaryKeySelective(baseModel);
    }

    @Override
    @Transactional
    public int updateByPrimaryKey(BaseModel baseModel) {
        baseModel.setUpdateTime(new Date());
        return getMapper().updateByPrimaryKey(baseModel);
    }
}
