import { cookies } from "next/headers";
import { parseAccessToken } from "@/lib/auth/token";
import ChatClient from "./ChatClient";

interface ChatPageProps {
    params: { calendarId: string };
}

export default async function ChatPage({ params }: ChatPageProps) {
    const calendarId = String(params.calendarId);
    const cookieStore = await cookies();
    const accessToken = cookieStore.get("accessToken")?.value;

    if (!accessToken) {
        return <div className="p-4">로그인이 필요합니다.</div>;
    }

    const userData = parseAccessToken(accessToken);
    const userId = userData?.accessTokenPayload?.id;

    if (!userId) {
        return <div className="p-4">사용자 정보를 가져올 수 없습니다.</div>;
    }

    return <ChatClient calendarId={calendarId} userId={userId} />;
}
