package com.company.project.service.impl;

import java.util.List;
import com.company.project.dao.RolePermissionMapper;
import com.company.project.model.RolePermission;
import com.company.project.service.RolePermissionService;
import com.company.project.core.AbstractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import javax.annotation.Resource;

/**
 * Created by CodeGenerator on 2017/08/26.
 */
@Service
@Transactional
public class RolePermissionServiceImpl extends AbstractService<RolePermission> implements RolePermissionService {
	@Resource
	private RolePermissionMapper rolePermissionMapper;

	public List<RolePermission> getBeanList(RolePermission bean) {
		return rolePermissionMapper.getBeanList(bean);
	}
}
