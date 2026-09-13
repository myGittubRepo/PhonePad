package com.phonepad.app

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHidDevice
import android.bluetooth.BluetoothHidDeviceAppSdpSettings
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
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

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "PhonePad"
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
            checkHidProfile()
        } else {
            Log.w(TAG, "Bluetooth permissions denied: $results")
            permissionStatus = "Denied"
            hidProfileStatus = "N/A (permission denied)"
        }
    }

    private val hidCallback = object : BluetoothHidDevice.Callback() {
        override fun onAppStatusChanged(pluggedDevice: BluetoothDevice?, registered: Boolean) {
            Log.d(TAG, "onAppStatusChanged: registered=$registered, device=$pluggedDevice")
            if (registered) {
                registrationStatus = "REGISTERED"
                loadBondedDevices()
            } else {
                registrationStatus = "NOT REGISTERED"
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
            connectedDevice = if (state == BluetoothProfile.STATE_CONNECTED) device else null
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
                hidDevice = null
                registrationStatus = "NOT REGISTERED"
                connectionStatus = "DISCONNECTED"
                connectedDevice = null
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val bluetoothManager = getSystemService(BluetoothManager::class.java)
        bluetoothAdapter = bluetoothManager?.adapter

        if (bluetoothAdapter == null) {
            Log.e(TAG, "BluetoothAdapter unavailable — no Bluetooth hardware")
            bluetoothStatus = "Bluetooth unavailable"
            permissionStatus = "N/A"
            hidProfileStatus = "N/A"
        } else {
            Log.d(TAG, "BluetoothAdapter available")
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

    override fun onDestroy() {
        super.onDestroy()
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
                checkHidProfile()
            } else {
                Log.d(TAG, "Requesting Bluetooth permissions")
                permissionLauncher.launch(permissions)
            }
        } else {
            Log.d(TAG, "Android < 12 — no runtime Bluetooth permissions needed")
            permissionStatus = "Granted (pre-Android 12)"
            checkHidProfile()
        }
    }

    private fun checkHidProfile() {
        if (bluetoothAdapter == null) return

        hidProfileStatus = "Checking…"
        val requested = bluetoothAdapter!!.getProfileProxy(
            this,
            profileListener,
            BluetoothProfile.HID_DEVICE
        )
        Log.d(TAG, "getProfileProxy(HID_DEVICE) returned: $requested")

        if (!requested) {
            Log.w(TAG, "getProfileProxy returned false — HID_DEVICE profile UNSUPPORTED")
            hidProfileStatus = "UNSUPPORTED"
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
        if (event.pointerCount != 1) return false

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                previousX = event.x
                previousY = event.y
                Log.d(TAG, "Touch started at (${event.x}, ${event.y})")
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                val dx = event.x - previousX
                val dy = event.y - previousY
                previousX = event.x
                previousY = event.y

                val clampedX = dx.toInt().coerceIn(-127, 127)
                val clampedY = dy.toInt().coerceIn(-127, 127)

                if (clampedX != 0 || clampedY != 0) {
                    sendMouseReport(clampedX, clampedY)
                }
                return true
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                Log.d(TAG, "Touch ended")
                return true
            }
        }
        return false
    }

    @SuppressLint("MissingPermission")
    private fun sendMouseReport(dx: Int, dy: Int) {
        val hid = hidDevice ?: return
        val device = connectedDevice ?: return

        val report = byteArrayOf(
            0x00.toByte(),
            dx.toByte(),
            dy.toByte()
        )

        val result = hid.sendReport(device, 0, report)
        if (!result) {
            Log.w(TAG, "sendReport failed: dx=$dx, dy=$dy")
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
                text = if (isConnected) "Move one finger to control cursor"
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
