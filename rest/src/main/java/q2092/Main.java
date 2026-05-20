package q2092;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import org.json.JSONObject;

public class Main {
    static final String EXAM_IP      = "36.50.135.242";
    static final String STUDENT_CODE = "B21DCCN001";   // TODO
    static final String Q_CODE       = "TODO_qAlias";  // TODO
    static final String BASE         = "http://" + EXAM_IP + ":2230/api/rest/path";

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        // Phase 1 — GET danh sách khách hàng
        String getUrl = BASE
                + "?studentCode=" + URLEncoder.encode(STUDENT_CODE, StandardCharsets.UTF_8)
                + "&qCode="       + URLEncoder.encode(Q_CODE,       StandardCharsets.UTF_8);

        HttpResponse<String> phase1 = client.send(
                HttpRequest.newBuilder().uri(URI.create(getUrl)).GET().build(),
                HttpResponse.BodyHandlers.ofString());

        JSONObject body      = new JSONObject(phase1.body());
        String     requestId = body.getString("requestId");
        JSONArray  customers = body.getJSONArray("data");

        // Bước 2 — lọc OVERDUE, chọn overdueAmount lớn nhất
        String  bestCustomerId = null;
        int     bestPage       = -1;
        double  bestAmount     = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < customers.length(); i++) {
            JSONObject c = customers.getJSONObject(i);
            if (!"OVERDUE".equals(c.getString("status"))) continue;
            double amt = c.getDouble("overdueAmount");
            if (amt > bestAmount) {
                bestAmount     = amt;
                bestCustomerId = c.get("customerId").toString();
                bestPage       = c.getInt("page");
            }
        }
        if (bestCustomerId == null) throw new RuntimeException("Không có khách OVERDUE");
        System.out.printf("customerId=%s page=%d overdueAmount=%.2f%n",
                bestCustomerId, bestPage, bestAmount);

        // Phase 2 — GET với path + query
        String phase2Url = BASE + "/" + URLEncoder.encode(bestCustomerId, StandardCharsets.UTF_8)
                + "?studentCode=" + URLEncoder.encode(STUDENT_CODE, StandardCharsets.UTF_8)
                + "&qCode="       + URLEncoder.encode(Q_CODE,       StandardCharsets.UTF_8)
                + "&requestId="   + URLEncoder.encode(requestId,    StandardCharsets.UTF_8)
                + "&status=OVERDUE"
                + "&page="        + bestPage;

        HttpResponse<String> phase2 = client.send(
                HttpRequest.newBuilder().uri(URI.create(phase2Url)).GET().build(),
                HttpResponse.BodyHandlers.ofString());

        System.out.println("Server response: " + phase2.body());
    }
}
