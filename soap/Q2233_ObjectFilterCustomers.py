from zeep import Client

EXAM_IP      = "36.50.135.242"
STUDENT_CODE = "B22DCCN863"   # TODO
Q_CODE       = "TODO_qCode"   # TODO
WSDL         = f"http://{EXAM_IP}:2221/ObjectService?wsdl"


def main():
    client = Client(WSDL)

    all_customers = client.service.requestListCustomer(STUDENT_CODE, Q_CODE)
    print("total=", len(all_customers))

    # Giữ thứ tự gốc; lọc purchaseCount >= 5 và totalSpent > 5000
    selected = [
        c for c in all_customers
        if c.purchaseCount >= 5 and c.totalSpent > 5000.0
    ]
    print("selected=", len(selected))

    client.service.submitListCustomer(STUDENT_CODE, Q_CODE, selected)
    print(f"Đã submit {len(selected)} khách")


if __name__ == "__main__":
    main()
