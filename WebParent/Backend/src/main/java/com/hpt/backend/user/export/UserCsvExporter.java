package com.hpt.backend.user.export;

import com.hpt.backend.AbstractExporter;
import com.hpt.common.entity.User;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.ICsvBeanWriter;
import org.supercsv.prefs.CsvPreference;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class UserCsvExporter extends AbstractExporter {
    public void export(List<User> listUsers, HttpServletResponse response) throws IOException {
        String contentType = "text/csv; charset=utf-8";
        String extension = ".csv";
        String prefix = "users_";
        super.setReponseHeader(response, contentType, extension, prefix);

        OutputStream outputStream = response.getOutputStream();
        ICsvBeanWriter csvWriter = new CsvBeanWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), CsvPreference.STANDARD_PREFERENCE);

        String[] csvHeader = {"ID", "E-mail", "Họ", "Tên", "SĐT", "Vai trò", "Trạng thái"};
        String[] fieldMapping = {"id", "email", "lastName", "firstName", "phoneNumber", "roles", "enabled"};

        csvWriter.writeHeader(csvHeader);

        for (User user : listUsers) {
            csvWriter.write(user, fieldMapping);
        }

        csvWriter.close();
        outputStream.flush();
    }
}
