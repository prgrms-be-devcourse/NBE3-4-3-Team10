import { useEffect, useState } from "react";
import { getCoordinates, getAddress } from "@/lib/schedule/utils/naverMapService";

interface MapPopupProps {
    onSelectLocation: (lat: number, lng: number, address: string) => void;
    onClose: () => void;
}

export default function MapPopup({ onSelectLocation, onClose }: MapPopupProps) {
    const [searchInput, setSearchInput] = useState("");
    const [selectedLocation, setSelectedLocation] = useState<{ lat: number, lng: number, address: string } | null>(null);
    const [map, setMap] = useState<naver.maps.Map | null>(null);
    const [marker, setMarker] = useState<naver.maps.Marker | null>(null);

    useEffect(() => {
        if (typeof window === "undefined" || !window.naver) return;

        const mapInstance = new naver.maps.Map("popup-map", {
            center: new naver.maps.LatLng(37.5665, 126.9780),
            zoom: 13,
        });

        const markerInstance = new naver.maps.Marker({
            position: mapInstance.getCenter(),
            map: mapInstance,
        });

        setMap(mapInstance);
        setMarker(markerInstance);

        naver.maps.Event.addListener(mapInstance, "click", async (e) => {
            const lat = e.coord.lat();
            const lng = e.coord.lng();

            try {
                console.log(`📍 지도 클릭 - 좌표 (${lat}, ${lng})`);
                const address = await getAddress(lat, lng);
                console.log("📌 Reverse Geocoding 결과:", address);

                setSelectedLocation({ lat, lng, address });
                markerInstance.setPosition(new naver.maps.LatLng(lat, lng));
            } catch (error) {
                console.error("❌ Reverse Geocoding 실패:", error);
            }
        });

        document.body.style.overflow = "hidden";
        return () => {
            document.body.style.overflow = "auto";
        };
    }, []);

    // 주소 검색 실행 (Geocoding)
    const handleSearch = async () => {
        if (!searchInput) return;
        const result = await getCoordinates(searchInput);

        if (result) {
            const { x, y, roadAddress } = result;
            const lat = parseFloat(y);
            const lng = parseFloat(x);
            const address = roadAddress || "주소 없음";

            console.log(`🔍 검색된 위치: (${lat}, ${lng}) - ${address}`);
            setSelectedLocation({ lat, lng, address });

            if (map) {
                map.setCenter(new naver.maps.LatLng(lat, lng));
                if (marker) {
                    marker.setPosition(new naver.maps.LatLng(lat, lng));
                }
            }
        }
    };

    return (
        <div className="fixed inset-0 flex items-center justify-center bg-black bg-opacity-50 z-[1000]">
            <div className="bg-white p-6 rounded-lg shadow-2xl max-w-lg w-full z-[1001] relative">
                <h2 className="text-lg font-bold mb-2">위치 선택</h2>

                {/* 주소 검색 */}
                <div className="flex mb-2">
                    <input
                        type="text"
                        value={searchInput}
                        onChange={(e) => setSearchInput(e.target.value)}
                        placeholder="주소 검색"
                        className="border p-2 w-full rounded"
                    />
                    <button onClick={handleSearch} className="ml-2 p-2 bg-black text-white rounded hover:bg-gray-800">
                        검색
                    </button>
                </div>

                {/* 지도 표시 */}
                <div id="popup-map" className="w-full h-64 mb-2 relative z-[1002]"></div>

                {/* 선택한 주소 표시 */}
                {selectedLocation && (
                    <div className="mt-2 p-2 border rounded">
                        <p>선택한 주소: {selectedLocation.address}</p>
                        <p>위도: {selectedLocation.lat}, 경도: {selectedLocation.lng}</p>
                    </div>
                )}

                {/* 버튼 */}
                <div className="flex justify-end space-x-2 mt-4">
                    <button
                        onClick={() => {
                            if (selectedLocation) {
                                onSelectLocation(selectedLocation.lat, selectedLocation.lng, selectedLocation.address);
                                onClose();
                            }
                        }}
                        className="p-2 bg-black text-white rounded hover:bg-gray-800"
                    >
                        저장
                    </button>
                    <button onClick={onClose} className="p-2 bg-black text-white rounded hover:bg-gray-800">
                        닫기
                    </button>
                </div>
            </div>
        </div>
    );
}
