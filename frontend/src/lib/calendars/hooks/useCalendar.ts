import { useState, useEffect } from 'react';
import { calendarApi } from '../api/calendarApi';
import type { Calendar, CalendarCreateDto, CalendarUpdateDto } from '../types/calendarTypes';

export const useCalendar = () => {
  const [calendars, setCalendars] = useState<Calendar[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);

  const fetchCalendars = async () => {
    try {
      setLoading(true);
      const response = await calendarApi.getAllCalendars();
      console.log('Raw response:', response); // 원본 응답 확인

      if (response?.data) {
        // response.data가 이미 객체/배열인 경우
        if (Array.isArray(response.data)) {
          setCalendars(response.data);
        }
        // response.data가 문자열인 경우
        else if (typeof response.data === 'string') {
          try {
            setCalendars(JSON.parse(response.data));
          } catch (parseError) {
            console.error('JSON 파싱 에러:', parseError);
            setCalendars([]);
          }
        } else {
          setCalendars([]);
        }
      } else {
        setCalendars([]);
      }
      setError(null);
    } catch (err) {
      setError(err as Error);
      console.error('Failed to fetch calendars:', err);
      setCalendars([]);
    } finally {
      setLoading(false);
    }
  };
  useEffect(() => {
    fetchCalendars();
  }, []);

  const createCalendar = async (calendarData: CalendarCreateDto) => {
    try {
      const response = await calendarApi.createCalendar(calendarData);
      const newCalendar = response.data;
      setCalendars(prev => [...prev, newCalendar]);
      return newCalendar;
    } catch (err) {
      console.error('Failed to create calendar:', err);
      throw err;
    }
  };

  const updateCalendar = async (id: number, calendarData: CalendarUpdateDto) => {
    try {
      const response = await calendarApi.updateCalendar(id, calendarData);
      const updatedCalendar = response.data;
      setCalendars(prev =>
          prev.map(calendar => (calendar.id === id ? updatedCalendar : calendar))
      );
      return updatedCalendar;
    } catch (err) {
      console.error('Failed to update calendar:', err);
      throw err;
    }
  };

  const deleteCalendar = async (id: number) => {
    try {
      await calendarApi.deleteCalendar(id);
      setCalendars(prev => prev.filter(calendar => calendar.id !== id));
    } catch (err) {
      console.error('Failed to delete calendar:', err);
      throw err;
    }
  };

  return {
    calendars,
    loading,
    error,
    createCalendar,
    updateCalendar,
    deleteCalendar,
    fetchCalendars,
  };
};