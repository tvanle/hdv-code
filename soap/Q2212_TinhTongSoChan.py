from zeep import Client

EXAM_IP      = "36.50.135.242"
STUDENT_CODE = "B22DCCN863"   # TODO
Q_CODE       = "TODO_qCode"   # TODO
WSDL         = f"http://{EXAM_IP}:2221/DataService?wsdl"


def main():
    client = Client(WSDL)

    data = client.service.getData(STUDENT_CODE, Q_CODE)
    print("data=", data)

    sum_even = sum(int(n) for n in data if int(n) % 2 == 0)
    print("sumEven=", sum_even)

    client.service.submitDataInt(STUDENT_CODE, Q_CODE, sum_even)
    print("Đã submit sumEven=", sum_even)


if __name__ == "__main__":
    main()
