import { useState, useEffect } from "react";
import { eventApi } from "../api/eventApi";
import type { CalendarEvent } from "../types/eventTypes";

export const useCalendarEvent = (calendarId: number) => {
  const [events, setEvents] = useState<CalendarEvent[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<Error | null>(null);

  useEffect(() => {
    const fetchEvents = async () => {
      try {
        setLoading(true);
        const response = await eventApi.getAllByCalendar(calendarId);
        setEvents(response.data);
      } catch (err) {
        setError(err as Error);
      } finally {
        setLoading(false);
      }
    };

    fetchEvents();
  }, [calendarId]);

  return { events, loading, error };
};

export default useCalendarEvent;
