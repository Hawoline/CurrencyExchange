package ru.hawoline.currencyexchange.data.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.hawoline.currencyexchange.data.dao.CurrencyDao;
import ru.hawoline.currencyexchange.data.dao.ExchangeRateDao;
import ru.hawoline.currencyexchange.domain.ExchangeRateParser;
import ru.hawoline.currencyexchange.domain.dto.ConvertedExchangeRateDto;
import ru.hawoline.currencyexchange.domain.dto.ErrorMessageDto;
import ru.hawoline.currencyexchange.domain.dto.ExchangeDto;
import ru.hawoline.currencyexchange.domain.exception.EntityNotFoundException;
import ru.hawoline.currencyexchange.domain.ExchangeRateService;
import ru.hawoline.currencyexchange.domain.validator.ExchangeDtoValidator;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = new ExchangeRateService(new ExchangeRateDao(), new CurrencyDao());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String queryString = request.getQueryString();
        ExchangeRateParser exchangeRateParser = new ExchangeRateParser();
        ExchangeDto exchangeDto = exchangeRateParser.parseExchangeQueryString(queryString);
        PrintWriter responseWriter = response.getWriter();
        try {
            ExchangeDtoValidator exchangeDtoValidator = new ExchangeDtoValidator();
            boolean isValid = exchangeDtoValidator.validate(exchangeDto);
            if (!isValid) {
                sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Query String is bad");
                return;
            }
            ConvertedExchangeRateDto converted = exchangeRateService.convert(exchangeDto);
            sendResponse(responseWriter, converted);
        } catch (EntityNotFoundException e) {
            String errorMessage = "Base currency or target currency or exchange rate with these currencies not found";
            sendError(response, HttpServletResponse.SC_NOT_FOUND, errorMessage);
        } finally {
            responseWriter.close();
        }
    }

    private void sendError(HttpServletResponse response, int httpErrorCode, String errorMessage) throws IOException {
        response.setStatus(httpErrorCode);
        ErrorMessageDto errorMessageDto = new ErrorMessageDto(errorMessage);
        response.getWriter().write(errorMessageDto.toString());
    }

    private void sendResponse(PrintWriter responseWriter, ConvertedExchangeRateDto response) {
        responseWriter.write(response.toString());
        responseWriter.flush();
    }
}
