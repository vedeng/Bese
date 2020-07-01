package com.bese.widget.numeditor

import android.content.Context
import android.graphics.Color
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.bese.R
import com.blankj.utilcode.util.SizeUtils
import kotlin.math.min

/**
 * 购物车商品数量、增加和减少控制按钮
 */
class NumEditor @JvmOverloads constructor(private val mCtx: Context, var attrs: AttributeSet? = null, var defStyleAttr: Int = 0)
    : LinearLayout(mCtx, attrs, defStyleAttr), View.OnClickListener, TextWatcher {

    companion object {
        private const val MIN_LIMIT = 1
        private const val MAX_LIMIT = 9999
        private const val SOURCE_LIMIT = MAX_LIMIT
    }

    /** 最大购买数量限制条件 */
    private var mBuyMax = MAX_LIMIT
    /** 最大购买数量限制条件 */
    private var mBuyMin = MIN_LIMIT
    /** 库存数量限制条件 */
    private var inputAreaSize = getDp(50f)
    /** 库存数量限制条件 */
    private var inventoryLimit = mBuyMax
    /**
     * 库存对数量编辑的限制开关   false为关 默认false
     */
    var inventoryFlag = true

    private var mCount: EditText? = null
    private var addButton: TextView? = null
    private var subButton: TextView? = null
    private var focusLayout: View? = null

    private var mOnWarnListener: OnWarnListener? = null

    private var mEditListener: OnEditListener? = null

    private val maxLimitNum: Int
        get() = min(mBuyMax, inventoryLimit)

    val number: Int
        get() {
            try {
                var n = mCount?.text.toString()
                if (n.isEmpty()) { n = mBuyMin.toString() }
                return n.toInt()
            } catch (e: NumberFormatException) {
                e.printStackTrace()
            }
            // 正常情况不会走到这一行
            setNum(mBuyMin)
            return mBuyMin
        }

    private fun getDp(dpValue: Float): Int {
        val scale = mCtx.resources.displayMetrics.density
        return (dpValue * scale + 0.5f).toInt()
    }

    override fun onFinishInflate() {
        super.onFinishInflate()
        init(mCtx, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        LayoutInflater.from(context).inflate(R.layout.layout_num_editor, this)
        addButton = findViewById(R.id.btn_add)
        addButton?.setOnClickListener(this)
        subButton = findViewById(R.id.btn_sub)
        subButton?.setOnClickListener(this)
        focusLayout = findViewById(R.id.request_focus_layout)

        mCount = findViewById(R.id.et_cnt)
        mCount?.addTextChangedListener(this)

        mCount?.setText(mBuyMin.toString())

        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.NumEditor, defStyleAttr, 0)
        val editable = typedArray.getBoolean(R.styleable.NumEditor_editable, true)
        val minNum = typedArray.getInt(R.styleable.NumEditor_inputMinNum, MIN_LIMIT)
        val maxNum = typedArray.getInt(R.styleable.NumEditor_inputMaxNum, MAX_LIMIT)
        val sourceNum = typedArray.getInt(R.styleable.NumEditor_inputSourceNum, SOURCE_LIMIT)
        val numberMinWidth = typedArray.getDimensionPixelSize(R.styleable.NumEditor_inputMinWidth, inputAreaSize)
        typedArray.recycle()

        isClickable = editable

        setNumLimit(min = if (minNum < 0) 1 else minNum, max = maxNum, source = sourceNum)

        setInputAreaWidth(numberMinWidth)

        setNum(mBuyMin)

        mCount?.setOnFocusChangeListener { _, _ ->
            mCount?.post {
                var n = mCount?.text.toString()
                if (n.isEmpty()) { n = mBuyMin.toString() }
                if (n.toInt() < mBuyMin) setNum(n.toInt())
                mCount?.setSelection(mCount?.text.toString().length)
            }
        }
    }

    fun resetFocus() {
        var str = mCount?.text.toString()
        if (TextUtils.isEmpty(str)) {
            str = mBuyMin.toString()
        }
        setNum(str.toInt())
        mCount?.clearFocus()
        focusLayout?.requestFocus()
    }

    /**
     * 编辑
     *
     * @param pre  变化之前的值
     * @param dest 目标值
     */
    fun edit(pre: Int, dest: Int) {
        mEditListener?.onEdit(pre, dest)
    }

    fun setPadding(paddingH: Int, paddingV: Int) {
        addButton?.setPadding(paddingH, paddingV, paddingH, paddingV)
        subButton?.setPadding(paddingH, paddingV, paddingH, paddingV)
    }

    fun setButtonWidth(buttonWidth: Int) {
        if (buttonWidth > 0) {
            val textParams = LayoutParams(buttonWidth, LayoutParams.MATCH_PARENT)
            subButton?.layoutParams = textParams
            addButton?.layoutParams = textParams
        }
    }

    /**
     * 设置输入区域的宽度，默认50dp
     */
    fun setInputAreaWidth(numberWidth: Int) {
        if (numberWidth > 0) {
            val textParams = LayoutParams(numberWidth, LayoutParams.MATCH_PARENT)
            val m = SizeUtils.dp2px(1f)
            textParams.setMargins(m, 0, m, 0)
            mCount?.layoutParams = textParams
        }
    }

    override fun onClick(v: View) {
        val id = v.id
        val count = number
        if (id == R.id.btn_sub) {
            if (count > mBuyMin) {
                setNum(count - 1)           //正常减
            }
            edit(count, count - 1)
        } else if (id == R.id.btn_add) {
            when {
                count < maxLimitNum -> {
                    setNum(count + 1)           //正常添加
                }
                inventoryLimit < mBuyMax -> {
                    warningForInventory()           //库存不足
                }
                else -> {
                    warningForBuyMax()          //超过最大购买数
                }
            }
            edit(count, count + 1)
        }
    }

    /**
     * 超过的库存限制
     * Warning for inventory.
     */
    private fun warningForInventory() {
        mOnWarnListener?.onWarningForInventory(inventoryLimit)
    }

    /**
     * 超过的最大购买数限制
     * Warning for buy max.
     */
    private fun warningForBuyMax() {
        mOnWarnListener?.onWarningForBuyMax(mBuyMax)
    }

    /**
     * 设置EditText显示数字，并置光标于末尾
     *
     * @param num 数字
     */
    fun setNum(num: Int?) {
        num?.run {
            if (num < mBuyMin) {
                mCount?.setText(mBuyMin.toString())
            } else if (num > mBuyMax) {
                mCount?.setText(mBuyMax.toString())
            } else {
                mCount?.setText(num.toString())
            }
            mCount?.setSelection(mCount?.text?.length ?: 0)

            setAddEnable(num < maxLimitNum)
            setSubEnable(num > mBuyMin)
        }
    }

    fun getEditText(): EditText? {
        return mCount
    }

    fun setNumLimit(min: Int = 1, max: Int = mBuyMax, source: Int = max) {
        mBuyMin = min
        mBuyMax = max
        inventoryLimit = if (inventoryFlag) source else max
        var n = mCount?.text.toString()
        if (n.isEmpty()) {
            n = mBuyMin.toString()
        }
        setNum(n.toInt())
    }

    /**
     * 重置最小值方法不推荐使用
     *   或仅推荐在未定义步进器数值前使用
     *   设置此方法会连带把数值设为最小值。
     *   一般在不确定最小值时采用。
     */
    fun setNumMinLimit(minNum: Int) {
        // 如果下限已经和设置值等同，就不执行。因为执行重置下限会初始化输入框的值
        if (mBuyMin != minNum) {
            mBuyMin = minNum
            var n = mCount?.text.toString()
            if (n.isEmpty()) {
                n = mBuyMin.toString()
            }
            setNum(n.toInt())
        }
    }

    fun setOnWarnListener(onWarnListener: OnWarnListener) {
        mOnWarnListener = onWarnListener
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}

    override fun afterTextChanged(s: Editable) {
        if (!TextUtils.isEmpty(s.toString())) {
            if (mBuyMin != 0 && s.toString().startsWith("0")) {
                mCount?.setText(s.toString().substring(1))
            }
            val thisNum = Integer.parseInt(s.toString())
            val limit = maxLimitNum
            if (thisNum >= limit) {
                setAddEnable(false)
                setSubEnable(true)
                if (thisNum != limit) {             //超过了数量
                    setNum(limit)
                    if (inventoryLimit < mBuyMax) {
                        warningForInventory()               //库存不足
                    } else {
                        warningForBuyMax()                  //超过最大购买数
                    }
                }
            } else {
                setAddEnable(true)
                setSubEnable(thisNum > mBuyMin)
                if (mBuyMin != 0 && "0" == s.toString()) { s.clear() }
            }
        } else {
            setAddEnable(true)
            setSubEnable(true)
        }
    }

    private fun setAddEnable(isEnable: Boolean) {
        addButton?.run {
            if (isEnable) {
                setTextColor(Color.parseColor("#333333"))
                isClickable = true
                isEnabled = true
            } else {
                setTextColor(Color.parseColor("#cccccc"))
                isClickable = false
                isEnabled = false
            }
        }
    }

    private fun setSubEnable(isEnable: Boolean) {
        subButton?.run {
            if (isEnable) {
                setTextColor(Color.parseColor("#333333"))
                isClickable = true
                isEnabled = true
            } else {
                setTextColor(Color.parseColor("#cccccc"))
                isClickable = false
                isEnabled = false
            }
        }
    }

    interface OnWarnListener {
        /** 库存超限方法 */
        fun onWarningForInventory(inventory: Int)
        /** 数量超限方法 */
        fun onWarningForBuyMax(max: Int)
    }

    interface OnEditListener {
        /** 编辑 */
        fun onEdit(pre: Int, dest: Int)
    }

    fun setOnEditListener(listener: OnEditListener) {
        mEditListener = listener
    }

}
