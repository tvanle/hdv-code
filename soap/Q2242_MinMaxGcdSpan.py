from math import gcd
from functools import reduce
from zeep import Client

EXAM_IP      = "36.50.135.242"
STUDENT_CODE = "B22DCCN863"   # TODO
Q_CODE       = "TODO_qCode"   # TODO
WSDL         = f"http://{EXAM_IP}:2221/DataService?wsdl"


def main():
    client = Client(WSDL)

    data = [int(v) for v in client.service.getData(STUDENT_CODE, Q_CODE)]
    print("data=", data)

    mn   = min(data)
    mx   = max(data)
    g    = reduce(gcd, (abs(v) for v in data), 0)
    span = mx - mn
    answer = [mn, mx, g, span]
    print("answer=", answer)

    client.service.submitDataIntArray(STUDENT_CODE, Q_CODE, answer)
    print("Đã submit", answer)


if __name__ == "__main__":
    main()
