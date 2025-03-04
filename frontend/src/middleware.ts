import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";
import { cookies } from "next/headers";
import client from "./lib/backend/client";
import { ResponseCookie } from "next/dist/compiled/@edge-runtime/cookies";
import { ReadonlyRequestCookies } from "next/dist/server/web/spec-extension/adapters/request-cookies";

export async function middleware(req: NextRequest) {
  const cookieStore = await cookies();
  const accessToken = cookieStore.get("accessToken")?.value;

  // JWT 토큰 만료 확인
  const { isLogin, isAccessTokenExpired, accessTokenPayload } =
    parseAccessToken(accessToken);

  // 만료 되어있으면 갱신
  if (isLogin && isAccessTokenExpired) await refreshTokens(cookieStore);

  return NextResponse.next({
    headers: {
      cookie: cookieStore.toString(),
    },
  });
}

function parseAccessToken(accessToken: string | undefined) {
  let isAccessTokenExpired = true;
  let accessTokenPayload = null;

  if (accessToken) {
    try {
      const tokenParts = accessToken.split(".");
      accessTokenPayload = JSON.parse(
        Buffer.from(tokenParts[1], "base64").toString()
      );
      const expTimestamp = accessTokenPayload.exp * 1000;
      isAccessTokenExpired = Date.now() > expTimestamp;   // 만료 확인
    } catch (e) {
      console.error("토큰 파싱 중 오류 발생:", e);
    }
  }

  const isLogin =
    typeof accessTokenPayload === "object" && accessTokenPayload !== null;

  return { isLogin, isAccessTokenExpired, accessTokenPayload };
}

async function refreshTokens(cookieStore: ReadonlyRequestCookies) {
  const meResponse = await client.GET("/api/user/me", {
    headers: {
      cookie: cookieStore.toString(),
    },
  });

  // 헤더 중에 Set-Cookie 만
  const setCookieHeader = meResponse.response.headers.get("Set-Cookie");

  if (setCookieHeader) {
    const cookies = setCookieHeader.split(",");

    for (const cookieStr of cookies) {
      // 쿠키 보안설정 유지
      const cookieData = parseCookie(cookieStr);

      if (cookieData) {
        const { name, value, options } = cookieData;
        if (name !== "accessToken" && name !== "apiKey") return null;

        // 새로운 쿠키 (JWT) 설정
        cookieStore.set(name, value, options);
      }
    }
  }
}

function parseCookie(cookieStr: string) {
  const parts = cookieStr.split(";").map((p) => p.trim());
  const [name, value] = parts[0].split("=");

  const options: Partial<ResponseCookie> = {};
  for (const part of parts.slice(1)) {
    if (part.toLowerCase() === "httponly") options.httpOnly = true;
    else if (part.toLowerCase() === "secure") options.secure = true;
    else {
      const [key, val] = part.split("=");
      const keyLower = key.toLowerCase();
      if (keyLower === "domain") options.domain = val;
      else if (keyLower === "path") options.path = val;
      else if (keyLower === "max-age") options.maxAge = parseInt(val);
      else if (keyLower === "expires")
        options.expires = new Date(val).getTime();
      else if (keyLower === "samesite")
        options.sameSite = val.toLowerCase() as "lax" | "strict" | "none";
    }
  }

  return { name, value, options };
}