import GRPC.SensorReading;
import GRPC.SensorTelemetryAnswer;
import GRPC.SensorTelemetryData;
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
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Q2113_DataSensorTelemetry {
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
            SensorTelemetryData t = resp.getSensorTelemetry();
            double threshold = t.getThreshold();
            List<SensorReading> readings = t.getReadingsList();
            int n = readings.size();
            System.out.println("requestId=" + requestId + ", n=" + n + " threshold=" + threshold);

            // average
            double sum = 0.0;
            List<Double> values = new ArrayList<>(n);
            int anomalyCount = 0;
            for (SensorReading r : readings) {
                double v = r.getValue();
                sum += v;
                values.add(v);
                if (v > threshold) anomalyCount++;
            }
            double average = n == 0 ? 0.0 : sum / n;

            // p95: sort asc, lấy index ceil(n*0.95) - 1
            Collections.sort(values);
            double p95 = 0.0;
            if (n > 0) {
                int idx = (int) Math.ceil(n * 0.95) - 1;
                if (idx < 0)  idx = 0;
                if (idx >= n) idx = n - 1;
                p95 = values.get(idx);
            }

            average = Math.round(average * 100.0) / 100.0;
            p95     = Math.round(p95     * 100.0) / 100.0;
            System.out.printf("avg=%.2f p95=%.2f anomaly=%d%n", average, p95, anomalyCount);

            SensorTelemetryAnswer answer = SensorTelemetryAnswer.newBuilder()
                    .setAverage(average)
                    .setP95(p95)
                    .setAnomalyCount(anomalyCount)
                    .build();

            SubmitResponse sr = stub.submitTyped(TypedAnswer.newBuilder()
                    .setStudentCode(STUDENT_CODE)
                    .setQuestionAlias(QUESTION_ALIAS)
                    .setRequestId(requestId)
                    .setSensorTelemetry(answer)
                    .build());

            System.out.println("status=" + sr.getStatus() + ", message=" + sr.getMessage());
        } finally {
            channel.shutdown().awaitTermination(5, TimeUnit.SECONDS);
        }
    }
}
