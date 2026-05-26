import grpc

import judge_pb2
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
        batch      = resp.transaction_risk
        print(f"requestId={request_id}, n={len(batch.transactions)}")

        high_risk_ids = []
        total_high_risk = 0.0
        for t in batch.transactions:
            review = (
                t.amount >= 5000.0
                or t.chargeback_count >= 2
                or (t.new_device and t.country != "VN")
            )
            if review:
                high_risk_ids.append(t.transaction_id)
                total_high_risk += t.amount
        total_high_risk = round(total_high_risk, 2)
        print(f"highRisk={len(high_risk_ids)} total={total_high_risk}")

        answer = typed_judge_pb2.TransactionRiskAnswer(
            high_risk_transaction_ids=high_risk_ids,
            review_count=len(high_risk_ids),
            total_high_risk_amount=total_high_risk,
        )
        sr = stub.SubmitTyped(typed_judge_pb2.TypedAnswer(
            student_code=STUDENT_CODE,
            question_alias=QUESTION_ALIAS,
            request_id=request_id,
            transaction_risk=answer,
        ))
        print(f"status={sr.status}, message={sr.message}")


if __name__ == "__main__":
    main()
