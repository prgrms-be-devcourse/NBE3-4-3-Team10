"use client";

import { Button } from "@/components/schedule/ui/button";
import client from "@/lib/backend/client";
import { components } from "@/lib/backend/schema";
import Link from "next/link";

export default function ClientPage({
  me,
}: Readonly<{
  me: components["schemas"]["UserDto"];
}>) {
  const handleDelete = async () => {
    if (
      !confirm(
        `⚠️ 정말로 탈퇴하시겠습니까? ⚠️\n` +
          `━━━━━━━━━━━━━━━━━\n` +
          `☑︎ 탈퇴 시 모든 개인정보가 삭제됩니다.\n` +
          `☑︎ 삭제된 데이터는 복구할 수 없습니다.\n` +
          `☑︎ 생성한 캘린더와 스케줄은 사라지지 않습니다.\n` +
          `━━━━━━━━━━━━━━━━━`
      )
    ) {
      return;
    }

    const response = await client.DELETE("/api/user/{id}", {
      params: { path: { id: me.id!! } },
    });

    if (response.error) {
      alert(response.error.msg);
      return;
    }

    alert("회원 탈퇴가 완료되었습니다.");
    window.location.replace("/");
  };

  return (
    <div className="flex flex-col gap-2">
      <div className="mt-2">nickname : {me.nickname}</div>
      {/* ( 이메일, 가입일? 필요하면?? ) */}

      <hr className="mt-5" />

      <div className="mt-2">
        <Button variant="outline" asChild className="hover:bg-gray-200">
          <Link href="/me/modify">내정보 수정</Link>
        </Button>
      </div>

      <div className="h-5"></div>

      <div className="">
        <Button
          variant="outline"
          className="hover:bg-red-200 text-red-600"
          onClick={() => handleDelete()}
        >
          회원 탈퇴
        </Button>
      </div>
    </div>
  );
}
