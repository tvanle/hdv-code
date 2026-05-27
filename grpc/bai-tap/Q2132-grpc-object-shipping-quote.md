# Q2132 — gRPC Object — Best Shipping Quote

| | |
|---|---|
| **Mức** | HARD |
| **Giao thức** | `gRPC TypedJudgeService` |
| **Endpoint** | `<Exam_IP>:2240` |

## Đề bài

Giống Q2032 REST nhưng qua gRPC typed message.

## Contract

```proto
message ShippingQuote {
  string carrier     = 1;
  double base_fee    = 2;
  double per_kg_fee  = 3;
  int32  eta_days    = 4;
  double reliability = 5;
}
message ShippingQuoteData {
  string order_id     = 1;
  double weight_kg    = 2;
  int32  max_eta_days = 3;
  repeated ShippingQuote quotes = 4;
}
message ShippingQuoteAnswer {
  string carrier   = 1;
  double total_fee = 2;
  int32  eta_days  = 3;
}
```

## Logic

1. Lọc quote có `eta_days <= max_eta_days`.
2. `total_fee = base_fee + weight_kg * per_kg_fee`, round 2dp.
3. Chọn min theo `(total_fee, -reliability)`.

## Code Python

Xem `Q2132_ObjectShippingQuote.py` cùng folder.

## Bẫy thường gặp

- Field gRPC snake_case: `weight_kg`, `eta_days`, `base_fee`, `per_kg_fee`, `max_eta_days`.
- Round **trước khi** compare.
- Tie-breaker là `reliability` cao hơn.
- Submit answer với `total_fee` đã round 2dp (double), `eta_days` int32.
