package com.ved.ui

import android.content.Intent
import android.view.View
import com.bese.widget.toast.XToast
import com.ved.R
import com.ved.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_toast.*


class DetailToastActivity : BaseActivity() {

    override fun loadView() : Int {
        return R.layout.activity_toast
    }

    override fun init() {
        initTitle("吐司")
        val baseUseArea = "●所有使用吐司的场景"

        val baseUseRole = "●直接使用，定义类型：\n" +
                "●在屏幕中心显示时，带图标的吐司，上下结构\n" +
                "●在屏幕底部展示时，纯文本的吐司，可以在文字前附带小图标\n" +
                "●文字颜色\n" +
                "●背景颜色"

        val baseInteract = "●控件默认不可点，展示数秒后消失，动画目前跟随系统吐司动画"

        val baseStyle = "●最大圆角"

        initContent(baseUseArea, baseUseRole, baseInteract, baseStyle)

        findId()
    }

    private fun findId() {
        tb_toast1?.setOnClickListener(this)
        tb_toast2?.setOnClickListener(this)
        tb_toast3?.setOnClickListener(this)
        tb_toast4?.setOnClickListener(this)
        tb_xtoast1?.setOnClickListener(this)
        tb_xtoast2?.setOnClickListener(this)
        tb_xtoast3?.setOnClickListener(this)
        tb_xtoast4?.setOnClickListener(this)
    }

    override fun doExecute() {

    }

    override fun clickEvent(v: View?) {
        when(v?.id) {
            R.id.tb_toast1 -> {
                XToast(this@DetailToastActivity)
                    .setView(R.layout.toast_hint_noicon)
                    .setDuration(3000)
                    .setText("系统吐司样式")
                    .show()
            }
            R.id.tb_toast2 -> {
                XToast(this@DetailToastActivity)
                    .setView(R.layout.toast_hint_noicon)
                    .setDuration(6000)
                    .setText("比系统吐司时间长的样式")
                    .show()
            }
            R.id.tb_toast3 -> {
                XToast(this@DetailToastActivity)
                    .setView(R.layout.toast_hint_noicon)
                    .setDuration(3000)
                    .setText("自定义背景的系统土司样式")
                    .show()
            }
            R.id.tb_toast4 -> {
                XToast(this@DetailToastActivity)
                    .setView(R.layout.toast_hint_noicon)
                    .setText("自定义基础样式吐司")
                    .show()
            }
            R.id.tb_xtoast1 -> {
                XToast(this@DetailToastActivity)
                    .setView(R.layout.toast_hint)
                    .setImageDrawable(R.drawable.svg_success)
                    .setText("带图标吐司")
                    .show()
            }
            R.id.tb_xtoast2 -> {
                XToast(this@DetailToastActivity)
                    .setView(R.layout.toast_hint)
                    .setAnimStyle(R.style.toast_animation)
                    .setImageDrawable(R.drawable.svg_success)
                    .setText("带动画带图标吐司")
                    .show()
            }
            R.id.tb_xtoast3 -> {
//                XToast(this@DetailToastActivity)
//                    .setView(R.layout.toast_hint)
//                    .setImageDrawable(R.mipmap.ic_launcher)
//                    .setText(android.R.id.message, "拖拽")
//                    .show()
            }
            R.id.tb_xtoast4 -> {
                XToast(this@DetailToastActivity)
                    .setDuration(5000)
                    .setView(R.layout.toast_hint)
                    .setAnimStyle(android.R.style.Animation_Dialog)
                    .setImageDrawable(android.R.id.icon, R.mipmap.ic_launcher)
                    .setText(android.R.id.message, "拖拽")
                    .show()
            }
        }
    }

}
