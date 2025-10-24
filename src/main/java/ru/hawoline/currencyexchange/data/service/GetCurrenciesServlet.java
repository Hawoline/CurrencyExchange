package ru.hawoline.currencyexchange.data.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.hawoline.currencyexchange.data.CurrenciesDao;
import ru.hawoline.currencyexchange.data.entity.CurrencyEntity;
import ru.hawoline.currencyexchange.domain.Currency;
import ru.hawoline.currencyexchange.domain.Dao;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/currencies")
public class GetCurrenciesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        Dao<CurrencyEntity> dao = new CurrenciesDao();
        List<CurrencyEntity> currencies = dao.getAll();
        StringBuilder result = new StringBuilder();
        result.append("[");
        for (CurrencyEntity c :
                currencies) {
            result.append("{\"id\": " + c.getId() + ",");
            result.append("\"name\": \"" + c.getName() + "\",");
            result.append("\"code\": \"" + c.getCode() + "\",");
            result.append("\"sign\": \"" + c.getSign() + "\"},");
        }
        result.append("]");
        out.write(result.toString());
        out.close();
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String test = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            int a = 0;
        }
    }
}
