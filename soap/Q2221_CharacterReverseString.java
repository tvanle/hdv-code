import soap.q2221.generated.CharacterService;
import soap.q2221.generated.SoapCharacterService;

public class Q2221_CharacterReverseString {
    static final String STUDENT_CODE = "B22DCCN863";   // TODO
    static final String Q_CODE       = "8QVq0OaK";     // TODO

    public static void main(String[] args) {
        CharacterService service = new CharacterService();
        SoapCharacterService port = service.getSoapCharacterServicePort();

        String data = port.requestString(STUDENT_CODE, Q_CODE);
        System.out.println("data=" + data);

        String reversed = new StringBuilder(data).reverse().toString();
        System.out.println("reversed=" + reversed);

        port.submitString(STUDENT_CODE, Q_CODE, reversed);
        System.out.println("Đã submit reversed=" + reversed);
    }
}
