package com.ved.ui

import androidx.navigation.findNavController
import com.blankj.utilcode.util.ToastUtils
import com.netlib.BaseCallback
import com.ved.R
import com.ved.entity.event.BackEvent
import com.ved.net.request.CheckUpdateRequest
import com.ved.net.response.CheckUpdateResponse
import com.ved.ui.base.BaseActivity
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe

class MainActivity : BaseActivity() {

    override fun loadView() : Int {
        return R.layout.activity_main
    }

    override fun init() {
        EventBus.getDefault().register(this)
    }

    override fun doExecute() {
        checkUpdate()

    }

    private fun checkUpdate() {
        CheckUpdateRequest().request(CheckUpdateRequest.Param("1"), object : BaseCallback<CheckUpdateResponse>() {
            override fun onSuccess(response: CheckUpdateResponse?) {
                response?.data?.run {
                    ToastUtils.showShort(this.updateContent)
                }
            }
        })
    }

    override fun onBackPressed() {
        // 如果有压栈，就出栈。返回是否出栈成功。当Fragment栈中仅有一个时，出栈失败，返回false。
        val isPop = findNavController(R.id.frame_main).popBackStack()
        if (!isPop) {
            // 必须先执行finish，再执行override，否则无效
            finish()
            overridePendingTransition(0, R.anim.anim_fade_above_out)
        }

    }

    @Subscribe
    fun backEvent(event: BackEvent) {
        onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().unregister(this)
    }

}
