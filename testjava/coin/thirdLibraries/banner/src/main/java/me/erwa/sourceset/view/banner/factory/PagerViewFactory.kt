package me.erwa.sourceset.view.banner.factory

import android.app.Activity
import android.content.Context
import android.os.Build
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.RelativeLayout
import me.erwa.sourceset.R
import me.erwa.sourceset.view.banner.IBannerViewInstance
import me.erwa.sourceset.view.banner.IPagerViewFactory
import me.erwa.sourceset.view.banner.IPagerViewInstance
import me.erwa.sourceset.view.banner.pager.PagerRecyclerView

/**
 * @author: drawf
 * @date: 2019-08-04
 * @see: <a href=""></a>
 * @description: 生成PagerView实例的工厂
 */
internal class PagerViewFactory(
    private val bannerView: IBannerViewInstance,
    private val intervalUseViewPager: Boolean = false
) : IPagerViewFactory {

    /**
     * 工厂根据参数创建对应PagerView实例
     */
    override fun getPagerView(): IPagerViewInstance {
        return if (bannerView.isSmoothMode()) {
            casePagerRecycler(true)
        } else {
            if (intervalUseViewPager) {
                //这里可以根据需要用ViewPager做底层实现
                throw IllegalStateException("这里未使用ViewPager做底层实现")
            } else {
                casePagerRecycler(false)
            }
        }
    }

    /**
     * 处理PagerRecyclerView
     */
    private fun casePagerRecycler(isSmoothMode: Boolean): IPagerViewInstance {
        val recyclerView = PagerRecyclerView(bannerView.getContext())
        recyclerView.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(bannerView.getContext(), androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false)
        recyclerView.adapter = object : androidx.recyclerview.widget.RecyclerView.Adapter<androidx.recyclerview.widget.RecyclerView.ViewHolder>() {
            override fun getItemCount(): Int {
                return Int.MAX_VALUE
            }

            override fun onBindViewHolder(holder: androidx.recyclerview.widget.RecyclerView.ViewHolder, position: Int) {
                if (!isActivityDestroyed(holder.itemView.context)) {
                    val realPos = position % bannerView.getCount()
                    bannerView.onBindView(holder.itemView.findViewById(R.id.id_real_item_view), realPos)
                }
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): androidx.recyclerview.widget.RecyclerView.ViewHolder {
                val itemWrapper = LayoutInflater.from(parent.context).inflate(
                    R.layout.layout_banner_item_wrapper,
                    parent,
                    false
                ) as RelativeLayout

                //处理ItemViewWrapper的宽
                itemWrapper.layoutParams.width = bannerView.getItemViewWidth() + bannerView.getItemViewMargin()

                //外部实际的ItemView
                val itemView = bannerView.getItemView(parent.context)
                itemView.id = R.id.id_real_item_view
                val ivParams = RelativeLayout.LayoutParams(
                    bannerView.getItemViewWidth(),
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                ivParams.addRule(bannerView.getItemViewAlign())

                //添加ItemView到Wrapper
                itemWrapper.addView(itemView, ivParams)
                return object : androidx.recyclerview.widget.RecyclerView.ViewHolder(itemWrapper) {}
            }
        }

        //初始化位置
        recyclerView.scrollToPosition(bannerView.getCount() * 100)
        recyclerView.setSmoothMode(isSmoothMode)

        return recyclerView
    }

    private fun isActivityDestroyed(context: Context?): Boolean {
        if (context == null) return true
        if (context !is Activity) {
            throw IllegalStateException("context 应该为 Activity实例")
        }
        if (context.isFinishing || (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1 && context.isDestroyed)) {
            return true
        }
        return false
    }

}
