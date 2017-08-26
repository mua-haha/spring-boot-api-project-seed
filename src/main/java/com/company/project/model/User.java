package com.company.project.model;

import java.util.Date;
import javax.persistence.*;

public class User {
    /**
     * 编号
     */
    @Id
    @Column(name = "user_id")
    private Integer userId;

    /**
     * 帐号
     */
    private String username;

    /**
     * 密码MD5(密码+盐)
     */
    private String password;

    /**
     * 盐
     */
    private String salt;

    /**
     * 姓名
     */
    private String realname;

    /**
     * 状态(1:正常,0:锁定)
     */
    private Byte status;

    @Column(name = "role_id")
    private Integer roleId;

    @Column(name = "create_time")
    private Date createTime;

    /**
     * 获取编号
     *
     * @return user_id - 编号
     */
    public Integer getUserId() {
        return userId;
    }

    /**
     * 设置编号
     *
     * @param userId 编号
     */
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    /**
     * 获取帐号
     *
     * @return username - 帐号
     */
    public String getUsername() {
        return username;
    }

    /**
     * 设置帐号
     *
     * @param username 帐号
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * 获取密码MD5(密码+盐)
     *
     * @return password - 密码MD5(密码+盐)
     */
    public String getPassword() {
        return password;
    }

    /**
     * 设置密码MD5(密码+盐)
     *
     * @param password 密码MD5(密码+盐)
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * 获取盐
     *
     * @return salt - 盐
     */
    public String getSalt() {
        return salt;
    }

    /**
     * 设置盐
     *
     * @param salt 盐
     */
    public void setSalt(String salt) {
        this.salt = salt;
    }

    /**
     * 获取姓名
     *
     * @return realname - 姓名
     */
    public String getRealname() {
        return realname;
    }

    /**
     * 设置姓名
     *
     * @param realname 姓名
     */
    public void setRealname(String realname) {
        this.realname = realname;
    }

    /**
     * 获取状态(1:正常,0:锁定)
     *
     * @return status - 状态(1:正常,0:锁定)
     */
    public Byte getStatus() {
        return status;
    }

    /**
     * 设置状态(1:正常,0:锁定)
     *
     * @param status 状态(1:正常,0:锁定)
     */
    public void setStatus(Byte status) {
        this.status = status;
    }

    /**
     * @return role_id
     */
    public Integer getRoleId() {
        return roleId;
    }

    /**
     * @param roleId
     */
    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    /**
     * @return create_time
     */
    public Date getCreateTime() {
        return createTime;
    }

    /**
     * @param createTime
     */
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}