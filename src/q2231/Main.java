package q2231;

import soap.q2231.generated.ObjectService;
import soap.q2231.generated.ObjectService_Service;
import soap.q2231.generated.ProductY;

public class Main {
    static final String STUDENT_CODE = "B21DCCN001";   // TODO
    static final String Q_CODE       = "z7a6WujT";     // TODO

    public static void main(String[] args) {
        ObjectService_Service service = new ObjectService_Service();
        ObjectService port = service.getObjectServicePort();

        // Phase 1 — requestProductY
        ProductY p = port.requestProductY(STUDENT_CODE, Q_CODE);
        System.out.printf("name=%s price=%.2f tax=%.2f disc=%.2f%n",
                p.getName(), p.getPrice(), p.getTaxRate(), p.getDiscount());

        // Bước 2 — tính finalPrice (% discount)
        double finalPrice = p.getPrice()
                          * (1 + p.getTaxRate()  / 100.0)
                          * (1 - p.getDiscount() / 100.0);
        p.setFinalPrice(finalPrice);
        System.out.printf("finalPrice=%.4f%n", finalPrice);

        // Phase 2 — submitProductY
        String status = port.submitProductY(STUDENT_CODE, Q_CODE, p);
        System.out.println("Server response: " + status);
    }
}
