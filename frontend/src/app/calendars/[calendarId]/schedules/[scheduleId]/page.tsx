'use client';

import { useState, useEffect } from 'react';
import { useParams, useRouter } from 'next/navigation';
import ScheduleForm from '@/components/schedule/ScheduleForm';
import { Schedule, ScheduleFormData } from '@/types/schedule/schedule';
import { scheduleApi } from '@/lib/schedule/api/scheduleApi';

export default function ScheduleDetailPage() {
    const params = useParams();
    const router = useRouter();

    const calendarId = params?.calendarId ? Number(params.calendarId) : null;
    const scheduleId = params?.scheduleId ? Number(params.scheduleId) : null;

    const [schedule, setSchedule] = useState<Schedule | null>(null);
    const [isEditFormVisible, setIsEditFormVisible] = useState(false);

    useEffect(() => {
        if (calendarId === null || scheduleId === null) return;

        const fetchSchedule = async () => {
            try {
                const data = await scheduleApi.getScheduleById(calendarId, scheduleId);
                setSchedule(data);
            } catch (error) {
                console.error("📛 일정 데이터를 가져오는 중 오류 발생:", error);
            }
        };

        fetchSchedule();
    }, [calendarId, scheduleId]);

    const handleUpdateSchedule = async (formData: ScheduleFormData) => {
        if (calendarId === null || scheduleId === null) return;

        try {
            const updatedSchedule = await scheduleApi.updateSchedule(calendarId, scheduleId, formData);
            setSchedule(updatedSchedule);
            setIsEditFormVisible(false);
        } catch (error) {
            console.error("📛 일정 업데이트 실패:", error);
        }
    };

    if (calendarId === null || scheduleId === null) {
        return <div className="text-center mt-20 text-xl font-bold">잘못된 접근입니다.</div>;
    }

    if (!schedule) {
        return <div className="text-center mt-20 text-xl font-bold">Loading...</div>;
    }

    return (
        <div className="max-w-4xl mx-auto p-8 bg-white text-black rounded-lg shadow-lg mt-10">
            <h1 className="text-3xl font-bold mb-6">📆 {schedule.title}</h1>
            <div className="mb-4"><strong>⏱️ 시작:</strong> {new Date(schedule.startTime).toLocaleString()}</div>
            <div className="mb-4"><strong>⏱️ 종료:</strong> {new Date(schedule.endTime).toLocaleString()}</div>
            <div className="mb-4"><strong>📍 위치:</strong> {schedule.location.address}</div>
            <div className="mb-4"><strong>📝 설명:</strong> {schedule.description}</div>

            <div className="flex justify-center mt-8 space-x-4">
                <button onClick={() => router.push(`/calendars/${calendarId}/schedules`)} className="px-6 py-2 bg-black text-white rounded-lg">
                    목록
                </button>
                <button onClick={() => setIsEditFormVisible(true)} className="px-6 py-2 bg-black text-white rounded-lg">
                    수정
                </button>
            </div>

            {isEditFormVisible && (
                <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50">
                    <div className="bg-white p-6 rounded-lg shadow-lg max-w-xl w-full">
                        <ScheduleForm
                            initialData={schedule}
                            onSubmit={handleUpdateSchedule}
                            onCancel={() => setIsEditFormVisible(false)}
                        />
                    </div>
                </div>
            )}
        </div>
    );
}