import { useRouter } from "next/navigation";
import { useEffect, useState } from "react";
import { client } from "@/lib/backend/client";

interface LoginRequiredProps {
    children: React.ReactNode;
}

export default function LoginRequired({ children }: LoginRequiredProps) {
    const router = useRouter();
    const [loaded, setLoaded] = useState(false);

    useEffect(() => {
        // JSESSIONID 토큰 체크
        client
            .GET("/auth/check")
            .then(() => {
                setLoaded(true);
            })
            .catch(() => {
                router.push("/login");
            });
    }, [router]);

    if (!loaded) {
        return null; // 또는 로딩 스피너/스켈레톤 UI
    }

    return <>{children}</>;
}