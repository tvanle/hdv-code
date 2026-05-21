import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import soap.q2221.generated.CharacterService;
import soap.q2221.generated.SoapCharacterService;

public class Q2251_CharacterTopBigrams {
    static final String STUDENT_CODE = "B21DCCN001";   // TODO
    static final String Q_CODE       = "TODO_qCode";   // TODO

    public static void main(String[] args) {
        CharacterService service = new CharacterService();
        SoapCharacterService port = service.getSoapCharacterServicePort();

        String raw = port.requestString(STUDENT_CODE, Q_CODE);
        System.out.println("raw=" + raw);

        // chuẩn hoá: lowercase, chỉ giữ [a-z0-9 ]
        String s = raw.toLowerCase().replaceAll("[^a-z0-9 ]", " ").trim().replaceAll("\\s+", " ");
        String[] tokens = s.isEmpty() ? new String[0] : s.split(" ");

        Map<String, Integer> count = new LinkedHashMap<>();
        for (int i = 0; i + 1 < tokens.length; i++) {
            String bg = tokens[i] + "_" + tokens[i + 1];
            count.merge(bg, 1, Integer::sum);
        }

        // top 3 theo count giảm dần; tie giữ insertion order (LinkedHashMap)
        List<Map.Entry<String, Integer>> top = count.entrySet().stream()
                .sorted(Comparator.<Map.Entry<String, Integer>>comparingInt(Map.Entry::getValue).reversed())
                .limit(3)
                .collect(Collectors.toList());

        String answer = top.stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("|"));
        System.out.println("answer=" + answer);

        port.submitString(STUDENT_CODE, Q_CODE, answer);
        System.out.println("Đã submit " + answer);
    }
}
