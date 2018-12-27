package main.product.service;

import main.product.dao.UserDao;
import main.product.domain.LoginUser;
import main.product.domain.User;


public class LoginService {

    public User getUser(LoginUser u) {
        UserDao userDao = new UserDao();
        return userDao.findUser(u);
    }

}
