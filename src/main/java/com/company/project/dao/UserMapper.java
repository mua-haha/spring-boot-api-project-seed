package com.company.project.dao;
import java.util.List;
import com.company.project.core.Mapper;
import com.company.project.model.User;
public interface UserMapper extends Mapper<User> {
public List<User> getBeanList(User bean);
}
