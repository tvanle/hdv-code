import requests

EXAM_IP      = "36.50.135.242"
STUDENT_CODE = "B22DCCN863"   # TODO
Q_CODE       = "LrRK7nD4"     # TODO
BASE         = f"http://{EXAM_IP}:2230/api/rest/header"


def main():
    r = requests.get(BASE, params={"studentCode": STUDENT_CODE, "qCode": Q_CODE})
    body = r.json()
    request_id = body["requestId"]
    checksum   = r.headers.get("X-Checksum")
    if checksum is None:
        raise RuntimeError("Missing X-Checksum")
    print("X-Checksum=", checksum)

    submit = {
        "studentCode": STUDENT_CODE,
        "qCode":       Q_CODE,
        "requestId":   request_id,
    }
    r = requests.post(f"{BASE}/submit", json=submit, headers={"X-Checksum": checksum})
    print("Server response:", r.text)


if __name__ == "__main__":
    main()
