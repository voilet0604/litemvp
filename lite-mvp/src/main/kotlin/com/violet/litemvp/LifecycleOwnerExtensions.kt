package com.violet.litemvp

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import timber.log.Timber
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty





/**
 * 扩展LifecycleOwner，对创建的对象，进行自动释放内存
 * 主要是对，Activity 和 Fragment、以及AppDelegate进行扩展
 * ReadWriteProperty 可读可写，
 * 使用的时候 修饰符用val 那么代表只读属性
 * 修饰符用var，那么代表属性可读可写。
 * 如果强制只读的话，那么可以 实现 ReadOnlyProperty接口，该接口只有getValue方法
 * 懒加载模式
 */
inline fun <reified T : Any> FragmentActivity.autoCleans(noinline objectFactory: ()-> T) : ReadWriteProperty<FragmentActivity, T> = object :
    ReadWriteProperty<FragmentActivity, T> {

    private var objects: T? = null

    init {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                //TODO 如果集合类型，应该先clean
                Timber.d("lifecycle ${this@autoCleans} 开始清理 $objects")
                objects = null
                lifecycle.removeObserver(this)
            }
        })
    }

    override fun setValue(thisRef: FragmentActivity, property: KProperty<*>, value: T) {
        objects = value
    }

    override fun getValue(thisRef: FragmentActivity, property: KProperty<*>): T {
        objects?.let { return it }
        objects = objectFactory()
        return objects!!
    }

    // 对象销毁，会回调这个方法
    protected fun finalize() {}
}

/**
 * 扩展LifecycleOwner，对创建的对象，进行自动释放内存
 */
inline fun <reified T : Any> FragmentActivity.autoCleans(obj: T): ReadWriteProperty<FragmentActivity, T> = autoCleans { obj }

/**
 * 扩展LifecycleOwner，对创建的对象，进行自动释放内存
 * 主要是对，Activity 和 Fragment、以及AppDelegate进行扩展
 * ReadWriteProperty 可读可写，
 * 使用的时候 修饰符用val 那么代表只读属性
 * 修饰符用var，那么代表属性可读可写。
 * 如果强制只读的话，那么可以 实现 ReadOnlyProperty接口，该接口只有getValue方法
 * 懒加载模式
 */
inline fun <reified T : Any> Fragment.autoCleans(noinline objectFactory: ()-> T) :ReadWriteProperty<Fragment, T> = object :
    ReadWriteProperty<Fragment, T> {

    private var objects: T? = null

    override fun setValue(thisRef: Fragment, property: KProperty<*>, value: T) {
        objects = value
    }

    override fun getValue(thisRef: Fragment, property: KProperty<*>): T {
        objects?.let { return it }
        objects = objectFactory()
        viewLifecycleOwnerLiveData.observe(thisRef) {
            it.lifecycle.addObserver(object : DefaultLifecycleObserver {
                override fun onDestroy(owner: LifecycleOwner) {
                    super.onDestroy(owner)
                    //TODO 如果集合类型，应该先clean
                    Timber.d("lifecycle ${this@autoCleans} 开始清理 $objects")
                    objects = null
                    lifecycle.removeObserver(this)
                }
            })
        }
        return objects!!
    }

    // 对象销毁，会回调这个方法
    protected fun finalize() {}
}

/**
 * 扩展LifecycleOwner，对创建的对象，进行自动释放内存
 */
inline fun <reified T : Any> Fragment.autoCleans(obj: T): ReadWriteProperty<Fragment, T> = autoCleans { obj }