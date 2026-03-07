package ru.hawoline.currencyexchange.data.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ru.hawoline.currencyexchange.data.dao.CurrencyDao;
import ru.hawoline.currencyexchange.domain.dto.ErrorMessageDto;
import ru.hawoline.currencyexchange.domain.exception.EntityNotFoundException;
import ru.hawoline.currencyexchange.domain.entity.CurrencyEntity;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@WebServlet(value = "/currency/*")
public class CurrencyServlet extends HttpServlet {
    private CurrencyDao dao = new CurrencyDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8);
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:63342");
        String uri = request.getRequestURI();
        String currencyCode = uri.replaceAll("/currency/", "");
        if (currencyCode.contains("/")) {
            sendError(response, HttpServletResponse.SC_BAD_REQUEST, "Uri path contains unnecessary '/'");
            return;
        }
        CurrencyEntity currency;
        PrintWriter out = response.getWriter();
        try {
            currency = dao.getEntityBy(currencyCode);
            out.write(currency.toString());
        } catch (EntityNotFoundException e) {
            sendError(response, HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        } finally {
            out.close();
        }
    }

    public void sendError(HttpServletResponse response, int httpErrorCode, String errorMessage) throws IOException {
        response.setStatus(httpErrorCode);
        ErrorMessageDto errorMessageDto = new ErrorMessageDto(errorMessage);
        response.getWriter().write(errorMessageDto.toString());
    }
}
