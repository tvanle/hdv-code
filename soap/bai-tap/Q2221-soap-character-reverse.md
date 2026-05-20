# Q2221 — SOAP Character — Reverse a String

| | |
|---|---|
| **Mức** | EASY |
| **Giao thức** | `SOAP_CHARACTER` |
| **WSDL** | `http://<Exam_IP>:2221/CharacterService?wsdl` |

## Đề bài

Dịch vụ SOAP `CharacterService`.

**Yêu cầu:**

1. Sinh Web Service Client từ WSDL.
   Gọi `requestString(studentCode, qCode)` → nhận một `String`. Ví dụ `"HelloWorld"`.

2. **Đảo ngược** chuỗi nhận được. Ví dụ `"HelloWorld"` → `"dlroWolleH"`.

3. Gọi `submitString(studentCode, qCode, reversedString)`.

4. Server trả status (`"AC"` / `"WA"`).

## Sinh client từ WSDL

```bash
wsimport -keep -s src/main/java -p soap.q2221.generated \
    http://<Exam_IP>:2221/CharacterService?wsdl
```

## Code Java

```java
package q2221;

import soap.q2221.generated.CharacterService;
import soap.q2221.generated.CharacterService_Service;

public class Main {
    static final String STUDENT_CODE = "B21DCCN001";   // TODO
    static final String Q_CODE       = "8QVq0OaK";     // TODO

    public static void main(String[] args) {
        CharacterService_Service service = new CharacterService_Service();
        CharacterService port = service.getCharacterServicePort();

        // Phase 1
        String data = port.requestString(STUDENT_CODE, Q_CODE);
        System.out.println("data=" + data);

        // Bước 2 — đảo ngược
        String reversed = new StringBuilder(data).reverse().toString();
        System.out.println("reversed=" + reversed);

        // Phase 2
        String status = port.submitString(STUDENT_CODE, Q_CODE, reversed);
        System.out.println("Server response: " + status);
    }
}
```

## Bẫy thường gặp

- `new StringBuilder(s).reverse().toString()` là cách ngắn gọn nhất.
- Nếu đề có ký tự Unicode (emoji, surrogate pair), reverse theo char có thể vỡ — nhưng đề thi PTIT thường chỉ ASCII, không phải lo.
- Tên port/service phụ thuộc WSDL: thường là `<ServiceName>_Service` + `get<ServiceName>Port()`.
