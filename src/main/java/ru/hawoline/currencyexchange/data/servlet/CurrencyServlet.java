package ru.hawoline.currencyexchange.data.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.hawoline.currencyexchange.data.dao.CurrencyDao;
import ru.hawoline.currencyexchange.domain.exception.EntityNotFoundException;
import ru.hawoline.currencyexchange.domain.entity.CurrencyEntity;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet(value = "/currency/*")
public class CurrencyServlet extends HttpServlet {
    private CurrencyDao dao = new CurrencyDao();


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String uri = request.getRequestURI();
        String currencyCode = uri.replaceAll("/currency/", "");
        if (currencyCode.contains("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        CurrencyEntity currency = null;
        try {
            currency = dao.getEntityBy(currencyCode);
        } catch (EntityNotFoundException e) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
        PrintWriter out = response.getWriter();
        out.write(currency.toString());
    }
}
