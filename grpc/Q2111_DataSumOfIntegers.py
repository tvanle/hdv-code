import grpc

import judge_pb2
import judge_pb2_grpc

EXAM_IP        = "36.50.135.242"
EXAM_PORT      = 2240
STUDENT_CODE   = "B22DCCN863"   # TODO
QUESTION_ALIAS = "gDdfzR0y"     # TODO


def main():
    with grpc.insecure_channel(f"{EXAM_IP}:{EXAM_PORT}") as channel:
        stub = judge_pb2_grpc.JudgeServiceStub(channel)

        resp = stub.Request(judge_pb2.JudgeRequest(
            student_code=STUDENT_CODE,
            question_alias=QUESTION_ALIAS,
        ))
        request_id = resp.request_id
        data       = resp.data
        print(f"requestId={request_id}, data={data}")

        total = sum(int(t.strip()) for t in data.split(",") if t.strip())
        print(f"sum={total}")

        sr = stub.Submit(judge_pb2.SubmitRequest(
            student_code=STUDENT_CODE,
            question_alias=QUESTION_ALIAS,
            request_id=request_id,
            answer=str(total),
        ))
        print(f"status={sr.status}, message={sr.message}")


if __name__ == "__main__":
    main()
