import math
import requests

EXAM_IP      = "36.50.135.242"
STUDENT_CODE = "B22DCCN863"   # TODO
Q_CODE       = "TODO_qAlias"  # TODO
BASE         = f"http://{EXAM_IP}:2230/api/rest/object"


def main():
    r = requests.get(BASE, params={"studentCode": STUDENT_CODE, "qCode": Q_CODE})
    body = r.json()
    request_id  = body["requestId"]
    data        = body["data"]
    weight_kg   = float(data["weightKg"])
    max_eta     = int(data["maxEtaDays"])
    quotes      = data["quotes"]

    best_carrier = None
    best_fee     = math.inf
    best_eta     = -1
    best_reliab  = -1.0

    for q in quotes:
        eta = int(q["etaDays"])
        if eta > max_eta:
            continue
        fee = float(q["baseFee"]) + weight_kg * float(q["perKgFee"])
        fee = round(fee, 2)
        reliab = float(q["reliability"])
        if fee < best_fee or (fee == best_fee and reliab > best_reliab):
            best_fee     = fee
            best_eta     = eta
            best_reliab  = reliab
            best_carrier = q["carrier"]

    if best_carrier is None:
        raise RuntimeError("Không có quote hợp lệ")
    print(f"best: carrier={best_carrier} fee={best_fee:.2f} eta={best_eta}")

    answer = {"carrier": best_carrier, "totalFee": best_fee, "etaDays": best_eta}
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
