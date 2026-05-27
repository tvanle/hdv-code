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
        d          = resp.enrollment
        print(f"requestId={request_id} student={d.student_id} "
              f"gpa={d.gpa} minGpa={d.min_gpa}")

        # missing_courses = required \ completed, sort tăng dần
        completed = set(d.completed_courses)
        missing   = sorted(c for c in d.required_courses if c not in completed)

        # gpa_gap = max(0, min_gpa - gpa), round 2dp
        gpa_gap = max(0.0, d.min_gpa - d.gpa)
        gpa_gap = round(gpa_gap, 2)

        eligible = not missing and gpa_gap == 0.0
        print(f"missing={missing} gap={gpa_gap} eligible={eligible}")

        answer = typed_judge_pb2.EnrollmentAnswer(
            eligible=eligible,
            missing_courses=missing,
            gpa_gap=gpa_gap,
        )
        sr = stub.SubmitTyped(typed_judge_pb2.TypedAnswer(
            student_code=STUDENT_CODE,
            question_alias=QUESTION_ALIAS,
            request_id=request_id,
            enrollment=answer,
        ))
        print(f"status={sr.status}, message={sr.message}")


if __name__ == "__main__":
    main()
