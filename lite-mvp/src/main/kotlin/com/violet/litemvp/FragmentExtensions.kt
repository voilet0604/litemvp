package com.violet.litemvp

import androidx.annotation.MainThread
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import timber.log.Timber
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * 对Fragment 扩展viewDelegates方法
 * 懒加载创建：
 * 如果Fragment的View == null，
 * 那么就需要AppDelegate 提供View 给Fragment
 * 反之则，Fragment 给AppDelegate提供View
 *
 * 自动销毁：
 * 监听生命周期onDestroyView事件，
 * 把AppDelegate 置为 null
 * 防止内存泄漏
 */
@MainThread
inline fun <reified T : AppDelegate> Fragment.viewDelegates(
    crossinline viewDelegateFactory: () -> ViewDelegateFactory<T> = {
        DefaultBindViewDelegateFactory(
            view,
            layoutInflater,
            childFragmentManager,
            T::class
        )
    }
): ReadOnlyProperty<Fragment, T> = object : ReadOnlyProperty<Fragment, T> {

    private var viewDelegate: T? = null

    init {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                Timber.d("lifecycle: ${this@viewDelegates} 的 $viewDelegate 置位null")
                viewDelegate = null
                lifecycle.removeObserver(this)
            }
        })
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        viewDelegate?.let { return it }

        //操作ViewBinding要在onDestroy() super方法之前，不然提示错误
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            error("Should not attempt to get viewDelegate when Fragment views are destroyed. The fragment has already called onDestroyView() at this point.")
        }

        return try {
            viewDelegateFactory().getViewViewDelegate().also { vb ->
                this.viewDelegate = vb
                this.viewDelegate?.setLifecycleOwner(viewLifecycleOwner)
                lifecycle.addObserver(vb)
            }
        } catch (e: Exception) {
            error("AppDelegate::${T::class.java.simpleName} create error")
        }
    }
}