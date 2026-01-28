package com.catcreator.catmaker.meme.ui.customview

import android.content.Intent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.catcreator.catmaker.meme.base.AbsBaseActivity
import com.catcreator.catmaker.meme.data.model.AvatarModel
import com.catcreator.catmaker.meme.data.model.BodyPartModel
import com.catcreator.catmaker.meme.dialog.DialogExit
import com.catcreator.catmaker.meme.ui.background.BackgroundActivity
import com.catcreator.catmaker.meme.utils.DataHelper
import com.catcreator.catmaker.meme.utils.fromList
import com.catcreator.catmaker.meme.utils.isInternetAvailable
import com.catcreator.catmaker.meme.utils.onSingleClick
import com.catcreator.catmaker.meme.utils.saveBitmap
import com.catcreator.catmaker.meme.utils.showToast
import com.catcreator.catmaker.meme.utils.viewToBitmap
import com.catcreator.catmaker.meme.R
import com.catcreator.catmaker.meme.databinding.ActivityCustomizeBinding
import com.catcreator.catmaker.meme.utils.DataHelper.arrBlackCentered
import dagger.hilt.android.AndroidEntryPoint
@AndroidEntryPoint
class CustomviewActivity : AbsBaseActivity<ActivityCustomizeBinding>() {
    val viewModel: CustomviewViewModel by viewModels()
    var arrShowColor = arrayListOf<Boolean>()
    var countRandom = 0
    val adapterColor by lazy {
        ColorAdapter()
    }
    val adapterNav by lazy {
        NavAdapter(this@CustomviewActivity)
    }
    val adapterPart by lazy {
        PartAdapter()
    }
    private var canSave = true
    override fun getLayoutId(): Int = R.layout.activity_customize
    override fun onRestart() {
        super.onRestart()
    }
    // Thêm map để cache index của icon (từ DataHelper.listImageSortView)
    private val iconToIndexMap = mutableMapOf<String, Int>()
//    private fun applyGradientToLoadingText() {
//        binding.txtContent.post {
//            binding.txtContent.gradientHorizontal(
//                "#01579B".toColorInt(),
//                "#2686C6".toColorInt()
//            )
//        }
//        binding.txtTitle.setTextColor(ContextCompat.getColor(this,R.color.white))
//
//    }

    // Call this when you show loading

    override fun initView() {
//        binding.txtContent.post {
//            binding.txtContent.gradientHorizontal(
//                startColor = "#01579B".toColorInt(),
//                endColor   = "#2686C6".toColorInt()
//            )
//        }

        binding.txtTitle.post { binding.txtTitle.isSelected =true }
        binding.txtTitle.setTextColor(ContextCompat.getColor(this,R.color.white))
        binding.btnSave.isSelected = true
        if (DataHelper.arrBlackCentered.size > 0) {
            binding.apply {
                rcvPart.adapter = adapterPart
                rcvPart.itemAnimator = null
                rcvColor.adapter = adapterColor
                rcvColor.itemAnimator = null
                rcvNav.adapter = adapterNav
                rcvNav.itemAnimator = null
                getData1()
                repeat(DataHelper.listImageSortView.size) {
                    listImg.add(AppCompatImageView(applicationContext).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )
                        binding.rl.addView(this)
                    })
                }
                adapterNav.posNav = 0
                adapterNav.submitList(listData)

                adapterColor.setPos(arrInt[0][1])
                updateColorSectionVisibility(0)
                if (listData[adapterNav.posNav].listPath.size == 1) {
                    binding.llColor.visibility = View.INVISIBLE
                    binding.imvShowColor.visibility = View.INVISIBLE
                } else {
                    binding.llColor.visibility = View.VISIBLE
                    binding.imvShowColor.visibility = View.VISIBLE
                    adapterColor.submitList(listData[adapterNav.posNav].listPath)
                }
                adapterPart.setPos(arrInt[0][0])
                adapterPart.submitList(listData[adapterNav.posNav].listPath[adapterColor.posColor].listPath)

                putImage(listData[adapterNav.posNav].icon, 1)
            }
            if (arrIntHottrend != null) {
                listData.forEachIndexed { index, partBody ->
                    putImage(
                        partBody.icon,
                        arrInt[index][0],
                        false,
                        index,
                        arrInt[index][1]
                    )
                }
                adapterPart.setPos(arrInt[adapterNav.posNav][0])
                adapterColor.setPos(arrInt[adapterNav.posNav][1])
                adapterPart.submitList(listData[adapterNav.posNav].listPath[adapterColor.posColor].listPath)
                if (listData[adapterNav.posNav].listPath.size == 1) {
                    binding.llColor.visibility = View.INVISIBLE
                    binding.imvShowColor.visibility = View.INVISIBLE
                } else {
                    binding.llColor.visibility = View.VISIBLE
                    binding.imvShowColor.visibility = View.VISIBLE
                    adapterColor.submitList(listData[adapterNav.posNav].listPath)
                }
                updateColorSectionVisibility(adapterNav.posNav)
            }
        } else {
            finish()
        }
    }

    var listImg = arrayListOf<AppCompatImageView>()
    fun putImage(
        icon: String,
        pos: Int,
        checkRestart: Boolean = false,
        posNav: Int? = null,
        posColor: Int? = null
    ) {
        iconToIndexMap[icon]?.let { _pos ->
            handleVisibility(
                listImg[_pos],
                pos,
                checkRestart,
                posNav,
                posColor
            )
        }
    }
    // Thêm biến đếm số ảnh đang load
    private var loadingImagesCount = 0

    // Sửa lại handleVisibility
    private fun handleVisibility(
        view: ImageView, pos: Int, checkRestart: Boolean = false,
        posNav: Int? = null,
        posColor: Int? = null
    ) {
        if (checkRestart) {
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
            loadingImagesCount++

            Glide.with(applicationContext)
                .load(
                    listData[posNav ?: adapterNav.posNav]
                        .listPath[posColor ?: adapterColor.posColor]
                        .listPath[pos]
                )
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .encodeQuality(60)
                .priority(Priority.HIGH)
                .skipMemoryCache(false)
                .dontAnimate()
                .listener(object : com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable> {
                    override fun onLoadFailed(
                        e: com.bumptech.glide.load.engine.GlideException?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        loadingImagesCount--
                        checkAllImagesLoaded()
                        return false
                    }

                    override fun onResourceReady(
                        resource: android.graphics.drawable.Drawable?,
                        model: Any?,
                        target: com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable>?,
                        dataSource: com.bumptech.glide.load.DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        loadingImagesCount--
                        checkAllImagesLoaded()
                        return false
                    }
                })
                .into(view)
        }
    }

    private fun checkAllImagesLoaded() {
        if (loadingImagesCount <= 0) {
            loadingImagesCount = 0
            binding.root.post {
                canSave = true
                binding.btnSave.alpha = 1f
            }
        }
    }
    var listData = arrayListOf<BodyPartModel>()
    //0 - path, 1 - color
    var arrInt = arrayListOf<ArrayList<Int>>()
    var blackCentered = 0
    var arrIntHottrend: ArrayList<ArrayList<Int>>? = null
    private fun getData1() {
        DataHelper.listImageSortView.clear()
        DataHelper.listImage.clear()
        blackCentered = intent.getIntExtra("data", 0)
        arrIntHottrend = intent.getSerializableExtra("arr") as? ArrayList<ArrayList<Int>>
        var checkFirst = true
        repeat(DataHelper.arrBlackCentered[blackCentered].bodyPart.size) {
            DataHelper.listImageSortView.add("")
            DataHelper.listImage.add("")
        }
        DataHelper.arrBlackCentered[blackCentered].bodyPart.forEach {
            val (x, y) = it.icon.substringBeforeLast("/").substringAfterLast("/").split("-")
                .map { it.toInt() }
            DataHelper.listImageSortView[x - 1] = it.icon
            DataHelper.listImage[y - 1] = it.icon
            iconToIndexMap[it.icon] = x - 1
        }
        //thu tu navi
        DataHelper.listImage.forEachIndexed { index, icon ->
            var x = arrBlackCentered[blackCentered].bodyPart.indexOfFirst { it.icon == icon }
            var y = DataHelper.listImageSortView.indexOf(icon)
            if (x != -1) {
                arrShowColor.add(true)
                listData.add(arrBlackCentered[blackCentered].bodyPart[x])
                if (checkFirst) {
                    checkFirst = false
//                    arrIntHottrend thu tu view
                    if (arrIntHottrend != null) {
                        arrInt.add(arrayListOf(arrIntHottrend!![y][0], arrIntHottrend!![y][1]))
                    } else {
                        arrInt.add(arrayListOf(1, 0))
                    }
                } else {
                    if (arrIntHottrend != null) {
                        arrInt.add(arrayListOf(arrIntHottrend!![y][0], arrIntHottrend!![y][1]))
                    } else {
                        arrInt.add(arrayListOf(0, 0))
                    }
                }
            }
        }
    }
    var checkRevert = true
    var checkHide = false
    override fun initAction() {
        adapterColor.onClick = {
            if (!DataHelper.arrBlackCentered[blackCentered].checkDataOnline || isInternetAvailable(
                    applicationContext
                )
            ) {
                val recyclerState = binding.rcvPart.layoutManager?.onSaveInstanceState()
                adapterColor.setPos(it)
                adapterColor.submitList(listData[adapterNav.posNav].listPath)
                arrInt[adapterNav.posNav][1] = it
                adapterPart.submitList(listData[adapterNav.posNav].listPath[adapterColor.posColor].listPath) {
                    binding.rcvPart.layoutManager?.onRestoreInstanceState(recyclerState)
                }
                putImage(listData[adapterNav.posNav].icon, adapterPart.posPath)
            } else {
                DialogExit(
                    this@CustomviewActivity,
                    "loadingnetwork"
                ).show()
            }
        }
        adapterNav.onClick = {
            if (!DataHelper.arrBlackCentered[blackCentered].checkDataOnline || isInternetAvailable(applicationContext)) {
                val newPos = it

                // Validate và chuẩn bị indices
                val currentBodyPart = listData[newPos]
                val maxColorIndex = currentBodyPart.listPath.size - 1
                val safeColorIndex = arrInt[newPos][1].coerceIn(0, maxColorIndex)

                // Cập nhật arrInt nếu cần
                if (arrInt[newPos][1] != safeColorIndex) {
                    arrInt[newPos][1] = safeColorIndex
                }

                val maxPartIndex = currentBodyPart.listPath[safeColorIndex].listPath.size - 1
                val safePartIndex = arrInt[newPos][0].coerceIn(0, maxPartIndex)

                if (arrInt[newPos][0] != safePartIndex) {
                    arrInt[newPos][0] = safePartIndex
                }

                // Bắt đầu cập nhật UI
                adapterNav.setPos(newPos)
                adapterNav.submitList(listData)

                // Set color adapter position trước
                adapterColor.setPos(safeColorIndex)

                // Cập nhật visibility
                updateColorSectionVisibility(newPos)

                // Submit color list nếu cần
                val hasMultipleColors = currentBodyPart.listPath.size > 1
                if (hasMultipleColors && arrShowColor[newPos]) {
                    adapterColor.submitList(currentBodyPart.listPath)
                    binding.root.postDelayed({
                        binding.rcvColor.smoothScrollToPosition(safeColorIndex)
                    }, 100)
                } else if (hasMultipleColors) {
                    adapterColor.submitList(currentBodyPart.listPath)
                }

                // Cập nhật part adapter
                if (adapterColor.posColor == safeColorIndex) {
                    adapterPart.setPos(safePartIndex)
                } else {
                    adapterPart.setPos(-1)
                }

                adapterPart.submitList(currentBodyPart.listPath[safeColorIndex].listPath)

                binding.root.postDelayed({
                    binding.rcvPart.smoothScrollToPosition(safePartIndex)
                }, 100)

            } else {
                DialogExit(this@CustomviewActivity, "loadingnetwork").show()
            }
        }
        adapterPart.onClick = { it, type ->
            if (!DataHelper.arrBlackCentered[blackCentered].checkDataOnline || isInternetAvailable(
                    applicationContext
                )
            ) {
                when (type) {
                    "none" -> {
                        adapterPart.setPos(it)
                        adapterPart.submitList(listData[adapterNav.posNav].listPath[adapterColor.posColor].listPath)
                        arrInt[adapterNav.posNav][0] = it
                        arrInt[adapterNav.posNav][1] = adapterColor.posColor
                        putImage(listData[adapterNav.posNav].icon, it, true)
                    }
                    "dice" -> {
                        when (listData[adapterNav.posNav].listPath[adapterColor.posColor].listPath[0]) {
                            "none" -> {
                                if (listData[adapterNav.posNav].listPath[adapterColor.posColor].listPath.size > 3) {
                                    var x =
                                        (2..<listData[adapterNav.posNav].listPath[adapterColor.posColor].listPath.size).random()
                                    adapterPart.setPos(x)
                                    adapterPart.submitList(listData[adapterNav.posNav].listPath[adapterColor.posColor].listPath)
                                    arrInt[adapterNav.posNav][0] = x
                                    arrInt[adapterNav.posNav][1] = adapterColor.posColor
                                    putImage(listData[adapterNav.posNav].icon, x)
                                } else {
                                    adapterPart.setPos(2)
                                    adapterPart.submitList(listData[adapterNav.posNav].listPath[adapterColor.posColor].listPath)
                                    arrInt[adapterNav.posNav][0] = 2
                                    arrInt[adapterNav.posNav][1] = adapterColor.posColor
                                    putImage(listData[adapterNav.posNav].icon, 2)
                                }
                            }
                            "dice" -> {
                                if (listData[adapterNav.posNav].listPath[adapterColor.posColor].listPath.size > 2) {
                                    var x =
                                        (1..<listData[adapterNav.posNav].listPath[adapterColor.posColor].listPath.size).random()
                                    adapterPart.setPos(x)
                                    adapterPart.submitList(listData[adapterNav.posNav].listPath[adapterColor.posColor].listPath)
                                    arrInt[adapterNav.posNav][0] = x
                                    arrInt[adapterNav.posNav][1] = adapterColor.posColor
                                    putImage(listData[adapterNav.posNav].icon, x)
                                } else {
                                    adapterPart.setPos(1)
                                    adapterPart.submitList(listData[adapterNav.posNav].listPath[adapterColor.posColor].listPath)
                                    arrInt[adapterNav.posNav][0] = 1
                                    arrInt[adapterNav.posNav][1] = adapterColor.posColor
                                    putImage(listData[adapterNav.posNav].icon, 1)
                                    showToast(
                                        applicationContext,
                                        R.string.the_layer_have_only_one_item
                                    )
                                }
                            }
                        }
                    }
                    else -> {
                        adapterPart.setPos(it)
                        adapterPart.submitList(listData[adapterNav.posNav].listPath[adapterColor.posColor].listPath)
                        arrInt[adapterNav.posNav][0] = it
                        arrInt[adapterNav.posNav][1] = adapterColor.posColor
                        putImage(listData[adapterNav.posNav].icon, it)
                    }
                }
            } else {
                DialogExit(
                    this@CustomviewActivity,
                    "loadingnetwork"
                ).show()
            }
        }
        binding.apply {
            imvShowColor.onSingleClick {
                if (listData[adapterNav.posNav].listPath.size <= 1) return@onSingleClick

                val newState = !arrShowColor[adapterNav.posNav]
                arrShowColor[adapterNav.posNav] = newState

                // Animate visibility change
                if (newState) {
                    binding.llColor.visibility = View.VISIBLE
                    binding.llColor.alpha = 0f
                    binding.llColor.animate().alpha(1f).setDuration(200).start()
                } else {
                    binding.llColor.animate().alpha(0f).setDuration(200).withEndAction {
                        binding.llColor.visibility = View.INVISIBLE
                    }.start()
                }
            }
            btnReset.onSingleClick {
                if(!arrBlackCentered[blackCentered].checkDataOnline || isInternetAvailable(
                        applicationContext
                    )
                ){
                var dialog = DialogExit(
                    this@CustomviewActivity,
                    "reset"
                )
                dialog.onClick = {
                    DataHelper.listImage.forEach {
                        putImage("0", 0, true)
                    }
                    arrInt.forEach { i ->
                        i[0] = 0
                        i[1] = 0
                    }
                    arrInt[0][0] = 1
                    arrInt[0][1] = 0

                    adapterPart.setPos(arrInt[adapterNav.posNav][0])
                    adapterColor.setPos(arrInt[adapterNav.posNav][1])
                    adapterPart.submitList(listData[adapterNav.posNav].listPath[adapterColor.posColor].listPath)
                    updateColorSectionVisibility()
                    listData.forEachIndexed { index, bodyPartModel ->
                        putImage(bodyPartModel.icon, 1, true)
                    }
                    putImage(listData[0].icon, 1, false, 0, 0)

                }
                dialog.show()
                }else{
                    DialogExit(
                        this@CustomviewActivity,
                        "loadingnetwork"
                    ).show()                  }
            }
            imvBack.onSingleClick {
                var dialog = DialogExit(
                    this@CustomviewActivity,
                    "exit"
                )
                dialog.onClick = {
                    finish()
                }
                dialog.show()
            }
            btnRevert.onSingleClick {
                checkRevert = !checkRevert
                if (checkRevert) {
                    listImg.forEach {
                        it.scaleX = 1f
                    }
                } else {
                    listImg.forEach {
                        it.scaleX = -1f
                    }
                }
            }
            btnDice.onSingleClick {
                if (!arrBlackCentered[blackCentered].checkDataOnline || isInternetAvailable(applicationContext)) {
                    // Disable save ngay lập tức
                    canSave = false
                    btnSave.alpha = 0.5f

                    countRandom++

                    listData.forEachIndexed { index, partBody ->
                        if (partBody.listPath.size > 1) {
                            arrInt[index][1] = (0..<partBody.listPath.size).random()
                        } else {
                            arrInt[index][1] = 0
                        }

                        if (partBody.listPath[arrInt[index][1]].listPath[0] == "none") {
                            if (partBody.listPath[arrInt[index][1]].listPath.size > 3) {
                                arrInt[index][0] = (2..<partBody.listPath[arrInt[index][1]].listPath.size).random()
                            } else {
                                arrInt[index][0] = 2
                            }
                        } else {
                            if (partBody.listPath[arrInt[index][1]].listPath.size > 2) {
                                arrInt[index][0] = (1..<partBody.listPath[arrInt[index][1]].listPath.size).random()
                            } else {
                                arrInt[index][0] = 1
                            }
                        }

                        putImage(
                            partBody.icon,
                            arrInt[index][0],
                            false,
                            index,
                            arrInt[index][1]
                        )
                    }

                    adapterPart.setPos(arrInt[adapterNav.posNav][0])
                    adapterColor.setPos(arrInt[adapterNav.posNav][1])
                    adapterPart.submitList(listData[adapterNav.posNav].listPath[adapterColor.posColor].listPath)
                    updateColorSectionVisibility()

                    binding.rcvPart.post {
                        binding.rcvPart.smoothScrollToPosition(adapterPart.posPath)
                    }
                    binding.rcvColor.post {
                        binding.rcvColor.smoothScrollToPosition(adapterColor.posColor)
                    }

                    // Enable lại save sau 2 giây (thời gian load ảnh)
                    binding.root.postDelayed({
                        canSave = true
                        btnSave.alpha = 1f
                    }, 2000)

                } else {
                    DialogExit(this@CustomviewActivity, "loadingnetwork").show()
                }
            }
            llLoading.onSingleClick {
                showToast(
                    applicationContext,
                    R.string.please_wait_a_few_seconds_for_data_to_load
                )
            }
            btnSave.onSingleClick {
                if (!canSave) {
                    return@onSingleClick
                }
                llLoading.visibility = View.VISIBLE
//                applyGradientToLoadingText()
//                animationView.visibility = View.VISIBLE
                val a = arrBlackCentered[blackCentered].avt.split("/")
                var b = a[a.size - 2]
                saveBitmap(
                    this@CustomviewActivity,
                    viewToBitmap(rl),
                    intent.getStringExtra("fileName") ?: "",
                    true
                ) { it, path, pathOld ->
                    if (it) {
                        viewModel.deleteAvatar(pathOld)
                        llLoading.visibility = View.GONE
//                        animationView.visibility = View.GONE
                        //lop layer
                        var x = arrayListOf<ArrayList<Int>>()
                        DataHelper.listImageSortView.forEachIndexed { _pos, icon ->
                            var y = DataHelper.listImage.indexOf(
                                icon
                            )
                            x.add(arrInt[y])
                        }
                        viewModel.addAvatar(
                            AvatarModel(
                                path,
                                arrBlackCentered[blackCentered].avt,
                                arrBlackCentered[blackCentered].checkDataOnline,
                                fromList(x)
                            )
                        )
                        startActivity(
                            Intent(
                                this@CustomviewActivity, BackgroundActivity::class.java
                            ).putExtra("path", path)
                        )


                    } else {
                        llLoading.visibility = View.GONE
//                        animationView.visibility = View.GONE
                        showToast(
                            this@CustomviewActivity, R.string.save_failed
                        )
                    }
                }
            }
//            btnSee.onSingleClick {
//                if (btnRevert.isInvisible) {
//                    btnRevert.show()
//                    btnReset.show()
//                    btnSave.show()
//                    if (listData[adapterNav.posNav].listPath.size > 1) {
//                        if (arrShowColor[adapterNav.posNav]) {
//                            binding.llColor.show()
//                        }
//                        imvShowColor.show()
//                    }
//                    if (countRandom < 3) {
//                        btnDice.show()
//                    }
//                    llPart.show()
//                    llNav.show()
//                } else {
//                    btnRevert.inhide()
//                    btnReset.inhide()
//                    btnSave.inhide()
//                    imvShowColor.inhide()
//                    llColor.inhide()
//                    btnDice.inhide()
//                    llPart.inhide()
//                    llNav.inhide()
//                    btnSee.setImageResource(R.drawable.imv_see_false)
//                }
//
//            }
        }

    }
    private fun updateColorSectionVisibility(posNav: Int = adapterNav.posNav) {
        if (posNav < 0 || posNav >= listData.size) return

        val currentBodyPart = listData[posNav]
        val hasMultipleColors = currentBodyPart.listPath.size > 1

        // Sử dụng animate() để transition mượt mà hơn
        if (!hasMultipleColors) {
            binding.imvShowColor.animate().alpha(0f).setDuration(150).withEndAction {
                binding.imvShowColor.visibility = View.INVISIBLE
            }
            binding.llColor.animate().alpha(0f).setDuration(150).withEndAction {
                binding.llColor.visibility = View.INVISIBLE
            }
            return
        }

        // Có nhiều màu
        binding.imvShowColor.animate().alpha(1f).setDuration(150).withStartAction {
            binding.imvShowColor.visibility = View.VISIBLE
        }

        if (arrShowColor[posNav]) {
            binding.llColor.animate().alpha(1f).setDuration(150).withStartAction {
                binding.llColor.visibility = View.VISIBLE
            }
        } else {
            binding.llColor.animate().alpha(0f).setDuration(150).withEndAction {
                binding.llColor.visibility = View.INVISIBLE
            }
        }
    }
    override fun onBackPressed() {
        var dialog = DialogExit(
            this@CustomviewActivity,
            "exit"
        )
        dialog.onClick = {
            finish()
        }
        dialog.show()
    }
}
