# Q2211 — SOAP Data — Sum of Integers

| | |
|---|---|
| **Mức** | MEDIUM |
| **Giao thức** | `SOAP_DATA` |
| **WSDL** | `http://<Exam_IP>:2221/DataService?wsdl` |

## Đề bài

Dịch vụ SOAP `DataService`.

**Yêu cầu:**

1. Sinh Web Service Client từ WSDL.
   Gọi `getData(studentCode, qCode)` → nhận `List<Integer>`. Ví dụ `[12, 45, 88, 3, 210, 50]`.

2. Tính **tổng** tất cả phần tử.

3. Gọi `submitDataInt(studentCode, qCode, sum)` để gửi tổng về server.

4. Server trả status (`"AC"` / `"WA"`).

**Ví dụ:** `[1, 2, 3, 4, 5]` → `15` → `submitDataInt("B21DCCN001", "<qCode>", 15)`.

## Sinh client từ WSDL

```bash
# JDK 8 — wsimport có sẵn
wsimport -keep -s src/main/java -p soap.q2211.generated \
    http://<Exam_IP>:2221/DataService?wsdl

# JDK 11+: cài jakarta-xml-ws-tools (https://eclipse-ee4j.github.io/metro-jax-ws/)
# Hoặc NetBeans: chuột phải project → New → Web Service Client → dán WSDL URL.
```

`wsimport` sẽ sinh ra (tùy WSDL):
- `DataService_Service` (Service stub)
- `DataService` (Port interface)
- Các class request/response wrapper

## Code Java

```java
package q2211;

import java.util.List;
import soap.q2211.generated.DataService;
import soap.q2211.generated.DataService_Service;
// LƯU Ý: tên class có thể khác tùy WSDL — kiểm tra trong package `generated/` sau khi wsimport.

public class Main {
    static final String STUDENT_CODE = "B21DCCN001";   // TODO
    static final String Q_CODE       = "irq9etkc";     // TODO

    public static void main(String[] args) {
        DataService_Service service = new DataService_Service();
        DataService port = service.getDataServicePort();

        // Phase 1 — getData
        List<Integer> data = port.getData(STUDENT_CODE, Q_CODE);
        System.out.println("data=" + data);

        // Bước 2 — tính tổng
        int sum = data.stream().mapToInt(Integer::intValue).sum();
        System.out.println("sum=" + sum);

        // Phase 2 — submitDataInt
        String status = port.submitDataInt(STUDENT_CODE, Q_CODE, sum);
        System.out.println("Server response: " + status);
    }
}
```

## Bẫy thường gặp

- Tên class sinh ra phụ thuộc WSDL — kiểm tra `generated/` package, đừng đoán bừa.
- `getData` trả về `List<Integer>` (auto-boxed) hoặc `int[]` — tùy binding.
- Nếu `sum` quá lớn vượt `Integer.MAX_VALUE` → dùng `long` rồi cast lại, nhưng signature `submitDataInt` nhận `int` nên thường an toàn với input đề.
- Nếu chạy trên JDK 11+, cần thêm jar `jaxws-rt`/`jakarta.xml.ws-api` vì JDK 11 đã bỏ JAX-WS khỏi runtime.
