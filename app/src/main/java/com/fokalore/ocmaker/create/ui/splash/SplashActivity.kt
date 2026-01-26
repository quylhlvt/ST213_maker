package com.fokalore.ocmaker.create.ui.splash

import android.content.Intent
import androidx.lifecycle.lifecycleScope
import com.fokalore.ocmaker.create.base.AbsBaseActivity
import com.fokalore.ocmaker.create.data.callapi.reponse.DataResponse
import com.fokalore.ocmaker.create.data.callapi.reponse.LoadingStatus
import com.fokalore.ocmaker.create.data.model.BodyPartModel
import com.fokalore.ocmaker.create.data.model.ColorModel
import com.fokalore.ocmaker.create.data.model.CustomModel
import com.fokalore.ocmaker.create.data.repository.ApiRepository
import com.fokalore.ocmaker.create.ui.language.LanguageActivity
import com.fokalore.ocmaker.create.ui.tutorial.TutorialActivity
import com.fokalore.ocmaker.create.utils.CONST
import com.fokalore.ocmaker.create.utils.DataHelper
import com.fokalore.ocmaker.create.utils.DataHelper.getData
import com.fokalore.ocmaker.create.utils.SharedPreferenceUtils
import com.fokalore.ocmaker.create.R
import com.fokalore.ocmaker.create.databinding.ActivitySplashBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.collections.forEach
import kotlin.collections.toList

@AndroidEntryPoint
class SplashActivity : AbsBaseActivity<ActivitySplashBinding>() {
    @Inject
    lateinit var apiRepository: ApiRepository
    @Inject
    lateinit var sharedPreferenceUtils: SharedPreferenceUtils
    private var minDelayPassed = false
    private var dataReady = false
    override fun getLayoutId(): Int = R.layout.activity_splash
    override fun initView() {
        // Observe data loading TRƯỚC khi load
        observeDataLoading()
        // Đợi tối thiểu 3 giây
        lifecycleScope.launch {
            delay(3000)
            minDelayPassed = true
            // Nếu data đã sẵn sàng thì chuyển màn ngay
            if (dataReady) {
                navigateToNextScreen()
            }
        }
    }

    override fun initAction() {
        // Bắt đầu load data
        lifecycleScope.launch(Dispatchers.IO) {
            getData(apiRepository)
        }
    }

    private fun observeDataLoading() {
        DataHelper.arrDataOnline.observe(this) { response ->
            response?.let {
                when (it.loadingStatus) {
                    LoadingStatus.Loading -> {
                        // Đang loading
                    }

                    LoadingStatus.Success -> {
                        // XỬ LÝ DATA ONLINE (logic từ MainActivity cũ)
                        if (DataHelper.arrBlackCentered.isNotEmpty() && !DataHelper.arrBlackCentered[0].checkDataOnline) {
                            val listA = (it as DataResponse.DataSuccess).body

                            if (listA != null) {
                                // Sort và merge data online
                                val sortedMap = listA
                                    .toList()
                                    .sortedBy { (_, list) ->
                                        list.firstOrNull()?.level ?: Int.MAX_VALUE
                                    }
                                    .toMap()

                                sortedMap.forEach { key, list ->
                                    val bodyPartList = arrayListOf<BodyPartModel>()

                                    list.forEach { x10 ->
                                        val colorList = arrayListOf<ColorModel>()

                                        x10.colorArray.split(",").forEach { color ->
                                            val pathList = arrayListOf<String>()

                                            if (color == "") {
                                                for (i in 1..x10.quantity) {
                                                    pathList.add(CONST.BASE_URL + "${CONST.BASE_CONNECT}/${x10.position}/${x10.parts}/${i}.png")
                                                }
                                                colorList.add(ColorModel("#", pathList))
                                            } else {
                                                for (i in 1..x10.quantity) {
                                                    pathList.add(CONST.BASE_URL + "${CONST.BASE_CONNECT}/${x10.position}/${x10.parts}/${color}/${i}.png")
                                                }
                                                colorList.add(ColorModel(color, pathList))
                                            }
                                        }

                                        bodyPartList.add(
                                            BodyPartModel(
                                                "${CONST.BASE_URL}${CONST.BASE_CONNECT}$key/${x10.parts}/nav.png",
                                                colorList
                                            )
                                        )
                                    }

                                    val dataModel = CustomModel(
                                        "${CONST.BASE_URL}${CONST.BASE_CONNECT}$key/avatar.png",
                                        bodyPartList,
                                        true
                                    )

                                    // Thêm "dice" và "none"
                                    dataModel.bodyPart.forEach { mbodyPath ->
                                        if (mbodyPath.icon.substringBeforeLast("/")
                                                .substringAfterLast("/").substringAfter("-") == "1"
                                        ) {
                                            mbodyPath.listPath.forEach { colorModel ->
                                                if (colorModel.listPath[0] != "dice") {
                                                    colorModel.listPath.add(0, "dice")
                                                }
                                            }
                                        } else {
                                            mbodyPath.listPath.forEach { colorModel ->
                                                if (colorModel.listPath[0] != "none") {
                                                    colorModel.listPath.add(0, "none")
                                                    colorModel.listPath.add(1, "dice")
                                                }
                                            }
                                        }
                                    }

                                    DataHelper.arrBlackCentered.add(0, dataModel)
                                }
                            }
                        }

                        // Set dataReady sau khi merge xong
                        dataReady = true

                        // Nếu đã qua 3 giây thì chuyển màn ngay
                        if (minDelayPassed) {
                            navigateToNextScreen()
                        }
                    }

                    LoadingStatus.Error -> {
                        // Nếu lỗi nhưng đã có data offline thì vẫn cho qua
                        if (DataHelper.arrBlackCentered.isNotEmpty()) {
                            dataReady = true
                            if (minDelayPassed) {
                                navigateToNextScreen()
                            }
                        } else {
                            // Thử load lại sau 2 giây
                            lifecycleScope.launch(Dispatchers.IO) {
                                delay(2000)
                                getData(apiRepository)
                            }
                        }
                    }
                    else -> {
                        // Loading hoặc trạng thái khác - đợi
                    }
                }
            }
        }
    }

    private fun navigateToNextScreen() {
        // Double-check: CHỈ navigate khi data thực sự sẵn sàng
        if (!dataReady || DataHelper.arrBlackCentered.isEmpty()) {
            return
        }

        if (!sharedPreferenceUtils.getBooleanValue(CONST.LANGUAGE)) {
            startActivity(Intent(this@SplashActivity, LanguageActivity::class.java))
        } else {
            startActivity(Intent(this@SplashActivity, TutorialActivity::class.java))
        }
        finish()
    }

    override fun onBackPressed() {
        // Không cho phép back ở splash
    }
}