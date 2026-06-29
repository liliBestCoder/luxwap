package com.v2ray.ang.ui

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.VpnService
import android.os.Build
import android.os.Bundle
import android.text.InputType
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.v2ray.ang.AppConfig
import com.v2ray.ang.AppConfig.VPN
import com.v2ray.ang.R
import com.v2ray.ang.databinding.ActivityMainNewBinding
import com.v2ray.ang.dto.SubscriptionItem
import com.v2ray.ang.extension.toast
import com.v2ray.ang.extension.toastError
import com.v2ray.ang.handler.MigrateManager
import com.v2ray.ang.handler.MmkvManager
import com.v2ray.ang.service.V2RayServiceManager
import com.v2ray.ang.ui.MainActivity.Action
import com.v2ray.ang.util.Utils
import com.v2ray.ang.viewmodel.MainViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainNewActivity : BaseActivity() {

    private val binding by lazy {
        ActivityMainNewBinding.inflate(layoutInflater)
    }

    private val requestVpnPermission = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            startV2Ray()
        }
    }

    val mainViewModel: MainViewModel by viewModels()

    private val adapter by lazy { RouteAdapter(this) }

    // register activity result for requesting permission
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted: Boolean ->
            if (isGranted) {
                when (pendingAction) {
                    Action.POST_NOTIFICATIONS -> {}
                    else -> {}
                }
            } else {
                toast(R.string.toast_permission_denied)
            }
            pendingAction = Action.NONE
        }

    private var pendingAction: Action = Action.NONE

    enum class Action {
        NONE,
        POST_NOTIFICATIONS
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(binding.root)
        setPlayBtn()

        toggleFileAndRefreshBtn()
        //监听订阅和服务器变化
        setupViewModel();
        //迁移旧的配置到新的配置的函数
        migrateLegacy();

        binding.cardStatus.setOnClickListener {
            if (mainViewModel.isRunning.value == true) {
                setTestState(getString(R.string.connection_test_testing))
                mainViewModel.testCurrentServerRealPing()
            } else {
//                tv_test_state.text = getString(R.string.connection_test_fail)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                pendingAction = Action.POST_NOTIFICATIONS
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        loadRoutes()
        //测速
        binding.ivStatusIcon.setOnClickListener {
            toast(getString(R.string.connection_test_testing_count, mainViewModel.serversCache.count()))
            mainViewModel.testAllRealPing()
        }

        binding.btnSettings.setOnClickListener {
            val intent = Intent(this, SettingsNewActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)

        }

    }

    private fun sortByTestResults() {
        //binding.pbWaiting.show()
        lifecycleScope.launch(Dispatchers.IO) {
            mainViewModel.sortByTestResults()
            launch(Dispatchers.Main) {
                mainViewModel.reloadServerList()
                //binding.pbWaiting.hide()
            }
        }
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun setupViewModel() {
        mainViewModel.updateListAction.observe(this) { index ->
            if (index >= 0) {
                adapter.notifyItemChanged(index)
                sortByTestResults()
            } else {
                adapter.notifyDataSetChanged()
            }
        }
        mainViewModel.updateTestResultAction.observe(this) { setTestState(it) }
        mainViewModel.isRunning.observe(this) { isRunning ->
            adapter.isRunning = isRunning
            if (isRunning) {
                binding.playBtn.setImageResource(R.drawable.ic_stop_24dp)
                binding.tvStatus.text = getString(R.string.connection_connected)
                binding.cardStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.color_fab_active_new))
                binding.cardStatus.isFocusable = true
            } else {
               binding.playBtn.setImageResource(R.drawable.ic_play_24dp)
               binding.tvStatus.text = getString(R.string.connection_not_connected)
               binding.cardStatus.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.color_fab_inactive_new))
               binding.cardStatus.isFocusable = false
            }
        }
        mainViewModel.startListenBroadcast()
        mainViewModel.initAssets(assets)
    }


    private fun migrateLegacy() {
        lifecycleScope.launch(Dispatchers.IO) {
            val result = MigrateManager.migrateServerConfig2Profile()
            launch(Dispatchers.Main) {
                if (result) {
                    toast(getString(R.string.migration_success))
                    mainViewModel.reloadServerList()
                } else {
                    //toast(getString(R.string.migration_fail))
                }
            }

        }
    }

    private fun setTestState(content: String?) {
        if (content != null) {
            toast(content)
        }
    }

    /**
     * import config from sub
     */
    private fun importConfigViaSub(): Boolean {
        //binding.pbWaiting.show()

        lifecycleScope.launch(Dispatchers.IO) {
            val count = mainViewModel.updateConfigViaSubAll()
            delay(500L)
            launch(Dispatchers.Main) {
                if (count > 0) {
                    toast(getString(R.string.title_update_config_count, count))
                    mainViewModel.reloadServerList()
                } else {
                    toastError(R.string.toast_failure)
                }
                //binding.pbWaiting.hide()
            }
        }
        return true
    }

    private fun initSub(): Boolean {
        //binding.pbWaiting.show()

        lifecycleScope.launch(Dispatchers.IO) {
            val count = mainViewModel.updateConfigViaSubAll()
            delay(500L)
            launch(Dispatchers.Main) {
                if (count > 0) {
                    toast(getString(R.string.title_update_config_count, count))
                    mainViewModel.reloadServerList();
                    delay(500L)
                    mainViewModel.testAllRealPing()
                } else {
                    toastError(R.string.toast_failure)
                }
                //binding.pbWaiting.hide()
            }
        }
        return true
    }

    private fun loadRoutes() {
        val (listId, listRemarks) = mainViewModel.getSubscriptions(this)
        val empty = listId == null || listRemarks == null;

        var uuid = ""
        if (empty){
            uuid  = Utils.getUuid()
            createSub(uuid , "sub1", "https://www.vpnpersonal.com/nodes10MPlus.txt")
        }else{
            if (listId != null) {
                uuid = listId.get(0)
            }
        }

        binding.recyclerView.setHasFixedSize(true)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        lifecycleScope.launch(Dispatchers.IO) {
            delay(500L)
            mainViewModel.subscriptionIdChanged(uuid)
            launch(Dispatchers.Main) {
                initSub();
                //binding.pbWaiting.hide()
            }
        }
    }


    private fun createSub(guid : String, remarks : String, url : String): Boolean {
        val subItem = SubscriptionItem()

        subItem.remarks = remarks
        subItem.url = url

        MmkvManager.encodeSubscription(guid, subItem)
        return true
    }

    private fun setPlayBtn() {
        binding.playBtn.setOnClickListener {
            if (mainViewModel.isRunning.value == true) {
                V2RayServiceManager.stopVService(this)
            } else if ((MmkvManager.decodeSettingsString(AppConfig.PREF_MODE) ?: VPN) == VPN) {
                val intent = VpnService.prepare(this)
                if (intent == null) {
                    startV2Ray()
                } else {
                    requestVpnPermission.launch(intent)
                }
            } else {
                startV2Ray()
            }
        }
    }


    private fun toggleFileAndRefreshBtn() {
        val cardFilter = binding.btnFilterCardView
        val cardRefresh = binding.btnRefreshCardView

        val selectedColor = ContextCompat.getColor(this, R.color.blue_100)
        val defaultColor = ContextCompat.getColor(this, R.color.gray)

        // 🔹 点击事件设置
        cardFilter.setOnClickListener {view ->
            cardFilter.setCardBackgroundColor(selectedColor)
            cardRefresh.setCardBackgroundColor(defaultColor)
           //showInputDialog()
            showSearchPopup(view)
        }

        cardRefresh.setOnClickListener {
            cardRefresh.setCardBackgroundColor(selectedColor)
            cardFilter.setCardBackgroundColor(defaultColor)

            importConfigViaSub()
        }
    }

    fun showSearchPopup(anchorView: View) {
        val popupView = LayoutInflater.from(this).inflate(R.layout.popup_search, null)

        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            true
        ).apply {
            isOutsideTouchable = true
            elevation = 10f
            setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            showAsDropDown(anchorView, 0, 8) // 显示在按钮下方
        }

        val options = listOf(
            R.id.option_all,
            R.id.option_type1,
            R.id.option_type2
        )

        for (id in options) {
            popupView.findViewById<TextView>(id).setOnClickListener { view ->
                val selectedText = (view as TextView).text.toString()
                applyFilter(selectedText)
                popupWindow?.dismiss()
            }
        }
    }

    private fun applyFilter(filterType: String) {
        // TODO: 你的筛选逻辑
        var inputText = "";
        if(!"全部".equals(filterType)){
            inputText = filterType
        }
        mainViewModel.filterConfig(inputText.orEmpty())
    }


    private fun showInputDialog() {
        var search_text = MmkvManager.decodeSettingsString("app_search_text")
        val editText = EditText(this).apply {
            imeOptions = EditorInfo.IME_ACTION_DONE
            inputType = InputType.TYPE_CLASS_TEXT
            gravity = Gravity.TOP
            height = 150

            val searchDrawable = ContextCompat.getDrawable(this@MainNewActivity, R.drawable.ic_description_24dp)
            setCompoundDrawablesWithIntrinsicBounds(null, null, searchDrawable, null)
            compoundDrawablePadding = 8
        }


        editText.setText(search_text)
        editText.setTextColor(Color.BLACK)

        val dialog = AlertDialog.Builder(this)
            .setView(editText)
            .setCancelable(true)  // 允许点外面取消
            .create()

        // 回车提交监听
        editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                val input = editText.text.toString()
                MmkvManager.encodeSettings("app_search_text", input)
                mainViewModel.filterConfig(input.orEmpty())
                dialog.dismiss()
                true
            } else {
                false
            }
        }
        editText.setBackgroundColor(Color.WHITE)
        dialog.show()
    }


    private fun startV2Ray() {
        if (MmkvManager.getSelectServer().isNullOrEmpty()) {
            toast(R.string.title_file_chooser)
            return
        }
        V2RayServiceManager.startVService(this)
    }

    private val requestSubSettingActivity = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {

    }
}