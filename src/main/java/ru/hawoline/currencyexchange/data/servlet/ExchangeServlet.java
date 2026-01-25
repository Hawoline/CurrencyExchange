package ru.hawoline.currencyexchange.data.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import ru.hawoline.currencyexchange.data.dao.CurrencyDao;
import ru.hawoline.currencyexchange.data.dao.ExchangeRateDao;
import ru.hawoline.currencyexchange.domain.ExchangeDtoValidator;
import ru.hawoline.currencyexchange.domain.ExchangeRateParser;
import ru.hawoline.currencyexchange.domain.dto.ConvertedExchangeRateDto;
import ru.hawoline.currencyexchange.domain.dto.ExchangeDto;
import ru.hawoline.currencyexchange.domain.exception.ExchangeRateNotFoundException;
import ru.hawoline.currencyexchange.domain.service.ExchangeRateService;

import java.io.IOException;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private ExchangeRateService exchangeRateService = new ExchangeRateService(new ExchangeRateDao(), new CurrencyDao());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        String queryString = request.getQueryString();
        ExchangeRateParser exchangeRateParser = new ExchangeRateParser();
        ExchangeDto exchangeDto = exchangeRateParser.parseExchangeQueryString(queryString);

        ExchangeDtoValidator exchangeDtoValidator = new ExchangeDtoValidator();
        boolean isValid = exchangeDtoValidator.validate(exchangeDto);
        if (!isValid) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", "Query String is bad");
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, jsonObject.toString());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        try {
            ConvertedExchangeRateDto convertedExchangeRateDto = exchangeRateService.convert(exchangeDto);
        } catch (ExchangeRateNotFoundException e) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("message", "Base currency or target currency or exchange rate with these currencies not found");
            try {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, jsonObject.toString());
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
}
