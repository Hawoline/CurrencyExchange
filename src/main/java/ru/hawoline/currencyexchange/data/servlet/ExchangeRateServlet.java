package ru.hawoline.currencyexchange.data.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.hawoline.currencyexchange.data.dao.ExchangeRateDao;
import ru.hawoline.currencyexchange.domain.CurrencyNotFoundException;
import ru.hawoline.currencyexchange.domain.ExchangeRateNotFoundException;
import ru.hawoline.currencyexchange.domain.dao.ExchangeRateId;
import ru.hawoline.currencyexchange.domain.dao.dto.ExchangeRateDto;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private ExchangeRateDao exchangeRateDao = new ExchangeRateDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String uri = request.getRequestURI();
        uri = uri.replaceAll("/exchangeRate/", "");
        if (uri.contains("/")) {
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        String baseCurrencyCode = uri.substring(0, 3);
        String targetCurrencyCode = uri.substring(3);

        PrintWriter out;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        ExchangeRateDto exchangeRateDto;
        try {
            exchangeRateDto = exchangeRateDao.getBy(new ExchangeRateId(baseCurrencyCode, targetCurrencyCode));
        } catch (CurrencyNotFoundException e) {
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            } catch (IOException ioException) {
                throw new RuntimeException(ioException);
            }
            return;
        } catch (ExchangeRateNotFoundException e) {
            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
            } catch (IOException ioException) {
                throw new RuntimeException(ioException);
            }
            return;
        }
        out.write(exchangeRateDto.toString());
        out.close();
    }

    @Override
    protected void doPatch(HttpServletRequest request, HttpServletResponse response) {

    }
}
