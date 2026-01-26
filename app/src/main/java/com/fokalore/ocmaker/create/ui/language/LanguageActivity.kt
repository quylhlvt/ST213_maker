package com.fokalore.ocmaker.create.ui.language

import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fokalore.ocmaker.create.base.AbsBaseActivity
import com.fokalore.ocmaker.create.data.model.LanguageModel
import com.fokalore.ocmaker.create.ui.main.MainActivity
import com.fokalore.ocmaker.create.ui.tutorial.TutorialActivity
import com.fokalore.ocmaker.create.utils.CONST
import com.fokalore.ocmaker.create.utils.DataHelper
import com.fokalore.ocmaker.create.utils.SharedPreferenceUtils
import com.fokalore.ocmaker.create.utils.SystemUtils
import com.fokalore.ocmaker.create.utils.onSingleClick
import com.fokalore.ocmaker.create.R
import com.fokalore.ocmaker.create.databinding.ActivityLanguageBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.text.equals

@AndroidEntryPoint
class LanguageActivity : AbsBaseActivity<ActivityLanguageBinding>() {
    lateinit var adapter: LanguageAdapter
    var codeLang: String? = null

    @Inject
    lateinit var providerSharedPreference: SharedPreferenceUtils


    override fun getLayoutId(): Int = R.layout.activity_language
    override fun initView() {

        codeLang = providerSharedPreference.getStringValue("language")
        if (codeLang.equals("")) {
            binding.icBack.visibility = View.GONE
            binding.tvTitle2.visibility = View.GONE
//            binding.imvDone.setImageResource(R.drawable.ic_tick_2)
        }else{
//            binding.imvDone.setImageResource(R.drawable.ic_tick)
            binding.tvTitle1.visibility = View.GONE
        }
        binding.rclLanguage.itemAnimator = null
        adapter = LanguageAdapter()
        setRecycleView()
    }

    override fun initAction() {

        binding.icBack.onSingleClick {
            finish()
        }
        binding.imvDone.onSingleClick {
            if (codeLang.equals("")) {
                Toast.makeText(
                    this,
                    getString(R.string.you_have_not_selected_anything_yet),
                    Toast.LENGTH_SHORT
                ).show()
            } else {
                SystemUtils.setPreLanguage(applicationContext, codeLang)
                providerSharedPreference.putStringValue("language", codeLang)
                if (SharedPreferenceUtils.Companion.getInstance(applicationContext).getBooleanValue(
                        CONST.LANGUAGE
                    )) {
                    var intent = Intent(
                        applicationContext,
                        MainActivity::class.java
                    )
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
                    finishAffinity()
                    startActivity(intent)
                } else {
                    SharedPreferenceUtils.Companion.getInstance(applicationContext)
                        .putBooleanValue(CONST.LANGUAGE, true)
                    var intent = Intent(applicationContext, TutorialActivity::class.java)
                    startActivity(intent)
                    finish()
                }
            }
        }
    }


    private fun setRecycleView() {
        var i = 0
        lateinit var x: LanguageModel
        if (!codeLang.equals("")) {
            DataHelper.listLanguage.forEach {
                DataHelper.listLanguage[i].active = false
                if (codeLang.equals(it.code)) {
                    x = DataHelper.listLanguage[i]
                    x.active = true
                }
                i++
            }

            DataHelper.listLanguage.remove(x)
            DataHelper.listLanguage.add(0, x)
        }
        adapter.getData(DataHelper.listLanguage)
        binding.rclLanguage.adapter = adapter
        val manager = GridLayoutManager(applicationContext, 1, RecyclerView.VERTICAL, false)
        binding.rclLanguage.layoutManager = manager

        adapter.onClick = {
            codeLang = DataHelper.listLanguage[it].code
        }
    }

    override fun onBackPressed() {
        DataHelper.listLanguage[DataHelper.positionLanguageOld].active = false
        DataHelper.positionLanguageOld = 0
        super.onBackPressed()
    }
}