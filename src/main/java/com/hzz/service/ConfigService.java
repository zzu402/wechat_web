package com.hzz.service;

import com.hzz.cache.CacheManager;
import com.hzz.common.dao.ModelDao;
import com.hzz.exception.CommonException;
import com.hzz.model.ConfigInfo;
import com.hzz.model.User;
import com.hzz.model.UserRole;
import com.hzz.utils.DecryptUtil;
import com.hzz.utils.StringUtil;
import com.hzz.utils.WechatExceptionHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * @Author: huangzz
 * @Description:
 * @Date :2018/4/28
 */
@Service
public class ConfigService {

    @Autowired
    private ModelDao dao;


    public ConfigInfo getConfig() throws CommonException {
        ConfigInfo configInfo=new ConfigInfo();
        configInfo.setStatus(1);
        configInfo.orderBy("id desc");
        List<ConfigInfo>list=dao.select(configInfo);
        if(list==null||list.isEmpty())
            return  null;
        return list.get(0);
    }


}
