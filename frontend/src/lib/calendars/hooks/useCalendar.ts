import { useState, useEffect } from 'react';
import { calendarApi } from '../api/calendarApi';
import type { Calendar, CalendarCreateDto, CalendarUpdateDto } from '../types/calendarTypes';

export const useCalendar = () => {
  const [calendars, setCalendars] = useState<Calendar[]>([]);
  const [sharedCalendars, setSharedCalendars] = useState<Calendar[]>([]); // ✅ 공유된 캘린더 상태 추가
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);

  /**
   * ✅ 모든 캘린더 불러오기
   */
  const fetchCalendars = async (): Promise<void> => {
    try {
      setLoading(true);
      const response = await calendarApi.getAllCalendars();
      console.log("📌 Raw response (All Calendars):", response);

      if (Array.isArray(response.data)) {
        setCalendars(response.data);
      } else {
        console.error("📛 캘린더 데이터 형식 오류:", response.data);
        setCalendars([]); // ✅ 데이터가 올바르지 않으면 빈 배열로 설정
      }
      setError(null);
    } catch (err) {
      setError(err as Error);
      console.error("📛 캘린더 불러오기 실패:", err);
      setCalendars([]); // ✅ 오류 발생 시 빈 배열 설정
    } finally {
      setLoading(false);
    }
  };


  /**
   * ✅ 공유된 캘린더 불러오기
   */
  const fetchSharedCalendars = async (userId: number): Promise<Calendar[]> => {
    try {
      setLoading(true);
      const response = await calendarApi.getSharedCalendars(userId);
      console.log("Raw response (Shared Calendars):", response);

      if (Array.isArray(response.data)) {
        setSharedCalendars(response.data);
        return response.data;  // ✅ 반환값을 Calendar[]로 변경
      } else {
        console.error("📛 공유된 캘린더 데이터 형식 오류:", response.data);
        setSharedCalendars([]);
        return [];  // ✅ 빈 배열 반환하여 오류 방지
      }
    } catch (err) {
      setError(err as Error);
      console.error("📛 공유된 캘린더 불러오기 실패:", err);
      setSharedCalendars([]);
      return [];  // ✅ 빈 배열 반환하여 오류 방지
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    fetchCalendars();
  }, []);

  /**
   * ✅ 캘린더 생성 (복구)
   */
  const createCalendar = async (calendarData: CalendarCreateDto) => {
    try {
      const response = await calendarApi.createCalendar(calendarData);
      const newCalendar = response.data;
      setCalendars(prev => [...prev, newCalendar]);
      return newCalendar;
    } catch (err) {
      console.error('📛 캘린더 생성 실패:', err);
      throw err;
    }
  };

  /**
   * ✅ 캘린더 수정 (복구)
   */
  const updateCalendar = async (id: number, calendarData: CalendarUpdateDto) => {
    try {
      const response = await calendarApi.updateCalendar(id, calendarData);
      const updatedCalendar = response.data;
      setCalendars(prev =>
          prev.map(calendar => (calendar.id === id ? updatedCalendar : calendar))
      );
      return updatedCalendar;
    } catch (err) {
      console.error('📛 캘린더 업데이트 실패:', err);
      throw err;
    }
  };

  /**
   * ✅ 캘린더 삭제
   */
  const deleteCalendar = async (id: number) => {
    try {
      await calendarApi.deleteCalendar(id);
      setCalendars(prev => prev.filter(calendar => calendar.id !== id));
    } catch (err) {
      console.error('📛 캘린더 삭제 실패:', err);
      throw err;
    }
  };

  /**
   * ✅ 친구에게 캘린더 공유
   */
  const shareCalendar = async (calendarId: number, friendId: number, userId: number) => {
    try {
      await calendarApi.shareCalendar(calendarId, friendId);
      console.log(`📌 캘린더 (${calendarId})가 친구 (${friendId})에게 공유되었습니다.`);

      // ✅ 공유된 캘린더 즉시 업데이트
      fetchSharedCalendars(userId);
      return true;
    } catch (err) {
      console.error("📛 캘린더 공유 실패:", err);
      throw err;
    }
  };


  /**
   * ✅ 친구와 캘린더 공유 해제
   */
  const unshareCalendar = async (calendarId: number, friendId: number) => {
    try {
      await calendarApi.unshareCalendar(calendarId, friendId);
      console.log(`📌 캘린더 (${calendarId}) 공유가 친구 (${friendId})와 해제되었습니다.`);
      return true;
    } catch (err) {
      console.error('📛 캘린더 공유 해제 실패:', err);
      throw err;
    }
  };

  return {
    calendars,
    sharedCalendars,
    loading,
    error,
    createCalendar,  // ✅ 캘린더 생성 기능 유지
    updateCalendar,  // ✅ 캘린더 수정 기능 유지
    deleteCalendar,
    fetchCalendars,
    fetchSharedCalendars,
    shareCalendar,
    unshareCalendar,
  };
};
