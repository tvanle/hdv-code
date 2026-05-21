import java.util.Arrays;
import java.util.List;
import soap.q2211.generated.DataService;
import soap.q2211.generated.SoapDataService;

public class Q2213_DataCountEvenOdd {
    static final String STUDENT_CODE = "B21DCCN001";   // TODO
    static final String Q_CODE       = "TODO_qCode";   // TODO

    public static void main(String[] args) {
        DataService service = new DataService();
        SoapDataService port = service.getSoapDataServicePort();

        List<Integer> data = port.getData(STUDENT_CODE, Q_CODE);
        System.out.println("data=" + data);

        int even = 0, odd = 0;
        for (int n : data) {
            if (n % 2 == 0) even++; else odd++;
        }
        List<String> answer = Arrays.asList("EVEN=" + even, "ODD=" + odd);
        System.out.println("answer=" + answer);

        port.submitDataStringArray(STUDENT_CODE, Q_CODE, answer);
        System.out.println("Đã submit " + answer);
    }
}
