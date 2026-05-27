# Q2092 — REST Path — Overdue Customer Lookup

| | |
|---|---|
| **Mức** | HARD |
| **Giao thức** | `REST_PATH` |
| **Endpoint** | `http://<Exam_IP>:2230/api/rest/path` |

## Đề bài

Tìm khách hàng có **dư nợ quá hạn cao nhất**, sau đó query chi tiết bằng path-param + query-string.

**Phase 1 — GET** trả danh sách:
```json
{
  "requestId": "abc",
  "data": [
    {"customerId":"C/01", "status":"OK",      "overdueAmount":  0.0, "page":1},
    {"customerId":"C-02", "status":"OVERDUE", "overdueAmount":500.0, "page":2},
    {"customerId":"C 03", "status":"OVERDUE", "overdueAmount":820.5, "page":3}
  ]
}
```

**Phase 2 — GET path-param + query-string** (KHÔNG phải POST):
```
GET /api/rest/path/{URL-encoded customerId}?studentCode=...&qCode=...&requestId=...&status=OVERDUE&page=<page>
```

## Logic chính

1. Lọc `status == "OVERDUE"`.
2. Chọn khách có `overdueAmount` lớn nhất.
3. Lấy `customerId` + `page` của khách đó.
4. URL-encode `customerId` (có thể chứa `/`, space) khi nhúng vào path.

## Code Python

Xem `Q2092_KhachNoQuaHan.py` cùng folder.

## Bẫy thường gặp

- **`customerId` có thể chứa `/` hoặc space** — bắt buộc dùng `urllib.parse.quote(cid, safe='')`. Nếu không URL-encode, path bị vỡ (`C/01` → server tưởng 2 segment).
- Phase 2 là **GET** (không phải POST như các bài data/character).
- `requestId` đi qua **query-string**, không phải path.
- `page` phải truyền **đúng kiểu int** trong query-string.
