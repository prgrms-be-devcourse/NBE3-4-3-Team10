// src/components/NaverMapLoader.tsx
"use client";

import { useEffect, useState, createContext, useContext } from "react";

const NaverMapContext = createContext(false);

export const useNaverMapLoaded = () => useContext(NaverMapContext);

export default function NaverMapLoader({ children }: { children: React.ReactNode }) {
    const [isNaverMapLoaded, setIsNaverMapLoaded] = useState(false);

    useEffect(() => {
        if (typeof window !== "undefined") {
            if (window.naver) {
                setIsNaverMapLoaded(true);
            } else {
                console.log("Naver Map Client ID:", process.env.NEXT_PUBLIC_NAVER_MAP_CLIENT_ID);
                const script = document.createElement("script");
                script.src = `https://oapi.map.naver.com/openapi/v3/maps.js?ncpClientId=${process.env.NEXT_PUBLIC_NAVER_MAP_CLIENT_ID}&submodules=geocoder`;
                script.async = true;
                script.onload = () => {
                    console.log("✅ 네이버 지도 스크립트 로드 완료");
                    setIsNaverMapLoaded(true);
                };
                script.onerror = () => {
                    console.error("❌ 네이버 지도 스크립트 로드 실패");
                };
                document.head.appendChild(script);
            }
        }
    }, []);

    return (
        <NaverMapContext.Provider value={isNaverMapLoaded}>
            {children}
        </NaverMapContext.Provider>
    );
}
