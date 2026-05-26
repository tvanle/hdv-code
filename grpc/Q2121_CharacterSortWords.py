import grpc

import judge_pb2
import judge_pb2_grpc

EXAM_IP        = "36.50.135.242"
EXAM_PORT      = 2240
STUDENT_CODE   = "B22DCCN863"   # TODO
QUESTION_ALIAS = "Tqy8zjYo"     # TODO


def main():
    with grpc.insecure_channel(f"{EXAM_IP}:{EXAM_PORT}") as channel:
        stub = judge_pb2_grpc.JudgeServiceStub(channel)

        resp = stub.Request(judge_pb2.JudgeRequest(
            student_code=STUDENT_CODE,
            question_alias=QUESTION_ALIAS,
        ))
        request_id = resp.request_id
        data       = resp.data

        # Sort case-INSENSITIVE, nối bằng dấu phẩy
        words  = sorted(data.split(","), key=str.lower)
        answer = ",".join(words)
        print("answer=", answer)

        sr = stub.Submit(judge_pb2.SubmitRequest(
            student_code=STUDENT_CODE,
            question_alias=QUESTION_ALIAS,
            request_id=request_id,
            answer=answer,
        ))
        print(f"status={sr.status}, message={sr.message}")


if __name__ == "__main__":
    main()
