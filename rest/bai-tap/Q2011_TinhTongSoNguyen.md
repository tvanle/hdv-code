# Q2011 — REST Data — Sum of Integers

| | |
|---|---|
| **Mức** | EASY |
| **Giao thức** | `REST_DATA` |
| **Endpoint** | `http://<Exam_IP>:2230/api/rest/data` |

## Đề bài

Một dịch vụ REST được triển khai trên server tại URL `http://<Exam_IP>:2230/api/rest/data` để xử lý các bài toán với dữ liệu nguyên thủy.

**Yêu cầu:** Viết chương trình Java (REST client) để giao tiếp với **DataService**:

1. Gửi `GET /api/rest/data?studentCode=<mã_sv>&qCode=<qCode>` để nhận JSON:
   ```json
   {
     "requestId": "a1b2c3d4",
     "data": [7602, 9136, 1090, 3431, 7830, 6179]
   }
   ```
2. Tính **tổng** tất cả phần tử trong `data`.
3. Gửi `POST /api/rest/data/submit` với body:
   ```json
   {
     "studentCode": "B21DCCN001",
     "qCode": "<qCode>",
     "requestId": "a1b2c3d4",
     "answer": 35268
   }
   ```
   `requestId` phải đúng giá trị nhận ở bước 1.
4. Server trả `{"status":"AC"}` hoặc `{"status":"WA"}`.

**Ví dụ:** `[1, 2, 3, 4, 5]` → `answer = 15`.

## Code Java

```java
package q2011;

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
    static final String STUDENT_CODE = "B21DCCN001";   // TODO: mã SV của bạn
    static final String Q_CODE       = "5Dg7uj0X";     // TODO: qCode trong đề
    static final String BASE         = "http://" + EXAM_IP + ":2230/api/rest/data";

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        // Phase 1 — GET danh sách số
        String getUrl = BASE
                + "?studentCode=" + URLEncoder.encode(STUDENT_CODE, StandardCharsets.UTF_8)
                + "&qCode="       + URLEncoder.encode(Q_CODE,       StandardCharsets.UTF_8);

        HttpResponse<String> getRes = client.send(
                HttpRequest.newBuilder().uri(URI.create(getUrl)).GET().build(),
                HttpResponse.BodyHandlers.ofString());

        JSONObject body      = new JSONObject(getRes.body());
        String     requestId = body.getString("requestId");
        JSONArray  data      = body.getJSONArray("data");

        // Bước 2 — tính tổng
        long sum = 0;
        for (int i = 0; i < data.length(); i++) sum += data.getLong(i);
        System.out.println("requestId=" + requestId + ", sum=" + sum);

        // Phase 2 — POST submit
        JSONObject submit = new JSONObject()
                .put("studentCode", STUDENT_CODE)
                .put("qCode",       Q_CODE)
                .put("requestId",   requestId)
                .put("answer",      sum);

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
```

## Bẫy thường gặp

- Dùng `long` (không `int`) để cộng phòng trường hợp tổng tràn — đề có thể có số lớn.
- `answer` phải là **số nguyên** (không quote). `org.json` tự xử khi `.put(key, long)`.
- `requestId` phải khớp với giá trị nhận từ Phase 1.
