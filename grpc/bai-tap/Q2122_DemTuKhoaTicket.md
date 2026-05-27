# Q2122 — gRPC Character — Ticket Tag Counter

| | |
|---|---|
| **Mức** | HARD |
| **Giao thức** | `gRPC TypedJudgeService` |
| **Endpoint** | `<Exam_IP>:2240` |

## Đề bài

Đếm số ticket chứa **từng từ khoá** trong danh sách KEYWORDS, case-insensitive.

## Contract

```proto
message TextBatchData {
  string mode = 1;
  repeated string entries = 2;
}
message TextBatchAnswer {
  repeated string   values = 1;
  map<string, int32> counts = 2;
}
```

`mode = "ticket_tags"` cho bài này. KEYWORDS cố định:
```python
["account", "payment", "refund", "shipping"]
```

## Logic

1. Với mỗi `keyword`, đếm số entry mà `entry.lower().contains(keyword)`.
2. Bỏ keyword nào có count = 0.
3. `values` = list keyword xuất hiện, sort alphabet.
4. `counts` = dict `{keyword: count}` cho các keyword xuất hiện.

## Code Python

Xem `Q2122_DemTuKhoaTicket.py` cùng folder.

## Bẫy thường gặp

- So sánh **case-insensitive** — phải `.lower()` entry trước khi `in`.
- Đếm là **đếm số entry chứa**, không đếm số lần xuất hiện (1 entry có 2 chữ "refund" vẫn chỉ +1).
- Map proto `map<string, int32>` set qua `counts=dict` trực tiếp khi tạo message.
- `values` sort alphabet (Python `sorted()` default đúng).
