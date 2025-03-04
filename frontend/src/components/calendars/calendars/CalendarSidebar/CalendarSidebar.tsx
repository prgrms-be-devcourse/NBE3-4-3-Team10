import React from 'react';
import {
    CalendarIcon,
    PlusIcon,
    PencilIcon,
    TrashIcon,
    ChevronDownIcon,
} from '@heroicons/react/24/outline';
import type { Calendar } from '@/lib/calendars/types/calendarTypes';
import './CalendarSidebar.css';

interface CalendarSidebarProps {
    onCreateClick: () => void;
    onUpdateClick: () => void;
    onDeleteClick: () => void;
    onViewClick: () => void;
    selectedCalendar: Calendar | null;
    calendars?: Calendar[];
    onCalendarSelect?: (calendar: Calendar) => void;
}

export const CalendarSidebar: React.FC<CalendarSidebarProps> = ({
                                                                    onCreateClick,
                                                                    onUpdateClick,
                                                                    onDeleteClick,
                                                                    selectedCalendar,
                                                                    calendars = [],
                                                                    onCalendarSelect,
                                                                }) => {
    const calendarItems = Array.isArray(calendars) ? calendars : [];

    console.log('Calendar items:', calendarItems); // 데이터 확인용

    return (
        <div className="calendar-sidebar">
            <button
                onClick={onCreateClick}
                className="create-button"
            >
                <PlusIcon className="w-5 h-5" />
                <span>NEW CALENDAR</span>
            </button>

            <div className="calendar-list">
                <div className="calendar-list-header">
                    <span className="calendar-list-title">내 캘린더</span>
                    <ChevronDownIcon className="w-4 h-4" />
                </div>

                {calendarItems.length > 0 ? (
                    <div className="calendar-items">
                        {calendarItems.map((calendar) => (
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
                    <div className="p-3 text-gray-500 text-sm">
                        캘린더가 없습니다.
                    </div>
                )}
            </div>
        </div>
    );
};