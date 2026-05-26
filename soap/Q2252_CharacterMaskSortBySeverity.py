import re
from zeep import Client

EXAM_IP      = "36.50.135.242"
STUDENT_CODE = "B22DCCN863"   # TODO
Q_CODE       = "TODO_qCode"   # TODO
WSDL         = f"http://{EXAM_IP}:2221/CharacterService?wsdl"

EMAIL_RE = re.compile(r"[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}")
PHONE_RE = re.compile(r"(?<!\d)0\d{9}(?!\d)")
TOKEN_RE = re.compile(r"token=\S+")

SEVERITY = {"ERROR": 0, "WARN": 1, "INFO": 2}


def redact(line: str) -> str:
    line = EMAIL_RE.sub("[EMAIL]", line)
    line = PHONE_RE.sub("[PHONE]", line)
    line = TOKEN_RE.sub("token=[TOKEN]", line)
    return line


def severity_key(line: str) -> int:
    first = line.split(" ", 1)[0] if " " in line else line
    return SEVERITY.get(first, 10**9)


def main():
    client = Client(WSDL)

    logs = client.service.requestStringArray(STUDENT_CODE, Q_CODE)
    print(f"logs(in)= {len(logs)} dòng")

    redacted = [redact(line) for line in logs]
    redacted.sort(key=severity_key)   # Python sort ổn định
    print("logs(sorted)=", redacted)

    client.service.submitStringArray(STUDENT_CODE, Q_CODE, redacted)
    print(f"Đã submit {len(redacted)} dòng")


if __name__ == "__main__":
    main()
