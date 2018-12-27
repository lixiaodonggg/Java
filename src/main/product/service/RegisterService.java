package main.product.service;

import main.product.dao.RegisterDao;
import main.product.domain.User;

public class RegisterService {

    public boolean findUser(String register) {
        RegisterDao registerDao = new RegisterDao();
        return registerDao.find(register) > 0;
    }

    public User register(User register) {
        RegisterDao registerDao = new RegisterDao();
        return registerDao.register(register);
    }
}
