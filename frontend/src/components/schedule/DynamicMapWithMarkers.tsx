'use client';

import { useEffect, useRef } from "react";

interface Marker {
    id: number;
    lat: number;
    lng: number;
    address: string;
    label: string;
}

interface Props {
    markers: Marker[];
    selectedMarkerId?: number;
}

export default function DynamicMapWithMarkers({ markers, selectedMarkerId }: Props) {
    const mapRef = useRef<naver.maps.Map | null>(null);

    useEffect(() => {
        if (typeof window === "undefined" || !window.naver) return;

        const mapOptions = {
            center: new naver.maps.LatLng(markers[0]?.lat || 37.5665, markers[0]?.lng || 126.9780),
            zoom: 13,
        };

        const map = new naver.maps.Map("map", mapOptions);
        mapRef.current = map;

        markers.forEach((marker) => {
            new naver.maps.Marker({
                position: new naver.maps.LatLng(marker.lat, marker.lng),
                map,
                icon: {
                    content: `<div style="
                        background-color: red;
                        color: white;
                        width: 24px;
                        height: 24px;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                        border-radius: 50%;
                        font-weight: bold;
                    ">${marker.label}</div>`,
                    anchor: new naver.maps.Point(12, 12),
                },
            });
        });

        if (selectedMarkerId) {
            const selectedMarker = markers.find(marker => marker.id === selectedMarkerId);
            if (selectedMarker) {
                map.setCenter(new naver.maps.LatLng(selectedMarker.lat, selectedMarker.lng));
                map.setZoom(15);
            }
        }
    }, [markers, selectedMarkerId]);

    return <div id="map" style={{ width: "100%", height: "400px", marginBottom: "20px" }} />;
}
