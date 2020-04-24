package com.ved.ui.fragment.element

import android.animation.Animator
import android.util.Log
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.LottieCompositionFactory
import com.ved.R
import com.ved.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_lottie.*

class LottieFragment : BaseFragment() {

    private var process = 0f

    override fun loadView(): Int {
        return R.layout.fragment_lottie
    }

    override fun init() {
        initTitle("Lottie 老铁动画")

        val baseUseArea = "●固定展示动画的场景"
        val baseUseRole = "●预设制作好的json参数, 使用时通过Lottie库展示" +
                "●如果需要可暂停动画，并且可以设置监听"
        val baseInteract = "●可触摸暂停"
        val baseStyle = "●预设数据"

        initContent(baseUseArea, baseUseRole, baseInteract, baseStyle)

    }

    override fun doExecute() {

        // 添加动画控制
        av_anim_tv_control?.setOnClickListener {
            if (av_anim_tv_control?.isAnimating == true) {
                // 记录暂停时的播放进度，恢复播放时可以复用进度
                process = av_anim_tv_control?.progress ?: 0f
                av_anim_tv_control?.pauseAnimation()
            } else {
                av_anim_tv_control?.playAnimation()
                av_anim_tv_control?.progress = process
                Log.e("时长进度= ", "${av_anim_tv_control?.progress} - " + process)
            }
        }

        // 添加动画监听
        av_anim_tv_control?.addAnimatorListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(animation: Animator?) {
                // loop的时候会调用，注意是重新播放的时候会调用
            }

            override fun onAnimationEnd(animation: Animator?) {
                // 动画最后一次结束的时候回调用
            }

            override fun onAnimationCancel(animation: Animator?) {
                // 动画主动取消的时候会调用
            }

            override fun onAnimationStart(animation: Animator?) {
                // 动画第一次开始播放的时候会调用
            }
        })

        // 手动设置动画，从文件中同步加载
        val composition = LottieCompositionFactory.fromAssetSync(baseContext, "loading.json")
        composition.value?.let {
            av_anim_load_run?.run {
                setComposition(it)
                av_anim_load_run?.progress = 0f
                av_anim_load_run?.playAnimation()
            }
        }

        // 手动设置动画第二种方式，从资源文件异步加载。加载完成可以有监听
        LottieComposition.Factory.fromAssetFileName(baseContext, "tv.json") { com ->
            com?.run { av_anim_tv?.setComposition(this) }
        }

        LottieComposition.Factory.fromAssetFileName(baseContext, "heart.json") { com ->
            com?.run { av_anim_heart?.setComposition(this) }
        }

        LottieComposition.Factory.fromAssetFileName(baseContext, "ecg.json") { com ->
            com?.run { av_anim_snack?.setComposition(this) }
        }

        LottieComposition.Factory.fromAssetFileName(baseContext, "confetti.json") { com ->
            com?.run { av_anim_conf?.setComposition(this) }
        }

        LottieComposition.Factory.fromAssetFileName(baseContext, "muzli.json") { com ->
            com?.run { av_anim_muz?.setComposition(this) }
        }

    }

}