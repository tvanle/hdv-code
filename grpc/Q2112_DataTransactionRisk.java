import GRPC.Transaction;
import GRPC.TransactionRiskAnswer;
import GRPC.TransactionRiskBatchData;
import GRPC.TypedAnswer;
import GRPC.TypedData;
import GRPC.TypedJudgeServiceGrpc;
import GRPC.TypedJudgeServiceGrpc.TypedJudgeServiceBlockingStub;
import GRPC.TypedRequest;
import GRPC.SubmitResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Q2112_DataTransactionRisk {
    static final String EXAM_IP        = "36.50.135.242";
    static final int    EXAM_PORT      = 2240;
    static final String STUDENT_CODE   = "B21DCCN001";   // TODO
    static final String QUESTION_ALIAS = "TODO_alias";   // TODO

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
            TransactionRiskBatchData batch = resp.getTransactionRisk();
            System.out.println("requestId=" + requestId + ", n=" + batch.getTransactionsCount());

            // Lọc giao dịch high-risk theo thứ tự input
            List<String> highRiskIds = new ArrayList<>();
            double totalHighRisk = 0.0;
            for (Transaction t : batch.getTransactionsList()) {
                boolean review =
                        t.getAmount() >= 5000.0
                        || t.getChargebackCount() >= 2
                        || (t.getNewDevice() && !"VN".equals(t.getCountry()));
                if (review) {
                    highRiskIds.add(t.getTransactionId());
                    totalHighRisk += t.getAmount();
                }
            }
            totalHighRisk = Math.round(totalHighRisk * 100.0) / 100.0;
            System.out.println("highRisk=" + highRiskIds.size() + " total=" + totalHighRisk);

            TransactionRiskAnswer answer = TransactionRiskAnswer.newBuilder()
                    .addAllHighRiskTransactionIds(highRiskIds)
                    .setReviewCount(highRiskIds.size())
                    .setTotalHighRiskAmount(totalHighRisk)
                    .build();

            SubmitResponse sr = stub.submitTyped(TypedAnswer.newBuilder()
                    .setStudentCode(STUDENT_CODE)
                    .setQuestionAlias(QUESTION_ALIAS)
                    .setRequestId(requestId)
                    .setTransactionRisk(answer)
                    .build());

            System.out.println("status=" + sr.getStatus() + ", message=" + sr.getMessage());
        } finally {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
