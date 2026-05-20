import GRPC.JudgeRequest;
import GRPC.JudgeResponse;
import GRPC.JudgeServiceGrpc;
import GRPC.JudgeServiceGrpc.JudgeServiceBlockingStub;
import GRPC.SubmitRequest;
import GRPC.SubmitResponse;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.concurrent.TimeUnit;

public class Q2111_DataSumOfIntegers {
    static final String EXAM_IP        = "36.50.135.242";
    static final int    EXAM_PORT      = 2240;
    static final String STUDENT_CODE   = "B21DCCN001";   // TODO
    static final String QUESTION_ALIAS = "gDdfzR0y";     // TODO

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
            String data      = resp.getData();
            System.out.println("requestId=" + requestId + ", data=" + data);

            // parse "12,45,88,3,210" → tổng
            long sum = 0;
            for (String t : data.split(",")) {
                if (!t.isEmpty()) sum += Long.parseLong(t.trim());
            }
            System.out.println("sum=" + sum);

            SubmitResponse sr = stub.submit(SubmitRequest.newBuilder()
                    .setStudentCode(STUDENT_CODE)
                    .setQuestionAlias(QUESTION_ALIAS)
                    .setRequestId(requestId)
                    .setAnswer(Long.toString(sum))
                    .build());

            System.out.println("status=" + sr.getStatus() + ", message=" + sr.getMessage());
        } finally {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
