package ru.hawoline.currencyexchange.data.servlet;

import jakarta.servlet.http.HttpServletResponse;
import ru.hawoline.currencyexchange.domain.dto.ErrorMessageDto;

import java.io.PrintWriter;

public class ErrorSender {
    public void send(HttpServletResponse response, int httpErrorCode, String errorMessage, PrintWriter printWriter) {
        response.setStatus(httpErrorCode);
        ErrorMessageDto errorMessageDto = new ErrorMessageDto(errorMessage);
        printWriter.write(errorMessageDto.toString());
    }
}
