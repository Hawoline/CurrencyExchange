package ru.hawoline.currencyexchange.data.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.hawoline.currencyexchange.data.dao.CurrencyDao;
import ru.hawoline.currencyexchange.data.dao.ExchangeRateDao;
import ru.hawoline.currencyexchange.domain.ExchangeRateParser;
import ru.hawoline.currencyexchange.domain.ExchangeRateService;
import ru.hawoline.currencyexchange.domain.dto.AddExchangeRateDto;
import ru.hawoline.currencyexchange.domain.dto.ExchangeRateDto;
import ru.hawoline.currencyexchange.domain.exception.DuplicateEntityException;
import ru.hawoline.currencyexchange.domain.exception.EntityNotFoundException;
import ru.hawoline.currencyexchange.domain.validator.ExchangeRateDtoValidator;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends CustomServlet {
    private final ExchangeRateService exchangeRateService = new ExchangeRateService(new ExchangeRateDao(), new CurrencyDao());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        addResponseHeaders(response);
        PrintWriter out = response.getWriter();
        List<ExchangeRateDto> exchangeRateEntities = exchangeRateService.getAll();
        StringBuilder result = new StringBuilder();
        result.append("[");
        String comma = ",";
        for (ExchangeRateDto exchangeRateResponse :
                exchangeRateEntities) {
            result.append(exchangeRateResponse).append(comma);
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
        Map<String, String[]> parameterMap = request.getParameterMap();
        PrintWriter responseWriter = response.getWriter();
        try {
            AddExchangeRateDto exchangeRateRequestBody = exchangeRateParser.parseAddExchangeRateFrom(parameterMap);
            boolean exchangeRateRequestBodyValid = new ExchangeRateDtoValidator().validate(exchangeRateRequestBody);
            if (exchangeRateRequestBodyValid) {
                String exchangeRateResponseString = exchangeRateService.add(exchangeRateRequestBody).toString();
                responseWriter.write(exchangeRateResponseString);
            } else {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Invalid request body:  " + exchangeRateRequestBody);
            }
        } catch (NumberFormatException e) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "rate empty or is not type double");
        } catch (DuplicateEntityException e) {
            sendError(response, HttpServletResponse.SC_CONFLICT, "It is duplicate Exchange Rate");
        } catch (EntityNotFoundException e) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, "One or two currency codes does not exists in db");
        }

        responseWriter.close();
    }
}
