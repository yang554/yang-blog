package com.yang.blog.service.impl;

import cn.hutool.core.util.RandomUtil;
import com.yang.blog.entity.SysUserWxEntity;
import com.yang.blog.mapper.SysUserWxMapper;
import com.yang.blog.service.SysUserWxService;
import com.yang.blog.utils.CommonDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

@Service
public class SysUserWxServiceImpl implements SysUserWxService {
    @Autowired
    private SysUserWxMapper wxMapper;
    @Override
    public SysUserWxEntity findByOpenid(String openid) {
        return wxMapper.findByOpenid(openid);
    }

    @Override
    public SysUserWxEntity register(String openid) {
        SysUserWxEntity wxEntity = wxMapper.findByOpenid(openid);
        if(ObjectUtils.isEmpty(wxEntity)){
            wxEntity = new SysUserWxEntity();
            wxEntity.setId(CommonDate.getStamp());
            wxEntity.setOpenid(openid);
            wxEntity.setNickname("用户"+RandomUtil.randomString(4));
            int s = wxMapper.register(wxEntity);
        }

        return wxEntity;
    }
}
