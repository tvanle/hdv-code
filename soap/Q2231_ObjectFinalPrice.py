from zeep import Client

EXAM_IP      = "36.50.135.242"
STUDENT_CODE = "B22DCCN863"   # TODO
Q_CODE       = "z7a6WujT"     # TODO
WSDL         = f"http://{EXAM_IP}:2221/ObjectService?wsdl"


def main():
    client = Client(WSDL)

    # ProductY chỉ có price/taxRate/discount/finalPrice (float) — không có name
    p = client.service.requestProductY(STUDENT_CODE, Q_CODE)
    print(f"price={p.price:.4f} tax={p.taxRate:.4f} disc={p.discount:.4f}")

    # discount = % → nhân (1 - discount/100)
    final_price = p.price * (1 + p.taxRate / 100.0) * (1 - p.discount / 100.0)
    p.finalPrice = float(final_price)
    print(f"finalPrice={final_price:.4f}")

    client.service.submitProductY(STUDENT_CODE, Q_CODE, p)
    print("Đã submit finalPrice=", final_price)


if __name__ == "__main__":
    main()
