package com.ved.ui.fragment.component

import com.ved.R
import com.ved.glide.BannerGlideImageLoader
import com.ved.ui.base.BaseFragment
import kotlinx.android.synthetic.main.base_title_bar.*
import kotlinx.android.synthetic.main.fragment_banner.*

class BannerFragment : BaseFragment() {

    override fun loadView(): Int {
        return R.layout.fragment_banner
    }

    override fun init() {
        initTitle("图片轮播")

        val baseUseArea = "需要广告图片轮播的地方"
        val baseUseRole = "布局文件直接使用控件，自定义属性。代码设置加载器、图片数据源，再开启轮播。"
        val baseInteract = "默认自动滚动，可手动滑动。可通自定义此交互，禁用自动滚动和手动滑动。"
        val baseStyle = "可自定义长宽、轮播间隔和停留时间。指示器也可自定义选中和未选中样式。"

        initContent(baseUseArea, baseUseRole, baseInteract, baseStyle)
    }

    override fun doExecute() {

        activity_banner_banner_default?.setImageLoader(BannerGlideImageLoader())
        activity_banner_banner_default?.setImages(
            listOf(
                "http://zhuxian.wanmei.com/resources/jpg/160707/41467872026447.jpg",
                "http://zhuxian.wanmei.com/resources/jpg/160405/41459838236039.jpg",
                "http://zhuxian.wanmei.com/resources/jpg/160707/41467862487756.jpg",
                ""
            )
        )
        activity_banner_banner_default?.start()

        activity_banner_banner_one?.setImageLoader(BannerGlideImageLoader())
        activity_banner_banner_one?.setImages(
            listOf(
                "http://zhuxian.wanmei.com/resources/jpg/160707/41467872026447.jpg"
            )
        )
        activity_banner_banner_one?.start()

    }

}