# Q2113 — gRPC Data — Sensor Telemetry Window

| | |
|---|---|
| **Mức** | HARD |
| **Giao thức** | `gRPC TypedJudgeService` |
| **Endpoint** | `<Exam_IP>:2240` |

## Đề bài

Tính thống kê **average, p95, anomaly_count** của một cửa sổ telemetry.

## Contract

```proto
message SensorReading { string sensor_id = 1; double value = 2; int64 timestamp = 3; }
message SensorTelemetryData {
  double threshold = 1;
  repeated SensorReading readings = 2;
}
message SensorTelemetryAnswer {
  double average       = 1;
  double p95           = 2;
  int32  anomaly_count = 3;
}
```

## Logic

- `average = Σ value / n`, round 2dp.
- `anomaly_count = đếm các value > threshold` (strictly greater).
- `p95`: sort tăng dần, lấy phần tử ở index `ceil(n * 0.95) - 1` (clamp về [0, n-1]). Round 2dp.

Ví dụ `n = 100`: idx = `ceil(95) - 1 = 94` → value thứ 95 sau khi sort.

## Code Python

Xem `Q2113_PhanTichSensor.py` cùng folder.

## Bẫy thường gặp

- `anomaly` là **strictly** `value > threshold`, không phải `>=`.
- p95 dùng công thức **`ceil(n*0.95) - 1`** (zero-indexed), không phải `floor` hay `round`.
- Sort **tăng dần** trước khi lấy percentile.
- Round **chỉ ở output cuối**, không round từng phần tử khi đang cộng.
