package com.bese.widget.dialog

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.bese.R

/**
 * < 图片选择弹窗 >
 *      不会自动申请权限，如果没有权限，请在点击事件中先添加权限申请动作
 */
class PicPickerDialog(private val cameraListener: View.OnClickListener? = null, private val galleryListener: View.OnClickListener? = null) : DialogFragment() {

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        val window = dialog?.window
        window?.decorView?.setPadding(0, 0, 0, 0)

        val attributes = window?.attributes
        attributes?.width = WindowManager.LayoutParams.MATCH_PARENT
        attributes?.height = WindowManager.LayoutParams.WRAP_CONTENT
        attributes?.dimAmount = 0.6f
        attributes?.gravity = Gravity.BOTTOM
        window?.attributes = attributes

        // 最后调用super
        super.onActivityCreated(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogBuilder = AlertDialog.Builder(requireContext(), R.style.Dialog_Fullscreen_Bottom)
        val view = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_picker, null)
        initView(view)
        dialogBuilder.setView(view)
        // 返回自定义布局的Dialog
        return dialogBuilder.create()
    }

    private fun initView(view: View) {
        val cameraPicker = view.findViewById(R.id.picker_camera) as TextView
        val galleryPicker = view.findViewById(R.id.picker_gallery) as TextView
        val cancelPicker = view.findViewById(R.id.picker_cancel) as TextView

        cameraPicker.setOnClickListener {
            dismiss()
            cameraListener?.onClick(cameraPicker)

        }
        galleryPicker.setOnClickListener {
            dismiss()
            galleryListener?.onClick(galleryPicker)
        }
        cancelPicker.setOnClickListener {
            dismiss()
        }
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            super.show(manager, tag)
        } catch (e: Exception) {
            Log.e("PickerDialog-Error", "${e.message}")
        }
    }
}
