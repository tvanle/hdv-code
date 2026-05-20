# Ôn luyện môn Phát triển hướng dịch vụ — PTIT

Bài tập JNP chia theo 3 protocol — mỗi folder là 1 Maven project chạy được độc lập.

## Layout

```
hdv-code/
├── rest/                 ← 15 bài REST (Maven, org.json)
│   ├── pom.xml
│   ├── bai-tap/          ← đề + code mẫu (.md)
│   └── src/main/java/q20**/Main.java
├── grpc/                 ← 3 bài gRPC (Maven, grpc-java + protoc plugin)
│   ├── pom.xml
│   ├── bai-tap/
│   ├── src/main/proto/judge.proto
│   └── src/main/java/q21**/Main.java
└── soap/                 ← 3 bài SOAP (Maven, jaxws-rt + jaxws-maven-plugin)
    ├── pom.xml
    ├── README.md         ← lưu ý API thực vs đề md
    ├── bai-tap/
    └── src/main/java/q22**/Main.java
```

## Trước khi chạy

Trong từng `Main.java`, sửa:
- `EXAM_IP` — IP máy thi
- `STUDENT_CODE` — mã sinh viên
- `Q_CODE` / `QUESTION_ALIAS` — qCode/qAlias ghi trong đề

## Cách chạy

```bash
# REST — chỉ cần Maven + JDK 11+
cd rest && mvn -q compile
mvn -q exec:java -Dexec.mainClass=q2011.Main

# gRPC — Maven sinh stub từ judge.proto qua protobuf-maven-plugin
cd grpc && mvn -q compile
mvn -q exec:java -Dexec.mainClass=q2111.Main

# SOAP — Maven chạy wsimport tự sinh stub từ WSDL (cần kết nối tới exam server)
cd soap && mvn -q compile
mvn -q exec:java -Dexec.mainClass=q2211.Main
```

## Yêu cầu môi trường

| Mảng | Yêu cầu |
|------|--------|
| **JDK** | Java 11 trở lên |
| **Maven** | 3.6+ |
| **gRPC** | Maven tự download `protoc` + `protoc-gen-grpc-java` theo OS |
| **SOAP** | Cần reach `<Exam_IP>:2221/<Service>?wsdl` lúc build (wsimport sinh stub) |

## Lưu ý đề thi

- Một số bài dùng `discount = %` (Q2031, Q2231), bài khác dùng `discount = số tiền` (Q2131) — đọc kỹ đề!
- Sort có bài **case-sensitive** (Q2021), bài **case-insensitive** (Q2121) — đừng nhầm.
- Tất cả bài đều theo mô hình **2 phase**: GET nhận `requestId` → tính toán → POST/PUT/Submit trả `answer`.
- Response cuối: REST trả `{"status":"AC"}`; SOAP trả `void` (không có status string từ server).
