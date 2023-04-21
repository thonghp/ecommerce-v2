package com.hpt.backend;

import javax.servlet.http.HttpServletResponse;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AbstractExporter {

    /**
     * Set response header for file on client side
     *
     * @param response    HttpServletResponse
     * @param contentType Content-Type to determine the content type of the request or response body
     * @param extension   File extension
     * @param prefix      File name prefix
     */
    public void setReponseHeader(HttpServletResponse response, String contentType, String extension, String prefix) {
        DateFormat dateFormater = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        String timestamp = dateFormater.format(new Date());
        String fileName = prefix + timestamp + extension;

        response.setContentType(contentType);

        String headerKey = "Content-Disposition";
        String headerValue = "attachment; fileName=" + fileName;
        response.setHeader(headerKey, headerValue);
    }
}
