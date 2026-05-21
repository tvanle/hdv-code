import java.util.Arrays;
import java.util.List;
import soap.q2211.generated.DataService;
import soap.q2211.generated.SoapDataService;

public class Q2242_DataMinMaxGcdSpan {
    static final String STUDENT_CODE = "B21DCCN001";   // TODO
    static final String Q_CODE       = "TODO_qCode";   // TODO

    public static void main(String[] args) {
        DataService service = new DataService();
        SoapDataService port = service.getSoapDataServicePort();

        List<Integer> data = port.getData(STUDENT_CODE, Q_CODE);
        System.out.println("data=" + data);

        int min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;
        int gcd = 0;
        for (int v : data) {
            if (v < min) min = v;
            if (v > max) max = v;
            gcd = gcd(gcd, Math.abs(v));
        }
        int span = max - min;
        List<Integer> answer = Arrays.asList(min, max, gcd, span);
        System.out.println("answer=" + answer);

        port.submitDataIntArray(STUDENT_CODE, Q_CODE, answer);
        System.out.println("Đã submit " + answer);
    }

    static int gcd(int a, int b) {
        while (b != 0) { int t = b; b = a % b; a = t; }
        return a;
    }
}
