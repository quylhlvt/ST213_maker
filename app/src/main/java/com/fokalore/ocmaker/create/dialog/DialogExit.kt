package com.fokalore.ocmaker.create.dialog

import android.app.Activity
import androidx.core.content.ContextCompat
import com.fokalore.ocmaker.create.base.BaseDialog
import com.fokalore.ocmaker.create.utils.hide
import com.fokalore.ocmaker.create.utils.onSingleClick
import com.fokalore.ocmaker.create.utils.show
import com.fokalore.ocmaker.create.R
import com.fokalore.ocmaker.create.databinding.DialogExitBinding

class DialogExit(context: Activity, var type: String) :
    BaseDialog<DialogExitBinding>(context, false) {
    var onClick: (() -> Unit)? = null
    override fun getContentView(): Int = R.layout.dialog_exit

    override fun initView() {
//        binding.txtContent.gradientHorizontal(
//            "#01579B".toColorInt(),
//            "#2686C6".toColorInt())
        binding.txtTitle.setTextColor(ContextCompat.getColor(context,R.color.white))

        when(type){
            "exit" ->{
                binding.txtTitle.text = context.getString(R.string.exit)
                binding.txtTitle.isSelected = true
                binding.txtContent.post {  }
                binding.txtContent.text = context.getString(R.string.haven_t_saved_it_yet_do_you_want_to_exit)
//                binding.nativeAds.show()
//                Admob.getInstance().loadNativeAd(
//                    context,
//                    context.getString(R.string.native_dialog),
//                    binding.nativeAds,
//                    com.lvt.ads.R.layout.ads_native_avg2
//                )
            }
            "network"->{
                binding.txtTitle.text = context.getString(R.string.no_internet)
                binding.txtTitle.isSelected = true
                binding.btnYes.hide()
                binding.btnNo.hide()
                binding.btnOk.show()
                binding.txtContent.hide()
                binding.txtContent1.show()
                binding.txtContent1.text = context.getString(R.string.please_check_your_network_connection)
            }
            "loadingnetwork"->{
                binding.txtTitle.text =
                    context.getString(R.string.no_internet)
                binding.txtTitle.isSelected = true
                binding.btnYes.hide()
                binding.btnNo.hide()
                binding.btnOk.show()
                binding.txtContent.hide()
                binding.txtContent1.show()
                binding.txtContent1.text = context.getString(R.string.please_check_your_network_connection)
            }
            "reset"->{
                binding.txtTitle.text = context.getString(R.string.reset)
                binding.txtTitle.isSelected = true
                binding.txtContent.text = context.getString(R.string.do_you_want_to_reset_all)
            }
            "delete"->{
                binding.txtTitle.text = context.getString(R.string.delete)
                binding.txtTitle.isSelected = true
                binding.txtContent.text = context.getString(R.string.do_you_want_to_delete_this_item)
            }
            "awaitdata"->{
                binding.btnYes.hide()
                binding.btnNo.hide()
                binding.txtTitle.text = context.getString(R.string.data)
                binding.txtTitle.isSelected = true
                binding.txtContent.hide()
                binding.txtContent1.show()
                binding.txtContent1.text = context.getString(R.string.please_wait_a_few_seconds_for_data_to_load)
            }
            "awaitdataHome"->{
                binding.btnYes.hide()
                binding.btnNo.hide()
                binding.btnOk.show()
                binding.txtTitle.text = context.getString(R.string.no_internet)
                binding.txtTitle.isSelected = true
                binding.txtContent.hide()
                binding.txtContent1.show()
                binding.txtContent1.text = context.getString(R.string.please_connect_to_the_internet_to_download_more_data)
            }
        }
    }

    override fun bindView() {
        binding.apply {
            btnYes.onSingleClick {
                onClick?.invoke()
                dismiss()
            }
            btnNo.onSingleClick {
                dismiss()
            }
            btnOk.onSingleClick {
                dismiss()
            }
        }
    }
}