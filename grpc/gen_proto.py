"""Sinh Python stub từ proto/*.proto.

Chạy 1 lần (hoặc mỗi khi proto thay đổi):
    python -m pip install -r requirements.txt
    python gen_proto.py

Sẽ tạo 4 file trong thư mục hiện tại:
    judge_pb2.py        judge_pb2_grpc.py
    typed_judge_pb2.py  typed_judge_pb2_grpc.py
"""
import subprocess
import sys


def main() -> None:
    cmd = [
        sys.executable, "-m", "grpc_tools.protoc",
        "-I", "proto",
        "--python_out=.",
        "--grpc_python_out=.",
        "proto/judge.proto",
        "proto/typed_judge.proto",
    ]
    print(" ".join(cmd))
    subprocess.check_call(cmd)
    print("OK — stub sinh tại judge_pb2.py / typed_judge_pb2.py (+ *_grpc.py)")


if __name__ == "__main__":
    main()
