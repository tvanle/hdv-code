# Q2212 — SOAP Data — Sum of Even Numbers

| | |
|---|---|
| **Mức** | EASY |
| **Giao thức** | `SOAP DataService` |
| **Endpoint** | `http://<Exam_IP>:2221/DataService?wsdl` |

## Đề bài

Tính **tổng các số chẵn** trong list integer server trả về.

## API

| Method | Tham số | Trả về |
|--------|--------|--------|
| `getData(studentCode, qCode)` | string, string | `list<int>` |
| `submitDataInt(studentCode, qCode, sum)` | string, string, int | void |

## Logic

```python
sum_even = sum(n for n in data if n % 2 == 0)
```

## Code Python

Xem `Q2212_DataSumEven.py` cùng folder.

## Bẫy thường gặp

- Coi `0` là **chẵn** (đúng theo định nghĩa toán).
- Coi số **âm** chẵn cũng tính (`-4` là chẵn).
- `submitDataInt` (single int), không phải `submitDataIntArray` (list).
