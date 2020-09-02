package com.ved.ui.fragment.element

import android.graphics.Color
import android.os.Handler
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.ved.R
import com.ved.entity.bean.IconFont
import com.ved.ui.base.BaseFragment
import kotlinx.android.synthetic.main.fragment_font_icon.*
import kotlinx.android.synthetic.main.item_icon_font.view.*
import kotlin.random.Random

class FontIconFragment : BaseFragment() {

    private var randomColor = false
    private var currentTextColor = Color.parseColor("#0099ff")

    private var mHandler = Handler(Handler.Callback {
        when (it.what) {
            101 -> {
                listAdapter.setList(fontIconList)
                Handler().postDelayed({ hideLoading() }, 1500)
            }
        }
        true
    })

    override fun loadView(): Int {
        return R.layout.fragment_font_icon
    }

    override fun init() {
        initTitle("字体图标")

        val baseUseArea = "●部分使用文字代替图标使用的场景"

        val baseUseRole = "●直接引用TextView，设置fontFamily \n" +
                "●引用字体编码表 font_string.xml中的值"

        val baseInteract = "●这个不需要交互"

        val baseStyle = "●样式和文字一样，可以像文字一样自由设置文字颜色，但有一点需要注意，字体图标没有行高这个概念。"

        initContent(baseUseArea, baseUseRole, baseInteract, baseStyle)

        cb_random_color?.setOnCheckedChangeListener { _, isChecked ->
            randomColor = isChecked
            listAdapter.notifyDataSetChanged()
        }
    }

    override fun doExecute() {

        showLoading()
        mHandler.post {
            resetFontMap()
        }
        rec_icon?.run {
            layoutManager = GridLayoutManager(baseContext, 3)
            adapter = listAdapter
        }
    }

    private var random = Random(200)

    private var listAdapter = object : BaseQuickAdapter<IconFont?, BaseViewHolder>(R.layout.item_icon_font) {
        override fun convert(holder: BaseViewHolder, item: IconFont?) {
            item?.run {
                holder.itemView.icon_name?.text = name
                holder.itemView.icon_code?.text = "\\u$code"
                holder.itemView.icon?.text = code.toInt(16).toChar().toString()
                if (randomColor) {
                    holder.itemView.icon?.setTextColor(Color.argb(255, random.nextInt(), random.nextInt(), random.nextInt()))
                } else {
                    holder.itemView.icon?.setTextColor(currentTextColor)
                }
            }
        }
    }

    private val fontIconList = ArrayList<IconFont>()

    private fun resetFontMap() {
        fontIconList.run {
            add(IconFont("company","e95f"))
            add(IconFont("crown","e95c"))
            add(IconFont("network","e95b"))
            add(IconFont("price","e95d"))
            add(IconFont("diamond","e95e"))
            add(IconFont("list","e95a"))
            add(IconFont("slide_down","e959"))
            add(IconFont("service","e958"))
            add(IconFont("selected2","e93e"))
            add(IconFont("popup","e957"))
            add(IconFont("picture","e956"))
            add(IconFont("record","e952"))
            add(IconFont("alarm_clock","e953"))
            add(IconFont("pentagram1","e954"))
            add(IconFont("pentagram2","e955"))
            add(IconFont("date","e94f"))
            add(IconFont("see","e950"))
            add(IconFont("weibo","e951"))
            add(IconFont("android","e94d"))
            add(IconFont("apple","e94e"))
            add(IconFont("purchase","e94c"))
            add(IconFont("collect1","e94a"))
            add(IconFont("collect2","e94b"))
            add(IconFont("edit","e946"))
            add(IconFont("after_sale","e949"))
            add(IconFont("slide_up","e947"))
            add(IconFont("setting","e948"))
            add(IconFont("radio1","e937"))
            add(IconFont("radio2","e938"))
            add(IconFont("circle_of_friends","e944"))
            add(IconFont("wechat","e945"))
            add(IconFont("app_gallery","e942"))
            add(IconFont("app_list","e943"))
            add(IconFont("app_right","e940"))
            add(IconFont("app_left","e941"))
            add(IconFont("app_more","e93f"))
            add(IconFont("selected1","e93d"))
            add(IconFont("back","e939"))
            add(IconFont("checkbox1","e93a"))
            add(IconFont("checkbox2","e93b"))
            add(IconFont("rotate","e93c"))
            add(IconFont("download","e935"))
            add(IconFont("system_message","e936"))
            add(IconFont("footprint","e933"))
            add(IconFont("time","e929"))
            add(IconFont("address","e931"))
            add(IconFont("coupon","e932"))
            add(IconFont("deliver","e934"))
            add(IconFont("verify","e92e"))
            add(IconFont("hide","e92f"))
            add(IconFont("display","e930"))
            add(IconFont("sms","e926"))
            add(IconFont("stock_out","e927"))
            add(IconFont("payment","e928"))
            add(IconFont("order","e92a"))
            add(IconFont("password","e92b"))
            add(IconFont("delivery","e92c"))
            add(IconFont("phone_number","e92d"))
            add(IconFont("qzone","e924"))
            add(IconFont("qq","e925"))
            add(IconFont("service3","e922"))
            add(IconFont("service2","e923"))
            add(IconFont("service4","e90c"))
            add(IconFont("service1","e921"))
            add(IconFont("error1","e917"))
            add(IconFont("error2","e918"))
            add(IconFont("yes1","e919"))
            add(IconFont("yes2","e91a"))
            add(IconFont("caution1","e91b"))
            add(IconFont("caution2","e91c"))
            add(IconFont("info1","e91d"))
            add(IconFont("info2","e91e"))
            add(IconFont("problem1","e91f"))
            add(IconFont("problem2","e920"))
            add(IconFont("category2","e916"))
            add(IconFont("user","e900"))
            add(IconFont("home","e904"))
            add(IconFont("category1","e913"))
            add(IconFont("message","e911"))
            add(IconFont("delete","e901"))
            add(IconFont("call","e902"))
            add(IconFont("return","e903"))
            add(IconFont("share1","e905"))
            add(IconFont("share2","e906"))
            add(IconFont("shopping_cart","e907"))
            add(IconFont("add","e908"))
            add(IconFont("deduct","e909"))
            add(IconFont("recycle","e90a"))
            add(IconFont("filter","e90b"))
            add(IconFont("mobile_phone","e90d"))
            add(IconFont("search","e90e"))
            add(IconFont("up","e90f"))
            add(IconFont("down","e910"))
            add(IconFont("mini_program","e912"))
            add(IconFont("right","e914"))
            add(IconFont("left","e915"))
            add(IconFont("qq2","e961"))
            add(IconFont("wrench","e962"))
            add(IconFont("honor","e963"))
            add(IconFont("fire","e964"))
            add(IconFont("about_us","e965"))
            add(IconFont("qzone2","e966"))
            add(IconFont("contacts","e967"))
            add(IconFont("cus","e968"))
            add(IconFont("verify2","e97b"))
            add(IconFont("brands","e96a"))
            add(IconFont("erweima1","e97d"))
            add(IconFont("erweima2","e97e"))
            mHandler.sendEmptyMessage(101)
        }
    }

}