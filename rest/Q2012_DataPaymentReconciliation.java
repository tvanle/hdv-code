import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import org.json.JSONObject;

public class Q2012_DataPaymentReconciliation {
    static final String EXAM_IP      = "36.50.135.242";
    static final String STUDENT_CODE = "B22DCCN863";   // TODO
    static final String Q_CODE       = "TODO_qAlias";  // TODO
    static final String BASE         = "http://" + EXAM_IP + ":2230/api/rest/data";

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        String getUrl = BASE
                + "?studentCode=" + URLEncoder.encode(STUDENT_CODE, StandardCharsets.UTF_8)
                + "&qCode="       + URLEncoder.encode(Q_CODE,       StandardCharsets.UTF_8);

        HttpResponse<String> getRes = client.send(
                HttpRequest.newBuilder().uri(URI.create(getUrl)).GET().build(),
                HttpResponse.BodyHandlers.ofString());

        JSONObject body      = new JSONObject(getRes.body());
        String     requestId = body.getString("requestId");
        JSONArray  txs       = body.getJSONArray("data");

        double capturedTotal = 0.0;
        double refundedTotal = 0.0;
        int    failedCount   = 0;
        for (int i = 0; i < txs.length(); i++) {
            JSONObject tx = txs.getJSONObject(i);
            String status = tx.getString("status");
            double amount = tx.getDouble("amount");
            switch (status) {
                case "CAPTURED": capturedTotal += amount; break;
                case "REFUNDED": refundedTotal += amount; break;
                case "FAILED":   failedCount++;           break;
                default: /* PENDING */
            }
        }
        double netTotal = capturedTotal - refundedTotal;
        capturedTotal = Math.round(capturedTotal * 100.0) / 100.0;
        refundedTotal = Math.round(refundedTotal * 100.0) / 100.0;
        netTotal      = Math.round(netTotal      * 100.0) / 100.0;

        System.out.printf("captured=%.2f refunded=%.2f net=%.2f failed=%d%n",
                capturedTotal, refundedTotal, netTotal, failedCount);

        JSONObject answer = new JSONObject()
                .put("capturedTotal", capturedTotal)
                .put("refundedTotal", refundedTotal)
                .put("netTotal",      netTotal)
                .put("failedCount",   failedCount);

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
