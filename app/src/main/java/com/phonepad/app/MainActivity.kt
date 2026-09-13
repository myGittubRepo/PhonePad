package com.phonepad.app

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHidDevice
import android.bluetooth.BluetoothHidDeviceAppSdpSettings
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.phonepad.app.ui.theme.PhonePadTheme
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "PhonePad"
        private const val TAP_DURATION_MS = 180L
        private const val TAP_MOVEMENT_DP = 10f
        private const val DRAG_HOLD_MS = 350L
        private const val PREFS_NAME = "phonepad_prefs"
        private const val PREF_LAST_HOST_ADDRESS = "last_host_address"
    }

    // Milestone 0 diagnostics
    private var bluetoothStatus by mutableStateOf("Checking…")
    private var permissionStatus by mutableStateOf("Checking…")
    private var hidProfileStatus by mutableStateOf("Checking…")

    // Milestone 1 state
    private var registrationStatus by mutableStateOf("N/A")
    private var connectionStatus by mutableStateOf("DISCONNECTED")
    private val bondedDevices = mutableStateListOf<BluetoothDevice>()

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var hidDevice: BluetoothHidDevice? = null
    private var connectedDevice: BluetoothDevice? = null

    // Milestone 2 touch tracking
    private var previousX = 0f
    private var previousY = 0f
    private var previousEventTime = 0L

    // Milestone 3 tap detection
    private var touchDownTime = 0L
    private var touchDownX = 0f
    private var touchDownY = 0f
    private var tapMovementThresholdPx = 0f
    private var tapEligible = false

    // Milestone 4 drag
    private var isDragging = false
    private var dragEligible = false
    private var fingerDown = false
    private val handler = Handler(Looper.getMainLooper())
    private val dragTriggerRunnable = Runnable { tryActivateDrag() }

    // Milestone 6 persistence and auto-reconnect
    private lateinit var prefs: SharedPreferences
    private var autoReconnectAttempted = false
    private var bluetoothReceiverRegistered = false
    private var isHidAppRegistered = false

    // Mouse HID report descriptor: 3 buttons, relative X, relative Y
    private val mouseDescriptor = byteArrayOf(
        0x05.toByte(), 0x01.toByte(), // USAGE_PAGE (Generic Desktop)
        0x09.toByte(), 0x02.toByte(), // USAGE (Mouse)
        0xA1.toByte(), 0x01.toByte(), // COLLECTION (Application)
        0x09.toByte(), 0x01.toByte(), //   USAGE (Pointer)
        0xA1.toByte(), 0x00.toByte(), //   COLLECTION (Physical)
        // Buttons (3)
        0x05.toByte(), 0x09.toByte(), //     USAGE_PAGE (Button)
        0x19.toByte(), 0x01.toByte(), //     USAGE_MINIMUM (Button 1)
        0x29.toByte(), 0x03.toByte(), //     USAGE_MAXIMUM (Button 3)
        0x15.toByte(), 0x00.toByte(), //     LOGICAL_MINIMUM (0)
        0x25.toByte(), 0x01.toByte(), //     LOGICAL_MAXIMUM (1)
        0x95.toByte(), 0x03.toByte(), //     REPORT_COUNT (3)
        0x75.toByte(), 0x01.toByte(), //     REPORT_SIZE (1)
        0x81.toByte(), 0x02.toByte(), //     INPUT (Data,Var,Abs)
        // Padding (5 bits)
        0x95.toByte(), 0x01.toByte(), //     REPORT_COUNT (1)
        0x75.toByte(), 0x05.toByte(), //     REPORT_SIZE (5)
        0x81.toByte(), 0x03.toByte(), //     INPUT (Cnst,Var,Abs)
        // X, Y relative movement
        0x05.toByte(), 0x01.toByte(), //     USAGE_PAGE (Generic Desktop)
        0x09.toByte(), 0x30.toByte(), //     USAGE (X)
        0x09.toByte(), 0x31.toByte(), //     USAGE (Y)
        0x15.toByte(), 0x81.toByte(), //     LOGICAL_MINIMUM (-127)
        0x25.toByte(), 0x7F.toByte(), //     LOGICAL_MAXIMUM (127)
        0x75.toByte(), 0x08.toByte(), //     REPORT_SIZE (8)
        0x95.toByte(), 0x02.toByte(), //     REPORT_COUNT (2)
        0x81.toByte(), 0x06.toByte(), //     INPUT (Data,Var,Rel)
        0xC0.toByte(),               //   END_COLLECTION
        0xC0.toByte()                // END_COLLECTION
    )

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val allGranted = results.values.all { it }
        if (allGranted) {
            Log.d(TAG, "Bluetooth permissions granted")
            permissionStatus = "Granted"
            ensureHidSession()
        } else {
            Log.w(TAG, "Bluetooth permissions denied: $results")
            permissionStatus = "Denied"
            hidProfileStatus = "N/A (permission denied)"
        }
    }

    private val hidCallback = object : BluetoothHidDevice.Callback() {
        override fun onAppStatusChanged(pluggedDevice: BluetoothDevice?, registered: Boolean) {
            Log.d(TAG, "onAppStatusChanged: registered=$registered, device=$pluggedDevice")
            isHidAppRegistered = registered
            if (registered) {
                registrationStatus = "REGISTERED"
                Log.d(TAG, "New registration cycle — resetting autoReconnectAttempted")
                autoReconnectAttempted = false
                loadBondedDevices()
                attemptAutoReconnect()
            } else {
                registrationStatus = "NOT REGISTERED"
                Log.d(TAG, "Registration lost — resetting autoReconnectAttempted")
                autoReconnectAttempted = false
            }
        }

        override fun onConnectionStateChanged(device: BluetoothDevice?, state: Int) {
            val stateName = when (state) {
                BluetoothProfile.STATE_CONNECTED -> "CONNECTED"
                BluetoothProfile.STATE_CONNECTING -> "CONNECTING"
                BluetoothProfile.STATE_DISCONNECTED -> "DISCONNECTED"
                BluetoothProfile.STATE_DISCONNECTING -> "DISCONNECTING"
                else -> "UNKNOWN($state)"
            }
            Log.d(TAG, "onConnectionStateChanged: device=$device, state=$stateName")
            connectionStatus = stateName

            when (state) {
                BluetoothProfile.STATE_CONNECTED -> {
                    connectedDevice = device
                    if (device != null) {
                        saveLastHost(device.address)
                    }
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    cleanupDragState()
                    connectedDevice = null
                }
                else -> {}
            }
        }
    }

    private val profileListener = object : BluetoothProfile.ServiceListener {
        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile?) {
            Log.d(TAG, "onServiceConnected: profile=$profile")
            if (profile == BluetoothProfile.HID_DEVICE && proxy is BluetoothHidDevice) {
                Log.d(TAG, "HID_DEVICE profile proxy obtained — SUPPORTED")
                hidProfileStatus = "SUPPORTED"
                hidDevice = proxy
                registerHidApp()
            }
        }

        override fun onServiceDisconnected(profile: Int) {
            Log.d(TAG, "onServiceDisconnected: profile=$profile")
            if (profile == BluetoothProfile.HID_DEVICE) {
                Log.w(TAG, "HID_DEVICE profile service lost — resetting session state")
                cleanupDragState()
                hidDevice = null
                isHidAppRegistered = false
                registrationStatus = "NOT REGISTERED"
                connectionStatus = "DISCONNECTED"
                connectedDevice = null
                hidProfileStatus = "DISCONNECTED"
                autoReconnectAttempted = false
            }
        }
    }

    private val bluetoothStateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action != BluetoothAdapter.ACTION_STATE_CHANGED) return
            val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR)

            when (state) {
                BluetoothAdapter.STATE_TURNING_OFF, BluetoothAdapter.STATE_OFF -> {
                    Log.w(TAG, "Bluetooth STATE_OFF — cleaning up")
                    bluetoothStatus = if (state == BluetoothAdapter.STATE_OFF) "Bluetooth disabled" else "Turning off…"
                    cleanupDragState()
                    connectedDevice = null
                    connectionStatus = "DISCONNECTED"
                    isHidAppRegistered = false
                    registrationStatus = "NOT REGISTERED"
                    autoReconnectAttempted = false
                    hidProfileStatus = "N/A (Bluetooth off)"
                }
                BluetoothAdapter.STATE_ON -> {
                    Log.d(TAG, "Bluetooth STATE_ON — starting HID session recovery")
                    bluetoothStatus = "Enabled"
                    ensureHidSession()
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
        tapMovementThresholdPx = TAP_MOVEMENT_DP * resources.displayMetrics.density

        val lastHost = prefs.getString(PREF_LAST_HOST_ADDRESS, null)
        if (lastHost != null) {
            Log.d(TAG, "Last host restored: $lastHost")
        }

        val bluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager?.adapter

        if (bluetoothAdapter == null) {
            Log.e(TAG, "BluetoothAdapter unavailable — no Bluetooth hardware")
            bluetoothStatus = "Bluetooth unavailable"
            permissionStatus = "N/A"
            hidProfileStatus = "N/A"
        } else {
            Log.d(TAG, "BluetoothAdapter available")
            registerBluetoothReceiver()
            if (!bluetoothAdapter!!.isEnabled) {
                Log.w(TAG, "Bluetooth is disabled")
                bluetoothStatus = "Bluetooth disabled"
                requestBluetoothPermissions()
            } else {
                Log.d(TAG, "Bluetooth is enabled")
                bluetoothStatus = "Enabled"
                requestBluetoothPermissions()
            }
        }

        setContent {
            PhonePadTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    PhonePadScreen(
                        bluetoothStatus = bluetoothStatus,
                        permissionStatus = permissionStatus,
                        hidProfileStatus = hidProfileStatus,
                        registrationStatus = registrationStatus,
                        connectionStatus = connectionStatus,
                        bondedDevices = bondedDevices,
                        isConnected = connectedDevice != null,
                        onDeviceSelected = { device -> connectToDevice(device) },
                        onTouchEvent = { event -> handleTrackpadTouch(event) },
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: checking HID session recovery")
        if (connectedDevice == null) {
            Log.d(TAG, "onResume: no active connection — resetting autoReconnectAttempted")
            autoReconnectAttempted = false
        }
        if (bluetoothAdapter != null && hasBluetoothPermissions()) {
            ensureHidSession()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: cleaning up")
        unregisterBluetoothReceiver()
        handler.removeCallbacks(dragTriggerRunnable)
        cleanupDragState()
        hidDevice?.let { hid ->
            Log.d(TAG, "Closing HID profile proxy")
            try {
                hid.unregisterApp()
            } catch (e: SecurityException) {
                Log.w(TAG, "SecurityException on unregisterApp: ${e.message}")
            }
            bluetoothAdapter?.closeProfileProxy(BluetoothProfile.HID_DEVICE, hid)
        }
        hidDevice = null
        connectedDevice = null
        isHidAppRegistered = false
    }

    @SuppressLint("MissingPermission")
    private fun ensureHidSession() {
        val adapter = bluetoothAdapter ?: return
        if (!adapter.isEnabled) {
            Log.d(TAG, "ensureHidSession: Bluetooth disabled, cannot proceed")
            bluetoothStatus = "Bluetooth disabled"
            return
        }
        bluetoothStatus = "Enabled"

        if (!hasBluetoothPermissions()) {
            Log.d(TAG, "ensureHidSession: missing permissions, cannot proceed")
            return
        }

        if (hidDevice == null) {
            Log.d(TAG, "ensureHidSession: no HID profile proxy — requesting profile")
            hidProfileStatus = "Checking…"
            val requested = adapter.getProfileProxy(this, profileListener, BluetoothProfile.HID_DEVICE)
            Log.d(TAG, "ensureHidSession: getProfileProxy returned $requested")
            if (!requested) {
                hidProfileStatus = "UNSUPPORTED"
            }
            return
        }

        if (!isHidAppRegistered) {
            Log.d(TAG, "ensureHidSession: HID profile present but app not registered — registering")
            registerHidApp()
            return
        }

        if (connectedDevice == null) {
            Log.d(TAG, "ensureHidSession: registered but disconnected — attempting reconnect")
            attemptAutoReconnect()
        }
    }

    private fun hasBluetoothPermissions(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADVERTISE) == PackageManager.PERMISSION_GRANTED
        } else {
            true
        }
    }

    private fun registerBluetoothReceiver() {
        if (bluetoothReceiverRegistered) return
        val filter = IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        registerReceiver(bluetoothStateReceiver, filter)
        bluetoothReceiverRegistered = true
        Log.d(TAG, "Bluetooth state receiver registered")
    }

    private fun unregisterBluetoothReceiver() {
        if (!bluetoothReceiverRegistered) return
        try {
            unregisterReceiver(bluetoothStateReceiver)
        } catch (e: IllegalArgumentException) {
            Log.w(TAG, "Bluetooth receiver already unregistered: ${e.message}")
        }
        bluetoothReceiverRegistered = false
        Log.d(TAG, "Bluetooth state receiver unregistered")
    }

    private fun cleanupDragState() {
        handler.removeCallbacks(dragTriggerRunnable)
        if (isDragging) {
            Log.d(TAG, "Cleaning up drag state, releasing button")
            releaseLeftButton()
        }
        isDragging = false
        dragEligible = false
        tapEligible = false
        fingerDown = false
    }

    @SuppressLint("MissingPermission")
    private fun saveLastHost(address: String) {
        Log.d(TAG, "Saving last host: $address")
        prefs.edit().putString(PREF_LAST_HOST_ADDRESS, address).apply()
    }

    @SuppressLint("MissingPermission")
    private fun attemptAutoReconnect() {
        if (autoReconnectAttempted) {
            Log.d(TAG, "Auto-reconnect: already attempted this cycle, skipping")
            return
        }
        if (connectedDevice != null) {
            Log.d(TAG, "Auto-reconnect: already connected, skipping")
            return
        }

        val lastAddress = prefs.getString(PREF_LAST_HOST_ADDRESS, null)
        if (lastAddress == null) {
            Log.d(TAG, "Auto-reconnect: no last host stored, skipping")
            return
        }

        val adapter = bluetoothAdapter ?: return
        val hid = hidDevice ?: return

        val bonded = adapter.bondedDevices ?: emptySet()
        val target = bonded.find { it.address == lastAddress }

        if (target == null) {
            Log.d(TAG, "Auto-reconnect: last host $lastAddress not in bonded devices, skipping")
            return
        }

        autoReconnectAttempted = true
        Log.d(TAG, "Auto-reconnect: attempting to connect to ${target.name} [$lastAddress]")
        connectionStatus = "CONNECTING"
        val requested = hid.connect(target)
        Log.d(TAG, "Auto-reconnect: connect() returned $requested")
        if (!requested) {
            Log.w(TAG, "Auto-reconnect: connect request was not accepted")
            connectionStatus = "DISCONNECTED"
        }
    }

    private fun requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val permissions = arrayOf(
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.BLUETOOTH_ADVERTISE
            )
            val allGranted = permissions.all {
                ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
            }
            if (allGranted) {
                Log.d(TAG, "Bluetooth permissions already granted")
                permissionStatus = "Granted"
                ensureHidSession()
            } else {
                Log.d(TAG, "Requesting Bluetooth permissions")
                permissionLauncher.launch(permissions)
            }
        } else {
            Log.d(TAG, "Android < 12 — no runtime Bluetooth permissions needed")
            permissionStatus = "Granted (pre-Android 12)"
            ensureHidSession()
        }
    }

    @SuppressLint("MissingPermission")
    private fun registerHidApp() {
        val hid = hidDevice ?: return

        val sdpSettings = BluetoothHidDeviceAppSdpSettings(
            "PhonePad",
            "Android Bluetooth Trackpad",
            "PhonePad",
            BluetoothHidDevice.SUBCLASS1_MOUSE,
            mouseDescriptor
        )

        Log.d(TAG, "Calling registerApp()")
        val requested = hid.registerApp(sdpSettings, null, null, { it.run() }, hidCallback)
        Log.d(TAG, "registerApp() returned: $requested")

        if (!requested) {
            Log.w(TAG, "registerApp() request was not accepted")
            registrationStatus = "NOT REGISTERED"
        }
    }

    @SuppressLint("MissingPermission")
    private fun loadBondedDevices() {
        val adapter = bluetoothAdapter ?: return
        bondedDevices.clear()
        val paired = adapter.bondedDevices ?: emptySet()
        Log.d(TAG, "Bonded devices: ${paired.size}")
        paired.forEach { device ->
            Log.d(TAG, "  Bonded: ${device.name} [${device.address}]")
            bondedDevices.add(device)
        }
    }

    @SuppressLint("MissingPermission")
    private fun connectToDevice(device: BluetoothDevice) {
        val hid = hidDevice ?: return
        Log.d(TAG, "Connecting to: ${device.name} [${device.address}]")
        connectionStatus = "CONNECTING"
        val requested = hid.connect(device)
        Log.d(TAG, "connect() returned: $requested")
        if (!requested) {
            Log.w(TAG, "connect() request was not accepted")
            connectionStatus = "DISCONNECTED"
        }
    }

    private fun handleTrackpadTouch(event: MotionEvent): Boolean {
        if (connectedDevice == null || hidDevice == null) return false

        if (event.pointerCount > 1) {
            tapEligible = false
            dragEligible = false
            handler.removeCallbacks(dragTriggerRunnable)
            if (isDragging) {
                Log.d(TAG, "Drag cancelled: multi-touch detected")
                releaseLeftButton()
                isDragging = false
            }
        }

        if (event.pointerCount != 1) return false

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                previousX = event.x
                previousY = event.y
                previousEventTime = event.eventTime
                touchDownTime = SystemClock.uptimeMillis()
                touchDownX = event.x
                touchDownY = event.y
                tapEligible = true
                dragEligible = true
                isDragging = false
                fingerDown = true
                handler.postDelayed(dragTriggerRunnable, DRAG_HOLD_MS)
                Log.d(TAG, "Touch started at (${event.x}, ${event.y})")
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - previousX
                val dy = event.y - previousY
                val dtMs = event.eventTime - previousEventTime
                previousX = event.x
                previousY = event.y
                previousEventTime = event.eventTime

                if (!isDragging) {
                    val distX = event.x - touchDownX
                    val distY = event.y - touchDownY
                    val distance = sqrt(distX * distX + distY * distY)

                    if (distance > tapMovementThresholdPx) {
                        if (tapEligible) tapEligible = false
                        if (dragEligible) {
                            dragEligible = false
                            handler.removeCallbacks(dragTriggerRunnable)
                        }
                    }
                }

                val (accX, accY) = applyAcceleration(dx, dy, dtMs)

                if (accX != 0 || accY != 0) {
                    if (isDragging) {
                        sendMouseReport(0x01, accX, accY)
                    } else {
                        sendMouseReport(0x00, accX, accY)
                    }
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                handler.removeCallbacks(dragTriggerRunnable)
                fingerDown = false

                if (isDragging) {
                    Log.d(TAG, "Drag ended")
                    releaseLeftButton()
                    isDragging = false
                } else {
                    val duration = SystemClock.uptimeMillis() - touchDownTime
                    when {
                        !tapEligible -> {
                            Log.d(TAG, "Tap suppressed: movement threshold exceeded during gesture")
                        }
                        duration > TAP_DURATION_MS -> {
                            Log.d(TAG, "Tap suppressed: duration ${duration}ms > ${TAP_DURATION_MS}ms")
                        }
                        else -> {
                            Log.d(TAG, "Tap detected: duration=${duration}ms")
                            sendLeftClick()
                        }
                    }
                }
                return true
            }
            MotionEvent.ACTION_CANCEL -> {
                handler.removeCallbacks(dragTriggerRunnable)
                fingerDown = false
                if (isDragging) {
                    Log.d(TAG, "Drag cancelled")
                    releaseLeftButton()
                    isDragging = false
                }
                tapEligible = false
                dragEligible = false
                Log.d(TAG, "Touch cancelled")
                return true
            }
        }
        return false
    }

    @SuppressLint("MissingPermission")
    private fun tryActivateDrag() {
        if (!fingerDown || !dragEligible || isDragging) return
        if (connectedDevice == null || hidDevice == null) return

        isDragging = true
        tapEligible = false
        dragEligible = false
        Log.d(TAG, "Drag activated after ${DRAG_HOLD_MS}ms hold")
        sendMouseReport(0x01, 0, 0)
    }

    private fun applyAcceleration(dx: Float, dy: Float, dtMs: Long): Pair<Int, Int> {
        val dt = if (dtMs > 0) dtMs.toFloat() else 1f
        val distance = sqrt(dx * dx + dy * dy)
        val speed = distance / dt

        val gain = when {
            speed <= 1.0f -> 0.8f
            speed <= 4.0f -> 0.8f + (speed - 1.0f) * (1.5f - 0.8f) / (4.0f - 1.0f)
            else -> 2.2f
        }

        val outX = (dx * gain).toInt().coerceIn(-127, 127)
        val outY = (dy * gain).toInt().coerceIn(-127, 127)
        return Pair(outX, outY)
    }

    @SuppressLint("MissingPermission")
    private fun sendLeftClick() {
        val hid = hidDevice ?: return
        val device = connectedDevice ?: return

        val down = byteArrayOf(0x01.toByte(), 0x00.toByte(), 0x00.toByte())
        val up = byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x00.toByte())

        val downResult = hid.sendReport(device, 0, down)
        val upResult = hid.sendReport(device, 0, up)

        if (!downResult || !upResult) {
            Log.w(TAG, "sendLeftClick failed: down=$downResult, up=$upResult")
        }
    }

    @SuppressLint("MissingPermission")
    private fun releaseLeftButton() {
        val hid = hidDevice ?: return
        val device = connectedDevice ?: return

        val release = byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x00.toByte())
        val result = hid.sendReport(device, 0, release)
        if (!result) {
            Log.w(TAG, "releaseLeftButton failed")
        }
    }

    @SuppressLint("MissingPermission")
    private fun sendMouseReport(buttons: Int, dx: Int, dy: Int) {
        val hid = hidDevice ?: return
        val device = connectedDevice ?: return

        val report = byteArrayOf(
            buttons.toByte(),
            dx.toByte(),
            dy.toByte()
        )

        val result = hid.sendReport(device, 0, report)
        if (!result) {
            Log.w(TAG, "sendReport failed: buttons=$buttons, dx=$dx, dy=$dy")
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@SuppressLint("MissingPermission")
@Composable
fun PhonePadScreen(
    bluetoothStatus: String,
    permissionStatus: String,
    hidProfileStatus: String,
    registrationStatus: String,
    connectionStatus: String,
    bondedDevices: List<BluetoothDevice>,
    isConnected: Boolean,
    onDeviceSelected: (BluetoothDevice) -> Unit,
    onTouchEvent: (MotionEvent) -> Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "PhonePad",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))

        DiagnosticRow("Bluetooth:", bluetoothStatus)
        DiagnosticRow("Permission:", permissionStatus)
        DiagnosticRow("HID Device Profile:", hidProfileStatus)
        DiagnosticRow("HID App Registration:", registrationStatus)
        DiagnosticRow("Connection:", connectionStatus)

        Spacer(modifier = Modifier.height(12.dp))

        if (bondedDevices.isNotEmpty() && !isConnected) {
            Text(
                text = "Bonded Devices",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            bondedDevices.forEach { device ->
                OutlinedButton(
                    onClick = { onDeviceSelected(device) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp)
                ) {
                    Text(text = "${device.name ?: "Unknown"} [${device.address}]", fontSize = 13.sp)
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .border(
                    width = 2.dp,
                    color = if (isConnected)
                        MaterialTheme.colorScheme.primary
                    else
                        MaterialTheme.colorScheme.outlineVariant,
                    shape = RoundedCornerShape(12.dp)
                )
                .background(
                    color = if (isConnected)
                        MaterialTheme.colorScheme.surfaceVariant
                    else
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(12.dp)
                )
                .then(
                    if (isConnected) {
                        Modifier.pointerInteropFilter { event -> onTouchEvent(event) }
                    } else {
                        Modifier
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isConnected) "Move one finger to control cursor\nTap to click\nHold to drag"
                       else "Not connected",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                textAlign = TextAlign.Center
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun DiagnosticRow(label: String, value: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 2.dp)
    ) {
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
