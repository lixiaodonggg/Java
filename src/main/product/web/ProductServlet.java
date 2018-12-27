package main.product.web;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.mail.Session;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import main.product.domain.Cart;
import main.product.domain.CartItem;
import main.product.domain.Category;
import main.product.domain.PageBean;
import main.product.domain.Product;
import main.product.service.ProductService;
import main.product.utils.JedisPoolUtils;
import redis.clients.jedis.Jedis;


public class ProductServlet extends BaseServlet {


    public void categoryList(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        //先从缓存中查询，如果有就返回，没有就从数据库中查询。
        ProductService service = new ProductService();
        Jedis jedis = JedisPoolUtils.getJedis();
        List<Category> categories;
        String json = jedis.get("categories");
        if (json == null) {
            System.out.println("缓存中没有数据");
            categories = service.findCategoryList();
            Gson gson = new Gson();
            json = gson.toJson(categories);
            jedis.set("categories", json);
        }
        response.setContentType("text/html;charset=UTF-8");
        response.getWriter().write(json);
    }

    public void index(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        ProductService service = new ProductService();
        List<Product> hotProducts = service.findHotProduct();
        List<Product> newProducts = service.findNewProduct();
        request.setAttribute("hotProducts", hotProducts);
        request.setAttribute("newProducts", newProducts);
        request.getRequestDispatcher(request.getContextPath() + "/index.jsp")
                .forward(request, response);
    }

    public void productInfo(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String cid = request.getParameter("cid");
        String currentPage = request.getParameter("currentPage");
        String pid = request.getParameter("pid");
        ProductService service = new ProductService();
        Product pro = service.findProductByPid(pid);
        request.setAttribute("product", pro);
        request.setAttribute("currentPage", currentPage);
        request.setAttribute("cid", cid);
        //创建cook之前先获得客户端携带的cookie

        Cookie[] cookies = request.getCookies();
        String pids = pid;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("pids".equalsIgnoreCase(cookie.getName())) {
                    pids = cookie.getValue();
                    String pidss[] = pids.split("%");
                    List<String> list = new LinkedList<>(Arrays.asList(pidss));
                    if (list.contains(pid)) {
                        list.remove(pid);
                    }
                    ((LinkedList<String>) list).addFirst(pid);
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int i = 0; i < list.size(); i++) {
                        if (i >= list.size() - 1) {
                            stringBuffer.append(list.get(i));
                        } else {
                            stringBuffer.append(list.get(i)).append("%");
                        }
                    }
                    pids = stringBuffer.toString();
                }
            }
        }
        Cookie cookiePids = new Cookie("pids", pids);
        response.addCookie(cookiePids);
        try {
            request.getRequestDispatcher(request.getContextPath() + "/product_info.jsp")
                    .forward(request, response);
        } catch (ServletException e) {
            e.printStackTrace();
        }
    }

    public void productListByCId(HttpServletRequest request, HttpServletResponse response) {
        String cid = request.getParameter("cid");
        String currentPageStr = request.getParameter("currentPage");
        if (currentPageStr == null) {
            currentPageStr = "1";
        }
        int currentPage = Integer.parseInt(currentPageStr);
        int currentCount = 2; //当前页的容量
        ProductService service = new ProductService();
        PageBean pageBean = service.findProductListByCid(cid, currentPage, currentCount);
        request.setAttribute("pageBean", pageBean);
        request.setAttribute("cid", cid);

        //定义一个记录历史商品信息的集合
        List<Product> historyProductList = new ArrayList<>();
        //获得客户端携带名字叫pids的cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("pids".equals(cookie.getName())) {
                    String pids = cookie.getValue();//3-2-1
                    String[] split = pids.split("%");
                    for (String pid : split) {
                        Product pro = service.findProductByPid(pid);
                        historyProductList.add(pro);
                    }
                }
            }
        }
        //将历史记录的集合放到域中
        request.setAttribute("historyProductList", historyProductList);
        try {
            request.getRequestDispatcher("/product_list.jsp").forward(request, response);
        } catch (ServletException | IOException e) {
            e.printStackTrace();
        }
    }

    public void addProduct2Cart(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        String buyNum = request.getParameter("buyNum");
        int num = 0;
        if (buyNum != null) {
            num = Integer.parseInt(buyNum);
        }
        String pid = request.getParameter("pid");
        HttpSession session = request.getSession();
        Product product = new ProductService().findProductByPid(pid);
        CartItem item = new CartItem();
        item.setProduct(product);
        item.setBuyNum(num);
        double newPrice = num * product.getShop_price();
        item.setSubtotal(newPrice);
        Cart cart = (Cart) session.getAttribute("cart");
        if (cart == null) {
            cart = new Cart();
        }
        Map<String, CartItem> cartItems = cart.getCartItems();
        double totalPrice = newPrice;
        if (cartItems.containsKey(pid)) {
            CartItem newItem = cartItems.get(pid);
            newItem.setBuyNum(newItem.getBuyNum() + item.getBuyNum());
            totalPrice = newItem.getSubtotal() + item.getSubtotal();
            newItem.setSubtotal(totalPrice);
            cart.setCartItems(cartItems);
        } else {
            cartItems.put(pid, item);
            cart.setCartItems(cartItems);
        }
        cart.setTotal(cart.getTotal() + totalPrice);
        session.setAttribute("cart", cart);
        response.sendRedirect(request.getContextPath() + "/cart.jsp");
    }

    public void clearCart(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getSession().removeAttribute("cart");
        response.sendRedirect(request.getContextPath() + "/cart.jsp");
    }

    public void deleteItem(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Cart cart = (Cart) request.getSession().getAttribute("cart");
        String pid = request.getParameter("pid");
        if (cart != null) {
            Map<String, CartItem> items = cart.getCartItems();
            cart.setTotal(cart.getTotal() - cart.getCartItems().get(pid).getSubtotal());
            items.remove(pid);
        }

        request.getSession().setAttribute("cart", cart);
        response.sendRedirect(request.getContextPath() + "/cart.jsp");
    }

}