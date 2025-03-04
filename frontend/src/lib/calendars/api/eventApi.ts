import axios from "axios";
import { CalendarEvent } from "../types/eventTypes";

const BASE_URL = "/api/calendars";

export const eventApi = {
  getAllByCalendar: (calendarId: number) =>
    axios.get<CalendarEvent[]>(`${BASE_URL}/${calendarId}/events`),

  create: (calendarId: number, event: Omit<CalendarEvent, "id">) =>
    axios.post<CalendarEvent>(`${BASE_URL}/${calendarId}/events`, event),

  update: (
    calendarId: number,
    eventId: number,
    event: Partial<CalendarEvent>
  ) =>
    axios.put<CalendarEvent>(
      `${BASE_URL}/${calendarId}/events/${eventId}`,
      event
    ),

  delete: (calendarId: number, eventId: number) =>
    axios.delete(`${BASE_URL}/${calendarId}/events/${eventId}`),
};
