import re
import grpc

import typed_judge_pb2
import typed_judge_pb2_grpc

EXAM_IP        = "36.50.135.242"
EXAM_PORT      = 2240
STUDENT_CODE   = "B22DCCN863"   # TODO
QUESTION_ALIAS = "TODO_alias"   # TODO

CODE_RE = re.compile(r"code=(\S+)")


def main():
    with grpc.insecure_channel(f"{EXAM_IP}:{EXAM_PORT}") as channel:
        stub = typed_judge_pb2_grpc.TypedJudgeServiceStub(channel)

        resp = stub.RequestTyped(typed_judge_pb2.TypedRequest(
            student_code=STUDENT_CODE,
            question_alias=QUESTION_ALIAS,
        ))
        request_id = resp.request_id
        batch      = resp.text_batch
        print(f"requestId={request_id}, entries={len(batch.entries)}")

        counts = {}     # INFO/WARN/ERROR → đếm
        values = []     # các code= theo thứ tự gặp
        for entry in batch.entries:
            sev = entry.split(" ", 1)[0] if " " in entry else entry
            if sev in ("INFO", "WARN", "ERROR"):
                counts[sev] = counts.get(sev, 0) + 1
            m = CODE_RE.search(entry)
            if m:
                values.append(m.group(1))
        print(f"counts={counts}")
        print(f"codes={values}")

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
