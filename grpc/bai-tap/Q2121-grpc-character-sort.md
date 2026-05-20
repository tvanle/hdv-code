# Q2121 — gRPC Character — Sort Words Alphabetically

| | |
|---|---|
| **Mức** | MEDIUM |
| **Giao thức** | `GRPC_CHARACTER` |
| **Server** | `<Exam_IP>:2240` |

## Đề bài

Dịch vụ gRPC `JudgeService` trên `<Exam_IP>:2240`.

**Yêu cầu:**

1. Gọi `Request(student_code, question_alias)` → nhận `JudgeResponse{request_id, data}` với `data` là các từ phân tách bằng dấu **phẩy**, ví dụ `"banana,apple,cherry,date"`.

2. Tách `data` thành list từ, sort theo thứ tự từ điển **case-insensitive**.

3. Gọi `Submit(...)` với `answer` là các từ đã sort, nối bằng **dấu phẩy**, ví dụ `"apple,banana,cherry,date"`.

4. Đóng kênh gRPC.

**Ví dụ:** `data = "banana,apple,cherry"` → `answer = "apple,banana,cherry"`.

## Proto contract

Dùng chung `judge.proto` của Q2111 — xem [Q2111-grpc-data-sum.md](Q2111-grpc-data-sum.md#proto-contract).

## Code Java

```java
package q2121;

import GRPC.JudgeRequest;
import GRPC.JudgeResponse;
import GRPC.JudgeServiceGrpc;
import GRPC.JudgeServiceGrpc.JudgeServiceBlockingStub;
import GRPC.SubmitRequest;
import GRPC.SubmitResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

public class Main {
    static final String EXAM_IP        = "36.50.135.242";
    static final int    EXAM_PORT      = 2240;
    static final String STUDENT_CODE   = "B21DCCN001";   // TODO
    static final String QUESTION_ALIAS = "Tqy8zjYo";     // TODO

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
            String data      = resp.getData();

            // Sort case-INSENSITIVE
            String[] words = data.split(",");
            Arrays.sort(words, String.CASE_INSENSITIVE_ORDER);
            String answer = String.join(",", words);
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

- **Q2121 case-INSENSITIVE** (khác Q2021 REST là case-sensitive). Dùng `String.CASE_INSENSITIVE_ORDER`.
- Phân tách bằng **dấu phẩy** (`,`), không phải khoảng trắng như Q2021.
- Nối lại bằng `","` không phải `" "`.
- Không trim các từ — server thường không có khoảng trắng dư, nếu có cần `.trim()` từng phần tử trước sort.
