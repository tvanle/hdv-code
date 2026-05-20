import soap.q2231.generated.ObjectService;
import soap.q2231.generated.SoapObjectService;
import soap.q2231.generated.ProductY;

public class Q2231_ObjectFinalPrice {
    static final String STUDENT_CODE = "B21DCCN001";   // TODO
    static final String Q_CODE       = "z7a6WujT";     // TODO

    public static void main(String[] args) {
        ObjectService service = new ObjectService();
        SoapObjectService port = service.getSoapObjectServicePort();

        // ProductY chỉ có price/taxRate/discount/finalPrice (float), không có name
        ProductY p = port.requestProductY(STUDENT_CODE, Q_CODE);
        System.out.printf("price=%.4f tax=%.4f disc=%.4f%n",
                p.getPrice(), p.getTaxRate(), p.getDiscount());

        // discount = %, công thức nhân (1 - discount/100)
        double finalPrice = p.getPrice()
                          * (1 + p.getTaxRate()  / 100.0)
                          * (1 - p.getDiscount() / 100.0);
        p.setFinalPrice((float) finalPrice);
        System.out.printf("finalPrice=%.4f%n", finalPrice);

        port.submitProductY(STUDENT_CODE, Q_CODE, p);
        System.out.println("Đã submit finalPrice=" + finalPrice);
    }
}
