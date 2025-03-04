//src/lib/calendar/api/calendarApi.ts
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
  getAllCalendars: () =>
      client.get<Calendar[] | string>('/calendars'),

  getCalendarById: (id: number) =>
      client.get<Calendar>(`/calendars/${id}`),

  createCalendar: (data: CalendarCreateDto) =>
      client.post<Calendar>('/calendars', data),

  updateCalendar: (id: number, data: CalendarUpdateDto) =>
      client.put<Calendar>(`/calendars/${id}`, data),

  deleteCalendar: (id: number) =>
      client.delete(`/calendars/${id}`)
};