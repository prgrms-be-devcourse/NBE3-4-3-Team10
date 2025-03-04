import axios from "axios";
import { Schedule, ScheduleFormData } from "@/types/schedule/schedule";

// 환경 변수에서 API 주소 가져오기
const API_BASE_URL = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";
const BASE_URL = `${API_BASE_URL}/api/calendars`;

export const scheduleApi = {
    async createSchedule(calendarId: number, scheduleData: ScheduleFormData): Promise<Schedule> {
        try {
            const response = await axios.post<Schedule>(`${BASE_URL}/${calendarId}/schedules`, scheduleData, {withCredentials: true});
            return response.data;
        } catch (error) {
            console.error("📛 일정 생성 실패:", error);
            throw error;
        }
    },

    async updateSchedule(calendarId: number, scheduleId: number, scheduleData: ScheduleFormData): Promise<Schedule> {
        try {
            const response = await axios.put<Schedule>(`${BASE_URL}/${calendarId}/schedules/${scheduleId}`, scheduleData, {withCredentials: true});
            return response.data;
        } catch (error) {
            console.error("📛 일정 업데이트 실패:", error);
            throw error;
        }
    },

    async deleteSchedule(calendarId: number, scheduleId: number): Promise<void> {
        try {
            await axios.delete(`${BASE_URL}/${calendarId}/schedules/${scheduleId}`,{withCredentials: true});
        } catch (error) {
            console.error("📛 일정 삭제 실패:", error);
            throw error;
        }
    },

    async getSchedules(calendarId: number, date: string): Promise<Schedule[]> {
        console.log("API Request URL:", `${BASE_URL}/${calendarId}/schedules/daily?date=${date}`);

        try {
            const response = await axios.get<Schedule[]>(`${BASE_URL}/${calendarId}/schedules/daily`, {
                params: { date },
                withCredentials: true
            });

            console.log("API Response Data:", response.data);
            return response.data;
        } catch (error) {
            console.error("Error fetching schedules:", error);
            throw error;
        }
    },


    async getWeeklySchedules(calendarId: number, date: string): Promise<Schedule[]> {
        try {
            const response = await axios.get<Schedule[]>(`${BASE_URL}/${calendarId}/schedules/weekly`, {
                params: { date }, withCredentials: true
            });
            return response.data;
        } catch (error) {
            console.error("📛 주별 일정 조회 실패:", error);
            throw error;
        }
    },

    async getMonthlySchedules(calendarId: number, date: string): Promise<Schedule[]> {
        try {
            const response = await axios.get<Schedule[]>(`${BASE_URL}/${calendarId}/schedules/monthly`, {
                params: { date }, withCredentials: true
            });
            return response.data;
        } catch (error) {
            console.error("📛 월별 일정 조회 실패:", error);
            throw error;
        }
    },


    async getScheduleById(calendarId: number, scheduleId: number): Promise<Schedule> {
        try {
            const response = await axios.get<Schedule>(`${BASE_URL}/${calendarId}/schedules/${scheduleId}`, {withCredentials: true});
            return response.data;

        } catch (error) {
            console.error("📛 일정 단일 조회 실패:", error);
            throw error;
        }
    },
};
