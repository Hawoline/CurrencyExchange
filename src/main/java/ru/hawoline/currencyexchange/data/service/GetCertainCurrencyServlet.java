package ru.hawoline.currencyexchange.data.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.hawoline.currencyexchange.data.CurrenciesDao;
import ru.hawoline.currencyexchange.data.entity.CurrencyEntity;
import ru.hawoline.currencyexchange.domain.Dao;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(value = "/currency/*")
public class GetCertainCurrencyServlet extends HttpServlet {
    private Dao<CurrencyEntity, String> dao = new CurrenciesDao();


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();
        uri = uri.replaceAll("/currency/", "");
        uri = uri.replaceAll("currency/", "");
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
        StringBuilder result = new StringBuilder();
        result.append("{\"id\": " + currency.getId() + ",");
        result.append("\"name\": \"" + currency.getName() + "\",");
        result.append("\"code\": \"" + currency.getCode() + "\",");
        result.append("\"sign\": \"" + currency.getSign() + "\"}");
        out.write(result.toString());
    }
}
