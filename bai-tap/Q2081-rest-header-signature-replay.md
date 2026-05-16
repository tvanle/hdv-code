# Q2081 — [REST] Header Signature Replay (X-Checksum)

| | |
|---|---|
| **Mức** | MEDIUM |
| **Giao thức** | `REST_HEADER` |
| **Endpoint** | `http://<Exam_IP>:2230/api/rest/header` |

## Đề bài

Đọc chữ ký checksum từ response header ở Phase 1 và gửi lại đúng trong request header ở Phase 2.

### Giao thức

**Bước 1 — GET:**
```
GET /api/rest/header?studentCode=<mã_sv>&qCode=<qAlias>
```
Response header:
```
X-Checksum: 8a3d8b4f9b4b77a90a0a9f9f43f2c43f2ce28f562e0b1726b5405c8c2512de67
```

**Bước 2 — POST:**
```
POST /api/rest/header/submit
```
Header bắt buộc:
```
X-Checksum: <giá trị đọc từ phase 1>
```
Body JSON:
```json
{
  "studentCode": "B22DCCN001",
  "qCode": "k9T2uV5m",
  "requestId": "x2Q8p4Lm"
}
```

### Yêu cầu

- Đọc & gửi đúng header `X-Checksum`.
- Body đủ `studentCode`, `qCode`, `requestId`.

## Code Java

```java
package q2081;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

public class Main {
    static final String EXAM_IP      = "36.50.135.242";
    static final String STUDENT_CODE = "B22DCCN001";   // TODO
    static final String Q_CODE       = "JknnWOkR";     // TODO
    static final String BASE         = "http://" + EXAM_IP + ":2230/api/rest/header";

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
        String     checksum  = getRes.headers().firstValue("X-Checksum")
                                     .orElseThrow(() -> new RuntimeException("Missing X-Checksum"));

        // Phase 2 — POST với header X-Checksum
        JSONObject submit = new JSONObject()
                .put("studentCode", STUDENT_CODE)
                .put("qCode",       Q_CODE)
                .put("requestId",   requestId);

        HttpResponse<String> postRes = client.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(BASE + "/submit"))
                        .header("Content-Type", "application/json")
                        .header("X-Checksum",   checksum)
                        .POST(HttpRequest.BodyPublishers.ofString(submit.toString()))
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        System.out.println("Server response: " + postRes.body());
    }
}
```

## Bẫy thường gặp

- **Giống hệt Q2051** về cấu trúc, chỉ khác đề mô tả. Có thể tái sử dụng code.
- Khác Q2051: đề bài Q2081 nhấn mạnh body đủ `studentCode`/`qCode`/`requestId`.
- Đừng tự sinh checksum mới — chỉ replay đúng giá trị.
