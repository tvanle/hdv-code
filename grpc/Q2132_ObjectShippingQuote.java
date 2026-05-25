import GRPC.ShippingQuote;
import GRPC.ShippingQuoteAnswer;
import GRPC.ShippingQuoteData;
import GRPC.SubmitResponse;
import GRPC.TypedAnswer;
import GRPC.TypedData;
import GRPC.TypedJudgeServiceGrpc;
import GRPC.TypedJudgeServiceGrpc.TypedJudgeServiceBlockingStub;
import GRPC.TypedRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.TimeUnit;

public class Q2132_ObjectShippingQuote {
    static final String EXAM_IP        = "36.50.135.242";
    static final int    EXAM_PORT      = 2240;
    static final String STUDENT_CODE   = "B22DCCN863";   // TODO
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
            ShippingQuoteData data = resp.getShippingQuote();
            double weightKg   = data.getWeightKg();
            int    maxEtaDays = data.getMaxEtaDays();
            System.out.println("requestId=" + requestId + " orderId=" + data.getOrderId()
                    + " weight=" + weightKg + " maxEta=" + maxEtaDays);

            String bestCarrier = null;
            double bestFee     = Double.POSITIVE_INFINITY;
            int    bestEta     = -1;
            double bestReliab  = -1.0;

            for (ShippingQuote q : data.getQuotesList()) {
                if (q.getEtaDays() > maxEtaDays) continue;
                double fee = q.getBaseFee() + weightKg * q.getPerKgFee();
                fee = Math.round(fee * 100.0) / 100.0;
                if (fee < bestFee || (fee == bestFee && q.getReliability() > bestReliab)) {
                    bestCarrier = q.getCarrier();
                    bestFee     = fee;
                    bestEta     = q.getEtaDays();
                    bestReliab  = q.getReliability();
                }
            }
            if (bestCarrier == null) throw new RuntimeException("Không có quote hợp lệ");
            System.out.printf("best: carrier=%s fee=%.2f eta=%d%n", bestCarrier, bestFee, bestEta);

            ShippingQuoteAnswer answer = ShippingQuoteAnswer.newBuilder()
                    .setCarrier(bestCarrier)
                    .setTotalFee(bestFee)
                    .setEtaDays(bestEta)
                    .build();

            SubmitResponse sr = stub.submitTyped(TypedAnswer.newBuilder()
                    .setStudentCode(STUDENT_CODE)
                    .setQuestionAlias(QUESTION_ALIAS)
                    .setRequestId(requestId)
                    .setShippingQuote(answer)
                    .build());

            System.out.println("status=" + sr.getStatus() + ", message=" + sr.getMessage());
        } finally {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
