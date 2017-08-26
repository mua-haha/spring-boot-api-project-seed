package com.company.project.service;
import java.util.List;
import com.company.project.model.RolePermission;
import com.company.project.core.Service;
/**
 * Created by CodeGenerator on 2017/08/26.
 */
public interface RolePermissionService extends Service<RolePermission> {
public List<RolePermission> getBeanList(RolePermission bean);
}
