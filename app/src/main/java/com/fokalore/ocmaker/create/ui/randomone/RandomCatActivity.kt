package com.fokalore.ocmaker.create.ui.randomone

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Rect
import android.graphics.RectF
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.fokalore.ocmaker.create.R
import com.fokalore.ocmaker.create.base.AbsBaseActivity
import com.fokalore.ocmaker.create.data.callapi.reponse.DataResponse
import com.fokalore.ocmaker.create.data.callapi.reponse.LoadingStatus
import com.fokalore.ocmaker.create.data.model.BodyPartModel
import com.fokalore.ocmaker.create.data.model.ColorModel
import com.fokalore.ocmaker.create.data.model.CustomModel
import com.fokalore.ocmaker.create.data.repository.ApiRepository
import com.fokalore.ocmaker.create.databinding.ActivityQuickMixBinding
import com.fokalore.ocmaker.create.databinding.ActivityRandomCatBinding
import com.fokalore.ocmaker.create.ui.customview.CustomviewActivity
import com.fokalore.ocmaker.create.utils.CONST
import com.fokalore.ocmaker.create.utils.DataHelper
import com.fokalore.ocmaker.create.utils.newIntent
import com.fokalore.ocmaker.create.utils.onSingleClick
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.Job
import javax.inject.Inject

@AndroidEntryPoint
class RandomCatActivity : AbsBaseActivity<ActivityRandomCatBinding>() {
    @Inject
    lateinit var apiRepository: ApiRepository
    private var randomModel: CustomModel? = null
    private var randomCoords: ArrayList<ArrayList<Int>>? = null
    private var listImageSortView: ArrayList<String>? = null
    private var characterBitmap: Bitmap? = null
    private var loadingJob: Job? = null

    override fun getLayoutId(): Int = R.layout.activity_random_cat
    private var checkCallingDataOnline = false

    private val networkReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val connectivityManager =
                context?.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo

            if (!checkCallingDataOnline) {
                if (networkInfo != null && networkInfo.isConnected) {
                    // Kiểm tra đã có data online chưa
                    var hasOnlineData = false
                    DataHelper.arrBlackCentered.forEach {
                        if (it.checkDataOnline) {
                            hasOnlineData = true
                            return@forEach
                        }
                    }

                    // Nếu chưa có data online thì gọi API
                    if (!hasOnlineData) {
                        DataHelper.callApi(apiRepository)
                    }
                }
            }
        }
    }

    override fun initView() {
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkReceiver, filter)
        observeDataOnline()
        // Kiểm tra data
        if (DataHelper.arrBg.isEmpty() || DataHelper.arrBlackCentered.isEmpty()) {
            finish()
            return
        }

        // Random 1 nhân vật
//        randomizeCharacter()
    }

    private fun observeDataOnline() {
        DataHelper.arrDataOnline.observe(this) {
            it?.let {
                when (it.loadingStatus) {
                    LoadingStatus.Loading -> {
                        checkCallingDataOnline = true
                    }

                    LoadingStatus.Success -> {
                        if (DataHelper.arrBlackCentered.isNotEmpty() && !DataHelper.arrBlackCentered[0].checkDataOnline) {
                            checkCallingDataOnline = false
                            val listA = (it as DataResponse.DataSuccess).body ?: return@observe
                            checkCallingDataOnline = true
                            val sortedMap = listA
                                .toList()
                                .sortedBy { (_, list) ->
                                    list.firstOrNull()?.level ?: Int.MAX_VALUE
                                }
                                .toMap()
                            sortedMap.forEach { key, list ->
                                var a = arrayListOf<BodyPartModel>()
                                list.forEachIndexed { index, x10 ->
                                    var b = arrayListOf<ColorModel>()
                                    x10.colorArray.split(",").forEach { coler ->
                                        var c = arrayListOf<String>()
                                        if (coler == "") {
                                            for (i in 1..x10.quantity) {
                                                c.add(CONST.BASE_URL + "${CONST.BASE_CONNECT}/${x10.position}/${x10.parts}/${i}.png")
                                            }
                                            b.add(
                                                ColorModel(
                                                    "#",
                                                    c
                                                )
                                            )
                                        } else {
                                            for (i in 1..x10.quantity) {
                                                c.add(CONST.BASE_URL + "${CONST.BASE_CONNECT}/${x10.position}/${x10.parts}/${coler}/${i}.png")
                                            }
                                            b.add(
                                                ColorModel(
                                                    coler,
                                                    c
                                                )
                                            )
                                        }
                                    }
                                    a.add(
                                        BodyPartModel(
                                            "${CONST.BASE_URL}${CONST.BASE_CONNECT}$key/${x10.parts}/nav.png",
                                            b
                                        )
                                    )
                                }
                                var dataModel =
                                    CustomModel(
                                        "${CONST.BASE_URL}${CONST.BASE_CONNECT}$key/avatar.png",
                                        a,
                                        true
                                    )
                                dataModel.bodyPart.forEach { mbodyPath ->
                                    if (mbodyPath.icon.substringBeforeLast("/")
                                            .substringAfterLast("/").substringAfter("-") == "1"
                                    ) {
                                        mbodyPath.listPath.forEach {
                                            if (it.listPath[0] != "dice") {
                                                it.listPath.add(0, "dice")
                                            }
                                        }
                                    } else {
                                        mbodyPath.listPath.forEach {
                                            if (it.listPath[0] != "none") {
                                                it.listPath.add(0, "none")
                                                it.listPath.add(1, "dice")
                                            }
                                        }
                                    }
                                }
                                DataHelper.arrBlackCentered.add(0, dataModel)
                            }
                        }
                        checkCallingDataOnline = false
                    }

                    LoadingStatus.Error -> {
                        checkCallingDataOnline = false
                    }

                    else -> {
                        checkCallingDataOnline = true
                    }
                }
            }
        }
    }

    private fun randomizeCharacter() {
        // Cancel job trước đó nếu đang chạy
        loadingJob?.cancel()

        loadingJob = lifecycleScope.launch(Dispatchers.Default) {
            // Kiểm tra kết nối mạng
            val connectivityManager = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            val hasInternet = networkInfo != null && networkInfo.isConnected

            // Nếu có mạng: random all, không có mạng: chỉ random offline
            val availableModels = if (hasInternet) {
                DataHelper.arrBlackCentered
            } else {
                DataHelper.arrBlackCentered.filter { !it.checkDataOnline }
            }

            // Kiểm tra danh sách có rỗng không
            if (availableModels.isEmpty()) {
                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = android.view.View.GONE
                }
                return@launch
            }

            // Random model từ danh sách available
            randomModel = availableModels.random()

            randomModel?.let { model ->
                // Tạo list image sort
                val list = ArrayList<String>().apply {
                    repeat(model.bodyPart.size) { add("") }
                }

                model.bodyPart.forEach {
                    val (x, _) = it.icon.substringBeforeLast("/")
                        .substringAfterLast("/")
                        .split("-")
                        .map { it.toInt() }
                    list[x - 1] = it.icon
                }
                listImageSortView = list

                // Random coords
                val coords = arrayListOf<ArrayList<Int>>()
                list.forEach { data ->
                    val bodyPart = model.bodyPart.find { it.icon == data }
                    val pair = if (bodyPart != null) {
                        val path = bodyPart.listPath[0].listPath
                        val color = bodyPart.listPath
                        val randomValue = if (path[0] == "none") {
                            if (path.size > 3) (2 until path.size).random() else 2
                        } else {
                            if (path.size > 2) (1 until path.size).random() else 1
                        }
                        val randomColor = (0 until color.size).random()
                        arrayListOf(randomValue, randomColor)
                    } else {
                        arrayListOf(-1, -1)
                    }
                    coords.add(pair)
                }
                randomCoords = coords

                // Load bitmap
                loadCharacterBitmap(model, list, coords)
            }
        }
    }

    private suspend fun loadCharacterBitmap(
        model: CustomModel,
        imageList: ArrayList<String>,
        coords: ArrayList<ArrayList<Int>>
    ) = withContext(Dispatchers.IO) {
        try {
            // Tính size
            val targetSize = calculateTargetSize(model, imageList, coords)
            val width = targetSize.first
            val height = targetSize.second

            // Tạo bitmap tổng - sử dụng mutable bitmap
            val merged = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(merged)
            val dstRect = RectF(0f, 0f, width.toFloat(), height.toFloat())

            // Ẩn progress bar và hiển thị image
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = android.view.View.GONE
                binding.imgCharacter.visibility = android.view.View.VISIBLE
            }

            var layerCount = 0
            val loadedBitmaps = mutableListOf<Bitmap>() // Track loaded bitmaps để không bị recycle

            // Load và vẽ từng layer
            imageList.forEachIndexed { index, icon ->
                if (icon.isEmpty()) {
                    android.util.Log.d("RandomCat", "Skip layer $index: empty icon")
                    return@forEachIndexed
                }

                val coord = coords.getOrNull(index)
                if (coord == null) {
                    android.util.Log.d("RandomCat", "Skip layer $index: no coord")
                    return@forEachIndexed
                }

                if (coord[0] >= 0) {
                    val bodyPart = model.bodyPart.find { it.icon == icon }
                    val targetPath = bodyPart
                        ?.listPath?.getOrNull(coord[1])
                        ?.listPath?.getOrNull(coord[0])

                    android.util.Log.d("RandomCat", "Layer $index: path=$targetPath")

                    if (!targetPath.isNullOrEmpty() && targetPath != "none" && targetPath != "dice") {
                        try {
                            // Load bitmap cho layer này
                            val bmp = Glide.with(this@RandomCatActivity)
                                .asBitmap()
                                .load(targetPath)
                                .override(width, height)
                                .encodeQuality(60)
                                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                .submit()
                                .get()

                            loadedBitmaps.add(bmp) // Lưu lại để track

                            // Vẽ layer lên canvas
                            val srcRect = Rect(0, 0, bmp.width, bmp.height)
                            canvas.drawBitmap(bmp, srcRect, dstRect, null)
                            layerCount++

                            android.util.Log.d("RandomCat", "Drew layer $index successfully")

                            // Tạo copy của bitmap hiện tại để hiển thị (tránh race condition)
                            val displayBitmap = merged.copy(Bitmap.Config.ARGB_8888, false)

                            // Update UI sau mỗi layer
                            withContext(Dispatchers.Main) {
                                // Lưu bitmap cũ để recycle sau
                                val oldBitmap = binding.imgCharacter.drawable?.let {
                                    if (it is android.graphics.drawable.BitmapDrawable) it.bitmap else null
                                }

                                binding.imgCharacter.setImageBitmap(displayBitmap)

                                // Recycle bitmap cũ nếu không phải là bitmap chính
                                if (oldBitmap != null && oldBitmap != merged && oldBitmap != characterBitmap) {
                                    oldBitmap.recycle()
                                }
                            }

                            // Thêm delay nhỏ để UI có thời gian render
                            kotlinx.coroutines.delay(10)

                        } catch (e: Exception) {
                            android.util.Log.e("RandomCat", "Error loading layer $index: ${e.message}")
                        }
                    }
                }
            }

            android.util.Log.d("RandomCat", "Total layers drawn: $layerCount")

            // Set bitmap cuối cùng
            withContext(Dispatchers.Main) {
                binding.imgCharacter.setImageBitmap(merged)
                Glide.with(this@RandomCatActivity).load(R.drawable.img_random_btn_randomone).into(binding.imageView)
                binding.btnRandomize.isEnabled = true
            }

            // Lưu bitmap cuối cùng
            characterBitmap?.recycle()
            characterBitmap = merged


        } catch (e: Exception) {
            e.printStackTrace()
            android.util.Log.e("RandomCat", "Error in loadCharacterBitmap: ${e.message}")
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = android.view.View.GONE
            }
        }
    }

    private suspend fun calculateTargetSize(
        model: CustomModel,
        listImageSortView: List<String>,
        coordSet: ArrayList<ArrayList<Int>>
    ): Pair<Int, Int> = withContext(Dispatchers.IO) {
        try {
            for (index in listImageSortView.indices) {
                val icon = listImageSortView[index]
                val coord = coordSet[index]

                if (coord[0] > 0) {
                    val targetPath = model.bodyPart
                        .find { it.icon == icon }
                        ?.listPath?.getOrNull(coord[1])
                        ?.listPath?.getOrNull(coord[0])
                    if (!targetPath.isNullOrEmpty()) {
                        val bmp = Glide.with(this@RandomCatActivity)
                            .asBitmap()
                            .load(targetPath)
                            .encodeQuality(80)
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .submit()
                            .get()
                        return@withContext Pair(bmp.width / 2, bmp.height / 2)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return@withContext Pair(256, 256)
    }

    override fun initAction() {
        binding.apply {
            // Back button
            imvBack.onSingleClick { finish() }

            // Random lại button
            btnRandomize.onSingleClick {
                btnRandomize.isEnabled = false
                Glide.with(this@RandomCatActivity).asGif().load(R.drawable.gif).into(imageView)
                // Clear image trước
                imgCharacter.setImageBitmap(null)
                progressBar.visibility = android.view.View.VISIBLE
                imgCharacter.visibility = android.view.View.GONE
                // Recycle bitmap cũ
                characterBitmap?.recycle()
                characterBitmap = null
                // Random lại
                randomizeCharacter()
            }
            // Click vào character để edit
            imvNext.onSingleClick {
                randomModel?.let { model ->
                    val index = DataHelper.arrBlackCentered.indexOf(model)
                    if (index != -1) {
                        startActivity(
                            newIntent(this@RandomCatActivity, CustomviewActivity::class.java)
                                .putExtra("data", index)
                                .putExtra("arr", randomCoords)
                        )
                    }
                }
            }
        }
    }


}