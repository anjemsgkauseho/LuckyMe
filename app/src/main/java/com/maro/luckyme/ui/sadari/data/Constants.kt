package com.maro.luckyme.ui.sadari.data

import com.maro.luckyme.R

object Constants {
    // 설정
    val DEFAULT_PLAYER_COUNT = 6
    val DEFAULT_BOMB_COUNT = 1

    val MIN_BRANCH_COUNT = 3 // 사다리 한칸 최소 브랜치 수
    val MAX_BRANCH_COUNT = 6 // 사다리 한칸 최대 브랜치 수
    val TOTAL_BRANCH_COUNT = 10 // 사다리 한칸 브랜치 총 수

    val STROKE_WIDTH = R.dimen.sadari_stroke_width

    val SPEED = 10 // 애미메이션 속도

    // 상수
    val DIRECTION_LEFT = 0
    val DIRECTION_RIGHT = 1

    val MIN_PLAYER_COUNT = 2
    val MAX_PLAYER_COUNT = 12

    val MIN_BOMB_COUNT = 1
    val MAX_BOMB_COUNT = MAX_PLAYER_COUNT / 2

    val STATUS_WAITING = 1
    val STATUS_STARTED = 2
    val STATUS_COMPLETED = 3
}