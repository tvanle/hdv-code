# Q2032 — REST Object — Best Shipping Quote

| | |
|---|---|
| **Mức** | MEDIUM |
| **Giao thức** | `REST_OBJECT` |
| **Endpoint** | `http://<Exam_IP>:2230/api/rest/object` |

## Đề bài

Chọn hãng vận chuyển **rẻ nhất** đáp ứng deadline; tie thì lấy hãng có `reliability` cao hơn.

**Phase 1 — GET** trả:
```json
{
  "requestId": "abc",
  "data": {
    "weightKg":   3.5,
    "maxEtaDays": 5,
    "quotes": [
      {"carrier":"FAST", "baseFee":2.0, "perKgFee":1.5, "etaDays":3, "reliability":0.95},
      {"carrier":"SLOW", "baseFee":1.0, "perKgFee":0.8, "etaDays":7, "reliability":0.99},
      {"carrier":"MID",  "baseFee":1.5, "perKgFee":1.2, "etaDays":5, "reliability":0.90}
    ]
  }
}
```

**Phase 2 — POST `/submit`:**
```json
{"answer": {"carrier":"MID", "totalFee": 5.70, "etaDays": 5}, ...}
```

## Logic chính

1. Lọc các quote có `etaDays <= maxEtaDays`.
2. Tính `totalFee = baseFee + weightKg * perKgFee`, round 2dp.
3. Chọn min theo `(totalFee, -reliability)` — fee thấp nhất; tie thì reliability cao nhất.

## Code Python

Xem `Q2032_ObjectShippingQuote.py` cùng folder.

## Bẫy thường gặp

- Lọc deadline **trước khi** so sánh giá (loại trừ `SLOW` ở ví dụ).
- Round 2dp **trước** khi compare để tránh quote `5.7000001` thua `5.70`.
- Tie-breaker là **reliability cao hơn** (`>`), không phải thấp hơn.
