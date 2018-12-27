package main.product.dao;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.SQLException;
import java.util.List;

import main.product.domain.Category;
import main.product.domain.Product;
import main.product.utils.DataSourceUtils;

public class ProductDao {

    public List<Product> findHotProduct() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from product where is_hot= ? limit ?, ?";
        return runner.query(sql, new BeanListHandler<>(Product.class), 1, 0, 9);
    }

    public List<Product> findNewProduct() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from product order by pdate desc limit ?, ? ";
        return runner.query(sql, new BeanListHandler<>(Product.class), 0, 9);
    }

    public List<Category> findCategoryList() throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from category";
        return runner.query(sql, new BeanListHandler<>(Category.class));
    }

    public int getCount(String cid) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select count(*) from product where cid=?";
        Long query = (Long) runner.query(sql, new ScalarHandler(), cid);
        return query.intValue();
    }

    public List<Product> findProductByPage(String cid, int index, int currentCount)
            throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from product where cid=? limit ?,?";
        List<Product> list =
                runner.query(sql, new BeanListHandler<>(Product.class), cid, index, currentCount);
        return list;
    }

    public Product findProductByPid(String pid) throws SQLException {
        QueryRunner runner = new QueryRunner(DataSourceUtils.getDataSource());
        String sql = "select * from product where pid=?";
        return runner.query(sql, new BeanHandler<>(Product.class), pid);
    }
}
