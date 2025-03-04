// 회원 리스트 페이지 (서버)

import client from "@/lib/backend/client";
import ClientPage from "./ClientPage";
import { cookies } from "next/headers";

export default async function Page({
  searchParams,
}: {
  searchParams: {
    searchKeywordType?: string;
    searchKeyword?: string;
    pageSize?: number;
    page?: number;
  };
}) {
  const {
    searchKeyword = "",
    searchKeywordType = "username",
    pageSize = 10,
    page = 1,
  } = await searchParams;
  const response = await client.GET("/api/admin/users", {
    params: {
      query: {
        searchKeyword,
        searchKeywordType,
        pageSize,
        page,
      },
    },
    headers: {
      cookie: (await cookies()).toString(),
    },
  });
  if (response.response.status === 403) {
    return (
      <div className="flex justify-center items-center min-h-screen">
        <div className="bg-gray-30 p-6 rounded-lg shadow-lg max-w-lg w-full text-center">
          <h2 className="text-3xl font-bold text-red-600 mb-4">
            !!! 권한이 없습니다. !!!
          </h2>
          <hr className="border-t-2 border-red-600 mb-4" />
          <p className="text-xl text-gray-700">
            이 페이지에 접근할 수 있는 권한이 없습니다.
          </p>
          <p className="mt-4 text-gray-500">관리자에게 문의하세요.</p>
        </div>
      </div>
    );
  }
  const itemPage = response.data?.data;
  return (
    <>
      <ClientPage
        searchKeyword={searchKeyword}
        searchKeywordType={searchKeywordType}
        page={page}
        pageSize={pageSize}
        itemPage={itemPage!!}
      />
    </>
  );
}
