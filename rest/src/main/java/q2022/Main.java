package q2022;

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
    static final String Q_CODE       = "TODO_qAlias";  // TODO
    static final String BASE         = "http://" + EXAM_IP + ":2230/api/rest/character";

    // Email RFC-5322 đơn giản hoá (đủ cho đề thi)
    static final String EMAIL_RE = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}";
    // Phone VN: 10 số bắt đầu bằng 0 — đặt boundary để không cắt vào chuỗi số dài hơn
    static final String PHONE_RE = "(?<!\\d)0\\d{9}(?!\\d)";
    // token=<giá_trị> — giá_trị là chuỗi non-space
    static final String TOKEN_RE = "token=[^\\s|]+";

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        // Phase 1
        String getUrl = BASE
                + "?studentCode=" + URLEncoder.encode(STUDENT_CODE, StandardCharsets.UTF_8)
                + "&qCode="       + URLEncoder.encode(Q_CODE,       StandardCharsets.UTF_8);

        HttpResponse<String> getRes = client.send(
                HttpRequest.newBuilder().uri(URI.create(getUrl)).GET().build(),
                HttpResponse.BodyHandlers.ofString());

        JSONObject body      = new JSONObject(getRes.body());
        String     requestId = body.getString("requestId");
        String     data      = body.getString("data");

        // Bước 2 — redact theo từng dòng (giữ thứ tự)
        String[] lines = data.split("\\|\\|", -1);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lines.length; i++) {
            String redacted = lines[i]
                    .replaceAll(EMAIL_RE, "[EMAIL]")
                    .replaceAll(PHONE_RE, "[PHONE]")
                    .replaceAll(TOKEN_RE, "token=[TOKEN]");
            if (i > 0) sb.append("||");
            sb.append(redacted);
        }
        String answer = sb.toString();
        System.out.println("answer=" + answer);

        // Phase 2
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
