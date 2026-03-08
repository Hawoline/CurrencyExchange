package ru.hawoline.currencyexchange.data.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.hawoline.currencyexchange.domain.CurrencyMapper;
import ru.hawoline.currencyexchange.data.dao.CurrencyDao;
import ru.hawoline.currencyexchange.domain.entity.CurrencyEntity;
import ru.hawoline.currencyexchange.domain.exception.DuplicateEntityException;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends CustomServlet {
    private CurrencyDao currencyDao = new CurrencyDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) {
        addResponseHeaders(response);
        PrintWriter out;
        try {
            out = response.getWriter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<CurrencyEntity> currencies = currencyDao.getAll();

        String result = toJsonString(currencies);
        out.write(result);
        out.close();
    }

    private String toJsonString(List<CurrencyEntity> currencies) {
        StringBuilder jsonString = new StringBuilder();
        String startSquareBracket = "[";
        jsonString.append(startSquareBracket);
        String comma = ",";
        for (CurrencyEntity currencyEntity :
                currencies) {
            jsonString.append(currencyEntity.toString()).append(comma);
        }
        removeLastComma(jsonString);

        String endSquareBracket = "]";
        jsonString.append(endSquareBracket);
        return jsonString.toString();
    }

    private void removeLastComma(StringBuilder string) {
        String comma = ",";
        int lastIndexOfComma = string.lastIndexOf(comma);
        string.deleteCharAt(lastIndexOfComma);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        addResponseHeaders(response);
        PrintWriter printWriter = response.getWriter();
        try {
            CurrencyEntity currencyEntity = new CurrencyMapper().getCurrencyEntityFrom(request.getParameterMap());
            CurrencyEntity createdCurrencyEntity = currencyDao.create(currencyEntity);
            response.setStatus(HttpServletResponse.SC_CREATED);
            printWriter.write(createdCurrencyEntity.toString());
        } catch (IllegalArgumentException exception) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, exception.getMessage());
        } catch (DuplicateEntityException e) {
            sendError(response, HttpServletResponse.SC_CONFLICT, e.getMessage());
        }  finally {
            printWriter.close();
        }
    }
}
