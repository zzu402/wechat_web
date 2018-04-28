package com.hzz.model;

import java.util.List;

public class UserPrivilegeGroup {
    private String groupName;
    private List<UserPrivilege> privileges;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public List<UserPrivilege> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(List<UserPrivilege> privileges) {
        this.privileges = privileges;
    }
}