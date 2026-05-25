import java.util.List;
import soap.q2211.generated.DataService;
import soap.q2211.generated.SoapDataService;

public class Q2241_DataPrimeCountChecksum {
    static final String STUDENT_CODE = "B22DCCN863";   // TODO
    static final String Q_CODE       = "TODO_qCode";   // TODO

    public static void main(String[] args) {
        DataService service = new DataService();
        SoapDataService port = service.getSoapDataServicePort();

        List<Integer> data = port.getData(STUDENT_CODE, Q_CODE);
        System.out.println("data=" + data);

        // checksum = sum((i+1) * value[i]) mod 100000
        long primeCount = 0;
        long checksum   = 0;
        for (int i = 0; i < data.size(); i++) {
            int v = data.get(i);
            if (isPrime(v)) primeCount++;
            checksum = (checksum + (long)(i + 1) * v) % 100000L;
        }
        if (checksum < 0) checksum += 100000L;
        String answer = "primeCount=" + primeCount + ";checksum=" + checksum;
        System.out.println("answer=" + answer);

        port.submitDataString(STUDENT_CODE, Q_CODE, answer);
        System.out.println("Đã submit " + answer);
    }

    static boolean isPrime(int n) {
        if (n < 2) return false;
        if (n < 4) return true;
        if (n % 2 == 0) return false;
        for (int i = 3; (long)i * i <= n; i += 2) if (n % i == 0) return false;
        return true;
    }
}
