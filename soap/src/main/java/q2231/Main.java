package q2231;

import soap.q2231.generated.ObjectService;
import soap.q2231.generated.SoapObjectService;
import soap.q2231.generated.ProductY;

public class Main {
    static final String STUDENT_CODE = "B21DCCN001";   // TODO
    static final String Q_CODE       = "z7a6WujT";     // TODO

    public static void main(String[] args) {
        ObjectService service = new ObjectService();
        SoapObjectService port = service.getSoapObjectServicePort();

        // Phase 1 — requestProductY (ProductY: price/taxRate/discount/finalPrice là float, không có name)
        ProductY p = port.requestProductY(STUDENT_CODE, Q_CODE);
        System.out.printf("price=%.4f tax=%.4f disc=%.4f%n",
                p.getPrice(), p.getTaxRate(), p.getDiscount());

        // Bước 2 — tính finalPrice (% discount). Schema dùng float → cast về float khi set.
        double finalPrice = p.getPrice()
                          * (1 + p.getTaxRate()  / 100.0)
                          * (1 - p.getDiscount() / 100.0);
        p.setFinalPrice((float) finalPrice);
        System.out.printf("finalPrice=%.4f%n", finalPrice);

        // Phase 2 — submitProductY (return void)
        port.submitProductY(STUDENT_CODE, Q_CODE, p);
        System.out.println("Đã submit finalPrice=" + finalPrice);
    }
}
