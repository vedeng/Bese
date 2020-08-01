package com.bese.widget.addressselect

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Color
import android.os.Handler
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.interpolator.view.animation.FastOutSlowInInterpolator
import com.bese.R
import java.util.*

/**
 * 地址选择器
 */
class AddressSelector(private val context: Context, private var loadPresenter: DataLoadPresenter?,
                      province: Region? = null, city: Region? = null, district: Region? = null, street: Region? = null)
    : AdapterView.OnItemClickListener {

    /** 默认是省份标签 */
    private var tabIndex = INDEX_TAB_PROVINCE

    /** 是否有地址数据需要回显 */
    private var echo = false

    /**
     * 获得view
     */
    var view: View? = null
        private set
    
    private var indicator: View? = null
    private var tabLayout: LinearLayout? = null
    private var textViewProvince: TextView? = null
    private var textViewCity: TextView? = null
    private var textViewDistrict: TextView? = null
    private var textViewStreet: TextView? = null
    private var progressBar: ProgressBar? = null
    private var listView: ListView? = null
    private var provinceAdapter: ProvinceAdapter? = null
    private var cityAdapter: CityAdapter? = null
    private var districtAdapter: DistrictAdapter? = null
    private var streetAdapter: StreetAdapter? = null
    private var onAddressSelectedListener: OnAddressSelectedListener? = null
    private var dialogCloseListener: OnDialogCloseListener? = null
    private var dataHelper: AddressDataHelper? = null
    private var provinceList: ArrayList<Region>? = null
    private var cityList: ArrayList<Region>? = null
    private var districtList: ArrayList<Region>? = null
    private var streetList: ArrayList<Region>? = null
    var selectProvince: Region? = null
    var selectCity: Region? = null
    var selectDistrict: Region? = null
    var selectStreet: Region? = null
    var selectedColor = Color.parseColor("#0099FF")
    var unSelectedColor = Color.parseColor("#000000")
    var indicatorColor = Color.parseColor("#0099FF")
    var addressSelectIcon = R.mipmap.icon_item_checked

    private val handler = Handler(Handler.Callback { msg ->
            when (msg.what) {
                WHAT_PROVINCES_PROVIDED -> {
                    provinceList = msg.obj as? ArrayList<Region>
                    provinceAdapter?.notifyDataSetChanged()
                    if (provinceList?.isNotEmpty() == true) {
                        if (echo) {
                            reloadAddress(ADDRESS_LIST_DEEP_TYPE)
                        } else {    // 更新索引
                            tabIndex = INDEX_TAB_PROVINCE
                            listView?.adapter = provinceAdapter
                        }
                    }
                }
                WHAT_CITIES_PROVIDED -> {
                    cityList = msg.obj as? ArrayList<Region>
                    cityAdapter?.notifyDataSetChanged()
                    if (cityList?.isNotEmpty() == true) {
                        listView?.adapter = cityAdapter
                        tabIndex = INDEX_TAB_CITY
                        if (echo) {
                            reloadAddress(ADDRESS_LIST_DEEP_TYPE)
                        } else {
                            if (ADDRESS_LIST_DEEP_TYPE >= DEEP_THREE) {
                                tabIndex = INDEX_TAB_CITY
                                listView?.adapter = cityAdapter
                            }
                        }
                    } else {
                        // 次级无内容，回调
                        selectOver(false)
                    }
                }
                WHAT_COUNTIES_PROVIDED -> {
                    districtList = msg.obj as? ArrayList<Region>
                    districtAdapter?.notifyDataSetChanged()
                    if (districtList?.isNotEmpty() == true) {
                        listView?.adapter = districtAdapter
                        tabIndex = INDEX_TAB_DISTRICT
                        if (echo) {
                            reloadAddress(ADDRESS_LIST_DEEP_TYPE)
                        } else {
                            if (ADDRESS_LIST_DEEP_TYPE >= DEEP_FOUR) {
                                tabIndex = INDEX_TAB_DISTRICT
                                listView?.adapter = districtAdapter               // 以次级内容更新列表
                            }
                        }
                    } else {
                        selectOver(false)
                    }
                }
                WHAT_STREETS_PROVIDED -> {
                    streetList = msg.obj as? ArrayList<Region>
                    streetAdapter?.notifyDataSetChanged()
                    if (streetList?.isNotEmpty() == true) {
                        listView?.adapter = streetAdapter
                        tabIndex = INDEX_TAB_STREET
                        if (echo) {
                            reloadAddress(ADDRESS_LIST_DEEP_TYPE)
                        }
                    } else {
                        selectOver(false)
                    }
                }
                WHAT_NET_ERROR -> {
                }
                else -> {
                }
            }
            updateLoadingState()
            updateTabUI(true)
            true
        })

    /**
     * 处理数据回显，并发3个请求，全部完成后显示第三Tab
     *
     * @param level 第几个Tab
     */
    private fun reloadAddress(level: Int) {
        when (level) {
            1 -> {
                if (provinceList?.isNotEmpty() == true) {
                    echo = false
                    listView?.adapter = provinceAdapter
                    tabIndex = INDEX_TAB_PROVINCE
                    updateTabUI(false)
                }
            }
            2 -> {
                if (provinceList?.isNotEmpty() == true && cityList?.isNotEmpty() == true) {
                    echo = false
                    listView?.adapter = cityAdapter
                    tabIndex = INDEX_TAB_CITY
                    updateTabUI(false)
                }
            }
            3 -> {
                if (provinceList?.isNotEmpty() == true && cityList?.isNotEmpty() == true && districtList?.isNotEmpty() == true) {
                    echo = false
                    listView?.adapter = districtAdapter
                    tabIndex = INDEX_TAB_DISTRICT
                    updateTabUI(false)
                }
            }
            4 -> {
                if (provinceList?.isNotEmpty() == true && cityList?.isNotEmpty() == true && districtList?.isNotEmpty() == true && streetList?.isNotEmpty() == true) {
                    echo = false
                    listView?.adapter = streetAdapter
                    tabIndex = INDEX_TAB_STREET
                    updateTabUI(false)
                }
            }
            else -> { }
        }
    }

    /**
     * 更新进度条
     */
    private fun updateLoadingState() {
        val adapter = listView?.adapter
        if (adapter != null) {
            val itemCount = adapter.count
            progressBar?.visibility = if (itemCount > 0) View.GONE else View.VISIBLE
        } else {
            progressBar?.visibility = View.GONE
        }
    }

    /**
     * 初始化布局
     */
    private fun initViews() {
        view = LayoutInflater.from(context).inflate(R.layout.address_selector, null)
        //进度条
        progressBar = view?.findViewById<View>(R.id.progressBar) as? ProgressBar
        val closeIcon = view?.findViewById<View>(R.id.tv_cancel) as? TextView
        listView = view?.findViewById<View>(R.id.listView) as? ListView
        //指示器
        indicator = view?.findViewById(R.id.indicator)
        indicator?.setBackgroundColor(indicatorColor)

        tabLayout = view?.findViewById<View>(R.id.layout_tab) as? LinearLayout
        //省份
        textViewProvince = view?.findViewById<View>(R.id.textViewProvince) as? TextView
        //城市
        textViewCity = view?.findViewById<View>(R.id.textViewCity) as? TextView
        //区 区县
        textViewDistrict = view?.findViewById<View>(R.id.textViewDistrict) as? TextView
        //街道
        textViewStreet = view?.findViewById<View>(R.id.textViewStreet) as? TextView
        textViewProvince?.setOnClickListener(OnProvinceTabClickListener())
        textViewCity?.setOnClickListener(OnCityTabClickListener())
        textViewDistrict?.setOnClickListener(OnDistrictTabClickListener())
        textViewStreet?.setOnClickListener(OnStreetTabClickListener())
        listView?.onItemClickListener = this
        closeIcon?.setOnClickListener(OnCloseClickListener())
        updateIndicator()
    }

    /**
     * 初始化adapter
     */
    private fun initAdapters() {
        provinceAdapter = ProvinceAdapter()
        cityAdapter = CityAdapter()
        districtAdapter = DistrictAdapter()
        streetAdapter = StreetAdapter()
    }

    /**
     * 更新标签样式：即使在选择省份时，街道信息也可能会展示。
     * 此处独立处理每一个Tab的样式，在作出选择时，需要手动清空下级已选择内容
     * 处理按钮是否可点，文字是否隐藏，文字文案，文字颜色，游标位置
     */
    private fun updateTabUI(fromSelect: Boolean) {
        //按钮能不能点击 false 不能点击 true 能点击
        textViewProvince?.isEnabled = tabIndex != INDEX_TAB_PROVINCE
        textViewCity?.isEnabled = tabIndex != INDEX_TAB_CITY
        textViewDistrict?.isEnabled = tabIndex != INDEX_TAB_DISTRICT
        textViewStreet?.isEnabled = tabIndex != INDEX_TAB_STREET

        // 展示文案
        textViewProvince?.text = if (TextUtils.isEmpty(selectProvince?.regionName)) PLEASE_SELECT else selectProvince?.regionName
        textViewCity?.text = if (TextUtils.isEmpty(selectCity?.regionName)) PLEASE_SELECT else selectCity?.regionName
        textViewDistrict?.text = if (TextUtils.isEmpty(selectDistrict?.regionName)) PLEASE_SELECT else selectDistrict?.regionName
        textViewStreet?.text = if (TextUtils.isEmpty(selectStreet?.regionName)) PLEASE_SELECT else selectStreet?.regionName

        // 是否隐藏：省始终可见，街道有数据四级时可见。
        if (tabIndex == INDEX_TAB_STREET) {             // Tab 在 街道， 省市区Tab都可见
            textViewCity?.visibility = View.VISIBLE
            textViewDistrict?.visibility = View.VISIBLE
            textViewStreet?.visibility = View.VISIBLE
        } else if (tabIndex == INDEX_TAB_DISTRICT) {             // Tab 在 区县， 省市Tab可见
            textViewCity?.visibility = View.VISIBLE
            textViewDistrict?.visibility = if (ADDRESS_LIST_DEEP_TYPE >= DEEP_THREE) View.VISIBLE else View.GONE
            textViewStreet?.visibility = if (ADDRESS_LIST_DEEP_TYPE >= DEEP_FOUR) View.VISIBLE else View.GONE
            if (TextUtils.isEmpty(selectDistrict?.regionName)) {
                textViewStreet?.visibility = View.GONE
            }
        } else if (tabIndex == INDEX_TAB_CITY) {            // Tab 在 市， 省Tab可见
            textViewCity?.visibility = View.VISIBLE
            textViewDistrict?.visibility = if (ADDRESS_LIST_DEEP_TYPE >= DEEP_THREE) View.VISIBLE else View.GONE
            textViewStreet?.visibility = if (ADDRESS_LIST_DEEP_TYPE >= DEEP_FOUR) View.VISIBLE else View.GONE
            if (TextUtils.isEmpty(selectCity?.regionName)) {
                // 街道在四级时也许可见，但选择后必不可见
                textViewDistrict?.visibility = View.GONE
                textViewStreet?.visibility = View.GONE
            }
        } else if (tabIndex == INDEX_TAB_PROVINCE) {            // Tab 在 省， 只有省Tab可见
            textViewDistrict?.visibility = if (ADDRESS_LIST_DEEP_TYPE >= DEEP_THREE) View.VISIBLE else View.GONE
            textViewStreet?.visibility = if (ADDRESS_LIST_DEEP_TYPE >= DEEP_FOUR) View.VISIBLE else View.GONE
            if (TextUtils.isEmpty(selectProvince?.regionName)) {
                textViewCity?.visibility = View.GONE
                textViewDistrict?.visibility = View.GONE
                textViewStreet?.visibility = View.GONE
            }
        }

        // 更新文字颜色
        if (selectedColor != 0 && unSelectedColor != 0) {
            updateTabTextColor()
        }

        // 更新游标位置
        updateIndicator()
    }

    /**
     * 更新字体的颜色
     */
    private fun updateTabTextColor() {
        if (tabIndex != INDEX_TAB_PROVINCE) {
            textViewProvince?.setTextColor(unSelectedColor)
        } else {
            textViewProvince?.setTextColor(selectedColor)
        }
        if (tabIndex != INDEX_TAB_CITY) {
            textViewCity?.setTextColor(unSelectedColor)
        } else {
            textViewCity?.setTextColor(selectedColor)
        }
        if (tabIndex != INDEX_TAB_DISTRICT) {
            textViewDistrict?.setTextColor(unSelectedColor)
        } else {
            textViewDistrict?.setTextColor(selectedColor)
        }
        if (tabIndex != INDEX_TAB_STREET) {
            textViewStreet?.setTextColor(unSelectedColor)
        } else {
            textViewStreet?.setTextColor(selectedColor)
        }
    }

    /**
     * 更新tab 指示器
     */
    private fun updateIndicator() {
        view?.post {
            when (tabIndex) {
                INDEX_TAB_PROVINCE -> buildIndicatorAnimatorTowards(textViewProvince).start()
                INDEX_TAB_CITY -> buildIndicatorAnimatorTowards(textViewCity).start()
                INDEX_TAB_DISTRICT -> buildIndicatorAnimatorTowards(textViewDistrict).start()
                INDEX_TAB_STREET -> buildIndicatorAnimatorTowards(textViewStreet).start()
                else -> { }
            }
        }
    }

    /**
     * tab 来回切换的动画
     */
    private fun buildIndicatorAnimatorTowards(tab: TextView?): AnimatorSet {
        val set = AnimatorSet()
        set.interpolator = FastOutSlowInInterpolator()
        if (tab == null) return set
        indicator?.let {
            val xAnimator = ObjectAnimator.ofFloat(it, "X", it.x, tab.x)
            val params = it.layoutParams
            val widthAnimator = ValueAnimator.ofInt(params.width, tab.measuredWidth)
            widthAnimator.addUpdateListener { animation ->
                params.width = animation.animatedValue as Int
                indicator?.layoutParams = params
            }
            set.playTogether(xAnimator, widthAnimator)
        }
        return set
    }

    /**
     * 点击省份的监听
     */
    internal inner class OnProvinceTabClickListener :
        View.OnClickListener {
        override fun onClick(v: View) {
            tabIndex = INDEX_TAB_PROVINCE
            listView?.adapter = provinceAdapter
            updateTabUI(false)
        }
    }

    /**
     * 点击城市的监听
     */
    internal inner class OnCityTabClickListener : View.OnClickListener {
        override fun onClick(v: View) {
            tabIndex = INDEX_TAB_CITY
            listView?.adapter = cityAdapter
            updateTabUI(false)
        }
    }

    /**
     * 点击区 区县的监听
     */
    internal inner class OnDistrictTabClickListener :
        View.OnClickListener {
        override fun onClick(v: View) {
            tabIndex = INDEX_TAB_DISTRICT
            listView?.adapter = districtAdapter
            updateTabUI(false)
        }
    }

    /**
     * 点击街道的监听
     */
    internal inner class OnStreetTabClickListener :
        View.OnClickListener {
        override fun onClick(v: View) {
            tabIndex = INDEX_TAB_STREET
            listView?.adapter = streetAdapter
            updateTabUI(false)
        }
    }

    /**
     * 点击右边关闭dialog监听
     */
    internal inner class OnCloseClickListener : View.OnClickListener {
        override fun onClick(view: View) {
            if (dialogCloseListener != null) {
                dialogCloseListener?.dialogClose()
            }
        }
    }

    override fun onItemClick(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
        when (tabIndex) {
            INDEX_TAB_PROVINCE -> {
                selectProvince = provinceAdapter?.getItem(position) ?: Region()
                updateTabUI(false)                     // 更新当前级别及子级标签文本
                loadCitiesWith(selectProvince?.regionId)            //根据省份的id,查询城市列表
                // 清空子级数据
                selectCity = Region()
                selectDistrict = Region()
                selectStreet = Region()
                cityList = null
                districtList = null
                streetList = null
                // 更新选中
                provinceAdapter?.notifyDataSetChanged()
                cityAdapter?.notifyDataSetChanged()
                districtAdapter?.notifyDataSetChanged()
                streetAdapter?.notifyDataSetChanged()
            }
            INDEX_TAB_CITY -> {
                selectCity = cityAdapter?.getItem(position) ?: Region()
                updateTabUI(false)
                // 更新选中效果
                cityAdapter?.notifyDataSetChanged()
                if (ADDRESS_LIST_DEEP_TYPE >= DEEP_THREE) {
                    loadCountiesWith(selectCity?.regionId)          //根据城市的id,从数据库中查询城市列表
                    // 清空子级数据
                    selectDistrict = Region()
                    selectStreet = Region()
                    districtList = null
                    streetList = null
                    districtAdapter?.notifyDataSetChanged()
                    streetAdapter?.notifyDataSetChanged()
                } else {
                    selectOver(true)
                }
            }
            INDEX_TAB_DISTRICT -> {
                selectDistrict = districtAdapter?.getItem(position) ?: Region()
                updateTabUI(false)
                // 更新选中效果
                districtAdapter?.notifyDataSetChanged()
                if (ADDRESS_LIST_DEEP_TYPE >= DEEP_FOUR) {
                    loadStreetsWith(selectDistrict?.regionId)
                    selectStreet = Region()
                    streetList = null
                    streetAdapter?.notifyDataSetChanged()
                } else {
                    selectOver(true)
                }
            }
            INDEX_TAB_STREET -> {
                selectStreet = streetAdapter?.getItem(position)
                updateTabUI(false)
                streetAdapter?.notifyDataSetChanged()
                selectOver(true)
            }
            else -> { }
        }
    }

    /**
     * 查询省份列表
     */
    private fun loadProvinces() {
        progressBar?.visibility = View.VISIBLE
        dataHelper?.getProvinceList("1")
    }

    /**
     * 根据省份id查询城市列表
     *
     * @param provinceId 省份id
     */
    private fun loadCitiesWith(provinceId: Int?) {
        progressBar?.visibility = View.VISIBLE
        dataHelper?.getCityList(provinceId?.toString())
    }

    /**
     * 根据城市id查询区县列表
     *
     * @param cityId 城市id
     */
    private fun loadCountiesWith(cityId: Int?) {
        progressBar?.visibility = View.VISIBLE
        dataHelper?.getDistrictList(cityId?.toString())
    }

    /**
     * 根据区县id查询区县列表
     *
     * @param districtId 区县id
     */
    private fun loadStreetsWith(districtId: Int?) {
        progressBar?.visibility = View.VISIBLE
        dataHelper?.getStreetList(districtId?.toString())
    }

    /**
     * 省份 城市 区县 街道 都选中完 后的回调
     */
    private fun selectOver(shouldCloseDialog: Boolean) {
        if (onAddressSelectedListener != null) {
            onAddressSelectedListener?.onAddressSelected(selectProvince, selectCity, selectDistrict, selectStreet)
            if (shouldCloseDialog && dialogCloseListener != null) {
                dialogCloseListener?.dialogClose()
            }
        }
    }

    /**
     * 省份的adapter
     */
    internal inner class ProvinceAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return provinceList?.size ?: 0
        }

        override fun getItem(position: Int): Region? {
            return provinceList?.get(position)
        }

        override fun getItemId(position: Int): Long {
            return getItem(position)?.regionId?.toLong() ?: 0L
        }

        override fun getView(position: Int, view: View?, parent: ViewGroup): View? {
            var convertView = view
            val holder: Holder
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.context).inflate(R.layout.address_selector_item_area, parent, false)
                holder = Holder()
                holder.areaName = convertView.findViewById(R.id.tv_area_name)
                holder.selectIcon = convertView.findViewById(R.id.icon_select)
                convertView.tag = holder
            } else {
                holder = convertView.tag as Holder
            }
            val r = getItem(position)
            if (!TextUtils.isEmpty(r?.regionName)) {
                holder.areaName?.text = r?.regionName
                val checked = r?.regionName == selectProvince?.regionName
                holder.areaName?.isEnabled = !checked
                holder.selectIcon?.visibility = if (checked) View.VISIBLE else View.GONE
                if (checked) holder.selectIcon?.setImageResource(addressSelectIcon)
            }
            return convertView
        }

        internal inner class Holder {
            var areaName: TextView? = null
            var selectIcon: ImageView? = null
        }
    }

    /**
     * 城市的adapter
     */
    internal inner class CityAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return cityList?.size ?: 0
        }

        override fun getItem(position: Int): Region? {
            return cityList?.get(position)
        }

        override fun getItemId(position: Int): Long {
            return getItem(position)?.regionId?.toLong() ?: 0L
        }

        override fun getView(position: Int, view: View?, parent: ViewGroup): View? {
            var convertView = view
            val holder: Holder
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.context).inflate(R.layout.address_selector_item_area, parent, false)
                holder = Holder()
                holder.areaName = convertView.findViewById(R.id.tv_area_name)
                holder.selectIcon = convertView.findViewById(R.id.icon_select)
                convertView.tag = holder
            } else {
                holder = convertView.tag as Holder
            }
            val r = getItem(position)
            if (!TextUtils.isEmpty(r?.regionName)) {
                holder.areaName?.text = r?.regionName
                val checked = r?.regionName == selectCity?.regionName
                holder.areaName?.isEnabled = !checked
                holder.selectIcon?.visibility = if (checked) View.VISIBLE else View.GONE
                if (checked) holder.selectIcon?.setImageResource(addressSelectIcon)
            }
            return convertView
        }

        internal inner class Holder {
            var areaName: TextView? = null
            var selectIcon: ImageView? = null
        }
    }

    /**
     * 区县的adapter
     */
    internal inner class DistrictAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return districtList?.size ?: 0
        }

        override fun getItem(position: Int): Region? {
            return districtList?.get(position)
        }

        override fun getItemId(position: Int): Long {
            return getItem(position)?.regionId?.toLong() ?: 0L
        }

        override fun getView(position: Int, view: View?, parent: ViewGroup): View? {
            var convertView = view
            val holder: Holder
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.context).inflate(R.layout.address_selector_item_area, parent, false)
                holder = Holder()
                holder.areaName = convertView.findViewById(R.id.tv_area_name)
                holder.selectIcon = convertView.findViewById(R.id.icon_select)
                convertView.tag = holder
            } else {
                holder = convertView.tag as Holder
            }
            val r = getItem(position)
            if (!TextUtils.isEmpty(r?.regionName)) {
                holder.areaName?.text = r?.regionName
                val checked = r?.regionName == selectDistrict?.regionName
                holder.areaName?.isEnabled = !checked
                holder.selectIcon?.visibility = if (checked) View.VISIBLE else View.GONE
                if (checked) holder.selectIcon?.setImageResource(addressSelectIcon)
            }
            return convertView
        }

        internal inner class Holder {
            var areaName: TextView? = null
            var selectIcon: ImageView? = null
        }
    }

    /**
     * 街道的adapter
     */
    internal inner class StreetAdapter : BaseAdapter() {
        override fun getCount(): Int {
            return streetList?.size ?: 0
        }

        override fun getItem(position: Int): Region? {
            return streetList?.get(position)
        }

        override fun getItemId(position: Int): Long {
            return getItem(position)?.regionId?.toLong() ?: 0L
        }

        override fun getView(position: Int, view: View?, parent: ViewGroup): View? {
            var convertView = view
            val holder: Holder
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.context).inflate(R.layout.address_selector_item_area, parent, false)
                holder = Holder()
                holder.areaName = convertView.findViewById(R.id.tv_area_name)
                holder.selectIcon = convertView.findViewById(R.id.icon_select)
                convertView.tag = holder
            } else {
                holder = convertView.tag as Holder
            }
            val r = getItem(position)
            if (!TextUtils.isEmpty(r?.regionName)) {
                holder.areaName?.text = r?.regionName
                val checked = r?.regionName == selectStreet?.regionName
                holder.areaName?.isEnabled = !checked
                holder.selectIcon?.visibility = if (checked) View.VISIBLE else View.GONE
                if (checked) holder.selectIcon?.setImageResource(addressSelectIcon)
            }
            return convertView
        }

        internal inner class Holder {
            var areaName: TextView? = null
            var selectIcon: ImageView? = null
        }
    }

    interface OnDialogCloseListener {
        fun dialogClose()
    }

    /**
     * 设置close监听
     */
    fun setOnDialogCloseListener(listener: OnDialogCloseListener?) {
        dialogCloseListener = listener
    }

    /**
     * 设置地址监听
     */
    fun setOnAddressSelectedListener(listener: OnAddressSelectedListener?) {
        onAddressSelectedListener = listener
    }

    fun setAddressDeep(deepType: Int = 3) {
        ADDRESS_LIST_DEEP_TYPE = when (deepType) {
            2 -> DEEP_TWO
            4 -> DEEP_FOUR
            else -> DEEP_THREE
        }
    }

    companion object {
        /** 省份标志 */
        private const val INDEX_TAB_PROVINCE = 0
        /** 城市标志 */
        private const val INDEX_TAB_CITY = 1
        /** 区县标志 */
        private const val INDEX_TAB_DISTRICT = 2
        /** 街道标志 */
        private const val INDEX_TAB_STREET = 3
        /** 未初始化下标值 */
        private const val INDEX_INVALID = -1
        const val WHAT_PROVINCES_PROVIDED = 1
        const val WHAT_CITIES_PROVIDED = 2
        const val WHAT_COUNTIES_PROVIDED = 3
        const val WHAT_STREETS_PROVIDED = 4
        const val WHAT_NET_ERROR = 5

        /**
         * 定义省市区选择的深度类型
         * 如果是普通型1，就是省市区三级。如果是深度型2，就是省市区街道4级
         */
        const val DEEP_TWO = 2
        const val DEEP_THREE = 3
        const val DEEP_FOUR = 4
        private var ADDRESS_LIST_DEEP_TYPE = DEEP_THREE

        /** 未选中的字体展示 */
        private const val PLEASE_SELECT = "请选择"
    }

    init {
        dataHelper = AddressDataHelper(handler)
        dataHelper?.setDataLoad(loadPresenter)
        initViews()
        initAdapters()
        province?.let { p ->
            echo = true
            selectProvince = p
            textViewProvince?.text = p.regionName
            city?.let {  c ->
                selectCity = c
                textViewCity?.text = c.regionName
                if (ADDRESS_LIST_DEEP_TYPE >= DEEP_THREE) {
                    district?.let { d ->
                        selectDistrict = d
                        textViewDistrict?.text = d.regionName
                        if (ADDRESS_LIST_DEEP_TYPE >= DEEP_FOUR) {
                            street?.let { s ->
                                selectStreet = s
                                textViewStreet?.text = s.regionName
                            }
                            loadStreetsWith(d.regionId)
                        }
                    }
                    loadCountiesWith(c.regionId)
                }
            }
            loadCitiesWith(p.regionId)
        }
        loadProvinces()
    }
}