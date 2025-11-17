package ru.hawoline.currencyexchange.data.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.hawoline.currencyexchange.data.dao.ExchangeRateDao;
import ru.hawoline.currencyexchange.data.entity.ExchangeRateResponseEntity;
import ru.hawoline.currencyexchange.domain.Dao;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private Dao<ExchangeRateResponseEntity> exchangeRateDao = new ExchangeRateDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        List<ExchangeRateResponseEntity> exchangeRateEntities = exchangeRateDao.getAll();
        StringBuilder result = new StringBuilder();
        result.append("[");
        for (ExchangeRateResponseEntity exchangeRateResponseEntity :
                exchangeRateEntities) {
            result.append(exchangeRateResponseEntity.toJson()).append(",");
        }
        result.append("]");
        out.write(result.toString());
        out.close();
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
