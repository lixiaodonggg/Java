package main.product.web;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class BaseServlet extends HttpServlet {

    protected void service(HttpServletRequest request, HttpServletResponse response) {
        response.setCharacterEncoding("utf-8");
        String methodName = request.getParameter("method");
        Class clazz = this.getClass();
        try {
            Method method = clazz.getMethod(methodName, HttpServletRequest.class,
                    HttpServletResponse.class);
            method.invoke(this, request, response);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
