# Q2261 — SOAP Object — Final Price (% Discount)

| | |
|---|---|
| **Mức** | HARD |
| **Giao thức** | `SOAP ObjectService` |
| **Endpoint** | `http://<Exam_IP>:2221/ObjectService?wsdl` |

## Đề bài

Tính `finalPrice` của `ProductY` với **discount là %** (giống Q2231, khác Q2232 dùng số tiền tuyệt đối).

## API

Giống Q2231/Q2232: `requestProductY`, `submitProductY`.

**ProductY**: `price`, `taxRate`, `discount`, `finalPrice` (đều float).

## Công thức

```
finalPrice = price * (1 + taxRate/100) * (1 - discount/100)
```

Round 2dp, gán `p.finalPrice`, submit.

## Code Python

Xem `Q2261_TinhGiaCuoiPhanTramV2.py` cùng folder.

## Bẫy thường gặp

- **3 bài cùng dạng nhưng công thức khác nhau**:
  - Q2231: discount = % → `* (1 - d/100)`
  - Q2232: discount = số tiền → `- d`
  - Q2261: discount = % → `* (1 - d/100)` (giống Q2231)
- Phải `float()` cast khi gán `finalPrice` (zeep yêu cầu kiểu khớp WSDL).
- Submit lại **chính object** đã nhận.
- Round 2dp **trước** khi gán.
