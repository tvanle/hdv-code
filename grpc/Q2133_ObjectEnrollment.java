import GRPC.EnrollmentAnswer;
import GRPC.EnrollmentData;
import GRPC.SubmitResponse;
import GRPC.TypedAnswer;
import GRPC.TypedData;
import GRPC.TypedJudgeServiceGrpc;
import GRPC.TypedJudgeServiceGrpc.TypedJudgeServiceBlockingStub;
import GRPC.TypedRequest;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class Q2133_ObjectEnrollment {
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
            EnrollmentData d = resp.getEnrollment();
            System.out.println("requestId=" + requestId + " student=" + d.getStudentId()
                    + " gpa=" + d.getGpa() + " minGpa=" + d.getMinGpa());

            // missing_courses = required \ completed, sort tăng dần
            Set<String> completed = new HashSet<>(d.getCompletedCoursesList());
            List<String> missing  = new ArrayList<>();
            for (String c : d.getRequiredCoursesList()) {
                if (!completed.contains(c)) missing.add(c);
            }
            Collections.sort(missing);

            // gpa_gap = max(0, min_gpa - gpa), round 2dp
            double gpaGap = Math.max(0.0, d.getMinGpa() - d.getGpa());
            gpaGap = Math.round(gpaGap * 100.0) / 100.0;

            boolean eligible = missing.isEmpty() && gpaGap == 0.0;
            System.out.println("missing=" + missing + " gap=" + gpaGap + " eligible=" + eligible);

            EnrollmentAnswer answer = EnrollmentAnswer.newBuilder()
                    .setEligible(eligible)
                    .addAllMissingCourses(missing)
                    .setGpaGap(gpaGap)
                    .build();

            SubmitResponse sr = stub.submitTyped(TypedAnswer.newBuilder()
                    .setStudentCode(STUDENT_CODE)
                    .setQuestionAlias(QUESTION_ALIAS)
                    .setRequestId(requestId)
                    .setEnrollment(answer)
                    .build());

            System.out.println("status=" + sr.getStatus() + ", message=" + sr.getMessage());
        } finally {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
