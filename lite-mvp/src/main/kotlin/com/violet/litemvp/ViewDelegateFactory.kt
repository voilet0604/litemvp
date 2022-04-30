package com.violet.litemvp

import android.view.LayoutInflater
import android.view.View
import androidx.fragment.app.FragmentManager
import kotlin.reflect.KClass

interface ViewDelegateFactory<T : AppDelegate> {

    fun getViewViewDelegate(): T

}

@Suppress("UNCHECKED_CAST")
class DefaultBindViewDelegateFactory<T : AppDelegate>(
    view: View?,
    layoutInflater: LayoutInflater,
    fragmentManager: FragmentManager,
    clazz: KClass<T>
) : ViewDelegateFactory<T> {

    private val vb: T =
        clazz.java.getDeclaredConstructor(LayoutInflater::class.java).newInstance(layoutInflater)
            .apply {
                view?.let {
                    setRootView(it)
                }
                setFragmentManager(fragmentManager)
            }

    override fun getViewViewDelegate(): T {
        return vb
    }
}