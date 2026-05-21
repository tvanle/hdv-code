import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import soap.q2221.generated.CharacterService;
import soap.q2221.generated.SoapCharacterService;

public class Q2252_CharacterMaskSortBySeverity {
    static final String STUDENT_CODE = "B21DCCN001";   // TODO
    static final String Q_CODE       = "TODO_qCode";   // TODO

    static final String EMAIL_RE = "[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}";
    static final String PHONE_RE = "(?<!\\d)0\\d{9}(?!\\d)";
    static final String TOKEN_RE = "token=\\S+";

    static final Map<String, Integer> SEVERITY = new HashMap<>();
    static {
        SEVERITY.put("ERROR", 0);
        SEVERITY.put("WARN",  1);
        SEVERITY.put("INFO",  2);
    }

    public static void main(String[] args) {
        CharacterService service = new CharacterService();
        SoapCharacterService port = service.getSoapCharacterServicePort();

        List<String> logs = port.requestStringArray(STUDENT_CODE, Q_CODE);
        System.out.println("logs(in)=" + logs.size() + " dòng");

        // 1) che dữ liệu nhạy cảm
        List<String> redacted = new ArrayList<>(logs.size());
        for (String line : logs) {
            redacted.add(line
                    .replaceAll(EMAIL_RE, "[EMAIL]")
                    .replaceAll(PHONE_RE, "[PHONE]")
                    .replaceAll(TOKEN_RE, "token=[TOKEN]"));
        }

        // 2) sort theo mức ERROR < WARN < INFO (token đầu tiên); ổn định (stable sort)
        redacted.sort((a, b) -> {
            int sa = SEVERITY.getOrDefault(firstToken(a), Integer.MAX_VALUE);
            int sb = SEVERITY.getOrDefault(firstToken(b), Integer.MAX_VALUE);
            return Integer.compare(sa, sb);
        });
        System.out.println("logs(sorted)=" + redacted);

        port.submitStringArray(STUDENT_CODE, Q_CODE, redacted);
        System.out.println("Đã submit " + redacted.size() + " dòng");
    }

    static String firstToken(String s) {
        int sp = s.indexOf(' ');
        return sp < 0 ? s : s.substring(0, sp);
    }
}
