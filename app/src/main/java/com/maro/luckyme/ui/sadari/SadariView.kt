package com.maro.luckyme.ui.sadari

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.MotionEvent.ACTION_UP
import android.view.View
import androidx.core.content.ContextCompat
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import com.maro.luckyme.R
import com.maro.luckyme.ui.sadari.data.Branch
import com.maro.luckyme.ui.sadari.data.Constants
import com.maro.luckyme.ui.sadari.data.Constants.DEFAULT_BOMB_COUNT
import com.maro.luckyme.ui.sadari.data.Constants.DEFAULT_PLAYER_COUNT
import com.maro.luckyme.ui.sadari.data.Constants.DIRECTION_RIGHT
import com.maro.luckyme.ui.sadari.data.Constants.SPEED
import com.maro.luckyme.ui.sadari.data.Constants.STATUS_STARTED
import com.maro.luckyme.ui.sadari.data.Constants.STATUS_WAITING
import com.maro.luckyme.ui.sadari.data.Constants.TOTAL_BRANCH_COUNT
import com.maro.luckyme.ui.sadari.data.DataHelper
import com.maro.luckyme.ui.sadari.data.Stream
import java.util.*


/**
 *  KKODARI는 사다리 최상단, 최하단 부분을 뜻함
 *
 * |  |  | <= KKODARI
 * |--|  |
 * |  |--|
 * |--|  |
 * |  |  | <= KKODARI
 */


// XXX 사다리 정보 만들 때 player 정보와 결과 정보 미리 다 만들어 놓자

class SadariView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {
    val TAG = SadariView::class.simpleName

    val DP16 = resources.getDimensionPixelSize(R.dimen.dp16)
    val DP14 = resources.getDimensionPixelSize(R.dimen.dp14)
    val DP10 = resources.getDimensionPixelSize(R.dimen.dp10)
    val DP0_5 = resources.getDimensionPixelSize(R.dimen.dp0_5)
    val DP1 = resources.getDimensionPixelSize(R.dimen.dp1)
    val DP8 = resources.getDimensionPixelSize(R.dimen.dp8)
    val DP20 = resources.getDimensionPixelSize(R.dimen.dp20)
    val DP24 = resources.getDimensionPixelSize(R.dimen.dp24)

    val CELL_WIDTH = resources.getDimensionPixelSize(R.dimen.sadari_cell_width)
    val CELL_HEIGHT = resources.getDimensionPixelSize(R.dimen.sadari_cell_height)

    val KKODARI = CELL_HEIGHT * 2 // 상,하 사다리 여분

    val PLAYER_WIDTH = resources.getDimensionPixelSize(R.dimen.sadari_player_width)
    val PLAYER_WIDTH_SMALL = resources.getDimensionPixelSize(R.dimen.sadari_player_width_s)
    val BOMB_WIDTH = resources.getDimensionPixelSize(R.dimen.sadari_bomb_width)
    val PLAYER_LIST = mutableListOf<VectorDrawableCompat>().apply {
        add(VectorDrawableCompat.create(resources, R.drawable.ic_rat, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_cow, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_tiger, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_rabbit, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_dragon, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_snake, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_horse, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_sheep, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_monkey, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_chicken, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_dog, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_pig, null)!!)
    }

    val PLAYER_HIT_LIST = mutableListOf<VectorDrawableCompat>().apply {
        add(VectorDrawableCompat.create(resources, R.drawable.ic_rat, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_cow, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_tiger, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_rabbit, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_dragon, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_snake, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_horse, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_sheep, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_monkey, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_chicken, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_dog, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.ic_pig, null)!!)
    }

    val BOMB_LIST = mutableListOf<VectorDrawableCompat>().apply {
        add(VectorDrawableCompat.create(resources, R.drawable.bomb1, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.bomb2, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.bomb3, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.bomb4, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.bomb5, null)!!)
        add(VectorDrawableCompat.create(resources, R.drawable.bomb6, null)!!)
    }

    val COLOR_LIST = mutableListOf<Int>().apply {
        add(R.color.rat)
        add(R.color.cow)
        add(R.color.tiger)
        add(R.color.rabbit)
        add(R.color.dragon)
        add(R.color.snake)
        add(R.color.horse)
        add(R.color.sheep)
        add(R.color.monkey)
        add(R.color.chicken)
        add(R.color.dog)
        add(R.color.pig)
    }

    val STROKE_WIDTH = resources.getDimensionPixelSize(Constants.STROKE_WIDTH)

    var playerCount: Int = DEFAULT_PLAYER_COUNT
    var bombCount: Int = DEFAULT_BOMB_COUNT

    var viewStartX = 0 // view의 시작 위치

    lateinit var paint: Paint
    lateinit var paint2: Paint
    lateinit var animPaint: Paint
    lateinit var hitPaint: Paint
    lateinit var bombPaint: Paint


    lateinit var sadari: LinkedList<Stream>
    lateinit var bombIndexList: List<Int>
    var playerResultMap: MutableMap<Int, PlayerResult> = mutableMapOf()

    // 애니메이션 처리
    var _matrix: Matrix = Matrix()
    var animPath: Path = Path()
    var curX: Float = 0f
    var curY: Float = 0f
    var pos: FloatArray = FloatArray(2)
    var tan: FloatArray = FloatArray(2)

    // 게임 상태
    var playStatus: Int = STATUS_WAITING
        get() = field
        set(value) {
            listener?.onChanged(value!!)
            field = value
        }

    var listener: OnSadariStatusChangedListener? = null

    init {
        initView()
    }

    fun setOnSadariStatusChangedListener(listener: OnSadariStatusChangedListener) {
        this@SadariView.listener = listener
    }

    private fun initView() {
        paint = Paint().apply {
            color = ContextCompat.getColor(context, R.color.indigo_200)
            strokeWidth = STROKE_WIDTH.toFloat()
            style = Paint.Style.FILL
        }
        paint2 = Paint().apply {
            color = ContextCompat.getColor(context, R.color.sadari_bg)
            strokeWidth = STROKE_WIDTH.toFloat()
            style = Paint.Style.FILL
        }

        animPaint = Paint().apply {
            color = ContextCompat.getColor(context, R.color.teal_200)
            strokeWidth = STROKE_WIDTH.toFloat()
            style = Paint.Style.STROKE
            strokeJoin = Paint.Join.BEVEL
            strokeCap = Paint.Cap.SQUARE
        }

        hitPaint = Paint().apply {
            color = ContextCompat.getColor(context, R.color.white)
            style = Paint.Style.FILL
            textSize = resources.getDimensionPixelSize(R.dimen.dp12).toFloat()
        }

        bombPaint = Paint().apply {
            color = ContextCompat.getColor(context, R.color.red)
            style = Paint.Style.FILL
            textSize = resources.getDimensionPixelSize(R.dimen.dp12).toFloat()
        }

        setOnTouchListener { v, event ->
            processTouchEvent(v, event)
        }
    }

    fun setData(playerCount: Int, bombCount: Int) {
        this@SadariView.playerCount = playerCount
        this@SadariView.bombCount = bombCount

        play()
    }

    fun play() {
        sadari = DataHelper.makeSadariData(playerCount)
        bombIndexList = DataHelper.makeBombIndexList(playerCount, bombCount)
        playerResultMap.clear()
        playStatus = STATUS_WAITING

        requestLayout()
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        var point = Point()
        display.getSize(point)

        // Width
        var width = CELL_WIDTH * playerCount + DP16 + DP16
        if (point.x > width) {
            viewStartX = (point.x - width) / 2 + DP10 + DP0_5 // XXX 임의의 값을 더해줌 (전체 크기 바뀔 때마다 수동 조절해야 함)
            width = point.x
        } else {
            viewStartX = DP16
        }

        // Height
        var height = PLAYER_WIDTH + DP8 + KKODARI + CELL_HEIGHT * TOTAL_BRANCH_COUNT + KKODARI + DP8 + DP20 * 2 + DP24 // bomb의 DP20

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let {
            sadari?.let {
                drawSadari(canvas)
                drawPlayer(canvas)
                drawBomb(canvas)
                drawAnimPath(canvas)
                drawStartButton(canvas)
            }
        }
    }

    private fun drawSadari(canvas: Canvas) {
        var sadariStartX = viewStartX + (PLAYER_WIDTH / 2).toFloat()
        var sadariStartY = (PLAYER_WIDTH + DP8).toFloat()
        var sadariEndY = sadariStartY + KKODARI + CELL_HEIGHT * TOTAL_BRANCH_COUNT + KKODARI

        sadari?.forEachIndexed { index, stream ->
            // 세로선
            var x = sadariStartX + CELL_WIDTH * index
            canvas?.drawLine(x, sadariStartY, x, sadariEndY, paint)

            // 가로선
            stream.branchList?.forEach { branch ->
                // 현재 stream의 오른쪽 branch만 그리면 모두 다 그릴 수 있다.
                // (다음 stream이 왼쪽 branch는 현재 stream의 오른쪽 branch와 같으므로...)
                if (branch.direction == DIRECTION_RIGHT) {
                    var y = sadariStartY + KKODARI + branch.position * CELL_HEIGHT
                    var startX = sadariStartX + index * CELL_WIDTH
                    var endX = startX + CELL_WIDTH
                    canvas?.drawLine(startX, y, endX, y, paint)
                }
            }
        }
    }

    private fun drawPlayer(canvas: Canvas) {
        for (i in 0..playerCount - 1) {
            var x = viewStartX + CELL_WIDTH * i
            PLAYER_LIST[i].apply {
                setBounds(x, 0, x + PLAYER_WIDTH, PLAYER_WIDTH)
                draw(canvas)
            }
        }
    }

    private fun drawBomb(canvas: Canvas) {
        var sadariStartY = PLAYER_WIDTH + DP8
        var sadariEndY = sadariStartY + KKODARI + CELL_HEIGHT * TOTAL_BRANCH_COUNT + KKODARI

        var bombIndex = 0
        for (i in 0..playerCount - 1) {
            var x = viewStartX + (CELL_WIDTH * i).toFloat() + DP14 // XXX 임의 조정
            var y = sadariEndY + DP24 // DP24는 전체 크기에 영향을 미침

            if (bombIndexList.contains(i)) {
                BOMB_LIST[bombIndex].apply {
                    setBounds(x.toInt(), y, x.toInt() + BOMB_WIDTH, y + BOMB_WIDTH)
                    draw(canvas)
                }
                bombIndex++
            }
        }
    }

    private fun drawAnimPath(canvas: Canvas) {
        if (playerResultMap.isEmpty()) {
            Log.e("XXX", "===1> animPath=${animPath.isEmpty}")
            return
        }

        _matrix.reset()
        for ((index, playerResult) in playerResultMap) {
            playerResult.pathMeasure?.getPosTan(playerResult.distance!!, pos, tan)

            if (pos[1] > 1110f) {
                playerResult.completed = true
            }

            Log.e("XXX", "===> animPath=${animPath.isEmpty}, pos0=${pos[0]}, pos1=${pos[1]}")
            curX = viewStartX + pos[0] - PLAYER_WIDTH_SMALL / 2
            curY = pos[1] - PLAYER_WIDTH_SMALL / 2
            _matrix.postTranslate(curX!!, curY!!)
            if (playerResult.distance == 0f) {
                playerResult.animPath.moveTo(viewStartX + pos[0], pos[1])
            } else {
                playerResult.animPath.lineTo(viewStartX + pos[0], pos[1])
            }

            animPaint.color = ContextCompat.getColor(context, COLOR_LIST[index])
            canvas.drawPath(playerResult.animPath, animPaint)
            playerResult.icon?.apply {
                setBounds(curX.toInt(), curY.toInt(), curX.toInt() + PLAYER_WIDTH_SMALL, curY.toInt() + PLAYER_WIDTH_SMALL)
                draw(canvas)
            }

            playerResult.distance = playerResult.distance + SPEED
//            Log.e("XXX", "===> distance=${playerResult.distance}")
        }

        invalidate()
    }

    private fun drawStartButton(canvas: Canvas) {
        if (playStatus != STATUS_WAITING) {
            return
        }

        var rect = getSadariRect()
        var left = rect.left.toFloat()
        var right = rect.right.toFloat()
        var top = rect.top.toFloat()
        var bottom = rect.bottom.toFloat()

        var margin = DP24

        canvas.drawRect(left - margin, top + margin, right + margin, bottom - margin, paint2)
    }

    private fun processTouchEvent(v: View, event: MotionEvent): Boolean {
        if (event.action == ACTION_UP) {
            when (playStatus) {
                STATUS_WAITING -> {
//                    var buttonRect = getStartButtonRect()
//                    if (buttonRect.left <= event.x && buttonRect.right >= event.x
//                            && buttonRect.top <= event.y && buttonRect.bottom >= event.y) {
//                        playStatus = STATUS_STARTED
//                        invalidate()
//                    }
                }
                STATUS_STARTED -> {
                    PLAYER_LIST.forEachIndexed { index, player ->
                        if (player.bounds.left <= event.x && player.bounds.right >= event.x
                                && player.bounds.top <= event.y && player.bounds.bottom >= event.y) {

                            if (!playerResultMap.containsKey(index)) {
                                playPlayer(index)
                            }

                            invalidate()
                        }
                    }
                }
            }
        }
        return true
    }

    private fun playPlayer(index: Int) {
        if (playerResultMap.containsKey(index)) {
            return
        }

        playerResultMap.put(index, PlayerResult(
                PathMeasure(branchToPath(DataHelper.getPlayerPathList(sadari, index), index), false),
                PLAYER_HIT_LIST[index],
                index = index
        ))
    }

    fun playAll() {
        for (i in 0..playerCount - 1) {
            playPlayer(i)
        }
        invalidate()
    }

    private fun branchToPath(branchList: List<Branch>, playerIndex: Int): Path {
        // XXX 중복 코드
        var sadariStartX = (PLAYER_WIDTH / 2).toFloat()
        var sadariStartY = (PLAYER_WIDTH + DP8).toFloat()
        var sadariEndY = sadariStartY + KKODARI + CELL_HEIGHT * TOTAL_BRANCH_COUNT + KKODARI

        var curX = sadariStartX + CELL_WIDTH * playerIndex
        var curY = sadariStartY
        var prePosition = 0
        var path = Path().apply {
            moveTo(curX, curY)
            curY = curY + KKODARI
            lineTo(curX, curY)
            branchList.forEach {
                curY = curY + CELL_HEIGHT * (it.position - prePosition)
                lineTo(curX, curY)
                curX = curX + CELL_WIDTH * if (it.direction == DIRECTION_RIGHT) 1 else -1
                lineTo(curX, curY)
                prePosition = it.position
            }
            curY = sadariEndY
            lineTo(curX, curY)
        }

        return path
    }

    private fun getSadariRect(): Rect {
        return Rect().apply {
            top = PLAYER_WIDTH + DP8
            bottom = top + KKODARI + CELL_HEIGHT * TOTAL_BRANCH_COUNT + KKODARI
            left = viewStartX + PLAYER_WIDTH / 2
            right = left + CELL_WIDTH * (playerCount - 1)
        }
    }

    fun startSadari() {
        playStatus = STATUS_STARTED
        invalidate()
    }
}

data class PlayerResult(
        var pathMeasure: PathMeasure,
        var icon: VectorDrawableCompat,
        var distance: Float = 0f,
        var animPath: Path = Path(),
        var completed: Boolean = false,
        var index: Int
)