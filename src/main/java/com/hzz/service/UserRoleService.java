package com.hzz.service;
import com.fasterxml.jackson.databind.JavaType;
import com.hzz.cache.CacheInitHandler;
import com.hzz.cache.CacheManager;
import com.hzz.common.dao.ModelDao;
import com.hzz.common.dao.ModelHelper;
import com.hzz.exception.CommonException;
import com.hzz.model.UserPrivilege;
import com.hzz.model.UserRole;
import com.hzz.utils.JsonMapper;
import com.hzz.utils.StringUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserRoleService {
    public static final String USER_ROLES = "USER_ROLES";
    public static final String ROLES_PRIVILEGES = "ROLES_PRIVILEGES";
    private Logger logger = LoggerFactory.getLogger(UserRoleService.class);
    @Autowired
    private ModelDao dao;
    @Autowired
    private UserPrivilegeService privilegeService;

    public Map<Long, UserRole> getAllRoles() {
        if (!CacheManager.checkCache(USER_ROLES)) {
            JsonMapper jsonMapper = JsonMapper.nonEmptyMapper();
            JavaType javaType = jsonMapper.constructMapType(LinkedHashMap.class, Long.class, UserRole.class);
            CacheManager.addCache(USER_ROLES, javaType, new CacheInitHandler() {
                @Override
                public String initCache() {
                    try {
                        Map<Long, UserRole> roleMap = new LinkedHashMap<>();
                        UserRole condition = new UserRole();
                        condition.orderBy("roleType,name");
                        List<UserRole> roles = dao.select(condition);
                        for (UserRole role : roles) {
                            List<UserPrivilege> privileges = privilegeService.getUserPrivileges(role.getPrivileges());
                            //将正则替换成真正的id
                            role.setPrivileges(StringUtils.join(ModelHelper.filterProperties(privileges,"id",Long.class), ","));
                            roleMap.put(role.getId(), role);
                        }
                        CacheManager.removeCache(ROLES_PRIVILEGES);
                        return JsonMapper.nonEmptyMapper().toJson(roleMap);
                    } catch (CommonException e) {
                        logger.error("load user roles failed.", e);
                    }
                    return null;
                }
            });
        }
        return CacheManager.getCacheValue(USER_ROLES);
    }

    public List<String> getRolePrivileges(Long roleId) throws CommonException {
        if (null == roleId || roleId <= 0)
            return new ArrayList<>();
        return new ArrayList<>(getPrivileges().get(roleId).keySet());
    }

    public Map<String, Long> getPrivilegeMap(Long roleId) throws CommonException {
        if (null == roleId || roleId <= 0)
            return new LinkedHashMap<>();
        return getPrivileges().get(roleId);
    }

    public Map<Long, Map<String, Long>> getPrivileges() throws CommonException {
        if (!CacheManager.checkCache(ROLES_PRIVILEGES)) {
            JsonMapper jsonMapper = JsonMapper.nonEmptyMapper();
            JavaType valueType = jsonMapper.constructMapType(LinkedHashMap.class, String.class, Long.class);
            JavaType javaType = jsonMapper.constructMapType(HashMap.class, jsonMapper.constructType(Long.class), valueType);
            CacheManager.addCache(ROLES_PRIVILEGES, javaType, new CacheInitHandler() {
                @Override
                public String initCache() {
                    Map<Long, Map<String, Long>> map = new HashMap<>();
                    Map<Long, UserRole> allRoles = getAllRoles();
                    Collection<UserRole> roles = allRoles.values();
                    for (UserRole role : roles) {
                        Map<String, Long> pMap = map.get(role.getId());
                        if (null == pMap) {
                            pMap = new LinkedHashMap<>();
                            map.put(role.getId(), pMap);
                        }
                        if (StringUtil.isBlank(role.getPrivileges())) {
                            continue;
                        }
                        List<UserPrivilege> privileges = privilegeService.getUserPrivileges(role.getPrivileges());
                        for (UserPrivilege privilege : privileges)
                            pMap.put(privilege.getName(), privilege.getId());
                    }
                    return JsonMapper.nonEmptyMapper().toJson(map);
                }
            });
        }
        return CacheManager.getCacheValue(ROLES_PRIVILEGES);
    }

    public UserRole getRole(Long id) throws CommonException {
        return getAllRoles().get(id);
    }

    public Map<String,Object> getRoleExtInfo(Long roleId) throws CommonException {
        UserRole role = getRole(roleId);
        if(null == role)
            return null;
        String extInfo = role.getExtInfo();
        if(StringUtil.isBlank(extInfo))
            return null;
        return (Map<String, Object>) JsonMapper.nonEmptyMapper().fromJson(extInfo);
    }

    public boolean checkPrivilege(Long roleId, String privilegeName) throws CommonException {
        return checkPrivileges(roleId, new String[]{privilegeName});
    }

    public boolean checkPrivileges(Long roleId, String[] privileges) throws CommonException {
        if (null == privileges || privileges.length <= 0)
            return true;
        if (null == roleId || roleId <= 0)
            return false;
        Map<String, Long> map = getPrivilegeMap(roleId);
        if (null == map || map.isEmpty())
            return false;
        for (String privilegeName : privileges) {
            if (!map.containsKey(privilegeName))
                return false;
        }
        return true;
    }
}
