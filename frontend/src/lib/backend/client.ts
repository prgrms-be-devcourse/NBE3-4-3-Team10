import type { paths } from "@/lib/backend/schema";
import createClient from "openapi-fetch";

export const client = createClient<paths>({
  baseUrl: "http://localhost:8080",
  credentials: "include",
});

export default client;