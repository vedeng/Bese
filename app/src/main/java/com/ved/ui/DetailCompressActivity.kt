package com.ved.ui

import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.SeekBar
import com.bese.widget.dialog.PicPickerDialog
import com.blankj.utilcode.util.ToastUtils
import com.hjq.permissions.OnPermission
import com.hjq.permissions.XXPermissions
import com.ved.R
import com.ved.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_bitmap_compress.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream


class DetailCompressActivity : BaseActivity() {

    private var picUri: String? = null
    private var picSize: Long = 1

    val REQUEST_CODE_SELECT = 100
    val REQUEST_CODE_PREVIEW = 101

    override fun loadView() : Int {
        return R.layout.activity_bitmap_compress
    }

    override fun init() {

        initTitle("图片压缩")

        val baseUseArea = "●所有使用吐司的场景"

        val baseUseRole = "●直接使用，定义类型：\n" +
                "●在屏幕中心显示时，带图标的吐司，上下结构\n" +
                "●在屏幕底部展示时，纯文本的吐司，可以在文字前附带小图标\n" +
                "●文字颜色\n" +
                "●背景颜色"

        val baseInteract = "●控件默认不可点，展示数秒后消失，动画目前跟随系统吐司动画"

        val baseStyle = "●最大圆角"
        initContent(baseUseArea, baseUseRole, baseInteract, baseStyle)

    }

    override fun doExecute() {
        initListener()
    }

    private fun initListener() {
        seek?.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                tv_seek_cursor?.text = progress.toString()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }
            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }
        })

        tv_minus?.setOnClickListener {
            seek?.run {
                if (progress > 0) {
                    progress -= 1
                }
            }
        }
        tv_add?.setOnClickListener {
            seek?.run {
                if (progress < max) {
                    progress += 1
                }
            }
        }

        img_start?.setOnClickListener {
            PicPickerDialog(this,
                View.OnClickListener { gotoPicPickerPage(true) },
                View.OnClickListener { gotoPicPickerPage(false) }).show(supportFragmentManager, "Picker")
        }

        tv_compress?.setOnClickListener {
            checkSavePermission()
        }
    }

    private fun gotoPicPickerPage(fromCamera: Boolean) {
        startActivityForResult(Intent(Intent.ACTION_PICK).apply { type = "image/*" }, REQUEST_CODE_SELECT)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_SELECT -> {
                    val uri = data?.data
                    val path = uri?.path
                    loadBitmap(path)
                }
            }
        }
    }

    private var bitmap: Bitmap? = null

    private fun loadBitmap(path: String?) {
        path?.let {
            val file = File(it)
            if (file.exists()) {
                Log.e("文件路径=", file.path)
                val options: BitmapFactory.Options = BitmapFactory.Options()
                options.inSampleSize = 1
                bitmap = BitmapFactory.decodeFile(file.path, options)
                Log.e("宽高=", "${bitmap?.width} + ${bitmap?.height}")
                img_start?.setImageBitmap(bitmap)

                tv_size?.text = "${bitmap?.width} * ${bitmap?.height}"
                seek?.progress = 100
                val k = file.length() / 1024
                tv_total?.text =  "$k K"
                picSize = k
            } else {
                Log.e("文件路径=null", "$path ///")
            }

//            var options: BitmapFactory.Options = BitmapFactory.Options()
//            options.inJustDecodeBounds = true
//            var img: Bitmap = BitmapFactory.decodeFile(str, options)
//            Loge.getLog().e("宽高=", "${img.width} + ${img.height}")

        }

    }

    private fun compressBitmap(p: Int) : Bitmap? {
        Log.e("正在压缩=", "$p")
        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, p, baos)
        baos.flush()
        val datas = baos.toByteArray()
        var bm = BitmapFactory.decodeByteArray(datas, 0, datas.size)
        Log.e("000压缩=", "${bm.byteCount} + ${baos.size()}")
        baos.close()
        return bm
    }

    private fun compressPic() {
        val percent = seek?.progress ?: 0

        val baos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, percent, baos)
        baos.flush()
        val datas = baos.toByteArray()
        val file = File("${Environment.getExternalStorageDirectory()}/eeeeee.jpg")
        if (!file.exists()) {
            file.createNewFile()
        }
        try {
            val fos = FileOutputStream(file)
            fos.write(datas)
            fos.flush()
            fos.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        Log.e("000压缩=", "${file.length()} + ${baos.size()}")
        baos.close()
        val bm = BitmapFactory.decodeFile(file.path)

        img_start?.setImageBitmap(bm)
        tv_size?.text = "${bm?.width} * ${bm?.height}"
        val k = file.length() / 1024
        tv_total?.text = "$k K"
        tv_compress_percent?.text = String.format("%.2f",(k * 1.0f / picSize))
        Log.e("压缩完毕=", " - ")
    }

    private fun checkSavePermission() {
        XXPermissions.with(this).permission(Manifest.permission.WRITE_EXTERNAL_STORAGE).request(object : OnPermission {
            override fun noPermission(denied: MutableList<String>?, quick: Boolean) {
                if (quick) {
                    ToastUtils.showShort("没有存储权限")
                }
            }

            override fun hasPermission(granted: MutableList<String>?, isAll: Boolean) {
                // 先相机权限，再存储权限
                compressPic()
            }
        })
    }

}
