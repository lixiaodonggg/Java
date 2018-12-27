package main.product.web;


import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.beanutils.Converter;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import main.product.domain.LoginUser;
import main.product.domain.User;
import main.product.service.LoginService;
import main.product.service.RegisterService;
import main.product.utils.Utils;

public class UserServlet extends BaseServlet {
    public void login(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("utf-8");
        Map<String, Object> objectMap = request.getParameterMap();
        HttpSession session = request.getSession();
        //获取
        LoginUser loginUser = new LoginUser();
        try {
            BeanUtils.populate(loginUser, objectMap);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        LoginService loginService = new LoginService();
        loginUser.setPassWord(Utils.getMD5String(loginUser.getPassWord()));
        User user = loginService.getUser(loginUser);
        if (user != null) {
            //如果找到了用户，返回主页并且将user存在session中
            response.setContentType("text/html;charset=UTF-8");
            response.sendRedirect(request.getContextPath() + "/index.jsp");
            //将登录的用户的user对象存到session中
            session.setAttribute("user", user);
        } else {
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        }
    }


    public void register(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        request.setCharacterEncoding("utf-8");
        Map<String, Object> objectMap = request.getParameterMap();
        User register = new User();
        ConvertUtils.register(new Converter() {
            @Override
            public Object convert(Class aClass, Object o) {
                Date date = null;
                SimpleDateFormat s = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    date = s.parse(o.toString());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return date;
            }
        }, Date.class);
        try {
            //映射封装
            BeanUtils.populate(register, objectMap);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        RegisterService service = new RegisterService();
        String activeCode = UUID.randomUUID().toString();
        register.setId(UUID.randomUUID().toString());
        register.setPassWord(Utils.getMD5String(register.getPassWord()));
        register.setActiveCode(activeCode);
        User user = service.register(register);
        if (user != null) {
/*            //发送激活邮件
            String emailMsg = "恭喜您注册成功，请点击下面的连接进行激活账户" +
                    "<a href='http://localhost:8080/HeimaShop/active?activeCode=" + activeCode +
                    "'>" + "http://localhost:8080/HeimaShop/active?activeCode=" + activeCode +
                    "</a>";
            try {
                MailUtils.sendMail(user.getEmail(), emailMsg);
            } catch (MessagingException e) {
                e.printStackTrace();
            } catch (com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException e) {
                e.printStackTrace();
            }*/
            response.sendRedirect(request.getContextPath() + "/login.jsp");
        } else {
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }

    public void checkUserName(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        RegisterService service = new RegisterService();
        String userName = request.getParameter("userName");
        boolean success = service.findUser(userName);
        String json = "{\"isExist\":" + success + "}";
        response.getWriter().write(json);
    }

    public void exit(HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.getSession().removeAttribute("user");
        response.sendRedirect(request.getContextPath() + "index.jsp");
    }
}