import requests

EXAM_IP      = "36.50.135.242"
STUDENT_CODE = "B22DCCN863"   # TODO
Q_CODE       = "TODO_qAlias"  # TODO
BASE         = f"http://{EXAM_IP}:2230/api/rest/method"


def main():
    r = requests.get(BASE, params={"studentCode": STUDENT_CODE, "qCode": Q_CODE})
    body = r.json()
    request_id = body["requestId"]
    ticket     = body["data"]
    etag       = ticket["etag"]
    print(f"requestId={request_id}, etag={etag}")

    submit = {
        "studentCode": STUDENT_CODE,
        "qCode":       Q_CODE,
        "answer":      {"status": "RESOLVED"},
    }
    r = requests.patch(
        f"{BASE}/{request_id}",
        json=submit,
        headers={"If-Match": etag},
    )
    print("Server response:", r.text)


if __name__ == "__main__":
    main()
