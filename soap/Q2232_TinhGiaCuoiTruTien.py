from zeep import Client

EXAM_IP      = "36.50.135.242"
STUDENT_CODE = "B22DCCN863"   # TODO
Q_CODE       = "TODO_qCode"   # TODO
WSDL         = f"http://{EXAM_IP}:2221/ObjectService?wsdl"


def main():
    client = Client(WSDL)

    p = client.service.requestProductY(STUDENT_CODE, Q_CODE)
    print(f"price={p.price:.4f} tax={p.taxRate:.4f} disc={p.discount:.4f}")

    # discount = SỐ TIỀN tuyệt đối → trừ trực tiếp (KHÁC Q2231/Q2261 dùng %)
    final_price = p.price * (1 + p.taxRate / 100.0) - p.discount
    final_price = round(final_price, 2)
    p.finalPrice = float(final_price)
    print(f"finalPrice={final_price:.2f}")

    client.service.submitProductY(STUDENT_CODE, Q_CODE, p)
    print("Đã submit finalPrice=", final_price)


if __name__ == "__main__":
    main()
