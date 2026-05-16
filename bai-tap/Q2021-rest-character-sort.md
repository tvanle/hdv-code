# Q2021 — REST Character — Sort Words Alphabetically

| | |
|---|---|
| **Mức** | EASY |
| **Giao thức** | `REST_CHARACTER` |
| **Endpoint** | `http://<Exam_IP>:2230/api/rest/character` |

## Đề bài

Một dịch vụ REST tại `http://<Exam_IP>:2230/api/rest/character` xử lý chuỗi và ký tự.

**Yêu cầu:**

1. `GET /api/rest/character?studentCode=<mã_sv>&qCode=<qCode>` → nhận:
   ```json
   {
     "requestId": "x1y2z3w4",
     "data": "banana apple cherry date elderberry"
   }
   ```
2. Tách `data` theo khoảng trắng, sort theo thứ tự từ điển **case-sensitive** (chữ hoa < chữ thường trong ASCII).
3. `POST /api/rest/character/submit` với body:
   ```json
   {
     "studentCode": "B21DCCN001",
     "qCode": "<qCode>",
     "requestId": "x1y2z3w4",
     "answer": "apple banana cherry date elderberry"
   }
   ```
   Các từ nối lại bằng **dấu cách đơn**.
4. Server trả `AC`/`WA`.

**Ví dụ case-sensitive:** `"Cherry apple Banana"` → `"Banana Cherry apple"` (hoa đứng trước thường).

## Code Java

```java
package q2021;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.json.JSONObject;

public class Main {
    static final String EXAM_IP      = "36.50.135.242";
    static final String STUDENT_CODE = "B21DCCN001";   // TODO
    static final String Q_CODE       = "GtBAcXjG";     // TODO
    static final String BASE         = "http://" + EXAM_IP + ":2230/api/rest/character";

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

        // Sort case-sensitive (mặc định String.compareTo là case-sensitive)
        String[] words = data.trim().split("\\s+");
        Arrays.sort(words);
        String answer = String.join(" ", words);
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
```

## Bẫy thường gặp

- **Q2021 là case-SENSITIVE** (khác với Q2121 gRPC là case-INsensitive).
- Sort case-sensitive = `Arrays.sort(words)` (dùng `String.compareTo`).
- Sort case-insensitive = `Arrays.sort(words, String.CASE_INSENSITIVE_ORDER)` — **không dùng cho bài này**.
- Nối bằng dấu cách **đơn**, không double-space.
