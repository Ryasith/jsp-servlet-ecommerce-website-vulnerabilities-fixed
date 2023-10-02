package com.ecommerce.control;

import com.ecommerce.dao.CategoryDao;
import com.ecommerce.dao.ProductDao;
import com.ecommerce.entity.Category;
import com.ecommerce.entity.Product;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

@WebServlet(name = "SearchControl", value = "/search")
public class SearchControl extends HttpServlet {
    // Call DAO class to access with database.
    ProductDao productDao = new ProductDao();
    CategoryDao categoryDao = new CategoryDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setHeader("X-Frame-Options", "DENY");
        // Set the Content-Type header
        response.setContentType("text/html;charset=UTF-8");

        // Set the X-Content-Type-Options header to prevent MIME-sniffing
        response.setHeader("X-Content-Type-Options", "nosniff");
        HttpSession session = request.getSession();
        String keyword = "";
        // Get the search keyword from request.
        if(Objects.equals(request.getParameter("csrf_token"), session.getAttribute("csrf_token"))) {
            keyword = request.getParameter("keyword");
        }
        // Get all products with the given keyword from database.
        List<Product> productList = productDao.searchProduct(keyword);
        // Get all categories from database.
        List<Category> categoryList = categoryDao.getAllCategories();

        request.setAttribute("product_list", productList);
        request.setAttribute("category_list", categoryList);
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("shop.jsp");
        requestDispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
