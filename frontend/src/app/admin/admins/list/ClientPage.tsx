// 관리자 리스트 페이지 (브라우저)

"use client";

import { Button } from "@/components/schedule/ui/button";
import client from "@/lib/backend/client";
import { components } from "@/lib/backend/schema";
import Link from "next/link";
import { useRouter } from "next/navigation";

export default function ClientPage({
  searchKeyword,
  searchKeywordType,
  page,
  pageSize,
  itemPage,
}: {
  searchKeyword: string;
  searchKeywordType: string;
  page: number;
  pageSize: number;
  itemPage: components["schemas"]["PageDtoUserDto"];
}) {
  const router = useRouter();

  const handleUnlock = async (id: number) => {
    if (!confirm("관리자 잠금을 해제하겠습니까?")) return;

    const response = await client.PATCH(`/api/admin/{id}/unlock`, {
      params: { path: { id: id!! } },
    });

    if (response.error) {
      alert(response.error.msg);
      return;
    }

    alert("관리자 잠금이 해제되었습니다.");
    window.location.reload();
  };

  return (
    <div>
      <form
        className="flex gap-2"
        onSubmit={(e) => {
          e.preventDefault();
          const formData = new FormData(e.target as HTMLFormElement);
          const searchKeyword = formData.get("searchKeyword") as string;
          const searchKeywordType = formData.get("searchKeywordType") as string;
          const page = formData.get("page") as string;
          const pageSize = formData.get("pageSize") as string;
          router.push(
            `?page=${page}&pageSize=${pageSize}&searchKeywordType=${searchKeywordType}&searchKeyword=${searchKeyword}`
          );
        }}
      >
        <input type="hidden" name="page" value="1" />
        <select name="pageSize" defaultValue={pageSize}>
          <option disabled>페이당 행 수</option>
          <option value="10">10</option>
          <option value="30">30</option>
          <option value="50">50</option>
        </select>
        <select name="searchKeywordType" defaultValue={searchKeywordType}>
          <option disabled>검색어 타입</option>
          <option value="username">아이디</option>
          <option value="email">이메일</option>
        </select>
        <input
          className="border p-1 rounded-l"
          placeholder="검색어를 입력해주세요."
          type="text"
          name="searchKeyword"
          defaultValue={searchKeyword}
        />
        <button className="border p-1 px-3 rounded-sm" type="submit">
          검색
        </button>
      </form>
      <div className="flex flex-col gap-2 p-2">
        <div>currentPageNumber: {itemPage.currentPageNumber}</div>
        <div>pageSize: {itemPage.pageSize}</div>
        <div>totalPages: {itemPage.totalPages}</div>
        <div>totalItems: {itemPage.totalItems}</div>
      </div>
      <hr />
      <div className="flex my-2 gap-2 p-2">
        {Array.from({ length: itemPage.totalPages }, (_, i) => i + 1).map(
          (pageNum) => (
            <Link
              key={pageNum}
              className={`px-2 py-1 border rounded ${
                pageNum === itemPage.currentPageNumber ? "text-red-500" : ""
              }`}
              href={`?page=${pageNum}&pageSize=${pageSize}&searchKeywordType=${searchKeywordType}&searchKeyword=${searchKeyword}`}
            >
              {pageNum}
            </Link>
          )
        )}
      </div>
      <hr />
      <ul>
        {itemPage.items.map((item) => (
          <li key={item.id} className="border-2 rounded-2xl p-2 my-3">
            <div className="block flex gap-5 items-center h-10">
              {/* ID 부분 */}
              <div className="flex items-baseline">
                <span className="text-gray-500">id:</span>
                <span className="font-bold ml-1">{item.id}</span>
              </div>

              {/* 아이디 (username) 부분 */}
              <div className="flex items-baseline">
                <span className="text-gray-500">아이디 (username):</span>
                <span className="font-semibold ml-1">{item.username}</span>
              </div>

              {/* 이메일 부분 */}
              <div className="flex items-baseline">
                <span className="text-gray-500">이메일:</span>
                <span className="font-semibold ml-1">{item.email}</span>
              </div>

              {/* 공간 확보 */}
              <div className="flex-grow"></div>

              {/* 계정 잠김 표시 */}
              {item.locked && (
                <div className="text-red-500 font-semibold text-sm">
                  계정 잠김
                </div>
              )}

              {/* 잠금 해제 버튼 */}
              {item.locked && (
                <Button
                  variant="outline"
                  className="p-3 hover:bg-green-100 border-[1.5px] border-green-500 text-green-600 font-bold"
                  onClick={() => handleUnlock(item.id!!)}
                >
                  잠금 해제
                </Button>
              )}
            </div>
          </li>
        ))}
      </ul>
      <hr />
      <div className="flex my-2 gap-2 p-2">
        {Array.from({ length: itemPage.totalPages }, (_, i) => i + 1).map(
          (pageNum) => (
            <Link
              key={pageNum}
              className={`px-2 py-1 border rounded ${
                pageNum === itemPage.currentPageNumber ? "text-red-500" : ""
              }`}
              href={`?page=${pageNum}&pageSize=${pageSize}&searchKeywordType=${searchKeywordType}&searchKeyword=${searchKeyword}`}
            >
              {pageNum}
            </Link>
          )
        )}
      </div>
    </div>
  );
}
