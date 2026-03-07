package ru.hawoline.currencyexchange.data.servlet;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.hawoline.currencyexchange.data.dao.CurrencyDao;
import ru.hawoline.currencyexchange.domain.exception.EntityNotFoundException;
import ru.hawoline.currencyexchange.domain.entity.CurrencyEntity;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@WebServlet(value = "/currency/*")
public class CurrencyServlet extends CustomServlet {
    private CurrencyDao dao = new CurrencyDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        addResponseHeaders(response);
        String uri = request.getRequestURI();
        String currencyCode = uri.replaceAll("/currency/", "");
        if (currencyCode.contains("/")) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Uri path contains unnecessary '/'");
            return;
        }
        PrintWriter out = response.getWriter();
        try {
            CurrencyEntity currency = dao.getEntityBy(currencyCode);
            out.write(currency.toString());
        } catch (EntityNotFoundException e) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } finally {
            out.close();
        }
    }
}
