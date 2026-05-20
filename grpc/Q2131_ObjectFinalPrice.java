import GRPC.JudgeRequest;
import GRPC.JudgeResponse;
import GRPC.JudgeServiceGrpc;
import GRPC.JudgeServiceGrpc.JudgeServiceBlockingStub;
import GRPC.SubmitRequest;
import GRPC.SubmitResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import org.json.JSONObject;

public class Q2131_ObjectFinalPrice {
    static final String EXAM_IP        = "36.50.135.242";
    static final int    EXAM_PORT      = 2240;
    static final String STUDENT_CODE   = "B21DCCN001";   // TODO
    static final String QUESTION_ALIAS = "WEFtuyKl";     // TODO

    public static void main(String[] args) throws InterruptedException {
        ManagedChannel channel = ManagedChannelBuilder
                .forAddress(EXAM_IP, EXAM_PORT)
                .usePlaintext()
                .build();

        try {
            JudgeServiceBlockingStub stub = JudgeServiceGrpc.newBlockingStub(channel);

            JudgeResponse resp = stub.request(JudgeRequest.newBuilder()
                    .setStudentCode(STUDENT_CODE)
                    .setQuestionAlias(QUESTION_ALIAS)
                    .build());

            String requestId = resp.getRequestId();
            JSONObject product = new JSONObject(resp.getData());

            double price    = product.getDouble("price");
            double taxRate  = product.getDouble("taxRate");
            double discount = product.getDouble("discount");

            // discount = SỐ TIỀN, trừ trực tiếp (khác Q2031/Q2231 dùng %)
            double finalPrice = price * (1 + taxRate / 100.0) - discount;
            String answer     = String.format(Locale.US, "%.2f", finalPrice);
            System.out.println("answer=" + answer);

            SubmitResponse sr = stub.submit(SubmitRequest.newBuilder()
                    .setStudentCode(STUDENT_CODE)
                    .setQuestionAlias(QUESTION_ALIAS)
                    .setRequestId(requestId)
                    .setAnswer(answer)
                    .build());

            System.out.println("status=" + sr.getStatus() + ", message=" + sr.getMessage());
        } finally {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
