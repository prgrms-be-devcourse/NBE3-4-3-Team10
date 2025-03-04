import { useState } from "react";
import { getCoordinates } from "@/lib/schedule/utils/naverMapService";

const SearchLocation = ({ onLocationSelect }: { onLocationSelect: (lat: number, lng: number, address: string) => void }) => {
    const [address, setAddress] = useState("");

    const handleSearch = async () => {
        if (!address) return;

        console.log("🔍 입력한 주소:", address);
        const result = await getCoordinates(address);

        if (result) {
            console.log("✅ 검색된 좌표:", { lat: result.y, lng: result.x, roadAddress: result.roadAddress });
            onLocationSelect(parseFloat(result.y), parseFloat(result.x), result.roadAddress);
        } else {
            console.error("📛 주소 검색 실패: 결과가 없습니다.");
        }
    };



    return (
        <div className="p-4">
            <input
                type="text"
                value={address}
                onChange={(e) => setAddress(e.target.value)}
                placeholder="주소 입력"
                className="border p-2 rounded w-full"
            />
            <button onClick={handleSearch} className="mt-2 p-2 bg-blue-500 text-white rounded">
                검색
            </button>
        </div>
    );
};

export default SearchLocation;
