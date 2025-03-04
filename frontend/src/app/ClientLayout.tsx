// 레이아웃 (브라우저)

"use client";

import client from "@/lib/backend/client";
import {components} from "@/lib/backend/schema";
import Link from "next/link";
import {useEffect, useState} from "react";
import {useRouter} from "next/navigation";
import {FontAwesomeIcon} from "@fortawesome/react-fontawesome";
import {faHouse} from "@fortawesome/free-solid-svg-icons/faHouse";
import {
    faCalendar,
    faCopyright,
    faList,
    faPowerOff,
    faUser,
} from "@fortawesome/free-solid-svg-icons";

export default function ClientLayout({
                                         children,
                                         me,
                                         isLogin,
                                         isAdmin,
                                     }: Readonly<{
    children: React.ReactNode;
    me: components["schemas"]["UserDto"];
    isLogin: boolean;
    isAdmin: boolean;
}>) {
    const [isHydrated, setIsHydrated] = useState(false);
    const router = useRouter();

    useEffect(() => {
        setIsHydrated(true);

        // 로그인한 사용자는 /calendars로 자동 이동
        // if (isLogin) {
        //   router.push("/calendars");
        // }
    }, [isLogin, router]);

    if (!isHydrated) {
        return null; // 클라이언트에서만 렌더링되도록 함
    }

    const logout = async () => {
        const response = await client.POST("/api/admin/logout");

        if (response.error) {
            alert(response.error.msg);
            return;
        }

        window.location.replace("/");
    };

    return (
        <>
            <header className="p-5">
                <div className="flex gap-8">
                    <Link href="/">
                        <FontAwesomeIcon icon={faHouse} className="px-2"/>홈
                    </Link>
                    {isLogin && <div>환영합니다. {me.nickname}님!</div>}
                    {isLogin && (
                        <Link href="/calendars">
                            <FontAwesomeIcon icon={faCalendar} className="px-2"/>
                            캘린더
                        </Link>
                    )}
                    {isLogin && (
                        <Link href="/me">
                            <FontAwesomeIcon icon={faUser} className="px-2"/>
                            내정보
                        </Link>
                    )}
                    {isAdmin && (
                        <Link href="/admin/users/list">
                            <FontAwesomeIcon icon={faList} className="px-2"/>
                            회원 명단
                        </Link>
                    )}
                    {isAdmin && (
                        <Link href="/admin/admins/list">
                            <FontAwesomeIcon icon={faList} className="px-2"/>
                            관리자 명단
                        </Link>
                    )}
                    <div className="flex-grow"></div>
                    {!isLogin && (
                        <Link href="/login">
                            <FontAwesomeIcon icon={faPowerOff} className="px-2"/>
                            로그인
                        </Link>
                    )}
                    {isLogin && (
                        <button onClick={logout}>
                            <FontAwesomeIcon icon={faPowerOff} className="px-2"/>
                            로그아웃
                        </button>
                    )}
                </div>
            </header>

            <hr/>

            <main className="flex-grow p-5">{children}</main>

            <hr/>

            <footer className="p-5">
                <FontAwesomeIcon icon={faCopyright} className="px-2"/>
                Copyright 2025.
            </footer>
        </>
    );
}
