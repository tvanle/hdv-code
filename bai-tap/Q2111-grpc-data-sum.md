# Q2111 — gRPC Data — Sum of Integers

| | |
|---|---|
| **Mức** | MEDIUM |
| **Giao thức** | `GRPC_DATA` |
| **Server** | `<Exam_IP>:2240` (plaintext, không TLS) |

## Đề bài

Dịch vụ gRPC `JudgeService` trên `<Exam_IP>:2240`.

**Yêu cầu:**

1. Tạo kênh gRPC (plaintext) và gọi `Request(student_code, question_alias)`.
   Nhận `JudgeResponse{request_id, data}`, ví dụ `data = "12,45,88,3,210"`.

2. Parse chuỗi `data` thành list số nguyên và tính **tổng**.

3. Gọi `Submit(student_code, question_alias, request_id, answer)` với `answer` là chuỗi tổng, ví dụ `"141"`.

4. Đóng kênh gRPC.

**Ví dụ:** `data = "1,2,3,4,5"` → tổng = `15` → `answer = "15"`.

## Proto contract

Lưu thành `src/main/proto/judge.proto`:

```proto
syntax = "proto3";
package GRPC;
option java_package = "GRPC";
option java_multiple_files = true;

service JudgeService {
  rpc Request (JudgeRequest) returns (JudgeResponse);
  rpc Submit  (SubmitRequest) returns (SubmitResponse);
}

message JudgeRequest {
  string student_code   = 1;
  string question_alias = 2;
}

message JudgeResponse {
  string request_id = 1;
  string data       = 2;
}

message SubmitRequest {
  string student_code   = 1;
  string question_alias = 2;
  string request_id     = 3;
  string answer         = 4;
}

message SubmitResponse {
  string status  = 1;
  string message = 2;
}
```

> ⚠️ `package GRPC` và field numbers phải giữ nguyên — sai wire format protobuf.

## Code Java

```java
package q2111;

import GRPC.JudgeRequest;
import GRPC.JudgeResponse;
import GRPC.JudgeServiceGrpc;
import GRPC.JudgeServiceGrpc.JudgeServiceBlockingStub;
import GRPC.SubmitRequest;
import GRPC.SubmitResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.TimeUnit;

public class Main {
    static final String EXAM_IP        = "36.50.135.242";
    static final int    EXAM_PORT      = 2240;
    static final String STUDENT_CODE   = "B21DCCN001";   // TODO
    static final String QUESTION_ALIAS = "gDdfzR0y";     // TODO

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(EXAM_IP, EXAM_PORT)
                .usePlaintext()
                .build();

        try {
            JudgeServiceBlockingStub stub = JudgeServiceGrpc.newBlockingStub(channel);

            // Phase 1 — Request
            JudgeResponse resp = stub.request(JudgeRequest.newBuilder()
                    .setStudentCode(STUDENT_CODE)
                    .setQuestionAlias(QUESTION_ALIAS)
                    .build());

            String requestId = resp.getRequestId();
            String data      = resp.getData();
            System.out.println("requestId=" + requestId + ", data=" + data);

            // Bước 2 — parse và cộng
            long sum = 0;
            for (String t : data.split(",")) {
                if (!t.isEmpty()) sum += Long.parseLong(t.trim());
            }
            System.out.println("sum=" + sum);

            // Phase 2 — Submit
            SubmitResponse sr = stub.submit(SubmitRequest.newBuilder()
                    .setStudentCode(STUDENT_CODE)
                    .setQuestionAlias(QUESTION_ALIAS)
                    .setRequestId(requestId)
                    .setAnswer(Long.toString(sum))
                    .build());

            System.out.println("status=" + sr.getStatus() + ", message=" + sr.getMessage());
        } finally {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
```

## Bẫy thường gặp

- `answer` là **String** (gRPC field `string`), không phải int → `Long.toString(sum)`.
- Phải close channel với `channel.shutdown().awaitTermination(...)`, không hệ thống treo.
- `usePlaintext()` bắt buộc (server không TLS).
- Field tên Java sinh ra: `student_code` (proto) → `setStudentCode(...)` (Java) — Protobuf tự chuyển snake_case → camelCase.
