package com.fokalore.ocmaker.create.ui.setting

import android.view.View
import com.fokalore.ocmaker.create.base.AbsBaseActivity
import com.fokalore.ocmaker.create.ui.language.LanguageActivity
import com.fokalore.ocmaker.create.utils.RATE
import com.fokalore.ocmaker.create.utils.SharedPreferenceUtils
import com.fokalore.ocmaker.create.utils.newIntent
import com.fokalore.ocmaker.create.utils.onSingleClick
import com.fokalore.ocmaker.create.utils.policy
import com.fokalore.ocmaker.create.utils.rateUs
import com.fokalore.ocmaker.create.utils.shareApp
import com.fokalore.ocmaker.create.utils.unItem
import com.fokalore.ocmaker.create.R
import com.fokalore.ocmaker.create.databinding.ActivitySettingBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingActivity : AbsBaseActivity<ActivitySettingBinding>() {
    @Inject
    lateinit var sharedPreferences: SharedPreferenceUtils
    override fun getLayoutId(): Int = R.layout.activity_setting

    override fun initView() {
        binding.titleSetting.isSelected = true
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