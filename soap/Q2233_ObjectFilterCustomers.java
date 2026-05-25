import java.util.ArrayList;
import java.util.List;
import soap.q2231.generated.Customer;
import soap.q2231.generated.ObjectService;
import soap.q2231.generated.SoapObjectService;

public class Q2233_ObjectFilterCustomers {
    static final String STUDENT_CODE = "B22DCCN863";   // TODO
    static final String Q_CODE       = "TODO_qCode";   // TODO

    public static void main(String[] args) {
        ObjectService service = new ObjectService();
        SoapObjectService port = service.getSoapObjectServicePort();

        List<Customer> all = port.requestListCustomer(STUDENT_CODE, Q_CODE);
        System.out.println("total=" + all.size());

        // Giữ thứ tự gốc; lọc purchaseCount >= 5 và totalSpent > 5000
        List<Customer> selected = new ArrayList<>();
        for (Customer c : all) {
            if (c.getPurchaseCount() >= 5 && c.getTotalSpent() > 5000f) {
                selected.add(c);
            }
        }
        System.out.println("selected=" + selected.size());

        port.submitListCustomer(STUDENT_CODE, Q_CODE, selected);
        System.out.println("Đã submit " + selected.size() + " khách");
    }
}
