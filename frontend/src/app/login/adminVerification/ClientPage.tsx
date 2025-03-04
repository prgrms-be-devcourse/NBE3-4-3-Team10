"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import client from "@/lib/backend/client";

export default function ClientPage() {
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [verificationCode, setVerificationCode] = useState("");
  const [isVerified, setIsVerified] = useState(false); // 검증 상태
  const [verificationSent, setVerificationSent] = useState(false); // 인증번호 발송 상태
  const [isSending, setIsSending] = useState(false); // 메일 전송 중 상태
  const router = useRouter();

  const handleSendVerification = async () => {
    if (!username || !email) {
      alert("아이디와 이메일을 입력해주세요.");
      return;
    }

    setIsSending(true); // 메일 전송 중 상태 설정

    try {
      // 서버에 인증번호 요청
      const response = await client.POST("/api/admin/verification-codes", {
        body: { username, email },
      });

      if (response.error) {
        alert(response.error.msg);
        setIsVerified(false); // 검증 실패 시 다시 입력 가능
      } else {
        alert("인증번호가 이메일로 발송되었습니다.");
        setVerificationSent(true); // 인증번호 발송 상태 업데이트
        setIsVerified(true); // 검증 성공 상태 설정
      }
    } catch (error) {
      alert("인증번호 발송 중 문제가 발생했습니다. 다시 시도해주세요.");
    } finally {
      setIsSending(false); // 메일 전송 완료 후 상태 해제
    }
  };

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (verificationCode.length === 0) {
      alert("인증번호를 입력해주세요.");
      return;
    }

    // 인증번호 검증 요청
    const response = await client.POST("/api/admin/verification-codes/verify", {
      body: { username, verificationCode },
    });

    if (response.error) {
      alert(response.error.msg);
    } else {
      alert("계정 인증이 완료되었습니다. 비밀번호를 변경해주세요.");
      router.push(`/login/adminChangePassword?username=${username}`);
    }
  };

  return (
    <>
      <h1 className="text-2xl font-bold">계정 인증</h1>
      <div>
        <label>아이디</label>
        <input
          type="text"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          className="p-2"
          placeholder="아이디"
          disabled={isVerified} // 검증이 완료되면 입력 비활성화
        />
      </div>
      <div>
        <label>이메일</label>
        <input
          type="email"
          value={email}
          onChange={(e) => setEmail(e.target.value)}
          className="p-2"
          placeholder="이메일"
          disabled={isVerified} // 검증이 완료되면 입력 비활성화
        />
      </div>
      <div>
        {!verificationSent ? (
          <button
            type="button"
            onClick={handleSendVerification}
            disabled={isSending}
          >
            {isSending ? "메일 전송 중..." : "인증번호 발송"}
          </button>
        ) : (
          <form onSubmit={handleSubmit}>
            <div>
              <label>인증번호</label>
              <input
                type="text"
                value={verificationCode}
                onChange={(e) => setVerificationCode(e.target.value)}
                className="p-2"
                placeholder="인증번호를 입력하세요"
              />
            </div>
            <div>
              <input type="submit" value="인증하기" />
            </div>
          </form>
        )}
      </div>
    </>
  );
}
