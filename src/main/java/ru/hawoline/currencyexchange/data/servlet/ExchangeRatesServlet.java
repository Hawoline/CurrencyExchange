package ru.hawoline.currencyexchange.data.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.hawoline.currencyexchange.domain.ExchangeRateParser;
import ru.hawoline.currencyexchange.data.ExchangeRateRequestBodyValidator;
import ru.hawoline.currencyexchange.data.repository.storage.ExchangeRateDao;
import ru.hawoline.currencyexchange.data.repository.storage.ExchangeRateSqlDataSource;
import ru.hawoline.currencyexchange.domain.dao.Dao;
import ru.hawoline.currencyexchange.domain.entity.AddExchangeRateDto;
import ru.hawoline.currencyexchange.domain.ExchangeRatesService;
import ru.hawoline.currencyexchange.domain.entity.ExchangeRateDto;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private ExchangeRatesService exchangeRatesService = new ExchangeRatesService(new ExchangeRateSqlDataSource());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        Dao<ExchangeRateDto> exchangeRateDao = new ExchangeRateDao();
        response.setContentType("application/json");
        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<ExchangeRateDto> exchangeRateEntities = exchangeRateDao.getAll();
        StringBuilder result = new StringBuilder();
        result.append("[");
        for (ExchangeRateDto exchangeRateDto :
                exchangeRateEntities) {
            result.append(exchangeRateDto.toString()).append(",");
        }
        result.append("]");
        out.write(result.toString());
        out.close();
    }

    /**
     * Пример body:
     * baseCurrencyCode=AAA&targetCurrencyCode=AAB&rate=0.9
     * Пример ответа:
     * exchangeRateResponseEntity {
     *  "id": 0,
     *  "baseCurrency": {
     *   "id": 0,
     *   "name": "United States dollar",
     *   "code": "USD",
     *   "sign": "$"
     * },
     * "targetCurrency": {
     *  "id": 1,
     *  "name": "Euro",
     *  "code": "EUR",
     *  "sign": "€"
     * },
     * "rate": 0.99
     *}
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        ExchangeRateParser exchangeRateParser = new ExchangeRateParser();
        AddExchangeRateDto addExchangeRateDto;
        try {
            addExchangeRateDto = exchangeRateParser.parseRequestBody(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        boolean exchangeRateRequestBodyValid = new ExchangeRateRequestBodyValidator().validate(addExchangeRateDto);
        if (!exchangeRateRequestBodyValid) {
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request body:  " + addExchangeRateDto);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        String exchangeRateResponseString = exchangeRatesService.add(addExchangeRateDto).toString();
        try (PrintWriter printWriter = response.getWriter()) {
            printWriter.write(exchangeRateResponseString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
