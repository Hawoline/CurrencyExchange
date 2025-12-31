package ru.hawoline.currencyexchange.data.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.hawoline.currencyexchange.data.dao.CurrencyDao;
import ru.hawoline.currencyexchange.domain.dao.entity.CurrencyEntity;
import ru.hawoline.currencyexchange.data.CurrencyEntityMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private CurrencyDao dao = new CurrencyDao();


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        resp.setContentType("application/json");
        PrintWriter out;
        try {
            out = resp.getWriter();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        List<CurrencyEntity> currencies = dao.getAll();

        StringBuilder result = toJsonString(currencies);
        out.write(result.toString());
        out.close();
    }

    private StringBuilder toJsonString(List<CurrencyEntity> currencies) {
        StringBuilder result = new StringBuilder();
        result.append("[");
        for (CurrencyEntity currencyEntity :
                currencies) {
            result.append(currencyEntity.toString()).append(",");
        }
        // TODO убрать последнюю запятую
        result.append("]");
        return result;
    }
    // TODO fix bug {
    //        "id": 5,
    //        "name": "AFN",
    //        "code": "Afghani",
    //        "sign": "%D8%8B"
    //    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) {
        String currencyRequestString;
        try {
            currencyRequestString = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        CurrencyEntity currencyEntity = new CurrencyEntityMapper().fromXWwwFormUrlEncoded(currencyRequestString);
        if (currencyEntity.getSign().isEmpty()
                || currencyEntity.getName().isEmpty()
                || currencyEntity.getCode().isEmpty()) {
            try {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        if (dao.exists(currencyEntity.getCode())) {
            try {
                response.sendError(HttpServletResponse.SC_CONFLICT, "Currency with this code exists");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
        dao.save(currencyEntity);
    }
}
