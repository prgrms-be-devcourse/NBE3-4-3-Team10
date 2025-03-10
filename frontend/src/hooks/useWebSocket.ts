import { useEffect, useState } from "react";
import Cookies from "js-cookie";
import client from "@/lib/backend/client";
interface Message {
    sender: string;
    content: string;
}

export const useWebSocket = (calendarId: string | undefined) => {
    const [messages, setMessages] = useState<Message[]>([]);
    const [socket, setSocket] = useState<WebSocket | null>(null);
    const backendHost = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";


    console.log("✅ 백엔드 호스트:", backendHost);
    useEffect(() => {

        if (!calendarId) {
            console.warn("📛 calendarId가 없습니다. 웹소켓 연결을 스킵합니다.");
            return;
        }

        const token = Cookies.get("accessToken");
        if (!token == undefined) {
            alert("로그인이 필요합니다.");
            return;
        }

        const wsUrl = `${backendHost.replace("http", "ws")}/api/calendars/${calendarId}/chat`;
        console.log("🕵️‍♂️ 연결 시도 WebSocket 주소:", wsUrl);
        const ws = new WebSocket(wsUrl);


        ws.onopen = () => {
            console.log(`✅ WebSocket 연결 성공 (calendarId=${calendarId})`);
            // 프론트에서 별도 AUTH 메시지를 보낼 필요 없음 (서버에서 쿠키 인증함)
        };

        ws.onmessage = (event) => {
            try {
                const data = JSON.parse(event.data);
                if (data.type === "CHAT") {
                    setMessages((prev) => [...prev, data]);
                } else if (data.type === "ERROR") {
                    console.error("📛 서버 오류 메시지 수신:", data.message);
                    alert(data.message); // 필요시 사용자에게 안내
                }
            } catch (error) {
                console.error("📛 메시지 파싱 실패", error);
            }
        };

        ws.onerror = (error) => {
            console.error("❌ WebSocket 에러", error);
        };

        ws.onclose = (event) => {
            console.log(`🚪 WebSocket 연결 종료 (code=${event.code}, reason=${event.reason})`);
        };

        setSocket(ws);

        return () => {
            console.log(`🚪 WebSocket 연결 해제 (calendarId=${calendarId})`);
            ws.close();
        };
    }, [calendarId]);

    const sendMessage = (content: string) => {
        if (!socket || socket.readyState !== WebSocket.OPEN) {
            alert("📛 WebSocket이 열려있지 않습니다. 다시 접속해주세요.");
            return;
        }

        const message = {
            calendarId: Number(calendarId),
            message: content
        };

        socket.send(JSON.stringify(message));
    };

    return { messages, sendMessage };
};
