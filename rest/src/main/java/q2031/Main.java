package q2031;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class Main {
    static final String EXAM_IP      = "36.50.135.242";
    static final String STUDENT_CODE = "B21DCCN001";   // TODO
    static final String Q_CODE       = "s543QlZR";     // TODO
    static final String BASE         = "http://" + EXAM_IP + ":2230/api/rest/object";

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        // Phase 1 — GET
        String getUrl = BASE
                + "?studentCode=" + URLEncoder.encode(STUDENT_CODE, StandardCharsets.UTF_8)
                + "&qCode="       + URLEncoder.encode(Q_CODE,       StandardCharsets.UTF_8);

        HttpResponse<String> getRes = client.send(
                HttpRequest.newBuilder().uri(URI.create(getUrl)).GET().build(),
                HttpResponse.BodyHandlers.ofString());

        JSONObject body      = new JSONObject(getRes.body());
        String     requestId = body.getString("requestId");
        JSONObject data      = body.getJSONObject("data");

        String name     = data.getString("name");
        double price    = data.getDouble("price");
        double taxRate  = data.getDouble("taxRate");
        double discount = data.getDouble("discount");

        // Bước 2 — tính finalPrice (% discount)
        double finalPrice = price * (1 + taxRate / 100.0) * (1 - discount / 100.0);
        System.out.printf("finalPrice=%.4f%n", finalPrice);

        // Phase 2 — POST
        JSONObject answer = new JSONObject()
                .put("name",       name)
                .put("price",      price)
                .put("taxRate",    taxRate)
                .put("discount",   discount)
                .put("finalPrice", finalPrice);

        JSONObject submit = new JSONObject()
                .put("studentCode", STUDENT_CODE)
                .put("qCode",       Q_CODE)
                .put("requestId",   requestId)
                .put("answer",      answer);

        HttpResponse<String> postRes = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE + "/submit"))
                        .header("Content-Type", "application/json")
                        .POST(HttpRequest.BodyPublishers.ofString(submit.toString()))
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        System.out.println("Server response: " + postRes.body());
    }
}
