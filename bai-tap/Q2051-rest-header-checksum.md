# Q2051 — [REST] HTTP Header — X-Checksum

| | |
|---|---|
| **Mức** | MEDIUM |
| **Giao thức** | `REST_HEADER` |
| **Endpoint** | `http://<Exam_IP>:2230/api/rest/header` |

## Đề bài

Đọc một **custom HTTP response header** (`X-Checksum`) ở Phase 1 và gửi lại đúng giá trị đó trong **request header** ở Phase 2.

### Giao thức

**Bước 1 — GET:**
```
GET /api/rest/header?studentCode=<mã_sv>&qCode=<qAlias>
```
Body phản hồi:
```json
{
  "requestId": "def56789",
  "data": [3421, 7890, 1234, 5678, 9012, 3456]
}
```
Header phản hồi:
```
X-Checksum: a3f2c1...  (SHA-256 của danh sách số)
```

**Bước 2 — POST:**
```
POST /api/rest/header/submit
```
Body JSON:
```json
{
  "studentCode": "<mã_sv>",
  "qCode": "<qAlias>",
  "requestId": "def56789"
}
```
Header bắt buộc:
```
X-Checksum: a3f2c1...  (giá trị đọc từ Phase 1)
```

### Yêu cầu

- Đọc header `X-Checksum` từ phản hồi Phase 1.
- Gửi **chính xác** giá trị đó trong request header Phase 2.
- **Không cần** tính SHA-256 thủ công.

## Code Java

```java
package q2051;

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
    static final String Q_CODE       = "LrRK7nD4";     // TODO
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
        System.out.println("X-Checksum=" + checksum);

        // Phase 2 — POST kèm header X-Checksum
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

- Đọc header chính xác: `response.headers().firstValue("X-Checksum")`.
- Header **tên** không phân biệt hoa thường (HTTP standard), nhưng **giá trị** có. Không tự convert.
- Body Phase 2 không chứa `answer` — chỉ cần `studentCode`, `qCode`, `requestId`.
- Đừng tự hash SHA-256 lại; chỉ truyền nguyên giá trị đã nhận.
