package com.bese.widget.dialog

import android.Manifest
import android.app.Activity
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
import com.blankj.utilcode.util.ToastUtils
import com.hjq.permissions.OnPermission
import com.hjq.permissions.XXPermissions

/**
 * < 图片选择弹窗 >
 *      会自动申请权限
 */
class PicPickerDialog(private val mAct: Activity?, private val cameraListener: View.OnClickListener?, private val galleryListener: View.OnClickListener?)
    : DialogFragment() {

    companion object {
        const val NO_PERMISSION_CAMERA = "没有相机权限"
        const val NO_PERMISSION_SAVE = "没有存储权限"
    }

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
            requireCameraPermission()

        }
        galleryPicker.setOnClickListener {
            dismiss()
            requireGalleryPermission(false)
        }
        cancelPicker.setOnClickListener {
            dismiss()
        }
    }

    private fun requireCameraPermission() {
        XXPermissions.with(mAct).permission(Manifest.permission.CAMERA).request(object : OnPermission {
            override fun noPermission(denied: MutableList<String>?, quick: Boolean) {
                if (quick) {
                    ToastUtils.showShort(NO_PERMISSION_CAMERA)
                }
            }

            override fun hasPermission(granted: MutableList<String>?, isAll: Boolean) {
                // 先相机权限，再存储权限
                requireGalleryPermission(true)
            }
        })
    }

    private fun requireGalleryPermission(fromCamera: Boolean) {
        XXPermissions.with(mAct).permission(Manifest.permission.WRITE_EXTERNAL_STORAGE).request(object :
            OnPermission {
            override fun noPermission(denied: MutableList<String>?, quick: Boolean) {
                if (quick) {
                    ToastUtils.showShort(NO_PERMISSION_SAVE)
                }
            }

            override fun hasPermission(granted: MutableList<String>?, isAll: Boolean) {
                if (fromCamera) {
                    cameraListener?.onClick(null)
                } else {
                    galleryListener?.onClick(null)
                }
            }
        })
    }

    override fun show(manager: FragmentManager, tag: String?) {
        try {
            super.show(manager, tag)
        } catch (e: Exception) {
            Log.e("PickerDialog-show-Error", "${e.message}")
        }
    }
}
