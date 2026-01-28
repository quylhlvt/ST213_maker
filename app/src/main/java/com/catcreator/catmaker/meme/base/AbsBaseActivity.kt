package com.catcreator.catmaker.meme.base

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.catcreator.catmaker.meme.utils.SystemUtils
import com.catcreator.catmaker.meme.utils.music.MusicLocal
import com.catcreator.catmaker.meme.utils.showSystemUI
import com.catcreator.catmaker.meme.R

abstract class AbsBaseActivity<V : ViewDataBinding> : AppCompatActivity() {
    lateinit var binding: V

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SystemUtils.setLocale(this)
        binding = DataBindingUtil.setContentView(this, getLayoutId())
        initView()
        initAction()
    }

    override fun onResume() {
        super.onResume()
            showSystemUI()
    }

    override fun onRestart() {
        super.onRestart()
        SystemUtils.setLocale(this)
    }
    abstract fun getLayoutId(): Int
    abstract fun initView()
    abstract fun initAction()
    fun initMusic( musicButtons: ImageView){
        var isPlayer= MusicLocal.status(this@AbsBaseActivity)
        isPlayer = ! isPlayer
        MusicLocal.toggle(this@AbsBaseActivity,isPlayer)
        updateMusicIcon(musicButtons)
    }
    open fun updateMusicIcon( musicButtons: ImageView) {
        val isPlaying = MusicLocal.status(this@AbsBaseActivity)
        if (isPlaying) {
            musicButtons.setImageResource( R.drawable.ic_music_app_true)
            MusicLocal.play(this)
        }
        else {
            musicButtons.setImageResource(R.drawable.ic_music_app_false)
            MusicLocal.pause()
        }
    }

}