import { useEffect, useRef, useState } from "react";
import { getAddress } from "@/lib/schedule/utils/naverMapService";
import { useNaverMapLoaded } from "@/components/schedule/NaverMapLoader"; // 네이버 지도 로드 상태 사용


interface DynamicMapProps {
    latitude: number;
    longitude: number;
    onLocationSelect: (lat: number, lng: number, address: string) => void;
}

const DynamicMap = ({ latitude, longitude, onLocationSelect }: DynamicMapProps) => {
    const mapRef = useRef<HTMLDivElement>(null);
    const [map, setMap] = useState<naver.maps.Map | null>(null);
    const [marker, setMarker] = useState<naver.maps.Marker | null>(null);
    const isNaverMapLoaded = useNaverMapLoaded(); // 네이버 지도 로드 상태 가져오기

    // 네이버 지도 초기화
    useEffect(() => {
        if (!mapRef.current || !isNaverMapLoaded) {
            return;
        }

        const mapInstance = new naver.maps.Map(mapRef.current, {
            center: new naver.maps.LatLng(latitude, longitude),
            zoom: 15,
        });
        setMap(mapInstance);

        const markerInstance = new naver.maps.Marker({
            position: new naver.maps.LatLng(latitude, longitude),
            map: mapInstance,
        });
        setMarker(markerInstance);
    }, [isNaverMapLoaded, latitude, longitude]); // 지도 로드와 좌표 변경에 따라 초기화

    // 지도 클릭 이벤트 핸들러
    const handleMapClick = async (event: naver.maps.PointerEvent) => {
        const lat = event.coord.y;
        const lng = event.coord.x;

        console.log("🗺️ 지도 클릭 - 좌표:", { lat, lng });

        if (marker) marker.setMap(null);
        const newMarker = new naver.maps.Marker({
            position: new naver.maps.LatLng(lat, lng),
            map: map!,
        });
        setMarker(newMarker);

        try {
            const address = await getAddress(lat, lng);
            console.log("📍 선택한 주소:", address);
            onLocationSelect(lat, lng, address);
        } catch (error) {
            console.error("📛 Reverse Geocoding 요청 실패:", error);
        }
    };


    // ✅ 지도 클릭 이벤트 등록 및 해제
    useEffect(() => {
        if (!map) return;

        const listener = naver.maps.Event.addListener(map, "click", handleMapClick);
        console.log("✅ 지도 클릭 이벤트 등록");

        return () => {
            console.log("❌ 지도 클릭 이벤트 해제");
            naver.maps.Event.removeListener(listener);
        };
    }, [map]);


    return <div ref={mapRef} style={{ width: "100%", height: "400px", backgroundColor: "#ddd" }} />;
};

export default DynamicMap;
