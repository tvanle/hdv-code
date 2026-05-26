import math
import grpc

import typed_judge_pb2
import typed_judge_pb2_grpc

EXAM_IP        = "36.50.135.242"
EXAM_PORT      = 2240
STUDENT_CODE   = "B22DCCN863"   # TODO
QUESTION_ALIAS = "TODO_alias"   # TODO


def main():
    with grpc.insecure_channel(f"{EXAM_IP}:{EXAM_PORT}") as channel:
        stub = typed_judge_pb2_grpc.TypedJudgeServiceStub(channel)

        resp = stub.RequestTyped(typed_judge_pb2.TypedRequest(
            student_code=STUDENT_CODE,
            question_alias=QUESTION_ALIAS,
        ))
        request_id = resp.request_id
        t          = resp.sensor_telemetry
        threshold  = t.threshold
        readings   = list(t.readings)
        n          = len(readings)
        print(f"requestId={request_id}, n={n} threshold={threshold}")

        values = [r.value for r in readings]
        avg = sum(values) / n if n else 0.0
        anomaly_count = sum(1 for v in values if v > threshold)

        # p95: sort asc, idx = ceil(n*0.95) - 1
        p95 = 0.0
        if n > 0:
            sv  = sorted(values)
            idx = max(0, min(n - 1, math.ceil(n * 0.95) - 1))
            p95 = sv[idx]

        avg = round(avg, 2)
        p95 = round(p95, 2)
        print(f"avg={avg:.2f} p95={p95:.2f} anomaly={anomaly_count}")

        answer = typed_judge_pb2.SensorTelemetryAnswer(
            average=avg,
            p95=p95,
            anomaly_count=anomaly_count,
        )
        sr = stub.SubmitTyped(typed_judge_pb2.TypedAnswer(
            student_code=STUDENT_CODE,
            question_alias=QUESTION_ALIAS,
            request_id=request_id,
            sensor_telemetry=answer,
        ))
        print(f"status={sr.status}, message={sr.message}")


if __name__ == "__main__":
    main()
