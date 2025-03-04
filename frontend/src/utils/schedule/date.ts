export const formatDateTime = (dateTime: string): string => {
    return new Date(dateTime).toLocaleString();
};