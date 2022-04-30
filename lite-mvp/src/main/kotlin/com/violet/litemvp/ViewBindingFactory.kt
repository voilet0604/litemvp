package com.violet.litemvp

import android.view.LayoutInflater
import android.view.View
import androidx.viewbinding.ViewBinding
import kotlin.reflect.KClass

interface ViewBindingFactory<T: ViewBinding> {

    fun getViewBindings(): T
}

@Suppress("UNCHECKED_CAST")
class DefaultInflateViewBindingFactory<T: ViewBinding>(layoutInflater: LayoutInflater, clazz: KClass<T>): ViewBindingFactory<T> {

    private val vb: T = clazz.java.getMethod("inflate", LayoutInflater::class.java).invoke(null, layoutInflater) as T

    override fun getViewBindings(): T {
        return vb
    }
}

@Suppress("UNCHECKED_CAST")
class DefaultBindViewBindingFactory<T: ViewBinding>(view: View, clazz: KClass<T>): ViewBindingFactory<T> {

    private val vb: T = clazz.java.getMethod("bind", View::class.java).invoke(null, view) as T

    override fun getViewBindings(): T {
        return vb
    }
}