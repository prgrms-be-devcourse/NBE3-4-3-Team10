"use client";

import React, { useState, useEffect } from "react";
import "./RightSidebar.css";
import { useFriendship } from "@/lib/calendars/hooks/useFriendship";
import client from "@/lib/backend/client";

const fetchUserId = async () => {
    try {
        const { data } = await client.GET("/api/user/me");
        console.log("📌 [RightSidebar] /api/user/me 응답:", data);
        return data?.id || null;
    } catch (error) {
        console.error("📛 [RightSidebar] 사용자 정보 가져오기 실패:", error);
        return null;
    }
};

export const RightSidebar: React.FC = () => {
    const [userId, setUserId] = useState<number | null>(null);

    useEffect(() => {
        const getUserId = async () => {
            const id = await fetchUserId();
            console.log("📌 [RightSidebar] 가져온 userId:", id);
            setUserId(id);
        };
        getUserId();
    }, []);

    // userId가 설정되기 전에는 useFriendship을 실행하지 않음
    const { friends, loading, error, addFriend, removeFriend } = useFriendship(userId ?? 0);

    return (
        <div className="right-sidebar">
            <div className="sidebar-header">
                <h1 className="sidebar-title">COMMUNITY</h1>
            </div>

            <div className="section">
                <h2 className="section-header">FRIENDS</h2>
                <div className="section-content">
                    {loading ? (
                        <p>🔄 친구 목록을 불러오는 중...</p>
                    ) : error ? (
                        <p>❌ 오류 발생: {error.message}</p>
                    ) : friends.length === 0 ? (
                        <p>😢 친구가 없습니다.</p>
                    ) : (
                        <ul className="friends-list">
                            {friends.map((friend) => (
                                <li key={friend.id} className="friend-item">
                                    <div className="item-avatar">{friend.username.charAt(0)}</div>
                                    <span className="item-name">{friend.username}</span>
                                    <button onClick={() => removeFriend(friend.id)} className="remove-btn">❌</button>
                                </li>
                            ))}
                        </ul>
                    )}
                </div>
            </div>
        </div>
    );
};
