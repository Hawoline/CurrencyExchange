package ru.hawoline.currencyexchange.data.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.hawoline.currencyexchange.data.ExchangeRateParser;
import ru.hawoline.currencyexchange.data.ExchangeRateRequestBodyValidator;
import ru.hawoline.currencyexchange.data.repository.ExchangeRateDao;
import ru.hawoline.currencyexchange.data.repository.storage.ExchangeRateSqlDataSource;
import ru.hawoline.currencyexchange.domain.Dao;
import ru.hawoline.currencyexchange.domain.entity.ExchangeRateRequestBody;
import ru.hawoline.currencyexchange.data.repository.ExchangeRateRepository;
import ru.hawoline.currencyexchange.domain.entity.ExchangeRateResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private ExchangeRateRepository exchangeRateRepository = new ExchangeRateRepository(new ExchangeRateSqlDataSource());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        Dao<ExchangeRateResponse> exchangeRateDao = new ExchangeRateDao();
        response.setContentType("application/json");
        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<ExchangeRateResponse> exchangeRateEntities = exchangeRateDao.getAll();
        StringBuilder result = new StringBuilder();
        result.append("[");
        for (ExchangeRateResponse exchangeRateResponse :
                exchangeRateEntities) {
            result.append(exchangeRateResponse.toString()).append(",");
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
     *
     * TODO реализовать все эти методыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыыы
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        ExchangeRateParser exchangeRateParser = new ExchangeRateParser();
        ExchangeRateRequestBody exchangeRateRequestBody;
        try {
            exchangeRateRequestBody = exchangeRateParser.parseRequestBody(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        boolean exchangeRateRequestBodyValid = new ExchangeRateRequestBodyValidator().validate(exchangeRateRequestBody);
        if (!exchangeRateRequestBodyValid) {
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request body:  " + exchangeRateRequestBody);
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String exchangeRateResponseString = exchangeRateRepository.add(exchangeRateRequestBody).toString();
        if (exchangeRateResponseString.equals(ExchangeRateRepository.EXCHANGE_RATE_ALREADY_SAVED)) {
            try {
                response.sendError(HttpServletResponse.SC_CONFLICT);
            } catch (IOException e) {
                throw new RuntimeException(e); // Для отладки, TODO потом нормально обработать
            }
            return;
        }
        if (exchangeRateResponseString.equals(ExchangeRateRepository.ONE_OR_MORE_RATE_NOT_EXISTS)) {
            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
            } catch (IOException e) {
                throw new RuntimeException(e); // Для отладки, TODO потом нормально обработать
            }
            return;
        }
        try (PrintWriter printWriter = response.getWriter()) {
            printWriter.write(exchangeRateResponseString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
