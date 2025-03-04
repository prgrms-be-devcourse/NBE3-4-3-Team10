import { useState } from "react";
import DynamicMap from "@/components/schedule/DynamicMap";
import SearchLocation from "@/components/schedule/SearchLocation";

export default function Schedule() {
    // location 상태 초기값 설정 (기본 좌표: 서울 시청)
    const [location, setLocation] = useState<{ lat: number; lng: number; address: string } | null>({
        lat: 37.5665,
        lng: 126.9780,
        address: "서울 시청",
    });

    // 사용자가 새로운 위치를 선택했을 때 상태 업데이트
    const handleLocationSelect = (lat: number, lng: number, address: string) => {
        console.log("📌 선택된 위치 업데이트:", { lat, lng, address });
        setLocation({ lat, lng, address });
    };

    return (
        <div className="p-4">
            <h1 className="text-xl font-bold mb-4">일정 추가</h1>

            {/* 주소 검색 컴포넌트 */}
            <SearchLocation onLocationSelect={handleLocationSelect} />

            {/* 조건부 렌더링: location이 존재할 때만 지도 표시 */}
            {location && (
                <>
                    <DynamicMap
                        latitude={location.lat}
                        longitude={location.lng}
                        onLocationSelect={handleLocationSelect}
                    />
                    {/* 선택된 위치 정보 표시 */}
                    <div className="mt-4 p-2 border rounded">
                        <p>선택한 장소: {location.address}</p>
                        <p>위도: {location.lat}</p>
                        <p>경도: {location.lng}</p>
                    </div>
                </>
            )}
        </div>
    );
}
