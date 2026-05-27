# Q2251 — SOAP Character — Top 3 Bigrams

| | |
|---|---|
| **Mức** | HARD |
| **Giao thức** | `SOAP CharacterService` |
| **Endpoint** | `http://<Exam_IP>:2221/CharacterService?wsdl` |

## Đề bài

Tìm **top 3 bigram** (cặp 2 từ liên tiếp) xuất hiện nhiều nhất.

## API

| Method | Tham số | Trả về |
|--------|--------|--------|
| `requestString(studentCode, qCode)` | string, string | string |
| `submitString(studentCode, qCode, answer)` | string, string, string | void |

## Quy tắc

1. Chuẩn hoá: `lowercase` → bỏ ký tự ngoài `[a-z0-9 ]` (thay bằng space) → gom space → trim.
2. Tokenize bằng split space.
3. Bigram = `tokens[i] + "_" + tokens[i+1]` cho mọi i.
4. Đếm tần suất, lấy top 3 theo count giảm dần (tie giữ thứ tự gặp).
5. Format output: `bg1=cnt1|bg2=cnt2|bg3=cnt3` (separator `|`).

Ví dụ input `"the cat and the cat sat"` → bigram counts `the_cat=2, cat_and=1, and_the=1, cat_sat=1` → top 3: `the_cat=2|cat_and=1|and_the=1`.

## Code Python

Xem `Q2251_TopBigram.py` cùng folder.

## Bẫy thường gặp

- Bigram separator giữa 2 token là **`_`** (underscore), không space.
- Output separator giữa các bigram là **`|`** (pipe).
- Sort **theo count giảm dần**; tie thì giữ **insertion order** (Python sort ổn định + dùng `OrderedDict`).
- Chuẩn hoá phải làm **trước** khi đếm — không đếm trực tiếp trên raw.
