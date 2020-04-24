package com.ved.ui

import android.content.Intent
import android.os.Handler
import android.view.View
import com.bese.util.SP
import com.ved.R
import com.ved.config.SPConfig
import com.ved.ui.base.BaseActivity

class SplashActivity : BaseActivity() {

    override fun loadView() : Int {
        return R.layout.activity_splash
    }

    override fun init() {

    }

    override fun doExecute() {
        turnToHome()
    }

    private fun turnToHome() {
        Handler().postDelayed({
            if (SP.getBoolean(SPConfig.HAS_OPEN_ONCE) == true) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // 首次打开 去引导页
                startActivity(Intent(this, MainActivity::class.java))
                SP.save(SPConfig.HAS_OPEN_ONCE, true)
            }
            finish()
        }, 1000)
    }

}
