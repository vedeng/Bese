package com.ved.ui.fragment.bbs

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class StickyDecoration<T>(var ctx: Context?, var dataList: List<T>?) : RecyclerView.ItemDecoration() {

    init {

    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state)
    }

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDrawOver(c, parent, state)
        val count = parent.childCount
        val topLevelPosition = (parent.layoutManager as? LinearLayoutManager)?.findFirstVisibleItemPosition()
    }

}