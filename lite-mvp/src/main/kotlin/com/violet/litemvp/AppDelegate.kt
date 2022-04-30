package com.violet.litemvp

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.*
import androidx.viewbinding.ViewBinding


interface AppDelegateLifecycle {

    //onCreate
    //Activity.onCreate -> AppDelegate.onCreate
    //Fragment.onCreateView -> AppDelegate.onCreate -> Fragment.onViewCreated
    fun onViewCreated()

    fun onStart()

    fun onResume()

    fun onPause()

    fun onStop()

    fun onDestroy()
}

//DefaultLifecycleObserver
abstract class AppDelegate constructor(val layoutInflater: LayoutInflater): LifecycleEventObserver, AppDelegateLifecycle, LifecycleOwner {

    //扩展需要感知AppDelegate的生命周期，所以才用到这个类
    private val lifecycleRegistry: LifecycleRegistry
        get() = LifecycleRegistry(this)

    private var fragmentManager: FragmentManager? = null

    fun setFragmentManager(manager: FragmentManager) {
        this.fragmentManager = manager
    }

    var mRootView: View? = null

    val requireFragmentManager: FragmentManager get() {
        if(fragmentManager == null) {
            error("You use fragmentManager more than after onCreate, before the onDestroy.")
        }
        return fragmentManager!!
    }

    open val requireView: View get() {
        mRootView?.let { return it }
        mRootView = onCreateView()
        return mRootView!!
    }

    protected val appContext: Context by lazy { requireView.context.applicationContext }

    protected val requiredActivity: Activity
        get() = mRootView!!.context as Activity

    open fun onCreateView(): View {
        return mRootView!!
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        lifecycleRegistry.currentState = source.lifecycle.currentState
        when(event) {
            Lifecycle.Event.ON_CREATE -> onViewCreated()
            Lifecycle.Event.ON_START -> onStart()
            Lifecycle.Event.ON_RESUME -> onResume()
            Lifecycle.Event.ON_PAUSE -> onPause()
            Lifecycle.Event.ON_STOP -> onStop()
            Lifecycle.Event.ON_DESTROY -> onDestroy()
            else -> Unit
        }
    }

    private var mLifecycleOwner:LifecycleOwner? = null

    fun setLifecycleOwner(lifecycleOwner: LifecycleOwner) {
        this.mLifecycleOwner = lifecycleOwner
    }

    override fun getLifecycle(): Lifecycle {
        mLifecycleOwner?.let { return it.lifecycle }
        return lifecycleRegistry
    }

    override fun onStart() = Unit

    override fun onResume() = Unit

    override fun onPause() = Unit

    override fun onStop() = Unit

    override fun onDestroy() {
        mLifecycleOwner = null
        mRootView = null
        fragmentManager = null
    }

    fun setRootView(requireView: View) {
        this.mRootView = requireView
    }
}
