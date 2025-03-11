import React, { useEffect } from 'react';
import {
    CalendarIcon,
    PlusIcon,
    PencilIcon,
    TrashIcon,
    ChevronDownIcon,
    UsersIcon
} from '@heroicons/react/24/outline';
import type { Calendar } from '@/lib/calendars/types/calendarTypes';
import { useCalendar } from '@/lib/calendars/hooks/useCalendar';
import './CalendarSidebar.css';

interface CalendarSidebarProps {
    onCreateClick: () => void;
    onUpdateClick: () => void;
    onDeleteClick: () => void;
    selectedCalendar: Calendar | null;
    userId: number; // ✅ 현재 로그인한 사용자 ID 추가
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

    // ✅ 사용자 ID 기반으로 공유된 캘린더 불러오기
    useEffect(() => {
        if (userId) {
            fetchSharedCalendars(userId);
        }
    }, [userId]);

    console.log('📌 내 캘린더:', calendars);
    console.log('📌 공유된 캘린더:', sharedCalendars);

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
                                className={`calendar-item ${
                                    selectedCalendar?.id === calendar.id ? 'selected' : ''
                                }`}
                            >
                                <CalendarIcon className="w-4 h-4" />
                                <span className="calendar-item-name">{calendar.name}</span>

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
                                className={`calendar-item shared ${
                                    selectedCalendar?.id === calendar.id ? 'selected' : ''
                                }`}
                            >
                                <CalendarIcon className="w-4 h-4 text-blue-500" />
                                <span className="calendar-item-name">{calendar.name} (공유)</span>
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
