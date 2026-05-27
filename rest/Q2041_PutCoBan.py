import requests

EXAM_IP      = "36.50.135.242"
STUDENT_CODE = "B22DCCN863"   # TODO
Q_CODE       = "jMOYV2vD"     # TODO
BASE         = f"http://{EXAM_IP}:2230/api/rest/method"


def main():
    r = requests.get(BASE, params={"studentCode": STUDENT_CODE, "qCode": Q_CODE})
    body = r.json()
    request_id = body["requestId"]
    print("requestId=", request_id)

    submit = {
        "studentCode": STUDENT_CODE,
        "qCode":       Q_CODE,
        "answer":      {"status": "done"},
    }
    r = requests.put(f"{BASE}/{request_id}", json=submit)
    print("Server response:", r.text)


if __name__ == "__main__":
    main()
