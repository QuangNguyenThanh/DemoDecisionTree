package quangnt;

public class DataRow {
    int id;
    String outlook;
    String temperature;
    String humidity;
    String wind;
    String result;

    public DataRow(int id, String outlook, String temperature, String humidity, String wind, String result) {
        super();
        this.id = id;
        this.outlook = outlook;
        this.temperature = temperature;
        this.humidity = humidity;
        this.wind = wind;
        this.result = result;
    }

    public DataRow(int id, String outlook, String temperature, String humidity, String wind) {
        super();
        this.id = id;
        this.outlook = outlook;
        this.temperature = temperature;
        this.humidity = humidity;
        this.wind = wind;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "DataRow [id=" + id + ", outlook=" + outlook + ", temperature=" + temperature + ", humidity=" + humidity
                + ", wind=" + wind + ", result=" + result + "]";
    }
}
