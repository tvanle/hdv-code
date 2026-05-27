from zeep import Client

EXAM_IP      = "36.50.135.242"
STUDENT_CODE = "B22DCCN863"   # TODO
Q_CODE       = "8QVq0OaK"     # TODO
WSDL         = f"http://{EXAM_IP}:2221/CharacterService?wsdl"


def main():
    client = Client(WSDL)

    data = client.service.requestString(STUDENT_CODE, Q_CODE)
    print("data=", data)

    reversed_str = data[::-1]
    print("reversed=", reversed_str)

    client.service.submitString(STUDENT_CODE, Q_CODE, reversed_str)
    print("Đã submit reversed=", reversed_str)


if __name__ == "__main__":
    main()
