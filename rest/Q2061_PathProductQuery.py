import requests

EXAM_IP      = "36.50.135.242"
STUDENT_CODE = "B22DCCN863"   # TODO
Q_CODE       = "Kcol2JVM"     # TODO
BASE         = f"http://{EXAM_IP}:2230/api/rest/path"


def main():
    r = requests.get(BASE, params={"studentCode": STUDENT_CODE, "qCode": Q_CODE})
    body       = r.json()
    request_id = body["requestId"]
    products   = body["data"]

    product_id = products[0]["id"]
    print("Chọn productId=", product_id)

    r = requests.get(f"{BASE}/{product_id}", params={
        "studentCode": STUDENT_CODE,
        "qCode":       Q_CODE,
        "requestId":   request_id,
        "currency":    "USD",
    })
    print("Server response:", r.text)


if __name__ == "__main__":
    main()
