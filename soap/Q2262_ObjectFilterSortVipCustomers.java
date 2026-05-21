import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import soap.q2231.generated.Customer;
import soap.q2231.generated.ObjectService;
import soap.q2231.generated.SoapObjectService;

public class Q2262_ObjectFilterSortVipCustomers {
    static final String STUDENT_CODE = "B21DCCN001";   // TODO
    static final String Q_CODE       = "TODO_qCode";   // TODO

    public static void main(String[] args) {
        ObjectService service = new ObjectService();
        SoapObjectService port = service.getSoapObjectServicePort();

        List<Customer> all = port.requestListCustomer(STUDENT_CODE, Q_CODE);
        System.out.println("total=" + all.size());

        // Lọc purchaseCount >= 6 và totalSpent >= 4000
        List<Customer> vip = new ArrayList<>();
        for (Customer c : all) {
            if (c.getPurchaseCount() >= 6 && c.getTotalSpent() >= 4000f) {
                vip.add(c);
            }
        }
        // Sort: totalSpent giảm dần; tie → customerId tăng dần
        vip.sort(
                Comparator.comparingDouble((Customer c) -> c.getTotalSpent()).reversed()
                        .thenComparing(Customer::getCustomerId)
        );
        System.out.println("vip=" + vip.size() + " khách");

        port.submitListCustomer(STUDENT_CODE, Q_CODE, vip);
        System.out.println("Đã submit " + vip.size() + " khách VIP");
    }
}
