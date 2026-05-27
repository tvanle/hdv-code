import requests

EXAM_IP      = "36.50.135.242"
STUDENT_CODE = "B22DCCN863"   # TODO
Q_CODE       = "s543QlZR"     # TODO
BASE         = f"http://{EXAM_IP}:2230/api/rest/object"


def main():
    r = requests.get(BASE, params={"studentCode": STUDENT_CODE, "qCode": Q_CODE})
    body = r.json()
    request_id = body["requestId"]
    data       = body["data"]

    name     = data["name"]
    price    = float(data["price"])
    tax_rate = float(data["taxRate"])
    discount = float(data["discount"])

    # discount = % → nhân (1 - discount/100)
    final_price = price * (1 + tax_rate / 100.0) * (1 - discount / 100.0)
    print(f"finalPrice={final_price:.4f}")

    answer = {
        "name":       name,
        "price":      price,
        "taxRate":    tax_rate,
        "discount":   discount,
        "finalPrice": final_price,
    }
    submit = {
        "studentCode": STUDENT_CODE,
        "qCode":       Q_CODE,
        "requestId":   request_id,
        "answer":      answer,
    }
    r = requests.post(f"{BASE}/submit", json=submit)
    print("Server response:", r.text)


if __name__ == "__main__":
    main()
