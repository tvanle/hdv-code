from zeep import Client

EXAM_IP      = "36.50.135.242"
STUDENT_CODE = "B22DCCN863"   # TODO
Q_CODE       = "TODO_qCode"   # TODO
WSDL         = f"http://{EXAM_IP}:2221/DataService?wsdl"


def is_prime(n: int) -> bool:
    if n < 2:
        return False
    if n < 4:
        return True
    if n % 2 == 0:
        return False
    i = 3
    while i * i <= n:
        if n % i == 0:
            return False
        i += 2
    return True


def main():
    client = Client(WSDL)

    data = client.service.getData(STUDENT_CODE, Q_CODE)
    print("data=", data)

    prime_count = 0
    checksum    = 0
    for i, v in enumerate(data):
        v = int(v)
        if is_prime(v):
            prime_count += 1
        checksum = (checksum + (i + 1) * v) % 100000
    if checksum < 0:
        checksum += 100000

    answer = f"primeCount={prime_count};checksum={checksum}"
    print("answer=", answer)

    client.service.submitDataString(STUDENT_CODE, Q_CODE, answer)
    print("Đã submit", answer)


if __name__ == "__main__":
    main()
