import java.util.List;
import soap.q2211.generated.DataService;
import soap.q2211.generated.SoapDataService;

public class Q2212_DataSumEven {
    static final String STUDENT_CODE = "B22DCCN863";   // TODO
    static final String Q_CODE       = "TODO_qCode";   // TODO

    public static void main(String[] args) {
        DataService service = new DataService();
        SoapDataService port = service.getSoapDataServicePort();

        List<Integer> data = port.getData(STUDENT_CODE, Q_CODE);
        System.out.println("data=" + data);

        int sumEven = 0;
        for (int n : data) if (n % 2 == 0) sumEven += n;
        System.out.println("sumEven=" + sumEven);

        port.submitDataInt(STUDENT_CODE, Q_CODE, sumEven);
        System.out.println("Đã submit sumEven=" + sumEven);
    }
}
