# Q2241 — SOAP Data — Prime Count + Weighted Checksum

| | |
|---|---|
| **Mức** | HARD |
| **Giao thức** | `SOAP DataService` |
| **Endpoint** | `http://<Exam_IP>:2221/DataService?wsdl` |

## Đề bài

Đếm số nguyên tố + tính checksum có trọng số position.

## API

| Method | Tham số | Trả về |
|--------|--------|--------|
| `getData(studentCode, qCode)` | string, string | `list<int>` |
| `submitDataString(studentCode, qCode, answer)` | string, string, string | void |

## Logic

- `primeCount = đếm phần tử là số nguyên tố`. Quy ước: `n < 2` không nguyên tố.
- `checksum = (Σ (i+1) * value[i]) mod 100000`, với `i` là index 0-based.
  → vị trí đầu tiên có trọng số 1, vị trí thứ 2 có trọng số 2, ...

Format output (chuỗi):
```
primeCount=<n>;checksum=<m>
```
Ví dụ: `primeCount=7;checksum=42135`.

## Code Python

Xem `Q2241_DataPrimeCountChecksum.py` cùng folder.

## Bẫy thường gặp

- Định nghĩa nguyên tố: `n >= 2`. `0`, `1` **không** là nguyên tố.
- `checksum mod 100000` — luôn `>= 0` (Python `%` đã đúng cho số dương; nếu có số âm thì `+= 100000` sau).
- Format **đúng chính tả** `primeCount=...;checksum=...` (separator `;`, không space).
- Submit qua `submitDataString` (single string), không `submitDataStringArray`.
