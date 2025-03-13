import axios from 'axios';
import type { Calendar, CalendarCreateDto, CalendarUpdateDto } from '../types/calendarTypes';

const client = axios.create({
  baseURL: 'http://localhost:8080/api',
  withCredentials: true
});

// 요청 인터셉터 수정
client.interceptors.request.use((config) => {
  if (!config.headers) {
    config.headers = {};
  }

  const apiKey = document.cookie
      .split('; ')
      .find(row => row.startsWith('apiKey='))
      ?.split('=')[1];

  const accessToken = document.cookie
      .split('; ')
      .find(row => row.startsWith('accessToken='))
      ?.split('=')[1];

  if (apiKey && accessToken) {
    config.headers.Authorization = `Bearer ${apiKey} ${accessToken}`;
  }

  return config;
}, (error) => {
  return Promise.reject(error);
});

export const calendarApi = {
  /**
   * ✅ 모든 캘린더 조회
   */
  getAllCalendars: () =>
      client.get<Calendar[] | string>('/calendars'),

  /**
   * ✅ 특정 캘린더 조회
   */
  getCalendarById: (id: number) =>
      client.get<Calendar>(`/calendars/${id}`),

  /**
   * ✅ 캘린더 생성
   */
  createCalendar: (data: CalendarCreateDto) =>
      client.post<Calendar>('/calendars', data),

  /**
   * ✅ 캘린더 수정
   */
  updateCalendar: (id: number, data: CalendarUpdateDto) =>
      client.put<Calendar>(`/calendars/${id}`, data),

  /**
   * ✅ 캘린더 삭제
   */
  deleteCalendar: (id: number) =>
      client.delete(`/calendars/${id}`),

  /**
   * ✅ 사용자가 공유받은 캘린더 목록 조회
   */
  getSharedCalendars: (userId: number) =>
      client.get<Calendar[]>(`/calendars/shared/${userId}`),

  /**
   * ✅ 특정 친구에게 캘린더 공유
   */
  shareCalendar: (calendarId: number, friendId: number) =>
      client.post<string>(`/calendars/${calendarId}/share/${friendId}`),

  /**
   * ✅ 특정 친구와 캘린더 공유 해제
   */
  unshareCalendar: (calendarId: number, friendId: number) =>
      client.delete<string>(`/calendars/${calendarId}/unshare/${friendId}`),
};
