package com.fokalore.ocmaker.create.ui.customview

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
import com.fokalore.ocmaker.create.base.AbsBaseActivity
import com.fokalore.ocmaker.create.data.model.AvatarModel
import com.fokalore.ocmaker.create.data.model.BodyPartModel
import com.fokalore.ocmaker.create.dialog.DialogExit
import com.fokalore.ocmaker.create.ui.background.BackgroundActivity
import com.fokalore.ocmaker.create.utils.DataHelper
import com.fokalore.ocmaker.create.utils.fromList
import com.fokalore.ocmaker.create.utils.hide
import com.fokalore.ocmaker.create.utils.isInternetAvailable
import com.fokalore.ocmaker.create.utils.onSingleClick
import com.fokalore.ocmaker.create.utils.saveBitmap
import com.fokalore.ocmaker.create.utils.show
import com.fokalore.ocmaker.create.utils.showToast
import com.fokalore.ocmaker.create.utils.viewToBitmap
import com.fokalore.ocmaker.create.R
import com.fokalore.ocmaker.create.databinding.ActivityCustomizeBinding
import com.fokalore.ocmaker.create.utils.DataHelper.arrBlackCentered
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
        if (arrBlackCentered.size > 0) {
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
//                    binding.imvShowColor.visibility = View.INVISIBLE
                } else {
                    binding.llColor.visibility = View.VISIBLE
//                    binding.imvShowColor.visibility = View.VISIBLE
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
                } else {
                    binding.llColor.visibility = View.VISIBLE
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
    private fun handleVisibility(
        view: ImageView, pos: Int, checkRestart: Boolean = false,
        posNav: Int? = null,
        posColor: Int? = null
    ) {
        if (checkRestart) {
            view.visibility = View.GONE
        } else {
            view.visibility = View.VISIBLE
            Glide.with(applicationContext)
                .load(
                    listData[posNav ?: adapterNav.posNav]
                        .listPath[posColor ?: adapterColor.posColor]
                        .listPath[pos]
                )
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .encodeQuality(60)
                .priority(Priority.HIGH)
                .skipMemoryCache(false)                     // Cache memory
                .dontAnimate()
                .into(view)
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
        repeat(arrBlackCentered[blackCentered].bodyPart.size) {
            DataHelper.listImageSortView.add("")
            DataHelper.listImage.add("")
        }
        arrBlackCentered[blackCentered].bodyPart.forEach {
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
            if (!arrBlackCentered[blackCentered].checkDataOnline || isInternetAvailable(
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
            if (!arrBlackCentered[blackCentered].checkDataOnline || isInternetAvailable(
                    applicationContext
                )
            ) {
                val newPos = it
                adapterNav.setPos(newPos)
                adapterNav.submitList(listData)
                adapterColor.setPos(arrInt[newPos][1])

                val currentBodyPart = listData[newPos]

                // Gọi hàm đồng bộ → thay thế toàn bộ logic if-else visibility cũ
                updateColorSectionVisibility(newPos)

                adapterColor.submitList(currentBodyPart.listPath)
                binding.root.postDelayed(
                    { binding.rcvColor.smoothScrollToPosition(arrInt[newPos][1]) },
                    100
                )

                if (adapterColor.posColor == arrInt[adapterNav.posNav][1]) {
                    adapterPart.setPos(arrInt[adapterNav.posNav][0])
                } else {
                    adapterPart.setPos(-1)
                }
                adapterPart.submitList(currentBodyPart.listPath[adapterColor.posColor].listPath)
                binding.root.postDelayed(
                    { binding.rcvPart.smoothScrollToPosition(arrInt[newPos][0]) },
                    100
                )

            } else {
                DialogExit(
                    this@CustomviewActivity,
                    "loadingnetwork"
                ).show()            }
        }
        adapterPart.onClick = { it, type ->
            if (!arrBlackCentered[blackCentered].checkDataOnline || isInternetAvailable(
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
                arrShowColor[adapterNav.posNav] = !arrShowColor[adapterNav.posNav]
                updateColorSectionVisibility()  // ← thêm dòng này
            }
            btnReset.onSingleClick {
                if(!DataHelper.arrBlackCentered[blackCentered].checkDataOnline || isInternetAvailable(
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
                if (!DataHelper.arrBlackCentered[blackCentered].checkDataOnline || isInternetAvailable(
                        applicationContext
                    )
                ) {
                    canSave = false
                    btnSave.alpha = 0.5f

                    countRandom++
//                    if (countRandom == 3) {
//                        btnDice.inhide()
//                    }
                    listData.forEachIndexed { index, partBody ->
                        if (partBody.listPath.size > 1) {
                            arrInt[index][1] = (0..<partBody.listPath.size).random()

                        } else {
                            arrInt[index][1] = 0
                        }
                        if (partBody.listPath[arrInt[index][1]].listPath[0] == "none") {
                            if (partBody.listPath[arrInt[index][1]].listPath.size > 3) {
                                arrInt[index][0] =
                                    (2..<partBody.listPath[arrInt[index][1]].listPath.size).random()
                            } else {
                                arrInt[index][0] = 2
                            }
                        } else {
                            if (partBody.listPath[arrInt[index][1]].listPath.size > 2) {
                                arrInt[index][0] =
                                    (1..<partBody.listPath[arrInt[index][1]].listPath.size).random()
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
                    binding.root.postDelayed({
                        canSave = true
                        btnSave.alpha = 1f
                    }, 3000)
                } else {
                    DialogExit(
                        this@CustomviewActivity,
                        "loadingnetwork"
                    ).show()
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
                val a = DataHelper.arrBlackCentered[blackCentered].avt.split("/")
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
                                DataHelper.arrBlackCentered[blackCentered].avt,
                                DataHelper.arrBlackCentered[blackCentered].checkDataOnline,
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

        if (!hasMultipleColors) {
            binding.llColor.visibility = View.INVISIBLE
//            binding.imvNoColor.show()
            binding.imvShowColor.setImageResource(R.drawable.imv_color)
            binding.imvShowColor.visibility = View.VISIBLE
//            binding.imvShowColor.visibility = View.INVISIBLE
            return
        }
//        binding.imvNoColor.hide()
        // Có nhiều màu → quyết định hiển thị dựa trên arrShowColor
        if (arrShowColor[posNav]) {
//            binding.imvShowColor.setImageResource(R.drawable.imv_color)
            binding.llColor.visibility = View.VISIBLE
        } else {
//            binding.imvShowColor.setImageResource(R.drawable.imv_color_hide)
            binding.llColor.visibility = View.INVISIBLE
        }
        binding.imvShowColor.visibility = View.VISIBLE
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
