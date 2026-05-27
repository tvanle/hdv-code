# Q2232 — SOAP Object — Final Price (Absolute Discount)

| | |
|---|---|
| **Mức** | MEDIUM |
| **Giao thức** | `SOAP ObjectService` |
| **Endpoint** | `http://<Exam_IP>:2221/ObjectService?wsdl` |

## Đề bài

Tính `finalPrice` của `ProductY` với **discount là số tiền tuyệt đối** (KHÁC Q2231/Q2261 dùng %).

## API

| Method | Tham số | Trả về |
|--------|--------|--------|
| `requestProductY(studentCode, qCode)` | string, string | `ProductY` |
| `submitProductY(studentCode, qCode, product)` | string, string, `ProductY` | void |

**ProductY** (4 field float — KHÔNG có `name`):
- `price`, `taxRate`, `discount`, `finalPrice`

## Công thức

```
finalPrice = price * (1 + taxRate/100) - discount
```

Round 2dp, set `p.finalPrice = float(finalPrice)`, submit lại.

## Code Python

Xem `Q2232_ObjectFinalPriceAbsoluteDiscount.py` cùng folder.

## Bẫy thường gặp

- **Discount = SỐ TIỀN** (trừ trực tiếp), khác Q2231/Q2261 dùng % (nhân `(1 - d/100)`).
- Phải `float()` cast khi set `finalPrice` (zeep complex type expect double).
- Submit lại **chính object** đã nhận (giữ price/taxRate/discount), không tạo `ProductY` mới.
- Round 2dp **trước khi** set finalPrice.
