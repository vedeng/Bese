package com.ved.ui

import android.content.Intent
import android.util.Log
import android.view.View
import android.widget.AdapterView
import com.bese.widget.dialog.PicPickerDialog
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.pic.picker.ImagePicker
import com.pic.picker.bean.ImageItem
import com.pic.picker.ui.ImageGridActivity
import com.pic.picker.ui.ImagePreviewDelActivity
import com.pic.picker.view.CropImageView
import com.ved.R
import com.ved.glide.PickerGlideImageLoader
import com.ved.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_select_picture.*
import kotlinx.android.synthetic.main.item_grid_picker.view.*


class DetailPicPickerActivity : BaseActivity() {

    companion object {
        const val CODE_SELECT = 101
        const val CODE_PREVIEW = 102

        const val ADD_FLAG = "/-"
        const val FLAG_PICKER_LIMIT = 6

        var SELECT_WITH_LIST = false
        var SELECT_WITH_SORT = false
    }

    /** 当前选择的所有图片 */
    private val selectList: ArrayList<ImageItem> = ArrayList()

    private var hasAddIcon = true

    override fun loadView() : Int {
        return R.layout.activity_select_picture
    }

    override fun init() {
        initTitle("图片选择器")

        val baseUseArea = "●需要从本地选择图片的场景"
        val baseUseRole = "●选择从相册还是相机选取图片：\n" +
                "●相机选择需要实时拍照，只能选择一张\n" +
                "●图库选择可以多选"
        val baseInteract = "●选图时需要先申请权限\n" +
                "●相机权限和存储权限"
        val baseStyle = "●图库选择支持定义选中是否数字排序 \n" +
                "●支持是否单选一张图（无排序，点选即确认）\n" +
                "●支持设置单张选图大小上限 \n" +
                "●支持设置允许选择的图片格式和不允许选择的图片格式 \n" +
                "●支持设置选择超限后的提示样式。\n" +
                "●支持裁剪（功能可用，需要再完善）"
        initContent(baseUseArea, baseUseRole, baseInteract, baseStyle)

        cb_select_with_list?.setOnCheckedChangeListener { _, ch ->
            SELECT_WITH_LIST = ch
        }
        cb_select_with_sort?.setOnCheckedChangeListener { _, ch ->
            SELECT_WITH_SORT = ch
        }

    }

    override fun doExecute() {
        rec_picker?.adapter = picAdapter
        appendAddIconToSelectList()
        picAdapter.addData(selectList)
    }

    var picAdapter = object : BaseQuickAdapter<ImageItem?, BaseViewHolder>(R.layout.item_grid_picker) {
        override fun convert(helper: BaseViewHolder, item: ImageItem?) {
            item?.run {
                if (item.name == ADD_FLAG) {
                    // 展示ADD图
                    helper.itemView.item_img?.setImageResource(R.drawable.svg_add)
                    helper.itemView.item_img?.setOnClickListener { openSelect() }
                    helper.itemView.item_mask?.visibility = View.GONE
                    helper.itemView.item_pic_delete?.visibility = View.GONE
                    helper.itemView.item_picker_process?.visibility = View.GONE
                } else {
                    // 展示库图，可预览
                    if (item.path.isNotEmpty()) {
                        helper.itemView.item_pic_delete?.visibility = View.VISIBLE
                        helper.itemView.item_pic_delete?.setOnClickListener {
                            removeFromList(item)
                        }
                        if (item.flag in 1..99) {
                            helper.itemView.item_picker_process?.visibility = View.VISIBLE
                            helper.itemView.item_mask?.visibility = View.VISIBLE
                            helper.itemView.item_picker_process?.progress = item.flag
                        } else {
                            helper.itemView.item_picker_process?.visibility = View.GONE
                            helper.itemView.item_mask?.visibility = View.GONE
                        }
                        helper.itemView.item_img?.run {
                            setOnClickListener { openPreview(helper.adapterPosition) }
                            PickerGlideImageLoader().displayImage(this@DetailPicPickerActivity, item.path, this)
                        }
                    } else {
                        // 展示错误图
                        helper.itemView.item_img?.setImageResource(R.drawable.svg_placeholder)
                        helper.itemView.item_img?.setOnClickListener { openPreview(helper.adapterPosition) }
                        helper.itemView.item_pic_delete?.visibility = View.GONE
                        helper.itemView.item_picker_process?.visibility = View.GONE
                    }
                }
            }
        }
    }

    fun openSelect() {
        PicPickerDialog(View.OnClickListener { gotoPicPickerPage(true) },
            View.OnClickListener { gotoPicPickerPage(false) }).show(supportFragmentManager, "Picker")
    }

    fun openPreview(currentPosition: Int) {
        //打开预览
        val preview: ArrayList<ImageItem> = selectList.filter { it.name != ADD_FLAG }.toMutableList() as ArrayList<ImageItem>
        val intentPreview = Intent(this, ImagePreviewDelActivity::class.java)
            .putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, preview)
            .putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, if (currentPosition < 0) 0 else currentPosition)
            .putExtra(ImagePicker.EXTRA_FROM_ITEMS, true)
        startActivityForResult(intentPreview, CODE_PREVIEW)
    }

    private fun removeFromList(item: ImageItem?) {
        selectList.remove(item)
        appendAddIconToSelectList()
        updateData()
    }

    private fun appendAddIconToSelectList() {
        // 先判断列表是否包含值
        if (selectList.any { it.name == ADD_FLAG }) {
            selectList.removeAll(selectList.filter { it.name == ADD_FLAG })
        }
        // 再判断列表是否需要添加一个加号图标
        if (selectList.size < FLAG_PICKER_LIMIT) {
            selectList.add(ImageItem().apply { name = ADD_FLAG; path = "" })
            hasAddIcon = true
        } else {
            hasAddIcon = false
        }
    }

    private var interval: Long = 0L
    private fun updateData() {
        val c = System.currentTimeMillis()
        if (c - interval > 120) {
            interval = c
            replaceRecData(selectList)
        }
    }

    private fun replaceRecData(list: ArrayList<ImageItem>?) {
        list?.run {
            picAdapter.replaceData(list)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CODE_SELECT) {
            // 选图
            val list: ArrayList<ImageItem>? = data?.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS) as? ArrayList<ImageItem>
            if (list?.size ?: 0 > 0) {
                if (SELECT_WITH_LIST) {
                    // 带着列表选图，会有数据重复。需要清空当前列表
                    selectList.clear()
                } else {
                    // 添加到现有列表
                    selectList.removeAll(selectList.filter { it.name == ADD_FLAG })
                }

                selectList.addAll(list!!)
                appendAddIconToSelectList()
                replaceRecData(selectList)
                setSelectPicPaths()
                // 上传选中图片 - 支持批量
                list.forEach {
                    Log.e("返回列表1=====", " -- ${it.name}")
                    batchUploadPic(it.path)
                }
            }
        } else if (requestCode == CODE_PREVIEW) {
            val list: ArrayList<ImageItem>? = data?.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS) as? ArrayList<ImageItem>

            if (list?.size ?: 0 > 0) {
                selectList.clear()
                selectList.addAll(list!!)
                appendAddIconToSelectList()
                replaceRecData(selectList)
                setSelectPicPaths()
                // 上传选中图片
                list.forEach {
                    Log.e("返回列表2=====", " -- ${it.name}")
                    batchUploadPic(it.path)
                }
            }
        }
    }

    private fun setSelectPicPaths() {
        // 已选图片路径展示
        select_path_list?.run {
            var txt = ""
            selectList.forEach {
                if (it.name != ADD_FLAG) {
                    txt = txt.plus(it.path + "\n\n")
                }
            }
            text = txt
        }
    }

    private fun gotoPicPickerPage(isCamera: Boolean) {
        if (ImagePicker.getInstance().imageLoader == null) {
            ImagePicker.getInstance().imageLoader = PickerGlideImageLoader()
        }
        if (isCamera) {
            // 相机
            ImagePicker.getInstance().run {
                selectLimit = 1
                isMultiMode = false
                isCrop = false
                val intent = Intent(this@DetailPicPickerActivity, ImageGridActivity::class.java).putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true)
                startActivityForResult(intent, CODE_SELECT)
            }
        } else {
            // 图库
            ImagePicker.getInstance().run {
                val intent = Intent(this@DetailPicPickerActivity, ImageGridActivity::class.java)
                if (SELECT_WITH_LIST) {
                    // 如果定制进入相册就选中之前的已选，需要传参。并且需要更改接收返回值时清空之前已选列表。选择上限也需要改。
                    intent.putExtra(
                        ImageGridActivity.EXTRAS_IMAGES,
                        selectList.filter { it.name != ADD_FLAG } as? ArrayList<ImageItem>)
                    selectLimit = FLAG_PICKER_LIMIT
                } else {
                    selectLimit = FLAG_PICKER_LIMIT - selectList.size + (if (hasAddIcon) 1 else 0)
                }
                isMultiMode = true
                isSelectLimitShowDialog = true      // 选图超限 以弹窗提示
                isFilterSelectFormat = true     // 开启选图限定类型功能
                formatAllowCollection = arrayListOf("jpg", "jpeg", "png", "bmp")    // 定义选图的允许类型
                selectLimitSize = 4f                    // 选图大小限制参数，单位M
                isSelectPicWithSortNumber = SELECT_WITH_SORT
                startActivityForResult(intent, CODE_SELECT)
            }
        }
    }

    private fun batchUploadPic(path: String?) {
        Log.e("上传操作：", "路径是== $path")
    }

}
