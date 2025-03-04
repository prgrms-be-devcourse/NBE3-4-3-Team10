"use client";

import { Button } from "@/components/schedule/ui/button";
import client from "@/lib/backend/client";
import { components } from "@/lib/backend/schema";

export default function ClientPage({
  me,
}: Readonly<{
  me: components["schemas"]["UserDto"];
}>) {
  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const form = e.target as HTMLFormElement;

    // 프론트 측 입력값 검사
    if (form.nickname.value.length === 0) {
      alert("새로운 닉네임을 입력해주세요.");
      form.nickname.focus();

      return;
    }

    const response = await client.POST("/api/user", {
      body: {
        nickname: form.nickname.value,
      },
    });

    if (response.error) {
      alert(response.error.msg);
      return;
    }

    alert(response.data.msg);

    window.location.replace("/me");
  };

  return (
    <div className="flex flex-col gap-2">
      <div>내 정보 수정</div>

      <hr />

      <div className="mb-2">현재 nickname : {me.nickname}</div>

      <form onSubmit={handleSubmit}>
        <div>
          <label className="mr-2">nickname</label>
          <input
            type="text"
            name="nickname"
            className="p-2 border-[2px] rounded-lg"
            placeholder="new nickname"
          />
        </div>

        <Button variant="outline" asChild className="mt-2 hover:bg-gray-200">
          <input type="submit" value="수정" />
        </Button>
      </form>
    </div>
  );
}
