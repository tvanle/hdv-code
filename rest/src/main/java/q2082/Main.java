package q2082;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import org.json.JSONArray;
import org.json.JSONObject;

public class Main {
    static final String EXAM_IP      = "36.50.135.242";
    static final String STUDENT_CODE = "B21DCCN001";   // TODO — sẽ chuyển sang UPPER khi build payload
    static final String Q_CODE       = "TODO_qAlias";  // TODO
    static final String BASE         = "http://" + EXAM_IP + ":2230/api/rest/header";

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        // Phase 1 — GET nonce, signingKey, events
        String getUrl = BASE
                + "?studentCode=" + URLEncoder.encode(STUDENT_CODE, StandardCharsets.UTF_8)
                + "&qCode="       + URLEncoder.encode(Q_CODE,       StandardCharsets.UTF_8);

        HttpResponse<String> getRes = client.send(
                HttpRequest.newBuilder().uri(URI.create(getUrl)).GET().build(),
                HttpResponse.BodyHandlers.ofString());

        JSONObject body      = new JSONObject(getRes.body());
        String     requestId = body.getString("requestId");
        JSONObject data      = body.getJSONObject("data");
        String     nonce      = data.getString("nonce");
        String     signingKey = data.getString("signingKey");
        JSONArray  events     = data.getJSONArray("events");

        // Bước 2 — build payload: <nonce>:<event1>|...|<eventN>:<STUDENT_CODE_HOA>
        StringBuilder ev = new StringBuilder();
        for (int i = 0; i < events.length(); i++) {
            if (i > 0) ev.append("|");
            ev.append(events.getString(i));
        }
        String payload = nonce + ":" + ev + ":" + STUDENT_CODE.toUpperCase();

        // HMAC-SHA256 hex lowercase
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(signingKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] sig = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));

        StringBuilder hex = new StringBuilder(sig.length * 2);
        for (byte b : sig) hex.append(String.format("%02x", b));
        String signature = hex.toString();
        System.out.println("X-Signature=" + signature);

        // Phase 2 — POST với header X-Signature
        JSONObject submit = new JSONObject()
                .put("studentCode", STUDENT_CODE)
                .put("qCode",       Q_CODE)
                .put("requestId",   requestId);

        HttpResponse<String> postRes = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE + "/submit"))
                        .header("Content-Type", "application/json")
                        .header("X-Signature",  signature)
                        .POST(HttpRequest.BodyPublishers.ofString(submit.toString()))
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        System.out.println("Server response: " + postRes.body());
    }
}
