package ru.leontyko;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DbHandler {
    // Константа, в которой хранится адрес подключения
    private static final String CON_STR = "jdbc:sqlite:/C:\\ProgramData\\Bregis\\Reader-M\\reader-m.sqdb";

    // Используем шаблон одиночка, чтобы не плодить множество
    // экземпляров класса DbHandler
    private static DbHandler instance = null;

    public static synchronized DbHandler getInstance() throws SQLException {
        if (instance == null)
            instance = new DbHandler();
        return instance;
    }

    // Объект, в котором будет храниться соединение с БД
    private Connection connection;

    private DbHandler() throws SQLException {
        // Регистрируем драйвер, с которым будем работать
        // в нашем случае Sqlite
//        DriverManager.registerDriver(new JDBC());
        // Выполняем подключение к базе данных
        this.connection = DriverManager.getConnection(CON_STR);
    }

    public List<Export> getAllByDay() {

        // Statement используется для того, чтобы выполнить sql-запрос
        try (Statement statement = this.connection.createStatement()) {
            // В данный список будем загружать наши продукты, полученные из БД
            List<Export> exports = new ArrayList<Export>();

            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            LocalDateTime now = LocalDateTime.now();

            String sql = "SELECT res.plate_id as ID, method.name, plt.timestamp\n" +
                    "FROM export_result as res\n" +
                    "INNER JOIN\n" +
                    "plate as plt\n" +
                    "ON res.plate_id = plt.id\n" +
                    "INNER JOIN method_revision as rev\n" +
                    "ON plt.method_revision_id = rev.id\n" +
                    "INNER JOIN method\n" +
                    "ON rev.method_id = method.id\n" +
                    "WHERE res.timestamp LIKE '" + dtf.format(now) + "%'\n" +
                    "GROUP BY plt.timestamp, method.name";

            ResultSet resultSet = statement.executeQuery(sql);
            // Проходимся по нашему resultSet и заносим данные
            while (resultSet.next()) {
                exports.add(new Export(resultSet.getString("id"),
                        resultSet.getString("name"),
                        resultSet.getString("timestamp")));
            }
            // Возвращаем наш список
            return exports;

        } catch (SQLException e) {
            e.printStackTrace();
            // Если произошла ошибка - возвращаем пустую коллекцию
            return Collections.emptyList();
        }
    }

    public List<Export> getLastOne() {
        try (Statement statement = this.connection.createStatement()) {
            List<Export> exports = new ArrayList<Export>();

            String sql = "SELECT res.plate_id as ID, method.name, plt.timestamp\n" +
                    "FROM export_result as res\n" +
                    "INNER JOIN\n" +
                    "plate as plt\n" +
                    "ON res.plate_id = plt.id\n" +
                    "INNER JOIN method_revision as rev\n" +
                    "ON plt.method_revision_id = rev.id\n" +
                    "INNER JOIN method\n" +
                    "ON rev.method_id = method.id\n" +
                    "WHERE res.plate_id = (SELECT MAX(plate_id) FROM export_result)\n" +
                    "GROUP BY plt.timestamp, method.name";

            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                exports.add(new Export(resultSet.getString("id"),
                        resultSet.getString("name"),
                        resultSet.getString("timestamp")));
            }

            return exports;

        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<Export> getById(String code) {
        try (Statement statement = this.connection.createStatement()) {
            List<Export> exports = new ArrayList<Export>();

            String sql = "SELECT res.plate_id as ID, method.name, plt.timestamp\n" +
                    "FROM export_result as res\n" +
                    "INNER JOIN\n" +
                    "plate as plt\n" +
                    "ON res.plate_id = plt.id\n" +
                    "INNER JOIN method_revision as rev\n" +
                    "ON plt.method_revision_id = rev.id\n" +
                    "INNER JOIN method\n" +
                    "ON rev.method_id = method.id\n" +
                    "WHERE res.plate_id = "+code+"\n" +
                    "GROUP BY plt.timestamp, method.name";

            ResultSet resultSet = statement.executeQuery(sql);

            while (resultSet.next()) {
                exports.add(new Export(resultSet.getString("id"),
                        resultSet.getString("name"),
                        resultSet.getString("timestamp")));
            }

            return exports;

        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public List<ExportString> getExportStringsById(String id) {
        try (Statement statement = this.connection.createStatement()) {
            List<ExportString> strings = new ArrayList<ExportString>();

            String sql = "SELECT indexes.code as name, indexes.timestamp, places.value as place, indexes.value as qn, qlts.value as ql\n" +
                    "FROM (SELECT * FROM export_result WHERE (code LIKE '%Index' OR code LIKE '%Conc') AND plate_id = "+id+") as indexes\n" +
                    "LEFT JOIN\n" +
                    "(SELECT * FROM export_result WHERE code LIKE '%Qlty' AND plate_id = "+id+") as qlts\n" +
                    "ON indexes.service_id = qlts.service_id\n" +
                    "LEFT JOIN\n" +
                    "(SELECT * FROM export_result WHERE code LIKE '%Place' AND plate_id = "+id+") as places\n" +
                    "ON indexes.service_id = places.service_id";

            ResultSet resultSet = statement.executeQuery(sql);
            while (resultSet.next()) {
                strings.add(new ExportString(resultSet.getString("name"),
                        resultSet.getString("timestamp"),
                        resultSet.getString("place"),
                        resultSet.getString("qn"),
                        resultSet.getString("ql")));
            }
            return strings;

        } catch (SQLException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public String getSampleForString(String place, String id) {
        try (Statement statement = this.connection.createStatement()) {
            String sql = "SELECT sample_"+place+" as val FROM plate WHERE id="+id+" LIMIT 1";

            ResultSet resultSet = statement.executeQuery(sql);

            return resultSet.getString("val");
        } catch (SQLException e) {
            e.printStackTrace();
            return "";
        }
    }
}
