package main.product.dao;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;

import main.product.domain.User;
import main.product.utils.DataSourceUtils;

public class RegisterDao {
    public User register(User u) {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "insert into " + "user" + " values (?,?,?,?,?,?,?,?,?)";
        int sucess = 0;
        try {
            sucess = runner.update(sql, u.getId(), u.getUserName(), u.getPassWord(), u.getGender(),
                    u.getEmail(), u.getDate(), u.getState(), u.getActiveCode(), u.getNickName());
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (sucess > 0) {
            return u;
        }
        return null;
    }

    public long find(String register) {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select count(*) from user where username = ?";
        long row = 0;
        try {
            row = (long) runner.query(sql, new ScalarHandler(), register);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return row;
    }
}
