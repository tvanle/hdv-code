# Q2233 — SOAP Object — Filter Customers

| | |
|---|---|
| **Mức** | MEDIUM |
| **Giao thức** | `SOAP ObjectService` |
| **Endpoint** | `http://<Exam_IP>:2221/ObjectService?wsdl` |

## Đề bài

Lọc khách hàng có **purchaseCount ≥ 5** VÀ **totalSpent > 5000**, giữ thứ tự gốc.

## API

| Method | Tham số | Trả về |
|--------|--------|--------|
| `requestListCustomer(studentCode, qCode)` | string, string | `list<Customer>` |
| `submitListCustomer(studentCode, qCode, customers)` | string, string, `list<Customer>` | void |

**Customer**:
- `customerId` (string), `location` (string), `purchaseCount` (int), `totalSpent` (float)

## Logic

```python
selected = [c for c in all_customers
            if c.purchaseCount >= 5 and c.totalSpent > 5000.0]
```

## Code Python

Xem `Q2233_LocKhachHang.py` cùng folder.

## Bẫy thường gặp

- `purchaseCount **>= 5**` (lớn hơn HOẶC bằng), `totalSpent **> 5000**` (chỉ lớn hơn — không bằng).
- **Giữ thứ tự gốc**, KHÔNG sort.
- Submit list rỗng cũng OK nếu không có khách nào đạt — không throw exception.
