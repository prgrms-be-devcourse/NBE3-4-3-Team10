"use client";

import { useState, useEffect } from "react";
import { useRouter, useSearchParams } from "next/navigation";
import client from "@/lib/backend/client";

export default function ChangePasswordPage() {
  const [password, setPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const searchParams = useSearchParams();
  const router = useRouter();
  const username = searchParams!!.get("username");

  useEffect(() => {
    if (!username) {
      alert("잘못된 접근입니다.");
      router.push("/login/adminLogin");
    }
  }, [username, router]);

  const handlePasswordChange = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    if (!password || !confirmPassword) {
      alert("비밀번호와 비밀번호 확인을 입력해주세요.");
      return;
    }

    if (password !== confirmPassword) {
      alert("비밀번호가 서로 일치하지 않습니다.");
      return;
    }

    try {
      const response = await client.PUT(`/api/admin/{username}/password`, {
        body: { password },
        params: { path: { username: username!! } },
      });

      if (response.error) {
        alert(response.error.msg);
      } else {
        alert("비밀번호가 성공적으로 변경되었습니다.");
      }
      router.push("/login/adminLogin");
    } catch (error) {
      alert("비밀번호 변경 실패: 다시 시도해주세요.");
    }
  };

  return (
    <>
      <h1 className="text-2xl font-bold">비밀번호 변경</h1>
      <form onSubmit={handlePasswordChange}>
        <div>
          <label>새 비밀번호</label>
          <input
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            className="p-2"
            placeholder="새 비밀번호"
          />
        </div>
        <div>
          <label>비밀번호 확인</label>
          <input
            type="password"
            value={confirmPassword}
            onChange={(e) => setConfirmPassword(e.target.value)}
            className="p-2"
            placeholder="비밀번호 확인"
          />
        </div>
        <div>
          <input type="submit" value="비밀번호 변경" />
        </div>
      </form>
    </>
  );
}
