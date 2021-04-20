package ru.leontyko;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        String mode;

        if (args!=null && args.length>0) {
            mode = args[0];
        }
        else {
            mode = "allByDay";
        }

        if (mode.equals("allByDay")) {
            try {
                DbHandler dbHandler = DbHandler.getInstance();

                List<Export> exports = dbHandler.getAllByDay();

                if (exports.isEmpty()) {
                    System.out.println("Пустой результат\n");
                    System.exit(0);
                }

                export(dbHandler, exports);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        else if(mode.equals("manage")) {
            scan();
        }
        else {
            System.out.println("Неизвестный параметр");
            System.exit(0);
        }
    }

    static void export(DbHandler dbHandler, List<Export> exports) {
        for (Export export : exports) {
            String fileName = "";

            FileWriter writer = null;

            System.out.println("\n" + export.toString());

            List<ExportString> strings = dbHandler.getExportStringsById(export.getId());

            if (strings.isEmpty()) {
                System.out.println("Пустой результат\n");
                System.exit(0);
            }

            for (ExportString string : strings) {
                if (fileName.equals("")) {
                    fileName = string.getName();
                    fileName = fileName.substring(0, fileName.indexOf("^"));
                    try {
                        writer = new FileWriter("C:\\IN\\"+fileName+".lou", false);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                String sample = dbHandler.getSampleForString(string.getPlace(), export.getId());

                String exportString = sample + "|" +
                        string.getTimestamp() + "|" +
                        string.getPlace() + "|" +
                        "QN:" + string.getQn() + "|" +
                        "QL:" + string.getQl();

                System.out.println(exportString);

                try {
                    assert writer != null;
                    writer.append(exportString);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    writer.append('\n');
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                writer.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    static void scan() {
        Scanner in = new Scanner(System.in);
        System.out.print("\nВведите ID для экспорта\n" +
                "Enter - чтобы отправить последние результаты\n" +
                "1 - чтобы отправить все результаты за сегодня\n" +
                "exit - для выхода:\n");

        String code=in.nextLine();

        if (code.isEmpty()) {
            try {
                DbHandler dbHandler = DbHandler.getInstance();

                List<Export> exports = dbHandler.getLastOne();

                if (exports.isEmpty()) {
                    System.out.println("Пустой результат\n");
                    scan();
                }

                export(dbHandler, exports);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            scan();
        }
        else if (code.equals("1")) {
            try {
                DbHandler dbHandler = DbHandler.getInstance();

                List<Export> exports = dbHandler.getAllByDay();

                if (exports.isEmpty()) {
                    System.out.println("Пустой результат\n");
                    scan();
                }

                export(dbHandler, exports);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            scan();
        }
        else if(code.equals("exit")) {
            System.exit(0);
        }
        else {
            try {
                DbHandler dbHandler = DbHandler.getInstance();

                List<Export> exports = dbHandler.getById(code);

                if (exports.isEmpty()) {
                    System.out.println("Неверный ID\n");
                    scan();
                }

                export(dbHandler, exports);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            scan();
        }
    }
}
