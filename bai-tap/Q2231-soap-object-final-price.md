# Q2231 — SOAP Object — Calculate Final Price

| | |
|---|---|
| **Mức** | MEDIUM |
| **Giao thức** | `SOAP_OBJECT` |
| **WSDL** | `http://<Exam_IP>:2221/ObjectService?wsdl` |

## Đề bài

Dịch vụ SOAP `ObjectService`.

**Yêu cầu:**

1. Sinh Web Service Client từ WSDL.
   Gọi `requestProductY(studentCode, qCode)` → nhận đối tượng `ProductY` với fields `name`, `price`, `taxRate`, `discount`, `finalPrice` (giá trị ban đầu = 0).

   Ví dụ: `ProductY{name="Laptop", price=1000.0, taxRate=10.0, discount=5.0, finalPrice=0.0}`

2. Tính `finalPrice` (`discount` là **phần trăm %**):
   ```
   finalPrice = price * (1 + taxRate / 100) * (1 - discount / 100)
   ```
   Ví dụ: `1000 × 1.10 × 0.95 = 1045.0`.

3. Set `finalPrice` vào đối tượng → gọi `submitProductY(studentCode, qCode, productY)`.

4. Server trả status (`"AC"` / `"WA"`).

## Sinh client từ WSDL

```bash
wsimport -keep -s src/main/java -p soap.q2231.generated \
    http://<Exam_IP>:2221/ObjectService?wsdl
```

## Code Java

```java
package q2231;

import soap.q2231.generated.ObjectService;
import soap.q2231.generated.ObjectService_Service;
import soap.q2231.generated.ProductY;

public class Main {
    static final String STUDENT_CODE = "B21DCCN001";   // TODO
    static final String Q_CODE       = "z7a6WujT";     // TODO

    public static void main(String[] args) {
        ObjectService_Service service = new ObjectService_Service();
        ObjectService port = service.getObjectServicePort();

        // Phase 1 — requestProductY
        ProductY p = port.requestProductY(STUDENT_CODE, Q_CODE);
        System.out.printf("name=%s price=%.2f tax=%.2f disc=%.2f%n",
                p.getName(), p.getPrice(), p.getTaxRate(), p.getDiscount());

        // Bước 2 — tính finalPrice (% discount)
        double finalPrice = p.getPrice()
                          * (1 + p.getTaxRate()  / 100.0)
                          * (1 - p.getDiscount() / 100.0);
        p.setFinalPrice(finalPrice);
        System.out.printf("finalPrice=%.4f%n", finalPrice);

        // Phase 2 — submitProductY
        String status = port.submitProductY(STUDENT_CODE, Q_CODE, p);
        System.out.println("Server response: " + status);
    }
}
```

## Bẫy thường gặp

- **Q2231 dùng `discount` là %** (giống Q2031 REST, khác Q2131 gRPC).
- Phải set `finalPrice` vào **đúng đối tượng đã nhận** (giữ nguyên `name`, `price`, `taxRate`, `discount`) rồi mới `submit`. Tạo `ProductY` mới có thể thiếu field nếu wsimport sinh constructor mặc định.
- Getter/setter tự sinh: `getName()`, `setFinalPrice(double)` — đúng theo schema XSD.
- Nếu JDK 11+ cần jar `jakarta.xml.ws` cho runtime SOAP.
