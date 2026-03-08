package ru.hawoline.currencyexchange.data.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.hawoline.currencyexchange.data.dao.CurrencyDao;
import ru.hawoline.currencyexchange.data.dao.ExchangeRateDao;
import ru.hawoline.currencyexchange.domain.entity.CurrencyPairEntity;
import ru.hawoline.currencyexchange.domain.ExchangeRateParser;
import ru.hawoline.currencyexchange.domain.dto.ExchangeRateDto;
import ru.hawoline.currencyexchange.domain.entity.CurrencyEntity;
import ru.hawoline.currencyexchange.domain.entity.ExchangeRateEntity;
import ru.hawoline.currencyexchange.domain.exception.CurrencyNotFoundException;
import ru.hawoline.currencyexchange.domain.exception.ExchangeRateNotFoundException;
import ru.hawoline.currencyexchange.domain.exception.RateNotFoundInRequestBodyException;
import ru.hawoline.currencyexchange.domain.ExchangeRateMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends CustomServlet {
    private ExchangeRateDao exchangeRateDao = new ExchangeRateDao();
    private CurrencyDao currencyDao = new CurrencyDao();
    private ExchangeRateMapper exchangeRateMapper = new ExchangeRateMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String uri = request.getRequestURI();
        addResponseHeaders(response);
        uri = uri.replaceAll("/exchangeRate/", "");
        if (uri.contains("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        String baseCurrencyCode = uri.substring(0, 3);
        String targetCurrencyCode = uri.substring(3);

        ExchangeRateEntity exchangeRateEntity;
        CurrencyEntity baseCurrencyEntity;
        CurrencyEntity targetCurrencyEntity;
        try {
            baseCurrencyEntity = currencyDao.getEntityBy(baseCurrencyCode);
            targetCurrencyEntity = currencyDao.getEntityBy(targetCurrencyCode);
            exchangeRateEntity = exchangeRateDao.getEntityBy(new CurrencyPairEntity(baseCurrencyEntity, targetCurrencyEntity));
        } catch (CurrencyNotFoundException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        } catch (ExchangeRateNotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
            return;
        }
        sendResponse(response, exchangeRateMapper.toExchangeRateDto(
                exchangeRateEntity,
                baseCurrencyEntity,
                targetCurrencyEntity)
        );
    }

    @Override
    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        addResponseHeaders(response);
        response.setHeader("Access-Control-Allow-Methods", "PATCH, OPTIONS");
        response.addHeader("Access-Control-Allow-Methods", "OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");
        String uri = request.getRequestURI();
        uri = uri.replaceAll("/exchangeRate/", "");
        if (uri.contains("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String baseCurrencyCode = uri.substring(0, 3);
        String targetCurrencyCode = uri.substring(3);

        CurrencyEntity baseCurrencyEntity;
        CurrencyEntity targetCurrencyEntity;
        try {
            baseCurrencyEntity = currencyDao.getEntityBy(baseCurrencyCode);
            targetCurrencyEntity = currencyDao.getEntityBy(targetCurrencyCode);
        } catch (CurrencyNotFoundException e) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "Base or Target Currency Code not found");
            return;
        }

        CurrencyPairEntity currencyPairEntity = new CurrencyPairEntity(baseCurrencyEntity, targetCurrencyEntity);
        ExchangeRateEntity exchangeRateEntityBeforeUpdate;
        try {
            exchangeRateEntityBeforeUpdate = exchangeRateDao.getEntityBy(currencyPairEntity);
        } catch (ExchangeRateNotFoundException e) {
            String exchangeRateNotExistsMessage = "Exchange Rate with this Codes does not exists";
            response.sendError(HttpServletResponse.SC_NOT_FOUND, exchangeRateNotExistsMessage);
            return;
        }
        ExchangeRateParser exchangeRateParser = new ExchangeRateParser();
        String requestUri = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        double rate;
        try {
            rate = exchangeRateParser.parseRateFromRequestBody(requestUri);
        } catch (RateNotFoundInRequestBodyException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            return;
        }
        if (rate <= 0) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "rate <= 0");
            return;
        }
        ExchangeRateEntity updatedExchangeRateEntity = new ExchangeRateEntity(
                exchangeRateEntityBeforeUpdate.id(),
                exchangeRateEntityBeforeUpdate.baseCurrency(),
                exchangeRateEntityBeforeUpdate.targetCurrency(),
                rate
        );
        exchangeRateDao.update(updatedExchangeRateEntity);

        sendResponse(response, exchangeRateMapper.toExchangeRateDto(updatedExchangeRateEntity,
                baseCurrencyEntity,
                targetCurrencyEntity)
        );
    }

    private void sendResponse(HttpServletResponse response, ExchangeRateDto exchangeRateDto) throws IOException {
        PrintWriter out;
        out = response.getWriter();
        out.write(exchangeRateDto.toString());
        out.close();
    }
}
