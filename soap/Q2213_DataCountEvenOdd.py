from zeep import Client

EXAM_IP      = "36.50.135.242"
STUDENT_CODE = "B22DCCN863"   # TODO
Q_CODE       = "TODO_qCode"   # TODO
WSDL         = f"http://{EXAM_IP}:2221/DataService?wsdl"


def main():
    client = Client(WSDL)

    data = client.service.getData(STUDENT_CODE, Q_CODE)
    print("data=", data)

    even = sum(1 for n in data if int(n) % 2 == 0)
    odd  = len(data) - even
    answer = [f"EVEN={even}", f"ODD={odd}"]
    print("answer=", answer)

    client.service.submitDataStringArray(STUDENT_CODE, Q_CODE, answer)
    print("Đã submit", answer)


if __name__ == "__main__":
    main()
