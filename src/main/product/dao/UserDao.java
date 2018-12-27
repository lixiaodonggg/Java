package main.product.dao;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;

import java.sql.SQLException;

import main.product.domain.LoginUser;
import main.product.domain.User;
import main.product.utils.DataSourceUtils;

public class UserDao {

    public User findUser(LoginUser u) {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from user where username = ? and password = ?";
        User user = null;
        try {
            user = runner.query(sql, new BeanHandler<>(User.class), u.getUserName(),
                    u.getPassWord());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

}
