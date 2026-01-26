package com.fokalore.ocmaker.create.ui.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Build
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.fokalore.ocmaker.create.base.AbsBaseActivity
import com.fokalore.ocmaker.create.data.callapi.reponse.DataResponse
import com.fokalore.ocmaker.create.data.callapi.reponse.LoadingStatus
import com.fokalore.ocmaker.create.data.model.BodyPartModel
import com.fokalore.ocmaker.create.data.model.ColorModel
import com.fokalore.ocmaker.create.data.model.CustomModel
import com.fokalore.ocmaker.create.data.repository.ApiRepository
import com.fokalore.ocmaker.create.dialog.DialogExit
import com.fokalore.ocmaker.create.ui.customview.CustomviewActivity
import com.fokalore.ocmaker.create.ui.customview.CustomviewViewModel
import com.fokalore.ocmaker.create.utils.CONST
import com.fokalore.ocmaker.create.utils.DataHelper
import com.fokalore.ocmaker.create.utils.DataHelper.getData
import com.fokalore.ocmaker.create.utils.checkPermision
import com.fokalore.ocmaker.create.utils.checkUsePermision
import com.fokalore.ocmaker.create.utils.hide
import com.fokalore.ocmaker.create.utils.isInternetAvailable
import com.fokalore.ocmaker.create.utils.onSingleClick
import com.fokalore.ocmaker.create.utils.requesPermission
import com.fokalore.ocmaker.create.utils.saveFileToExternalStorage
import com.fokalore.ocmaker.create.utils.scanMediaFile
import com.fokalore.ocmaker.create.utils.shareListFiles
import com.fokalore.ocmaker.create.utils.show
import com.fokalore.ocmaker.create.utils.showToast
import com.fokalore.ocmaker.create.utils.toList
import com.fokalore.ocmaker.create.R
import com.fokalore.ocmaker.create.databinding.ActivityViewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class ViewActivity : AbsBaseActivity<ActivityViewBinding>() {
    @Inject
    lateinit var apiRepository: ApiRepository
    var checkCallingDataOnline = false
    val viewModel: CustomviewViewModel by viewModels()
    var path = ""
    override fun getLayoutId(): Int = R.layout.activity_view
    private var networkReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val connectivityManager =
                context?.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = connectivityManager.activeNetworkInfo
            if (!checkCallingDataOnline) {
                if (networkInfo != null && networkInfo.isConnected) {
                    var checkDataOnline = false
                    DataHelper.arrBlackCentered.forEach {
                        if (it.checkDataOnline) {
                            checkDataOnline = true
                            return@forEach
                        }
                    }
                    if (!checkDataOnline) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            getData(apiRepository)
                        }
                    }
                } else {
                    if (DataHelper.arrBlackCentered.isEmpty()) {
                        lifecycleScope.launch(Dispatchers.IO) {
                            getData(apiRepository)
                        }
                    }
                }
            }
        }
    }
    override fun initView() {
        val filter = IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(networkReceiver, filter)
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
                                .toList() // Chuyển map -> list<Pair<String, List<X10>>>
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
        path = intent.getStringExtra("data").toString()
        if (intent?.getStringExtra("type") == "avatar") {
            binding.imvEdit.show()
        } else {
            binding.imvEdit.hide()

        }
        Glide.with(applicationContext).load(path).into(binding.imv)

    }

    override fun initAction() {
        binding.apply {
            tvEditShare.isSelected = true
            tvDownload.isSelected = true
            imvBack.onSingleClick { finish() }
            imvEdit.onSingleClick {
                viewModel.getAvatar(path) { avatar ->
                    if (!isInternetAvailable(this@ViewActivity) && avatar!!.online == true){
                        DialogExit(
                            this@ViewActivity,
                            "loadingnetwork"
                        ).show()
                        return@getAvatar
                    }
                    if (avatar != null) {
                        var a =
                            DataHelper.arrBlackCentered.indexOfFirst { it.avt == avatar.pathAvatar }
                        if (a > -1) {
                            var a = avatar.pathAvatar.split("/")
                            var b = a[a.size - 1]

                            startActivity(
                                Intent(
                                    applicationContext,
                                    CustomviewActivity::class.java
                                ).putExtra(
                                    "data",
                                    DataHelper.arrBlackCentered.indexOfFirst { it.avt == avatar.pathAvatar })
                                    .putExtra(
                                        "arr",
                                        toList(avatar.arr)
                                    ).putExtra("checkEdit", true)
                                    .putExtra("fileName", File(avatar.path).name)
                            )

                        } else {
                            lifecycleScope.launch {
                                val dialog= DialogExit(
                                    this@ViewActivity,
                                    "awaitdata"
                                )
                                dialog.show()
                                delay(1500)
                                dialog.dismiss()
                            }
                        }
                    }
                }
            }
            imvDelete.onSingleClick {
                var dialog = DialogExit(
                    this@ViewActivity,
                    "delete"
                )
                dialog.onClick = {
                    File(path).delete()
                    finish()
                }
                dialog.show()
            }
            btnDownload.onSingleClick {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q &&
                    !checkPermision(application)
                ) {
                    ActivityCompat.requestPermissions(
                        this@ViewActivity,
                        checkUsePermision(),
                        CONST.REQUEST_STORAGE_PERMISSION
                    )
                } else {
                    saveFileToExternalStorage(
                        applicationContext,
                        path,
                        "",
                    ) { check, path ->
                        if (check) {
                            Toast.makeText(
                                applicationContext,
                                getString(R.string.download_successfully) + " " + CONST.NAME_SAVE_FILE,
                                Toast.LENGTH_SHORT
                            ).show()
                            scanMediaFile(
                                this@ViewActivity,
                                File(path)
                            )
                        } else {
                            showToast(
                                this@ViewActivity,
                                R.string.download_failed
                            )
                        }
                    }

                }
            }
            btnEditShareAll.onSingleClick {
//                if (intent?.getStringExtra("type") == "avatar"){
//                viewModel.getAvatar(path) { avatar ->
//                    if (avatar != null) {
//                        var a =
//                            DataHelper.arrBlackCentered.indexOfFirst { it.avt == avatar.pathAvatar }
//                        if (a > -1) {
//                            var a = avatar.pathAvatar.split("/")
//                            var b = a[a.size - 2]
//
//                            startActivity(
//                                Intent(
//                                    applicationContext,
//                                    CustomviewActivity::class.java
//                                ).putExtra(
//                                    "data",
//                                    DataHelper.arrBlackCentered.indexOfFirst { it.avt == avatar.pathAvatar })
//                                    .putExtra(
//                                        "arr",
//                                        toList(avatar.arr)
//                                    ).putExtra("checkEdit", true)
//                                    .putExtra("fileName", File(avatar.path).name)
//                            )
//
//                        } else {
//                            showToast(
//                                applicationContext,
//                                R.string.please_check_your_network_connection
//                            )
//                        }
//
//                    } else {
//                        File(path).delete()
//                        showToast(
//                            applicationContext,
//                            R.string.image_error_please_try_again
//                        )
//                        finish()
//                    }
//                }
//            }else{
                shareListFiles(
                    this@ViewActivity,
                    arrayListOf(path)
                )
//            }

            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requesPermission(requestCode)) {
            CONST.REQUEST_STORAGE_PERMISSION -> {
                saveFileToExternalStorage(
                    applicationContext,
                    path,
                    "",
                ) { check, path ->
                    if (check) {
                        Toast.makeText(
                            applicationContext,
                            getString(R.string.download_successfully) + " " + CONST.NAME_SAVE_FILE,
                            Toast.LENGTH_SHORT
                        ).show()
                        scanMediaFile(
                            this@ViewActivity,
                            File(path)
                        )
                    } else {
                        showToast(
                            this@ViewActivity,
                            R.string.download_failed
                        )
                    }
                }
            }
        }
    }
}