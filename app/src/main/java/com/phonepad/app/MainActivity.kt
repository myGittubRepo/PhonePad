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
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "PhonePad"
        private const val TAP_DURATION_MS = 180L
        private const val TAP_MOVEMENT_DP = 10f
        private const val DRAG_HOLD_MS = 350L
        private const val PREFS_NAME = "phonepad_prefs"
        private const val PREF_LAST_HOST_ADDRESS = "last_host_address"

        private const val SCROLL_PIXELS_PER_NOTCH = 12f
        private const val SCROLL_DIRECTION = 1
        private const val SCROLL_DIRECTION_H = -1
        private const val SCROLL_OUTPUT_INTERVAL_MS = 16L
        private const val SCROLL_VELOCITY_SMOOTHING = 0.35f
        private const val SCROLL_MOMENTUM_FRICTION = 0.94f
        private const val SCROLL_MOMENTUM_MIN_VELOCITY = 0.05f
        private const val SCROLL_MOMENTUM_RELEASE_GRACE_MS = 150L
        private const val SCROLL_AXIS_LOCK_THRESHOLD_DP = 8f

        // Two-finger tap → right-click
        private const val TWO_FINGER_TAP_DURATION_MS = 300L
        private const val TWO_FINGER_TAP_MOVEMENT_DP = 15f

        // Milestone 2.4 pinch-to-zoom
        private const val PINCH_DISTANCE_THRESHOLD_DP = 20f
        private const val PINCH_PIXELS_PER_STEP_DP = 20f
        private const val KEYBOARD_REPORT_ID: Int = 3
        private const val KEY_MOD_LCTRL: Byte = 0x01

        private const val RESOLUTION_MULTIPLIER_PHYSICAL_MIN = 1
        private const val RESOLUTION_MULTIPLIER_PHYSICAL_MAX = 8
        private const val FEATURE_REPORT_ID: Byte = 2
        private const val INPUT_REPORT_ID: Int = 1
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
    private var twoFingerTapMovementThresholdPx = 0f
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

    // Milestone 2.1 two-finger scroll (vertical)
    private var isTwoFingerScrolling = false
    private var gestureContainedMultiTouch = false
    private var previousCentroidY = 0f
    private var scrollAccumulator = 0f
    private var scrollVelocityPxPerMs = 0f
    private var lastScrollEventTime = 0L
    private var scrollOutputActive = false
    private var momentumScrollActive = false
    private var previousScrollTickTime = 0L
    private var pointerUpTime = 0L
    private val scrollTickRunnable = Runnable { tickScrollOutput() }

    // Milestone 2.2 horizontal scroll (AC Pan) + axis lock
    private var previousCentroidX = 0f
    private var scrollAccumulatorX = 0f
    private var scrollVelocityXPxPerMs = 0f
    private var initialCentroidX = 0f
    private var initialCentroidY = 0f
    private var scrollAxisLocked = false
    private var isHorizontalScroll = false
    private var scrollAxisLockThresholdPx = 0f

    // Milestone 2.4 pinch-to-zoom
    private enum class TwoFingerMode { UNDECIDED, SCROLL, PINCH }
    private var twoFingerMode = TwoFingerMode.UNDECIDED
    private var initialFingerDistance = 0f
    private var previousFingerDistance = 0f
    private var pinchAccumulator = 0f
    private var pinchDistanceThresholdPx = 0f
    private var pinchPixelsPerStep = 0f
    private var ctrlHeldForPinch = false

    // High-resolution wheel
    private var wheelResolutionMultiplierRaw = 0
    private var effectiveWheelMultiplier = 1

    // Milestone 2.3 two-finger tap = right-click
    private var twoFingerDownTime = 0L
    private var twoFingerTapPointerId0 = -1
    private var twoFingerTapPointerId1 = -1
    private var twoFingerDownX0 = 0f
    private var twoFingerDownY0 = 0f
    private var twoFingerDownX1 = 0f
    private var twoFingerDownY1 = 0f
    private var twoFingerTapEligible = false
    private var rightClickFiredInGesture = false

    // Mouse HID report descriptor: 3 buttons, relative X/Y, vertical wheel
    // with Resolution Multiplier Feature Report for high-resolution scrolling.
    // Report ID 1 = Input (buttons, X, Y, wheel), Report ID 2 = Feature (multiplier).
    private val mouseDescriptor = byteArrayOf(
        0x05.toByte(), 0x01.toByte(), // USAGE_PAGE (Generic Desktop)
        0x09.toByte(), 0x02.toByte(), // USAGE (Mouse)
        0xA1.toByte(), 0x01.toByte(), // COLLECTION (Application)
        0x09.toByte(), 0x01.toByte(), //   USAGE (Pointer)
        0xA1.toByte(), 0x00.toByte(), //   COLLECTION (Physical)

        // --- Report ID 1: Input Report ---
        0x85.toByte(), 0x01.toByte(), //     REPORT_ID (1)

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

        // --- Logical Collection: Wheel + Resolution Multiplier ---
        0xA1.toByte(), 0x02.toByte(), //     COLLECTION (Logical)

        // Feature Report: Resolution Multiplier (Report ID 2)
        0x85.toByte(), 0x02.toByte(), //       REPORT_ID (2)
        0x09.toByte(), 0x48.toByte(), //       USAGE (Resolution Multiplier)
        0x15.toByte(), 0x00.toByte(), //       LOGICAL_MINIMUM (0)
        0x25.toByte(), 0x01.toByte(), //       LOGICAL_MAXIMUM (1)
        0x35.toByte(), 0x01.toByte(), //       PHYSICAL_MINIMUM (1)
        0x45.toByte(), 0x08.toByte(), //       PHYSICAL_MAXIMUM (8)
        0x75.toByte(), 0x02.toByte(), //       REPORT_SIZE (2)
        0x95.toByte(), 0x01.toByte(), //       REPORT_COUNT (1)
        0xB1.toByte(), 0x02.toByte(), //       FEATURE (Data,Var,Abs)
        // Feature padding (6 bits to fill byte)
        0x75.toByte(), 0x06.toByte(), //       REPORT_SIZE (6)
        0x95.toByte(), 0x01.toByte(), //       REPORT_COUNT (1)
        0xB1.toByte(), 0x01.toByte(), //       FEATURE (Cnst,Var,Abs)

        // Input: Wheel (Report ID 1, bound to same Logical Collection as multiplier)
        0x85.toByte(), 0x01.toByte(), //       REPORT_ID (1)
        0x09.toByte(), 0x38.toByte(), //       USAGE (Wheel)
        0x15.toByte(), 0x81.toByte(), //       LOGICAL_MINIMUM (-127)
        0x25.toByte(), 0x7F.toByte(), //       LOGICAL_MAXIMUM (127)
        0x35.toByte(), 0x00.toByte(), //       PHYSICAL_MINIMUM (0)
        0x45.toByte(), 0x00.toByte(), //       PHYSICAL_MAXIMUM (0)
        0x75.toByte(), 0x08.toByte(), //       REPORT_SIZE (8)
        0x95.toByte(), 0x01.toByte(), //       REPORT_COUNT (1)
        0x81.toByte(), 0x06.toByte(), //       INPUT (Data,Var,Rel)

        0xC0.toByte(),               //     END_COLLECTION (Logical)

        // Horizontal wheel (AC Pan, Consumer usage 0x0238) — Milestone 2.2
        0x05.toByte(), 0x0C.toByte(),                   //   USAGE_PAGE (Consumer)
        0x0A.toByte(), 0x38.toByte(), 0x02.toByte(),    //   USAGE (AC Pan)
        0x15.toByte(), 0x81.toByte(),                   //   LOGICAL_MINIMUM (-127)
        0x25.toByte(), 0x7F.toByte(),                   //   LOGICAL_MAXIMUM (127)
        0x75.toByte(), 0x08.toByte(),                   //   REPORT_SIZE (8)
        0x95.toByte(), 0x01.toByte(),                   //   REPORT_COUNT (1)
        0x81.toByte(), 0x06.toByte(),                   //   INPUT (Data,Var,Rel)

        0xC0.toByte(),               //   END_COLLECTION (Physical)
        0xC0.toByte(),               // END_COLLECTION (Application)

        // ===== Keyboard Application Collection (Report ID 3) =====
        // Standard boot keyboard layout: 1 modifier byte + 1 reserved
        // byte + 6 keycode bytes = 8-byte report. Milestone 2.4 uses only
        // the modifier byte (Ctrl for pinch-to-zoom); Phase 3 will use
        // the keycode array for Win+Tab, Alt+Tab, Win+D, etc.
        0x05.toByte(), 0x01.toByte(), // USAGE_PAGE (Generic Desktop)
        0x09.toByte(), 0x06.toByte(), // USAGE (Keyboard)
        0xA1.toByte(), 0x01.toByte(), // COLLECTION (Application)
        0x85.toByte(), KEYBOARD_REPORT_ID.toByte(), //   REPORT_ID (3)

        // Modifier byte (8 bits: LCtrl LShift LAlt LGui RCtrl RShift RAlt RGui)
        0x05.toByte(), 0x07.toByte(), //   USAGE_PAGE (Key Codes)
        0x19.toByte(), 0xE0.toByte(), //   USAGE_MINIMUM (LCtrl)
        0x29.toByte(), 0xE7.toByte(), //   USAGE_MAXIMUM (RGui)
        0x15.toByte(), 0x00.toByte(), //   LOGICAL_MINIMUM (0)
        0x25.toByte(), 0x01.toByte(), //   LOGICAL_MAXIMUM (1)
        0x75.toByte(), 0x01.toByte(), //   REPORT_SIZE (1)
        0x95.toByte(), 0x08.toByte(), //   REPORT_COUNT (8)
        0x81.toByte(), 0x02.toByte(), //   INPUT (Data,Var,Abs)

        // Reserved byte
        0x75.toByte(), 0x08.toByte(), //   REPORT_SIZE (8)
        0x95.toByte(), 0x01.toByte(), //   REPORT_COUNT (1)
        0x81.toByte(), 0x03.toByte(), //   INPUT (Cnst,Var,Abs)

        // 6 keycodes (array — up to 6 keys held simultaneously)
        0x05.toByte(), 0x07.toByte(), //   USAGE_PAGE (Key Codes)
        0x19.toByte(), 0x00.toByte(), //   USAGE_MINIMUM (0)
        0x29.toByte(), 0xFF.toByte(), //   USAGE_MAXIMUM (255)
        0x15.toByte(), 0x00.toByte(), //   LOGICAL_MINIMUM (0)
        0x26.toByte(), 0xFF.toByte(), 0x00.toByte(), // LOGICAL_MAXIMUM (255)
        0x75.toByte(), 0x08.toByte(), //   REPORT_SIZE (8)
        0x95.toByte(), 0x06.toByte(), //   REPORT_COUNT (6)
        0x81.toByte(), 0x00.toByte(), //   INPUT (Data,Ary,Abs)

        0xC0.toByte()                // END_COLLECTION (Application)
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
                    stopScrollOutput()
                    resetScrollState()
                    resetWheelMultiplier()
                    connectedDevice = null
                }
                else -> {}
            }
        }

        @SuppressLint("MissingPermission")
        override fun onGetReport(device: BluetoothDevice?, type: Byte, id: Byte, bufferSize: Int) {
            Log.d(TAG, "onGetReport: type=$type, id=$id, bufferSize=$bufferSize")
            val hid = hidDevice ?: return
            if (type == 3.toByte() && id == FEATURE_REPORT_ID) {
                val data = byteArrayOf((wheelResolutionMultiplierRaw and 0x03).toByte())
                hid.replyReport(device, type, id, data)
                Log.d(TAG, "Replied to GET_REPORT: multiplier raw=$wheelResolutionMultiplierRaw")
            } else {
                hid.reportError(device, BluetoothHidDevice.ERROR_RSP_UNSUPPORTED_REQ)
            }
        }

        @SuppressLint("MissingPermission")
        override fun onSetReport(device: BluetoothDevice?, type: Byte, id: Byte, data: ByteArray?) {
            Log.d(TAG, "onSetReport: type=$type, id=$id, data=${data?.map { it.toInt() and 0xFF }}")
            val hid = hidDevice ?: return
            if (type == 3.toByte() && id == FEATURE_REPORT_ID && data != null && data.isNotEmpty()) {
                val rawValue = data[0].toInt() and 0x03
                wheelResolutionMultiplierRaw = rawValue
                effectiveWheelMultiplier = rawValue *
                    (RESOLUTION_MULTIPLIER_PHYSICAL_MAX - RESOLUTION_MULTIPLIER_PHYSICAL_MIN) +
                    RESOLUTION_MULTIPLIER_PHYSICAL_MIN
                Log.d(TAG, "Resolution Multiplier SET: raw=$rawValue, effective=${effectiveWheelMultiplier}x")
                hid.reportError(device, BluetoothHidDevice.ERROR_RSP_SUCCESS)
            } else {
                hid.reportError(device, BluetoothHidDevice.ERROR_RSP_UNSUPPORTED_REQ)
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
                stopScrollOutput()
                resetScrollState()
                resetWheelMultiplier()
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
                    stopScrollOutput()
                    resetScrollState()
                    resetWheelMultiplier()
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
        twoFingerTapMovementThresholdPx = TWO_FINGER_TAP_MOVEMENT_DP * resources.displayMetrics.density
        scrollAxisLockThresholdPx = SCROLL_AXIS_LOCK_THRESHOLD_DP * resources.displayMetrics.density
        pinchDistanceThresholdPx = PINCH_DISTANCE_THRESHOLD_DP * resources.displayMetrics.density
        pinchPixelsPerStep = PINCH_PIXELS_PER_STEP_DP * resources.displayMetrics.density

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
        stopScrollOutput()
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

    private fun resetScrollState() {
        isTwoFingerScrolling = false
        gestureContainedMultiTouch = false
        scrollAccumulator = 0f
        scrollAccumulatorX = 0f
        previousCentroidY = 0f
        previousCentroidX = 0f
        scrollVelocityPxPerMs = 0f
        scrollVelocityXPxPerMs = 0f
        lastScrollEventTime = 0L
        pointerUpTime = 0L
        twoFingerTapEligible = false
        rightClickFiredInGesture = false
        scrollAxisLocked = false
        isHorizontalScroll = false
        twoFingerMode = TwoFingerMode.UNDECIDED
        pinchAccumulator = 0f
        initialFingerDistance = 0f
        previousFingerDistance = 0f
        if (ctrlHeldForPinch) releaseCtrlForPinch()
    }

    private fun resetWheelMultiplier() {
        wheelResolutionMultiplierRaw = 0
        effectiveWheelMultiplier = 1
        Log.d(TAG, "Wheel resolution multiplier reset to default (1x)")
    }

    private fun stopScrollOutput() {
        if (scrollOutputActive || momentumScrollActive) {
            Log.d(TAG, "Scroll output stopped")
        }
        scrollOutputActive = false
        momentumScrollActive = false
        handler.removeCallbacks(scrollTickRunnable)
    }

    private fun startScrollOutputLoop() {
        if (scrollOutputActive) return
        scrollOutputActive = true
        previousScrollTickTime = SystemClock.uptimeMillis()
        handler.postDelayed(scrollTickRunnable, SCROLL_OUTPUT_INTERVAL_MS)
    }

    private fun stopScrollOutputLoop() {
        scrollOutputActive = false
        if (!momentumScrollActive) {
            handler.removeCallbacks(scrollTickRunnable)
        }
    }

    private fun startMomentum() {
        val bothIdle = abs(scrollVelocityPxPerMs) < SCROLL_MOMENTUM_MIN_VELOCITY &&
            abs(scrollVelocityXPxPerMs) < SCROLL_MOMENTUM_MIN_VELOCITY
        if (bothIdle) {
            Log.d(TAG, "Scroll velocity too low for momentum: v=$scrollVelocityPxPerMs, h=$scrollVelocityXPxPerMs px/ms")
            scrollAccumulator = 0f; scrollAccumulatorX = 0f
            scrollVelocityPxPerMs = 0f; scrollVelocityXPxPerMs = 0f
            return
        }
        momentumScrollActive = true
        previousScrollTickTime = SystemClock.uptimeMillis()
        Log.d(TAG, "Scroll momentum started, v=$scrollVelocityPxPerMs, h=$scrollVelocityXPxPerMs px/ms")
        handler.postDelayed(scrollTickRunnable, SCROLL_OUTPUT_INTERVAL_MS)
    }

    private fun tickScrollOutput() {
        if (!scrollOutputActive && !momentumScrollActive) return
        if (connectedDevice == null || hidDevice == null) {
            stopScrollOutput()
            return
        }

        val now = SystemClock.uptimeMillis()
        val dtMs = (now - previousScrollTickTime).toFloat().coerceAtLeast(1f)
        previousScrollTickTime = now

        // Velocity-driven integration for BOTH active scroll and momentum.
        // MotionEvent timing (5-30 ms gaps) never reaches the wheel output —
        // the output cadence is locked to the 16 ms tick.
        scrollAccumulator += scrollVelocityPxPerMs * dtMs
        scrollAccumulatorX += scrollVelocityXPxPerMs * dtMs

        if (momentumScrollActive) {
            val decay = SCROLL_MOMENTUM_FRICTION.pow(dtMs / SCROLL_OUTPUT_INTERVAL_MS.toFloat())
            scrollVelocityPxPerMs *= decay
            scrollVelocityXPxPerMs *= decay

            val bothIdle = abs(scrollVelocityPxPerMs) < SCROLL_MOMENTUM_MIN_VELOCITY &&
                abs(scrollVelocityXPxPerMs) < SCROLL_MOMENTUM_MIN_VELOCITY
            if (bothIdle) {
                Log.d(TAG, "Scroll momentum ended naturally")
                momentumScrollActive = false
                scrollAccumulator = 0f; scrollAccumulatorX = 0f
                scrollVelocityPxPerMs = 0f; scrollVelocityXPxPerMs = 0f
                return
            }
        }

        val pixelsPerCount = SCROLL_PIXELS_PER_NOTCH / effectiveWheelMultiplier
        val countsY = (scrollAccumulator / pixelsPerCount).toInt()
        val countsX = (scrollAccumulatorX / pixelsPerCount).toInt()
        if (countsY != 0 || countsX != 0) {
            scrollAccumulator -= countsY * pixelsPerCount
            scrollAccumulatorX -= countsX * pixelsPerCount
            val wheelY = (countsY * SCROLL_DIRECTION).coerceIn(-127, 127)
            val wheelX = (countsX * SCROLL_DIRECTION_H).coerceIn(-127, 127)
            sendMouseReport(0x00, 0, 0, wheelY, wheelX)
        }

        if (scrollOutputActive || momentumScrollActive) {
            handler.postDelayed(scrollTickRunnable, SCROLL_OUTPUT_INTERVAL_MS)
        }
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
            "Bluetooth Trackpad + Keyboard",
            "PhonePad",
            BluetoothHidDevice.SUBCLASS1_COMBO,
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

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                stopScrollOutput()
                scrollAccumulator = 0f; scrollAccumulatorX = 0f
                scrollVelocityPxPerMs = 0f; scrollVelocityXPxPerMs = 0f
                isTwoFingerScrolling = false
                gestureContainedMultiTouch = false
                return handleSingleFingerDown(event)
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                enterMultiTouch()
                if (event.pointerCount == 2) {
                    beginTwoFingerScroll(event)
                }
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (event.pointerCount == 2 && isTwoFingerScrolling) {
                    handleTwoFingerScrollInput(event)
                    return true
                }
                if (event.pointerCount == 1 && !gestureContainedMultiTouch) {
                    return handleSingleFingerMove(event)
                }
                return true
            }
            MotionEvent.ACTION_POINTER_UP -> {
                if (isTwoFingerScrolling) {
                    val duration = SystemClock.uptimeMillis() - twoFingerDownTime
                    if (twoFingerTapEligible && duration <= TWO_FINGER_TAP_DURATION_MS) {
                        Log.d(TAG, "Two-finger tap detected: duration=${duration}ms → right-click")
                        sendRightClick()
                        rightClickFiredInGesture = true
                        scrollVelocityPxPerMs = 0f; scrollVelocityXPxPerMs = 0f
                        scrollAccumulator = 0f; scrollAccumulatorX = 0f
                    } else {
                        Log.d(TAG, "Two-finger tap NOT fired: eligible=$twoFingerTapEligible, duration=${duration}ms (max ${TWO_FINGER_TAP_DURATION_MS})")
                    }
                    endTwoFingerScroll()
                }
                return true
            }
            MotionEvent.ACTION_UP -> {
                if (gestureContainedMultiTouch) {
                    fingerDown = false
                    // Safety: release Ctrl if pinch is somehow still active.
                    if (ctrlHeldForPinch) releaseCtrlForPinch()
                    val timeSincePointerUp = SystemClock.uptimeMillis() - pointerUpTime
                    val wasPinch = twoFingerMode == TwoFingerMode.PINCH
                    val shouldStartMomentum = !rightClickFiredInGesture && !wasPinch &&
                        pointerUpTime > 0 && timeSincePointerUp <= SCROLL_MOMENTUM_RELEASE_GRACE_MS
                    if (shouldStartMomentum) {
                        Log.d(TAG, "Final finger lifted ${timeSincePointerUp}ms after first, starting momentum")
                        startMomentum()
                    } else {
                        Log.d(TAG, "Final finger lifted ${timeSincePointerUp}ms after first (mode=$twoFingerMode, rc=$rightClickFiredInGesture), no momentum")
                        scrollVelocityPxPerMs = 0f; scrollVelocityXPxPerMs = 0f
                        scrollAccumulator = 0f; scrollAccumulatorX = 0f
                    }
                    return true
                }
                return handleSingleFingerUp()
            }
            MotionEvent.ACTION_CANCEL -> {
                stopScrollOutput()
                handler.removeCallbacks(dragTriggerRunnable)
                fingerDown = false
                isTwoFingerScrolling = false
                if (isDragging) {
                    Log.d(TAG, "Drag cancelled")
                    releaseLeftButton()
                    isDragging = false
                }
                tapEligible = false
                dragEligible = false
                gestureContainedMultiTouch = false
                twoFingerTapEligible = false
                rightClickFiredInGesture = false
                scrollAccumulator = 0f; scrollAccumulatorX = 0f
                scrollVelocityPxPerMs = 0f; scrollVelocityXPxPerMs = 0f
                if (ctrlHeldForPinch) releaseCtrlForPinch()
                twoFingerMode = TwoFingerMode.UNDECIDED
                Log.d(TAG, "Touch cancelled")
                return true
            }
        }
        return false
    }

    private fun enterMultiTouch() {
        if (gestureContainedMultiTouch) return
        gestureContainedMultiTouch = true
        tapEligible = false
        dragEligible = false
        handler.removeCallbacks(dragTriggerRunnable)

        if (isDragging) {
            Log.d(TAG, "Drag cancelled: multi-touch detected")
            releaseLeftButton()
            isDragging = false
        }
    }

    private fun beginTwoFingerScroll(event: MotionEvent) {
        val y0 = event.getY(0)
        val y1 = event.getY(1)
        val x0 = event.getX(0)
        val x1 = event.getX(1)
        val cx = (x0 + x1) / 2f
        val cy = (y0 + y1) / 2f
        val fingerDist = sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0))
        previousCentroidY = cy
        previousCentroidX = cx
        initialCentroidY = cy
        initialCentroidX = cx
        initialFingerDistance = fingerDist
        previousFingerDistance = fingerDist
        pinchAccumulator = 0f
        twoFingerMode = TwoFingerMode.UNDECIDED
        scrollAxisLocked = false
        isHorizontalScroll = false
        scrollAccumulator = 0f
        scrollAccumulatorX = 0f
        scrollVelocityPxPerMs = 0f
        scrollVelocityXPxPerMs = 0f
        lastScrollEventTime = event.eventTime
        pointerUpTime = 0L
        isTwoFingerScrolling = true

        // Two-finger tap tracking
        twoFingerDownTime = SystemClock.uptimeMillis()
        twoFingerTapPointerId0 = event.getPointerId(0)
        twoFingerTapPointerId1 = event.getPointerId(1)
        twoFingerDownX0 = x0; twoFingerDownY0 = y0
        twoFingerDownX1 = x1; twoFingerDownY1 = y1
        twoFingerTapEligible = true
        rightClickFiredInGesture = false

        startScrollOutputLoop()
        Log.d(TAG, "Two-finger gesture started (scroll + tap-eligible)")
    }

    private fun endTwoFingerScroll() {
        Log.d(TAG, "Two-finger input ended, mode=$twoFingerMode, velocityY=$scrollVelocityPxPerMs px/ms")
        isTwoFingerScrolling = false
        stopScrollOutputLoop()
        pointerUpTime = SystemClock.uptimeMillis()
        // Always release Ctrl on gesture end so it can't get stuck.
        if (ctrlHeldForPinch) releaseCtrlForPinch()
    }

    private fun handleTwoFingerScrollInput(event: MotionEvent) {
        if (event.pointerCount != 2) return

        // Two-finger tap tracking: if either finger has moved beyond the tap
        // movement threshold since it came down, this is no longer eligible
        // to fire a right-click on lift.
        if (twoFingerTapEligible) {
            val idx0 = event.findPointerIndex(twoFingerTapPointerId0)
            val idx1 = event.findPointerIndex(twoFingerTapPointerId1)
            if (idx0 >= 0) {
                val dx = event.getX(idx0) - twoFingerDownX0
                val dy = event.getY(idx0) - twoFingerDownY0
                if (sqrt(dx * dx + dy * dy) > twoFingerTapMovementThresholdPx) twoFingerTapEligible = false
            }
            if (twoFingerTapEligible && idx1 >= 0) {
                val dx = event.getX(idx1) - twoFingerDownX1
                val dy = event.getY(idx1) - twoFingerDownY1
                if (sqrt(dx * dx + dy * dy) > twoFingerTapMovementThresholdPx) twoFingerTapEligible = false
            }
        }

        val y0 = event.getY(0)
        val y1 = event.getY(1)
        val x0 = event.getX(0)
        val x1 = event.getX(1)
        val currentCentroidY = (y0 + y1) / 2f
        val currentCentroidX = (x0 + x1) / 2f
        val currentFingerDist = sqrt((x1 - x0) * (x1 - x0) + (y1 - y0) * (y1 - y0))

        // Mode decision: whichever metric crosses its threshold first wins.
        // Pinch = change in inter-finger distance (fingers spreading/together).
        // Scroll = centroid movement (fingers translating together).
        if (twoFingerMode == TwoFingerMode.UNDECIDED) {
            val distChange = abs(currentFingerDist - initialFingerDistance)
            val totalMoveX = abs(currentCentroidX - initialCentroidX)
            val totalMoveY = abs(currentCentroidY - initialCentroidY)
            when {
                distChange > pinchDistanceThresholdPx -> {
                    twoFingerMode = TwoFingerMode.PINCH
                    Log.d(TAG, "Two-finger mode: PINCH (distChange=$distChange)")
                    holdCtrlForPinch()
                    previousFingerDistance = currentFingerDist
                    pinchAccumulator = 0f
                }
                totalMoveX > scrollAxisLockThresholdPx || totalMoveY > scrollAxisLockThresholdPx -> {
                    twoFingerMode = TwoFingerMode.SCROLL
                    isHorizontalScroll = totalMoveX > totalMoveY
                    scrollAxisLocked = true
                    Log.d(TAG, "Two-finger mode: SCROLL (${if (isHorizontalScroll) "HORIZONTAL" else "VERTICAL"})")
                }
            }
        }

        if (twoFingerMode == TwoFingerMode.PINCH) {
            handlePinchDelta(currentFingerDist)
            previousCentroidY = currentCentroidY
            previousCentroidX = currentCentroidX
            return
        }

        val deltaY = currentCentroidY - previousCentroidY
        val deltaX = currentCentroidX - previousCentroidX
        previousCentroidY = currentCentroidY
        previousCentroidX = currentCentroidX

        val dtMs = event.eventTime - lastScrollEventTime
        lastScrollEventTime = event.eventTime

        // Only update velocity for the locked axis. The tick loop drives the
        // accumulator from this smoothed velocity, decoupling touch event
        // jitter (5-30 ms delivery gaps) from the wheel output cadence.
        if (twoFingerMode == TwoFingerMode.SCROLL && scrollAxisLocked && dtMs > 0) {
            if (isHorizontalScroll) {
                val instantVelocityX = deltaX / dtMs.toFloat()
                scrollVelocityXPxPerMs = scrollVelocityXPxPerMs * (1f - SCROLL_VELOCITY_SMOOTHING) +
                    instantVelocityX * SCROLL_VELOCITY_SMOOTHING
                scrollVelocityPxPerMs = 0f
            } else {
                val instantVelocityY = deltaY / dtMs.toFloat()
                scrollVelocityPxPerMs = scrollVelocityPxPerMs * (1f - SCROLL_VELOCITY_SMOOTHING) +
                    instantVelocityY * SCROLL_VELOCITY_SMOOTHING
                scrollVelocityXPxPerMs = 0f
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun holdCtrlForPinch() {
        if (ctrlHeldForPinch) return
        ctrlHeldForPinch = true
        sendKeyboardReport(KEY_MOD_LCTRL)
    }

    @SuppressLint("MissingPermission")
    private fun releaseCtrlForPinch() {
        if (!ctrlHeldForPinch) return
        ctrlHeldForPinch = false
        sendKeyboardReport(0x00)
    }

    private fun handlePinchDelta(currentFingerDist: Float) {
        val deltaDist = currentFingerDist - previousFingerDistance
        previousFingerDistance = currentFingerDist
        pinchAccumulator += deltaDist

        val steps = (pinchAccumulator / pinchPixelsPerStep).toInt()
        if (steps != 0) {
            pinchAccumulator -= steps * pinchPixelsPerStep
            val wheel = steps.coerceIn(-127, 127)
            // Ctrl is already held; wheel becomes zoom on Windows.
            sendMouseReport(0x00, 0, 0, wheel, 0)
        }
    }

    @SuppressLint("MissingPermission")
    private fun sendKeyboardReport(modifier: Byte, key1: Byte = 0, key2: Byte = 0,
                                    key3: Byte = 0, key4: Byte = 0, key5: Byte = 0, key6: Byte = 0) {
        val hid = hidDevice ?: return
        val device = connectedDevice ?: return
        val report = byteArrayOf(modifier, 0x00, key1, key2, key3, key4, key5, key6)
        val ok = hid.sendReport(device, KEYBOARD_REPORT_ID, report)
        if (!ok) Log.w(TAG, "sendKeyboardReport failed: modifier=$modifier")
    }

    private fun handleSingleFingerDown(event: MotionEvent): Boolean {
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

    private fun handleSingleFingerMove(event: MotionEvent): Boolean {
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

    private fun handleSingleFingerUp(): Boolean {
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

        val down = byteArrayOf(0x01.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte())
        val up = byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte())

        val downResult = hid.sendReport(device, INPUT_REPORT_ID, down)
        val upResult = hid.sendReport(device, INPUT_REPORT_ID, up)

        if (!downResult || !upResult) {
            Log.w(TAG, "sendLeftClick failed: down=$downResult, up=$upResult")
        }
    }

    @SuppressLint("MissingPermission")
    private fun sendRightClick() {
        val hid = hidDevice ?: return
        val device = connectedDevice ?: return

        val down = byteArrayOf(0x02.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte())
        val up = byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte())

        val downResult = hid.sendReport(device, INPUT_REPORT_ID, down)
        val upResult = hid.sendReport(device, INPUT_REPORT_ID, up)

        if (!downResult || !upResult) {
            Log.w(TAG, "sendRightClick failed: down=$downResult, up=$upResult")
        }
    }

    @SuppressLint("MissingPermission")
    private fun releaseLeftButton() {
        val hid = hidDevice ?: return
        val device = connectedDevice ?: return

        val release = byteArrayOf(0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte(), 0x00.toByte())
        val result = hid.sendReport(device, INPUT_REPORT_ID, release)
        if (!result) {
            Log.w(TAG, "releaseLeftButton failed")
        }
    }

    @SuppressLint("MissingPermission")
    private fun sendMouseReport(buttons: Int, dx: Int, dy: Int, wheel: Int = 0, wheelH: Int = 0) {
        val hid = hidDevice ?: return
        val device = connectedDevice ?: return

        val report = byteArrayOf(
            buttons.toByte(),
            dx.toByte(),
            dy.toByte(),
            wheel.toByte(),
            wheelH.toByte()
        )

        val result = hid.sendReport(device, INPUT_REPORT_ID, report)
        if (!result) {
            Log.w(TAG, "sendReport failed: buttons=$buttons, dx=$dx, dy=$dy, wheel=$wheel, wheelH=$wheelH")
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
                text = if (isConnected) "Move one finger to control cursor\nTap to click\nHold to drag\nTwo fingers to scroll (vertical or horizontal)\nTwo-finger tap to right-click\nPinch in/out to zoom"
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
