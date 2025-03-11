import { useEffect, useState, useRef } from "react";
import client from "@/lib/backend/client";

interface Message {
    senderId: number;
    calendarId: number;
    message: string;
    sentAt?: string;
}

export const useWebSocket = (calendarId: string, userId: number) => {
    const [messages, setMessages] = useState<Message[]>([]);
    const [wsToken, setWsToken] = useState<string | null>(null);
    const wsRef = useRef<WebSocket | null>(null);
    const backendHost = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

    // 1️⃣ wsToken 받아오기
    useEffect(() => {
        if (!calendarId || !userId) return;

        const fetchWsToken = async () => {
            try {
                const response = await client.POST("/api/ws-token");
                setWsToken(response.data?.wsToken ?? null);
            } catch (error) {
                console.error("📛 wsToken 발급 실패", error);
                setWsToken(null);
            }
        };

        fetchWsToken();
    }, [calendarId, userId]);

    // 2️⃣ WebSocket 연결
    useEffect(() => {
        if (!calendarId || !wsToken || wsRef.current) return;

        const wsUrl = `${backendHost.replace("http", "ws")}/api/calendars/${calendarId}/chat?wsToken=${wsToken}`;
        const ws = new WebSocket(wsUrl);
        wsRef.current = ws;

        ws.onopen = () => {
            console.log("✅ WebSocket 연결됨");
        };

        ws.onmessage = (event) => {
            const data = JSON.parse(event.data);
            setMessages((prev) => [...prev, data]);
        };

        ws.onclose = (event) => {
            console.log("🔌 WebSocket 연결 종료", event.reason);
            wsRef.current = null;
        };

        ws.onerror = (err) => {
            console.error("📛 WebSocket 에러", err);
        };

        return () => {
            ws.close();
            wsRef.current = null;
        };
    }, [wsToken, calendarId]);

    // 3️⃣ 메시지 전송 함수
    const sendMessage = (content: string) => {
        if (!calendarId || !userId || !wsRef.current || wsRef.current.readyState !== WebSocket.OPEN) {
            alert("📛 WebSocket 연결이 안 되어 있습니다.");
            return;
        }

        const message = {
            senderId: userId,
            calendarId: Number(calendarId),
            message: content,
            sentAt: new Date().toISOString()
        };

        setMessages((prev) => [...prev, message]); // 즉시 화면에 반영
        wsRef.current.send(JSON.stringify(message));
    };

    return { messages, sendMessage };
};
