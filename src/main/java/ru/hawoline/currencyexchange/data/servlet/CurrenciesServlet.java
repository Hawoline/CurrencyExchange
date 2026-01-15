package ru.hawoline.currencyexchange.data.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.hawoline.currencyexchange.data.CurrencyMapper;
import ru.hawoline.currencyexchange.data.dao.CurrencyDao;
import ru.hawoline.currencyexchange.domain.dto.CurrencyDto;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private CurrencyDao currencyDao = new CurrencyDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json");
        PrintWriter out;
        try {
            out = resp.getWriter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<CurrencyDto> currencies = currencyDao.getAll();

        StringBuilder result = toJsonString(currencies);
        out.write(result.toString());
        out.close();
    }

    private StringBuilder toJsonString(List<CurrencyDto> currencies) {
        StringBuilder result = new StringBuilder();
        result.append("[");
        for (CurrencyDto currencyDto :
                currencies) {
            result.append(currencyDto.toString()).append(",");
        }
        // TODO убрать последнюю запятую
        result.append("]");
        return result;
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String currencyRequestString;
        try {
            currencyRequestString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        CurrencyDto currencyDto = new CurrencyMapper().fromXWwwFormUrlEncoded(currencyRequestString);
        if (currencyDto.getSign().isEmpty()
                || currencyDto.getName().isEmpty()
                || currencyDto.getCode().isEmpty()) {
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        if (checkCurrencyCodeExistsInDb(response, currencyDto)) return;
        currencyDao.save(currencyDto);
    }

    private boolean checkCurrencyCodeExistsInDb(HttpServletResponse response, CurrencyDto currencyDto) {
        if (currencyDao.exists(currencyDto.getCode())) {
            try {
                response.sendError(HttpServletResponse.SC_CONFLICT, "Currency with this code exists");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return true;
        }
        return false;
    }
}
