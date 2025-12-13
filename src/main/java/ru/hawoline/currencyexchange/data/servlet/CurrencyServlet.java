package ru.hawoline.currencyexchange.data.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.hawoline.currencyexchange.data.repository.CurrencyDao;
import ru.hawoline.currencyexchange.domain.entity.CurrencyEntity;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(value = "/currency/*")
public class CurrencyServlet extends HttpServlet {
    private CurrencyDao dao = new CurrencyDao();


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();
        uri = uri.replaceAll("/currency/", "");
        if (uri.contains("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (!dao.exists(uri)) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        CurrencyEntity currency = dao.get(uri);
        PrintWriter out = response.getWriter();
        out.write(currency.toString());
    }
}
