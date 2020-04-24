package com.ved.ui

import android.util.Log
import android.widget.SeekBar
import com.ved.R
import com.ved.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_imageview.*

class DetailImageViewActivity : BaseActivity() {

    override fun loadView() : Int {
        return R.layout.activity_imageview
    }

    override fun init() {
        initTitle("图片展示")
        val baseUseArea = "●部分使用限制图片宽高的场景"

        val baseUseRole = "●直接引用TextButton，定义属性：\n" +
                "●圆角值\n" +
                "●边框粗细（默认0无边框）\n" +
                "●边框颜色\n" +
                "●背景颜色\n" +
                "●按下背景色"

        val baseInteract = "●控件默认含有触摸事件。\n" +
                "●如果定义按下背景色，按下时颜色改变；如果没有设置，按下无感知（有点击事件）"

        val baseStyle = "●可定义圆角大小，圆角非常大时变成粗线形式 \n" +
                "●可定义是否有边框，以及各种颜色"

        initContent(baseUseArea, baseUseRole, baseInteract, baseStyle)

        initListener()
    }

    private fun initListener() {

        /** 解决可横向滑动控件与DrawerLayout的滑动冲突 */
        seek_radius?.setOnTouchListener { _, _ ->
            drawer?.requestDisallowInterceptTouchEvent(true)
            false
        }
        seek_radius?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.e("拖动===", "$progress")
//                img_square?.setRatio(progress / 100f + 0.3f)

                img_square?.setRadius(progress.toFloat())
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) { }
            override fun onStopTrackingTouch(seekBar: SeekBar?) { }
        })

        seek_radius_out?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                Log.e("拖动===", "$progress")
//                img_square?.setRatio(progress / 100f + 0.3f)
                img_square?.setRadius(progress.toFloat() * 2)
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) { }
            override fun onStopTrackingTouch(seekBar: SeekBar?) { }
        })

        ratio_0_5?.setOnClickListener {
            img_square?.setRatio(0.5f)
        }
        ratio_1_0?.setOnClickListener {
            img_square?.setRatio(1f)
        }
        ratio_2_0?.setOnClickListener {
            img_square?.setRatio(2f)
        }
    }

    override fun doExecute() {
        initImage()
    }

    private fun initImage() {
        img_normal?.setImageResource(R.mipmap.seckill_bg)
        img_square?.setImageResource(R.mipmap.seckill_bg)
    }

}
