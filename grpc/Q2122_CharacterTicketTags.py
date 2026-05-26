import grpc

import typed_judge_pb2
import typed_judge_pb2_grpc

EXAM_IP        = "36.50.135.242"
EXAM_PORT      = 2240
STUDENT_CODE   = "B22DCCN863"   # TODO
QUESTION_ALIAS = "TODO_alias"   # TODO

KEYWORDS = ["account", "payment", "refund", "shipping"]


def main():
    with grpc.insecure_channel(f"{EXAM_IP}:{EXAM_PORT}") as channel:
        stub = typed_judge_pb2_grpc.TypedJudgeServiceStub(channel)

        resp = stub.RequestTyped(typed_judge_pb2.TypedRequest(
            student_code=STUDENT_CODE,
            question_alias=QUESTION_ALIAS,
        ))
        request_id = resp.request_id
        batch      = resp.text_batch
        print(f"requestId={request_id}, mode={batch.mode}, entries={len(batch.entries)}")

        # Đếm số entry chứa từng keyword (case-insensitive)
        counts = {}
        for kw in KEYWORDS:
            c = sum(1 for entry in batch.entries if kw in entry.lower())
            if c > 0:
                counts[kw] = c

        values = sorted(counts.keys())
        print(f"values={values} counts={counts}")

        answer = typed_judge_pb2.TextBatchAnswer(
            values=values,
            counts=counts,
        )
        sr = stub.SubmitTyped(typed_judge_pb2.TypedAnswer(
            student_code=STUDENT_CODE,
            question_alias=QUESTION_ALIAS,
            request_id=request_id,
            text_batch=answer,
        ))
        print(f"status={sr.status}, message={sr.message}")


if __name__ == "__main__":
    main()
