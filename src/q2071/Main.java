package q2071;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class Main {
    static final String EXAM_IP      = "36.50.135.242";
    static final String STUDENT_CODE = "B22DCCN001";   // TODO
    static final String Q_CODE       = "RlQyMHKW";     // TODO
    static final String BASE         = "http://" + EXAM_IP + ":2230/api/rest/method";

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

        // Phase 2 — PUT với audit fields
        JSONObject answer = new JSONObject()
                .put("status",      "ACTIVE")
                .put("activatedBy", STUDENT_CODE)
                .put("auditNote",   "manual-review-ok");

        JSONObject submit = new JSONObject()
                .put("studentCode", STUDENT_CODE)
                .put("qCode",       Q_CODE)
                .put("answer",      answer);

        HttpResponse<String> putRes = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE + "/" + requestId))
                        .header("Content-Type", "application/json")
                        .method("PUT", BodyPublishers.ofString(submit.toString()))
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        System.out.println("Server response: " + putRes.body());
    }
}
