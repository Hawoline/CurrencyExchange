package ru.hawoline.currencyexchange.data.servlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletResponse;
import ru.hawoline.currencyexchange.domain.dto.ErrorMessageDto;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CustomServlet extends HttpServlet {
    protected void sendError(HttpServletResponse response, int httpErrorCode, String errorMessage) throws IOException {
        response.setStatus(httpErrorCode);
        ErrorMessageDto errorMessageDto = new ErrorMessageDto(errorMessage);
        response.getWriter().write(errorMessageDto.toString());
    }

    protected void addResponseHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:63342");
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8);
    }
}
