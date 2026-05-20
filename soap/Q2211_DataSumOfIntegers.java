import java.util.List;
import soap.q2211.generated.DataService;
import soap.q2211.generated.SoapDataService;

public class Q2211_DataSumOfIntegers {
    static final String STUDENT_CODE = "B21DCCN001";   // TODO
    static final String Q_CODE       = "irq9etkc";     // TODO

    public static void main(String[] args) {
        DataService service = new DataService();
        SoapDataService port = service.getSoapDataServicePort();

        // Phase 1 — getData
        List<Integer> data = port.getData(STUDENT_CODE, Q_CODE);
        System.out.println("data=" + data);

        int sum = data.stream().mapToInt(Integer::intValue).sum();
        System.out.println("sum=" + sum);

        // Phase 2 — submitDataInt (return void, server không trả status)
        port.submitDataInt(STUDENT_CODE, Q_CODE, sum);
        System.out.println("Đã submit sum=" + sum);
    }
}
