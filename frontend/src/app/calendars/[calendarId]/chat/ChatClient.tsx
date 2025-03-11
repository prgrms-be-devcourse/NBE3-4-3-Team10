"use client";

import React, { useState } from "react";
import { useWebSocket } from "@/hooks/useWebSocket";

interface ChatClientProps {
    calendarId: string;
    userId: number;
}

const ChatClient = ({ calendarId, userId }: ChatClientProps) => {
    const { messages, sendMessage } = useWebSocket(calendarId, userId);
    const [newMessage, setNewMessage] = useState("");

    const handleSend = () => {
        if (newMessage.trim()) {
            sendMessage(newMessage);
            setNewMessage("");
        }
    };

    return (
        <div className="h-full flex flex-col">
            <div className="flex-1 overflow-y-auto p-4 bg-gray-100">
                {messages.map((msg, index) => (
                    <div key={index} className="p-2 bg-white my-2 rounded">
                        <strong>{msg.senderId === userId ? "나" : `사용자 ${msg.senderId}`}</strong>: {msg.message}
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

export default ChatClient;
