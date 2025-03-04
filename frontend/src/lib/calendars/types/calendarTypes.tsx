//src/lib/calendar/types/calendarTypes.ts
export interface Calendar {
  id: number;
  userId: number;
  name: string;
  description: string;
  createdDate?: string;
  modifiedDate?: string;
}

export interface CalendarCreateDto {
  name: string;
  description: string;
}

export interface CalendarUpdateDto {
  name: string;
  description: string;
}