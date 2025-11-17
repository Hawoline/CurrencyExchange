package ru.hawoline.currencyexchange.data.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.hawoline.currencyexchange.data.dao.CurrencyDao;
import ru.hawoline.currencyexchange.data.dao.ExchangeRateDao;
import ru.hawoline.currencyexchange.data.entity.ExchangeRateResponseEntity;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private ExchangeRateDao  exchangeRateDao = new ExchangeRateDao();
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();
        uri = uri.replaceAll("/exchangeRate/", "");
        if (uri.contains("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String baseCurrencyCode = uri.substring(0, 3);
        String targetCurrencyCode = uri.substring(3);

        boolean baseCurrencyFound = new CurrencyDao().exists(baseCurrencyCode);
        boolean targetCurrencyFound = new CurrencyDao().exists(targetCurrencyCode);
        PrintWriter out = response.getWriter();
        if (!(baseCurrencyFound && targetCurrencyFound)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            out.close();
            return;
        }
        boolean exchangeRatesFound =  exchangeRateDao.exists(baseCurrencyCode, targetCurrencyCode);
        if (!exchangeRatesFound) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            out.close();
            return;
        }
        ExchangeRateResponseEntity exchangeRateResponseEntity = exchangeRateDao.get(baseCurrencyCode, targetCurrencyCode);
        out.write(exchangeRateResponseEntity.toJson());
        out.close();
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

    }
}
