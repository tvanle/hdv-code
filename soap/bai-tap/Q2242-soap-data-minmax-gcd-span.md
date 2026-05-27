# Q2242 — SOAP Data — Min/Max/GCD/Span

| | |
|---|---|
| **Mức** | HARD |
| **Giao thức** | `SOAP DataService` |
| **Endpoint** | `http://<Exam_IP>:2221/DataService?wsdl` |

## Đề bài

Tính 4 chỉ số: min, max, GCD, span của list integer.

## API

| Method | Tham số | Trả về |
|--------|--------|--------|
| `getData(studentCode, qCode)` | string, string | `list<int>` |
| `submitDataIntArray(studentCode, qCode, answers)` | string, string, `list<int>` | void |

## Logic

- `min`, `max` = giá trị nhỏ/lớn nhất.
- `gcd` = ƯCLN của **giá trị tuyệt đối** của tất cả phần tử. Bắt đầu với 0 (`gcd(0, x) = |x|`).
- `span = max - min`.

Output: `[min, max, gcd, span]` (đúng thứ tự).

## Code Python

```python
from math import gcd
from functools import reduce
g = reduce(gcd, (abs(v) for v in data), 0)
```

Xem `Q2242_DataMinMaxGcdSpan.py` cùng folder.

## Bẫy thường gặp

- GCD trên **trị tuyệt đối** — `gcd(-12, 18) = 6`, không phải -6.
- Thứ tự trong list answer: **`min, max, gcd, span`** (đúng tuyệt đối).
- Khởi tạo `reduce(gcd, ..., 0)` — đáp số bắt đầu từ 0 (đơn vị của gcd).
- `submitDataIntArray` — danh sách integer.
