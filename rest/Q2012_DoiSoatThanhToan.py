import requests

EXAM_IP      = "36.50.135.242"
STUDENT_CODE = "B22DCCN863"   # TODO
Q_CODE       = "TODO_qAlias"  # TODO
BASE         = f"http://{EXAM_IP}:2230/api/rest/data"


def main():
    r = requests.get(BASE, params={"studentCode": STUDENT_CODE, "qCode": Q_CODE})
    body = r.json()
    request_id = body["requestId"]
    txs        = body["data"]

    captured_total = 0.0
    refunded_total = 0.0
    failed_count   = 0
    for tx in txs:
        status = tx["status"]
        amount = float(tx["amount"])
        if   status == "CAPTURED": captured_total += amount
        elif status == "REFUNDED": refunded_total += amount
        elif status == "FAILED":   failed_count   += 1
        # PENDING: bỏ qua

    net_total = captured_total - refunded_total
    captured_total = round(captured_total, 2)
    refunded_total = round(refunded_total, 2)
    net_total      = round(net_total,      2)

    print(f"captured={captured_total:.2f} refunded={refunded_total:.2f} "
          f"net={net_total:.2f} failed={failed_count}")

    answer = {
        "capturedTotal": captured_total,
        "refundedTotal": refunded_total,
        "netTotal":      net_total,
        "failedCount":   failed_count,
    }
    submit = {
        "studentCode": STUDENT_CODE,
        "qCode":       Q_CODE,
        "requestId":   request_id,
        "answer":      answer,
    }
    r = requests.post(f"{BASE}/submit", json=submit)
    print("Server response:", r.text)


if __name__ == "__main__":
    main()
