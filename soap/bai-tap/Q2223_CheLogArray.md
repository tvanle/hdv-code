# Q2223 — SOAP Character — Mask PII in Log Array

| | |
|---|---|
| **Mức** | MEDIUM |
| **Giao thức** | `SOAP CharacterService` |
| **Endpoint** | `http://<Exam_IP>:2221/CharacterService?wsdl` |

## Đề bài

Che dữ liệu nhạy cảm trong **danh sách dòng log** (mỗi dòng là 1 chuỗi).

## API

| Method | Tham số | Trả về |
|--------|--------|--------|
| `requestStringArray(studentCode, qCode)` | string, string | `list<string>` |
| `submitStringArray(studentCode, qCode, lines)` | string, string, `list<string>` | void |

## Quy tắc che

| Mẫu | Thay bằng | Regex |
|-----|----------|-------|
| Email | `[EMAIL]` | `[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}` |
| Phone (`0` + 9 số) | `[PHONE]` | `(?<!\d)0\d{9}(?!\d)` |
| Token | `token=[TOKEN]` | `token=\S+` |

Áp dụng **độc lập** cho từng dòng, giữ nguyên số dòng và thứ tự.

## Code Python

Xem `Q2223_CheLogArray.py` cùng folder.

## Bẫy thường gặp

- Khác Q2022 REST: input là **list** từng dòng (không có separator `||`), regex token dùng `\S+` (không cần loại `|`).
- Phải giữ **đúng số dòng** output bằng input.
- Apply 3 regex theo thứ tự `EMAIL → PHONE → TOKEN`.
