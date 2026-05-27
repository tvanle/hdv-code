import re
import requests

EXAM_IP      = "36.50.135.242"
STUDENT_CODE = "B22DCCN863"   # TODO
Q_CODE       = "TODO_qAlias"  # TODO
BASE         = f"http://{EXAM_IP}:2230/api/rest/character"

EMAIL_RE = re.compile(r"[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}")
PHONE_RE = re.compile(r"(?<!\d)0\d{9}(?!\d)")
TOKEN_RE = re.compile(r"token=[^\s|]+")


def redact(line: str) -> str:
    line = EMAIL_RE.sub("[EMAIL]", line)
    line = PHONE_RE.sub("[PHONE]", line)
    line = TOKEN_RE.sub("token=[TOKEN]", line)
    return line


def main():
    r = requests.get(BASE, params={"studentCode": STUDENT_CODE, "qCode": Q_CODE})
    body = r.json()
    request_id = body["requestId"]
    data       = body["data"]

    answer = "||".join(redact(seg) for seg in data.split("||"))
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
