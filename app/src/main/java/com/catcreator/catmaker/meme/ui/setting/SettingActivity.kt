package com.catcreator.catmaker.meme.ui.setting

import android.view.View
import com.catcreator.catmaker.meme.base.AbsBaseActivity
import com.catcreator.catmaker.meme.ui.language.LanguageActivity
import com.catcreator.catmaker.meme.utils.RATE
import com.catcreator.catmaker.meme.utils.SharedPreferenceUtils
import com.catcreator.catmaker.meme.utils.newIntent
import com.catcreator.catmaker.meme.utils.onSingleClick
import com.catcreator.catmaker.meme.utils.policy
import com.catcreator.catmaker.meme.utils.rateUs
import com.catcreator.catmaker.meme.utils.shareApp
import com.catcreator.catmaker.meme.utils.unItem
import com.catcreator.catmaker.meme.R
import com.catcreator.catmaker.meme.databinding.ActivitySettingBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingActivity : AbsBaseActivity<ActivitySettingBinding>() {
    @Inject
    lateinit var sharedPreferences: SharedPreferenceUtils
    override fun getLayoutId(): Int = R.layout.activity_setting

    override fun initView() {
        binding.titleSetting.isSelected = true
        updateMusicIcon(binding.imvMusic)
        if (sharedPreferences.getBooleanValue(RATE)) {
            binding.llRateUs.visibility = View.GONE
        }
        unItem = {
            binding.llRateUs.visibility = View.GONE
        }
    }

    override fun onStop() {
        super.onStop()
    }
    override fun initAction() {
        binding.apply {
            llLanguage.onSingleClick {
                startActivity(
                    newIntent(
                        applicationContext,
                        LanguageActivity::class.java
                    )
                )
            }
            imvMusic.onSingleClick {
                initMusic(imvMusic)
            }
            llRateUs.onSingleClick {
                rateUs(0)
            }
            llShareApp.onSingleClick {
                shareApp()
            }
            llPrivacy.onSingleClick {
                policy()
            }
            imvBack.onSingleClick {
                finish()
            }
        }
    }
}