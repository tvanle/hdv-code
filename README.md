# Ôn luyện môn Phát triển hướng dịch vụ — PTIT

Bài tập JNP chia theo 3 protocol. Mỗi file Java = 1 bài, tên file = `Q<id>_<Title>.java` để dễ track.

## Layout

```
hdv-code/
├── rest/                                       Maven · org.json
│   ├── pom.xml
│   ├── bai-tap/                                9 file md đề gốc
│   ├── Q2011_DataSumOfIntegers.java
│   ├── Q2012_DataPaymentReconciliation.java
│   ├── Q2021_CharacterSortWords.java
│   ├── Q2022_CharacterMaskPII.java
│   ├── Q2031_ObjectFinalPrice.java
│   ├── Q2032_ObjectShippingQuote.java
│   ├── Q2041_MethodPut.java
│   ├── Q2051_HeaderChecksum.java
│   ├── Q2061_PathProductQuery.java
│   ├── Q2071_MethodPutAudit.java
│   ├── Q2072_MethodPatchIfMatch.java
│   ├── Q2081_HeaderChecksumReplay.java
│   ├── Q2082_HeaderHmacSignature.java
│   ├── Q2091_PathInvoiceQuery.java
│   └── Q2092_PathOverdueCustomer.java
├── grpc/                                       Maven · grpc-java + protoc plugin
│   ├── pom.xml
│   ├── bai-tap/                                3 file md
│   ├── proto/judge.proto
│   ├── Q2111_DataSumOfIntegers.java
│   ├── Q2121_CharacterSortWords.java
│   └── Q2131_ObjectFinalPrice.java
└── soap/                                       Maven · jaxws-rt + wsimport
    ├── pom.xml
    ├── README.md                               ghi chú API thực tế vs đề md
    ├── bai-tap/                                3 file md
    ├── Q2211_DataSumOfIntegers.java
    ├── Q2221_CharacterReverseString.java
    └── Q2231_ObjectFinalPrice.java
```

## Trước khi chạy

Trong từng `Q*.java`, sửa:
- `EXAM_IP` — IP máy thi
- `STUDENT_CODE` — mã sinh viên
- `Q_CODE` / `QUESTION_ALIAS` — qCode/qAlias ghi trong đề

## Cách chạy

```bash
# REST
cd rest
mvn -q compile
mvn -q exec:java -Dexec.mainClass=Q2011_DataSumOfIntegers

# gRPC (Maven sinh stub từ proto/judge.proto)
cd grpc
mvn -q compile
mvn -q exec:java -Dexec.mainClass=Q2111_DataSumOfIntegers

# SOAP (Maven chạy wsimport tự sinh stub từ WSDL — cần reach exam server)
cd soap
mvn -q compile
mvn -q exec:java -Dexec.mainClass=Q2211_DataSumOfIntegers
```

## Yêu cầu môi trường

| Mảng | Yêu cầu |
|------|--------|
| **JDK** | Java 11 trở lên |
| **Maven** | 3.6+ |
| **gRPC** | Maven tự download `protoc` + `protoc-gen-grpc-java` theo OS |
| **SOAP** | Cần reach `<exam.ip>:2221/<Service>?wsdl` lúc build (wsimport sinh stub) |

## Lưu ý đề thi

- `discount = %` (Q2031, Q2231) vs `discount = số tiền` (Q2131) — đọc kỹ đề.
- Sort **case-sensitive** (Q2021) vs **case-insensitive** (Q2121).
- Tất cả theo mô hình **2 phase**: GET/Request nhận `requestId` → tính → POST/PUT/Submit gửi `answer`.
- REST trả `{"status":"AC"}`; SOAP trả `void` (server không trả status string).

---

# 📋 Cheat-sheet cú pháp & cách kết nối

## 1. REST — `java.net.http.HttpClient` + `org.json`

### Imports chuẩn (luôn cần)
```java
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import org.json.JSONArray;
import org.json.JSONObject;
```

### Hằng số chuẩn
```java
static final String EXAM_IP      = "36.50.135.242";
static final String STUDENT_CODE = "B22DCCN863";
static final String Q_CODE       = "xxxxxx";    // qCode/qAlias ghi trong đề
static final String BASE         = "http://" + EXAM_IP + ":2230/api/rest/<group>";
// <group> = data | character | object | method | header | path
```

### Mẫu Phase-1 (GET request) — dùng chung mọi bài REST
```java
HttpClient client = HttpClient.newHttpClient();
String getUrl = BASE
        + "?studentCode=" + URLEncoder.encode(STUDENT_CODE, StandardCharsets.UTF_8)
        + "&qCode="       + URLEncoder.encode(Q_CODE,       StandardCharsets.UTF_8);

HttpResponse<String> getRes = client.send(
        HttpRequest.newBuilder().uri(URI.create(getUrl)).GET().build(),
        HttpResponse.BodyHandlers.ofString());

JSONObject body      = new JSONObject(getRes.body());
String     requestId = body.getString("requestId");
// body.getJSONArray("data") | body.getJSONObject("data")
```

### Các biến thể Phase-2 (submit)
| Dạng bài | HTTP method | Body |
|---------|-------------|------|
| `data/character/object` (Q2011-Q2032) | `POST /submit` | `{studentCode, qCode, requestId, answer}` |
| `method/PUT` (Q2041, Q2071) | `PUT /{requestId}` | `{studentCode, qCode, answer}` |
| `method/PATCH + If-Match` (Q2072) | `PATCH /{requestId}` + header `If-Match: <etag>` | `{studentCode, qCode, answer:{...}}` |
| `header/checksum` (Q2051) | `POST /submit` + header `X-Checksum: <hex>` | `{studentCode, qCode, requestId, answer}` |
| `header/HMAC` (Q2082) | `POST /submit` + header `X-Signature: <hex>` | `{studentCode, qCode, requestId}` |
| `path/invoice` (Q2091) | `GET /{requestId}` rồi `POST /submit` | `{...answer}` |

**Skeleton POST submit:**
```java
JSONObject submit = new JSONObject()
        .put("studentCode", STUDENT_CODE)
        .put("qCode",       Q_CODE)
        .put("requestId",   requestId)
        .put("answer",      sum);

HttpResponse<String> postRes = client.send(
        HttpRequest.newBuilder()
                .uri(URI.create(BASE + "/submit"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(submit.toString()))
                .build(),
        HttpResponse.BodyHandlers.ofString());
```

**Skeleton PATCH (Q2072):**
```java
.method("PATCH", BodyPublishers.ofString(submit.toString()))
.header("If-Match", etag)
```

**Skeleton HMAC-SHA256 (Q2082):**
```java
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

Mac mac = Mac.getInstance("HmacSHA256");
mac.init(new SecretKeySpec(signingKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
byte[] sig = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));

StringBuilder hex = new StringBuilder();
for (byte b : sig) hex.append(String.format("%02x", b));
String signature = hex.toString();
```

---

## 2. gRPC — `io.grpc:grpc-netty-shaded` + auto-gen stubs

### Imports chuẩn
```java
import GRPC.JudgeRequest;
import GRPC.JudgeResponse;
import GRPC.JudgeServiceGrpc;
import GRPC.JudgeServiceGrpc.JudgeServiceBlockingStub;
import GRPC.SubmitRequest;
import GRPC.SubmitResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.TimeUnit;
```

### Hằng số
```java
static final String EXAM_IP        = "36.50.135.242";
static final int    EXAM_PORT      = 2240;
static final String STUDENT_CODE   = "B22DCCN863";
static final String QUESTION_ALIAS = "xxxxx";
```

### Kết nối + cleanup
```java
ManagedChannel channel = ManagedChannelBuilder
        .forAddress(EXAM_IP, EXAM_PORT)
        .usePlaintext()
        .build();
try {
    JudgeServiceBlockingStub stub = JudgeServiceGrpc.newBlockingStub(channel);
    // ... request + submit ...
} finally {
    channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
}
```

### Hai họ service trong project

| Service | Bài dùng | Đặc điểm |
|---------|---------|---------|
| `JudgeService` (simple) | Q2111, Q2121, Q2131 | `data` & `answer` đều là **String**, tự parse trong client |
| `TypedJudgeService` (oneof) | Q2112, Q2113, Q2122, Q2123, Q2132, Q2133 | Server trả `TypedData` chứa `oneof` payload (TransactionRiskBatchData, SensorTelemetryData, TextBatchData, ShippingQuoteData, EnrollmentData) — client gửi `TypedAnswer` cùng kiểu |

**Skeleton `JudgeService`:**
```java
JudgeResponse resp = stub.request(JudgeRequest.newBuilder()
        .setStudentCode(STUDENT_CODE)
        .setQuestionAlias(QUESTION_ALIAS)
        .build());
String requestId = resp.getRequestId();
String data      = resp.getData();       // String — split(",") rồi parse

SubmitResponse sr = stub.submit(SubmitRequest.newBuilder()
        .setStudentCode(STUDENT_CODE)
        .setQuestionAlias(QUESTION_ALIAS)
        .setRequestId(requestId)
        .setAnswer(answerString)
        .build());
```

**Skeleton `TypedJudgeService`:**
```java
TypedData resp = stub.requestTyped(TypedRequest.newBuilder()
        .setStudentCode(STUDENT_CODE)
        .setQuestionAlias(QUESTION_ALIAS)
        .build());
String requestId    = resp.getRequestId();
SensorTelemetryData t = resp.getSensorTelemetry();   // chọn 1 trong các oneof

// ... tính toán ...

SensorTelemetryAnswer answer = SensorTelemetryAnswer.newBuilder()
        .setAverage(avg).setP95(p95).setAnomalyCount(n).build();

SubmitResponse sr = stub.submitTyped(TypedAnswer.newBuilder()
        .setStudentCode(STUDENT_CODE)
        .setQuestionAlias(QUESTION_ALIAS)
        .setRequestId(requestId)
        .setSensorTelemetry(answer)        // setter tương ứng oneof
        .build());
```

**Lưu ý gen stub:** chạy `mvn -q compile` trong `grpc/` → Maven gọi `protoc` sinh class trong `target/generated-sources/protobuf/`. Package gen là `GRPC` (theo `option java_package` trong `.proto`).

---

## 3. SOAP — `com.sun.xml.ws:jaxws-rt` + wsimport tự gen stub

### Imports chuẩn (theo từng bài, package gen riêng)
```java
import soap.q2211.generated.DataService;        // Service class
import soap.q2211.generated.SoapDataService;    // Port interface
// + các DTO bài dùng: ProductY, Customer, ...
```

### Hằng số (đơn giản hơn, KHÔNG cần EXAM_IP — đã nhúng trong WSDL lúc gen)
```java
static final String STUDENT_CODE = "B22DCCN863";
static final String Q_CODE       = "xxxxx";
```

### Mẫu kết nối + 2 phase
```java
DataService service = new DataService();
SoapDataService port = service.getSoapDataServicePort();   // port = stub thực tế

List<Integer> data = port.getData(STUDENT_CODE, Q_CODE);   // Phase 1
// ... tính ...
port.submitDataInt(STUDENT_CODE, Q_CODE, sum);             // Phase 2 — return void
```

### Mapping Service ↔ Port ↔ DTO (theo WSDL thực tế)

| Service class | Port getter | Bài dùng |
|---------------|-------------|---------|
| `DataService` | `getSoapDataServicePort()` → `SoapDataService` | Q2211-Q2213, Q2241-Q2242 |
| `CharacterService` | `getSoapCharacterServicePort()` → `SoapCharacterService` | Q2221-Q2223, Q2251-Q2252 |
| `ObjectService` | `getSoapObjectServicePort()` → `SoapObjectService` | Q2231-Q2233, Q2261-Q2262 |

| DTO | Trường thực tế (KHÁC đề md) |
|-----|----------------------------|
| `ProductY` | `discount`, `finalPrice`, `price`, `taxRate` — đều `float`, **không có** `name` |
| `Customer` | `customerId`, `location`, `purchaseCount` (int), `totalSpent` (float) |

### Lưu ý sinh stub
- `mvn -q compile` ở `soap/` → `jaxws-maven-plugin` chạy `wsimport` hit `http://36.50.135.242:2221/<Service>?wsdl` → sinh vào `target/generated-sources/jaxws/soap/q22xx/generated/`.
- **Phải reach exam server lúc build**, nếu không stub không gen được.
- `submit*` đều trả `void`, không có status string — log thủ công sau khi gọi để biết đã gửi.

---

## 4. Bảng so sánh nhanh 3 protocol

| Tiêu chí | REST | gRPC | SOAP |
|---------|------|------|------|
| **Wire format** | JSON text | Protobuf binary | XML |
| **Transport** | HTTP/1.1 | HTTP/2 | HTTP/1.1 |
| **Schema** | Không bắt buộc (org.json runtime) | `.proto` → gen stub compile-time | WSDL → gen stub compile-time |
| **Lib** | `java.net.http.HttpClient` (JDK 11+) | `grpc-netty-shaded` + plugin | `jaxws-rt` + `jaxws-maven-plugin` |
| **Cổng exam** | 2230 | 2240 | 2221 |
| **Phase 1** | GET `?studentCode&qCode` | `stub.request(...)` / `stub.requestTyped(...)` | `port.get<X>(student, q)` |
| **Phase 2** | POST/PUT/PATCH `/submit` hoặc `/{requestId}` | `stub.submit(...)` / `stub.submitTyped(...)` | `port.submit<X>(student, q, answer)` |
| **Response submit** | `{"status":"AC", ...}` | `SubmitResponse{status, message}` | **void** |
| **Encoding param** | Cần `URLEncoder` cho path/query có ký tự lạ | Tự lo bởi protobuf | Tự lo bởi JAX-WS marshalling |
