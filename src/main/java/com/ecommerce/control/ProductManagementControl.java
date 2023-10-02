package com.ecommerce.control;

import com.ecommerce.dao.CategoryDao;
import com.ecommerce.dao.ProductDao;
import com.ecommerce.entity.Account;
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

@WebServlet(name = "ProductManagementControl", value = "/product-management")
public class ProductManagementControl extends HttpServlet {
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
        // Get the seller id from session.
        HttpSession session = request.getSession();
        Account account = (Account) session.getAttribute("account");
        int sellerId = account.getId();
        // Get products of seller from database.
        List<Product> productList = productDao.getSellerProducts(sellerId);
        // Get all category for category selection.
        List<Category> categoryList = categoryDao.getAllCategories();

        request.setAttribute("category_list", categoryList);
        request.setAttribute("product_list", productList);
        // Set attribute active status for product management tab in header.
        request.setAttribute("product_management_active", "active");
        // Get request dispatcher and render to product-management page.
        RequestDispatcher requestDispatcher = request.getRequestDispatcher("product-management.jsp");
        requestDispatcher.forward(request, response);
    }
}
