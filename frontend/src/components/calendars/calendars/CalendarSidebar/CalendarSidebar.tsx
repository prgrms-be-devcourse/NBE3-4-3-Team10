import React, { useEffect, useState } from "react";
import {
    CalendarIcon,
    PlusIcon,
    PencilIcon,
    TrashIcon,
    ChevronDownIcon,
    UsersIcon,
    ShareIcon
} from "@heroicons/react/24/outline";
import type { Calendar } from "@/lib/calendars/types/calendarTypes";
import { useCalendar } from "@/lib/calendars/hooks/useCalendar";
import { calendarApi } from "@/lib/calendars/api/calendarApi";
import "./CalendarSidebar.css";

interface CalendarSidebarProps {
    onCreateClick: () => void;
    onUpdateClick: () => void;
    onDeleteClick: () => void;
    onViewClick: () => void;
    selectedCalendar: Calendar | null;
    userId: number;
    onCalendarSelect?: (calendar: Calendar) => void;
}

export const CalendarSidebar: React.FC<CalendarSidebarProps> = ({
                                                                    onCreateClick,
                                                                    onUpdateClick,
                                                                    onDeleteClick,
                                                                    selectedCalendar,
                                                                    userId,
                                                                    onCalendarSelect,
                                                                }) => {
    const { calendars, sharedCalendars, fetchSharedCalendars } = useCalendar();
    const [calendarSharedWith, setCalendarSharedWith] = useState<{ [key: number]: string[] }>({}); // ✅ 공유된 사용자 정보 저장 (추가됨)

    // ✅ 공유된 캘린더 불러오기
    useEffect(() => {
        if (userId) {
            fetchSharedCalendars(userId)
                .then((sharedCals) => {
                    if (!sharedCals || !Array.isArray(sharedCals)) {
                        console.error("📛 공유된 캘린더 데이터 형식 오류:", sharedCals);
                        return;
                    }

                    const sharedUsersMap: { [key: number]: string[] } = {};
                    sharedCals.forEach((calendar) => {
                        sharedUsersMap[calendar.id] = calendar.sharedWith || []; // ✅ `sharedWith`이 undefined일 경우 빈 배열 할당
                    });

                    setCalendarSharedWith(sharedUsersMap);
                })
                .catch((error) => {
                    console.error("📛 공유된 캘린더 불러오기 실패:", error);
                });
        }
    }, [userId]);


    // ✅ 캘린더 공유 함수
    const handleShareCalendar = async (calendarId: number, friendId: number) => {
        try {
            await calendarApi.shareCalendar(calendarId, friendId);
            alert("✅ 캘린더 공유 성공!");

            // ✅ 공유 후, 즉시 업데이트 (추가됨)
            fetchSharedCalendars(userId).then((sharedCals) => {
                if (sharedCals) {
                    const sharedUsersMap: { [key: number]: string[] } = {};
                    sharedCals.forEach((calendar) => {
                        sharedUsersMap[calendar.id] = calendar.sharedWith || [];
                    });
                    setCalendarSharedWith(sharedUsersMap);
                }
            });
        } catch (error) {
            console.error("📛 공유된 캘린더 불러오기 실패:", error);
            alert("❌ 캘린더 공유에 실패했습니다!");
        }
    };

    return (
        <div className="calendar-sidebar">
            {/* 새 캘린더 생성 버튼 */}
            <button onClick={onCreateClick} className="create-button">
                <PlusIcon className="w-5 h-5" />
                <span>NEW CALENDAR</span>
            </button>

            {/* 내 캘린더 목록 */}
            <div className="calendar-list">
                <div className="calendar-list-header">
                    <span className="calendar-list-title">내 캘린더</span>
                    <ChevronDownIcon className="w-4 h-4" />
                </div>

                {calendars.length > 0 ? (
                    <div className="calendar-items">
                        {calendars.map((calendar) => (
                            <div
                                key={calendar.id}
                                onClick={() => onCalendarSelect?.(calendar)}
                                className={`calendar-item ${selectedCalendar?.id === calendar.id ? "selected" : ""}`}
                            >
                                <CalendarIcon className="w-4 h-4" />
                                <span className="calendar-item-name">{calendar.name}</span>

                                {/* ✅ 공유 버튼 */}
                                <button
                                    onClick={(e) => {
                                        e.stopPropagation();
                                        handleShareCalendar(calendar.id, userId);
                                    }}
                                    className="share-btn"
                                >
                                    <ShareIcon className="w-4 h-4 text-gray-700" />
                                </button>

                                {selectedCalendar?.id === calendar.id && (
                                    <div className="calendar-actions">
                                        <button
                                            onClick={(e) => {
                                                e.stopPropagation();
                                                onUpdateClick();
                                            }}
                                            className="action-button"
                                        >
                                            <PencilIcon className="w-4 h-4" />
                                        </button>
                                        <button
                                            onClick={(e) => {
                                                e.stopPropagation();
                                                onDeleteClick();
                                            }}
                                            className="action-button"
                                        >
                                            <TrashIcon className="w-4 h-4" />
                                        </button>
                                    </div>
                                )}
                            </div>
                        ))}
                    </div>
                ) : (
                    <div className="p-3 text-gray-500 text-sm">캘린더가 없습니다.</div>
                )}
            </div>

            {/* 공유된 캘린더 목록 */}
            <div className="calendar-list mt-4">
                <div className="calendar-list-header">
                    <span className="calendar-list-title">공유된 캘린더</span>
                    <UsersIcon className="w-5 h-5 text-blue-500" />
                </div>

                {sharedCalendars.length > 0 ? (
                    <div className="calendar-items">
                        {sharedCalendars.map((calendar) => (
                            <div
                                key={calendar.id}
                                onClick={() => onCalendarSelect?.(calendar)}
                                className={`calendar-item shared ${selectedCalendar?.id === calendar.id ? "selected" : ""}`}
                            >
                                <CalendarIcon className="w-4 h-4 text-blue-500" />
                                <span className="calendar-item-name">{calendar.name} (공유)</span>

                                {/* ✅ 공유된 사용자 목록 표시 (추가됨) */}
                                {calendarSharedWith[calendar.id]?.length > 0 && (
                                    <div className="shared-users">
                                        공유된 사용자: {calendarSharedWith[calendar.id].join(", ")}
                                    </div>
                                )}
                            </div>
                        ))}
                    </div>
                ) : (
                    <div className="p-3 text-gray-500 text-sm">공유된 캘린더가 없습니다.</div>
                )}
            </div>
        </div>
    );
};