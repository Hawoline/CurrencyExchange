package ru.hawoline.currencyexchange.data.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.hawoline.currencyexchange.data.dao.CurrencyDao;
import ru.hawoline.currencyexchange.data.dao.ExchangeRateDao;
import ru.hawoline.currencyexchange.domain.ExchangeRateMapper;
import ru.hawoline.currencyexchange.domain.dto.ExchangeRateDto;
import ru.hawoline.currencyexchange.domain.entity.CurrencyEntity;
import ru.hawoline.currencyexchange.domain.entity.CurrencyPairEntity;
import ru.hawoline.currencyexchange.domain.entity.ExchangeRateEntity;
import ru.hawoline.currencyexchange.domain.exception.CurrencyNotFoundException;
import ru.hawoline.currencyexchange.domain.exception.EntityNotFoundException;
import ru.hawoline.currencyexchange.domain.exception.ExchangeRateNotFoundException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends CustomServlet {
    private final ExchangeRateDao exchangeRateDao = new ExchangeRateDao();
    private final CurrencyDao currencyDao = new CurrencyDao();
    private final ExchangeRateMapper exchangeRateMapper = new ExchangeRateMapper();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter responseWriter = response.getWriter();
        try {
            ExchangeRateEntity exchangeRateEntity = getExchangeRateEntity(request, response);
            sendResponse(responseWriter, exchangeRateMapper.toExchangeRateDto(exchangeRateEntity));
        } catch (CurrencyNotFoundException | IllegalArgumentException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (ExchangeRateNotFoundException e) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
        responseWriter.close();
    }

    @Override
    protected void doOptions(HttpServletRequest request, HttpServletResponse response) throws IOException {
        addResponseHeaders(response);
        response.setHeader("Access-Control-Allow-Methods", "OPTIONS, PATCH");
        response.setHeader("Access-Control-Allow-Headers", "Content-Type");

        response.getWriter().write("");
    }

    @Override
    protected void doPatch(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            ExchangeRateEntity exchangeRateEntityBeforeUpdate = getExchangeRateEntity(request, response);
            String rateQueryString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            double rate = parseRate(rateQueryString);
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
            sendResponse(response.getWriter(), exchangeRateMapper.toExchangeRateDto(updatedExchangeRateEntity));
        } catch (EntityNotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } catch (NumberFormatException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (IndexOutOfBoundsException e) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "Exchange Rate not found for update.");
        }
    }

    private ExchangeRateEntity getExchangeRateEntity(HttpServletRequest request, HttpServletResponse response)
            throws CurrencyNotFoundException, ExchangeRateNotFoundException {
        addResponseHeaders(response);
        String pathInfo = request.getPathInfo();
        String currencyCodePair = pathInfo.replace("/", "");
        if (currencyCodePair.length() != 6) {
            throw new IllegalArgumentException("Currency code pair length has not 6 characters");
        }
        String baseCurrencyCode = currencyCodePair.substring(0, 3);
        String targetCurrencyCode = currencyCodePair.substring(3);
        CurrencyEntity baseCurrencyEntity = currencyDao.getEntityBy(baseCurrencyCode);
        CurrencyEntity targetCurrencyEntity = currencyDao.getEntityBy(targetCurrencyCode);
        return exchangeRateDao.getEntityBy(new CurrencyPairEntity(baseCurrencyEntity, targetCurrencyEntity));
    }

    private double parseRate(String rateQueryString) throws NumberFormatException {
        String[] pair = rateQueryString.split("=");
        if (pair.length > 1) {
            return Double.parseDouble(pair[1]);
        }

        return -1;
    }

    private void sendResponse(PrintWriter out, ExchangeRateDto exchangeRateDto) {
        out.write(exchangeRateDto.toString());
        out.close();
    }
}
