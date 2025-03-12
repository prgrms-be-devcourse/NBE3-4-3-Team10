# CANBEJ - 일정 공유 & 관리 웹서비스

## 📌 프로젝트 개요
**CANBEJ**는 개인 및 그룹 일정을 효과적으로 공유하고 관리할 수 있는 웹 서비스입니다. 지도 기반 위치 공유 기능을 제공하여, 사용자는 장소와 시간을 한 번에 공유하고 조율할 수 있습니다.

## 👥 팀 소개 - TEAM 10
| 이름  | 역할  | 담당 업무 |
|-------|------|----------|
| 유영주 | 팀장  | 친구 추가, 캘린더 공유 / 캘린더 API, 전체 연결 담당 |
| 박찬호 | 팀원  | 친구 추가, 캘린더 공유 / 캘린더 API, 전체 연결 담당 |
| 황인우 | 팀원  | C.I / 로그인 (Spring Security) 담당 |
| 민태희 | 팀원  | 휴먼 계정 관리 / 로그인 (Spring Security) 담당 |
| 김혜윤 | 팀원  | 챗봇, 채팅 기능 / 스케줄, 지도 API 담당 |
| 권기용 | 팀원  | 챗봇, 채팅 기능 / 스케줄, 지도 API 담당 |
| **JH** | 멘토  | 프로젝트 피드백 및 질의응답 지원 |

* 구현 기능별로 2명씩 짝 프로그래밍을 통해 협업 진행

---

## 🎯 주요 기능
### 🔐 사용자 계정 관리
- **소셜 로그인 (구글, 카카오) 지원**
- **JWT 기반 인증 및 인가 (Refresh Token 사용)**
- **장기 미사용 계정 휴면 처리 (자동화)**
- **닉네임 변경 및 금지어 설정**

### 📅 캘린더 기능
- **개인 및 그룹 캘린더 생성 및 관리**
- **캘린더 별 권한 관리 (소유자만 수정/삭제 가능)**
- **FullCalendar API 연동 (월별/주별 보기 지원)**

### 📌 일정 관리 기능
- **일정 시간 검증 (시작/종료 시간 유효성 검사)**
- **일정 CRUD 기능 (생성, 조회, 수정, 삭제)**
- **일정 상세 정보 조회 (제목, 설명, 위치, 시간 등)**
- **일정 권한 관리 (캘린더 소유자 및 참가자 역할 구분)**

### 🗺️ 지도 연동 (Naver Map API)
- **위치 기반 일정 관리 지원**
- **마커 추가 및 위치 검색 (Geocoding, Reverse Geocoding)**
- **지도에서 일정 위치 시각화**

### 🛡️ 관리자 기능
- **사용자 계정 관리 (검색, 페이징 지원)**
- **계정 잠금 및 복구 기능 (비밀번호 5회 실패 시 계정 잠금, 이메일 인증 필요)**
- **보안 강화 (비밀번호 해싱, JWT 인증 시스템 적용)**

### 🤖 3차 프로젝트 추가 기능
- **GitHub Actions CI/CD 자동화**
  - `develop` 브랜치 PR 시 자동 빌드 및 테스트 실행
  - 코드 병합 후 자동 배포 가능하도록 구성
  - 지속적인 코드 품질 유지 및 배포 시간 단축
- **휴면 계정 자동 처리**
  - 매월 1일 10시에 장기 미사용 계정 자동 휴면 전환 및 안내 메일 발송
  - 일정 및 캘린더 정보 유지하며 계정만 Soft Delete 처리
  - 트랜잭션과 비동기 처리 적용 (Batch 100명 단위 처리)
- **친구 추가 기능 구현**
  - 사용자는 다른 사용자를 검색하여 친구 추가 가능
  - 중복 친구 추가 방지 및 자기 자신 추가 불가 처리
  - 친구 목록 조회 및 친구 삭제 가능
- **실시간 채팅 (WebSocket 활용)**
  - **캘린더 별 채팅방 개설 및 참여 가능**
  - **WebSocket을 활용한 실시간 메시지 송수신**
  - **wsToken 기반 인증 방식 적용**

---

## 📢 향후 보완할 점
- **친구 추가 및 일정 공유 기능 강화**
- **실시간 채팅 기능 추가** (WebSocket 활용)
- **캘린더와의 실시간 연동 기능 강화**
- **자동화된 일정 추천 기능** (chatbotAI 활용 가능성 검토)

---

## 📂 프로젝트 관리 및 문서화
- [📌 프로젝트 관리 Notion](https://www.notion.so/10-1a64873f28dd80268966e65485a70b7f?pvs=4)
- [📑 CANBEJ_발표 ppt](https://github.com/user-attachments/files/19205425/CANBEJ-3.-PPT.pdf)
- [📽️ CANBEJ_시연 영상](https://youtu.be/d9QJ5sefXTc)



---

## 📄 Git Commit Convention
| 태그 이름 | 설명 |
| --- | --- |
| Feat | 새로운 기능 추가 |
| Fix | 버그 수정 |
| Design | CSS 등 UI 디자인 변경 |
| !BREAKING CHANGE | 커다란 API 변경 |
| !HOTFIX | 긴급 버그 수정 |
| Style | 코드 포맷 변경 (기능 변경 없음) |
| Refactor | 프로덕션 코드 리팩토링 |
| Comment | 주석 추가 및 변경 |
| Docs | 문서 수정 |
| Test | 테스트 코드 추가 및 수정 |
| Chore | 빌드 설정 및 패키지 관리 (기능 변경 없음) |
| Rename | 파일/폴더명 변경 |
| Remove | 파일 삭제 |

---

CANBEJ 프로젝트는 **일정 관리와 장소 공유를 한 번에 해결**할 수 있도록 설계되었습니다. 사용자의 **편의성과 보안**을 최우선으로 고려하며, 지속적인 개선을 통해 완성도를 높여 나갈 예정입니다.

