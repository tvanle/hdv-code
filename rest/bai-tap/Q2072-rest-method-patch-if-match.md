# Q2072 — REST Method — PATCH with If-Match

| | |
|---|---|
| **Mức** | MEDIUM |
| **Giao thức** | `REST_METHOD` |
| **Endpoint** | `http://<Exam_IP>:2230/api/rest/method` |

## Đề bài

Cập nhật **một phần** trạng thái ticket dùng `PATCH` + header `If-Match` (optimistic concurrency).

**Phase 1 — GET** trả ticket + `etag`:
```json
{
  "requestId": "TKT-007",
  "data": {
    "ticketId": "TKT-007",
    "status":   "OPEN",
    "etag":     "v3-9a7c2d"
  }
}
```

**Phase 2 — PATCH `/{requestId}`** (KHÔNG phải `/submit`):
```
PATCH /api/rest/method/TKT-007
If-Match: v3-9a7c2d
Content-Type: application/json
```
Body:
```json
{"studentCode":"...", "qCode":"...", "answer":{"status":"RESOLVED"}}
```

## Yêu cầu

- Method **PATCH** (không phải PUT/POST).
- Header `If-Match` = `etag` nhận từ Phase 1 — sai → server trả `412 Precondition Failed`.
- `answer` chỉ chứa **field cần đổi** (`status`) — không gửi cả object ticket.
- `answer.status` phải là `"RESOLVED"` (uppercase).

## Code Python

Xem `Q2072_MethodPatchIfMatch.py` cùng folder.

## Bẫy thường gặp

- `requests.patch(...)` (lowercase) — Python idiom.
- `If-Match` header có **dấu gạch ngang** giữa, không phải `IfMatch`.
- KHÔNG có `requestId` trong body Phase 2 (vì đã ở trên path).
- `PATCH` ≠ `PUT` — `PUT` thay toàn bộ object, `PATCH` chỉ thay phần.
