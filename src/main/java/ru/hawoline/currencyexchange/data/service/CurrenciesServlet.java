package ru.hawoline.currencyexchange.data.service;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import ru.hawoline.currencyexchange.data.dao.CurrenciesDao;
import ru.hawoline.currencyexchange.data.entity.CurrencyEntity;
import ru.hawoline.currencyexchange.domain.Dao;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private CurrenciesDao dao = new CurrenciesDao();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();
        List<CurrencyEntity> currencies = dao.getAll();
        StringBuilder result = new StringBuilder();
        result.append("[");
        for (CurrencyEntity c :
                currencies) {
            result.append("{\"id\": ").append(c.getId()).append(",");
            result.append("\"name\": \"").append(c.getName()).append("\",");
            result.append("\"code\": \"").append(c.getCode()).append("\",");
            result.append("\"sign\": \"").append(c.getSign()).append("\"},");
        }
        result.append("]");
        out.write(result.toString());
        out.close();
    }
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String currency = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
        JSONObject currencyJsonObject = new JSONObject(currency);
        CurrencyEntity currencyEntity = new CurrencyEntity(
                currencyJsonObject.getString("name"),
                currencyJsonObject.getString("code"),
                currencyJsonObject.getString("sign")
        );
        if (currencyEntity.getSign().isEmpty() || currencyEntity.getName().isEmpty()  || currencyEntity.getCode().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        if (dao.exists(currencyEntity.getCode())) {
            response.sendError(HttpServletResponse.SC_CONFLICT);
            return;
        }
        dao.save(currencyEntity);
    }
}
