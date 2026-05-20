import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class Q2072_MethodPatchIfMatch {
    static final String EXAM_IP      = "36.50.135.242";
    static final String STUDENT_CODE = "B21DCCN001";   // TODO
    static final String Q_CODE       = "TODO_qAlias";  // TODO
    static final String BASE         = "http://" + EXAM_IP + ":2230/api/rest/method";

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
        JSONObject ticket    = body.getJSONObject("data");
        String     etag      = ticket.getString("etag");
        System.out.println("requestId=" + requestId + ", etag=" + etag);

        JSONObject submit = new JSONObject()
                .put("studentCode", STUDENT_CODE)
                .put("qCode",       Q_CODE)
                .put("answer",      new JSONObject().put("status", "RESOLVED"));

        HttpResponse<String> patchRes = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE + "/" + requestId))
                        .header("Content-Type", "application/json")
                        .header("If-Match",     etag)
                        .method("PATCH", BodyPublishers.ofString(submit.toString()))
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        System.out.println("Server response: " + patchRes.body());
    }
}
