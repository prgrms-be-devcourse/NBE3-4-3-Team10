"use client";

import React, { useState } from "react";
import { useParams } from "next/navigation";
import { useWebSocket } from "@/hooks/useWebSocket";

const ChatPage = () => {
    const params = useParams<Record<string, string>>();
    const calendarId = params?.calendarId;


    const { messages, sendMessage } = useWebSocket(calendarId);

    const [newMessage, setNewMessage] = useState("");

    const handleSend = () => {
        if (newMessage.trim()) {
            sendMessage(newMessage);
            setNewMessage("");
        }
    };

    if (!calendarId) {
        return <div className="p-4">📛 캘린더 ID가 필요합니다.</div>;
    }

    return (
        <div className="h-full flex flex-col">
            <div className="flex-1 overflow-y-auto p-4 bg-gray-100">
                {messages.map((msg, index) => (
                    <div key={index} className="p-2 bg-white my-2 rounded">
                        <strong>{msg.sender}</strong>: {msg.content}
                    </div>
                ))}
            </div>
            <div className="p-4 border-t">
                <input
                    value={newMessage}
                    onChange={(e) => setNewMessage(e.target.value)}
                    className="w-full p-2 border rounded"
                />
                <button
                    onClick={handleSend}
                    className="mt-2 w-full bg-blue-600 text-white py-2"
                >
                    전송
                </button>
            </div>
        </div>
    );
};

export default ChatPage;
