"use client";

import React, { useState, useEffect, useCallback } from "react";
import "./RightSidebar.css";
import { useFriendship } from "@/lib/calendars/hooks/useFriendship";
import { calendarApi } from "@/lib/calendars/api/calendarApi";
import client from "@/lib/backend/client";
import { UserPlusIcon, ShareIcon } from "@heroicons/react/24/outline";
import axios from "axios";

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

// 📌 친구의 username으로 userId 가져오기 API
const fetchUserIdByUsername = async (username: string): Promise<number | null> => {
    try {
        const response = await axios.get(`http://localhost:8080/api/user/findByUsername`, {
            params: { username },
            withCredentials: true,
        });

        console.log("📌 [RightSidebar] 찾은 친구 ID:", response.data?.id);
        return response.data?.id || null;
    } catch (error: any) {
        if (error.response?.status === 404) {
            console.warn("🚨 해당 사용자를 찾을 수 없습니다!", username);
            return null;
        }
        console.error("📛 [RightSidebar] 친구 ID 가져오기 실패:", error);
        return null;
    }
};

export const RightSidebar: React.FC = () => {
    const [userId, setUserId] = useState<number | null>(null);
    const [friendUsername, setFriendUsername] = useState<string>("");
    const [sharedCalendars, setSharedCalendars] = useState<any[]>([]);
    const [selectedCalendarId, setSelectedCalendarId] = useState<number | null>(null);

    // ✅ 1️⃣ API 요청 함수 `useCallback`으로 분리
    const fetchSharedCalendars = useCallback(async (id: number) => {
        try {
            const sharedCals = await calendarApi.getSharedCalendars(id);
            if (!Array.isArray(sharedCals)) {
                console.warn("⚠️ 공유된 캘린더 응답이 배열이 아님:", sharedCals);
                setSharedCalendars([]);
            } else {
                setSharedCalendars(sharedCals);
            }
        } catch (error) {
            console.error("📛 공유된 캘린더 불러오기 실패:", error);
            setSharedCalendars([]);
        }
    }, []);

    useEffect(() => {
        const getUserId = async () => {
            const id = await fetchUserId();
            console.log("📌 [RightSidebar] 가져온 userId:", id);
            setUserId(id);
        };
        getUserId();
    }, []);

    // ✅ 2️⃣ `useEffect` 의존성 배열 수정 (userId 변경 시만 실행)
    useEffect(() => {
        if (userId) {
            fetchSharedCalendars(userId);
        }
    }, [userId, fetchSharedCalendars]);

    const { friends, loading, error, addFriend, removeFriend } = useFriendship(userId ?? 0);

    const handleAddFriend = async () => {
        if (!friendUsername.trim()) {
            alert("❌ 친구의 사용자명을 입력해주세요!");
            return;
        }

        try {
            const friendId = await fetchUserIdByUsername(friendUsername);
            if (!friendId) {
                alert("❌ 해당 사용자를 찾을 수 없습니다.");
                return;
            }

            if (friendId === userId) {
                alert("❌ 본인을 친구로 추가할 수 없습니다!");
                return;
            }

            if (userId === null) {
                alert("❌ 현재 사용자 정보를 가져올 수 없습니다.");
                return;
            }

            await addFriend(userId, friendId);
            alert("✅ 친구 추가 성공!");
            setFriendUsername("");
        } catch (error) {
            console.error("📛 친구 추가 중 오류 발생:", error);
            alert("❌ 친구 추가에 실패했습니다!");
        }
    };

    const handleShareCalendar = async (friendId: number) => {
        if (!selectedCalendarId) {
            alert("❌ 공유할 캘린더를 선택해주세요!");
            return;
        }

        try {
            await calendarApi.shareCalendar(selectedCalendarId, friendId);
            alert("✅ 캘린더 공유 성공!");
        } catch (error) {
            console.error("📛 캘린더 공유 중 오류 발생:", error);
            alert("❌ 캘린더 공유에 실패했습니다!");
        }
    };

    return (
        <div className="right-sidebar">
            <div className="sidebar-header">
                <h1 className="sidebar-title">COMMUNITY</h1>
            </div>

            {/* FRIENDS 섹션 */}
            <div className="section">
                <div className="section-header-container">
                    <h2 className="section-header">FRIENDS</h2>

                    <button onClick={handleAddFriend} className="add-friend-button">
                        <UserPlusIcon className="w-5 h-5 text-gray-700" />
                    </button>
                </div>

                <input
                    type="text"
                    className="friend-input"
                    placeholder="친구 이름 입력"
                    value={friendUsername}
                    onChange={(e) => setFriendUsername(e.target.value)}
                />

                <div className="section-content">
                    {loading ? <p>🔄 친구 목록을 불러오는 중...</p> :
                        error ? <p>❌ 오류 발생: {error.message}</p> :
                            friends.length === 0 ? <p>😢 친구가 없습니다.</p> : (
                                <ul className="friends-list">
                                    {friends.map((friend) => (
                                        <li key={friend.id} className="friend-item">
                                            <div className="item-avatar">{friend.username.charAt(0)}</div>
                                            <span className="item-name">{friend.username}</span>

                                            <button onClick={() => removeFriend(userId!, friend.id)} className="remove-btn">
                                                ❌
                                            </button>

                                            <button onClick={() => handleShareCalendar(friend.id)} className="share-btn">
                                                <ShareIcon className="w-5 h-5 text-gray-700" />
                                            </button>
                                        </li>
                                    ))}
                                </ul>
                            )}
                </div>
            </div>

            {/* ✅ 3️⃣ 공유된 캘린더 목록 */}
            <div className="section">
                <h2 className="section-header">SHARED CALENDARS</h2>
                {sharedCalendars.length === 0 ? (
                    <p>📭 공유된 캘린더가 없습니다.</p>
                ) : (
                    <ul className="shared-calendars-list">
                        {sharedCalendars.map((calendar) => (
                            <li
                                key={calendar.id}
                                className={`calendar-item ${selectedCalendarId === calendar.id ? "selected" : ""}`}
                                onClick={() => selectedCalendarId !== calendar.id && setSelectedCalendarId(calendar.id)}
                            >
                                {calendar.name}
                            </li>
                        ))}
                    </ul>
                )}
            </div>
        </div>
    );
};
