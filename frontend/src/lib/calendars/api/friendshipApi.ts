import axios from "axios";
import type { FriendResponseDto } from "../types/friendshipTypes";

const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";
const BASE_URL = `${API_BASE_URL}/api/friends`;

const client = axios.create({
    baseURL: BASE_URL,
    withCredentials: true,
});

export const friendshipApi = {
    /**
     * 친구 추가
     */
    async addFriend(userId1: number, userId2: number) {
        try {
            await client.post("/add", null, {
                params: { userId1, userId2 },
            });
            return { success: true };
        } catch (error: unknown) {
            console.error("📛 친구 추가 실패:", error);
            throw error;
        }
    },

    /**
     * 친구 삭제
     */
    async removeFriend(userId1: number, userId2: number) {
        try {
            await client.delete("/remove", {
                params: { userId1, userId2 },
            });
            return { success: true };
        } catch (error: unknown) {
            console.error("📛 친구 삭제 실패:", error);
            throw error;
        }
    },

    /**
     * 친구 목록 가져오기
     */
    async getFriends(userId: number): Promise<FriendResponseDto[]> {
        try {
            const response = await client.get<FriendResponseDto[]>(`/${userId}`);
            return response.data;
        } catch (error: unknown) {
            console.error("📛 친구 목록 조회 실패:", error);
            throw error;
        }
    },
};
