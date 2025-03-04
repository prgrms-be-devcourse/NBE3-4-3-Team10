// src/types/naverMapTypes.ts
export interface NaverGeocodeResult {
    region: {
        area1: { name: string };
        area2: { name: string };
        area3: { name: string };
        area4: { name: string };
    };
    land?: {
        name?: string; // 도로명
        number1?: string; // 도로 번호
        number2?: string; // 도로 번호 (추가)
        addition0?: { value: string }; // 건물명
        x?: string; // 경도 (longitude)
        y?: string; // 위도 (latitude)
    };
}

export interface NaverGeocodeApiResponse {
    status: string;
    meta: {
        totalCount: number;
        page: number;
        count: number;
    };
    addresses: {
        roadAddress: string;
        jibunAddress: string;
        x: string;
        y: string;
    }[]; //
    errorMessage?: string;
}


export interface NaverGeocodeApiResponse {
    results: NaverGeocodeResult[];
}
