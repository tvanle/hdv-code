import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import org.json.JSONObject;

public class Q2032_ObjectShippingQuote {
    static final String EXAM_IP      = "36.50.135.242";
    static final String STUDENT_CODE = "B22DCCN863";   // TODO
    static final String Q_CODE       = "TODO_qAlias";  // TODO
    static final String BASE         = "http://" + EXAM_IP + ":2230/api/rest/object";

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
        JSONObject data      = body.getJSONObject("data");

        double weightKg    = data.getDouble("weightKg");
        int    maxEtaDays  = data.getInt("maxEtaDays");
        JSONArray quotes   = data.getJSONArray("quotes");

        String bestCarrier  = null;
        double bestFee      = Double.POSITIVE_INFINITY;
        int    bestEta      = -1;
        double bestReliab   = -1.0;

        for (int i = 0; i < quotes.length(); i++) {
            JSONObject q = quotes.getJSONObject(i);
            int etaDays = q.getInt("etaDays");
            if (etaDays > maxEtaDays) continue;

            double totalFee = q.getDouble("baseFee") + weightKg * q.getDouble("perKgFee");
            totalFee = Math.round(totalFee * 100.0) / 100.0;
            double reliab = q.getDouble("reliability");

            if (totalFee < bestFee || (totalFee == bestFee && reliab > bestReliab)) {
                bestFee     = totalFee;
                bestEta     = etaDays;
                bestReliab  = reliab;
                bestCarrier = q.getString("carrier");
            }
        }
        if (bestCarrier == null) throw new RuntimeException("Không có quote hợp lệ");
        System.out.printf("best: carrier=%s fee=%.2f eta=%d%n", bestCarrier, bestFee, bestEta);

        JSONObject answer = new JSONObject()
                .put("carrier",  bestCarrier)
                .put("totalFee", bestFee)
                .put("etaDays",  bestEta);

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
