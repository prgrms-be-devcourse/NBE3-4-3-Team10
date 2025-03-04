import React, { useState } from 'react';
import { ScheduleFormData } from '@/types/schedule/schedule';
import MapPopup from '@/components/schedule/MapPopup';

interface ScheduleFormProps {
    initialData?: ScheduleFormData;
    onSubmit: (formData: ScheduleFormData) => void;
    onCancel: () => void;
    isNew?: boolean;
}

export default function ScheduleForm({ initialData, onSubmit, onCancel, isNew }: ScheduleFormProps) {
    const [formData, setFormData] = useState<ScheduleFormData>({
        title: initialData?.title || '',
        description: initialData?.description || '',
        startTime: initialData?.startTime || '',
        endTime: initialData?.endTime || '',
        location: initialData?.location || { address: '', latitude: 0, longitude: 0 }
    });

    const [isMapPopupVisible, setIsMapPopupVisible] = useState(false);

    // 입력값 변경 핸들러
    const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { name, value } = e.target;
        setFormData(prev => ({ ...prev, [name]: value }));
    };

    // 지도에서 선택한 위치 반영
    const handleLocationSelect = (lat: number, lng: number, address: string) => {
        setFormData(prev => ({
            ...prev,
            location: { latitude: lat, longitude: lng, address }
        }));
        setIsMapPopupVisible(false);
    };

    // 폼 제출
    const handleSubmit = (e: React.FormEvent) => {
        e.preventDefault();
        onSubmit(formData);
    };

    return (
        <>
            <form onSubmit={handleSubmit} className="space-y-4">
                <div>
                    <label className="block font-bold mb-1">제목</label>
                    <input
                        type="text"
                        name="title"
                        value={formData.title}
                        onChange={handleChange}
                        className="w-full p-2 border rounded"
                        required
                    />
                </div>

                <div>
                    <label className="block font-bold mb-1">시간</label>
                    <input
                        type="datetime-local"
                        name="startTime"
                        value={formData.startTime}
                        onChange={handleChange}
                        className="w-full p-2 border rounded mb-2"
                        required
                    />
                    <input
                        type="datetime-local"
                        name="endTime"
                        value={formData.endTime}
                        onChange={handleChange}
                        className="w-full p-2 border rounded"
                        required
                    />
                </div>

                {/* 주소 입력 (비활성화) + 지도에서 선택 버튼 */}
                <div>
                    <label className="block font-bold mb-1">주소</label>
                    <div className="flex">
                        <input
                            type="text"
                            value={formData.location.address}
                            className="w-full p-2 border rounded bg-gray-100 cursor-not-allowed"
                            readOnly
                        />
                        <button
                            type="button"
                            onClick={() => setIsMapPopupVisible(true)}
                            className="ml-2 p-2 bg-black text-white rounded hover:bg-gray-800"
                        >
                            지도에서 선택
                        </button>
                    </div>
                </div>

                <div>
                    <label className="block font-bold mb-1">설명</label>
                    <textarea
                        name="description"
                        value={formData.description}
                        onChange={handleChange}
                        className="w-full p-2 border rounded"
                    />
                </div>

                <div className="flex justify-end space-x-4">
                    <button type="submit" className="p-2 bg-black text-white font-bold rounded hover:bg-gray-700">
                        {isNew ? '저장' : '수정'}
                    </button>
                    <button type="button" onClick={onCancel}
                            className="p-2 bg-black text-white font-bold rounded hover:bg-gray-700">
                        취소
                    </button>
                </div>
            </form>

            {/* 지도 팝업 (z-index로 최상단 유지) */}
            {isMapPopupVisible && (
                <MapPopup
                    onSelectLocation={handleLocationSelect}
                    onClose={() => setIsMapPopupVisible(false)}
                />
            )}
        </>
    );
}
