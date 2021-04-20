package ru.leontyko;

public class ExportString {

    String name;

    String timestamp;

    String place;

    String qn;

    String ql;

    public ExportString(String name, String timestamp, String place, String qn, String ql) {
        this.name = name;
        this.timestamp = timestamp;
        this.place = place;
        this.qn = qn;
        this.ql = ql;
    }

    public String getPlace() {
        return place;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getName() {
        return name;
    }

    public String getQl() {
        return ql;
    }

    public String getQn() {
        return qn;
    }

    // Выводим информацию по экспорту
    @Override
    public String toString() {
        return String.format("%s|%s|%s|QN:%s|QL:%s",
                this.name, this.timestamp, this.place, this.qn, this.ql);
    }
}
