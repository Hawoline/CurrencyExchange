package ru.hawoline.currencyexchange.data.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.hawoline.currencyexchange.data.dao.CurrencyDao;
import ru.hawoline.currencyexchange.data.dao.ExchangeRateDao;
import ru.hawoline.currencyexchange.domain.validator.ExchangeRateDtoValidator;
import ru.hawoline.currencyexchange.domain.ExchangeRateParser;
import ru.hawoline.currencyexchange.domain.dto.AddExchangeRateDto;
import ru.hawoline.currencyexchange.domain.dto.ExchangeRateDto;
import ru.hawoline.currencyexchange.domain.exception.DuplicateEntityException;
import ru.hawoline.currencyexchange.domain.exception.EntityNotFoundException;
import ru.hawoline.currencyexchange.domain.ExchangeRateService;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends CustomServlet {
    private ExchangeRateService exchangeRateService = new ExchangeRateService(new ExchangeRateDao(), new CurrencyDao());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        addResponseHeaders(response);
        PrintWriter out = null;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<ExchangeRateDto> exchangeRateEntities = exchangeRateService.getAll();
        StringBuilder result = new StringBuilder();
        result.append("[");
        String comma = ",";
        for (ExchangeRateDto exchangeRateResponse :
                exchangeRateEntities) {
            result.append(exchangeRateResponse.toString()).append(comma);
        }
        removeLastComma(result, comma);
        result.append("]");
        out.write(result.toString());
        out.close();
    }

    private void removeLastComma(StringBuilder result, String comma) {
        int lastIndexOfComma = result.lastIndexOf(comma);
        result.deleteCharAt(lastIndexOfComma);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        addResponseHeaders(response);
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
        PrintWriter printWriter = response.getWriter();
        try {
            exchangeRateService.add(exchangeRateRequestBody);
        } catch (DuplicateEntityException e) {
            sendError(response, HttpServletResponse.SC_CONFLICT, "It is duplicate Exchange Rate");
            printWriter.close();
            return;
        } catch (EntityNotFoundException e) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "One or two currency codes doesnot exists in db");
            printWriter.close();
            return;
        }

        try {
            String exchangeRateResponseString = exchangeRateService.getLastAdded().toString();
            printWriter.write(exchangeRateResponseString);
        } catch (EntityNotFoundException e) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "Exchange Rate not found after adding");
        } finally {
            printWriter.close();
        }
    }
}
