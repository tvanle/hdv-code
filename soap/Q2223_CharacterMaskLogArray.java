import java.util.ArrayList;
import java.util.List;
import soap.q2221.generated.CharacterService;
import soap.q2221.generated.SoapCharacterService;

public class Q2223_CharacterMaskLogArray {
    static final String STUDENT_CODE = "B21DCCN001";   // TODO
    static final String Q_CODE       = "TODO_qCode";   // TODO

    static final String EMAIL_RE = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}";
    static final String PHONE_RE = "(?<!\\d)0\\d{9}(?!\\d)";
    static final String TOKEN_RE = "token=\\S+";

    public static void main(String[] args) {
        CharacterService service = new CharacterService();
        SoapCharacterService port = service.getSoapCharacterServicePort();

        List<String> logs = port.requestStringArray(STUDENT_CODE, Q_CODE);
        System.out.println("logs(in)=" + logs);

        List<String> redacted = new ArrayList<>(logs.size());
        for (String line : logs) {
            String r = line
                    .replaceAll(EMAIL_RE, "[EMAIL]")
                    .replaceAll(PHONE_RE, "[PHONE]")
                    .replaceAll(TOKEN_RE, "token=[TOKEN]");
            redacted.add(r);
        }
        System.out.println("logs(out)=" + redacted);

        port.submitStringArray(STUDENT_CODE, Q_CODE, redacted);
        System.out.println("Đã submit " + redacted.size() + " dòng log đã che");
    }
}
