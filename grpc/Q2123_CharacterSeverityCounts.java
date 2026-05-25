import GRPC.SubmitResponse;
import GRPC.TextBatchAnswer;
import GRPC.TextBatchData;
import GRPC.TypedAnswer;
import GRPC.TypedData;
import GRPC.TypedJudgeServiceGrpc;
import GRPC.TypedJudgeServiceGrpc.TypedJudgeServiceBlockingStub;
import GRPC.TypedRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Q2123_CharacterSeverityCounts {
    static final String EXAM_IP        = "36.50.135.242";
    static final int    EXAM_PORT      = 2240;
    static final String STUDENT_CODE   = "B22DCCN863";   // TODO
    static final String QUESTION_ALIAS = "TODO_alias";   // TODO

    static final Pattern CODE_RE = Pattern.compile("code=(\\S+)");

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(EXAM_IP, EXAM_PORT).usePlaintext().build();
        try {
            TypedJudgeServiceBlockingStub stub = TypedJudgeServiceGrpc.newBlockingStub(channel);

            TypedData resp = stub.requestTyped(TypedRequest.newBuilder()
                    .setStudentCode(STUDENT_CODE)
                    .setQuestionAlias(QUESTION_ALIAS)
                    .build());

            String requestId = resp.getRequestId();
            TextBatchData batch = resp.getTextBatch();
            System.out.println("requestId=" + requestId + ", entries=" + batch.getEntriesCount());

            // counts theo severity (token đầu tiên: INFO/WARN/ERROR)
            Map<String, Integer> counts = new LinkedHashMap<>();
            // values = các code= xuất hiện đầu tiên trong các entry có code=... (theo thứ tự gặp)
            List<String> values = new ArrayList<>();

            for (String entry : batch.getEntriesList()) {
                int sp = entry.indexOf(' ');
                String sev = sp < 0 ? entry : entry.substring(0, sp);
                if ("INFO".equals(sev) || "WARN".equals(sev) || "ERROR".equals(sev)) {
                    counts.merge(sev, 1, Integer::sum);
                }
                Matcher m = CODE_RE.matcher(entry);
                if (m.find()) values.add(m.group(1));
            }
            System.out.println("counts=" + counts);
            System.out.println("codes=" + values);

            TextBatchAnswer answer = TextBatchAnswer.newBuilder()
                    .addAllValues(values)
                    .putAllCounts(counts)
                    .build();

            SubmitResponse sr = stub.submitTyped(TypedAnswer.newBuilder()
                    .setStudentCode(STUDENT_CODE)
                    .setQuestionAlias(QUESTION_ALIAS)
                    .setRequestId(requestId)
                    .setTextBatch(answer)
                    .build());

            System.out.println("status=" + sr.getStatus() + ", message=" + sr.getMessage());
        } finally {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
