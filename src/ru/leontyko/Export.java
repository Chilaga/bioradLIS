package ru.leontyko;

public class Export {
    String id;

    String name;

    String timestamp;

    public Export(String id, String name, String timestamp) {
        this.id = id;
        this.name = name;
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    // Выводим информацию по экспорту
    @Override
    public String toString() {
        return String.format("ID:%s %s - %s",
                this.id, this.name, this.timestamp);
    }
}
