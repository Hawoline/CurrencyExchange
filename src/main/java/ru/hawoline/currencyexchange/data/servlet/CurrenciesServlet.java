package ru.hawoline.currencyexchange.data.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.hawoline.currencyexchange.domain.dto.ErrorMessageDto;
import ru.hawoline.currencyexchange.domain.CurrencyMapper;
import ru.hawoline.currencyexchange.data.dao.CurrencyDao;
import ru.hawoline.currencyexchange.domain.entity.CurrencyEntity;
import ru.hawoline.currencyexchange.domain.exception.DuplicateEntityException;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private CurrencyDao currencyDao = new CurrencyDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:63342");
        response.setContentType("application/json");
        PrintWriter out;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<CurrencyEntity> currencies = currencyDao.getAll();

        StringBuilder result = toJsonString(currencies);
        out.write(result.toString());
        out.close();
    }

    private StringBuilder toJsonString(List<CurrencyEntity> currencies) {
        StringBuilder result = new StringBuilder();
        result.append("[");
        String comma = ",";
        for (CurrencyEntity currencyEntity :
                currencies) {
            result.append(currencyEntity.toString()).append(comma);
        }
        removeLastComma(result, comma);

        result.append("]");
        return result;
    }

    private void removeLastComma(StringBuilder result, String comma) {
        int lastIndexOfComma = result.lastIndexOf(comma);
        result.deleteCharAt(lastIndexOfComma);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String currencyRequestString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        CurrencyEntity currencyEntity = new CurrencyMapper().fromXWwwFormUrlEncoded(currencyRequestString);
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8);
        PrintWriter printWriter = response.getWriter();
        if (currencyEntity.getSign().isEmpty()
                || currencyEntity.getName().isEmpty()
                || currencyEntity.getCode().isEmpty()) {
            int errorCode = HttpServletResponse.SC_BAD_REQUEST;
            String errorMessage = "Некоторые поля пустые";
            sendError(response, errorCode, errorMessage);
            printWriter.close();
            return;
        }
        try {
            CurrencyEntity createdCurrencyEntity = currencyDao.create(currencyEntity);
            response.setStatus(HttpServletResponse.SC_CREATED);
            printWriter.write(createdCurrencyEntity.toString());
        } catch (DuplicateEntityException e) {
            int errorCode = HttpServletResponse.SC_CONFLICT;
            String errorMessage = "Такая валюта уже существует в базе данных";
            sendError(response, errorCode, errorMessage);
        } finally {
            printWriter.close();
        }
    }

    public void sendError(HttpServletResponse response, int httpErrorCode, String errorMessage) throws IOException {
        response.setStatus(httpErrorCode);
        ErrorMessageDto errorMessageDto = new ErrorMessageDto(errorMessage);
        response.getWriter().write(errorMessageDto.toString());
    }


}
