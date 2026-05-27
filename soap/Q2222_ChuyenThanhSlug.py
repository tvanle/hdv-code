import re
import unicodedata
from zeep import Client

EXAM_IP      = "36.50.135.242"
STUDENT_CODE = "B22DCCN863"   # TODO
Q_CODE       = "TODO_qCode"   # TODO
WSDL         = f"http://{EXAM_IP}:2221/CharacterService?wsdl"


def slugify(text: str) -> str:
    # lowercase → bỏ dấu Unicode (NFD + strip combining marks)
    s = text.lower()
    s = unicodedata.normalize("NFD", s)
    s = "".join(ch for ch in s if not unicodedata.combining(ch))
    # bỏ ký tự không phải chữ/số/space
    s = re.sub(r"[^a-z0-9\s]", "", s)
    # gom space → 1 space → đổi sang dash
    s = re.sub(r"\s+", " ", s).strip()
    return s.replace(" ", "-")


def main():
    client = Client(WSDL)

    data = client.service.requestString(STUDENT_CODE, Q_CODE)
    print("data=", data)

    slug = slugify(data)
    print("slug=", slug)

    client.service.submitString(STUDENT_CODE, Q_CODE, slug)
    print("Đã submit slug=", slug)


if __name__ == "__main__":
    main()
