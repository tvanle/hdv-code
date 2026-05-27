# Q2012 — REST Data — Payment Reconciliation

| | |
|---|---|
| **Mức** | MEDIUM |
| **Giao thức** | `REST_DATA` |
| **Endpoint** | `http://<Exam_IP>:2230/api/rest/data` |

## Đề bài

Đối soát danh sách giao dịch thanh toán: tính tổng đã thu (`CAPTURED`), tổng hoàn trả (`REFUNDED`), số đơn lỗi (`FAILED`), và doanh thu ròng. Đơn `PENDING` bỏ qua.

**Phase 1 — GET:**
```
GET /api/rest/data?studentCode=<mã_sv>&qCode=<qCode>
```
Response:
```json
{
  "requestId": "abc123",
  "data": [
    {"id": "TX001", "status": "CAPTURED", "amount": 150.50},
    {"id": "TX002", "status": "REFUNDED", "amount":  30.00},
    {"id": "TX003", "status": "FAILED",   "amount":   0.00},
    {"id": "TX004", "status": "PENDING",  "amount":  20.00}
  ]
}
```

**Phase 2 — POST `/submit`:**
```json
{
  "studentCode": "...", "qCode": "...", "requestId": "abc123",
  "answer": {
    "capturedTotal": 150.50,
    "refundedTotal":  30.00,
    "netTotal":      120.50,
    "failedCount":   1
  }
}
```

## Logic chính

- `capturedTotal = Σ amount` của `status == "CAPTURED"`
- `refundedTotal = Σ amount` của `status == "REFUNDED"`
- `netTotal     = capturedTotal − refundedTotal`
- `failedCount  = đếm` của `status == "FAILED"`
- Round 2dp tất cả số tiền.

## Code Python

Xem `Q2012_DataPaymentReconciliation.py` cùng folder.

## Bẫy thường gặp

- `PENDING` **không tính** vào bất kỳ trường nào.
- Round 2dp **sau khi cộng**, không round từng phần tử (tránh sai số tích lũy).
- `failedCount` là **số nguyên**, không phải float.
