package com.fokalore.ocmaker.create.ui.succes

import android.content.pm.PackageManager
import android.os.Build
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.fokalore.ocmaker.create.base.AbsBaseActivity
import com.fokalore.ocmaker.create.ui.main.MainActivity
import com.fokalore.ocmaker.create.ui.my_creation.MyCreationActivity
import com.fokalore.ocmaker.create.ui.permision.PermissionViewModel
import com.fokalore.ocmaker.create.utils.CONST
import com.fokalore.ocmaker.create.utils.PermissionHelper
import com.fokalore.ocmaker.create.utils.SharedPreferenceUtils
import com.fokalore.ocmaker.create.utils.newIntent
import com.fokalore.ocmaker.create.utils.onClick
import com.fokalore.ocmaker.create.utils.onSingleClick
import com.fokalore.ocmaker.create.utils.saveFileToExternalStorage
import com.fokalore.ocmaker.create.utils.scanMediaFile
import com.fokalore.ocmaker.create.utils.shareListFiles
import com.fokalore.ocmaker.create.utils.showDialogNotifiListener
import com.fokalore.ocmaker.create.utils.showToast
import com.fokalore.ocmaker.create.R
import com.fokalore.ocmaker.create.databinding.ActivitySuccessBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject
import kotlin.getValue
@AndroidEntryPoint
class SuccessActivity : AbsBaseActivity<ActivitySuccessBinding>() {
    var path = ""
    override fun getLayoutId(): Int = R.layout.activity_success
    private val permissionViewModel: PermissionViewModel by viewModels()
    @Inject
    lateinit var sharedPreference: SharedPreferenceUtils
    override fun initView() {
        path = intent.getStringExtra("path").toString()
        Glide.with(applicationContext).load(path).into(binding.imv)
        binding.apply {
            tvDownload.isSelected = true
            tvMyWork.isSelected = true
            tvTitle.isSelected = true
        }
      }

    override fun initAction() {
        binding.apply {
            imvBack.onSingleClick { finish() }
            imvShare.onClick {
                shareListFiles(
                    this@SuccessActivity,
                    arrayListOf(path)
                )
            }
            imvHome.onSingleClick {
                    startActivity(
                        newIntent(
                            applicationContext,
                            MainActivity::class.java
                        )
                    )
                    finish()

            }
            btnMyWork.onSingleClick {
                    startActivity(
                        newIntent(
                            applicationContext,
                            MyCreationActivity::class.java
                        )
                    )
                    finish()
                }

            btnDownload.onClick {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    performDownload()
                } else {
                    handlePermissionRequest(isStorage = true)}

            }
        }
    }
    private fun handlePermissionRequest(isStorage: Boolean) {
        val permissions = if (isStorage) {
            permissionViewModel.getStoragePermissions()
        } else {
            permissionViewModel.getNotificationPermissions()
        }

        // Kiểm tra đã có permission chưa
        if (PermissionHelper.checkPermissions(permissions, this@SuccessActivity)) {
            performDownload()
            return
        }

        // Kiểm tra nếu đã từ chối nhiều lần → gợi ý vào Settings
        if (permissionViewModel.needGoToSettings(sharedPreference, isStorage)) {
            val dialogRes = if (isStorage) R.string.reques_storage else R.string.content_dialog_notification
            showDialogNotifiListener(dialogRes)
            return
        }

        // Request permission bình thường
        val requestCode = if (isStorage) CONST.REQUEST_STORAGE_PERMISSION else CONST.REQUEST_NOTIFICATION_PERMISSION
        ActivityCompat.requestPermissions(this, permissions, requestCode)
    }

    private fun performDownload() {
        saveFileToExternalStorage(
            applicationContext,
            path,
            ""
        ) { check, path ->
            if (check) {
                Toast.makeText(
                    applicationContext,
                    getString(R.string.download_successfully) + " " + CONST.NAME_SAVE_FILE,
                    Toast.LENGTH_SHORT
                ).show()
                scanMediaFile(this@SuccessActivity, File(path))
            } else {
                showToast(this@SuccessActivity, R.string.download_failed)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        val isGranted = grantResults.isNotEmpty() &&
                grantResults.all { it == PackageManager.PERMISSION_GRANTED }

        when (requestCode) {
            CONST.REQUEST_STORAGE_PERMISSION -> {
                permissionViewModel.updateStorageGranted(sharedPreference, isGranted)

                if (isGranted) {
                    performDownload()
                }
            }
        }

}

}