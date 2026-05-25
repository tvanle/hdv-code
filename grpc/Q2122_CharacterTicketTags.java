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
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Q2122_CharacterTicketTags {
    static final String EXAM_IP        = "36.50.135.242";
    static final int    EXAM_PORT      = 2240;
    static final String STUDENT_CODE   = "B22DCCN863";   // TODO
    static final String QUESTION_ALIAS = "TODO_alias";   // TODO

    static final List<String> KEYWORDS =
            Arrays.asList("account", "payment", "refund", "shipping");

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
            System.out.println("requestId=" + requestId + ", mode=" + batch.getMode()
                    + ", entries=" + batch.getEntriesCount());

            // Đếm số entry chứa từng keyword (case-insensitive)
            Map<String, Integer> counts = new LinkedHashMap<>();
            for (String kw : KEYWORDS) {
                int c = 0;
                for (String entry : batch.getEntriesList()) {
                    if (entry.toLowerCase().contains(kw)) c++;
                }
                if (c > 0) counts.put(kw, c);
            }

            // values = keyword xuất hiện, sort alphabet
            List<String> values = new ArrayList<>(counts.keySet());
            Collections.sort(values);
            System.out.println("values=" + values + " counts=" + counts);

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
