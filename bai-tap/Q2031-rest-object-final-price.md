# Q2031 — REST Object — Calculate Final Price

| | |
|---|---|
| **Mức** | MEDIUM |
| **Giao thức** | `REST_OBJECT` |
| **Endpoint** | `http://<Exam_IP>:2230/api/rest/object` |

## Đề bài

Một dịch vụ REST tại `http://<Exam_IP>:2230/api/rest/object` xử lý bài toán với đối tượng.

**Yêu cầu:**

1. `GET /api/rest/object?studentCode=<mã_sv>&qCode=<qCode>` → nhận:
   ```json
   {
     "requestId": "m1n2o3p4",
     "data": {
       "name": "Laptop Pro",
       "price": 100.0,
       "taxRate": 10.0,
       "discount": 5.0
     }
   }
   ```
   `discount` là **phần trăm chiết khấu (%)**.

2. Tính `finalPrice`:
   ```
   finalPrice = price × (1 + taxRate / 100) × (1 - discount / 100)
   ```

3. `POST /api/rest/object/submit` với body:
   ```json
   {
     "studentCode": "B21DCCN001",
     "qCode": "<qCode>",
     "requestId": "m1n2o3p4",
     "answer": {
       "name": "Laptop Pro",
       "price": 100.0,
       "taxRate": 10.0,
       "discount": 5.0,
       "finalPrice": 104.5
     }
   }
   ```

**Sai số cho phép:** `<= 0.01`. Ví dụ: `price=100, tax=10, disc=5` → `100 × 1.1 × 0.95 = 104.5`.

## Code Java

```java
package q2031;

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
    static final String Q_CODE       = "s543QlZR";     // TODO
    static final String BASE         = "http://" + EXAM_IP + ":2230/api/rest/object";

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
        JSONObject data      = body.getJSONObject("data");

        String name     = data.getString("name");
        double price    = data.getDouble("price");
        double taxRate  = data.getDouble("taxRate");
        double discount = data.getDouble("discount");

        // Bước 2 — tính finalPrice (% discount)
        double finalPrice = price * (1 + taxRate / 100.0) * (1 - discount / 100.0);
        System.out.printf("finalPrice=%.4f%n", finalPrice);

        // Phase 2 — POST
        JSONObject answer = new JSONObject()
                .put("name",       name)
                .put("price",      price)
                .put("taxRate",    taxRate)
                .put("discount",   discount)
                .put("finalPrice", finalPrice);

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

- **Q2031 dùng `discount` là PHẦN TRĂM** → công thức nhân `(1 - discount/100)`.
- **Khác với Q2131 (gRPC)** ở đó `discount` là **số tiền tuyệt đối** → trừ trực tiếp.
- `answer` là **object** (không phải số) — bao đủ 5 field gốc + `finalPrice`.
- Không cần làm tròn ở phía client; server cho sai số ≤ 0.01.
