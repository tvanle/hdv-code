# Q2213 — SOAP Data — Count Even & Odd

| | |
|---|---|
| **Mức** | EASY |
| **Giao thức** | `SOAP DataService` |
| **Endpoint** | `http://<Exam_IP>:2221/DataService?wsdl` |

## Đề bài

Đếm số phần tử **chẵn** và **lẻ**, trả về 2 chuỗi `"EVEN=<n>"`, `"ODD=<n>"`.

## API

| Method | Tham số | Trả về |
|--------|--------|--------|
| `getData(studentCode, qCode)` | string, string | `list<int>` |
| `submitDataStringArray(studentCode, qCode, answers)` | string, string, `list<string>` | void |

## Logic

```python
even = sum(1 for n in data if n % 2 == 0)
odd  = len(data) - even
answer = [f"EVEN={even}", f"ODD={odd}"]
```

## Code Python

Xem `Q2213_DataCountEvenOdd.py` cùng folder.

## Bẫy thường gặp

- Thứ tự **bắt buộc**: `EVEN` **trước**, `ODD` sau.
- Format `EVEN=<n>` (uppercase, dấu `=`, không space).
- Dùng `submitDataStringArray` (array of strings), **không** `submitDataIntArray`.
