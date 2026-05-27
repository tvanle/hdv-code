# Q2133 — gRPC Object — Enrollment Eligibility

| | |
|---|---|
| **Mức** | HARD |
| **Giao thức** | `gRPC TypedJudgeService` |
| **Endpoint** | `<Exam_IP>:2240` |

## Đề bài

Kiểm tra sinh viên có đủ điều kiện đăng ký môn (đủ môn tiên quyết + đạt GPA tối thiểu).

## Contract

```proto
message EnrollmentData {
  string student_id = 1;
  repeated string completed_courses = 2;
  repeated string required_courses  = 3;
  double gpa     = 4;
  double min_gpa = 5;
}
message EnrollmentAnswer {
  bool   eligible = 1;
  repeated string missing_courses = 2;
  double gpa_gap  = 3;
}
```

## Logic

- `missing_courses` = `required \ completed`, sort tăng dần (alphabet).
- `gpa_gap = max(0, min_gpa - gpa)`, round 2dp.
- `eligible = (missing == [])` **AND** `(gpa_gap == 0.0)`.

## Code Python

Xem `Q2133_KiemTraDangKyMon.py` cùng folder.

## Bẫy thường gặp

- `missing` sort **alphabet tăng dần** (Python `sorted()` default đúng).
- `gpa_gap` **không âm** — đã đủ GPA thì gap = 0.0, không phải số âm.
- `eligible` là `AND` cả 2 điều kiện — thiếu môn HOẶC thiếu GPA đều `False`.
- Dùng `set()` để so sánh nhanh nhưng output `missing_courses` phải là **list đã sort**.
