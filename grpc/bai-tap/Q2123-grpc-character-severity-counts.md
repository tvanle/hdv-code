# Q2123 — gRPC Character — Severity Counts + Code Extract

| | |
|---|---|
| **Mức** | HARD |
| **Giao thức** | `gRPC TypedJudgeService` |
| **Endpoint** | `<Exam_IP>:2240` |

## Đề bài

Phân tích log: đếm số dòng theo severity (INFO/WARN/ERROR) và trích `code=<value>` xuất hiện trong dòng.

Reuse cùng message `TextBatchData` / `TextBatchAnswer` như Q2122 (mode khác).

## Logic

Mỗi entry có dạng `"<SEVERITY> <message...>"`:
- Token đầu (split bằng space) là severity. Chỉ đếm nếu thuộc `{INFO, WARN, ERROR}`.
- Search regex `code=(\S+)` — nếu match thì thêm vào `values` (theo thứ tự gặp, KHÔNG dedupe).

Output:
- `counts` = `{"INFO": ..., "WARN": ..., "ERROR": ...}` (chỉ key xuất hiện).
- `values` = list code theo thứ tự gặp.

## Ví dụ input
```
INFO  user login code=U001
WARN  retry code=R5
ERROR  db down code=D99
INFO  cleanup
```

Output: `counts = {INFO:2, WARN:1, ERROR:1}`, `values = ["U001", "R5", "D99"]`.

## Code Python

Xem `Q2123_CharacterSeverityCounts.py` cùng folder.

## Bẫy thường gặp

- Severity là **token đầu tiên**, không phải substring (`"INFOMATIC"` không tính).
- Regex `\S+` cho `code=...` — không gồm whitespace.
- `values` giữ **thứ tự gặp**, không sort, không dedupe.
- Dòng không có `code=` thì bỏ qua phần extract, nhưng vẫn tính severity.
