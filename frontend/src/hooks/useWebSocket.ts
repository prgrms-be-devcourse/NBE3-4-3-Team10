import { useEffect, useState, useRef } from "react";
import client from "@/lib/backend/client";

interface Message {
    sender: string;
    content: string;
}

export const useWebSocket = (calendarId: string | undefined) => {
    const [messages, setMessages] = useState<Message[]>([]);
    const [wsToken, setWsToken] = useState<string | null>(null);
    const wsRef = useRef<WebSocket | null>(null);
    const backendHost = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

    // ✅ Step 1: wsToken 요청 (최초 1회 실행)
    useEffect(() => {
        if (!calendarId) {
            console.warn("📛 calendarId가 없습니다. 웹소켓 연결을 스킵합니다.");
            return;
        }

        const fetchWsToken = async () => {
            try {
                const response = await client.POST("/api/ws-token");
                console.log("✅ wsToken 발급 성공:", response.data?.wsToken);
                setWsToken(response.data?.wsToken ?? null);
            } catch (error) {
                console.error("📛 wsToken 발급 실패", error);
                setWsToken(null);
            }
        };

        fetchWsToken();
    }, [calendarId]);

    // ✅ Step 2: WebSocket 연결
    useEffect(() => {
        if (!calendarId || !wsToken || wsRef.current) return;

        const wsUrl = `${backendHost.replace("http", "ws")}/api/calendars/${calendarId}/chat?wsToken=${wsToken}`;
        console.log("🕵️‍♂️ WebSocket 연결 시도:", wsUrl);
        const ws = new WebSocket(wsUrl);
        wsRef.current = ws; // WebSocket 객체 저장

        ws.onopen = () => {
            console.log(`✅ WebSocket 연결 성공 (calendarId=${calendarId})`);
        };

        ws.onmessage = (event) => {
            try {
                const data = JSON.parse(event.data);
                console.log("📩 WebSocket 메시지 수신:", data);

                if (data.type === "CHAT") {
                    setMessages((prev) => [...prev, data]);
                } else if (data.type === "ERROR") {
                    console.error("📛 서버 오류 메시지 수신:", data.message);
                    alert(data.message);
                }
            } catch (error) {
                console.error("📛 메시지 파싱 실패", error);
            }
        };

        ws.onerror = (error) => {
            console.error("❌ WebSocket 에러", error);
        };

        ws.onclose = async (event) => {
            console.log(`🚪 WebSocket 연결 종료 (code=${event.code}, reason=${event.reason})`);
            wsRef.current = null; // WebSocket 객체 초기화

            // WebSocket 재연결 로직
            console.log("🔄 새로운 wsToken 요청 중...");
            try {
                const response = await client.POST("/api/ws-token");
                if (response.data?.wsToken) {
                    console.log("✅ 새로운 wsToken 발급 성공:", response.data.wsToken);
                    setWsToken(response.data.wsToken);
                }
            } catch (error) {
                console.error("📛 wsToken 갱신 실패", error);
                setWsToken(null);
            }
        };

        // ✅ Cleanup: 컴포넌트 언마운트 시 WebSocket 닫기
        return () => {
            if (wsRef.current) {
                console.log("🔌 WebSocket 연결 해제");
                wsRef.current.close();
                wsRef.current = null;
            }
        };
    }, [wsToken]);

    // ✅ Step 3: WebSocket 메시지 전송
    const sendMessage = (content: string) => {
        if (!calendarId || !wsRef.current || wsRef.current.readyState !== WebSocket.OPEN) {
            alert("📛 WebSocket이 열려있지 않습니다. 다시 접속해주세요.");
            return;
        }

        const message = {
            calendarId: Number(calendarId),
            message: content
        };

        wsRef.current.send(JSON.stringify(message));
    };

    return { messages, sendMessage };
};
