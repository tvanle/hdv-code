# Q2222 — SOAP Character — Slugify

| | |
|---|---|
| **Mức** | MEDIUM |
| **Giao thức** | `SOAP CharacterService` |
| **Endpoint** | `http://<Exam_IP>:2221/CharacterService?wsdl` |

## Đề bài

Chuyển chuỗi (có thể có dấu Unicode tiếng Việt) thành **slug** kiểu URL.

## API

| Method | Tham số | Trả về |
|--------|--------|--------|
| `requestString(studentCode, qCode)` | string, string | string |
| `submitString(studentCode, qCode, slug)` | string, string, string | void |

## Quy tắc slug

1. `lowercase`
2. Bỏ dấu Unicode (NFD + strip combining marks). `"Tiếng Việt"` → `"tieng viet"`.
3. Bỏ ký tự không thuộc `[a-z0-9 ]` (giữ khoảng trắng).
4. Gom nhiều space thành 1, trim 2 đầu.
5. Đổi space → `-`.

Ví dụ: `"Học Lập Trình Java!"` → `"hoc-lap-trinh-java"`.

## Code Python

```python
import unicodedata, re

s = text.lower()
s = unicodedata.normalize("NFD", s)
s = "".join(ch for ch in s if not unicodedata.combining(ch))
s = re.sub(r"[^a-z0-9\s]", "", s)
s = re.sub(r"\s+", " ", s).strip()
return s.replace(" ", "-")
```

Xem `Q2222_ChuyenThanhSlug.py` cùng folder.

## Bẫy thường gặp

- **NFD + strip combining** mới bỏ được dấu tiếng Việt; `lower()` đơn thuần không đủ.
- Phải **giữ chữ số** `0-9` trong slug.
- Trim **sau khi** đã gom space, không trước.
- Dùng `-` (dash) chứ không phải `_` (underscore).
