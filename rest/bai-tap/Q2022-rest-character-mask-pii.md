# Q2022 — REST Character — Mask PII

| | |
|---|---|
| **Mức** | MEDIUM |
| **Giao thức** | `REST_CHARACTER` |
| **Endpoint** | `http://<Exam_IP>:2230/api/rest/character` |

## Đề bài

Che dữ liệu nhạy cảm (email, số điện thoại, token) trong chuỗi log nhiều dòng phân tách bằng `||`.

**Phase 1 — GET** trả:
```json
{
  "requestId": "abc",
  "data": "user a@b.com login||call 0123456789||token=secret123 expired"
}
```

**Phase 2 — POST `/submit`** với `answer` = chuỗi đã che, giữ nguyên separator `||`.

## Quy tắc che

| Mẫu | Thay bằng | Regex |
|-----|----------|-------|
| Email | `[EMAIL]` | `[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}` |
| Phone VN (10 số, bắt đầu `0`) | `[PHONE]` | `(?<!\d)0\d{9}(?!\d)` |
| Token | `token=[TOKEN]` | `token=[^\s\|]+` |

Ví dụ output: `user [EMAIL] login||call [PHONE]||token=[TOKEN] expired`

## Code Python

Xem `Q2022_CharacterMaskPII.py` cùng folder.

## Bẫy thường gặp

- Phone regex dùng **negative lookbehind/ahead** `(?<!\d)` và `(?!\d)` để không match số dài hơn 10 chữ số.
- Token regex **không match** ký tự `|` để không nuốt sang segment kế.
- Split bằng `"||"` (literal 2 ký tự), không phải regex.
- Phải apply 3 regex theo thứ tự `EMAIL → PHONE → TOKEN`; đảo có thể gây xung đột.
