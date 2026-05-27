import re
from zeep import Client

EXAM_IP      = "36.50.135.242"
STUDENT_CODE = "B22DCCN863"   # TODO
Q_CODE       = "TODO_qCode"   # TODO
WSDL         = f"http://{EXAM_IP}:2221/CharacterService?wsdl"

EMAIL_RE = re.compile(r"[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}")
PHONE_RE = re.compile(r"(?<!\d)0\d{9}(?!\d)")
TOKEN_RE = re.compile(r"token=\S+")


def redact(line: str) -> str:
    line = EMAIL_RE.sub("[EMAIL]", line)
    line = PHONE_RE.sub("[PHONE]", line)
    line = TOKEN_RE.sub("token=[TOKEN]", line)
    return line


def main():
    client = Client(WSDL)

    logs = client.service.requestStringArray(STUDENT_CODE, Q_CODE)
    print(f"logs(in)= {len(logs)} dòng")

    redacted = [redact(line) for line in logs]
    print("logs(out)=", redacted)

    client.service.submitStringArray(STUDENT_CODE, Q_CODE, redacted)
    print(f"Đã submit {len(redacted)} dòng log đã che")


if __name__ == "__main__":
    main()
