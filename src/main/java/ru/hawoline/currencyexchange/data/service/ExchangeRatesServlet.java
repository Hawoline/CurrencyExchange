package ru.hawoline.currencyexchange.data.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.hawoline.currencyexchange.data.dao.ExchangeRatesDao;
import ru.hawoline.currencyexchange.data.entity.CurrencyEntity;
import ru.hawoline.currencyexchange.data.entity.ExchangeRateEntity;
import ru.hawoline.currencyexchange.domain.Dao;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private Dao<ExchangeRateEntity> exchangeRateDao = new ExchangeRatesDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        List<ExchangeRateEntity> exchangeRateEntities = exchangeRateDao.getAll();
        StringBuilder result = new StringBuilder();
        result.append("[");
        for (ExchangeRateEntity exchangeRateEntity :
                exchangeRateEntities) {
            result.append("{\"baseCurrency\": {");
            result.append("\t\"id\": \"").append(exchangeRateEntity.getBaseCurrency().getId()).append("\",");
            result.append("\t\"name\": \"").append(exchangeRateEntity.getBaseCurrency().getName()).append("\",");
            result.append("\t\"code\": \"").append(exchangeRateEntity.getBaseCurrency().getCode()).append("\",");
            result.append("\t\"sign\": \"").append(exchangeRateEntity.getBaseCurrency().getSign()).append("\"},");
            result.append("\"targetCurrency\": {");
            result.append("\t\"id\": \"").append(exchangeRateEntity.getTargetCurrency().getId()).append("\",");
            result.append("\t\"name\": \"").append(exchangeRateEntity.getTargetCurrency().getName()).append("\",");
            result.append("\t\"code\": \"").append(exchangeRateEntity.getTargetCurrency().getCode()).append("\",");
            result.append("\t\"sign\": \"").append(exchangeRateEntity.getTargetCurrency().getSign()).append("\"},");
            result.append("\"rate\": \"").append(exchangeRateEntity.getRate()).append("\"},");
        }
        result.append("]");
        out.write(result.toString());
        out.close();
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }
}
