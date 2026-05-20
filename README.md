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
