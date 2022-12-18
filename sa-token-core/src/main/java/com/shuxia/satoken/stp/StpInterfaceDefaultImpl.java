package com.shuxia.satoken.stp;

import java.util.Collections;
import java.util.List;

/**
 * 权限默认实现类
 * @author shuxia
 * @date 12/18/2022
 */
public class StpInterfaceDefaultImpl implements StpInterface {
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return Collections.emptyList();
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return Collections.emptyList();
    }
}
