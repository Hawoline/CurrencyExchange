package ru.hawoline.currencyexchange.data.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.hawoline.currencyexchange.data.dao.ExchangeRateDao;
import ru.hawoline.currencyexchange.domain.exception.CurrencyNotFoundException;
import ru.hawoline.currencyexchange.domain.exception.ExchangeRateNotFoundException;
import ru.hawoline.currencyexchange.domain.ExchangeRateParser;
import ru.hawoline.currencyexchange.domain.ExchangeRateId;
import ru.hawoline.currencyexchange.domain.dto.ExchangeRateDto;
import ru.hawoline.currencyexchange.domain.exception.RateNotFoundExceptionInRequestBody;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.stream.Collectors;

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
        try {
            sendResponse(response, exchangeRateDto);
        } catch (IOException e) {
            try {
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch (IOException _) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void doPatch(HttpServletRequest request, HttpServletResponse response) {
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

        ExchangeRateId exchangeRateId = new ExchangeRateId(baseCurrencyCode, targetCurrencyCode);
        ExchangeRateDto exchangeRateDtoBeforeUpdate;
        try {
            exchangeRateDtoBeforeUpdate = exchangeRateDao.getBy(exchangeRateId);
        } catch (CurrencyNotFoundException | ExchangeRateNotFoundException e) {
            try {
                String exchangeRateNotExistsMessage = "Exchange Rate with this Codes does not exists";
                response.sendError(HttpServletResponse.SC_NOT_FOUND, exchangeRateNotExistsMessage);
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            return;
        }
        double rate;
        try {
            ExchangeRateParser exchangeRateParser = new ExchangeRateParser();
            String requestUri = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
            rate = exchangeRateParser.parseRateFromRequestBody(requestUri);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        } catch (RateNotFoundExceptionInRequestBody e) {
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            return;
        }
        if (rate <= 0) {
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "rate <= 0");
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
            return;
        }
        ExchangeRateDto updatedExchangeRateDto = new ExchangeRateDto(
                exchangeRateDtoBeforeUpdate.getId(),
                exchangeRateDtoBeforeUpdate.getBaseCurrency(),
                exchangeRateDtoBeforeUpdate.getTargetCurrency(),
                rate
        );
        exchangeRateDao.update(updatedExchangeRateDto, exchangeRateId);

        try {
            sendResponse(response, updatedExchangeRateDto);
        } catch (IOException e) {
            try {
                e.printStackTrace();
                response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } catch (IOException _) {
            }
        }
    }

    private void sendResponse(HttpServletResponse response, ExchangeRateDto exchangeRateDto) throws IOException {
        PrintWriter out;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        out.write(exchangeRateDto.toString());
        out.close();
    }
}
