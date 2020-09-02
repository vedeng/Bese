package com.ved.ui.fragment.component

import android.net.Uri
import android.view.SurfaceHolder
import android.widget.MediaController
import com.aliyun.player.AliPlayer
import com.aliyun.player.AliPlayerFactory
import com.aliyun.player.IPlayer
import com.aliyun.player.bean.ErrorInfo
import com.aliyun.player.nativeclass.CacheConfig
import com.aliyun.player.nativeclass.TrackInfo
import com.aliyun.player.source.UrlSource
import com.bese.util.glide.BannerGlideImageLoader
import com.blankj.utilcode.util.ColorUtils
import com.blankj.utilcode.util.LogUtils
import com.blankj.utilcode.util.PathUtils
import com.ved.R
import com.ved.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_banner.*

class BannerFragment : BaseFragment() {

    private val video1 = "http://cha100.cn/dld/whenSong.flv"        // 13M
    private val video2 = "http://cha100.cn/dld/NoNoNo.mp4"         // 20M

    private var videoPlayer: AliPlayer? = null

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

        initAliVideo()
        video_ali?.holder?.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceCreated(holder: SurfaceHolder?) {
                videoPlayer?.setDisplay(holder)
            }
            override fun surfaceChanged(holder: SurfaceHolder?, format: Int, width: Int, height: Int) {
                videoPlayer?.redraw()
            }
            override fun surfaceDestroyed(holder: SurfaceHolder?) {
                videoPlayer?.setDisplay(null)
            }
        })

        tv_play?.setOnClickListener {
            videoPlayer?.start()
            video_system?.start()
        }

        tv_pause?.setOnClickListener {
            videoPlayer?.pause()
            video_system?.pause()
        }

        tv_flush?.setOnClickListener {
            videoPlayer?.reset()
            video_system?.resume()
        }
        tv_seek?.setOnClickListener {
            videoPlayer?.seekTo(50)
            video_system?.seekTo(50)
        }
        video_system?.setMediaController(MediaController(context))
        video_system?.setVideoURI(Uri.parse(video1))
    }

    private fun initAliVideo() {
        videoPlayer = AliPlayerFactory.createAliPlayer(context)
        videoPlayer?.run {
            setOnCompletionListener {
                LogUtils.i("Ali--> 播放完成")
            }
            setOnErrorListener {
                LogUtils.i("Ali--> 播放出错：${it.msg}")
            }
            setOnPreparedListener {
                LogUtils.i("Ali--> 准备好了")
            }
            setOnInfoListener {
                LogUtils.i("Ali--> 其他信息的事件，type包括了：循环播放开始，缓冲位置，当前播放位置，自动播放开始等")
            }
            setOnRenderingStartListener {
                LogUtils.i("Ali--> 首帧渲染显示")
            }
            setOnVideoSizeChangedListener { w, h ->
                LogUtils.i("Ali--> 视频分辨率变化回调：$w   -   $h")
            }
            setOnSeekCompleteListener {
                LogUtils.i("Ali--> 拖动结束")
            }
            setOnStateChangedListener {
                LogUtils.i("Ali--> 播放状态改变  $it")
            }
            setOnSnapShotListener { bitmap, w, h ->
                LogUtils.i("Ali--> 截图事件：$w  -  $h    /  ${bitmap.byteCount}")
            }
            setOnLoadingStatusListener(object : IPlayer.OnLoadingStatusListener {
                override fun onLoadingBegin() {
                    LogUtils.i("Ali--> 缓冲开始")
                }
                override fun onLoadingProgress(process: Int, kbps: Float) {
                    LogUtils.i("Ali--> 缓冲进度：$process    --  $kbps")
                }
                override fun onLoadingEnd() {
                    LogUtils.i("Ali--> 缓冲结束")
                }
            })
            setOnTrackChangedListener(object : IPlayer.OnTrackChangedListener {
                override fun onChangedSuccess(trackInfo: TrackInfo?) {
                    LogUtils.i("Ali--> 切换音视频流或者清晰度成功: ${trackInfo?.description}")
                }
                override fun onChangedFail(trackInfo: TrackInfo?, errInfo: ErrorInfo?) {
                    LogUtils.i("Ali--> 切换音视频流或者清晰度失败：${errInfo?.msg}")
                }
            })

            setCacheConfig(CacheConfig().apply {
                mEnable = true
                mMaxDurationS = 30      // 缓存后30秒
                mMaxSizeMB = 100
                mDir = PathUtils.getExternalAppCachePath()
            })
            setDataSource(UrlSource().apply {
                uri = video1
            })

            prepare()

        }


    }

    override fun doExecute() {

        activity_banner_banner_default?.setImageLoader(BannerGlideImageLoader())
        activity_banner_banner_default?.setImages(
            listOf(
                "http://zhuxian.wanmei.com/resources/jpg/160707/41467872026447.jpg"
            )
        )
        activity_banner_banner_default?.start()

        activity_banner_banner_one?.setImageLoader(BannerGlideImageLoader())
        activity_banner_banner_one?.setImages(
            listOf(
                "http://zhuxian.wanmei.com/resources/jpg/160707/41467872026447.jpg",
                "http://zhuxian.wanmei.com/resources/jpg/160707/41467872026447.jpg",
                "http://zhuxian.wanmei.com/resources/jpg/160405/41459838236039.jpg",
                "http://zhuxian.wanmei.com/resources/jpg/160707/41467862487756.jpg",
                ""
            )
        )
        point_indicator?.bindBanner(activity_banner_banner_one)
        activity_banner_banner_one?.start()

    }

    override fun onDestroy() {
        super.onDestroy()
        videoPlayer?.release()
    }

}