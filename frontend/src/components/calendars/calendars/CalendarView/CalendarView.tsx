"use client";
import React, { useEffect, useState } from "react";
import FullCalendar from "@fullcalendar/react";
import dayGridPlugin from "@fullcalendar/daygrid";
import interactionPlugin from "@fullcalendar/interaction";
import type { Calendar } from "@/lib/calendars/types/calendarTypes";
import { EventClickArg } from "@fullcalendar/core";
import { scheduleApi } from "@/lib/schedule/api/scheduleApi";
import dayjs from "dayjs";
import { useRouter } from "next/navigation";
import './CalendarView.css';
import {PlusIcon} from "@heroicons/react/24/outline";

interface CalendarViewProps {
    calendars: Calendar[];
    selectedCalendar: Calendar | null;
    onCalendarSelect: (calendar: Calendar) => void;
}

export const CalendarView: React.FC<CalendarViewProps> = ({
                                                              selectedCalendar,
                                                          }) => {
    const [events, setEvents] = useState<any[]>([]);
    const router = useRouter();

    useEffect(() => {
        if (!selectedCalendar) return;

        const fetchSchedules = async () => {
            try {
                const today = dayjs().format("YYYY-MM-DD");
                const fetchedSchedules = await scheduleApi.getMonthlySchedules(
                    selectedCalendar.id,
                    today
                );

                const formattedEvents = fetchedSchedules.map(schedule => ({
                    id: String(schedule.id),
                    title: schedule.title,
                    start: schedule.startTime,
                    end: schedule.endTime,
                    description: schedule.description,
                    allDay: false,
                }));

                formattedEvents.sort((a, b) => dayjs(b.start).valueOf() - dayjs(a.start).valueOf());

                setEvents(formattedEvents);
            } catch (error) {
                console.error("📛 일정 불러오기 실패:", error);
            }
        };

        fetchSchedules();
    }, [selectedCalendar]);

    const handleEventClick = (clickInfo: EventClickArg) => {
        if (selectedCalendar) {
            router.push(`/calendars/${selectedCalendar.id}/schedules/${clickInfo.event.id}`);
        } else {
            console.error("📛 선택된 캘린더가 없습니다.");
        }
    };

    if (!selectedCalendar) {
        return (
            <div className="empty-state">
                <p className="empty-state-title">새로운 캘린더를 만들어보세요!</p>
                <p className="empty-state-description">
                    좌측 메뉴에서 + NEW CALENDAR을 통해 새로운 캘린더를 만들어보세요!
                </p>
            </div>
        );
    }

    return (
        <div className="w-full h-full bg-white relative">
            <div className="h-full p-4">
                <div className="flex">
                    <div className="flex-grow"></div>
                    <button
                        onClick={() => {
                            if (selectedCalendar) {
                                router.push(`/calendars/${selectedCalendar.id}/schedules`);
                            } else {
                                console.error("📛 선택된 캘린더가 없습니다.");
                            }
                        }}
                        className="flex items-center gap-2 bg-white text-gray-700 py-3 px-3 transition-all hover:bg-[#f6fafe] hover:shadow-[0_1px_3px_0_rgba(60,64,67,0.302)]"
                    >
                        <PlusIcon className="w-5 h-5"/>
                        <span className="text-sm font-semibold tracking-wide">NEW SCHEDULE</span>
                    </button>
                </div>
                <FullCalendar
                    plugins={[dayGridPlugin, interactionPlugin]}
                    initialView="dayGridMonth"
                    headerToolbar={{
                        left: "prev,today,next",
                        center: "title",
                        right: "dayGridMonth,dayGridWeek",
                    }}
                    events={events}
                    eventClick={handleEventClick}
                    selectable={true}
                    handleWindowResize={true}
                    dayCellContent={(e) => e.dayNumberText}
                    height="100%"
                    aspectRatio={1.5}
                    contentHeight="auto"
                    dayMaxEventRows={true}
                    fixedWeekCount={false}
                    dayCellClassNames="min-h-[100px] p-2"
                    eventDisplay="block"
                    eventContent={(eventInfo) => (
                        <div
                            className="truncate font-bold text-sm cursor-pointer text-white hover:bg-blue-400 p-1 rounded">
                            {eventInfo.event.title}
                        </div>
                    )}
                    buttonText={{
                        today: 'TODAY',
                        month: 'M',
                        week: 'W',
                    }}
                    buttonIcons={{
                        prev: 'chevron-left',
                        next: 'chevron-right',
                    }}
                    views={{
                        dayGridMonth: {
                            titleFormat: {year: 'numeric', month: 'long'},
                            dayHeaderFormat: {weekday: 'short'},
                        },
                        dayGridWeek: {
                            titleFormat: {year: 'numeric', month: 'long'},
                        },
                    }}
                />
            </div>

        </div>
    );
};