package q2221;

import soap.q2221.generated.CharacterService;
import soap.q2221.generated.CharacterService_Service;

public class Main {
    static final String STUDENT_CODE = "B21DCCN001";   // TODO
    static final String Q_CODE       = "8QVq0OaK";     // TODO

    public static void main(String[] args) {
        CharacterService_Service service = new CharacterService_Service();
        CharacterService port = service.getCharacterServicePort();

        // Phase 1
        String data = port.requestString(STUDENT_CODE, Q_CODE);
        System.out.println("data=" + data);

        // Bước 2 — đảo ngược
        String reversed = new StringBuilder(data).reverse().toString();
        System.out.println("reversed=" + reversed);

        // Phase 2
        String status = port.submitString(STUDENT_CODE, Q_CODE, reversed);
        System.out.println("Server response: " + status);
    }
}
