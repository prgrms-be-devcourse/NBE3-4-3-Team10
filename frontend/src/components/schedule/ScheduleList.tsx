import { Schedule } from '@/types/schedule/schedule';
import { useRouter } from 'next/navigation';

interface ScheduleListProps {
    schedules: Schedule[];
    onEdit: (schedule: Schedule) => void;
    onDelete: (scheduleId: number) => void;
    onView: (scheduleId: number) => void; // 위치 버튼 기능 유지
}

export default function ScheduleList({ schedules, onEdit, onDelete, onView }: ScheduleListProps) {
    const router = useRouter();

    return (
        <div className="bg-white p-4 rounded-lg shadow">
            <h2 className="text-2xl font-bold mb-4">일정 목록</h2>
            {schedules.length === 0 ? (
                <p className="text-center text-gray-500">현재 일정이 없습니다.</p>
            ) : (
                <ul className="space-y-4">
                    {schedules.map(schedule => (
                        <li
                            key={schedule.id}
                            className="p-4 bg-gray-100 rounded-lg shadow hover:bg-gray-200 transition cursor-pointer"
                        >
                            <h3
                                className="font-bold text-lg mb-2 cursor-pointer"
                            >
                                📅 {schedule.title}
                            </h3>
                            <p className="text-sm text-gray-600 mb-1">⏱️ 시작: {new Date(schedule.startTime).toLocaleString()}</p>
                            <p className="text-sm text-gray-600 mb-1">📍 주소: {schedule.location.address}</p>

                            <div className="flex justify-end space-x-3 mt-3">
                                <button
                                    onClick={() => router.push(`/calendars/${schedule.calendarId}/schedules/${schedule.id}`)}
                                    className="px-3 py-1 bg-black text-white font-semibold rounded transition duration-200 hover:bg-gray-700"
                                >
                                    상세 보기
                                </button>
                                <button
                                    onClick={() => onView(schedule.id)}
                                    className="px-3 py-1 bg-black text-white font-semibold rounded transition duration-200 hover:bg-gray-700"
                                >
                                    위치
                                </button>
                                <button
                                    onClick={() => onEdit(schedule)}
                                    className="px-3 py-1 bg-black text-white font-semibold rounded transition duration-200 hover:bg-gray-700"
                                >
                                    수정
                                </button>
                                <button
                                    onClick={() => onDelete(schedule.id)}
                                    className="px-3 py-1 bg-red-500 text-white font-semibold rounded transition duration-200 hover:bg-red-600"
                                >
                                    삭제
                                </button>
                            </div>
                        </li>
                    ))}
                </ul>
            )}
        </div>
    );
}
