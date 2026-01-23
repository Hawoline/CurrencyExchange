package ru.hawoline.currencyexchange.data.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.hawoline.currencyexchange.domain.ExchangeRateDtoValidator;
import ru.hawoline.currencyexchange.data.dao.CurrencyDao;
import ru.hawoline.currencyexchange.data.dao.ExchangeRateDao;
import ru.hawoline.currencyexchange.domain.exception.DuplicateValueInDbException;
import ru.hawoline.currencyexchange.domain.ExchangeRateParser;
import ru.hawoline.currencyexchange.domain.exception.ValueNotFoundException;
import ru.hawoline.currencyexchange.domain.dto.AddExchangeRateDto;
import ru.hawoline.currencyexchange.domain.dto.ExchangeRateDto;
import ru.hawoline.currencyexchange.domain.service.ExchangeRateService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private ExchangeRateService exchangeRateService = new ExchangeRateService(new ExchangeRateDao());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        ExchangeRateDao exchangeRateDao = new ExchangeRateDao();
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
        for (ExchangeRateDto exchangeRateResponse :
                exchangeRateEntities) {
            result.append(exchangeRateResponse.toString()).append(",");
        }
        result.append("]");
        out.write(result.toString());
        out.close();
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        ExchangeRateParser exchangeRateParser = new ExchangeRateParser();
        AddExchangeRateDto exchangeRateRequestBody;
        try {
            exchangeRateRequestBody = exchangeRateParser.parseRequestBody(request.getReader().lines().collect(Collectors.joining(System.lineSeparator())));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        boolean exchangeRateRequestBodyValid = new ExchangeRateDtoValidator().validate(exchangeRateRequestBody);
        if (!exchangeRateRequestBodyValid) {
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid request body:  " + exchangeRateRequestBody);
                return;
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        try {
            exchangeRateService.add(exchangeRateRequestBody);
        } catch (DuplicateValueInDbException e) {
            try {
                response.sendError(HttpServletResponse.SC_CONFLICT, "It is duplicate Exchange Rate");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            return;
        } catch (ValueNotFoundException e) {
            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "One or two currency codes doesnot exists in db");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
            return;
        }

        String exchangeRateResponseString = exchangeRateService.getLastAdded().toString();
        try (PrintWriter printWriter = response.getWriter()) {
            printWriter.write(exchangeRateResponseString);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
