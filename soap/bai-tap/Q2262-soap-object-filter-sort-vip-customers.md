# Q2262 — SOAP Object — Filter + Sort VIP Customers

| | |
|---|---|
| **Mức** | HARD |
| **Giao thức** | `SOAP ObjectService` |
| **Endpoint** | `http://<Exam_IP>:2221/ObjectService?wsdl` |

## Đề bài

Lọc khách hàng VIP (`purchaseCount ≥ 6` VÀ `totalSpent ≥ 4000`) rồi sort theo:
1. `totalSpent` **giảm dần**
2. Tie → `customerId` **tăng dần** (alphabet)

## API

| Method | Tham số | Trả về |
|--------|--------|--------|
| `requestListCustomer(studentCode, qCode)` | string, string | `list<Customer>` |
| `submitListCustomer(studentCode, qCode, customers)` | string, string, `list<Customer>` | void |

## Logic

```python
vip = [c for c in all_customers
       if c.purchaseCount >= 6 and c.totalSpent >= 4000.0]
vip.sort(key=lambda c: (-c.totalSpent, c.customerId))
```

## Code Python

Xem `Q2262_ObjectFilterSortVipCustomers.py` cùng folder.

## Bẫy thường gặp

- Khác Q2233: ngưỡng `purchaseCount **>= 6**` (không phải 5), `totalSpent **>= 4000**` (không phải > 5000, dùng `>=` cả 2).
- Sort 2 tier: spent giảm (negative), id tăng — dùng tuple `(-spent, id)`.
- VIP có thể rỗng — submit list trống.
- Không tự thêm/xoá field nào trên Customer, chỉ filter + sort.
