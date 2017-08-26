package com.company.project.service;

import java.util.List;
import com.company.project.model.User;
import com.company.project.core.Service;

/**
 * Created by CodeGenerator on 2017/08/26.
 */
public interface UserService extends Service<User> {
	public List<User> getBeanList(User bean);
}
