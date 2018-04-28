package com.hzz.service;

import com.fasterxml.jackson.databind.JavaType;
import com.hzz.cache.CacheInitHandler;
import com.hzz.cache.CacheManager;
import com.hzz.common.dao.ModelDao;
import com.hzz.exception.CommonException;
import com.hzz.model.UserPrivilege;
import com.hzz.model.UserPrivilegeGroup;
import com.hzz.utils.JsonMapper;
import com.hzz.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 权限的数量很有限，因此直接缓存起来
 * Created by hongshuiqiao on 2017/6/20.
 */
@Service
public class UserPrivilegeService {
    private static final Pattern NUMBER_RANGE_PATTERN = Pattern.compile("(\\d+)[-](\\d+)");
    public static final String USER_PRIVILEGES = "USER_PRIVILEGES";
    public static final String USER_PRIVILEGES_GROUP = "USER_PRIVILEGES_GROUP";
    private Logger logger = LoggerFactory.getLogger(UserPrivilegeService.class);
    @Autowired
    private ModelDao dao;

    private Map<Long, UserPrivilege> doGetAllPrivileges() {
        if (!CacheManager.checkCache(USER_PRIVILEGES)) {
            JsonMapper jsonMapper = JsonMapper.nonEmptyMapper();
            JavaType javaType = jsonMapper.constructMapType(LinkedHashMap.class, Long.class, UserPrivilege.class);
            CacheManager.addCache(USER_PRIVILEGES, javaType, new CacheInitHandler() {
                @Override
                public String initCache() {
                    try {
                        Map<Long, UserPrivilege> privilegeIdMap = new LinkedHashMap<>();
                        UserPrivilege condition = new UserPrivilege();
                        condition.orderBy("name");
                        List<UserPrivilege> privileges = dao.select(condition);
                        for (UserPrivilege privilege : privileges) {
                            privilegeIdMap.put(privilege.getId(), privilege);
                        }
                        CacheManager.removeCache(USER_PRIVILEGES_GROUP);
                        return JsonMapper.nonEmptyMapper().toJson(privilegeIdMap);
                    } catch (CommonException e) {
                        logger.error("load user privileges failed.", e);
                    }
                    return null;
                }
            });
        }
        return CacheManager.getCacheValue(USER_PRIVILEGES);
    }

    public List<UserPrivilegeGroup> getPrivilegeGroups() {
        if (!CacheManager.checkCache(USER_PRIVILEGES_GROUP)) {
            JsonMapper jsonMapper = JsonMapper.nonEmptyMapper();
            JavaType javaType = jsonMapper.constructCollectionType(ArrayList.class, UserPrivilegeGroup.class);
            CacheManager.addCache(USER_PRIVILEGES_GROUP, javaType, new CacheInitHandler() {
                @Override
                public String initCache() {
                    List<UserPrivilegeGroup> groups = new ArrayList<>();
                    Map<String, List<UserPrivilege>> map = new LinkedHashMap<>();
                    Map<Long, UserPrivilege> privilegeIdMap = doGetAllPrivileges();
                    Collection<UserPrivilege> privileges = privilegeIdMap.values();
                    for (UserPrivilege privilege : privileges) {
                        List<UserPrivilege> list = map.get(privilege.getCategoryName());
                        if (null == list) {
                            list = new ArrayList<>();
                            map.put(privilege.getCategoryName(), list);
                        }
                        list.add(privilege);
                    }
                    Iterator<Map.Entry<String, List<UserPrivilege>>> iterator = map.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry<String, List<UserPrivilege>> next = iterator.next();
                        UserPrivilegeGroup group = new UserPrivilegeGroup();
                        group.setGroupName(next.getKey());
                        group.setPrivileges(next.getValue());
                        groups.add(group);
                    }
                    return JsonMapper.nonEmptyMapper().toJson(groups);
                }
            });
        }
        return CacheManager.getCacheValue(USER_PRIVILEGES_GROUP);
    }

    public List<UserPrivilege> getUserPrivileges(String privilegeIdPatterns) {
        Map<Long, UserPrivilege> map = new LinkedHashMap<>();
        String[] segments = privilegeIdPatterns.split("[,]");
        for (String segment : segments) {
            if (StringUtil.isBlank(segment))
                continue;
            if ("*".equalsIgnoreCase(segment)) {
                map.putAll(doGetAllPrivileges());
            } else if (segment.startsWith("-")) {
                map.remove(Long.parseLong(segment.substring(1)));
            } else if (segment.contains("-")) {
                Matcher matcher = NUMBER_RANGE_PATTERN.matcher(segment);
                if (matcher.matches()) {
                    long start = Long.parseLong(matcher.group(1));
                    long end = Long.parseLong(matcher.group(2));
                    for (long i = start; i <= end; i++) {
                        UserPrivilege privilege = doGetAllPrivileges().get(i);
                        if (null != privilege)
                            map.put(i, privilege);
                    }
                }
            } else {
                Long id = Long.parseLong(segment);
                UserPrivilege privilege = doGetAllPrivileges().get(id);
                if (null != privilege)
                    map.put(id, privilege);
            }
        }
        return new ArrayList<>(map.values());
    }

    public List<UserPrivilege> getAllPrivileges() {
        Map<Long, UserPrivilege> map = doGetAllPrivileges();
        return new ArrayList<>(map.values());
    }
}