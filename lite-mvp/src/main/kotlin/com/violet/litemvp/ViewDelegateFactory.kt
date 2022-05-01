package com.violet.litemvp

import android.view.LayoutInflater
import kotlin.reflect.KClass

interface ViewDelegateFactory<T : AppDelegate> {

    fun getViewViewDelegate(): T

}

@Suppress("UNCHECKED_CAST")
class DefaultBindViewDelegateFactory<T : AppDelegate>(
    layoutInflater: LayoutInflater,
    clazz: KClass<T>
) : ViewDelegateFactory<T> {

    private val vb: T =
        clazz.java.getDeclaredConstructor(LayoutInflater::class.java).newInstance(layoutInflater)

    override fun getViewViewDelegate(): T {
        return vb
    }
}