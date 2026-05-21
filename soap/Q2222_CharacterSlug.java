import java.text.Normalizer;
import soap.q2221.generated.CharacterService;
import soap.q2221.generated.SoapCharacterService;

public class Q2222_CharacterSlug {
    static final String STUDENT_CODE = "B21DCCN001";   // TODO
    static final String Q_CODE       = "TODO_qCode";   // TODO

    public static void main(String[] args) {
        CharacterService service = new CharacterService();
        SoapCharacterService port = service.getSoapCharacterServicePort();

        String data = port.requestString(STUDENT_CODE, Q_CODE);
        System.out.println("data=" + data);

        // lowercase → bỏ dấu Unicode → bỏ ký tự không phải chữ/số/space → gom space → dash
        String s = data.toLowerCase();
        s = Normalizer.normalize(s, Normalizer.Form.NFD)
                      .replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
        s = s.replaceAll("[^a-z0-9\\s]", "");   // bỏ dấu câu
        s = s.trim().replaceAll("\\s+", " ");   // gom space
        String slug = s.replace(' ', '-');
        System.out.println("slug=" + slug);

        port.submitString(STUDENT_CODE, Q_CODE, slug);
        System.out.println("Đã submit slug=" + slug);
    }
}
