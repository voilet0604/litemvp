package com.violet.litemvp

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import timber.log.Timber
import kotlin.properties.ReadOnlyProperty
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 扩展LifecycleOwner，对创建的对象，进行自动释放内存
 */
inline fun <reified T : Any> LifecycleOwner.autoCleans(objectFactory: ()-> T) = autoCleans(objectFactory.invoke())

/**
 * 扩展LifecycleOwner，对创建的对象，进行自动释放内存
 * 主要是对，Activity 和 Fragment、以及AppDelegate进行扩展
 * ReadWriteProperty 可读可写，
 * 使用的时候 修饰符用val 那么代表只读属性
 * 修饰符用var，那么代表属性可读可写。
 * 如果强制只读的话，那么可以 实现 ReadOnlyProperty接口，该接口只有getValue方法
 */
inline fun <reified T : Any> LifecycleOwner.autoCleans(obj: T): ReadWriteProperty<LifecycleOwner, T> = object :
    ReadWriteProperty<LifecycleOwner, T> {

    private var objects: T? = null

    init {
        lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                //TODO 如果集合类型，应该先clean
                objects = null
                lifecycle.removeObserver(this)
            }
        })
    }

    override fun setValue(thisRef: LifecycleOwner, property: KProperty<*>, value: T) {
        objects = value
    }

    override fun getValue(thisRef: LifecycleOwner, property: KProperty<*>): T {
        objects?.let { return it }
        objects = obj
        return objects!!
    }

    // 对象销毁，会回调这个方法
    protected fun finalize() {}
}