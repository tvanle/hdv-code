package q2211;

import java.util.List;
import soap.q2211.generated.DataService;
import soap.q2211.generated.DataService_Service;
// LƯU Ý: tên class có thể khác tùy WSDL — kiểm tra trong package `generated/` sau khi wsimport.

public class Main {
    static final String STUDENT_CODE = "B21DCCN001";   // TODO
    static final String Q_CODE       = "irq9etkc";     // TODO

    public static void main(String[] args) {
        DataService_Service service = new DataService_Service();
        DataService port = service.getDataServicePort();

        // Phase 1 — getData
        List<Integer> data = port.getData(STUDENT_CODE, Q_CODE);
        System.out.println("data=" + data);

        // Bước 2 — tính tổng
        int sum = data.stream().mapToInt(Integer::intValue).sum();
        System.out.println("sum=" + sum);

        // Phase 2 — submitDataInt
        String status = port.submitDataInt(STUDENT_CODE, Q_CODE, sum);
        System.out.println("Server response: " + status);
    }
}
