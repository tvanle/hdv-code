import requests

EXAM_IP      = "36.50.135.242"
STUDENT_CODE = "B22DCCN863"   # TODO
Q_CODE       = "5Dg7uj0X"     # TODO
BASE         = f"http://{EXAM_IP}:2230/api/rest/data"


def main():
    # Phase 1 — GET danh sách số
    r = requests.get(BASE, params={"studentCode": STUDENT_CODE, "qCode": Q_CODE})
    body = r.json()
    request_id = body["requestId"]
    data       = body["data"]

    total = sum(int(x) for x in data)
    print(f"requestId={request_id}, sum={total}")

    # Phase 2 — POST submit
    submit = {
        "studentCode": STUDENT_CODE,
        "qCode":       Q_CODE,
        "requestId":   request_id,
        "answer":      total,
    }
    r = requests.post(f"{BASE}/submit", json=submit)
    print("Server response:", r.text)


if __name__ == "__main__":
    main()
