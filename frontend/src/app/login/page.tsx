// 로그인 페이지 (서버)

import { cookies } from "next/headers";
import ClientPage from "./ClientPage";

export default async function Page() {
  const cookieStore = await cookies();
  const lastLogin = cookieStore.get("lastLogin")?.value!!;

  return <ClientPage lastLogin={lastLogin} />;
}
