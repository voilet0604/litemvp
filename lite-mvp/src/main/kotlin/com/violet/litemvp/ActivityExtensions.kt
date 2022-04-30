package com.violet.litemvp

import android.view.View
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * 对FragmentActivity的AppDelegate进行扩展辅助创建和销毁
 * 懒加载创建：
 * 如果FragmentActivity的View == null，
 * 那么就需要AppDelegate 提供View 给FragmentActivity
 * 反之则，FragmentActivity 给AppDelegate提供View
 *
 * 自动销毁：
 * 监听生命周期onDestroy事件，
 * 把AppDelegate 置为 null
 * 防止内存泄漏
 */
@MainThread
inline fun <reified T : AppDelegate> FragmentActivity.viewDelegates(
    crossinline viewDelegateFactory: () -> ViewDelegateFactory<T> = {

        DefaultBindViewDelegateFactory(
            layoutInflater,
            T::class
        )
    }
): ReadOnlyProperty<AppCompatActivity, T> = object : ReadOnlyProperty<AppCompatActivity, T> {

    private var viewDelegate: T? = null

    init {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                viewDelegate = null
                lifecycle.removeObserver(this)
            }
        })
    }

    override fun getValue(thisRef: AppCompatActivity, property: KProperty<*>): T {
        viewDelegate?.let { return it }

        //操作ViewBinding要在onDestroy() super方法之前，不然提示错误
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            error("Should not attempt to get viewDelegate when Fragment views are destroyed. The fragment has already called onDestroyView() at this point.")
        }

        val contentParent: ViewGroup =
            (window.decorView as ViewGroup).findViewById(android.R.id.content)

        val activityView: View? =
            if (contentParent.childCount < 1) {
                null
            } else {
                contentParent.getChildAt(0)
            }

        return try {
            viewDelegateFactory().getViewViewDelegate().also { vb ->
                activityView?.let { vb.setRootView(it) }
                vb.setFragmentManager(supportFragmentManager)
                vb.setLifecycleOwner(thisRef)
                this.viewDelegate = vb
                lifecycle.addObserver(vb)
            }
        } catch (e: Exception) {
            error("${this@viewDelegates} create ${T::class.java.simpleName} error ${e.message}")
        }
    }
}


