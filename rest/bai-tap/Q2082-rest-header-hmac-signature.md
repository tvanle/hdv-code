# Q2082 — REST Header — HMAC-SHA256 Signature

| | |
|---|---|
| **Mức** | HARD |
| **Giao thức** | `REST_HEADER` |
| **Endpoint** | `http://<Exam_IP>:2230/api/rest/header` |

## Đề bài

Ký toàn bộ payload bằng HMAC-SHA256 với khóa server cấp, gửi qua header `X-Signature`.

**Phase 1 — GET** trả nonce + khóa + danh sách events:
```json
{
  "requestId": "abc",
  "data": {
    "nonce":      "n-2026-001",
    "signingKey": "S3CR3T_KEY_HEX",
    "events":     ["LOGIN", "PURCHASE", "LOGOUT"]
  }
}
```

**Phase 2 — POST `/submit`** với header `X-Signature: <hex>`:
- `payload = nonce + ":" + events.join("|") + ":" + STUDENT_CODE.toUpperCase()`
  → ví dụ `"n-2026-001:LOGIN|PURCHASE|LOGOUT:B22DCCN863"`
- `signature = HMAC-SHA256(key=signingKey.bytes, msg=payload.bytes).hex()` (lowercase)
- Body: `{"studentCode":"...", "qCode":"...", "requestId":"abc"}` (KHÔNG cần `answer`)

## Logic chính

```python
import hmac, hashlib
payload = f"{nonce}:{'|'.join(events)}:{STUDENT_CODE.upper()}"
sig = hmac.new(signing_key.encode(), payload.encode(), hashlib.sha256).hexdigest()
requests.post(url, json=body, headers={"X-Signature": sig})
```

## Code Python

Xem `Q2082_HeaderHmacSignature.py` cùng folder.

## Bẫy thường gặp

- `STUDENT_CODE` phải **`.upper()`** trong payload (server check uppercase).
- Hex output **lowercase** (mặc định `.hexdigest()` Python đã đúng).
- Separator giữa events là **pipe `|`**, giữa 3 phần là **colon `:`**.
- `signingKey` dùng cả `.encode("utf-8")` cho key và message — không base64 decode.
- KHÔNG có `answer` trong body — chỉ có `studentCode`, `qCode`, `requestId`.
