import math
import requests

EXAM_IP      = "36.50.135.242"
STUDENT_CODE = "B22DCCN863"   # TODO
Q_CODE       = "TODO_qAlias"  # TODO
BASE         = f"http://{EXAM_IP}:2230/api/rest/path"


def main():
    r = requests.get(BASE, params={"studentCode": STUDENT_CODE, "qCode": Q_CODE})
    body       = r.json()
    request_id = body["requestId"]
    customers  = body["data"]

    best_customer_id = None
    best_page        = -1
    best_amount      = -math.inf
    for c in customers:
        if c["status"] != "OVERDUE":
            continue
        amt = float(c["overdueAmount"])
        if amt > best_amount:
            best_amount      = amt
            best_customer_id = str(c["customerId"])
            best_page        = int(c["page"])

    if best_customer_id is None:
        raise RuntimeError("Không có khách OVERDUE")
    print(f"customerId={best_customer_id} page={best_page} overdueAmount={best_amount:.2f}")

    # requests tự URL-encode segment khi truyền qua params; path thì tự encode khi nhúng
    # Nhưng để an toàn (customerId có thể chứa /, space) — dùng urllib.parse.quote
    from urllib.parse import quote
    r = requests.get(f"{BASE}/{quote(best_customer_id, safe='')}", params={
        "studentCode": STUDENT_CODE,
        "qCode":       Q_CODE,
        "requestId":   request_id,
        "status":      "OVERDUE",
        "page":        best_page,
    })
    print("Server response:", r.text)


if __name__ == "__main__":
    main()
