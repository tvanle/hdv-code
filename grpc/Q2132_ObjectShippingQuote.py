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
        request_id  = resp.request_id
        data        = resp.shipping_quote
        weight_kg   = data.weight_kg
        max_eta     = data.max_eta_days
        print(f"requestId={request_id} orderId={data.order_id} "
              f"weight={weight_kg} maxEta={max_eta}")

        best_carrier = None
        best_fee     = math.inf
        best_eta     = -1
        best_reliab  = -1.0

        for q in data.quotes:
            if q.eta_days > max_eta:
                continue
            fee = q.base_fee + weight_kg * q.per_kg_fee
            fee = round(fee, 2)
            if fee < best_fee or (fee == best_fee and q.reliability > best_reliab):
                best_carrier = q.carrier
                best_fee     = fee
                best_eta     = q.eta_days
                best_reliab  = q.reliability

        if best_carrier is None:
            raise RuntimeError("Không có quote hợp lệ")
        print(f"best: carrier={best_carrier} fee={best_fee:.2f} eta={best_eta}")

        answer = typed_judge_pb2.ShippingQuoteAnswer(
            carrier=best_carrier,
            total_fee=best_fee,
            eta_days=best_eta,
        )
        sr = stub.SubmitTyped(typed_judge_pb2.TypedAnswer(
            student_code=STUDENT_CODE,
            question_alias=QUESTION_ALIAS,
            request_id=request_id,
            shipping_quote=answer,
        ))
        print(f"status={sr.status}, message={sr.message}")


if __name__ == "__main__":
    main()
