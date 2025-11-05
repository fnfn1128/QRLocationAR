package com.example.arlocationqr.routes

/** 경로 세그먼트 타입 */
sealed class Seg {
    /** 직선 이동 (m 단위) */
    data class Straight(val meters: Double) : Seg()
    /** 회전 (좌/우, 각도(+좌회전 / -우회전)) */
    data class Turn(val yawDeltaDeg: Float) : Seg()
    /** 계단: 수평(참) → 경사(연속 계단) → 수평(참) 같은 식을 조합해서 사용
     *  risePerStepM: 한 계단 높이(m), steps: 계단 개수, runPerStepM: 디딤면 깊이(m)
     *  앞뒤의 수평 구간은 Straight 세그먼트로 별도 넣기
     */
    data class Stairs(
        val risePerStepM: Double,
        val runPerStepM: Double,
        val steps: Int
    ) : Seg()
}

/** 도착지 이름 → 세그먼트 리스트(시작점은 A구역, 단위 m) */
object Routes {

    // 편의 상: 계단 치수 공통값
    private const val RISE_FIRST = 0.13   // 첫 칸 높이 13cm
    private const val RISE_NORMAL = 0.14  // 이후 14cm
    private const val RISE_SMALL = 0.12   // 일부 12cm
    private const val RUN_DEFAULT = 0.26  // 디딤면(깊이) 26cm 가정 (필요시 조정)

    /** 2층 화장실 */
    val TOILET_2F: List<Seg> = buildList {
        // 1층 → 첫 코너까지
        add(Seg.Straight(9.64))                      // 직진 9.64m
        add(Seg.Straight(1.31))                      // 첫 참(수평) 1.31m
        add(Seg.Stairs(RISE_NORMAL, RUN_DEFAULT, 6)) // 14cm × 6
        add(Seg.Straight(1.89))                      // 1번째 경사 길이(근사) 1.89m
        add(Seg.Turn(+90f))                          // 왼쪽 회전
        add(Seg.Straight(1.34))                      // 두번째 참 1.34m
        add(Seg.Stairs(RISE_NORMAL, RUN_DEFAULT, 15))// 14cm × 15 (첫칸 포함이라고 했으니 15로 수렴)
        add(Seg.Straight(5.15))                      // 2번째 경사 길이 5.15m
        add(Seg.Turn(+90f))                          // 왼쪽 회전
        // 세 번째 계단 묶음
        add(Seg.Stairs(RISE_SMALL, RUN_DEFAULT, 3))  // 12cm × 3
        add(Seg.Straight(1.04))                      // 3번째 경사 길이 1.04m
        add(Seg.Straight(2.14))                      // 3번째 참 2.14m
        add(Seg.Straight(1.34))                      // 직진 1.34m
        add(Seg.Turn(+90f))                          // 왼쪽 회전
        add(Seg.Straight(5.30))                      // 직진 5.3m
        add(Seg.Turn(-90f))                          // 오른쪽 회전
        add(Seg.Straight(6.44))                      // 직진 6.44m
        add(Seg.Turn(+90f))                          // 왼쪽 회전
        add(Seg.Straight(5.10))                      // 직진 5.1m
        // 마지막 복도 너비 1.41m 참고: 중앙 정렬용이므로 별도 세그는 필요 없음
    }

    /** 2층 프린트실 */
    val PRINT_ROOM: List<Seg> = buildList {
        // 2층 화장실 경로와 초반 동일
        addAll(TOILET_2F.takeWhile { true }) // 복사 시작 (가독성 위해 아래로 교체)
    }.let {
        // 위의 takeWhile 은 IDE 가 싫어할 수 있으니, 실제로는 아래처럼 그대로 적는 걸 권장
        buildList {
            add(Seg.Straight(9.64))
            add(Seg.Straight(1.31))
            add(Seg.Stairs(RISE_NORMAL, RUN_DEFAULT, 6))
            add(Seg.Straight(1.89))
            add(Seg.Turn(+90f))
            add(Seg.Straight(1.34))
            add(Seg.Stairs(RISE_NORMAL, RUN_DEFAULT, 15))
            add(Seg.Straight(5.15))
            add(Seg.Turn(+90f))
            add(Seg.Stairs(RISE_SMALL, RUN_DEFAULT, 3))
            add(Seg.Straight(1.04))
            add(Seg.Straight(2.14))
            add(Seg.Straight(1.34))
            add(Seg.Turn(+90f))
            add(Seg.Straight(5.30))
            add(Seg.Turn(-90f))
            add(Seg.Straight(19.36)) // 프린트실은 이 구간이 길다
            // 복도 너비 2.91m → 중앙선 정렬 목적
        }
    }

    /** 2층 회의실 */
    val MEETING_ROOM: List<Seg> = buildList {
        // 프린트실 경로 + 추가 구간
        add(Seg.Straight(9.64))
        add(Seg.Straight(1.31))
        add(Seg.Stairs(RISE_NORMAL, RUN_DEFAULT, 6))
        add(Seg.Straight(1.89))
        add(Seg.Turn(+90f))
        add(Seg.Straight(1.34))
        add(Seg.Stairs(RISE_NORMAL, RUN_DEFAULT, 15))
        add(Seg.Straight(5.15))
        add(Seg.Turn(+90f))
        add(Seg.Stairs(RISE_SMALL, RUN_DEFAULT, 3))
        add(Seg.Straight(1.04))
        add(Seg.Straight(2.14))
        add(Seg.Straight(1.34))
        add(Seg.Turn(+90f))
        add(Seg.Straight(5.30))
        add(Seg.Turn(-90f))
        add(Seg.Straight(19.36))
        add(Seg.Straight(3.66))
        add(Seg.Turn(+90f))
        add(Seg.Straight(4.68))
    }

    /** 선택한 도착지 키워드로 경로 얻기 */
    fun byDestination(name: String): List<Seg> = when {
        name.contains("화장실") -> TOILET_2F
        name.contains("프린트") -> PRINT_ROOM
        name.contains("회의실") -> MEETING_ROOM
        else -> TOILET_2F // 기본값
    }
}
