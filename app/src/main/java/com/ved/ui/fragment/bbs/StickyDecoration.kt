package com.ved.ui.fragment.bbs

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class StickyDecoration<T>(var ctx: Context?, var dataList: List<T>?, var callback: DecorationCallback?) : RecyclerView.ItemDecoration() {

    interface DecorationCallback {
        fun getGroupId(position: Int) : String
    }

    init {

    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
        val pos = parent.getChildAdapterPosition(view)
        val groupId = callback?.getGroupId(pos)
        if (groupId == "-1") return
        if (pos == 0 || true) {
            outRect.top = 0
        } else {
            outRect.top = 0
        }
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
        val left = parent.paddingLeft
        val right = parent.width - parent.paddingRight
        val childNum = parent.childCount
        for (i in 0 until childNum) {
            val view = parent.getChildAt(i)
            val pos = parent.getChildAdapterPosition(view)
            val gid = callback?.getGroupId(pos)
            if (gid == " -1") return
        }
    }

}