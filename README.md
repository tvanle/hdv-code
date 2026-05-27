# Ôn luyện môn Phát triển hướng dịch vụ — PTIT (Python)

Bài tập JNP chia theo 3 protocol. Mỗi file `Q<id>_<TitleTV>.py` = 1 bài, tên tiếng Việt PascalCase (không dấu) cho dễ track.

## Layout

```
hdv-code/
├── rest/                                       requests
│   ├── requirements.txt
│   ├── bai-tap/                                15 file md đề gốc
│   ├── Q2011_TinhTongSoNguyen.py
│   ├── Q2012_DoiSoatThanhToan.py
│   ├── Q2021_SapXepTu.py
│   ├── Q2022_CheDuLieuNhayCam.py
│   ├── Q2031_TinhGiaCuoiPhanTram.py
│   ├── Q2032_ChonHangVanChuyen.py
│   ├── Q2041_PutCoBan.py
│   ├── Q2051_KiemTraChecksum.py
│   ├── Q2061_TruyVanSanPham.py
│   ├── Q2071_PutVoiAudit.py
│   ├── Q2072_PatchVoiIfMatch.py
│   ├── Q2081_ChecksumReplay.py
│   ├── Q2082_KyHmacSha256.py
│   ├── Q2091_TruyVanHoaDon.py
│   └── Q2092_KhachNoQuaHan.py
├── grpc/                                       grpcio + grpcio-tools
│   ├── requirements.txt
│   ├── gen_proto.py                            chạy 1 lần để sinh *_pb2.py
│   ├── proto/judge.proto
│   ├── proto/typed_judge.proto
│   ├── bai-tap/                                9 file md
│   ├── Q2111_TinhTongSoNguyen.py
│   ├── Q2112_LocGiaoDichRuiRo.py
│   ├── Q2113_PhanTichSensor.py
│   ├── Q2121_SapXepTu.py
│   ├── Q2122_DemTuKhoaTicket.py
│   ├── Q2123_DemSeverityLog.py
│   ├── Q2131_TinhGiaCuoiTruTien.py
│   ├── Q2132_ChonHangVanChuyen.py
│   └── Q2133_KiemTraDangKyMon.py
└── soap/                                       zeep (runtime WSDL)
    ├── requirements.txt
    ├── bai-tap/                                15 file md
    ├── Q2211_TinhTongSoNguyen.py
    ├── Q2212_TinhTongSoChan.py
    ├── Q2213_DemSoChanLe.py
    ├── Q2221_DaoNguocChuoi.py
    ├── Q2222_ChuyenThanhSlug.py
    ├── Q2223_CheLogArray.py
    ├── Q2231_TinhGiaCuoiPhanTram.py
    ├── Q2232_TinhGiaCuoiTruTien.py
    ├── Q2233_LocKhachHang.py
    ├── Q2241_DemSoNguyenToChecksum.py
    ├── Q2242_MinMaxGcdSpan.py
    ├── Q2251_TopBigram.py
    ├── Q2252_CheVaSapXepLog.py
    ├── Q2261_TinhGiaCuoiPhanTramV2.py
    └── Q2262_LocVaSapXepKhachVip.py
```

## Trước khi chạy

Trong từng `Q*.py`, sửa:
- `EXAM_IP` — IP máy thi
- `STUDENT_CODE` — mã sinh viên
- `Q_CODE` / `QUESTION_ALIAS` — qCode/qAlias ghi trong đề

## Setup môi trường

```powershell
# Windows PowerShell
python -m venv .venv
.\.venv\Scripts\Activate.ps1

# Cài dependencies cho từng folder bạn dùng
pip install -r rest\requirements.txt
pip install -r grpc\requirements.txt
pip install -r soap\requirements.txt
```

Yêu cầu: **Python ≥ 3.9**.

## Cách chạy

```powershell
# REST — chạy thẳng
cd rest
python .\Q2011_TinhTongSoNguyen.py

# gRPC — sinh stub 1 lần rồi chạy
cd ..\grpc
python gen_proto.py           # tạo judge_pb2.py / typed_judge_pb2.py (+ *_grpc.py)
python .\Q2111_TinhTongSoNguyen.py

# SOAP — zeep tự load WSDL runtime, không cần gen stub
cd ..\soap
python .\Q2211_TinhTongSoNguyen.py
```

## Lưu ý đề thi

- `discount = %` (Q2031, Q2231, Q2261) vs `discount = số tiền` (Q2131, Q2232) — đọc kỹ đề.
- Sort **case-sensitive** (Q2021) vs **case-insensitive** (Q2121).
- Tất cả theo mô hình **2 phase**: GET/Request nhận `requestId` → tính → POST/PUT/Submit gửi `answer`.
- REST trả `{"status":"AC"}`; SOAP `submit*` return `None` (server không trả status string).

---

# 📋 Cheat-sheet cú pháp & cách kết nối

## 1. REST — `requests`

### Imports & hằng số chuẩn
```python
import requests

EXAM_IP      = "36.50.135.242"
STUDENT_CODE = "B22DCCN863"
Q_CODE       = "xxxxxx"
BASE         = f"http://{EXAM_IP}:2230/api/rest/<group>"
# <group> = data | character | object | method | header | path
```

### Mẫu Phase-1 (GET) — dùng chung mọi bài
```python
r = requests.get(BASE, params={"studentCode": STUDENT_CODE, "qCode": Q_CODE})
body       = r.json()
request_id = body["requestId"]
data       = body["data"]    # list | dict tuỳ bài
```

`requests` **tự lo URL-encoding** khi truyền qua `params={...}` — không cần `urlencode` thủ công.

### Các biến thể Phase-2 (submit)

| Dạng bài | Cách gọi |
|---------|----------|
| `data/character/object` (Q2011-Q2032) | `requests.post(f"{BASE}/submit", json={...})` |
| `method/PUT` (Q2041, Q2071) | `requests.put(f"{BASE}/{request_id}", json={...})` |
| `method/PATCH + If-Match` (Q2072) | `requests.patch(f"{BASE}/{request_id}", json={...}, headers={"If-Match": etag})` |
| `header/checksum` (Q2051) | `requests.post(f"{BASE}/submit", json={...}, headers={"X-Checksum": chk})` |
| `header/HMAC` (Q2082) | `requests.post(f"{BASE}/submit", json={...}, headers={"X-Signature": sig})` |
| `path/query` (Q2061, Q2091) | `requests.get(f"{BASE}/{id}", params={...})` (Phase 2 cũng là GET) |

**Skeleton POST submit:**
```python
submit = {
    "studentCode": STUDENT_CODE,
    "qCode":       Q_CODE,
    "requestId":   request_id,
    "answer":      result,
}
r = requests.post(f"{BASE}/submit", json=submit)
print("Server response:", r.text)
```

**HMAC-SHA256 (Q2082):**
```python
import hmac, hashlib

payload = f"{nonce}:{'|'.join(events)}:{STUDENT_CODE.upper()}"
sig = hmac.new(
    signing_key.encode("utf-8"),
    payload.encode("utf-8"),
    hashlib.sha256,
).hexdigest()
```

**Đọc response header (Q2051/Q2081):**
```python
checksum = r.headers.get("X-Checksum")
```

---

## 2. gRPC — `grpcio` + stub auto-gen

### Sinh stub 1 lần
```powershell
cd grpc
python gen_proto.py
# Tạo 4 file: judge_pb2.py / judge_pb2_grpc.py / typed_judge_pb2.py / typed_judge_pb2_grpc.py
```

### Imports
```python
import grpc
import judge_pb2
import judge_pb2_grpc
# Bài hard (Q2112/Q2113/Q2122/Q2123/Q2132/Q2133) thêm:
import typed_judge_pb2
import typed_judge_pb2_grpc
```

### Hằng số
```python
EXAM_IP        = "36.50.135.242"
EXAM_PORT      = 2240
STUDENT_CODE   = "B22DCCN863"
QUESTION_ALIAS = "xxxxx"
```

### Kết nối (context manager tự cleanup)
```python
with grpc.insecure_channel(f"{EXAM_IP}:{EXAM_PORT}") as channel:
    stub = judge_pb2_grpc.JudgeServiceStub(channel)
    # ... request + submit ...
```

### Hai họ service

| Service | Bài dùng | Đặc điểm |
|---------|---------|---------|
| `JudgeService` (simple) | Q2111, Q2121, Q2131 | `data` & `answer` đều là **string**, tự parse |
| `TypedJudgeService` (oneof) | Q2112, Q2113, Q2122, Q2123, Q2132, Q2133 | Server trả `TypedData` với `oneof` payload (transaction_risk, sensor_telemetry, text_batch, shipping_quote, enrollment) |

**Skeleton `JudgeService`:**
```python
resp = stub.Request(judge_pb2.JudgeRequest(
    student_code=STUDENT_CODE,
    question_alias=QUESTION_ALIAS,
))
request_id = resp.request_id
data       = resp.data       # string — split(",") rồi parse

sr = stub.Submit(judge_pb2.SubmitRequest(
    student_code=STUDENT_CODE,
    question_alias=QUESTION_ALIAS,
    request_id=request_id,
    answer=str(result),
))
print(sr.status, sr.message)
```

**Skeleton `TypedJudgeService`:**
```python
resp = stub.RequestTyped(typed_judge_pb2.TypedRequest(
    student_code=STUDENT_CODE,
    question_alias=QUESTION_ALIAS,
))
request_id = resp.request_id
t          = resp.sensor_telemetry   # chọn 1 trong các oneof

# ... tính ...

answer = typed_judge_pb2.SensorTelemetryAnswer(
    average=avg, p95=p95, anomaly_count=n,
)
sr = stub.SubmitTyped(typed_judge_pb2.TypedAnswer(
    student_code=STUDENT_CODE,
    question_alias=QUESTION_ALIAS,
    request_id=request_id,
    sensor_telemetry=answer,          # setter tương ứng oneof
))
```

> **Naming convention**: proto dùng snake_case → Python dùng snake_case (`request_id`, `student_code`). RPC method giữ PascalCase (`stub.Request`, `stub.SubmitTyped`).

---

## 3. SOAP — `zeep` (no codegen)

### Imports & kết nối
```python
from zeep import Client

WSDL = f"http://{EXAM_IP}:2221/<Service>?wsdl"
client = Client(WSDL)            # load WSDL runtime — KHÔNG cần wsimport
```

### Mẫu 2 phase
```python
# Phase 1 — request
data = client.service.getData(STUDENT_CODE, Q_CODE)        # trả list[int]
# hoặc
p = client.service.requestProductY(STUDENT_CODE, Q_CODE)   # trả object có .price/.taxRate/...

# ... tính ...

# Phase 2 — submit (return None)
client.service.submitDataInt(STUDENT_CODE, Q_CODE, sum_value)
```

### Mapping Service ↔ WSDL ↔ bài

| WSDL endpoint | Method điển hình | Bài dùng |
|---------------|------------------|---------|
| `/DataService?wsdl` | `getData`, `submitDataInt`, `submitDataString`, `submitDataIntArray`, `submitDataStringArray` | Q2211-Q2213, Q2241-Q2242 |
| `/CharacterService?wsdl` | `requestString`, `requestStringArray`, `submitString`, `submitStringArray` | Q2221-Q2223, Q2251-Q2252 |
| `/ObjectService?wsdl` | `requestProductY`, `submitProductY`, `requestListCustomer`, `submitListCustomer` | Q2231-Q2233, Q2261-Q2262 |

### DTO thực tế (theo WSDL — KHÁC đề md)

| DTO | Field |
|-----|-------|
| `ProductY` | `discount`, `finalPrice`, `price`, `taxRate` — đều `float`, **không có** `name` |
| `Customer` | `customerId` (str), `location` (str), `purchaseCount` (int), `totalSpent` (float) |

### Modify object trả về rồi submit
```python
p = client.service.requestProductY(STUDENT_CODE, Q_CODE)
p.finalPrice = float(price * (1 + p.taxRate/100) * (1 - p.discount/100))
client.service.submitProductY(STUDENT_CODE, Q_CODE, p)
```

zeep cho phép gán attribute trực tiếp vào object DTO trả về — không cần tạo object mới.

### Khám phá WSDL khi không nhớ method
```python
client.wsdl.dump()                # in toàn bộ service/port/operation
print(client.service.__dir__())   # list method khả dụng
```

---

## 4. Bảng so sánh nhanh 3 protocol

| Tiêu chí | REST | gRPC | SOAP |
|---------|------|------|------|
| **Wire format** | JSON text | Protobuf binary | XML |
| **Transport** | HTTP/1.1 | HTTP/2 | HTTP/1.1 |
| **Lib Python** | `requests` | `grpcio` + `grpcio-tools` | `zeep` |
| **Schema** | Không cần | `.proto` → `python -m grpc_tools.protoc` | WSDL → zeep load runtime |
| **Cổng exam** | 2230 | 2240 | 2221 |
| **Phase 1** | `requests.get(BASE, params=...)` | `stub.Request(...)` / `stub.RequestTyped(...)` | `client.service.get<X>(...)` |
| **Phase 2** | `requests.post/put/patch(.../submit, json=...)` | `stub.Submit(...)` / `stub.SubmitTyped(...)` | `client.service.submit<X>(...)` |
| **Response submit** | `{"status":"AC", ...}` | `SubmitResponse{status, message}` | **None** (void) |
| **Naming** | camelCase JSON keys | snake_case (proto) | camelCase (WSDL) |

## So sánh code lines (Q2011 - bài đơn giản nhất)

| Java | Python |
|------|--------|
| ~30 dòng + 8 import | ~18 dòng + 1 import (`requests`) |

Python **gọn hơn ~40%**, không cần khai báo type, không cần `HttpRequest.newBuilder()...build()`, không cần `JSONObject().put(...).put(...)`.
