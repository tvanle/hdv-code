# Q2252 — SOAP Character — Mask + Sort by Severity

| | |
|---|---|
| **Mức** | HARD |
| **Giao thức** | `SOAP CharacterService` |
| **Endpoint** | `http://<Exam_IP>:2221/CharacterService?wsdl` |

## Đề bài

Combo 2 bài: **che PII** (như Q2223) **rồi sort theo severity** (ERROR < WARN < INFO).

## API

| Method | Tham số | Trả về |
|--------|--------|--------|
| `requestStringArray(studentCode, qCode)` | string, string | `list<string>` |
| `submitStringArray(studentCode, qCode, lines)` | string, string, `list<string>` | void |

## Quy tắc

1. Che PII: email → `[EMAIL]`, phone → `[PHONE]`, token → `token=[TOKEN]`.
2. Sort theo severity (token đầu tiên trong dòng):
   - `ERROR` = 0, `WARN` = 1, `INFO` = 2, khác = ∞ (xuống cuối).
3. **Stable sort** — dòng cùng severity giữ thứ tự gốc.

## Code Python

```python
SEVERITY = {"ERROR": 0, "WARN": 1, "INFO": 2}

def severity_key(line):
    first = line.split(" ", 1)[0] if " " in line else line
    return SEVERITY.get(first, 10**9)

redacted.sort(key=severity_key)   # Python sort ổn định
```

Xem `Q2252_CheVaSapXepLog.py` cùng folder.

## Bẫy thường gặp

- Thứ tự: `ERROR < WARN < INFO` (ERROR lên đầu, INFO xuống cuối).
- Dòng không bắt đầu bằng severity hợp lệ → xếp **cuối cùng**.
- Sort phải **ổn định** (Python `sort()` mặc định đúng).
- Phải **che PII trước** rồi mới sort (không sort raw rồi che).
