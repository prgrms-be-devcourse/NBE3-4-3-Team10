"use client";
import client from "@/lib/backend/client";
import {useRouter} from "next/navigation";
import Script from "next/script";
import {useEffect, useState} from "react";


export const LoginRequired = <T,>(WrappedComponent: React.FC<T>) => {
    const InnerFunction: React.FC<T> = (props: T) => {
        const router = useRouter();
        const [loaded, setLoaded] = useState(false);
        useEffect(() => {
            client
                .GET("/api/user/me")
                .then(({data}) => {
                    if (data?.id) {
                        console.log(data);
                        setLoaded(true);
                    } else {
                        console.log("no user");
                        router.push("/login");
                    }
                })
                .catch(() => {
                    router.push("/login");
                });
        }, []);
        if (!loaded) return <></>;
        return <WrappedComponent {...(props as any)} />;
    };
    return InnerFunction;
};

export default function CalendarLayout({
                                           children,
                                       }: {
    children: React.ReactNode;
}) {
    return (
        <div>
            <Script
                src="https://cdn.jsdelivr.net/npm/fullcalendar@6.1.15/index.global.min.js"
                strategy="afterInteractive"
            />
            {children}
        </div>
    );
}