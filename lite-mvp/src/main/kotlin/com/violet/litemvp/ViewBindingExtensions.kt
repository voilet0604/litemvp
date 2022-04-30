package com.violet.litemvp

import android.view.View
import android.view.ViewGroup
import androidx.annotation.MainThread
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.viewbinding.ViewBinding
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty


/**
 * 对AppDelegate的ViewBinding进行扩展辅助创建和销毁
 * 懒加载创建：
 * 自动判断root View 已经创建了。
 * 那么使用ViewBinding::bind方法
 * 如果没有则使用ViewBinding::inflater方法
 *
 * 自动销毁：
 * 监听生命周期onDestroy事件，
 * 把ViewBinding 置为 null
 */
@MainThread
inline fun <reified T : ViewBinding> AppDelegate.viewBindings(
    crossinline viewBindingFactory: (View?) -> ViewBindingFactory<T> = {
        if (it == null) DefaultInflateViewBindingFactory(
            layoutInflater,
            T::class
        ) else DefaultBindViewBindingFactory(it, T::class)
    }
): ReadOnlyProperty<AppDelegate, T> = object : ReadOnlyProperty<AppDelegate, T> {

    private var binding: T? = null

    override fun getValue(thisRef: AppDelegate, property: KProperty<*>): T {
        binding?.let { return it }

        //操作ViewBinding要在onDestroy() super方法之前，不然提示错误
        if (!getLifecycle().currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            error("Should not attempt to get bindings when AppDelegate views are destroyed. The fragment has already called onDestroy() at this point.")
        }

        return viewBindingFactory(mRootView).getViewBindings().also { viewBinding ->
            if (viewBinding is ViewDataBinding) {
                viewBinding.lifecycleOwner = getLifecycleOwner()
            }
            this.binding = viewBinding
            getLifecycle().addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    super.onDestroy(owner)
                    (binding as? ViewDataBinding)?.unbind()
                    binding = null
                    getLifecycle().removeObserver(this)
                }
            })
        }
    }
}

/**
 * FragmentActivity 扩展viewBindings方法
 * 懒加载创建：
 * 自动判断root View 已经创建了。
 * 那么使用ViewBinding::bind方法
 * 如果没有则使用ViewBinding::inflater方法
 *
 * 自动销毁：
 * 监听生命周期onDestroy事件，
 * 把ViewBinding 置为 null
 * 使用方法：
 *
```
    class MapActivity: AppCompatActivity(R.layout.activity_map) {

        private val viewBindings by viewBindingsBind<ActivityMapBinding>()

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
        }
    }
```
 */
@MainThread
inline fun <reified T : ViewBinding> FragmentActivity.viewBindings(
    crossinline viewBindingFactory: (view: View?) -> ViewBindingFactory<T> = {
        if(it == null) DefaultInflateViewBindingFactory(layoutInflater, T::class) else DefaultBindViewBindingFactory(it, T::class)
    }
): ReadOnlyProperty<FragmentActivity, T> = object : ReadOnlyProperty<FragmentActivity, T> {

    private var binding: T? = null

    init {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                (binding as? ViewDataBinding)?.unbind()
                binding = null
                lifecycle.removeObserver(this)
            }
        })
    }

    override fun getValue(thisRef: FragmentActivity, property: KProperty<*>): T {
        binding?.let { return it }

        //操作ViewBinding要在onDestroy() super方法之前，不然提示错误
        if (!lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            error("Should not attempt to get bindings when Activity views are destroyed. The activity has already called onDestroy() at this point.")
        }

        val contentParent: ViewGroup =
            (thisRef.window.decorView as ViewGroup).findViewById(android.R.id.content)
        val activityView: View? = if (contentParent.childCount < 1) {
            null
        } else {
            contentParent.getChildAt(0)
        }
        return try {
            viewBindingFactory(activityView).getViewBindings().also { vb ->
                if (vb is ViewDataBinding) {
                    vb.lifecycleOwner = thisRef
                }
                this.binding = vb
            }
        } catch (e: Exception) {
            error("ViewBinding::${T::class.java.simpleName} disagree with $activityView")
        }
    }
}

/**
 * 对Fragment 扩展viewBindings方法
 * 懒加载创建：
 * 自动判断root View 已经创建了。
 * 那么使用ViewBinding::bind方法
 * 如果没有则使用ViewBinding::inflater方法
 *
 * 自动销毁：
 * 监听生命周期onDestroyView事件，
 * 把ViewBinding 置为 null
 * 防止内存泄漏
 */
@MainThread
inline fun <reified T : ViewBinding> Fragment.viewBindings(
    crossinline viewBindingFactory: (View?) -> ViewBindingFactory<T> = {
        if(it == null) DefaultInflateViewBindingFactory(layoutInflater, T::class) else DefaultBindViewBindingFactory(it, T::class)
    }
): ReadOnlyProperty<Fragment, T> = object : ReadOnlyProperty<Fragment, T> {

    private var binding: T? = null

    init {

        viewLifecycleOwnerLiveData.observe(this@viewBindings, Observer { viewLifecycleOwner ->
            viewLifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
                //监听Fragment onDestroyView
                override fun onDestroy(owner: LifecycleOwner) {
                    (binding as? ViewDataBinding)?.unbind()
                    binding = null
                }
            })
        })
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        binding?.let { return it }

        // 操作ViewBinding 必须在onCreateView之后，所以给一个错误提示
        val viewLifecycleOwner = try {
            thisRef.viewLifecycleOwner
        } catch (e: IllegalStateException) {
            error("Should not attempt to get bindings when Fragment views haven't been created yet. The fragment has not called onCreateView() at this point.")
        }
        //操作ViewBinding要在onDestroyView() super方法之前，不然提示错误
        if (!viewLifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.INITIALIZED)) {
            error("Should not attempt to get bindings when Fragment views are destroyed. The fragment has already called onDestroyView() at this point.")
        }

        return viewBindingFactory(thisRef.view).getViewBindings().also { viewBinding ->
            if (viewBinding is ViewDataBinding) {
                viewBinding.lifecycleOwner = viewLifecycleOwner
            }
            this.binding = viewBinding
        }
    }
}