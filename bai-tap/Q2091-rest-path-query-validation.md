# Q2091 — [REST] Path + Query Validation

| | |
|---|---|
| **Mức** | HARD |
| **Giao thức** | `REST_PATH` |
| **Endpoint** | `http://<Exam_IP>:2230/api/rest/path` |

## Đề bài

Chọn invoice hợp lệ từ Phase 1 rồi gọi Phase 2 với path param + query param đúng chuẩn.

### Giao thức

**Bước 1 — GET danh sách invoice:**
```
GET /api/rest/path?studentCode=<mã_sv>&qCode=<qAlias>
```

**Bước 2 — GET theo path + query:**
```
GET /api/rest/path/{invoiceId}?studentCode=<mã_sv>&qCode=<qAlias>&requestId=<requestId phase1>&currency=USD
```
Ví dụ:
```
GET /api/rest/path/2?studentCode=B22DCCN001&qCode=z3Np8Rk1&requestId=p4Ks7n2Q&currency=USD
```

### Yêu cầu

- `invoiceId` phải có trong danh sách Phase 1.
- `currency` phải là `USD`.
- Phase 2 chỉ chấp nhận **GET**.

## Code Java

```java
package q2091;

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
    static final String STUDENT_CODE = "B22DCCN001";   // TODO
    static final String Q_CODE       = "CYFvYaOu";     // TODO
    static final String BASE         = "http://" + EXAM_IP + ":2230/api/rest/path";

    public static void main(String[] args) throws Exception {
        HttpClient client = HttpClient.newHttpClient();

        // Phase 1 — GET danh sách invoice
        String getUrl = BASE
                + "?studentCode=" + URLEncoder.encode(STUDENT_CODE, StandardCharsets.UTF_8)
                + "&qCode="       + URLEncoder.encode(Q_CODE,       StandardCharsets.UTF_8);

        HttpResponse<String> phase1 = client.send(
                HttpRequest.newBuilder().uri(URI.create(getUrl)).GET().build(),
                HttpResponse.BodyHandlers.ofString());

        JSONObject body      = new JSONObject(phase1.body());
        String     requestId = body.getString("requestId");
        JSONArray  invoices  = body.getJSONArray("data");

        // Chọn invoiceId đầu tiên trong danh sách
        int invoiceId = invoices.getJSONObject(0).getInt("id");
        System.out.println("invoiceId=" + invoiceId + ", requestId=" + requestId);

        // Phase 2 — GET với path + query
        String phase2Url = BASE + "/" + invoiceId
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

- **Giống Q2061** về cấu trúc — chỉ khác tên tài nguyên (invoice thay vì product). Có thể tái sử dụng code.
- `invoiceId` lấy từ field `id` của object trong mảng `data`.
- `currency=USD` viết hoa.
- Đừng quên truyền `requestId` qua **query**, không phải header hay body.
