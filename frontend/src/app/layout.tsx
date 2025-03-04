// src/app/layout.tsx
import type { Metadata } from "next";
import { Geist } from "next/font/google";
import localFont from "next/font/local";
import "./globals.css";
import ClientLayout from "./ClientLayout";
import { cookies } from "next/headers";
import { parseAccessToken } from "@/lib/auth/token";
import { config } from "@fortawesome/fontawesome-svg-core";
import "@fortawesome/fontawesome-svg-core/styles.css";
import NaverMapLoader from "@/components/schedule/NaverMapLoader";

config.autoAddCss = false;

// ✅ 폰트 설정 유지
const geistSans = Geist({ variable: "--font-geist-sans", subsets: ["latin"] });
const geistMono = Geist({ variable: "--font-geist-mono", subsets: ["latin"] });
const pretendard = localFont({
    src: [
        {
            path: "../../node_modules/pretendard/dist/web/static/woff2/Pretendard-Black.woff2",
            weight: "45 920",
            style: "normal",
        },
    ],
    variable: "--font-pretendard",
});

// ✅ 메타데이터 설정 (SEO 및 브라우저 제목 적용)
export const metadata: Metadata = {
    title: "Naver Map Schedule App",
    description: "Naver Map 연동 일정 관리 앱",
};

export default async function RootLayout({ children }: { children: React.ReactNode }) {
    let me: {
        id?: number;
        username?: string;
        nickname?: string;
        email?: string;
        createDate?: string;
        modifyDate?: string;
    } = {};  // 빈 객체로 초기화 (null 방지)

    let isLogin = false;
    let isAdmin = false;

    try {
        const cookieStore = await cookies();
        const accessToken = cookieStore.get("accessToken")?.value ?? null;

        if (accessToken) {
            const parsedToken = parseAccessToken(accessToken);
            me = parsedToken.me;
            isLogin = parsedToken.isLogin;
            isAdmin = parsedToken.isAdmin;
        }
    } catch (error) {
        console.error("❌ JWT 파싱 오류:", error);
    }

    return (
        <html lang="en" className={`${geistSans.variable} ${geistMono.variable} ${pretendard.variable}`}>
        <head>
            <meta charSet="utf-8" />
            <meta name="viewport" content="width=device-width, initial-scale=1" />
            <title>Naver Map Schedule App</title>
        </head>
        <body className="antialiased">
        <div className="antialiased flex flex-col min-h-[100dvh]">
            <NaverMapLoader>
                <ClientLayout me={me} isLogin={isLogin} isAdmin={isAdmin}>
                    {children}
                </ClientLayout>
            </NaverMapLoader>
        </div>
        </body>
        </html>
    );
}
