import re
from collections import OrderedDict
from zeep import Client

EXAM_IP      = "36.50.135.242"
STUDENT_CODE = "B22DCCN863"   # TODO
Q_CODE       = "TODO_qCode"   # TODO
WSDL         = f"http://{EXAM_IP}:2221/CharacterService?wsdl"


def main():
    client = Client(WSDL)

    raw = client.service.requestString(STUDENT_CODE, Q_CODE)
    print("raw=", raw)

    # chuẩn hoá: lowercase, chỉ giữ [a-z0-9 ]
    s = re.sub(r"[^a-z0-9 ]", " ", raw.lower())
    s = re.sub(r"\s+", " ", s).strip()
    tokens = s.split(" ") if s else []

    # Đếm bigram theo thứ tự xuất hiện
    count: "OrderedDict[str, int]" = OrderedDict()
    for i in range(len(tokens) - 1):
        bg = f"{tokens[i]}_{tokens[i + 1]}"
        count[bg] = count.get(bg, 0) + 1

    # Top 3 theo count giảm dần; tie giữ insertion order (Python sorted ổn định)
    top = sorted(count.items(), key=lambda kv: -kv[1])[:3]

    answer = "|".join(f"{k}={v}" for k, v in top)
    print("answer=", answer)

    client.service.submitString(STUDENT_CODE, Q_CODE, answer)
    print("Đã submit", answer)


if __name__ == "__main__":
    main()
