// 회원 리스트 페이지 (브라우저)

"use client";

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
          <li key={item.id} className="border-[2px] rounded-2xl p-2 my-3">
            <Link className="block" href={""}>
              <div>id : {item.id}</div>
              <div>가입일 : {item.createDate}</div>
              <div>아이디 (username) : {item.username}</div>
              <div>이메일 : {item.email}</div>
            </Link>
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
