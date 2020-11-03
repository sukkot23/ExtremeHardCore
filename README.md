
[![](https://jitpack.io/v/sukkot23/ExtremeHardCore.svg)](https://jitpack.io/#sukkot23/ExtremeHardCore)   



# ExtremeHardCore
Twitch Streamer DongWooDong's Minecraft Extreme Hard Core Contents Project.   
   
**트위치 스트리머 동우동님의 극 하드코어 컨텐츠 프로젝트**   
   > [동우동 트위치](https://www.twitch.tv/dongwoodong)
</br>

```
 시즌1 제작기간 : 2020.02.08 ~ 2020.04.13
 시즌2 제작기간 : 2020.10.08 ~ 2020.11.04
```
</br>

## Thumbnail
![2020-07-26_14 18 56](https://user-images.githubusercontent.com/56511728/98035163-688f2180-1e5b-11eb-9abc-0f18b1088219.png)
</br>

##### 본 플러그인 제작에 Protocolib와 DiscordJDA 라이브러리를 사용하였습니다.
> [Protocollib](https://github.com/dmulloy2/ProtocolLib/)   
> [DiscordJDA](https://github.com/DV8FromTheWorld/JDA)
</br>

### 1. R-ExtremeHardCore
  ##### 극하드코어 컨텐츠 메인 플러그인 입니다.
```
명령어
   /edit help : 명령어 메뉴얼을 확인합니다
   /edit tiger : '호랭이 털'을 지급합니다
   /edit try (count) : 게임 도전횟수를 조정합니다
   /edit life (count) : 게임 라이프를 조정합니다
   /edit game (state) : 게임 상태를 조정합니다
   /edit player [name] : 플레이어 데이터를 조정합니다.
   
게임상태
   state : 게임 시작 여부 (TRUE : 게임시작 / FALSE : 게임중지)
   reset : 서버 리셋 여부 (TRUE : 종료시 리셋 / FALSE : 리셋 안함)
   retry : 게임 재시작 여부 (TRUE : 사망시 게임 재시작 / FALSE : 게임 재시작 안함)
```
</br>

### 2. R-RandomTeam
  ##### 랜덤으로 팀을 지정해주는 플러그인 입니다.
```
명령어
   /rteam : 모든 유저에게 랜덤으로 팀을 지정해줍니다
```
</br>

### 3. R-PenaltySystem
  ##### 사망 시 패널티 효과를 주는 플러그인 입니다.
</br>

### 4. R-TheEnd
  ##### 엔더월드와 엔더드래곤에게 특별한 이펙트를 주는 플러그인 입니다.
```
 - 엔더 월드 입장 시 타이핑 메세지 출력
 - 엔더 드래곤 BossBar에 체력을 표시
 - 플레이어가 엔더드래곤에게 근접공격 시 공격 무효화
 - 발사체(화살, 분광화살, 삼지창)으로 공격가능
 - 발사체를 방어하는 페이즈에서도 공격 가능
 - 드래곤 사망 시 특별한 이벤트 발생
```
</br>

### 5. R-HardCoreRule
  ##### 극하드코어 전용 룰을 설정하는 플러그인 입니다.
```
명령어
   /rule help : 명령어 메뉴얼을 확인합니다.
   /rule state : 게임 룰 상태를 확인합니다 (GameRule이 아닌 HardCore Rule)
   /rule protectVillage : 마을 보호 룰을(를) 조정합니다 (TRUE: 마을 보호, FALSE: 마을 비보호)
   /rule protectVillager : 주민 보호 룰을(를) 조정합니다 (TRUE: 주민 보호, FALSE: 주민 비보호)
   /rule protectBed : 침대 사용 방지 룰을(를) 조정합니다 (TRUE: 침대 사용 불가능, FALSE: 침대 사용 가능)
   /rule burningOut : 3초 무적 방지 룰을(를) 조정합니다 (TRUE: 3초 무적시 강제퇴장, FALSE: 강제퇴장 비활성화)
   /rule nightJoin : 야간 접속 룰을(를) 조정합니다 (TRUE: 야간에 접속 가능, FALSE: 야간에 접속 불가능)
   /rule timeOut : 1시간 초과 룰을(를) 조정합니다 (TRUE: 중도참여 불가능, FALSE: 중도참여 가능)
   /rule runTime : 1시간 타이머를 설정합니다
   /rule clear : 게임 룰을 초기값으로 바꿉니다
   /rule staff : 서버를 점검하는 모드로 변경합니다 (TRUE: GM만 입장가능, FALSE: 모든유저 입장가능)
   
   * 마을의 경우 주민을 기준으로 반경 30블럭을 마을로 취급합니다.
   * 3초 무적의 경우 FireTick이 1이상일 경우 (플레이어가 불이 붙은경우) 강제퇴장 처리됩니다.
```
</br>

### 6. R-ServerStatistics
  ##### 컨텐츠 간 통계수치를 기록하는 플러그인 입니다.
```
명령어
   /phase (1~5) : 현재 게임의 진행도를 변경합니다
   
진행도
   1 : 생존(집 짓기 + 다양한 자원 확보) + 지옥문 건설
   2 : 참가자 전원 철 장비 확보 + 네더 유적 탐색 + 블레이드 막대 구하기
   3 : 엔더의 눈 수집 및 엔더 포탈 탐색
   4 : 엔더드래곤 처치
```
</br>


### 7. R-DiscordBeco
  ##### 우동님의 친구 배코가 자동으로 화이트리스트를 등록해주는 플러그인 입니다.
```
 * 토큰 유출을 방지하기 위해 4.0.0 이상부터 Config에 토큰을 기입해야 됩니다.
 * 화이트리스트 양식은 다음과 같습니다
 
    [마인크래프트 아이디/트위치 아이디]
    
```
</br>
