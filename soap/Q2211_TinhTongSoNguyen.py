from zeep import Client

EXAM_IP      = "36.50.135.242"
STUDENT_CODE = "B22DCCN863"   # TODO
Q_CODE       = "irq9etkc"     # TODO
WSDL         = f"http://{EXAM_IP}:2221/DataService?wsdl"


def main():
    client = Client(WSDL)

    # Phase 1 — getData
    data = client.service.getData(STUDENT_CODE, Q_CODE)
    print("data=", data)

    total = sum(int(x) for x in data)
    print("sum=", total)

    # Phase 2 — submitDataInt (return void)
    client.service.submitDataInt(STUDENT_CODE, Q_CODE, total)
    print("Đã submit sum=", total)


if __name__ == "__main__":
    main()
