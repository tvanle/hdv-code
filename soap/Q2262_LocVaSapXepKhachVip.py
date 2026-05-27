from zeep import Client

EXAM_IP      = "36.50.135.242"
STUDENT_CODE = "B22DCCN863"   # TODO
Q_CODE       = "TODO_qCode"   # TODO
WSDL         = f"http://{EXAM_IP}:2221/ObjectService?wsdl"


def main():
    client = Client(WSDL)

    all_customers = client.service.requestListCustomer(STUDENT_CODE, Q_CODE)
    print("total=", len(all_customers))

    # Lọc purchaseCount >= 6 và totalSpent >= 4000
    vip = [c for c in all_customers
           if c.purchaseCount >= 6 and c.totalSpent >= 4000.0]

    # Sort: totalSpent giảm dần; tie → customerId tăng dần
    vip.sort(key=lambda c: (-c.totalSpent, c.customerId))
    print(f"vip= {len(vip)} khách")

    client.service.submitListCustomer(STUDENT_CODE, Q_CODE, vip)
    print(f"Đã submit {len(vip)} khách VIP")


if __name__ == "__main__":
    main()
