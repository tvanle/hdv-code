import soap.q2231.generated.ObjectService;
import soap.q2231.generated.ProductY;
import soap.q2231.generated.SoapObjectService;

public class Q2261_ObjectFinalPricePercentDiscount {
    static final String STUDENT_CODE = "B21DCCN001";   // TODO
    static final String Q_CODE       = "TODO_qCode";   // TODO

    public static void main(String[] args) {
        ObjectService service = new ObjectService();
        SoapObjectService port = service.getSoapObjectServicePort();

        ProductY p = port.requestProductY(STUDENT_CODE, Q_CODE);
        System.out.printf("price=%.4f tax=%.4f disc=%.4f%n",
                p.getPrice(), p.getTaxRate(), p.getDiscount());

        // discount = % (giống Q2231, khác Q2232 trừ tuyệt đối)
        double finalPrice = p.getPrice()
                          * (1 + p.getTaxRate()  / 100.0)
                          * (1 - p.getDiscount() / 100.0);
        finalPrice = Math.round(finalPrice * 100.0) / 100.0;
        p.setFinalPrice((float) finalPrice);
        System.out.printf("finalPrice=%.2f%n", finalPrice);

        port.submitProductY(STUDENT_CODE, Q_CODE, p);
        System.out.println("Đã submit finalPrice=" + finalPrice);
    }
}
