# Ôn luyện môn Phát triển hướng dịch vụ — PTIT

Bộ 15 bài tập JNP (REST · gRPC · SOAP) kèm code Java mẫu.

## Cấu trúc

```
ôn luyện/
├── README.md                  ← bạn đang đọc
├── bai-tap-jnp.md             ← index (đề mục tất cả 15 bài)
└── bai-tap/                   ← từng bài có 1 file .md riêng (đề + code)
    ├── Q2011-rest-data-sum.md
    └── ...
```

## Yêu cầu môi trường

| Mảng | Yêu cầu |
|------|--------|
| **JDK** | Java 11 trở lên (dùng `java.net.http.HttpClient` cho REST) |
| **REST** | Thêm jar `org.json` (~70 KB) — [Download](https://search.maven.org/artifact/org.json/json) |
| **gRPC** | Maven/Gradle với `io.grpc:grpc-netty-shaded`, `grpc-protobuf`, `grpc-stub`, `protobuf-java` + plugin `protoc-gen-grpc-java` |
| **SOAP** | `wsimport` (JDK 8) hoặc `jakarta-xml-ws-tools` (JDK 11+). NetBeans/IntelliJ có wizard sinh client từ WSDL. |

### Maven dependency cho REST (org.json)
```xml
<dependency>
  <groupId>org.json</groupId>
  <artifactId>json</artifactId>
  <version>20240303</version>
</dependency>
```

### Maven dependency cho gRPC
```xml
<dependencies>
  <dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-netty-shaded</artifactId>
    <version>1.65.1</version>
  </dependency>
  <dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-protobuf</artifactId>
    <version>1.65.1</version>
  </dependency>
  <dependency>
    <groupId>io.grpc</groupId>
    <artifactId>grpc-stub</artifactId>
    <version>1.65.1</version>
  </dependency>
  <dependency>
    <groupId>javax.annotation</groupId>
    <artifactId>javax.annotation-api</artifactId>
    <version>1.3.2</version>
  </dependency>
</dependencies>
```

## Cách chạy (REST)

```bash
javac -cp .:json-20240303.jar Main.java
java  -cp .:json-20240303.jar Main
```

Trước khi chạy, sửa 3 hằng số trong mỗi `Main.java`:
- `EXAM_IP` — IP máy thi (ví dụ `36.50.135.242`)
- `STUDENT_CODE` — mã sinh viên của bạn (ví dụ `B21DCCN001`)
- `Q_CODE` — `qCode` (hay `qAlias`) ghi trong đề thi

## Cách chạy (gRPC)

```bash
# Sinh stub từ judge.proto
protoc --java_out=src/main/java --grpc-java_out=src/main/java judge.proto

# Build & run
mvn compile exec:java -Dexec.mainClass=q2111.Main
```

## Cách chạy (SOAP)

```bash
# Sinh client stub từ WSDL
wsimport -keep -s src/main/java -p soap.q2211.generated \
    http://<Exam_IP>:2221/DataService?wsdl

# Hoặc trong NetBeans: New > Other > Web Service Client → dán WSDL URL
```

## Lưu ý đề thi

- Một số bài dùng `discount = %` (Q2031, Q2231), bài khác dùng `discount = số tiền` (Q2131) — đọc kỹ đề!
- Sort có bài **case-sensitive** (Q2021), bài **case-insensitive** (Q2121) — đừng nhầm.
- Tất cả bài đều theo mô hình **2 phase**: GET nhận `requestId` → tính toán → POST/PUT trả `answer` kèm `requestId`.
- Response cuối là `{"status":"AC"}` (Accepted) hoặc `{"status":"WA"}` (Wrong Answer).
