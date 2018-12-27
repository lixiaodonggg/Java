package main.product.service;


import java.sql.SQLException;
import java.util.List;

import main.product.dao.ProductDao;
import main.product.domain.Category;
import main.product.domain.PageBean;
import main.product.domain.Product;

public class ProductService {
    public List<Product> findHotProduct() {
        List<Product> products = null;
        ProductDao dao = new ProductDao();
        try {
            products = dao.findHotProduct();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public List<Product> findNewProduct() {
        ProductDao dao = new ProductDao();
        List<Product> products = null;
        try {
            products = dao.findNewProduct();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public List<Category> findCategoryList() {
        ProductDao dao = new ProductDao();
        List<Category> category = null;
        try {
            category = dao.findCategoryList();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return category;
    }

    public PageBean findProductListByCid(String cid, int currentPage, int currentCount) {

        ProductDao dao = new ProductDao();

        //封装一个PageBean 返回web层
        PageBean<Product> pageBean = new PageBean<>();

        //1、封装当前页
        pageBean.setCurrentPage(currentPage);
        //2、封装每页显示的条数
        pageBean.setCurrentCount(currentCount);
        //3、封装总条数
        int totalCount = 0;
        try {
            totalCount = dao.getCount(cid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        pageBean.setTotalCount(totalCount);
        //4、封装总页数
        int totalPage = (int) Math.ceil(1.0 * totalCount / currentCount);
        pageBean.setTotalPage(totalPage);

        //5、当前页显示的数据
        // select * from product where cid=? limit ?,?
        // 当前页与起始索引index的关系
        int index = (currentPage - 1) * currentCount;
        List<Product> list = null;
        try {
            list = dao.findProductByPage(cid, index, currentCount);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        pageBean.setList(list);
        return pageBean;
    }

    public Product findProductByPid(String pid) {
        ProductDao dao = new ProductDao();
        Product product = null;
        try {
            product = dao.findProductByPid(pid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return product;
    }
}
