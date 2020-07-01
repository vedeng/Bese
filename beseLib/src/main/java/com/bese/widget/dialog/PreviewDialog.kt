package com.bese.widget.dialog

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bese.R
import com.bese.view.photoview.PhotoView
import com.bese.widget.button.TextButton
import com.blankj.utilcode.util.FileUtils
import com.blankj.utilcode.util.PathUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils.runOnUiThread
import com.bumptech.glide.Glide
import com.hjq.permissions.OnPermission
import com.hjq.permissions.XXPermissions
import java.io.File

/**
 * < 大图预览Dialog >
 */
class PreviewDialog(
    private var mCtx: Context?,
    private var imageList: ArrayList<String?>?,
    private var currentPosition: Int = 0,
    private var enableDownload: Boolean = false
) : DialogFragment() {

    companion object {
        var TIP_SAVE_OVER = "保存成功"
        var TIP_SAVING = "保存中..."
        var TIP_SAVE_FAIL = "保存失败"
        var TIP_SAVE_NO_PERMISSION = "缺少必要权限，请去设置页面打开相应权限"
        var SAVE_DIR = "Download"
        var SAVE_PATH = PathUtils.getExternalStoragePath() + "/$SAVE_DIR/"

    }

    /** 预览图ViewPager */
    private var preview: ViewPager? = null
    private var previewLayout: RelativeLayout? = null
    private var downloadIcon: ImageView? = null
    private var numIndicator: TextButton? = null

    init {
        imageList?.forEach { if(it?.isNotEmpty() == true) { imageList?.add(it) } }
    }

    /**
     * preview适配
     */
    private var previewAdapter = object : PagerAdapter() {
        override fun isViewFromObject(view: View, obj: Any): Boolean {
            return view == obj
        }

        override fun getCount(): Int {
            return imageList?.size ?: 1
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val view = PhotoView(mCtx)
            view.isEnableZoom = true
            view.scaleType = ImageView.ScaleType.FIT_CENTER
            mCtx?.run { Glide.with(this).load(imageList?.get(position)).into(view) }
            view.setOnClickListener {
                dismiss()
            }
            container.addView(view)
            return view
        }

        override fun destroyItem(container: ViewGroup, position: Int, obj: Any) {
            container.removeView(obj as View)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val window = dialog?.window
        window?.decorView?.setPadding(0, 0, 0, 0)

        val attributes = window?.attributes
        attributes?.width = WindowManager.LayoutParams.MATCH_PARENT
        attributes?.height = WindowManager.LayoutParams.WRAP_CONTENT
        attributes?.dimAmount = 1f
        attributes?.gravity = Gravity.TOP
        window?.attributes = attributes

        // 最后调用super
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireActivity(), R.style.Dialog_Fullscreen)
        val view = LayoutInflater.from(activity).inflate(R.layout.content_preview, null)
        initView(view)
        dialogBuilder.setView(view)
        // 返回自定义布局的Dialog
        return dialogBuilder.create()
    }

    private fun initView(view: View) {
        previewLayout = view.findViewById(R.id.layout_preview)
        preview = view.findViewById(R.id.preview)
        downloadIcon = view.findViewById(R.id.preview_download)
        numIndicator = view.findViewById(R.id.btn_num_indicator)

        previewLayout?.visibility = View.VISIBLE

        if (currentPosition < (imageList?.size ?: 1)) {
            preview?.let {
                it.pageMargin = (resources.displayMetrics.density * 10).toInt()
                it.adapter = previewAdapter
                it.currentItem = currentPosition
            }
        }

        previewLayout?.setOnClickListener {
            dismiss()
        }
        downloadIcon?.visibility = if (enableDownload) View.VISIBLE else View.GONE
        downloadIcon?.setOnClickListener {
            XXPermissions.with(requireActivity()).constantRequest()
                .permission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request(object : OnPermission {
                    override fun noPermission(denied: MutableList<String>?, quick: Boolean) {
                        if (quick) {
                            ToastUtils.showShort(TIP_SAVE_NO_PERMISSION)
                        }
                    }

                    override fun hasPermission(granted: MutableList<String>?, isAll: Boolean) {
                        ToastUtils.showShort(TIP_SAVING)
                        Thread(Runnable { downloadPic(imageList?.get(currentPosition)) }).start()
                    }
                })
        }
        if (imageList?.size ?: 1 > 1) {
            numIndicator?.visibility = View.VISIBLE
            numIndicator?.text = "${currentPosition + 1}/${imageList?.size ?: 1}"
        }
        preview?.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(state: Int) {}
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) { }
            override fun onPageSelected(position: Int) {
                currentPosition = position
                numIndicator?.text = "${position + 1}/${imageList?.size ?: 1}"
            }
        })
    }

    private fun downloadPic(url: String?) {
        if (url?.isNotEmpty() == true) {
            var isDone = false
            try {
                val sourceFile = Glide.with(requireContext()).asFile().load(url).submit().get()
                val baseDir = SAVE_PATH
                val filePath = getPicSaveName(baseDir, url)
                val dir = File(baseDir)
                if (!dir.exists()) {
                    dir.mkdirs()
                }
                val newFile = File(filePath)
                FileUtils.copyFile(sourceFile, newFile)
                // 通知媒体库，如果失败可能文件不存在，isDone要放在后面
                MediaStore.Images.Media.insertImage(mCtx?.contentResolver, newFile.absolutePath, filePath, null)
                requireActivity().sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(File(newFile.path))))

                isDone = true
            } catch (exception: Exception) {
            }

            if (isDone) {
                runOnUiThread { ToastUtils.showShort(TIP_SAVE_OVER) }
            } else {
                runOnUiThread { ToastUtils.showShort(TIP_SAVE_FAIL) }
            }
        } else {
            runOnUiThread { ToastUtils.showShort(TIP_SAVE_FAIL) }
        }
    }

    private fun getPicSaveName(path: String?, resPath: String?): String {
        var name = ""
        path?.run {
            // png格式特殊，单独处理，如果有其他特殊格式，也可单独处理
            val isPng = resPath?.endsWith(".png", true)
            name = this.plus(System.currentTimeMillis()).plus(if (isPng == true) ".png" else ".jpg")
        }
        return name
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            super.show(manager, tag)
        } catch (e: Exception) {
            Log.e("PreviewDialog-Error->", "${e.message}")
        }
    }

}
