package com.violet.litemvp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import com.violet.litemvp.databinding.ActivityMainBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class MainDelegate(layoutInflater: LayoutInflater): AppDelegate(layoutInflater) {

    private val mViewBinding by viewBindings<ActivityMainBinding>()

    override fun onViewCreated() {
        lifecycleScope.launch(Dispatchers.IO) {
            Log.e("MainDelegate", Thread.currentThread().name)
            delay(5000)
            Log.e("MainDelegate", "closed")
        }
    }

    fun update(text: String) {
        mViewBinding.textName.text = text
    }

}

class MainActivity : AppCompatActivity(R.layout.activity_main) {

    private val mViewDelegate by viewDelegates<MainDelegate>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewDelegate.update("text")
    }
}