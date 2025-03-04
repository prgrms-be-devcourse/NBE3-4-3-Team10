import { Schedule } from '@/types/schedule/schedule';

interface ScheduleListProps {
    schedules: Schedule[];
    onEdit: (schedule: Schedule) => void;
    onDelete: (scheduleId: number) => void;
}

export default function ScheduleList({
                                         schedules,
                                         onEdit,
                                         onDelete
                                     }: ScheduleListProps) {
    const formatDateTime = (dateTime: string) => {
        return new Date(dateTime).toLocaleString();
    };

    return (
        <div className="bg-white text-black p-4">
            <h2 className="text-xl font-bold mb-4">일정 목록</h2>
            {schedules.length === 0 ? (
                <p>일정이 없습니다.</p>
            ) : (
                <ul className="space-y-4">
                    {schedules.map(schedule => (
                        <li
                            key={schedule.id}
                            className="border border-gray-200 p-3 rounded-md"
                        >
                            <h3 className="font-bold text-lg">{schedule.title}</h3>
                            <p className="text-gray-600 mb-2">{schedule.description}</p>
                            <div>
                                <strong>시작:</strong> {formatDateTime(schedule.startTime)}
                            </div>
                            <div>
                                <strong>종료:</strong> {formatDateTime(schedule.endTime)}
                            </div>
                            <div className="flex justify-end mt-2">
                                <button
                                    onClick={() => onEdit(schedule)}
                                    className="bg-white text-black font-bold mr-2 border-none cursor-pointer"
                                >
                                    수정
                                </button>
                                <button
                                    onClick={() => onDelete(schedule.id)}
                                    className="bg-white text-black font-bold border-none cursor-pointer"
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
