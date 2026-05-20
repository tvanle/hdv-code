package q2221;

import soap.q2221.generated.CharacterService;
import soap.q2221.generated.SoapCharacterService;

public class Main {
    static final String STUDENT_CODE = "B21DCCN001";   // TODO
    static final String Q_CODE       = "8QVq0OaK";     // TODO

    public static void main(String[] args) {
        CharacterService service = new CharacterService();
        SoapCharacterService port = service.getSoapCharacterServicePort();

        // Phase 1
        String data = port.requestString(STUDENT_CODE, Q_CODE);
        System.out.println("data=" + data);

        // Bước 2 — đảo ngược
        String reversed = new StringBuilder(data).reverse().toString();
        System.out.println("reversed=" + reversed);

        // Phase 2 (return void)
        port.submitString(STUDENT_CODE, Q_CODE, reversed);
        System.out.println("Đã submit reversed=" + reversed);
    }
}
