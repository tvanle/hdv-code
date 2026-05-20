# Q2131 — gRPC Object — Calculate Final Price

| | |
|---|---|
| **Mức** | MEDIUM |
| **Giao thức** | `GRPC_OBJECT` |
| **Server** | `<Exam_IP>:2240` |

## Đề bài

Dịch vụ gRPC `JudgeService` trên `<Exam_IP>:2240`.

**Yêu cầu:**

1. Gọi `Request(...)` → nhận `data` là chuỗi JSON, ví dụ:
   ```json
   {"name":"ProductABC","price":150.0,"taxRate":10.0,"discount":15.0}
   ```
   Trong đó `discount` là **giá trị tuyệt đối** (số tiền chiết khấu, KHÔNG phải %).

2. Tính `finalPrice`:
   ```
   finalPrice = price × (1 + taxRate / 100) - discount
   ```

3. Gọi `Submit(...)` với `answer` là `finalPrice` làm tròn **2 chữ số thập phân**, dạng String, ví dụ `"150.00"`.

4. Đóng kênh gRPC.

**Ví dụ:** `price=150, tax=10, disc=15` → `150 × 1.1 - 15 = 150.00`.
**Sai số cho phép:** `<= 0.01`.

## Proto contract

Dùng chung `judge.proto` của Q2111.

## Code Java

```java
package q2131;

import GRPC.JudgeRequest;
import GRPC.JudgeResponse;
import GRPC.JudgeServiceGrpc;
import GRPC.JudgeServiceGrpc.JudgeServiceBlockingStub;
import GRPC.SubmitRequest;
import GRPC.SubmitResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;

public class Main {
    static final String EXAM_IP        = "36.50.135.242";
    static final int    EXAM_PORT      = 2240;
    static final String STUDENT_CODE   = "B21DCCN001";   // TODO
    static final String QUESTION_ALIAS = "WEFtuyKl";     // TODO

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(EXAM_IP, EXAM_PORT)
                .usePlaintext()
                .build();

        try {
            JudgeServiceBlockingStub stub = JudgeServiceGrpc.newBlockingStub(channel);

            // Phase 1
            JudgeResponse resp = stub.request(JudgeRequest.newBuilder()
                    .setStudentCode(STUDENT_CODE)
                    .setQuestionAlias(QUESTION_ALIAS)
                    .build());

            String requestId = resp.getRequestId();
            JSONObject product = new JSONObject(resp.getData());

            double price    = product.getDouble("price");
            double taxRate  = product.getDouble("taxRate");
            double discount = product.getDouble("discount");

            // Bước 2 — discount là SỐ TIỀN, không phải %
            double finalPrice = price * (1 + taxRate / 100.0) - discount;
            String answer     = String.format(Locale.US, "%.2f", finalPrice);
            System.out.println("answer=" + answer);

            // Phase 2
            SubmitResponse sr = stub.submit(SubmitRequest.newBuilder()
                    .setStudentCode(STUDENT_CODE)
                    .setQuestionAlias(QUESTION_ALIAS)
                    .setRequestId(requestId)
                    .setAnswer(answer)
                    .build());

            System.out.println("status=" + sr.getStatus() + ", message=" + sr.getMessage());
        } finally {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
```

## Bẫy thường gặp

- **Q2131: `discount` là SỐ TIỀN** → công thức `... - discount` (trừ thẳng).
- **Khác hẳn Q2031 / Q2231** (`discount` là % → nhân `(1 - discount/100)`). **Đừng nhầm!**
- Dùng `Locale.US` trong `String.format` để chắc chắn dùng dấu chấm `.` thập phân (Việt Nam mặc định dùng dấu phẩy `,`).
- `answer` phải có **đúng 2 chữ số thập phân**: `"150.00"` không phải `"150.0"` hay `"150"`.
