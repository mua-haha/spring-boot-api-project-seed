package com.company.project.dao;
import java.util.List;
import com.company.project.core.Mapper;
import com.company.project.model.RolePermission;
public interface RolePermissionMapper extends Mapper<RolePermission> {
public List<RolePermission> getBeanList(RolePermission bean);
}
