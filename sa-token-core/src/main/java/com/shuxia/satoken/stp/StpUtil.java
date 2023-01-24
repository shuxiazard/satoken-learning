package com.shuxia.satoken.stp;

import java.util.List;

/**
 * Sa-Token 权限认证工具类
 * @author shuxia
 * @date 11/16/2022
 */
public class StpUtil {
    private StpUtil(){}

    /**
     * 账号类型
     */
    public static final String TYPE="login";

    public static StpLogic stpLogic=new StpLogic(TYPE);
    /**
     * 获取当前 StpLogic 的账号类型
     * @return See Note
     */
    public static String getLoginType() {
        return stpLogic.getLoginType();
    }
    // ------------------- 登录相关操作 -------------------

    // --- 登录

    /**
     * 会话登录
     * @param id 账号id，建议的类型：（long | int | String）
     */
    public static void login(Object id) {
        stpLogic.login(id);
    }

    /**
     * 检查是否登录
     */
    public static void checkLogin(){
       stpLogic.checkLogin();
    }

    //region ----------------踢人下线---------------
    /**
     * 踢人下线
     */
    public static void kickout(Object loginId){
        stpLogic.kickout(loginId);
    }

    /**
     * 踢人下线 根据token
     * @param token
     */
    public static void kickoutByToken(String token){
        stpLogic.kickoutByTokenValue(token);
    }

    //endregion

    //region ----------------封禁相关---------------
    /**
     * 封禁账号
     * @param loginId
     * @param time 单位秒
     */
    public static void disable(Object loginId,long time){
        stpLogic.disable(loginId,time);
    }

    /**
     * 封禁账号
     * @param loginId
     * @param service
     * @param time 单位秒
     */
    public static void disable(Object loginId,String service,long time){
        stpLogic.disable(loginId,service,time);
    }

    /**
     * 账号是否封禁
     * @param loginId
     */
    public static boolean isDisable(Object loginId){
        return stpLogic.isDisable(loginId);
    }
    public static boolean isDisable(Object loginId,String service){
        return stpLogic.isDisable(loginId,service);
    }

    /**
     * 获取封禁时间
     * @param loginId
     * @return
     */
    public static long getDisableTime(Object loginId){
        return  stpLogic.getDisableTime(loginId);
    }

    public static void uniteDisable(Object loginId){
        stpLogic.uniteDisable(loginId);
    }
    //endregion

    //region -----------------注销相关-------------
    /**
     * 注销
     */
    public static void logout(){
        stpLogic.logout();
    }

    /**
     * 根据id注销
     * @param loginId
     */
    public static void logout(Object loginId){
        stpLogic.logout(loginId);
    }

    /**
     * 根据id和设备注销
     * @param loginId
     * @param device
     */
    public static void logout(Object loginId,String device){
        stpLogic.logout(loginId,device);
    }
    //endregion

    //region -------------------权限相关--------------

    /**
     * 获取当前账号权限
     * @return
     */
    public static List<String> getPermissionList(){
        return stpLogic.getPermissionList();
    }

    /**
     * 获取账号权限 根据id
     * @param loginId
     * @return
     */
    public static List<String> getPermissionList(Object loginId){
        return stpLogic.getPermissionList(loginId);
    }

    /**
     * 判断是否有指定权限
     * @param permission
     * @return
     */
    public static boolean hasPermission(String permission){
        return stpLogic.hasPermission(permission);
    }

    /**
     * 判断是否有指定权限(多个)
     * @param permissionArray
     * @return
     */
    public static boolean hasPermissionAnd(String... permissionArray){
        return stpLogic.hasPermissionAnd(permissionArray);
    }

    /**
     * 判断是否有指定权限(任意一个)
     * @param permissionArray
     * @return
     */
    public static boolean hasPermissionOr(String... permissionArray){
        return stpLogic.hasPermissionOr(permissionArray);
    }
    //endregion

    //region -----------------角色认证相关-------------

    /**
     * 获取当前账号角色集合
     * @return
     */
    public List<String> getRoleList(){
        return stpLogic.getRoleList();
    }

    /**
     * 获取指定账号角色集合
     * @param loginId
     * @return
     */
    public List<String> getRoleList(Object loginId){
        return stpLogic.getRoleList(loginId);
    }

    /**
     * 判断当前账号是否有指定角色
     * @param role
     * @return
     */
    public boolean hasRole(String role){
        return stpLogic.hasRole(role);
    }

    /**
     * 判断账号是否有指定角色
     * @param loginId
     * @param role
     * @return
     */
    public boolean hasRole(Object loginId,String role){
        return stpLogic.hasRole(loginId,role);
    }

    /**
     * 判断当前账号是否有指定角色（多个）
     * @param role
     * @return
     */
    public boolean hasRoleAnd(String...role){
        return stpLogic.hasRoleAnd(role);
    }

    /**
     * 判断当前账号是否有指定角色（任意）
     * @param role
     * @return
     */
    public boolean hasRoleOr(String...role){
        return stpLogic.hasRoleOr(role);
    }
    //endregion


    // region ---------------------二级认证---------------------

    /**
     * 开启二级认证
     */
    public static void openSafe(long safeTime){ stpLogic.openSafe(safeTime);}
    /**
     * 开启二级认证
     * @param service 业务标识
     * @param safeTime 维持时间
     */
    public static void openSafe(String service,long safeTime){
        stpLogic.openSafe(service,safeTime);
    }

    /**
     * 二级认证校验
     * @param service
     */
    public static void checkSafe(String service) {
        stpLogic.checkSafe(service);
    }

    public static void checkSafe(){stpLogic.checkSafe();}

    public static boolean isSafe(){return stpLogic.isSafe();}
    public static boolean isSafe(String service){return stpLogic.isSafe(service);}
    //endregion
}
