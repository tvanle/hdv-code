import hmac
import hashlib
import requests

EXAM_IP      = "36.50.135.242"
STUDENT_CODE = "B22DCCN863"   # TODO
Q_CODE       = "TODO_qAlias"  # TODO
BASE         = f"http://{EXAM_IP}:2230/api/rest/header"


def main():
    r = requests.get(BASE, params={"studentCode": STUDENT_CODE, "qCode": Q_CODE})
    body = r.json()
    request_id  = body["requestId"]
    data        = body["data"]
    nonce       = data["nonce"]
    signing_key = data["signingKey"]
    events      = data["events"]

    # payload = <nonce>:<event1>|...|<eventN>:<STUDENT_CODE_HOA>
    payload = f"{nonce}:{'|'.join(events)}:{STUDENT_CODE.upper()}"

    sig = hmac.new(
        signing_key.encode("utf-8"),
        payload.encode("utf-8"),
        hashlib.sha256,
    ).hexdigest()
    print("X-Signature=", sig)

    submit = {
        "studentCode": STUDENT_CODE,
        "qCode":       Q_CODE,
        "requestId":   request_id,
    }
    r = requests.post(f"{BASE}/submit", json=submit, headers={"X-Signature": sig})
    print("Server response:", r.text)


if __name__ == "__main__":
    main()
