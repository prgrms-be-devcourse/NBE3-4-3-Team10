import { useState, useEffect } from "react";
import { friendshipApi } from "@/lib/calendars/api/friendshipApi";
import type { FriendResponseDto } from "@/lib/calendars/types/friendshipTypes";

export const useFriendship = (userId: number | null) => {
    const validUserId = userId || 0;
    console.log("📌 [useFriendship] 받은 userId:", validUserId);

    const [friends, setFriends] = useState<FriendResponseDto[]>([]);
    const [loading, setLoading] = useState<boolean>(true);
    const [error, setError] = useState<Error | null>(null);

    useEffect(() => {
        if (!userId) {
            console.log("❌ [useFriendship] userId가 없음. API 요청하지 않음.");
            return;
        }

        const fetchFriends = async () => {
            try {
                setLoading(true);
                const data = await friendshipApi.getFriends(validUserId);
                setFriends(data);
                setError(null);
            } catch (err) {
                setError(err as Error);
            } finally {
                setLoading(false);
            }
        };

        fetchFriends();
    }, [userId]);

    return { friends, loading, error, addFriend: friendshipApi.addFriend, removeFriend: friendshipApi.removeFriend };
};
