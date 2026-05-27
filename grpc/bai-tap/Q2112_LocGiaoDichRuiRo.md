# Q2112 — gRPC Data — Transaction Risk Batch

| | |
|---|---|
| **Mức** | HARD |
| **Giao thức** | `gRPC TypedJudgeService` |
| **Endpoint** | `<Exam_IP>:2240` |

## Đề bài

Phân loại giao dịch **high-risk** theo nhiều rule, dùng `TypedJudgeService.RequestTyped` + `SubmitTyped` với payload `transaction_risk` (oneof).

## Contract (`typed_judge.proto`)

```proto
message Transaction {
  string transaction_id   = 1;
  double amount           = 2;
  int32  chargeback_count = 3;
  bool   new_device       = 4;
  string country          = 5;
}
message TransactionRiskBatchData { repeated Transaction transactions = 1; }
message TransactionRiskAnswer {
  repeated string high_risk_transaction_ids = 1;
  int32  review_count                       = 2;
  double total_high_risk_amount             = 3;
}
```

## Logic (rule OR)

Giao dịch là **high-risk** nếu thoả **bất kỳ** điều kiện sau:
- `amount >= 5000.0`, HOẶC
- `chargeback_count >= 2`, HOẶC
- `new_device == True` **VÀ** `country != "VN"`

Output:
- `high_risk_transaction_ids` = danh sách `transaction_id` (theo thứ tự input)
- `review_count` = `len(high_risk_ids)`
- `total_high_risk_amount` = `Σ amount` của các giao dịch high-risk, round 2dp

## Code Python

Xem `Q2112_LocGiaoDichRuiRo.py` cùng folder.

## Bẫy thường gặp

- Rule là **OR** (đủ 1 điều kiện là high-risk), không phải AND.
- `new_device` chỉ tính nếu **kèm** `country != "VN"` — riêng `new_device=true country=VN` thì OK.
- Phải giữ **thứ tự input** trong `high_risk_transaction_ids`, không sort.
- Round 2dp **sau khi cộng**.
