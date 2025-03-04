import axios from "axios";
import { NaverGeocodeApiResponse } from "@/types/naverMapTypes";

/**
 * Reverse Geocoding (좌표 → 도로명 주소 변환)
 */
export const getAddress = async (lat: number, lng: number): Promise<string> => {
    try {
        console.log(`📍 Reverse Geocoding 요청: (${lat}, ${lng})`);

        const response = await axios.get<NaverGeocodeApiResponse>("/api/naverGeocode", {
            params: { lat, lng },
        });

        console.log("🔄 Reverse Geocoding 응답:", response.data);

        if (!response.data || !response.data.results || response.data.results.length === 0) {
            console.warn("⚠️ Reverse Geocoding 결과 없음:", response.data);
            return "주소 없음";
        }

        let bestAddress = "";
        let roadAddress = "";
        let regionAddress = "";

        response.data.results.forEach((result) => {
            const area1 = result.region.area1.name || "";
            const area2 = result.region.area2.name || "";
            const area3 = result.region.area3.name || "";
            const area4 = result.region.area4.name || "";

            const land = result.land;
            const roadName = land?.name || "";
            const roadNumber = land?.number1 ? `${land.number1}${land.number2 ? `-${land.number2}` : ""}` : "";
            const buildingName = land?.addition0?.value || "";

            if (buildingName) {
                bestAddress = `${area1} ${area2} ${area3}, ${roadName} ${roadNumber} (${buildingName})`;
            }

            if (roadName && roadNumber) {
                roadAddress = `${area1} ${area2} ${area3}, ${roadName} ${roadNumber}`;
            }

            if (!regionAddress) {
                regionAddress = `${area1} ${area2} ${area3} ${area4}`.trim();
            }
        });

        return bestAddress || roadAddress || regionAddress || "주소 없음";
    } catch (error) {
        console.error("❌ Reverse Geocoding 요청 실패:", error);
        return "주소 가져오기 실패";
    }
};

/**
 * Geocoding (주소 → 좌표 변환)
 */
export const getCoordinates = async (address: string): Promise<{ x: string; y: string; roadAddress: string } | null> => {
    try {
        console.log(`📍 Geocoding 요청: ${address}`);

        const response = await axios.get<NaverGeocodeApiResponse>("/api/naverGeocode", {
            params: { address },
        });

        console.log("🔄 Geocoding 응답:", response.data);

        if (!response.data || !response.data.addresses || response.data.addresses.length === 0) {
            console.warn("⚠️ 주소 검색 실패 - 응답이 비어 있음:", response.data);
            return null;
        }

        const result = response.data.addresses[0];

        if (!result || !result.x || !result.y) {
            console.warn("⚠️ 올바른 좌표 정보를 찾을 수 없음:", result);
            return null;
        }

        return {
            x: result.x,
            y: result.y,
            roadAddress: result.roadAddress || result.jibunAddress || "주소 없음",
        };
    } catch (error) {
        console.error("❌ Geocoding 요청 실패:", error);
        return null;
    }
};
0