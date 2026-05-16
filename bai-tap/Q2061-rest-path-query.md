# Q2061 — [REST] Path Parameter + Query Parameter

| | |
|---|---|
| **Mức** | HARD |
| **Giao thức** | `REST_PATH` |
| **Endpoint** | `http://<Exam_IP>:2230/api/rest/path` |

## Đề bài

Sử dụng **path parameter** + **query parameter** để truy vấn một tài nguyên cụ thể trong danh sách.

### Giao thức

**Bước 1 — GET danh sách sản phẩm:**
```
GET /api/rest/path?studentCode=<mã_sv>&qCode=<qAlias>
```
Phản hồi:
```json
{
  "requestId": "ghi01234",
  "data": [
    {"id": 1, "name": "Laptop",     "priceVND": 15000000},
    {"id": 2, "name": "Smartphone", "priceVND": 8500000},
    {"id": 3, "name": "Tablet",     "priceVND": 6200000}
  ]
}
```

**Bước 2 — GET theo path + query:**
```
GET /api/rest/path/{productId}?studentCode=<mã_sv>&qCode=<qAlias>&requestId=ghi01234&currency=USD
```
Ví dụ id=2:
```
GET /api/rest/path/2?studentCode=B22DCCN001&qCode=...&requestId=ghi01234&currency=USD
```

### Yêu cầu

- Chọn `id` hợp lệ từ danh sách Phase 1 → đưa vào path.
- `requestId` từ Phase 1 → query parameter.
- `currency=USD` → query parameter.
- Endpoint Phase 2 **chỉ chấp nhận GET**.

## Code Java

```java
package q2061;

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
    static final String Q_CODE       = "Kcol2JVM";     // TODO
    static final String BASE         = "http://" + EXAM_IP + ":2230/api/rest/path";

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        // Phase 1 — GET danh sách
        String getUrl = BASE
                + "?studentCode=" + URLEncoder.encode(STUDENT_CODE, StandardCharsets.UTF_8)
                + "&qCode="       + URLEncoder.encode(Q_CODE,       StandardCharsets.UTF_8);

        HttpResponse<String> phase1 = client.send(
                HttpRequest.newBuilder().uri(URI.create(getUrl)).GET().build(),
                HttpResponse.BodyHandlers.ofString());

        JSONObject body      = new JSONObject(phase1.body());
        String     requestId = body.getString("requestId");
        JSONArray  products  = body.getJSONArray("data");

        // Chọn id hợp lệ đầu tiên
        int productId = products.getJSONObject(0).getInt("id");
        System.out.println("Chọn productId=" + productId);

        // Phase 2 — GET /api/rest/path/{id}?studentCode=&qCode=&requestId=&currency=USD
        String phase2Url = BASE + "/" + productId
                + "?studentCode=" + URLEncoder.encode(STUDENT_CODE, StandardCharsets.UTF_8)
                + "&qCode="       + URLEncoder.encode(Q_CODE,       StandardCharsets.UTF_8)
                + "&requestId="   + URLEncoder.encode(requestId,    StandardCharsets.UTF_8)
                + "&currency=USD";

        HttpResponse<String> phase2 = client.send(
                HttpRequest.newBuilder().uri(URI.create(phase2Url)).GET().build(),
                HttpResponse.BodyHandlers.ofString());

        System.out.println("Server response: " + phase2.body());
    }
}
```

## Bẫy thường gặp

- Phase 2 là **GET**, không POST/PUT.
- Path param **phải** là `id` thực sự có trong danh sách Phase 1 (đừng hardcode `1` nếu danh sách không có id=1).
- `currency=USD` viết hoa, không phải `usd`.
- URL-encode `requestId` đề phòng ký tự đặc biệt.
