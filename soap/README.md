# SOAP exercises — Q2211 / Q2221 / Q2231

## Quy trình trong phòng thi

1. **Sửa IP máy thi** trong `pom.xml` (`<exam.ip>` ở phần `<properties>`).
2. **Mở khối `<executions>`** trong `jaxws-maven-plugin` (xoá `<!-- ... -->` bao quanh) — đây là bước sinh stub từ WSDL của 3 service `DataService`, `CharacterService`, `ObjectService`.
3. **Sửa `STUDENT_CODE` và `Q_CODE`** trong từng `src/main/java/q*/Main.java`.
4. Build & chạy:

   ```bash
   # Sinh stub + compile
   mvn -q clean compile

   # Chạy 1 bài
   mvn -q exec:java -Dexec.mainClass=q2211.Main
   ```

## API thực tế từ WSDL (khác đề md mô tả!)

Sau khi wsimport sinh stub thật, một số điểm cần lưu ý vì **khác với đề bản md**:

| Service | Service class | Port getter | Port interface |
|---|---|---|---|
| `DataService` | `DataService` (không có suffix `_Service`) | `getSoapDataServicePort()` | `SoapDataService` |
| `CharacterService` | `CharacterService` | `getSoapCharacterServicePort()` | `SoapCharacterService` |
| `ObjectService` | `ObjectService` | `getSoapObjectServicePort()` | `SoapObjectService` |

- Cả 3 method `submitX(...)` đều **return `void`** — server không trả status string.
- `ProductY` (Q2231) chỉ có 4 field `float`: `discount`, `finalPrice`, `price`, `taxRate` — **không có `name`**. Phải `(float) finalPrice` khi `setFinalPrice`.

## Phương án dự phòng — wsimport thủ công

Nếu plugin lỗi, sinh stub thủ công vào `src/main/java/`:

```bash
# JDK 8 (có sẵn wsimport)
wsimport -keep -s src/main/java -p soap.q2211.generated \
    http://<Exam_IP>:2221/DataService?wsdl

wsimport -keep -s src/main/java -p soap.q2221.generated \
    http://<Exam_IP>:2221/CharacterService?wsdl

wsimport -keep -s src/main/java -p soap.q2231.generated \
    http://<Exam_IP>:2221/ObjectService?wsdl
```

JDK 11+ thì cài `jakarta-xml-ws-tools` từ <https://eclipse-ee4j.github.io/metro-jax-ws/> để có lệnh `wsimport`.

## Lưu ý đề thi

- **Tên class generated phụ thuộc WSDL** — nếu wsimport sinh `DataServiceService` thay vì `DataService_Service`, sửa lại import/khởi tạo trong `Main.java` cho khớp.
- **`Q2231` dùng `discount` là %** (giống Q2031 REST, khác Q2131 gRPC trừ trực tiếp).
- Phải **set `finalPrice` vào đúng object đã nhận** từ Phase 1 (giữ nguyên các field khác), không tạo ProductY mới.
