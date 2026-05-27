import requests

EXAM_IP      = "36.50.135.242"
STUDENT_CODE = "B22DCCN863"   # TODO
Q_CODE       = "GtBAcXjG"     # TODO
BASE         = f"http://{EXAM_IP}:2230/api/rest/character"


def main():
    r = requests.get(BASE, params={"studentCode": STUDENT_CODE, "qCode": Q_CODE})
    body = r.json()
    request_id = body["requestId"]
    data       = body["data"]

    # Sort case-sensitive (mặc định Python str: codepoint)
    words = sorted(data.strip().split())
    answer = " ".join(words)
    print("answer=", answer)

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
