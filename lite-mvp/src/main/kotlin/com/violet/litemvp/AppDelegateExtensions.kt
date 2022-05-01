package com.violet.litemvp


import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * 扩展AppDelegate，对创建的对象，进行自动释放内存
 */
inline fun <reified T : Any> AppDelegate.autoCleans(objectFactory: ()-> T) = autoCleans(objectFactory.invoke())

/**
 * 扩展AppDelegate，对创建的对象，进行自动释放内存
 * ReadWriteProperty 可读可写，
 * 使用的时候 修饰符用val 那么代表只读属性
 * 修饰符用var，那么代表属性可读可写。
 * 如果强制只读的话，那么可以 实现 ReadOnlyProperty接口，该接口只有getValue方法
 */
inline fun <reified T : Any> AppDelegate.autoCleans(obj: T): ReadWriteProperty<AppDelegate, T> = object :
    ReadWriteProperty<AppDelegate, T> {

    private var objects: T? = null

    override fun setValue(thisRef: AppDelegate, property: KProperty<*>, value: T) {
        objects = value
    }

    override fun getValue(thisRef: AppDelegate, property: KProperty<*>): T {
        objects?.let { return it }
        objects = obj
        getLifecycle().addObserver(object : DefaultLifecycleObserver {
            override fun onDestroy(owner: LifecycleOwner) {
                super.onDestroy(owner)
                //TODO 如果集合类型，应该先clean
                objects = null
                getLifecycle().removeObserver(this)
            }
        })
        return objects!!
    }
}