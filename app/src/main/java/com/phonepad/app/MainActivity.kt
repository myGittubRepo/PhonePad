package com.phonepad.app

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHidDevice
import android.bluetooth.BluetoothHidDeviceAppSdpSettings
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.res.Configuration
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.media.AudioManager
import android.view.SoundEffectConstants
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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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

import android.net.Uri
import android.os.BatteryManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.provider.Settings as AndroidSettings
import android.view.HapticFeedbackConstants
import android.view.View
import android.view.WindowInsetsController
import androidx.compose.animation.Crossfade
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.BluetoothDisabled
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Gesture
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Adjust
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.East
import androidx.compose.material.icons.outlined.FlashOn
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Mail
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.OpenWith
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.SwapVert
import androidx.compose.material.icons.outlined.TouchApp
import androidx.compose.material.icons.outlined.WebAsset
import androidx.compose.material.icons.outlined.ZoomIn
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.TextButton
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalView
import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.translate
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

enum class AppScreen {
    SPLASH, COMPAT_FAIL, ONBOARDING, PERMISSION, PERMISSION_DENIED, PAIRING_GUIDE, TRACKPAD, KEYBOARD, SPLIT
}

class MainActivity : ComponentActivity() {

    companion object {
        private const val TAG = "PhonePad"
        private const val TAP_DURATION_MS = 180L
        private const val TAP_MOVEMENT_DP = 10f
        private const val DRAG_HOLD_MS = 350L
        private const val PREFS_NAME = "phonepad_prefs"
        private const val PREF_LAST_HOST_ADDRESS = "last_host_address"
        private const val PREF_SENSITIVITY = "_sensitivity"
        private const val PREF_TAP_TO_CLICK = "_tap_to_click"
        private const val PREF_NATURAL_SCROLL = "_natural_scroll"
        private const val PREF_RIPPLE_ENABLED = "_ripple_enabled"
        private const val PREF_HAPTICS_ENABLED = "_haptics_enabled"
        private const val PREF_STATUS_BAR_AUTO_HIDE = "status_bar_auto_hide"
        private const val PREF_TRACKPAD_THEME = "trackpad_theme"
        private const val PREF_UI_THEME = "ui_theme"
        private const val PREF_DEVICE_NICKNAME = "_nickname"
        private const val PREF_SPLIT_RATIO = "_split_ratio"
        private const val PREF_HAS_SEEN_ONBOARDING = "has_seen_onboarding"
        private const val PREF_GESTURE_GUIDE_SECTIONS = "gesture_guide_sections"

        private const val EDGE_ZONE_DP = 20f
        private const val AUTO_RECONNECT_TIMEOUT_MS = 10_000L

        private const val SCROLL_PIXELS_PER_NOTCH = 12f
        private const val SCROLL_DIRECTION = 1
        private const val SCROLL_DIRECTION_H = -1
        private const val SCROLL_OUTPUT_INTERVAL_MS = 8L
        private const val SCROLL_VELOCITY_SMOOTHING = 0.5f
        private const val SCROLL_MOMENTUM_FRICTION = 0.94f
        private const val SCROLL_MOMENTUM_MIN_VELOCITY = 0.05f
        private const val SCROLL_MOMENTUM_RELEASE_GRACE_MS = 150L
        private const val SCROLL_AXIS_LOCK_THRESHOLD_DP = 8f

        // Milestone 4.1 cursor output cadence & smoothing
        private const val CURSOR_OUTPUT_INTERVAL_MS = 8L
        private const val CURSOR_VELOCITY_SMOOTHING = 0.5f

        // Milestone 4.2 palm rejection
        private const val PALM_TOUCH_MAJOR_THRESHOLD_DP = 40f
        private const val PALM_GRACE_PERIOD_MS = 80L
        private const val PALM_GRACE_MOVEMENT_DP = 4f

        // Two-finger tap → right-click
        private const val TWO_FINGER_TAP_DURATION_MS = 300L
        private const val TWO_FINGER_TAP_MOVEMENT_DP = 15f

        // Milestone 2.4 pinch-to-zoom
        private const val PINCH_DISTANCE_THRESHOLD_DP = 20f
        private const val PINCH_PIXELS_PER_STEP_DP = 20f
        private const val KEYBOARD_REPORT_ID: Int = 3
        private const val CONSUMER_REPORT_ID: Int = 4

        // HID keyboard modifiers
        private const val KEY_MOD_LCTRL: Byte = 0x01
        private const val KEY_MOD_LSHIFT: Byte = 0x02
        private const val KEY_MOD_LALT: Byte = 0x04
        private const val KEY_MOD_LGUI: Byte = 0x08

        // HID keycodes we use in Phase 3
        private const val KEY_TAB: Byte = 0x2B
        private const val KEY_D: Byte = 0x07
        private const val KEY_N: Byte = 0x11
        private const val KEY_A: Byte = 0x04
        private const val KEY_ARROW_RIGHT: Byte = 0x4F
        private const val KEY_ARROW_LEFT: Byte = 0x50

        // Consumer Control usage codes (16-bit, little-endian in report)
        private const val CONSUMER_VOLUME_DOWN: Int = 0x00EA
        private const val CONSUMER_VOLUME_UP: Int = 0x00E9
        private const val CONSUMER_MUTE: Int = 0x00E2
        private const val CONSUMER_PLAY_PAUSE: Int = 0x00CD
        private const val CONSUMER_NEXT_TRACK: Int = 0x00B5
        private const val CONSUMER_PREV_TRACK: Int = 0x00B6

        // Milestones 3.2 (3-finger) and 3.3 (4-finger) share the multi-finger
        // gesture pipeline — thresholds tuned once for both.
        private const val MULTI_FINGER_SWIPE_THRESHOLD_DP = 15f
        private const val MULTI_FINGER_TAP_DURATION_MS = 500L
        private const val MULTI_FINGER_TAP_MOVEMENT_DP = 15f

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

    // Milestone 4.4 settings & customization
    private var sensitivityMultiplier by mutableFloatStateOf(1.0f)
    private var tapToClickEnabled by mutableStateOf(true)
    private var naturalScrollEnabled by mutableStateOf(false)
    private var showSettings by mutableStateOf(false)
    private var rippleEnabled by mutableStateOf(false)
    private var hapticsEnabled by mutableStateOf(true)
    private var statusBarAutoHide by mutableStateOf(true)
    private var trackpadTheme by mutableStateOf("dark") // dark, darker, amoled
    private var uiTheme by mutableStateOf("dark") // dark, light

    // Phase 1 UI navigation
    private var currentScreen by mutableStateOf(AppScreen.SPLASH)

    // Keyboard report engine (Milestone 1.1)
    private val keyboardEngine = KeyboardReportSender { modifier, keys ->
        sendKeyboardReport(modifier, keys[0], keys[1], keys[2], keys[3], keys[4], keys[5])
    }
    private var activeMode by mutableStateOf("trackpad") // trackpad, keyboard, split
    private var doubleSpaceForPeriod by mutableStateOf(true)
    private var keySoundEnabled by mutableStateOf(false)
    private var showModePopup by mutableStateOf(false)

    // Phase 2 trackpad surface state
    private var showGestureGuide by mutableStateOf(false)
    private var settingsInitialTab by mutableStateOf(0)

    // Phase 4 device manager
    private var showDeviceManager by mutableStateOf(false)
    private var deviceNicknames by mutableStateOf(mapOf<String, String>())

    // Phase 5 error states
    private var isBluetoothOff by mutableStateOf(false)
    private var showDisconnectSheet by mutableStateOf(false)
    private var disconnectAutoReconnectFailed by mutableStateOf(false)
    private val disconnectTimerRunnable = Runnable {
        if (connectedDevice == null && (currentScreen == AppScreen.TRACKPAD || currentScreen == AppScreen.SPLIT)) {
            disconnectAutoReconnectFailed = true
            showDisconnectSheet = true
        }
    }

    // Milestone 4.2 palm rejection
    private var palmTouchMajorThresholdPx = 0f
    private var palmGraceMovementThresholdPx = 0f
    private var primaryPointerGraceActive = false
    private var primaryPointerGraceStartTime = 0L
    private var primaryPointerConfirmed = false

    // Milestone 4.1 cursor output cadence & smoothing
    private var cursorVelocityX = 0f
    private var cursorVelocityY = 0f
    private var cursorOutputActive = false
    private var previousCursorTickTime = 0L
    private val cursorTickRunnable = Runnable { tickCursorOutput() }

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

    // Milestones 3.2 + 3.3 multi-finger gestures (3-finger and 4-finger)
    private var multiFingerActive = false
    private var multiFingerMaxCount = 0
    private var multiFingerDownTime = 0L
    private var multiFingerInitialCentroidX = 0f
    private var multiFingerInitialCentroidY = 0f
    private var multiFingerCurrentCentroidX = 0f
    private var multiFingerCurrentCentroidY = 0f
    private var multiFingerSwipeThresholdPx = 0f
    private var multiFingerTapMovementThresholdPx = 0f

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

    // Composite HID descriptor: Mouse (0x01) + Keyboard (0x02) + Consumer Control (0x03)
    // Feature Report for Resolution Multiplier uses Report ID 0x04.
    private val mouseDescriptor = byteArrayOf(
        // ===== Mouse Application Collection (Report ID 0x01) =====
        0x05.toByte(), 0x01.toByte(), // USAGE_PAGE (Generic Desktop)
        0x09.toByte(), 0x02.toByte(), // USAGE (Mouse)
        0xA1.toByte(), 0x01.toByte(), // COLLECTION (Application)
        0x09.toByte(), 0x01.toByte(), //   USAGE (Pointer)
        0xA1.toByte(), 0x00.toByte(), //   COLLECTION (Physical)

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

        // Logical Collection: Wheel + Resolution Multiplier
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
        // Feature padding (6 bits)
        0x75.toByte(), 0x06.toByte(), //       REPORT_SIZE (6)
        0x95.toByte(), 0x01.toByte(), //       REPORT_COUNT (1)
        0xB1.toByte(), 0x01.toByte(), //       FEATURE (Cnst,Var,Abs)

        // Wheel (Report ID 1)
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

        // Horizontal wheel (AC Pan)
        0x05.toByte(), 0x0C.toByte(),                   //   USAGE_PAGE (Consumer)
        0x0A.toByte(), 0x38.toByte(), 0x02.toByte(),    //   USAGE (AC Pan)
        0x15.toByte(), 0x81.toByte(),                   //   LOGICAL_MINIMUM (-127)
        0x25.toByte(), 0x7F.toByte(),                   //   LOGICAL_MAXIMUM (127)
        0x75.toByte(), 0x08.toByte(),                   //   REPORT_SIZE (8)
        0x95.toByte(), 0x01.toByte(),                   //   REPORT_COUNT (1)
        0x81.toByte(), 0x06.toByte(),                   //   INPUT (Data,Var,Rel)

        0xC0.toByte(),               //   END_COLLECTION (Physical)
        0xC0.toByte(),               // END_COLLECTION (Application — Mouse)

        // ===== Keyboard Application Collection (Report ID 0x03) =====
        // Standard 6KRO boot protocol: modifier(1) + reserved(1) + keycodes(6) = 8 bytes
        0x05.toByte(), 0x01.toByte(), // USAGE_PAGE (Generic Desktop)
        0x09.toByte(), 0x06.toByte(), // USAGE (Keyboard)
        0xA1.toByte(), 0x01.toByte(), // COLLECTION (Application)
        0x85.toByte(), 0x03.toByte(), //   REPORT_ID (3)

        // Modifier byte (LCtrl LShift LAlt LGui RCtrl RShift RAlt RGui)
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

        // 6 keycodes (6KRO array)
        0x05.toByte(), 0x07.toByte(), //   USAGE_PAGE (Key Codes)
        0x19.toByte(), 0x00.toByte(), //   USAGE_MINIMUM (0)
        0x29.toByte(), 0xFF.toByte(), //   USAGE_MAXIMUM (255)
        0x15.toByte(), 0x00.toByte(), //   LOGICAL_MINIMUM (0)
        0x26.toByte(), 0xFF.toByte(), 0x00.toByte(), // LOGICAL_MAXIMUM (255)
        0x75.toByte(), 0x08.toByte(), //   REPORT_SIZE (8)
        0x95.toByte(), 0x06.toByte(), //   REPORT_COUNT (6)
        0x81.toByte(), 0x00.toByte(), //   INPUT (Data,Ary,Abs)

        0xC0.toByte(),               // END_COLLECTION (Application — Keyboard)

        // ===== Consumer Control Application Collection (Report ID 0x04) =====
        // 16-bit usage code for media keys (volume, play/pause, etc.)
        0x05.toByte(), 0x0C.toByte(), // USAGE_PAGE (Consumer)
        0x09.toByte(), 0x01.toByte(), // USAGE (Consumer Control)
        0xA1.toByte(), 0x01.toByte(), // COLLECTION (Application)
        0x85.toByte(), 0x04.toByte(), //   REPORT_ID (4)

        0x15.toByte(), 0x00.toByte(), //   LOGICAL_MINIMUM (0)
        0x26.toByte(), 0xFF.toByte(), 0x03.toByte(), // LOGICAL_MAXIMUM (0x03FF)
        0x19.toByte(), 0x00.toByte(), //   USAGE_MINIMUM (0)
        0x2A.toByte(), 0xFF.toByte(), 0x03.toByte(), // USAGE_MAXIMUM (0x03FF)
        0x75.toByte(), 0x10.toByte(), //   REPORT_SIZE (16)
        0x95.toByte(), 0x01.toByte(), //   REPORT_COUNT (1)
        0x81.toByte(), 0x00.toByte(), //   INPUT (Data,Ary,Abs)

        0xC0.toByte()                // END_COLLECTION (Application — Consumer)
    )

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        val allGranted = results.values.all { it }
        if (allGranted) {
            Log.d(TAG, "Bluetooth permissions granted")
            permissionStatus = "Granted"
            ensureHidSession()
            val lastHost = prefs.getString(PREF_LAST_HOST_ADDRESS, null)
            currentScreen = if (lastHost != null) AppScreen.TRACKPAD else AppScreen.PAIRING_GUIDE
        } else {
            Log.w(TAG, "Bluetooth permissions denied: $results")
            permissionStatus = "Denied"
            hidProfileStatus = "N/A (permission denied)"
            currentScreen = AppScreen.PERMISSION_DENIED
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
                    handler.removeCallbacks(disconnectTimerRunnable)
                    showDisconnectSheet = false
                    disconnectAutoReconnectFailed = false
                    if (device != null) {
                        saveLastHost(device.address)
                        loadHostSettings(device.address)
                    }
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    cleanupDragState()
                    stopScrollOutput()
                    resetScrollState()
                    resetWheelMultiplier()
                    val wasConnected = connectedDevice != null
                    connectedDevice = null
                    if (wasConnected && (currentScreen == AppScreen.TRACKPAD || currentScreen == AppScreen.SPLIT)) {
                        disconnectAutoReconnectFailed = false
                        autoReconnectAttempted = false
                        handler.postDelayed(disconnectTimerRunnable, AUTO_RECONNECT_TIMEOUT_MS)
                    }
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
                    isBluetoothOff = true
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
                    isBluetoothOff = false
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
        multiFingerSwipeThresholdPx = MULTI_FINGER_SWIPE_THRESHOLD_DP * resources.displayMetrics.density
        multiFingerTapMovementThresholdPx = MULTI_FINGER_TAP_MOVEMENT_DP * resources.displayMetrics.density
        palmTouchMajorThresholdPx = PALM_TOUCH_MAJOR_THRESHOLD_DP * resources.displayMetrics.density
        palmGraceMovementThresholdPx = PALM_GRACE_MOVEMENT_DP * resources.displayMetrics.density
        loadDeviceNicknames()
        statusBarAutoHide = prefs.getBoolean(PREF_STATUS_BAR_AUTO_HIDE, true)
        trackpadTheme = prefs.getString(PREF_TRACKPAD_THEME, "dark") ?: "dark"
        uiTheme = prefs.getString(PREF_UI_THEME, "dark") ?: "dark"

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
            bluetoothStatus = if (bluetoothAdapter!!.isEnabled) "Enabled" else "Bluetooth disabled"
            isBluetoothOff = !bluetoothAdapter!!.isEnabled
        }

        currentScreen = AppScreen.SPLASH
        handler.postDelayed({ routeFromSplash() }, 2800L)

        setContent {
            PhonePadTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    @Suppress("UnusedContentLambdaTargetStateParameter")
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            if (initialState == AppScreen.SPLASH) {
                                (fadeIn(tween(600)) + slideInVertically { -it / 12 }) togetherWith
                                    fadeOut(tween(500))
                            } else if (targetState == AppScreen.TRACKPAD) {
                                (fadeIn(tween(400)) + slideInHorizontally { it / 4 }) togetherWith
                                    (fadeOut(tween(300)) + slideOutHorizontally { -it / 4 })
                            } else {
                                (fadeIn(tween(350)) + slideInHorizontally { it / 6 }) togetherWith
                                    (fadeOut(tween(250)) + slideOutHorizontally { -it / 6 })
                            }
                        },
                        label = "screen"
                    ) { screen ->
                        when (screen) {
                            AppScreen.SPLASH -> SplashScreen(
                                modifier = Modifier.padding(innerPadding)
                            )
                            AppScreen.COMPAT_FAIL -> CompatFailScreen(
                                deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}",
                                modifier = Modifier.padding(innerPadding)
                            )
                            AppScreen.ONBOARDING -> OnboardingScreen(
                                onComplete = {
                                    prefs.edit().putBoolean(PREF_HAS_SEEN_ONBOARDING, true).apply()
                                    if (hasBluetoothPermissions()) {
                                        ensureHidSession()
                                        val host = prefs.getString(PREF_LAST_HOST_ADDRESS, null)
                                        currentScreen = if (host != null) AppScreen.TRACKPAD else AppScreen.PAIRING_GUIDE
                                    } else {
                                        currentScreen = AppScreen.PERMISSION
                                    }
                                },
                                onSkip = {
                                    prefs.edit().putBoolean(PREF_HAS_SEEN_ONBOARDING, true).apply()
                                    if (hasBluetoothPermissions()) {
                                        ensureHidSession()
                                        val host = prefs.getString(PREF_LAST_HOST_ADDRESS, null)
                                        currentScreen = if (host != null) AppScreen.TRACKPAD else AppScreen.PAIRING_GUIDE
                                    } else {
                                        currentScreen = AppScreen.PERMISSION
                                    }
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                            AppScreen.PERMISSION -> PermissionScreen(
                                onContinue = { requestBluetoothPermissions() },
                                modifier = Modifier.padding(innerPadding)
                            )
                            AppScreen.PERMISSION_DENIED -> PermissionDeniedScreen(
                                onOpenSettings = { openAppSettings() },
                                onTryAgain = { requestBluetoothPermissions() },
                                modifier = Modifier.padding(innerPadding)
                            )
                            AppScreen.PAIRING_GUIDE -> PairingGuideScreen(
                                connectionStatus = connectionStatus,
                                isConnected = connectedDevice != null,
                                connectedHostName = connectedDevice?.name,
                                onNavigateToTrackpad = { currentScreen = AppScreen.TRACKPAD },
                                modifier = Modifier.padding(innerPadding)
                            )
                            AppScreen.TRACKPAD, AppScreen.KEYBOARD, AppScreen.SPLIT -> {
                                // Enforce orientation lock on every recomposition
                                SideEffect {
                                    when (currentScreen) {
                                        AppScreen.SPLIT -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
                                        AppScreen.KEYBOARD -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
                                        else -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                                    }
                                }
                                Box(modifier = Modifier.fillMaxSize()) {
                                    // Cross-fade between modes
                                    Crossfade(
                                        targetState = currentScreen,
                                        animationSpec = tween(200),
                                        label = "mode-crossfade"
                                    ) { screen ->
                                        when (screen) {
                                            AppScreen.TRACKPAD -> TrackpadScreen(
                                                connectionStatus = connectionStatus,
                                                bondedDevices = bondedDevices,
                                                isConnected = connectedDevice != null,
                                                connectedHostName = connectedDevice?.let { getDeviceDisplayName(it) },
                                                onDeviceSelected = { device -> connectToDevice(device) },
                                                onTouchEvent = { event -> handleTrackpadTouch(event) },
                                                showSettings = showSettings,
                                                onToggleSettings = { showSettings = !showSettings },
                                                sensitivity = sensitivityMultiplier,
                                                onSensitivityChange = { sensitivityMultiplier = it; saveHostSettings() },
                                                tapToClick = tapToClickEnabled,
                                                onTapToClickChange = { tapToClickEnabled = it; saveHostSettings() },
                                                naturalScroll = naturalScrollEnabled,
                                                onNaturalScrollChange = { naturalScrollEnabled = it; saveHostSettings() },
                                                rippleEnabled = rippleEnabled,
                                                onRippleChange = { rippleEnabled = it; saveHostSettings() },
                                                hapticsEnabled = hapticsEnabled,
                                                onHapticsChange = { hapticsEnabled = it; saveHostSettings() },
                                                statusBarAutoHide = statusBarAutoHide,
                                                onStatusBarAutoHideChange = { statusBarAutoHide = it; saveGlobalSettings() },
                                                trackpadTheme = trackpadTheme,
                                                onTrackpadThemeChange = { trackpadTheme = it; saveGlobalSettings() },
                                                uiTheme = uiTheme,
                                                onUiThemeChange = { uiTheme = it; saveGlobalSettings() },
                                                batteryPercent = getBatteryPercent(),
                                                onToggleGestureGuide = { settingsInitialTab = 1; showSettings = true },
                                                settingsInitialTab = settingsInitialTab,
                                                showDeviceManager = showDeviceManager,
                                                onToggleDeviceManager = { showDeviceManager = !showDeviceManager },
                                                onDismissDeviceManager = { showDeviceManager = false },
                                                deviceNicknames = deviceNicknames,
                                                onRenameDevice = { addr, name -> saveDeviceNickname(addr, name) },
                                                onForgetDevice = { device -> forgetDevice(device) },
                                                onNavigateToPairingGuide = {
                                                    showDeviceManager = false
                                                    showSettings = false
                                                    currentScreen = AppScreen.PAIRING_GUIDE
                                                },
                                                isBluetoothOff = isBluetoothOff,
                                                onTurnOnBluetooth = {
                                                    try {
                                                        startActivity(Intent(AndroidSettings.ACTION_BLUETOOTH_SETTINGS))
                                                    } catch (_: Exception) {}
                                                },
                                                showDisconnectSheet = showDisconnectSheet,
                                                disconnectAutoReconnectFailed = disconnectAutoReconnectFailed,
                                                onDismissDisconnectSheet = { showDisconnectSheet = false },
                                                onRetryConnect = {
                                                    showDisconnectSheet = false
                                                    disconnectAutoReconnectFailed = false
                                                    autoReconnectAttempted = false
                                                    ensureHidSession()
                                                },
                                                appVersion = try { packageManager.getPackageInfo(packageName, 0).versionName ?: "1.0" } catch (_: Exception) { "1.0" },
                                                hasBluetoothPermissions = hasBluetoothPermissions(),
                                                onRequestPermissions = { requestBluetoothPermissions() },
                                                onOpenAppSettings = { openAppSettings() },
                                                modifier = Modifier
                                            )
                                            AppScreen.KEYBOARD -> KeyboardScreen(
                                                keyboardEngine = keyboardEngine,
                                                onSwitchToTrackpad = {
                                                    showModePopup = !showModePopup
                                                },
                                                onConsumerKey = { code -> sendConsumerKeyPress(code) },
                                                onConsumerPress = { code -> sendConsumerReport(code) },
                                                onConsumerRelease = { sendConsumerReport(0) },
                                                doubleSpaceForPeriod = doubleSpaceForPeriod,
                                                keySoundEnabled = keySoundEnabled
                                            )
                                            AppScreen.SPLIT -> SplitScreen(
                                                trackpadContent = { mod ->
                                                    Box(modifier = mod) {
                                                        Box(
                                                            modifier = Modifier
                                                                .fillMaxSize()
                                                                .background(
                                                                    when (trackpadTheme) {
                                                                        "darker" -> Color(0xFF050508)
                                                                        "amoled" -> Color.Black
                                                                        else -> Color(0xFF111118)
                                                                    }
                                                                )
                                                                .then(
                                                                    if (connectedDevice != null) {
                                                                        Modifier.pointerInteropFilter { event ->
                                                                            handleTrackpadTouch(event)
                                                                        }
                                                                    } else Modifier
                                                                )
                                                        ) {
                                                            if (connectedDevice == null) {
                                                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                                    Text("Not connected", color = Color.White.copy(alpha = 0.3f), fontSize = 12.sp)
                                                                }
                                                            }
                                                        }
                                                    }
                                                },
                                                keyboardContent = { mod ->
                                                    CompactKeyboardScreen(
                                                        keyboardEngine = keyboardEngine,
                                                        onConsumerPress = { code -> sendConsumerReport(code) },
                                                        onConsumerRelease = { sendConsumerReport(0) },
                                                        doubleSpaceForPeriod = doubleSpaceForPeriod,
                                                        keySoundEnabled = keySoundEnabled,
                                                        modifier = mod
                                                    )
                                                }
                                            )
                                            else -> {}
                                        }
                                    }

                                    // Mode switcher popup
                                    if (!showSettings) {
                                        if (showModePopup) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .clickable(
                                                        interactionSource = remember { MutableInteractionSource() },
                                                        indication = null
                                                    ) { showModePopup = false }
                                            )
                                        }
                                        when (currentScreen) {
                                            AppScreen.KEYBOARD -> {
                                                // Keyboard: no trigger button (🖱 key is the trigger), popup at bottom-right
                                                ModePopup(
                                                    currentMode = currentScreen,
                                                    isVisible = showModePopup,
                                                    onModeSelected = { mode ->
                                                        showModePopup = false
                                                        if (mode != currentScreen) {
                                                            keyboardEngine.releaseAll()
                                                            currentScreen = mode
                                                        }
                                                    },
                                                    onToggle = { showModePopup = !showModePopup },
                                                    showTrigger = false,
                                                    modifier = Modifier
                                                        .align(Alignment.BottomEnd)
                                                        .windowInsetsPadding(WindowInsets.navigationBars)
                                                        .padding(end = 12.dp, bottom = 12.dp)
                                                )
                                            }
                                            AppScreen.SPLIT -> {
                                                // Split: trigger at top-center, popup drops down
                                                ModePopup(
                                                    currentMode = currentScreen,
                                                    isVisible = showModePopup,
                                                    onModeSelected = { mode ->
                                                        showModePopup = false
                                                        if (mode != currentScreen) {
                                                            keyboardEngine.releaseAll()
                                                            currentScreen = mode
                                                        }
                                                    },
                                                    onToggle = { showModePopup = !showModePopup },
                                                    dropDown = true,
                                                    modifier = Modifier
                                                        .align(Alignment.TopCenter)
                                                        .windowInsetsPadding(WindowInsets.statusBars)
                                                        .padding(top = 8.dp)
                                                )
                                            }
                                            else -> {
                                                // Trackpad: mouse icon trigger at bottom-right, popup above
                                                ModePopup(
                                                    currentMode = currentScreen,
                                                    isVisible = showModePopup,
                                                    onModeSelected = { mode ->
                                                        showModePopup = false
                                                        if (mode != currentScreen) {
                                                            keyboardEngine.releaseAll()
                                                            currentScreen = mode
                                                        }
                                                    },
                                                    onToggle = { showModePopup = !showModePopup },
                                                    modifier = Modifier
                                                        .align(Alignment.BottomEnd)
                                                        .windowInsetsPadding(WindowInsets.navigationBars)
                                                        .padding(end = 12.dp, bottom = 12.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume: checking HID session recovery")

        // If user returned from system Settings and permissions are now granted
        if (currentScreen == AppScreen.PERMISSION_DENIED && hasBluetoothPermissions()) {
            Log.d(TAG, "onResume: permissions granted from Settings, proceeding")
            permissionStatus = "Granted"
            ensureHidSession()
            val lastHost = prefs.getString(PREF_LAST_HOST_ADDRESS, null)
            currentScreen = if (lastHost != null) AppScreen.TRACKPAD else AppScreen.PAIRING_GUIDE
        }

        if (connectedDevice == null) {
            Log.d(TAG, "onResume: no active connection — resetting autoReconnectAttempted")
            autoReconnectAttempted = false
        }
        if (bluetoothAdapter != null && hasBluetoothPermissions()) {
            ensureHidSession()
        }
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        when (currentScreen) {
            AppScreen.SPLIT -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
            AppScreen.KEYBOARD -> requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
            else -> {}
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: cleaning up")
        unregisterBluetoothReceiver()
        handler.removeCallbacks(dragTriggerRunnable)
        handler.removeCallbacks(disconnectTimerRunnable)
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
        multiFingerActive = false
        multiFingerMaxCount = 0
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
            val scrollDir = if (naturalScrollEnabled) -SCROLL_DIRECTION else SCROLL_DIRECTION
            val scrollDirH = if (naturalScrollEnabled) -SCROLL_DIRECTION_H else SCROLL_DIRECTION_H
            val wheelY = (countsY * scrollDir).coerceIn(-127, 127)
            val wheelX = (countsX * scrollDirH).coerceIn(-127, 127)
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

    private fun loadHostSettings(address: String) {
        sensitivityMultiplier = prefs.getFloat(address + PREF_SENSITIVITY, 1.0f)
        tapToClickEnabled = prefs.getBoolean(address + PREF_TAP_TO_CLICK, true)
        naturalScrollEnabled = prefs.getBoolean(address + PREF_NATURAL_SCROLL, false)
        rippleEnabled = prefs.getBoolean(address + PREF_RIPPLE_ENABLED, false)
        hapticsEnabled = prefs.getBoolean(address + PREF_HAPTICS_ENABLED, true)
        statusBarAutoHide = prefs.getBoolean(PREF_STATUS_BAR_AUTO_HIDE, true)
        trackpadTheme = prefs.getString(PREF_TRACKPAD_THEME, "dark") ?: "dark"
        uiTheme = prefs.getString(PREF_UI_THEME, "dark") ?: "dark"
        loadDeviceNicknames()
        Log.d(TAG, "Loaded settings for $address: sensitivity=$sensitivityMultiplier, tapToClick=$tapToClickEnabled, naturalScroll=$naturalScrollEnabled, ripple=$rippleEnabled, haptics=$hapticsEnabled")
    }

    private fun saveHostSettings() {
        val address = connectedDevice?.address ?: return
        prefs.edit()
            .putFloat(address + PREF_SENSITIVITY, sensitivityMultiplier)
            .putBoolean(address + PREF_TAP_TO_CLICK, tapToClickEnabled)
            .putBoolean(address + PREF_NATURAL_SCROLL, naturalScrollEnabled)
            .putBoolean(address + PREF_RIPPLE_ENABLED, rippleEnabled)
            .putBoolean(address + PREF_HAPTICS_ENABLED, hapticsEnabled)
            .apply()
        Log.d(TAG, "Saved settings for $address")
    }

    private fun saveGlobalSettings() {
        prefs.edit()
            .putBoolean(PREF_STATUS_BAR_AUTO_HIDE, statusBarAutoHide)
            .putString(PREF_TRACKPAD_THEME, trackpadTheme)
            .putString(PREF_UI_THEME, uiTheme)
            .apply()
    }

    private fun loadDeviceNicknames() {
        val map = mutableMapOf<String, String>()
        prefs.all.forEach { (key, value) ->
            if (key.endsWith(PREF_DEVICE_NICKNAME) && value is String) {
                val addr = key.removeSuffix(PREF_DEVICE_NICKNAME)
                map[addr] = value
            }
        }
        deviceNicknames = map
    }

    private fun saveDeviceNickname(address: String, name: String) {
        prefs.edit().putString(address + PREF_DEVICE_NICKNAME, name).apply()
        deviceNicknames = deviceNicknames.toMutableMap().apply { put(address, name) }
    }

    private fun removeDeviceNickname(address: String) {
        prefs.edit().remove(address + PREF_DEVICE_NICKNAME).apply()
        deviceNicknames = deviceNicknames.toMutableMap().apply { remove(address) }
    }

    private fun getDeviceDisplayName(device: BluetoothDevice): String {
        return deviceNicknames[device.address] ?: device.name ?: device.address
    }

    @SuppressLint("MissingPermission")
    private fun forgetDevice(device: BluetoothDevice) {
        try {
            val method = device.javaClass.getMethod("removeBond")
            method.invoke(device)
            removeDeviceNickname(device.address)
            prefs.edit()
                .remove(device.address + PREF_SENSITIVITY)
                .remove(device.address + PREF_TAP_TO_CLICK)
                .remove(device.address + PREF_NATURAL_SCROLL)
                .remove(device.address + PREF_RIPPLE_ENABLED)
                .remove(device.address + PREF_HAPTICS_ENABLED)
                .apply()
            val lastHost = prefs.getString(PREF_LAST_HOST_ADDRESS, null)
            if (lastHost == device.address) {
                prefs.edit().remove(PREF_LAST_HOST_ADDRESS).apply()
            }
            refreshBondedDevices()
            Log.d(TAG, "Forgot device: ${device.address}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to forget device: ${e.message}")
        }
    }

    @SuppressLint("MissingPermission")
    private fun refreshBondedDevices() {
        val adapter = bluetoothAdapter ?: return
        bondedDevices.clear()
        bondedDevices.addAll(adapter.bondedDevices.orEmpty())
    }


    private fun getBatteryPercent(): Int {
        val bm = getSystemService(BatteryManager::class.java) ?: return -1
        return bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
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

    private fun routeFromSplash() {
        if (bluetoothAdapter == null) {
            Log.d(TAG, "routeFromSplash: no adapter — COMPAT_FAIL")
            currentScreen = AppScreen.COMPAT_FAIL
            return
        }
        if (!prefs.getBoolean(PREF_HAS_SEEN_ONBOARDING, false)) {
            Log.d(TAG, "routeFromSplash: first launch — ONBOARDING")
            currentScreen = AppScreen.ONBOARDING
            return
        }
        if (hasBluetoothPermissions()) {
            ensureHidSession()
            val lastHost = prefs.getString(PREF_LAST_HOST_ADDRESS, null)
            if (lastHost != null) {
                Log.d(TAG, "routeFromSplash: returning user with paired device — TRACKPAD")
                currentScreen = AppScreen.TRACKPAD
            } else {
                Log.d(TAG, "routeFromSplash: returning user, no paired device — PAIRING_GUIDE")
                currentScreen = AppScreen.PAIRING_GUIDE
            }
        } else {
            Log.d(TAG, "routeFromSplash: returning user, no permissions — PERMISSION")
            currentScreen = AppScreen.PERMISSION
        }
    }

    private fun openAppSettings() {
        val intent = Intent(AndroidSettings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
        }
        startActivity(intent)
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
                stopCursorOutput()
                scrollAccumulator = 0f; scrollAccumulatorX = 0f
                scrollVelocityPxPerMs = 0f; scrollVelocityXPxPerMs = 0f
                isTwoFingerScrolling = false
                gestureContainedMultiTouch = false
                return handleSingleFingerDown(event)
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                enterMultiTouch()
                activateMultiFingerIfNeeded(event)
                if (!multiFingerActive && event.pointerCount == 2) {
                    beginTwoFingerScroll(event)
                }
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                // Fallback: some Android touch drivers skip ACTION_POINTER_DOWN
                // for the 3rd / 4th finger — we still catch it here.
                activateMultiFingerIfNeeded(event)
                if (multiFingerActive) {
                    handleMultiFingerMove(event)
                    return true
                }
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
                if (multiFingerActive) {
                    // Any finger lift during a multi-finger gesture ends it.
                    endMultiFingerGesture()
                    return true
                }
                if (isTwoFingerScrolling) {
                    val duration = SystemClock.uptimeMillis() - twoFingerDownTime
                    if (twoFingerTapEligible && duration <= TWO_FINGER_TAP_DURATION_MS) {
                        Log.d(TAG, "Two-finger tap detected: duration=${duration}ms → right-click")
                        performHaptic(HapticFeedbackConstants.CONFIRM)
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
                if (multiFingerActive) {
                    // Rare: all fingers lifted at once (only ACTION_UP fired).
                    endMultiFingerGesture()
                    gestureContainedMultiTouch = false
                    fingerDown = false
                    return true
                }
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
                // If we were tracking a multi-finger gesture, treat CANCEL as
                // the end of the gesture and fire the classified action based
                // on the movement we saw. Some Compose/OEM touch pipelines
                // send CANCEL instead of ACTION_POINTER_UP for multi-touch.
                if (multiFingerActive) {
                    endMultiFingerGesture()
                }
                stopScrollOutput()
                stopCursorOutput()
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
                multiFingerActive = false
                multiFingerMaxCount = 0
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
        stopCursorOutput()

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
        performHaptic(HapticFeedbackConstants.CLOCK_TICK)
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

    @SuppressLint("MissingPermission")
    private fun sendConsumerReport(usageCode: Int) {
        val hid = hidDevice ?: return
        val device = connectedDevice ?: return
        // 16-bit usage code, little-endian
        val report = byteArrayOf(
            (usageCode and 0xFF).toByte(),
            ((usageCode shr 8) and 0xFF).toByte()
        )
        val ok = hid.sendReport(device, CONSUMER_REPORT_ID, report)
        if (!ok) Log.w(TAG, "sendConsumerReport failed: usage=0x${usageCode.toString(16)}")
    }

    @SuppressLint("MissingPermission")
    private fun sendConsumerKeyPress(usageCode: Int) {
        sendConsumerReport(usageCode)  // press
        sendConsumerReport(0x0000)     // release
    }

    /**
     * Send a modifier + key press using the strict HID sequence Windows
     * expects: modifier alone → modifier + key → modifier alone → all released.
     * Alt+Tab in particular needs to see Alt held before Tab arrives to fire
     * the app-switcher.
     */
    @SuppressLint("MissingPermission")
    private fun sendKeyPress(modifier: Byte, key: Byte) {
        sendKeyboardReport(modifier)             // press modifier alone
        sendKeyboardReport(modifier, key)        // press key with modifier held
        sendKeyboardReport(modifier)             // release key, modifier still held
        sendKeyboardReport(0x00)                 // release modifier
    }

    @SuppressLint("MissingPermission")
    private fun sendMiddleClick() {
        val hid = hidDevice ?: return
        val device = connectedDevice ?: return
        val down = byteArrayOf(0x04.toByte(), 0x00, 0x00, 0x00, 0x00) // button 3 (middle) = bit 2
        val up = byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00)
        hid.sendReport(device, INPUT_REPORT_ID, down)
        hid.sendReport(device, INPUT_REPORT_ID, up)
    }

    private fun activateMultiFingerIfNeeded(event: MotionEvent) {
        if (event.pointerCount >= 3) {
            if (!multiFingerActive) {
                beginMultiFingerGesture(event)
            }
            // Always keep multiFingerMaxCount at the highest pointerCount
            // observed during the gesture. This is what lets us distinguish
            // 3-finger vs 4-finger when classification runs on end.
            if (event.pointerCount > multiFingerMaxCount) {
                multiFingerMaxCount = event.pointerCount
            }
        }
    }

    private fun computeCentroid(event: MotionEvent): Pair<Float, Float> {
        val n = event.pointerCount.coerceAtLeast(1)
        var sumX = 0f
        var sumY = 0f
        for (i in 0 until n) {
            sumX += event.getX(i)
            sumY += event.getY(i)
        }
        return Pair(sumX / n, sumY / n)
    }

    /**
     * Begin a multi-finger (3 or 4) gesture. Cancels any two-finger tracking
     * state so a briefly-multi-finger touch can't accidentally fire scroll,
     * pinch or the two-finger tap right-click.
     */
    private fun beginMultiFingerGesture(event: MotionEvent) {
        stopScrollOutput()
        if (ctrlHeldForPinch) releaseCtrlForPinch()
        isTwoFingerScrolling = false
        twoFingerMode = TwoFingerMode.UNDECIDED
        twoFingerTapEligible = false
        scrollAccumulator = 0f; scrollAccumulatorX = 0f
        scrollVelocityPxPerMs = 0f; scrollVelocityXPxPerMs = 0f

        multiFingerActive = true
        multiFingerMaxCount = event.pointerCount
        multiFingerDownTime = SystemClock.uptimeMillis()
        val (cx, cy) = computeCentroid(event)
        multiFingerInitialCentroidX = cx
        multiFingerInitialCentroidY = cy
        multiFingerCurrentCentroidX = cx
        multiFingerCurrentCentroidY = cy
        Log.d(TAG, "Multi-finger gesture started at ($cx, $cy), n=${event.pointerCount}")
    }

    private fun handleMultiFingerMove(event: MotionEvent) {
        // Also promote the max count if a 4th (or higher) finger arrives
        // via MOVE rather than POINTER_DOWN.
        if (event.pointerCount > multiFingerMaxCount) {
            multiFingerMaxCount = event.pointerCount
        }
        val (cx, cy) = computeCentroid(event)
        multiFingerCurrentCentroidX = cx
        multiFingerCurrentCentroidY = cy
    }

    /**
     * Called when the multi-finger gesture ends. Classifies as tap or
     * directional swipe and dispatches to the 3-finger or 4-finger action
     * set based on the maximum pointer count observed during the gesture.
     */
    private fun endMultiFingerGesture() {
        val dx = multiFingerCurrentCentroidX - multiFingerInitialCentroidX
        val dy = multiFingerCurrentCentroidY - multiFingerInitialCentroidY
        val absDx = abs(dx)
        val absDy = abs(dy)
        val duration = SystemClock.uptimeMillis() - multiFingerDownTime
        val dxI = dx.toInt(); val dyI = dy.toInt()
        val fourFinger = multiFingerMaxCount >= 4

        val kind: String = when {
            duration <= MULTI_FINGER_TAP_DURATION_MS &&
                absDx <= multiFingerTapMovementThresholdPx &&
                absDy <= multiFingerTapMovementThresholdPx -> "TAP"
            absDy > absDx && absDy > multiFingerSwipeThresholdPx ->
                if (dy < 0) "UP" else "DOWN"
            absDx > absDy && absDx > multiFingerSwipeThresholdPx ->
                if (dx < 0) "LEFT" else "RIGHT"
            else -> "NONE"
        }

        val summary: String
        when {
            kind == "TAP" && fourFinger -> {
                summary = "4f TAP → Notification Center (Win+N)"
                sendKeyPress(KEY_MOD_LGUI, KEY_N)
            }
            kind == "TAP" -> {
                summary = "3f TAP → middle click (dur=${duration}ms)"
                sendMiddleClick()
            }
            kind == "UP" -> {
                summary = "${if (fourFinger) "4f" else "3f"} UP → Task View (dy=$dyI)"
                sendKeyPress(KEY_MOD_LGUI, KEY_TAB)
            }
            kind == "DOWN" -> {
                summary = "${if (fourFinger) "4f" else "3f"} DOWN → Show Desktop (dy=$dyI)"
                sendKeyPress(KEY_MOD_LGUI, KEY_D)
            }
            kind == "LEFT" && fourFinger -> {
                summary = "4f LEFT → prev virtual desktop (Ctrl+Win+Left, dx=$dxI)"
                sendKeyPress((KEY_MOD_LCTRL.toInt() or KEY_MOD_LGUI.toInt()).toByte(), KEY_ARROW_LEFT)
            }
            kind == "LEFT" -> {
                summary = "3f LEFT → prev app (Alt+Shift+Tab, dx=$dxI)"
                sendKeyPress((KEY_MOD_LALT.toInt() or KEY_MOD_LSHIFT.toInt()).toByte(), KEY_TAB)
            }
            kind == "RIGHT" && fourFinger -> {
                summary = "4f RIGHT → next virtual desktop (Ctrl+Win+Right, dx=$dxI)"
                sendKeyPress((KEY_MOD_LCTRL.toInt() or KEY_MOD_LGUI.toInt()).toByte(), KEY_ARROW_RIGHT)
            }
            kind == "RIGHT" -> {
                summary = "3f RIGHT → next app (Alt+Tab, dx=$dxI)"
                sendKeyPress(KEY_MOD_LALT, KEY_TAB)
            }
            else -> {
                summary = "no action (n=$multiFingerMaxCount dx=$dxI dy=$dyI dur=${duration}ms)"
            }
        }
        if (kind != "NONE") {
            performHaptic(HapticFeedbackConstants.LONG_PRESS)
        }
        Log.d(TAG, "Multi-finger: $summary")
        multiFingerActive = false
        multiFingerMaxCount = 0
    }

    private fun handleSingleFingerDown(event: MotionEvent): Boolean {
        previousX = event.x
        previousY = event.y
        previousEventTime = event.eventTime
        touchDownTime = SystemClock.uptimeMillis()
        touchDownX = event.x
        touchDownY = event.y

        if (isPalmContact(event, 0)) {
            Log.d(TAG, "Palm rejected on DOWN (touchMajor=${event.getTouchMajor(0)})")
            tapEligible = false
            dragEligible = false
            fingerDown = false
            primaryPointerConfirmed = false
            primaryPointerGraceActive = false
            return true
        }

        primaryPointerConfirmed = false
        primaryPointerGraceActive = true
        primaryPointerGraceStartTime = SystemClock.uptimeMillis()

        tapEligible = true
        dragEligible = true
        isDragging = false
        fingerDown = true
        handler.postDelayed(dragTriggerRunnable, DRAG_HOLD_MS)
        Log.d(TAG, "Touch started at (${event.x}, ${event.y})")
        return true
    }

    private fun isPalmContact(event: MotionEvent, pointerIndex: Int): Boolean {
        val major = event.getTouchMajor(pointerIndex)
        return major > palmTouchMajorThresholdPx && palmTouchMajorThresholdPx > 0f
    }

    private fun handleSingleFingerMove(event: MotionEvent): Boolean {
        if (isPalmContact(event, 0)) {
            stopCursorOutput()
            return true
        }

        val dx = event.x - previousX
        val dy = event.y - previousY
        val dtMs = event.eventTime - previousEventTime
        previousX = event.x
        previousY = event.y
        previousEventTime = event.eventTime

        if (primaryPointerGraceActive && !primaryPointerConfirmed) {
            val distX = event.x - touchDownX
            val distY = event.y - touchDownY
            val dist = sqrt(distX * distX + distY * distY)
            val elapsed = SystemClock.uptimeMillis() - primaryPointerGraceStartTime

            if (dist >= palmGraceMovementThresholdPx) {
                primaryPointerConfirmed = true
                primaryPointerGraceActive = false
            } else if (elapsed >= PALM_GRACE_PERIOD_MS) {
                Log.d(TAG, "Palm rejected: no movement within grace period")
                primaryPointerGraceActive = false
                tapEligible = false
                dragEligible = false
                handler.removeCallbacks(dragTriggerRunnable)
                return true
            } else {
                return true
            }
        }

        if (!primaryPointerConfirmed && !isDragging) return true

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

        val alpha = CURSOR_VELOCITY_SMOOTHING
        cursorVelocityX = alpha * accX + (1 - alpha) * cursorVelocityX
        cursorVelocityY = alpha * accY + (1 - alpha) * cursorVelocityY

        startCursorOutput()
        return true
    }

    private fun startCursorOutput() {
        if (cursorOutputActive) return
        cursorOutputActive = true
        previousCursorTickTime = SystemClock.uptimeMillis()
        handler.post(cursorTickRunnable)
    }

    private fun stopCursorOutput() {
        cursorOutputActive = false
        handler.removeCallbacks(cursorTickRunnable)
        cursorVelocityX = 0f
        cursorVelocityY = 0f
    }

    private fun tickCursorOutput() {
        if (!cursorOutputActive) return

        val now = SystemClock.uptimeMillis()
        val dt = (now - previousCursorTickTime).toFloat()
        previousCursorTickTime = now

        val outX = (cursorVelocityX * dt / CURSOR_OUTPUT_INTERVAL_MS).toInt().coerceIn(-127, 127)
        val outY = (cursorVelocityY * dt / CURSOR_OUTPUT_INTERVAL_MS).toInt().coerceIn(-127, 127)

        if (outX != 0 || outY != 0) {
            if (isDragging) {
                sendMouseReport(0x01, outX, outY)
            } else {
                sendMouseReport(0x00, outX, outY)
            }
        }

        cursorVelocityX *= 0.6f
        cursorVelocityY *= 0.6f

        if (abs(cursorVelocityX) < 0.1f && abs(cursorVelocityY) < 0.1f) {
            stopCursorOutput()
            return
        }

        handler.postDelayed(cursorTickRunnable, CURSOR_OUTPUT_INTERVAL_MS)
    }

    private fun handleSingleFingerUp(): Boolean {
        handler.removeCallbacks(dragTriggerRunnable)
        stopCursorOutput()
        fingerDown = false

        if (isDragging) {
            Log.d(TAG, "Drag ended")
            releaseLeftButton()
            isDragging = false
        } else {
            val duration = SystemClock.uptimeMillis() - touchDownTime
            when {
                !tapToClickEnabled -> {
                    Log.d(TAG, "Tap suppressed: tap-to-click disabled")
                }
                !tapEligible -> {
                    Log.d(TAG, "Tap suppressed: movement threshold exceeded during gesture")
                }
                duration > TAP_DURATION_MS -> {
                    Log.d(TAG, "Tap suppressed: duration ${duration}ms > ${TAP_DURATION_MS}ms")
                }
                else -> {
                    Log.d(TAG, "Tap detected: duration=${duration}ms")
                    performHaptic(HapticFeedbackConstants.CONFIRM)
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
            speed <= 1.0f -> 0.7f
            speed <= 4.0f -> 0.7f + (speed - 1.0f) * (1.3f - 0.7f) / (4.0f - 1.0f)
            else -> 1.9f
        }

        val finalGain = gain * sensitivityMultiplier
        val outX = (dx * finalGain).toInt().coerceIn(-127, 127)
        val outY = (dy * finalGain).toInt().coerceIn(-127, 127)
        return Pair(outX, outY)
    }

    @SuppressLint("MissingPermission")
    private fun performHaptic(type: Int) {
        if (!hapticsEnabled) return
        try {
            val vibrator = getSystemService(Vibrator::class.java) ?: return
            when (type) {
                HapticFeedbackConstants.CONFIRM -> {
                    vibrator.vibrate(VibrationEffect.createOneShot(15, VibrationEffect.DEFAULT_AMPLITUDE))
                }
                HapticFeedbackConstants.CLOCK_TICK -> {
                    vibrator.vibrate(VibrationEffect.createOneShot(8, 80))
                }
                HapticFeedbackConstants.LONG_PRESS -> {
                    vibrator.vibrate(VibrationEffect.createOneShot(30, VibrationEffect.DEFAULT_AMPLITUDE))
                }
            }
        } catch (_: Exception) {}
    }

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

// ──────────────────────────────────────────────────────────────────────────────
// Keyboard Report Engine (Milestone 1.1)
// ──────────────────────────────────────────────────────────────────────────────

class KeyboardReportSender(
    private val sendReport: (modifier: Byte, keys: ByteArray) -> Unit
) {
    private var modifierBitmap: Byte = 0
    private val heldKeys = mutableListOf<Byte>()
    private val handler = Handler(Looper.getMainLooper())
    private var repeatKey: Byte = 0
    private var repeatRunnable: Runnable? = null

    companion object {
        private const val REPEAT_DELAY_MS = 500L
        private const val REPEAT_INTERVAL_MS = 33L // ~30 reports/sec
        private const val ROLLOVER_ERROR: Byte = 0x01

        // Modifier bit positions in the HID modifier bitmap
        const val MOD_LCTRL: Byte = 0x01
        const val MOD_LSHIFT: Byte = 0x02
        const val MOD_LALT: Byte = 0x04
        const val MOD_LGUI: Byte = 0x08
        const val MOD_RCTRL: Byte = 0x10
        const val MOD_RSHIFT: Byte = 0x20
        const val MOD_RALT: Byte = 0x40
        @Suppress("unused")
        val MOD_RGUI: Byte = 0x80.toByte()

        // HID Usage IDs (page 0x07) for standard keys
        const val KEY_A: Byte = 0x04
        const val KEY_B: Byte = 0x05
        const val KEY_C: Byte = 0x06
        const val KEY_D: Byte = 0x07
        const val KEY_E: Byte = 0x08
        const val KEY_F: Byte = 0x09
        const val KEY_G: Byte = 0x0A
        const val KEY_H: Byte = 0x0B
        const val KEY_I: Byte = 0x0C
        const val KEY_J: Byte = 0x0D
        const val KEY_K: Byte = 0x0E
        const val KEY_L: Byte = 0x0F
        const val KEY_M: Byte = 0x10
        const val KEY_N: Byte = 0x11
        const val KEY_O: Byte = 0x12
        const val KEY_P: Byte = 0x13
        const val KEY_Q: Byte = 0x14
        const val KEY_R: Byte = 0x15
        const val KEY_S: Byte = 0x16
        const val KEY_T: Byte = 0x17
        const val KEY_U: Byte = 0x18
        const val KEY_V: Byte = 0x19
        const val KEY_W: Byte = 0x1A
        const val KEY_X: Byte = 0x1B
        const val KEY_Y: Byte = 0x1C
        const val KEY_Z: Byte = 0x1D
        const val KEY_1: Byte = 0x1E
        const val KEY_2: Byte = 0x1F
        const val KEY_3: Byte = 0x20
        const val KEY_4: Byte = 0x21
        const val KEY_5: Byte = 0x22
        const val KEY_6: Byte = 0x23
        const val KEY_7: Byte = 0x24
        const val KEY_8: Byte = 0x25
        const val KEY_9: Byte = 0x26
        const val KEY_0: Byte = 0x27
        const val KEY_ENTER: Byte = 0x28
        const val KEY_ESCAPE: Byte = 0x29
        const val KEY_BACKSPACE: Byte = 0x2A
        const val KEY_TAB: Byte = 0x2B
        const val KEY_SPACE: Byte = 0x2C
        const val KEY_MINUS: Byte = 0x2D
        const val KEY_EQUALS: Byte = 0x2E
        const val KEY_LBRACKET: Byte = 0x2F
        const val KEY_RBRACKET: Byte = 0x30
        const val KEY_BACKSLASH: Byte = 0x31
        const val KEY_SEMICOLON: Byte = 0x33
        const val KEY_APOSTROPHE: Byte = 0x34
        const val KEY_GRAVE: Byte = 0x35
        const val KEY_COMMA: Byte = 0x36
        const val KEY_PERIOD: Byte = 0x37
        const val KEY_SLASH: Byte = 0x38
        const val KEY_CAPS_LOCK: Byte = 0x39
        const val KEY_F1: Byte = 0x3A
        const val KEY_F2: Byte = 0x3B
        const val KEY_F3: Byte = 0x3C
        const val KEY_F4: Byte = 0x3D
        const val KEY_F5: Byte = 0x3E
        const val KEY_F6: Byte = 0x3F
        const val KEY_F7: Byte = 0x40
        const val KEY_F8: Byte = 0x41
        const val KEY_F9: Byte = 0x42
        const val KEY_F10: Byte = 0x43
        const val KEY_F11: Byte = 0x44
        const val KEY_F12: Byte = 0x45
        const val KEY_INSERT: Byte = 0x49
        const val KEY_HOME: Byte = 0x4A
        const val KEY_PAGE_UP: Byte = 0x4B
        const val KEY_DELETE: Byte = 0x4C
        const val KEY_END: Byte = 0x4D
        const val KEY_PAGE_DOWN: Byte = 0x4E
        const val KEY_ARROW_RIGHT: Byte = 0x4F
        const val KEY_ARROW_LEFT: Byte = 0x50
        const val KEY_ARROW_DOWN: Byte = 0x51
        const val KEY_ARROW_UP: Byte = 0x52
    }

    fun pressModifier(mod: Byte) {
        modifierBitmap = (modifierBitmap.toInt() or mod.toInt()).toByte()
        flushReport()
    }

    fun releaseModifier(mod: Byte) {
        modifierBitmap = (modifierBitmap.toInt() and mod.toInt().inv()).toByte()
        flushReport()
    }

    fun pressKey(hidUsage: Byte) {
        if (hidUsage.toInt() == 0) return
        if (heldKeys.contains(hidUsage)) return

        heldKeys.add(hidUsage)
        flushReport()
        startRepeat(hidUsage)
    }

    fun releaseKey(hidUsage: Byte) {
        heldKeys.remove(hidUsage)
        stopRepeat(hidUsage)
        flushReport()
    }

    fun releaseAll() {
        modifierBitmap = 0
        heldKeys.clear()
        stopAllRepeats()
        flushReport()
    }

    fun isModifierHeld(mod: Byte): Boolean =
        (modifierBitmap.toInt() and mod.toInt()) != 0

    private fun flushReport() {
        val keys = ByteArray(6)
        if (heldKeys.size > 6) {
            // 6KRO rollover error — fill all slots with 0x01
            for (i in 0..5) keys[i] = ROLLOVER_ERROR
        } else {
            for (i in heldKeys.indices) {
                keys[i] = heldKeys[i]
            }
        }
        sendReport(modifierBitmap, keys)
    }

    private fun startRepeat(hidUsage: Byte) {
        stopAllRepeats()
        repeatKey = hidUsage
        val runnable = object : Runnable {
            override fun run() {
                if (heldKeys.contains(repeatKey)) {
                    flushReport()
                    handler.postDelayed(this, REPEAT_INTERVAL_MS)
                }
            }
        }
        repeatRunnable = runnable
        handler.postDelayed(runnable, REPEAT_DELAY_MS)
    }

    private fun stopRepeat(hidUsage: Byte) {
        if (repeatKey == hidUsage) {
            stopAllRepeats()
        }
    }

    private fun stopAllRepeats() {
        repeatRunnable?.let { handler.removeCallbacks(it) }
        repeatRunnable = null
        repeatKey = 0
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Screen composables
// ──────────────────────────────────────────────────────────────────────────────

@Composable
fun SplashScreen(modifier: Modifier = Modifier) {
    val trackpadProgress = remember { Animatable(0f) }
    val cursorProgress = remember { Animatable(0f) }
    val textAlpha = remember { Animatable(0f) }
    val textSlide = remember { Animatable(20f) }
    val glowAlpha = remember { Animatable(0f) }
    val particleProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        launch { trackpadProgress.animateTo(1f, tween(1000, easing = EaseOutCubic)) }
        delay(400)
        launch { cursorProgress.animateTo(1f, tween(700, easing = EaseOutBack)) }
        delay(300)
        launch { glowAlpha.animateTo(0.6f, tween(600)) }
        launch { particleProgress.animateTo(1f, tween(1200, easing = EaseOutCubic)) }
        delay(200)
        launch { textAlpha.animateTo(1f, tween(500)) }
        launch { textSlide.animateTo(0f, tween(500, easing = EaseOutCubic)) }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "splashGlow")
    val shimmer by infiniteTransition.animateFloat(
        initialValue = -0.3f, targetValue = 1.3f,
        animationSpec = infiniteRepeatable(tween(2000, easing = LinearEasing)),
        label = "shimmer"
    )
    val glowPulse by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 0.8f,
        animationSpec = infiniteRepeatable(tween(1500), RepeatMode.Reverse),
        label = "glowPulse"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1A1B4B)),
        contentAlignment = Alignment.Center
    ) {
        // Ambient glow circles
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cx = size.width / 2f
            val cy = size.height / 2f
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFF4B4FCF).copy(alpha = glowPulse * 0.15f), Color.Transparent),
                    center = Offset(cx, cy - 40f),
                    radius = size.minDimension * 0.6f
                ),
                radius = size.minDimension * 0.6f,
                center = Offset(cx, cy - 40f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0xFF0D9488).copy(alpha = glowPulse * 0.08f), Color.Transparent),
                    center = Offset(cx + 80f, cy + 60f),
                    radius = size.minDimension * 0.4f
                ),
                radius = size.minDimension * 0.4f,
                center = Offset(cx + 80f, cy + 60f)
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Animated trackpad + cursor icon
            Canvas(modifier = Modifier.size(140.dp)) {
                val w = size.width
                val h = size.height
                val prog = trackpadProgress.value
                val cProg = cursorProgress.value

                // Trackpad body outline — draws on progressively
                val trackpadPath = Path().apply {
                    val l = w * 0.18f; val t = h * 0.1f
                    val r = w * 0.82f; val b = h * 0.78f
                    val cr = w * 0.08f
                    moveTo(l + cr, t)
                    lineTo(r - cr, t)
                    cubicTo(r, t, r, t, r, t + cr)
                    lineTo(r, b - cr)
                    cubicTo(r, b, r, b, r - cr, b)
                    lineTo(l + cr, b)
                    cubicTo(l, b, l, b, l, b - cr)
                    lineTo(l, t + cr)
                    cubicTo(l, t, l, t, l + cr, t)
                    close()
                }
                val pathMeasure = PathMeasure()
                pathMeasure.setPath(trackpadPath, true)
                val totalLength = pathMeasure.length
                val drawnPath = Path()
                pathMeasure.getSegment(0f, totalLength * prog, drawnPath)

                // Glow behind trackpad
                if (glowAlpha.value > 0f) {
                    drawRoundRect(
                        brush = Brush.radialGradient(
                            listOf(
                                Color(0xFF7B7FD4).copy(alpha = glowAlpha.value * 0.3f),
                                Color.Transparent
                            ),
                            center = Offset(w / 2f, h * 0.44f),
                            radius = w * 0.5f
                        ),
                        topLeft = Offset(w * 0.08f, h * 0.02f),
                        size = Size(w * 0.84f, h * 0.84f),
                        cornerRadius = CornerRadius(w * 0.08f)
                    )
                }

                // Trackpad outline
                drawPath(
                    drawnPath,
                    color = Color.White,
                    style = Stroke(width = 3f, cap = StrokeCap.Round)
                )

                // Fill when fully drawn
                if (prog > 0.95f) {
                    val fillAlpha = ((prog - 0.95f) / 0.05f).coerceIn(0f, 1f) * 0.08f
                    drawPath(trackpadPath, color = Color.White.copy(alpha = fillAlpha))
                }

                // Inner trackpad surface
                if (prog > 0.5f) {
                    val innerAlpha = ((prog - 0.5f) / 0.5f).coerceIn(0f, 1f) * 0.12f
                    drawRoundRect(
                        color = Color(0xFF7B7FD4).copy(alpha = innerAlpha),
                        topLeft = Offset(w * 0.22f, h * 0.16f),
                        size = Size(w * 0.56f, h * 0.52f),
                        cornerRadius = CornerRadius(w * 0.04f)
                    )
                }

                // Divider line
                if (prog > 0.7f) {
                    val divAlpha = ((prog - 0.7f) / 0.3f).coerceIn(0f, 1f)
                    drawLine(
                        color = Color.White.copy(alpha = 0.3f * divAlpha),
                        start = Offset(w * 0.25f, h * 0.66f),
                        end = Offset(w * 0.75f, h * 0.66f),
                        strokeWidth = 1.5f
                    )
                }

                // Cursor arrow — scales in with bounce
                if (cProg > 0f) {
                    val cx = w * 0.52f
                    val cy = h * 0.28f
                    val cursorScale = cProg
                    translate(
                        left = cx - cx * cursorScale + cx,
                        top = cy - cy * cursorScale + cy
                    ) {
                        val arrowPath = Path().apply {
                            moveTo(cx - w * 0.02f, cy)
                            lineTo(cx - w * 0.02f, cy + h * 0.22f)
                            lineTo(cx + w * 0.04f, cy + h * 0.16f)
                            lineTo(cx + w * 0.09f, cy + h * 0.26f)
                            lineTo(cx + w * 0.13f, cy + h * 0.24f)
                            lineTo(cx + w * 0.08f, cy + h * 0.14f)
                            lineTo(cx + w * 0.14f, cy + h * 0.12f)
                            close()
                        }
                        drawPath(arrowPath, color = Color(0xFF2DD4BF).copy(alpha = cProg))
                    }
                }

                // Sparkle particles
                if (particleProgress.value > 0f) {
                    val pProg = particleProgress.value
                    val sparkles = listOf(
                        Offset(w * 0.15f, h * 0.2f), Offset(w * 0.85f, h * 0.15f),
                        Offset(w * 0.9f, h * 0.6f), Offset(w * 0.1f, h * 0.7f),
                        Offset(w * 0.75f, h * 0.85f), Offset(w * 0.25f, h * 0.88f)
                    )
                    sparkles.forEachIndexed { i, pos ->
                        val delay = i * 0.12f
                        val localProg = ((pProg - delay) / (1f - delay)).coerceIn(0f, 1f)
                        if (localProg > 0f) {
                            val sparkAlpha = if (localProg < 0.5f) localProg * 2f else (1f - localProg) * 2f
                            val sparkSize = 2f + localProg * 3f
                            drawCircle(
                                color = Color(0xFF7B7FD4).copy(alpha = sparkAlpha * 0.8f),
                                radius = sparkSize,
                                center = pos
                            )
                        }
                    }
                }

                // Shimmer line across trackpad
                if (prog > 0.9f) {
                    val shimmerX = w * shimmer
                    drawLine(
                        brush = Brush.horizontalGradient(
                            listOf(Color.Transparent, Color.White.copy(alpha = 0.15f), Color.Transparent),
                            startX = shimmerX - w * 0.15f,
                            endX = shimmerX + w * 0.15f
                        ),
                        start = Offset(shimmerX, h * 0.12f),
                        end = Offset(shimmerX, h * 0.76f),
                        strokeWidth = w * 0.06f
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // App name with slide-up
            Text(
                text = "PhonePad",
                color = Color.White.copy(alpha = textAlpha.value),
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                modifier = Modifier.offset(y = textSlide.value.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "bluetooth trackpad",
                color = Color(0xFF2DD4BF).copy(alpha = textAlpha.value * 0.7f),
                fontSize = 13.sp,
                letterSpacing = 3.sp,
                modifier = Modifier.offset(y = textSlide.value.dp)
            )
        }
    }
}

@Composable
fun CompatFailScreen(deviceModel: String, modifier: Modifier = Modifier) {
    var showWhy by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.BluetoothDisabled,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.error,
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "This phone can't run PhonePad",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = deviceModel,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        TextButton(onClick = { showWhy = !showWhy }) {
            Text(text = if (showWhy) "Hide details" else "Why?")
            Icon(
                imageVector = if (showWhy) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
        AnimatedVisibility(visible = showWhy) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = "PhonePad requires:",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "- Bluetooth hardware on your phone", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "- Bluetooth HID Device profile support", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "- Android 9 (Pie) or newer", fontSize = 14.sp)
            }
        }
    }
}

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    onSkip: () -> Unit,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = { 4 })
    val scope = rememberCoroutineScope()

    Column(modifier = modifier.fillMaxSize()) {
        // Skip button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TextButton(onClick = onSkip) {
                Text("Skip", fontSize = 14.sp)
            }
        }

        // Pager
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { page ->
            when (page) {
                0 -> OnboardingPage(
                    icon = Icons.Filled.TouchApp,
                    iconTint = MaterialTheme.colorScheme.primary,
                    heading = "Your phone is now a trackpad",
                    body = "Turn your phone into a wireless trackpad for your Windows PC.",
                    illustration = { OnboardingIllustrationPhoneTrackpad() }
                )
                1 -> OnboardingPage(
                    icon = null,
                    iconTint = MaterialTheme.colorScheme.primary,
                    heading = "What you need",
                    body = "Just a Bluetooth-enabled PC. That's it.",
                    illustration = { OnboardingIllustrationChecklist() }
                )
                2 -> OnboardingPage(
                    icon = null,
                    iconTint = MaterialTheme.colorScheme.primary,
                    heading = "How it works",
                    body = "Your phone connects directly over Bluetooth.",
                    illustration = { OnboardingIllustrationFlow() }
                )
                3 -> OnboardingPage(
                    icon = Icons.Filled.Bluetooth,
                    iconTint = MaterialTheme.colorScheme.secondary,
                    heading = "Ready to pair?",
                    body = "Let's get your phone connected to your computer.",
                    illustration = null,
                    action = {
                        Spacer(modifier = Modifier.height(32.dp))
                        Button(
                            onClick = onComplete,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 32.dp)
                                .height(52.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Set Up", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                )
            }
        }

        // Dot indicators
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(4) { index ->
                val selected = pagerState.currentPage == index
                val dotColor by animateColorAsState(
                    targetValue = if (selected) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.outlineVariant,
                    label = "dot$index"
                )
                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(if (selected) 10.dp else 8.dp)
                        .clip(CircleShape)
                        .background(dotColor)
                )
            }
        }
    }
}

@Composable
private fun OnboardingPage(
    icon: androidx.compose.ui.graphics.vector.ImageVector?,
    iconTint: Color,
    heading: String,
    body: String,
    illustration: (@Composable () -> Unit)?,
    action: (@Composable () -> Unit)? = null
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (illustration != null) {
            illustration()
            Spacer(modifier = Modifier.height(32.dp))
        } else if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(72.dp)
            )
            Spacer(modifier = Modifier.height(32.dp))
        }
        Text(
            text = heading,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = body,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        if (action != null) {
            action()
        }
    }
}

@Composable
private fun OnboardingIllustrationPhoneTrackpad() {
    val primary = MaterialTheme.colorScheme.primary
    val accent = MaterialTheme.colorScheme.secondary

    val phoneSlide = remember { Animatable(40f) }
    val phoneAlpha = remember { Animatable(0f) }
    val cursorAnim = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        launch { phoneAlpha.animateTo(1f, tween(600)) }
        launch { phoneSlide.animateTo(0f, tween(700, easing = EaseOutCubic)) }
        delay(400)
        cursorAnim.animateTo(1f, tween(800, easing = EaseOutBack))
    }

    val infiniteTransition = rememberInfiniteTransition(label = "cursorFloat")
    val cursorY by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 8f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "cursorY"
    )

    Canvas(
        modifier = Modifier
            .size(160.dp)
            .alpha(phoneAlpha.value)
            .offset(y = phoneSlide.value.dp)
    ) {
        val w = size.width
        val h = size.height
        // Glow behind phone
        drawRoundRect(
            brush = Brush.radialGradient(
                listOf(primary.copy(alpha = 0.12f), Color.Transparent),
                center = Offset(w * 0.5f, h * 0.45f),
                radius = w * 0.35f
            ),
            topLeft = Offset(w * 0.25f, h * 0.05f),
            size = Size(w * 0.5f, h * 0.8f),
            cornerRadius = CornerRadius(w * 0.06f)
        )
        // Phone body
        drawRoundRect(
            color = primary,
            topLeft = Offset(w * 0.3f, h * 0.1f),
            size = Size(w * 0.4f, h * 0.7f),
            cornerRadius = CornerRadius(w * 0.04f),
            style = Stroke(width = 3f)
        )
        // Screen area
        drawRoundRect(
            color = primary.copy(alpha = 0.08f),
            topLeft = Offset(w * 0.34f, h * 0.18f),
            size = Size(w * 0.32f, h * 0.5f),
            cornerRadius = CornerRadius(w * 0.02f)
        )
        // Cursor arrow — animated in with float
        if (cursorAnim.value > 0f) {
            val cScale = cursorAnim.value
            val yOff = cursorY * cScale
            val arrowPath = Path().apply {
                moveTo(w * 0.45f, h * 0.3f + yOff)
                lineTo(w * 0.45f, h * 0.55f + yOff)
                lineTo(w * 0.50f, h * 0.48f + yOff)
                lineTo(w * 0.57f, h * 0.58f + yOff)
                lineTo(w * 0.60f, h * 0.55f + yOff)
                lineTo(w * 0.53f, h * 0.45f + yOff)
                lineTo(w * 0.59f, h * 0.42f + yOff)
                close()
            }
            drawPath(arrowPath, color = accent.copy(alpha = cScale))
        }
    }
}

@Composable
private fun OnboardingIllustrationChecklist() {
    val accent = MaterialTheme.colorScheme.secondary
    val textColor = MaterialTheme.colorScheme.onSurface
    val items = listOf("Bluetooth-enabled PC", "No companion app needed", "No Wi-Fi required")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        items.forEachIndexed { index, text ->
            var visible by remember { mutableStateOf(false) }
            LaunchedEffect(Unit) {
                delay(index * 300L + 200L)
                visible = true
            }
            val itemAlpha by animateFloatAsState(
                targetValue = if (visible) 1f else 0f, tween(400), label = "checkAlpha$index"
            )
            val itemSlide by animateFloatAsState(
                targetValue = if (visible) 0f else 20f, tween(400, easing = EaseOutCubic), label = "checkSlide$index"
            )
            Box(modifier = Modifier.alpha(itemAlpha).offset(x = itemSlide.dp)) {
                OnboardingCheckItem(text = text, checked = true, accent = accent, textColor = textColor)
            }
            if (index < items.lastIndex) Spacer(modifier = Modifier.height(12.dp))
        }
    }
}

@Composable
private fun OnboardingCheckItem(text: String, checked: Boolean, accent: Color, textColor: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(28.dp)
                .clip(CircleShape)
                .background(if (checked) accent.copy(alpha = 0.15f) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            if (checked) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = accent,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
        Spacer(modifier = Modifier.width(12.dp))
        Text(text = text, fontSize = 16.sp, color = textColor)
    }
}

@Composable
private fun OnboardingIllustrationFlow() {
    val primary = MaterialTheme.colorScheme.primary
    val accent = MaterialTheme.colorScheme.secondary

    var phoneVisible by remember { mutableStateOf(false) }
    var btVisible by remember { mutableStateOf(false) }
    var pcVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        phoneVisible = true; delay(300)
        btVisible = true; delay(300)
        pcVisible = true
    }

    val phoneAlpha by animateFloatAsState(if (phoneVisible) 1f else 0f, tween(500), label = "pA")
    val phoneX by animateFloatAsState(if (phoneVisible) 0f else -30f, tween(500, easing = EaseOutCubic), label = "pX")
    val btAlpha by animateFloatAsState(if (btVisible) 1f else 0f, tween(400), label = "bA")
    val btScale by animateFloatAsState(if (btVisible) 1f else 0.5f, tween(400, easing = EaseOutBack), label = "bS")
    val pcAlpha by animateFloatAsState(if (pcVisible) 1f else 0f, tween(500), label = "pcA")
    val pcX by animateFloatAsState(if (pcVisible) 0f else 30f, tween(500, easing = EaseOutCubic), label = "pcX")

    // Pulsing signal dots between phone and PC
    val infiniteTransition = rememberInfiniteTransition(label = "signal")
    val signalPhase by infiniteTransition.animateFloat(
        0f, 1f, infiniteRepeatable(tween(1200, easing = LinearEasing)), label = "sigPhase"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Phone
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(phoneAlpha).offset(x = phoneX.dp)
        ) {
            Icon(Icons.Filled.PhoneAndroid, null, tint = primary, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text("Phone", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        // Signal dots
        Spacer(modifier = Modifier.width(8.dp))
        Canvas(modifier = Modifier.size(width = 40.dp, height = 32.dp).alpha(btAlpha)) {
            val dots = 3
            for (i in 0 until dots) {
                val phase = ((signalPhase + i * 0.33f) % 1f)
                val x = size.width * phase
                val alpha = if (phase < 0.5f) phase * 2f else (1f - phase) * 2f
                drawCircle(accent.copy(alpha = alpha * 0.7f), radius = 3f, center = Offset(x, size.height / 2f))
            }
        }
        // BT icon
        Icon(
            Icons.Filled.Bluetooth, null, tint = accent,
            modifier = Modifier.size(32.dp).alpha(btAlpha).scale(btScale)
        )
        // Signal dots
        Canvas(modifier = Modifier.size(width = 40.dp, height = 32.dp).alpha(btAlpha)) {
            val dots = 3
            for (i in 0 until dots) {
                val phase = ((signalPhase + i * 0.33f) % 1f)
                val x = size.width * phase
                val alpha = if (phase < 0.5f) phase * 2f else (1f - phase) * 2f
                drawCircle(accent.copy(alpha = alpha * 0.7f), radius = 3f, center = Offset(x, size.height / 2f))
            }
        }
        Spacer(modifier = Modifier.width(8.dp))
        // PC
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.alpha(pcAlpha).offset(x = pcX.dp)
        ) {
            Icon(Icons.Filled.Computer, null, tint = primary, modifier = Modifier.size(48.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text("PC", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
fun PermissionScreen(
    onContinue: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Bluetooth,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(80.dp)
        )
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = "Bluetooth Access",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "PhonePad needs Bluetooth access to connect to your computer as a trackpad.\n\nWe don't collect any data.",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(40.dp))
        Button(
            onClick = onContinue,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Continue", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
fun PermissionDeniedScreen(
    onOpenSettings: () -> Unit,
    onTryAgain: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = null,
            tint = Color(0xFFD97706),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "Bluetooth Access Required",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = "Bluetooth access is required for PhonePad to work. Please grant permission in Settings or try again.",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onOpenSettings,
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .height(52.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Open Settings", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onTryAgain) {
            Text("Try Again", fontSize = 16.sp)
        }
    }
}

@Composable
fun PairingGuideScreen(
    connectionStatus: String,
    isConnected: Boolean,
    connectedHostName: String?,
    onNavigateToTrackpad: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Connected celebration state
    var showCelebration by remember { mutableStateOf(false) }
    val celebrationScale = remember { Animatable(0f) }
    val celebrationAlpha = remember { Animatable(0f) }

    // Auto-navigate to trackpad after connection with celebration
    LaunchedEffect(isConnected) {
        if (isConnected) {
            showCelebration = true
            launch { celebrationAlpha.animateTo(1f, tween(300)) }
            launch { celebrationScale.animateTo(1f, tween(500, easing = EaseOutBack)) }
            delay(1800L)
            launch { celebrationAlpha.animateTo(0f, tween(400)) }
            delay(400)
            onNavigateToTrackpad()
        }
    }

    val scrollState = rememberScrollState()
    var showTroubleshooting by remember { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Text(
            text = "Pairing Guide",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Follow these steps to connect your phone to your PC.",
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(32.dp))

        // Step 1
        PairingStep(
            number = 1,
            title = "Open Bluetooth settings on your computer",
            description = "Go to Windows Settings → Bluetooth & devices",
            isActive = true,
            isComplete = false
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Step 2
        PairingStep(
            number = 2,
            title = "Tap 'Add Device' and choose 'Bluetooth'",
            description = "Your computer will start scanning for nearby devices.",
            isActive = true,
            isComplete = false
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Step 3 - live status
        PairingStep(
            number = 3,
            title = "Select your phone from the device list",
            description = "It will appear by your phone's Bluetooth name.",
            isActive = true,
            isComplete = isConnected,
            statusContent = {
                Spacer(modifier = Modifier.height(8.dp))
                PairingStatusIndicator(
                    isConnected = isConnected,
                    connectedHostName = connectedHostName
                )
            }
        )
        Spacer(modifier = Modifier.height(20.dp))

        // Step 4
        PairingStep(
            number = 4,
            title = if (isConnected) "You're all set!" else "Waiting for connection...",
            description = if (isConnected) "Navigating to trackpad..." else null,
            isActive = isConnected,
            isComplete = isConnected
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Troubleshooting
        TextButton(onClick = { showTroubleshooting = !showTroubleshooting }) {
            Text(
                text = "Troubleshooting",
                fontSize = 14.sp
            )
            Icon(
                imageVector = if (showTroubleshooting) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
        AnimatedVisibility(visible = showTroubleshooting) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant,
                        RoundedCornerShape(12.dp)
                    )
                    .padding(16.dp)
            ) {
                Text(
                    text = "If your phone doesn't appear:",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "- Make sure Bluetooth is enabled on both devices", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "- Restart Bluetooth on your computer", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "- Move your phone closer to the computer", fontSize = 14.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(text = "- Try removing your phone from paired devices and re-pairing", fontSize = 14.sp)
            }
        }
    }

    // Success celebration overlay
    if (showCelebration) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface.copy(alpha = celebrationAlpha.value * 0.85f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .alpha(celebrationAlpha.value)
                    .scale(celebrationScale.value)
            ) {
                // Success checkmark with ring
                Canvas(modifier = Modifier.size(96.dp)) {
                    val w = size.width
                    val strokeW = 4f
                    // Outer ring
                    drawCircle(
                        color = Color(0xFF0D9488),
                        radius = w / 2f - strokeW,
                        style = Stroke(width = strokeW)
                    )
                    // Inner glow
                    drawCircle(
                        brush = Brush.radialGradient(
                            listOf(Color(0xFF0D9488).copy(alpha = 0.15f), Color.Transparent)
                        ),
                        radius = w / 2f
                    )
                    // Checkmark
                    val check = Path().apply {
                        moveTo(w * 0.28f, w * 0.5f)
                        lineTo(w * 0.44f, w * 0.65f)
                        lineTo(w * 0.72f, w * 0.35f)
                    }
                    drawPath(check, Color(0xFF0D9488), style = Stroke(width = 5f, cap = StrokeCap.Round))
                }
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "Connected!",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF0D9488)
                )
                if (connectedHostName != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = connectedHostName,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
    } // close Box
}

@Composable
private fun PairingStep(
    number: Int,
    title: String,
    description: String?,
    isActive: Boolean,
    isComplete: Boolean,
    statusContent: (@Composable () -> Unit)? = null
) {
    val circleColor by animateColorAsState(
        targetValue = when {
            isComplete -> MaterialTheme.colorScheme.secondary
            isActive -> MaterialTheme.colorScheme.primary
            else -> MaterialTheme.colorScheme.outlineVariant
        },
        label = "stepCircle$number"
    )

    Row(modifier = Modifier.fillMaxWidth()) {
        // Numbered circle
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(circleColor),
            contentAlignment = Alignment.Center
        ) {
            if (isComplete) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Text(
                    text = number.toString(),
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            if (description != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = description,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            if (statusContent != null) {
                statusContent()
            }
        }
    }
}

@Composable
private fun PairingStatusIndicator(
    isConnected: Boolean,
    connectedHostName: String?
) {
    val statusColor by animateColorAsState(
        targetValue = if (isConnected) MaterialTheme.colorScheme.secondary else Color(0xFFD97706),
        label = "statusColor"
    )

    Row(verticalAlignment = Alignment.CenterVertically) {
        if (isConnected) {
            Icon(
                imageVector = Icons.Filled.CheckCircle,
                contentDescription = null,
                tint = statusColor,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Connected" + if (connectedHostName != null) " to $connectedHostName" else "",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = statusColor
            )
        } else {
            // Pulsing dot
            val infiniteTransition = rememberInfiniteTransition(label = "pulse")
            val pulseAlpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "pulseAlpha"
            )
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .clip(CircleShape)
                    .alpha(pulseAlpha)
                    .background(statusColor)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Waiting for connection...",
                fontSize = 14.sp,
                color = statusColor
            )
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@SuppressLint("MissingPermission")
@Composable
fun TrackpadScreen(
    connectionStatus: String,
    bondedDevices: List<BluetoothDevice>,
    isConnected: Boolean,
    connectedHostName: String?,
    onDeviceSelected: (BluetoothDevice) -> Unit,
    onTouchEvent: (MotionEvent) -> Boolean,
    showSettings: Boolean,
    onToggleSettings: () -> Unit,
    sensitivity: Float,
    onSensitivityChange: (Float) -> Unit,
    tapToClick: Boolean,
    onTapToClickChange: (Boolean) -> Unit,
    naturalScroll: Boolean,
    onNaturalScrollChange: (Boolean) -> Unit,
    rippleEnabled: Boolean,
    onRippleChange: (Boolean) -> Unit,
    hapticsEnabled: Boolean,
    onHapticsChange: (Boolean) -> Unit,
    statusBarAutoHide: Boolean,
    onStatusBarAutoHideChange: (Boolean) -> Unit,
    trackpadTheme: String,
    onTrackpadThemeChange: (String) -> Unit,
    uiTheme: String,
    onUiThemeChange: (String) -> Unit,
    batteryPercent: Int,
    onToggleGestureGuide: () -> Unit,
    settingsInitialTab: Int,
    showDeviceManager: Boolean,
    onToggleDeviceManager: () -> Unit,
    onDismissDeviceManager: () -> Unit,
    deviceNicknames: Map<String, String>,
    onRenameDevice: (String, String) -> Unit,
    onForgetDevice: (BluetoothDevice) -> Unit,
    onNavigateToPairingGuide: () -> Unit,
    isBluetoothOff: Boolean,
    onTurnOnBluetooth: () -> Unit,
    showDisconnectSheet: Boolean,
    disconnectAutoReconnectFailed: Boolean,
    onDismissDisconnectSheet: () -> Unit,
    onRetryConnect: () -> Unit,
    appVersion: String,
    hasBluetoothPermissions: Boolean,
    onRequestPermissions: () -> Unit,
    onOpenAppSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val darkSurface = when (trackpadTheme) {
        "darker" -> Color(0xFF050508)
        "amoled" -> Color.Black
        else -> Color(0xFF111118)
    }
    val accentGlow = when (trackpadTheme) {
        "darker" -> Color(0xFF3B3F9E)
        "amoled" -> Color.Transparent
        else -> Color(0xFF4B4FCF)
    }
    val edgeGlow = when (trackpadTheme) {
        "darker" -> Color(0xFF2B2D6E).copy(alpha = 0.06f)
        "amoled" -> Color.Transparent
        else -> Color(0xFF2B2D6E).copy(alpha = 0.12f)
    }

    // Status bar fade: visible initially, fades after 3s of no interaction
    var statusBarVisible by remember { mutableStateOf(true) }
    var lastInteractionTime by remember { mutableStateOf(System.currentTimeMillis()) }
    val statusBarAlpha by animateFloatAsState(
        targetValue = if (statusBarVisible) 1f else 0f,
        animationSpec = tween(600),
        label = "statusBarAlpha"
    )

    // Connected entrance animation
    var justConnected by remember { mutableStateOf(false) }
    val connectGlow = remember { Animatable(0f) }
    LaunchedEffect(isConnected) {
        if (isConnected) {
            justConnected = true
            connectGlow.snapTo(0f)
            connectGlow.animateTo(1f, tween(600, easing = EaseOutCubic))
            delay(800)
            connectGlow.animateTo(0f, tween(1000))
            justConnected = false
        }
    }

    // Fade out after 3s
    LaunchedEffect(lastInteractionTime, connectionStatus, statusBarAutoHide) {
        if (connectionStatus != "CONNECTED" && connectionStatus != "DISCONNECTED") {
            statusBarVisible = true
            return@LaunchedEffect
        }
        statusBarVisible = true
        if (statusBarAutoHide) {
            delay(3000L)
            if (isConnected) statusBarVisible = false
        }
    }

    // Breathing glow animation for trackpad surface
    val infiniteTransition = rememberInfiniteTransition(label = "trackpadAmbient")
    val breathe by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(4000, easing = FastOutSlowInEasing), RepeatMode.Reverse),
        label = "breathe"
    )

    // Show reconnect sheet
    var showReconnectSheet by remember { mutableStateOf(false) }

    // Immersive mode
    val view = LocalView.current
    DisposableEffect(Unit) {
        val window = (view.context as? ComponentActivity)?.window
        window?.let { w ->
            w.insetsController?.let { controller ->
                controller.hide(android.view.WindowInsets.Type.systemBars())
                controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            }
        }
        onDispose {
            window?.let { w ->
                w.insetsController?.show(android.view.WindowInsets.Type.systemBars())
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(darkSurface)
    ) {
        // Very subtle ambient gradient — theme-aware
        if (trackpadTheme != "amoled") {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val cx = size.width / 2f
                val cy = size.height * 0.35f
                val glowRadius = size.minDimension * 0.5f
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(accentGlow.copy(alpha = 0.012f + breathe * 0.005f), Color.Transparent),
                        center = Offset(cx, cy), radius = glowRadius
                    ),
                    radius = glowRadius, center = Offset(cx, cy)
                )
            }
        }

        // Connected celebration glow
        if (connectGlow.value > 0f) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawCircle(
                    brush = Brush.radialGradient(
                        listOf(Color(0xFF0D9488).copy(alpha = connectGlow.value * 0.2f), Color.Transparent),
                        center = Offset(size.width / 2f, size.height / 2f),
                        radius = size.minDimension * (0.3f + connectGlow.value * 0.5f)
                    ),
                    radius = size.minDimension * (0.3f + connectGlow.value * 0.5f),
                    center = Offset(size.width / 2f, size.height / 2f)
                )
            }
        }

        // Touch surface (edge-to-edge)
        Box(
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (isConnected) {
                        Modifier.pointerInteropFilter { event ->
                            lastInteractionTime = System.currentTimeMillis()
                            onTouchEvent(event)
                        }
                    } else {
                        Modifier
                    }
                )
        ) {
            if (!isConnected) {
                // Animated not-connected state
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (connectionStatus == "CONNECTING") {
                        // Pulsing Bluetooth waves animation
                        val waveTransition = rememberInfiniteTransition(label = "btWaves")
                        val wave1 by waveTransition.animateFloat(
                            0f, 1f, infiniteRepeatable(tween(1500, easing = LinearEasing)), label = "w1"
                        )
                        val wave2 by waveTransition.animateFloat(
                            0f, 1f, infiniteRepeatable(tween(1500, 500, easing = LinearEasing)), label = "w2"
                        )
                        val wave3 by waveTransition.animateFloat(
                            0f, 1f, infiniteRepeatable(tween(1500, 1000, easing = LinearEasing)), label = "w3"
                        )
                        Canvas(modifier = Modifier.size(100.dp)) {
                            val cx = size.width / 2f
                            val cy = size.height / 2f
                            listOf(wave1, wave2, wave3).forEach { w ->
                                val radius = 12f + w * (size.minDimension / 2f - 12f)
                                val alpha = (1f - w) * 0.4f
                                drawCircle(
                                    color = Color(0xFF7B7FD4).copy(alpha = alpha),
                                    radius = radius,
                                    center = Offset(cx, cy),
                                    style = Stroke(width = 2.5f)
                                )
                            }
                            // BT icon center
                            drawCircle(Color(0xFF7B7FD4), radius = 14f, center = Offset(cx, cy))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Connecting...",
                            color = Color(0xFF7B7FD4).copy(alpha = 0.8f),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        // Subtle trackpad outline hint
                        Canvas(modifier = Modifier.size(80.dp)) {
                            drawRoundRect(
                                color = Color.White.copy(alpha = 0.08f + breathe * 0.04f),
                                cornerRadius = CornerRadius(12f),
                                style = Stroke(width = 1.5f)
                            )
                            val arrowPath = Path().apply {
                                val cx = size.width * 0.45f
                                val cy = size.height * 0.3f
                                moveTo(cx, cy)
                                lineTo(cx, cy + size.height * 0.28f)
                                lineTo(cx + size.width * 0.08f, cy + size.height * 0.2f)
                                lineTo(cx + size.width * 0.15f, cy + size.height * 0.32f)
                                lineTo(cx + size.width * 0.2f, cy + size.height * 0.28f)
                                lineTo(cx + size.width * 0.13f, cy + size.height * 0.18f)
                                lineTo(cx + size.width * 0.2f, cy + size.height * 0.15f)
                                close()
                            }
                            drawPath(arrowPath, color = Color.White.copy(alpha = 0.1f + breathe * 0.05f))
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Not connected",
                            color = Color.White.copy(alpha = 0.35f),
                            fontSize = 15.sp
                        )
                    }
                    if (bondedDevices.isNotEmpty() && connectionStatus != "CONNECTING") {
                        Spacer(modifier = Modifier.height(24.dp))
                        bondedDevices.forEach { device ->
                            OutlinedButton(
                                onClick = { onDeviceSelected(device) },
                                modifier = Modifier.padding(vertical = 4.dp),
                                colors = ButtonDefaults.outlinedButtonColors(
                                    contentColor = Color(0xFF7B7FD4)
                                ),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = device.name ?: device.address,
                                    fontSize = 14.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Status bar (top) — glass effect with blur
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .alpha(statusBarAlpha)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Black.copy(alpha = 0.5f), Color.Black.copy(alpha = 0.15f), Color.Transparent)
                    )
                )
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Connection status pill (left)
            ConnectionStatusPill(
                connectionStatus = connectionStatus,
                isConnected = isConnected,
                connectedHostName = connectedHostName,
                onClick = {
                    if (!isConnected) showReconnectSheet = true
                },
                onLongClick = {
                    onToggleDeviceManager()
                }
            )

            // Battery % (right)
            if (batteryPercent >= 0) {
                Text(
                    text = "$batteryPercent%",
                    color = Color.White.copy(alpha = 0.5f),
                    fontSize = 11.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }

        // Gear icon (top-right, always present, subtle)
        IconButton(
            onClick = {
                lastInteractionTime = System.currentTimeMillis()
                onToggleSettings()
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 42.dp, end = 8.dp)
                .alpha(0.25f)
        ) {
            Icon(
                imageVector = Icons.Filled.Settings,
                contentDescription = "Settings",
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }


        // Settings overlay (full-screen)
        AnimatedVisibility(
            visible = showSettings,
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(300))
        ) {
            SettingsOverlay(
                onDismiss = onToggleSettings,
                sensitivity = sensitivity,
                onSensitivityChange = onSensitivityChange,
                tapToClick = tapToClick,
                onTapToClickChange = onTapToClickChange,
                naturalScroll = naturalScroll,
                onNaturalScrollChange = onNaturalScrollChange,
                rippleEnabled = rippleEnabled,
                onRippleChange = onRippleChange,
                hapticsEnabled = hapticsEnabled,
                onHapticsChange = onHapticsChange,
                statusBarAutoHide = statusBarAutoHide,
                onStatusBarAutoHideChange = onStatusBarAutoHideChange,
                uiTheme = uiTheme,
                onUiThemeChange = onUiThemeChange,
                settingsInitialTab = settingsInitialTab,
                connectedHostName = connectedHostName,
                bondedDevices = bondedDevices,
                isConnected = isConnected,
                connectedDeviceAddress = bondedDevices.firstOrNull { (deviceNicknames[it.address] ?: it.name) == connectedHostName }?.address,
                deviceNicknames = deviceNicknames,
                onOpenDeviceManager = {
                    onToggleSettings()
                    onToggleDeviceManager()
                },
                onNavigateToPairingGuide = onNavigateToPairingGuide,
                appVersion = appVersion
            )
        }

        // Device Manager overlay
        AnimatedVisibility(
            visible = showDeviceManager,
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(300))
        ) {
            DeviceManagerOverlay(
                bondedDevices = bondedDevices,
                connectedDeviceAddress = bondedDevices.firstOrNull { isConnected && (deviceNicknames[it.address] ?: it.name) == connectedHostName }?.address,
                deviceNicknames = deviceNicknames,
                onRenameDevice = onRenameDevice,
                onForgetDevice = onForgetDevice,
                onDeviceSelected = onDeviceSelected,
                onNavigateToPairingGuide = onNavigateToPairingGuide,
                onDismiss = onDismissDeviceManager
            )
        }

        // Reconnect sheet (manual, from tapping disconnected pill)
        AnimatedVisibility(
            visible = showReconnectSheet && !isConnected,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut()
        ) {
            ReconnectSheet(
                connectedHostName = connectedHostName,
                bondedDevices = bondedDevices,
                onDeviceSelected = { device ->
                    onDeviceSelected(device)
                    showReconnectSheet = false
                },
                onDismiss = { showReconnectSheet = false }
            )
        }

        // Auto-reconnect failed sheet (Phase 5)
        AnimatedVisibility(
            visible = showDisconnectSheet && disconnectAutoReconnectFailed,
            enter = slideInVertically { it } + fadeIn(),
            exit = slideOutVertically { it } + fadeOut()
        ) {
            DisconnectErrorSheet(
                lastDeviceName = connectedHostName,
                onRetry = onRetryConnect,
                onPairDifferent = onNavigateToPairingGuide,
                onDismiss = onDismissDisconnectSheet
            )
        }

        // Bluetooth off overlay (Phase 5)
        AnimatedVisibility(
            visible = isBluetoothOff,
            enter = fadeIn(tween(300)),
            exit = fadeOut(tween(300))
        ) {
            BluetoothOffOverlay(onTurnOn = onTurnOnBluetooth)
        }

        // Permission revoked overlay (Phase 5)
        if (!hasBluetoothPermissions && !isBluetoothOff) {
            PermissionRevokedOverlay(
                onGrantPermission = onOpenAppSettings
            )
        }

    }
}

@Composable
private fun ConnectionStatusPill(
    connectionStatus: String,
    isConnected: Boolean,
    connectedHostName: String?,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val dotColor = when {
        isConnected -> Color(0xFF0D9488)
        connectionStatus == "CONNECTING" -> Color(0xFFD97706)
        else -> Color(0xFFDC2626)
    }

    val infiniteTransition = rememberInfiniteTransition(label = "statusPulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
            .clickable { onClick() }
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .alpha(if (!isConnected) pulseAlpha else 1f)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = when {
                isConnected -> connectedHostName ?: "Connected"
                connectionStatus == "CONNECTING" -> "Reconnecting..."
                else -> "Disconnected"
            },
            color = Color.White.copy(alpha = 0.8f),
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium
        )
        if (connectionStatus == "CONNECTING") {
            Spacer(modifier = Modifier.width(6.dp))
            CircularProgressIndicator(
                modifier = Modifier.size(12.dp),
                strokeWidth = 1.5.dp,
                color = Color(0xFFD97706)
            )
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Phase 4 — Settings Bottom Sheet (3 tabs: Controls, Gestures, More)
// ──────────────────────────────────────────────────────────────────────────────

@SuppressLint("MissingPermission")
@Composable
private fun SettingsOverlay(
    onDismiss: () -> Unit,
    sensitivity: Float,
    onSensitivityChange: (Float) -> Unit,
    tapToClick: Boolean,
    onTapToClickChange: (Boolean) -> Unit,
    naturalScroll: Boolean,
    onNaturalScrollChange: (Boolean) -> Unit,
    rippleEnabled: Boolean,
    onRippleChange: (Boolean) -> Unit,
    hapticsEnabled: Boolean,
    onHapticsChange: (Boolean) -> Unit,
    statusBarAutoHide: Boolean,
    onStatusBarAutoHideChange: (Boolean) -> Unit,
    uiTheme: String,
    onUiThemeChange: (String) -> Unit,
    settingsInitialTab: Int,
    connectedHostName: String?,
    bondedDevices: List<BluetoothDevice>,
    isConnected: Boolean,
    connectedDeviceAddress: String?,
    deviceNicknames: Map<String, String>,
    onOpenDeviceManager: () -> Unit,
    onNavigateToPairingGuide: () -> Unit,
    appVersion: String
) {
    val context = LocalContext.current
    val isDark = uiTheme == "dark"

    // Theme colors
    val bg = if (isDark) Color(0xFF08080D) else Color(0xFFF2F2F7)
    val surface0 = if (isDark) Color(0xFF0F0F16) else Color(0xFFE8E8EE)
    val surface1 = if (isDark) Color(0xFF14141E) else Color.White
    val surface2 = if (isDark) Color(0xFF1B1B28) else Color(0xFFF5F5FA)
    val accent = Color(0xFF7C6AF6)
    val accentLight = Color(0xFF9D8DF7)
    val accentSoft = if (isDark) Color(0xFF7C6AF6).copy(alpha = 0.08f) else Color(0xFF7C6AF6).copy(alpha = 0.06f)
    val green = Color(0xFF3DDC84)
    val pink = Color(0xFFF472B6)
    val blue = Color(0xFF60A5FA)
    val text1 = if (isDark) Color(0xFFEEEEF2) else Color(0xFF1A1A2E)
    val text2 = if (isDark) Color(0xFF9494AC) else Color(0xFF6B6B88)
    val text3 = if (isDark) Color(0xFF5C5C74) else Color(0xFF9898AE)
    val border = if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.06f)
    val toggleOff = if (isDark) Color(0xFF2A2A3A) else Color(0xFFCDCDD8)
    val success = Color(0xFF3DDC84)
    val successSoft = Color(0xFF3DDC84).copy(alpha = 0.10f)
    val trackBg = if (isDark) Color(0xFF2A2A3A) else Color(0xFFD8D8E4)
    val thumbSh = if (isDark) Color(0x80000000) else Color(0x1F000000)
    val accentGlow = accent.copy(alpha = if (isDark) 0.25f else 0.12f)
    val devBg = if (isDark) Color(0xFF1B1B28) else Color(0xFFF0F0F6)
    val devConn = Color(0xFF3DDC84).copy(alpha = 0.08f)
    val tabBg = if (isDark) Color(0xFF1B1B28) else Color.White
    val dashedColor = accent.copy(alpha = if (isDark) 0.25f else 0.35f)

    val cardShape = RoundedCornerShape(16.dp)
    val sheetShape = RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp)

    var selectedTab by remember { mutableStateOf(settingsInitialTab.coerceIn(0, 2)) }

    // Slide-up animation
    val slideAnim = remember { Animatable(1f) }
    val scrimAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        launch { scrimAlpha.animateTo(1f, tween(300)) }
        launch { slideAnim.animateTo(0f, tween(400, easing = EaseOutCubic)) }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f * scrimAlpha.value))
            .clickable { onDismiss() },
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.82f)
                .offset(y = (slideAnim.value * 600).dp)
                .background(bg, sheetShape)
                .border(0.5.dp, border, sheetShape)
                .clickable(enabled = false) {}
        ) {
            // ── Gradient Header (logo + device banner) ──
            val glassWhite = Color.White.copy(alpha = 0.16f)
            val glassBorder = Color.White.copy(alpha = 0.28f)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 10.dp)
                    .shadow(
                        elevation = 10.dp,
                        shape = RoundedCornerShape(22.dp),
                        ambientColor = Color(0x503730A3),
                        spotColor = Color(0x604F46E5)
                    )
                    .clip(RoundedCornerShape(22.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF3730A3), // Deep Royal Indigo
                                Color(0xFF4F46E5), // Electric Indigo
                                Color(0xFF7C3AED), // Vivid Purple
                                Color(0xFF8B5CF6)  // Luminous Violet
                            ),
                            start = Offset(0f, 0f),
                            end = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY)
                        )
                    )
                    .border(
                        1.dp,
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.White.copy(alpha = 0.35f),
                                Color.White.copy(alpha = 0.12f)
                            )
                        ),
                        RoundedCornerShape(22.dp)
                    )
            ) {
                // Celestial sonar radar axis with glowing epicenter & orbital rings
                Canvas(modifier = Modifier.matchParentSize()) {
                    val cx = size.width * 0.52f
                    val cy = size.height * 0.50f

                    // Soft ambient radial glow behind the epicenter
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(Color.White.copy(alpha = 0.12f), Color.Transparent),
                            center = Offset(cx, cy),
                            radius = 120.dp.toPx()
                        ),
                        radius = 120.dp.toPx(),
                        center = Offset(cx, cy)
                    )

                    // Concentric sonar rings
                    val ringColor = Color.White.copy(alpha = 0.08f)
                    for (r in listOf(115.dp.toPx(), 75.dp.toPx(), 45.dp.toPx())) {
                        drawCircle(
                            color = ringColor,
                            radius = r,
                            center = Offset(cx, cy),
                            style = Stroke(1.2.dp.toPx())
                        )
                    }

                    // Horizontal radar underline axis between the 2 rows across the card
                    drawLine(
                        color = Color.White.copy(alpha = 0.24f),
                        start = Offset(0f, cy),
                        end = Offset(size.width, cy),
                        strokeWidth = 1.dp.toPx()
                    )

                    // Glowing center dot (outer pulse halo + core)
                    drawCircle(
                        color = Color.White.copy(alpha = 0.35f),
                        radius = 6.dp.toPx(),
                        center = Offset(cx, cy)
                    )
                    drawCircle(
                        color = Color.White,
                        radius = 3.5.dp.toPx(),
                        center = Offset(cx, cy)
                    )

                    // Micro-constellation accent dots
                    drawCircle(
                        color = Color.White.copy(alpha = 0.35f),
                        radius = 1.5.dp.toPx(),
                        center = Offset(size.width * 0.15f, cy)
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.28f),
                        radius = 1.8.dp.toPx(),
                        center = Offset(size.width * 0.42f, cy - 35.dp.toPx())
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.22f),
                        radius = 1.5.dp.toPx(),
                        center = Offset(size.width * 0.88f, cy)
                    )
                }

                Column(modifier = Modifier.padding(start = 18.dp, end = 18.dp, top = 18.dp, bottom = 18.dp)) {
                    // Top row: app icon + name, theme + close
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // App icon — solid crisp white squircle tile with drop shadow
                            Box(
                                modifier = Modifier
                                    .size(42.dp)
                                    .shadow(4.dp, RoundedCornerShape(13.dp), spotColor = Color(0x40000000))
                                    .clip(RoundedCornerShape(13.dp))
                                    .background(Color.White),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PhoneAndroid,
                                    contentDescription = null,
                                    tint = Color(0xFF4338CA),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                "PhonePad",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                letterSpacing = (-0.3).sp
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Theme toggle — frosted glass circle
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(glassWhite)
                                    .border(1.dp, glassBorder, CircleShape)
                                    .clickable { onUiThemeChange(if (isDark) "light" else "dark") },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = if (isDark) Icons.Outlined.LightMode else Icons.Outlined.DarkMode,
                                    contentDescription = "Toggle theme",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            // Close — frosted glass circle
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(glassWhite)
                                    .border(1.dp, glassBorder, CircleShape)
                                    .clickable { onDismiss() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(26.dp))

                    // Device banner row inside gradient
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Device icon — frosted rounded square
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(13.dp))
                                .background(Color.White.copy(alpha = 0.16f))
                                .border(1.dp, glassBorder, RoundedCornerShape(13.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Computer,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = connectedHostName ?: "No device",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                lineHeight = 19.sp,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .background(
                                        if (isConnected) Color.Black.copy(alpha = 0.22f) else Color.Black.copy(alpha = 0.15f),
                                        RoundedCornerShape(50)
                                    )
                                    .border(
                                        1.dp,
                                        if (isConnected) Color(0xFF22C55E).copy(alpha = 0.50f) else Color.White.copy(alpha = 0.15f),
                                        RoundedCornerShape(50)
                                    )
                                    .padding(horizontal = 9.dp, vertical = 2.5.dp)
                            ) {
                                if (isConnected) {
                                    val pulseTransition = rememberInfiniteTransition(label = "pulse")
                                    val pulseAlpha by pulseTransition.animateFloat(
                                        initialValue = 1f,
                                        targetValue = 0.35f,
                                        animationSpec = infiniteRepeatable(
                                            animation = tween(1000, easing = LinearEasing),
                                            repeatMode = RepeatMode.Reverse
                                        ),
                                        label = "pulseAlpha"
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(7.dp)
                                            .clip(CircleShape)
                                            .alpha(pulseAlpha)
                                            .background(Color(0xFF22C55E))
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                }
                                Text(
                                    text = if (isConnected) "Connected" else "Disconnected",
                                    fontSize = 11.5.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color.White,
                                    lineHeight = 14.sp
                                )
                            }
                        }
                        // Device switcher buttons — glass style, perfectly aligned below top row buttons
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(glassWhite)
                                    .border(1.dp, glassBorder, RoundedCornerShape(12.dp))
                                    .clickable { onOpenDeviceManager() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Filled.Computer, contentDescription = "Device manager", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                            Box(
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(glassWhite)
                                    .border(1.dp, glassBorder, RoundedCornerShape(12.dp))
                                    .clickable { onNavigateToPairingGuide() },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.Add, contentDescription = "Add device", tint = Color.White, modifier = Modifier.size(18.dp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Tab Bar ──
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(surface0)
                    .border(0.5.dp, border, RoundedCornerShape(16.dp))
                    .padding(3.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val tabLabels = listOf("Controls", "Gestures", "More")
                tabLabels.forEachIndexed { index, label ->
                    val isSelected = selectedTab == index
                    val tabTextColor by animateColorAsState(
                        targetValue = if (isSelected) text1 else text3,
                        animationSpec = tween(250),
                        label = "tabText$index"
                    )
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .then(
                                if (isSelected) Modifier
                                    .shadow(3.dp, RoundedCornerShape(13.dp))
                                    .clip(RoundedCornerShape(13.dp))
                                    .background(surface1)
                                    .border(0.5.dp, border, RoundedCornerShape(13.dp))
                                else Modifier
                                    .clip(RoundedCornerShape(13.dp))
                                    .background(Color.Transparent)
                            )
                            .clickable { selectedTab = index }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                            color = tabTextColor
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // ── Tab Content ──
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                when (selectedTab) {
                    0 -> SettingsControlsTab(
                        sensitivity = sensitivity,
                        onSensitivityChange = onSensitivityChange,
                        tapToClick = tapToClick,
                        onTapToClickChange = onTapToClickChange,
                        naturalScroll = naturalScroll,
                        onNaturalScrollChange = onNaturalScrollChange,
                        hapticsEnabled = hapticsEnabled,
                        onHapticsChange = onHapticsChange,
                        rippleEnabled = rippleEnabled,
                        onRippleChange = onRippleChange,
                        statusBarAutoHide = statusBarAutoHide,
                        onStatusBarAutoHideChange = onStatusBarAutoHideChange,
                        isDark = isDark,
                        surface1 = surface1,
                        accent = accent,
                        accentLight = accentLight,
                        green = green,
                        pink = pink,
                        blue = blue,
                        text1 = text1,
                        text2 = text2,
                        text3 = text3,
                        border = border,
                        toggleOff = toggleOff,
                        trackBg = trackBg,
                        thumbSh = thumbSh,
                        accentGlow = accentGlow
                    )
                    1 -> SettingsGesturesTab(
                        isDark = isDark,
                        surface1 = surface1,
                        accent = accent,
                        accentSoft = accentSoft,
                        text1 = text1,
                        text2 = text2,
                        text3 = text3,
                        border = border
                    )
                    2 -> SettingsMoreTab(
                        bondedDevices = bondedDevices,
                        isConnected = isConnected,
                        connectedDeviceAddress = connectedDeviceAddress,
                        deviceNicknames = deviceNicknames,
                        onOpenDeviceManager = onOpenDeviceManager,
                        onNavigateToPairingGuide = onNavigateToPairingGuide,
                        appVersion = appVersion,
                        isDark = isDark,
                        surface1 = surface1,
                        accent = accent,
                        accentLight = accentLight,
                        green = green,
                        text1 = text1,
                        text2 = text2,
                        text3 = text3,
                        border = border,
                        success = success,
                        successSoft = successSoft,
                        devBg = devBg,
                        devConn = devConn,
                        dashedColor = dashedColor
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

// ── Controls Tab ──

@Composable
private fun SettingsControlsTab(
    sensitivity: Float,
    onSensitivityChange: (Float) -> Unit,
    tapToClick: Boolean,
    onTapToClickChange: (Boolean) -> Unit,
    naturalScroll: Boolean,
    onNaturalScrollChange: (Boolean) -> Unit,
    hapticsEnabled: Boolean,
    onHapticsChange: (Boolean) -> Unit,
    rippleEnabled: Boolean,
    onRippleChange: (Boolean) -> Unit,
    statusBarAutoHide: Boolean,
    onStatusBarAutoHideChange: (Boolean) -> Unit,
    isDark: Boolean,
    surface1: Color,
    accent: Color,
    accentLight: Color,
    green: Color,
    pink: Color,
    blue: Color,
    text1: Color,
    text2: Color,
    text3: Color,
    border: Color,
    toggleOff: Color,
    trackBg: Color,
    thumbSh: Color,
    accentGlow: Color
) {
    val cardShape = RoundedCornerShape(16.dp)

    // ── Cursor Speed ──
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(surface1)
            .border(0.5.dp, border, cardShape)
            .padding(16.dp)
    ) {
        // Header: "Cursor speed" + badge
        val speedLabel = when {
            sensitivity <= 0.7f -> "Precise"
            sensitivity <= 1.4f -> "Balanced"
            else -> "Fast"
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Cursor speed",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = text1
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(accent.copy(alpha = 0.10f))
                    .border(1.dp, accent.copy(alpha = 0.25f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 5.dp)
            ) {
                Text(
                    speedLabel,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = accent
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Large value display
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                "${"%.1f".format(sensitivity)}",
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = text1,
                lineHeight = 38.sp
            )
            Text(
                "×",
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = text3,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Custom slider
        var sliderFraction by remember { mutableStateOf((sensitivity - 0.3f) / 1.7f) }
        LaunchedEffect(sensitivity) { sliderFraction = ((sensitivity - 0.3f) / 1.7f).coerceIn(0f, 1f) }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .pointerInput(Unit) {
                    awaitEachGesture {
                        val down = awaitFirstDown(requireUnconsumed = false)
                        val w = size.width.toFloat()
                        fun update(x: Float) {
                            val f = (x / w).coerceIn(0f, 1f)
                            sliderFraction = f
                            onSensitivityChange(0.3f + f * 1.7f)
                        }
                        update(down.position.x)
                        down.consume()
                        while (true) {
                            val event = awaitPointerEvent()
                            if (event.changes.all { !it.pressed }) break
                            event.changes.forEach {
                                update(it.position.x)
                                it.consume()
                            }
                        }
                    }
                }
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val trackH = 8.dp.toPx()
                val trackY = (size.height - trackH) / 2
                val thumbR = 12.dp.toPx()
                val thumbX = thumbR + sliderFraction * (size.width - 2 * thumbR)

                // Track background
                drawRoundRect(
                    color = trackBg,
                    topLeft = Offset(0f, trackY),
                    size = Size(size.width, trackH),
                    cornerRadius = CornerRadius(trackH / 2)
                )

                // Fill gradient
                if (thumbX > 0f) {
                    drawRoundRect(
                        brush = Brush.horizontalGradient(
                            colors = listOf(accent, accentLight),
                            startX = 0f,
                            endX = thumbX
                        ),
                        topLeft = Offset(0f, trackY),
                        size = Size(thumbX, trackH),
                        cornerRadius = CornerRadius(trackH / 2)
                    )
                }

                // Thumb glow ring
                drawCircle(
                    color = accentGlow,
                    radius = thumbR + 4.dp.toPx(),
                    center = Offset(thumbX, size.height / 2)
                )

                // Thumb shadow
                drawCircle(
                    color = thumbSh,
                    radius = thumbR,
                    center = Offset(thumbX, size.height / 2 + 1.5.dp.toPx())
                )

                // Thumb white fill
                drawCircle(
                    color = Color.White,
                    radius = thumbR,
                    center = Offset(thumbX, size.height / 2)
                )

                // Thumb inner accent dot
                drawCircle(
                    color = accent,
                    radius = 4.dp.toPx(),
                    center = Offset(thumbX, size.height / 2)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Tick marks
        val tickCount = 10
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        ) {
            val thumbR = 12.dp.toPx()
            val usableW = size.width - 2 * thumbR
            for (i in 0..tickCount) {
                val x = thumbR + (usableW * i / tickCount)
                val tickH = 5.dp.toPx()
                val tickW = 2.dp.toPx()
                val isFilled = (i.toFloat() / tickCount) <= sliderFraction
                drawRoundRect(
                    color = if (isFilled) accent else trackBg,
                    topLeft = Offset(x - tickW / 2, 0f),
                    size = Size(tickW, tickH),
                    cornerRadius = CornerRadius(1.dp.toPx())
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Precise", fontSize = 11.sp, color = text3)
            Text("Balanced", fontSize = 11.sp, color = text3)
            Text("Fast", fontSize = 11.sp, color = text3)
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // ── Behavior ──
    Text(
        "BEHAVIOR",
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = text3,
        letterSpacing = 0.6.sp,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(surface1)
            .border(0.5.dp, border, cardShape)
    ) {
        SettingsToggleRow(
            title = "Tap to click",
            description = "Tap anywhere to left-click",
            icon = Icons.Outlined.TouchApp,
            iconColor = accentLight,
            iconBgColor = accent.copy(alpha = 0.12f),
            checked = tapToClick,
            onCheckedChange = onTapToClickChange,
            text1 = text1,
            text3 = text3,
            accent = accent,
            toggleOff = toggleOff,
            border = border
        )
        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(border))
        SettingsToggleRow(
            title = "Natural scroll",
            description = "Content follows finger direction",
            icon = Icons.Outlined.KeyboardArrowDown,
            iconColor = green,
            iconBgColor = green.copy(alpha = 0.10f),
            checked = naturalScroll,
            onCheckedChange = onNaturalScrollChange,
            text1 = text1,
            text3 = text3,
            accent = accent,
            toggleOff = toggleOff,
            border = border
        )
        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(border))
        SettingsToggleRow(
            title = "Haptic feedback",
            description = "Vibrate on tap and gesture",
            icon = Icons.Outlined.FlashOn,
            iconColor = pink,
            iconBgColor = pink.copy(alpha = 0.10f),
            checked = hapticsEnabled,
            onCheckedChange = onHapticsChange,
            text1 = text1,
            text3 = text3,
            accent = accent,
            toggleOff = toggleOff,
            border = border
        )
    }

    Spacer(modifier = Modifier.height(20.dp))

    // ── Display ──
    Text(
        "DISPLAY",
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = text3,
        letterSpacing = 0.6.sp,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(surface1)
            .border(0.5.dp, border, cardShape)
    ) {
        SettingsToggleRow(
            title = "Touch ripple",
            description = "Show ripple on touch",
            icon = Icons.Outlined.Adjust,
            iconColor = blue,
            iconBgColor = blue.copy(alpha = 0.10f),
            checked = rippleEnabled,
            onCheckedChange = onRippleChange,
            text1 = text1,
            text3 = text3,
            accent = accent,
            toggleOff = toggleOff,
            border = border
        )
        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(border))
        SettingsToggleRow(
            title = "Auto-hide status bar",
            description = "Fades after 3 seconds",
            icon = Icons.Outlined.WebAsset,
            iconColor = accentLight,
            iconBgColor = accent.copy(alpha = 0.12f),
            checked = statusBarAutoHide,
            onCheckedChange = onStatusBarAutoHideChange,
            text1 = text1,
            text3 = text3,
            accent = accent,
            toggleOff = toggleOff,
            border = border
        )
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    description: String,
    icon: ImageVector,
    iconColor: Color,
    iconBgColor: Color,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    text1: Color,
    text3: Color,
    accent: Color,
    toggleOff: Color,
    border: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Icon box
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(RoundedCornerShape(11.dp))
                .background(iconBgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = text1,
                lineHeight = 18.sp
            )
            Text(
                description,
                fontSize = 11.sp,
                color = text3,
                lineHeight = 14.sp,
                maxLines = 1
            )
        }
        Spacer(modifier = Modifier.width(8.dp))
        // Custom toggle - 46x28, rounded 14, thumb 22dp
        val toggleBg by animateColorAsState(
            targetValue = if (checked) accent else toggleOff,
            animationSpec = tween(200),
            label = "toggleBg"
        )
        val thumbOffset by animateFloatAsState(
            targetValue = if (checked) 1f else 0f,
            animationSpec = tween(200),
            label = "thumbOffset"
        )
        Box(
            modifier = Modifier
                .width(46.dp)
                .height(28.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(toggleBg)
                .clickable { onCheckedChange(!checked) }
                .padding(3.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .offset(x = (18 * thumbOffset).dp)
                    .shadow(4.dp, CircleShape)
                    .clip(CircleShape)
                    .background(Color.White)
            )
        }
    }
}

// ── Gestures Tab ──

@Composable
private fun SettingsGesturesTab(
    isDark: Boolean,
    surface1: Color,
    accent: Color,
    accentSoft: Color,
    text1: Color,
    text2: Color,
    text3: Color,
    border: Color
) {
    val cardShape = RoundedCornerShape(16.dp)

    gestureData.forEach { (sectionKey, entries) ->
        val fingerCount = sectionKey.first().digitToIntOrNull() ?: 1
        val sectionLabel = when (fingerCount) {
            1 -> "One finger"
            2 -> "Two fingers"
            3 -> "Three fingers"
            4 -> "Four fingers"
            else -> "$fingerCount fingers"
        }

        // Section header with finger dots
        Row(
            modifier = Modifier.padding(top = 8.dp, bottom = 8.dp, start = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                repeat(fingerCount) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .clip(CircleShape)
                            .background(accent)
                    )
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = sectionLabel,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = text2,
                letterSpacing = 0.3.sp
            )
        }

        if (fingerCount == 3 && entries.isEmpty()) {
            // "No gestures assigned" placeholder
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(cardShape)
                    .background(surface1)
                    .border(0.5.dp, border, cardShape)
                    .padding(vertical = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Text("No gestures assigned", fontSize = 13.sp, color = text3)
            }
        } else {
            // 2-column grid of gesture cards
            val chunked = entries.chunked(2)
            chunked.forEach { rowEntries ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowEntries.forEach { entry ->
                        val isWide = entry.gesture.contains("hold")
                        Box(
                            modifier = Modifier
                                .then(if (isWide && rowEntries.size == 1) Modifier.fillMaxWidth() else Modifier.weight(1f))
                                .padding(bottom = 8.dp)
                                .clip(cardShape)
                                .background(surface1)
                                .border(0.5.dp, border, cardShape)
                                .padding(12.dp)
                        ) {
                            if (isWide) {
                                // Wide card: row layout (icon left, text right)
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(46.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(accentSoft),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = gestureIcon(entry.gesture, entry.action),
                                            contentDescription = null,
                                            tint = accent,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Text(
                                            text = entry.gesture,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = text1,
                                            lineHeight = 17.sp
                                        )
                                        Text(
                                            text = entry.action,
                                            fontSize = 11.sp,
                                            color = text3,
                                            lineHeight = 14.sp
                                        )
                                    }
                                }
                            } else {
                                // Normal card: column layout
                                Column {
                                    Box(
                                        modifier = Modifier
                                            .size(44.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(accentSoft),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = gestureIcon(entry.gesture, entry.action),
                                            contentDescription = null,
                                            tint = accent,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = entry.gesture,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = text1
                                    )
                                    Text(
                                        text = entry.action,
                                        fontSize = 11.sp,
                                        color = text3
                                    )
                                }
                            }
                        }
                    }
                    // Fill empty space if odd number
                    if (rowEntries.size == 1 && !rowEntries[0].gesture.contains("hold")) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}

// ── More Tab ──

@SuppressLint("MissingPermission")
@Composable
private fun SettingsMoreTab(
    bondedDevices: List<BluetoothDevice>,
    isConnected: Boolean,
    connectedDeviceAddress: String?,
    deviceNicknames: Map<String, String>,
    onOpenDeviceManager: () -> Unit,
    onNavigateToPairingGuide: () -> Unit,
    appVersion: String,
    isDark: Boolean,
    surface1: Color,
    accent: Color,
    accentLight: Color,
    green: Color,
    text1: Color,
    text2: Color,
    text3: Color,
    border: Color,
    success: Color,
    successSoft: Color,
    devBg: Color,
    devConn: Color,
    dashedColor: Color
) {
    val context = LocalContext.current
    val cardShape = RoundedCornerShape(16.dp)

    // ── Devices section ──
    Text(
        "DEVICES",
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = text3,
        letterSpacing = 0.6.sp,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(surface1)
            .border(0.5.dp, border, cardShape)
    ) {
        if (bondedDevices.isEmpty()) {
            Text(
                "No paired devices",
                fontSize = 13.sp,
                color = text2,
                modifier = Modifier.padding(16.dp)
            )
        } else {
            bondedDevices.take(3).forEachIndexed { index, device ->
                val nickname = deviceNicknames[device.address]
                val displayName = nickname ?: device.name ?: device.address
                val isThisConnected = device.address == connectedDeviceAddress
                if (index > 0) Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(border))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .then(if (isThisConnected) Modifier.background(devConn) else Modifier)
                        .clickable { onOpenDeviceManager() }
                        .padding(horizontal = 14.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Device icon
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isThisConnected) successSoft else devBg),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Computer,
                            contentDescription = null,
                            tint = if (isThisConnected) success else text3,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            displayName,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = text1,
                            lineHeight = 18.sp
                        )
                        Text(
                            if (isThisConnected) "Connected" else "Paired",
                            fontSize = 12.sp,
                            color = if (isThisConnected) success else text3,
                            lineHeight = 15.sp
                        )
                    }
                    // Chevron
                    Icon(
                        imageVector = Icons.Outlined.ChevronRight,
                        contentDescription = null,
                        tint = text3,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }

    // Add new device button - OUTSIDE the card, dashed border
    Spacer(modifier = Modifier.height(10.dp))
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .drawBehind {
                drawRoundRect(
                    color = dashedColor,
                    cornerRadius = CornerRadius(16.dp.toPx()),
                    style = Stroke(
                        width = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8.dp.toPx(), 4.dp.toPx()))
                    )
                )
            }
            .clickable { onNavigateToPairingGuide() }
            .padding(14.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
                tint = accentLight,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                "Add new device",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = accentLight
            )
        }
    }

    Spacer(modifier = Modifier.height(20.dp))

    // ── About section ──
    Text(
        "ABOUT",
        fontSize = 11.sp,
        fontWeight = FontWeight.SemiBold,
        color = text3,
        letterSpacing = 0.6.sp,
        modifier = Modifier.padding(start = 4.dp, bottom = 8.dp)
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(cardShape)
            .background(surface1)
            .border(0.5.dp, border, cardShape)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Version", fontSize = 14.sp, color = text2)
            Text(appVersion, fontSize = 14.sp, color = text3)
        }

        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(border))

        // Send feedback link row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                        data = Uri.parse("mailto:iammd.uzair@gmail.com")
                        putExtra(Intent.EXTRA_SUBJECT, "PhonePad Feedback")
                    }
                    try { context.startActivity(intent) } catch (_: Exception) {}
                }
                .padding(horizontal = 15.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Send feedback", color = accentLight, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = text3,
                modifier = Modifier.size(16.dp)
            )
        }

        Box(modifier = Modifier.fillMaxWidth().height(0.5.dp).background(border))

        // Rate PhonePad link row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=${context.packageName}"))
                        context.startActivity(intent)
                    } catch (_: Exception) {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=${context.packageName}"))
                            context.startActivity(intent)
                        } catch (_: Exception) {}
                    }
                }
                .padding(horizontal = 15.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Rate PhonePad", color = accentLight, fontSize = 14.sp, fontWeight = FontWeight.Medium)
            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = text3,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Phase 4.1 — Device Manager
// ──────────────────────────────────────────────────────────────────────────────

@SuppressLint("MissingPermission")
@Composable
private fun DeviceManagerOverlay(
    bondedDevices: List<BluetoothDevice>,
    connectedDeviceAddress: String?,
    deviceNicknames: Map<String, String>,
    onRenameDevice: (String, String) -> Unit,
    onForgetDevice: (BluetoothDevice) -> Unit,
    onDeviceSelected: (BluetoothDevice) -> Unit,
    onNavigateToPairingGuide: () -> Unit,
    onDismiss: () -> Unit
) {
    var expandedDevice by remember { mutableStateOf<String?>(null) }
    var editingNickname by remember { mutableStateOf<String?>(null) }
    var nicknameText by remember { mutableStateOf("") }
    var confirmForget by remember { mutableStateOf<BluetoothDevice?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xF0121218))
            .clickable(enabled = false) {}
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Device Manager", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color.White)
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Filled.Close, contentDescription = "Close", tint = Color.White)
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            if (bondedDevices.isEmpty()) {
                Text("No paired devices", fontSize = 14.sp, color = Color.White.copy(alpha = 0.5f))
            } else {
                bondedDevices.forEach { device ->
                    val addr = device.address
                    val nickname = deviceNicknames[addr]
                    val displayName = nickname ?: device.name ?: addr
                    val isThisConnected = addr == connectedDeviceAddress
                    val isExpanded = expandedDevice == addr

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(
                                if (isExpanded) Color.White.copy(alpha = 0.04f) else Color.Transparent,
                                RoundedCornerShape(12.dp)
                            )
                            .clickable { expandedDevice = if (isExpanded) null else addr }
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (isThisConnected) Color(0xFF0D9488) else Color(0xFF555555))
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(displayName, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = Color.White)
                                if (nickname != null) {
                                    Text(device.name ?: addr, fontSize = 11.sp, color = Color.White.copy(alpha = 0.35f))
                                }
                            }
                            if (isThisConnected) {
                                Text("Connected", fontSize = 11.sp, color = Color(0xFF0D9488))
                            }
                            Icon(
                                imageVector = if (isExpanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.4f),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        AnimatedVisibility(visible = isExpanded) {
                            Column(modifier = Modifier.padding(top = 12.dp, start = 22.dp)) {
                                Text("Address: $addr", fontSize = 11.sp, color = Color.White.copy(alpha = 0.3f))
                                Spacer(modifier = Modifier.height(12.dp))

                                if (!isThisConnected) {
                                    TextButton(onClick = { onDeviceSelected(device) }) {
                                        Text("Connect", color = Color(0xFF7B7FD4), fontSize = 13.sp)
                                    }
                                }

                                TextButton(onClick = {
                                    editingNickname = addr
                                    nicknameText = nickname ?: device.name ?: ""
                                }) {
                                    Text("Rename", color = Color(0xFF7B7FD4), fontSize = 13.sp)
                                }

                                TextButton(onClick = { confirmForget = device }) {
                                    Text("Forget Device", color = Color(0xFFDC2626), fontSize = 13.sp)
                                }
                            }
                        }
                    }

                    HorizontalDivider(color = Color.White.copy(alpha = 0.05f))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onNavigateToPairingGuide,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B2D6E)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("+ Add New Device", fontSize = 14.sp)
            }
        }

        // Rename dialog
        if (editingNickname != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable { editingNickname = null },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .background(Color(0xFF1C1C24), RoundedCornerShape(16.dp))
                        .clickable(enabled = false) {}
                        .padding(24.dp)
                ) {
                    Text("Rename Device", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(16.dp))
                    androidx.compose.material3.OutlinedTextField(
                        value = nicknameText,
                        onValueChange = { nicknameText = it },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Nickname") }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        TextButton(onClick = { editingNickname = null }) {
                            Text("Cancel", color = Color.White.copy(alpha = 0.5f))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                val addr = editingNickname!!
                                if (nicknameText.isNotBlank()) {
                                    onRenameDevice(addr, nicknameText.trim())
                                }
                                editingNickname = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B2D6E))
                        ) {
                            Text("Save")
                        }
                    }
                }
            }
        }

        // Forget confirmation dialog
        if (confirmForget != null) {
            val device = confirmForget!!
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.6f))
                    .clickable { confirmForget = null },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .background(Color(0xFF1C1C24), RoundedCornerShape(16.dp))
                        .clickable(enabled = false) {}
                        .padding(24.dp)
                ) {
                    Text("Forget Device?", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "This will unpair ${device.name ?: device.address} and remove all its settings. You'll need to pair again to use it.",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Row(horizontalArrangement = Arrangement.End, modifier = Modifier.fillMaxWidth()) {
                        TextButton(onClick = { confirmForget = null }) {
                            Text("Cancel", color = Color.White.copy(alpha = 0.5f))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                onForgetDevice(device)
                                confirmForget = null
                                expandedDevice = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                        ) {
                            Text("Forget")
                        }
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Phase 5 — Error States & Edge Cases
// ──────────────────────────────────────────────────────────────────────────────

@Composable
private fun BluetoothOffOverlay(onTurnOn: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xF0121218)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Icon(
                imageVector = Icons.Filled.BluetoothDisabled,
                contentDescription = null,
                tint = Color.White.copy(alpha = 0.3f),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Bluetooth is off",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "PhonePad needs Bluetooth to connect to your computer.",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onTurnOn,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B2D6E)),
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text("Turn on Bluetooth", fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun PermissionRevokedOverlay(onGrantPermission: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xF0121218)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(32.dp)) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = null,
                tint = Color(0xFFD97706),
                modifier = Modifier.size(56.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "Bluetooth permission was removed",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "PhonePad needs Bluetooth permission to work.",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(32.dp))
            Button(
                onClick = onGrantPermission,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B2D6E)),
                modifier = Modifier.fillMaxWidth(0.7f)
            ) {
                Text("Grant Permission", fontSize = 14.sp)
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
private fun DisconnectErrorSheet(
    lastDeviceName: String?,
    onRetry: () -> Unit,
    onPairDifferent: () -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable { onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color(0xFF1C1C24), RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .clickable(enabled = false) {}
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.3f))
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Can't reach ${lastDeviceName ?: "device"}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Check that Bluetooth is on and the computer is awake.",
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2B2D6E)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Retry", fontSize = 14.sp)
            }
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(
                onClick = onPairDifferent,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Pair a different device", color = Color(0xFF7B7FD4), fontSize = 13.sp)
            }
        }
    }
}

@SuppressLint("MissingPermission")
@Composable
private fun ReconnectSheet(
    connectedHostName: String?,
    bondedDevices: List<BluetoothDevice>,
    onDeviceSelected: (BluetoothDevice) -> Unit,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.6f))
            .clickable { onDismiss() }
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(Color(0xFF1C1C24), RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                .clickable(enabled = false) {}
                .padding(24.dp)
        ) {
            Box(
                modifier = Modifier
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(Color.White.copy(alpha = 0.3f))
                    .align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(20.dp))

            val searchName = connectedHostName ?: "device"
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp,
                    color = Color(0xFFD97706)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text("Looking for $searchName...", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }

            if (bondedDevices.isNotEmpty()) {
                Spacer(modifier = Modifier.height(20.dp))
                Text("Or connect to a different device:", color = Color.White.copy(alpha = 0.6f), fontSize = 13.sp)
                Spacer(modifier = Modifier.height(8.dp))
                bondedDevices.forEach { device ->
                    TextButton(onClick = { onDeviceSelected(device) }, modifier = Modifier.fillMaxWidth()) {
                        Text(device.name ?: device.address, color = Color(0xFF7B7FD4), fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = onDismiss, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text("Cancel", color = Color.White.copy(alpha = 0.5f))
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Phase 3 — Gesture Guide Bottom Sheet
// ──────────────────────────────────────────────────────────────────────────────

private data class GestureEntry(
    val gesture: String,
    val action: String,
    val illustrationType: String
)

private val gestureData = mapOf(
    "1-finger" to listOf(
        GestureEntry("Drag", "Move cursor", "arrow_trail"),
        GestureEntry("Tap", "Left click", "tap_ripple"),
        GestureEntry("Tap + hold + drag", "Click and drag", "press_trail"),
    ),
    "2-finger" to listOf(
        GestureEntry("Drag vertical", "Scroll up / down", "two_dots_vertical"),
        GestureEntry("Drag horizontal", "Scroll left / right", "two_dots_horizontal"),
        GestureEntry("Tap", "Right click", "two_dots_tap"),
        GestureEntry("Pinch", "Zoom in / out", "pinch"),
    ),
    "3-finger" to listOf(
        GestureEntry("Tap", "Middle click", "three_dots_tap"),
        GestureEntry("Swipe up", "Task View", "three_dots_up"),
        GestureEntry("Swipe down", "Show Desktop", "three_dots_down"),
        GestureEntry("Swipe left / right", "Switch apps", "three_dots_side"),
    ),
    "4-finger" to listOf(
        GestureEntry("Tap", "Notifications", "four_dots_tap"),
        GestureEntry("Swipe up", "Task View", "four_dots_up"),
        GestureEntry("Swipe down", "Show Desktop", "four_dots_down"),
        GestureEntry("Swipe left / right", "Switch desktop", "four_dots_side"),
    )
)


private fun gestureIcon(name: String, action: String = ""): ImageVector = when {
    name == "Drag" -> Icons.Outlined.East
    name == "Tap" && action.contains("Notif", ignoreCase = true) -> Icons.Outlined.Notifications
    name == "Tap" -> Icons.Outlined.Adjust
    name.contains("hold") -> Icons.Outlined.OpenWith
    name == "Drag vertical" -> Icons.Outlined.SwapVert
    name == "Drag horizontal" -> Icons.Outlined.SwapHoriz
    name.contains("Pinch") -> Icons.Outlined.ZoomIn
    name == "Swipe up" -> Icons.Outlined.ArrowUpward
    name == "Swipe down" -> Icons.Outlined.ArrowDownward
    name.contains("Swipe left") -> Icons.Outlined.SwapHoriz
    else -> Icons.Outlined.TouchApp
}

@Composable
private fun GestureIllustration(type: String, dotCount: Int, accentColor: Color = Color(0xFF2DD4BF), surfaceBg: Color = Color(0xFF0E0E14), modifier: Modifier = Modifier) {
    val teal = accentColor
    val surfaceColor = surfaceBg

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val padW = w * 0.1f
        val padH = h * 0.1f

        // Rounded-rectangle surface
        drawRoundRect(
            color = surfaceColor,
            topLeft = Offset(padW, padH),
            size = Size(w - padW * 2, h - padH * 2),
            cornerRadius = CornerRadius(6f, 6f)
        )

        val cx = w / 2f
        val cy = h / 2f
        val dotRadius = w * 0.055f
        val spacing = w * 0.12f

        when {
            type.contains("tap_ripple") || type.contains("press_trail") -> {
                drawCircle(teal, dotRadius * 1.2f, Offset(cx, cy))
                drawCircle(teal.copy(alpha = 0.3f), dotRadius * 2.5f, Offset(cx, cy), style = Stroke(1.5f))
            }
            type.contains("arrow_trail") -> {
                drawCircle(teal, dotRadius * 1.1f, Offset(cx - spacing, cy))
                val path = Path().apply {
                    moveTo(cx - spacing * 0.5f, cy)
                    lineTo(cx + spacing * 1.2f, cy)
                }
                drawPath(path, teal.copy(alpha = 0.5f), style = Stroke(1.5f, cap = StrokeCap.Round))
                // arrowhead
                drawLine(teal.copy(alpha = 0.5f), Offset(cx + spacing * 0.9f, cy - spacing * 0.3f), Offset(cx + spacing * 1.2f, cy), strokeWidth = 1.5f, cap = StrokeCap.Round)
                drawLine(teal.copy(alpha = 0.5f), Offset(cx + spacing * 0.9f, cy + spacing * 0.3f), Offset(cx + spacing * 1.2f, cy), strokeWidth = 1.5f, cap = StrokeCap.Round)
            }
            type.contains("two_dots_vertical") -> {
                drawCircle(teal, dotRadius, Offset(cx - spacing * 0.5f, cy))
                drawCircle(teal, dotRadius, Offset(cx + spacing * 0.5f, cy))
                drawLine(teal.copy(alpha = 0.4f), Offset(cx, cy - spacing * 1.2f), Offset(cx, cy + spacing * 1.2f), strokeWidth = 1.5f, cap = StrokeCap.Round)
                // arrows
                drawLine(teal.copy(alpha = 0.4f), Offset(cx - spacing * 0.3f, cy - spacing * 0.8f), Offset(cx, cy - spacing * 1.2f), strokeWidth = 1.5f, cap = StrokeCap.Round)
                drawLine(teal.copy(alpha = 0.4f), Offset(cx + spacing * 0.3f, cy - spacing * 0.8f), Offset(cx, cy - spacing * 1.2f), strokeWidth = 1.5f, cap = StrokeCap.Round)
                drawLine(teal.copy(alpha = 0.4f), Offset(cx - spacing * 0.3f, cy + spacing * 0.8f), Offset(cx, cy + spacing * 1.2f), strokeWidth = 1.5f, cap = StrokeCap.Round)
                drawLine(teal.copy(alpha = 0.4f), Offset(cx + spacing * 0.3f, cy + spacing * 0.8f), Offset(cx, cy + spacing * 1.2f), strokeWidth = 1.5f, cap = StrokeCap.Round)
            }
            type.contains("two_dots_horizontal") -> {
                drawCircle(teal, dotRadius, Offset(cx, cy - spacing * 0.4f))
                drawCircle(teal, dotRadius, Offset(cx, cy + spacing * 0.4f))
                drawLine(teal.copy(alpha = 0.4f), Offset(cx - spacing * 1.2f, cy), Offset(cx + spacing * 1.2f, cy), strokeWidth = 1.5f, cap = StrokeCap.Round)
            }
            type.contains("two_dots_tap") -> {
                drawCircle(teal, dotRadius, Offset(cx - spacing * 0.5f, cy))
                drawCircle(teal, dotRadius, Offset(cx + spacing * 0.5f, cy))
                drawCircle(teal.copy(alpha = 0.25f), dotRadius * 2.2f, Offset(cx, cy), style = Stroke(1.5f))
            }
            type.contains("pinch") -> {
                drawCircle(teal, dotRadius, Offset(cx - spacing * 0.8f, cy))
                drawCircle(teal, dotRadius, Offset(cx + spacing * 0.8f, cy))
                // arrows pointing outward
                drawLine(teal.copy(alpha = 0.4f), Offset(cx - spacing * 0.3f, cy), Offset(cx - spacing * 1.3f, cy), strokeWidth = 1.5f, cap = StrokeCap.Round)
                drawLine(teal.copy(alpha = 0.4f), Offset(cx + spacing * 0.3f, cy), Offset(cx + spacing * 1.3f, cy), strokeWidth = 1.5f, cap = StrokeCap.Round)
            }
            type.contains("three_dots_tap") || type.contains("four_dots_tap") -> {
                val count = if (type.contains("four")) 4 else 3
                val totalW = (count - 1) * spacing
                val startX = cx - totalW / 2f
                for (i in 0 until count) {
                    drawCircle(teal, dotRadius, Offset(startX + i * spacing, cy))
                }
                drawCircle(teal.copy(alpha = 0.25f), dotRadius * 2.5f, Offset(cx, cy), style = Stroke(1.5f))
            }
            type.contains("three_dots_up") || type.contains("four_dots_up") -> {
                val count = if (type.contains("four")) 4 else 3
                val totalW = (count - 1) * spacing
                val startX = cx - totalW / 2f
                for (i in 0 until count) {
                    drawCircle(teal, dotRadius, Offset(startX + i * spacing, cy + spacing * 0.3f))
                }
                drawLine(teal.copy(alpha = 0.4f), Offset(cx, cy + spacing * 0.3f), Offset(cx, cy - spacing * 1.0f), strokeWidth = 1.5f, cap = StrokeCap.Round)
                drawLine(teal.copy(alpha = 0.4f), Offset(cx - spacing * 0.3f, cy - spacing * 0.6f), Offset(cx, cy - spacing * 1.0f), strokeWidth = 1.5f, cap = StrokeCap.Round)
                drawLine(teal.copy(alpha = 0.4f), Offset(cx + spacing * 0.3f, cy - spacing * 0.6f), Offset(cx, cy - spacing * 1.0f), strokeWidth = 1.5f, cap = StrokeCap.Round)
            }
            type.contains("three_dots_down") || type.contains("four_dots_down") -> {
                val count = if (type.contains("four")) 4 else 3
                val totalW = (count - 1) * spacing
                val startX = cx - totalW / 2f
                for (i in 0 until count) {
                    drawCircle(teal, dotRadius, Offset(startX + i * spacing, cy - spacing * 0.3f))
                }
                drawLine(teal.copy(alpha = 0.4f), Offset(cx, cy - spacing * 0.3f), Offset(cx, cy + spacing * 1.0f), strokeWidth = 1.5f, cap = StrokeCap.Round)
                drawLine(teal.copy(alpha = 0.4f), Offset(cx - spacing * 0.3f, cy + spacing * 0.6f), Offset(cx, cy + spacing * 1.0f), strokeWidth = 1.5f, cap = StrokeCap.Round)
                drawLine(teal.copy(alpha = 0.4f), Offset(cx + spacing * 0.3f, cy + spacing * 0.6f), Offset(cx, cy + spacing * 1.0f), strokeWidth = 1.5f, cap = StrokeCap.Round)
            }
            type.contains("three_dots_side") || type.contains("four_dots_side") -> {
                val count = if (type.contains("four")) 4 else 3
                val totalW = (count - 1) * spacing
                val startX = cx - totalW / 2f
                for (i in 0 until count) {
                    drawCircle(teal, dotRadius, Offset(startX + i * spacing, cy))
                }
                // side arrows
                drawLine(teal.copy(alpha = 0.4f), Offset(cx - spacing * 1.5f, cy), Offset(cx - spacing * 2.2f, cy), strokeWidth = 1.5f, cap = StrokeCap.Round)
                drawLine(teal.copy(alpha = 0.4f), Offset(cx + spacing * 1.5f, cy), Offset(cx + spacing * 2.2f, cy), strokeWidth = 1.5f, cap = StrokeCap.Round)
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Keyboard Layout Engine (Milestone 2.0 + 2.2 + 2.3)
// ──────────────────────────────────────────────────────────────────────────────

enum class ShiftState { OFF, SHIFTED, CAPS_LOCK }

enum class KeyType { CHAR, MODIFIER, SHIFT, ACTION, CONSUMER, FN }

data class KeyDef(
    val label: String,
    val shiftLabel: String = label.uppercase(),
    val hid: Byte = 0,
    val widthUnits: Float = 1.0f,
    val type: KeyType = KeyType.CHAR,
    val modByte: Byte = 0,
    val actionId: String = "",
    val consumerCode: Int = 0,
    val fnLabel: String = "",
    val fnHid: Byte = 0,
    val icon: String = "",
    val isMediaRow: Boolean = false,
    val altLabel: String = "",
    val altHid: Byte = 0,
    val autoShift: Boolean = false
)

data class KeyboardLayout(
    val rows: List<List<KeyDef>>,
    val keyGapDp: Float = 2f,
    val rowGapDp: Float = 3f
)

object KeyboardLayouts {
    private val S = KeyboardReportSender

    // Media strip shared by both compact layouts
    private val COMPACT_MEDIA_ROW = listOf(
        KeyDef("Esc", "Esc", S.KEY_ESCAPE, 1.0f, KeyType.CHAR, isMediaRow = true),
        KeyDef("", "", 0, 1.0f, KeyType.CONSUMER, consumerCode = 0x0070, icon = "bright_down", fnLabel = "F1", fnHid = S.KEY_F1, isMediaRow = true),
        KeyDef("", "", 0, 1.0f, KeyType.CONSUMER, consumerCode = 0x006F, icon = "bright_up", fnLabel = "F2", fnHid = S.KEY_F2, isMediaRow = true),
        KeyDef("", "", 0, 1.0f, KeyType.CONSUMER, consumerCode = 0x00E2, icon = "mute", fnLabel = "F3", fnHid = S.KEY_F3, isMediaRow = true),
        KeyDef("", "", 0, 1.0f, KeyType.CONSUMER, consumerCode = 0x00EA, icon = "vol_down", fnLabel = "F4", fnHid = S.KEY_F4, isMediaRow = true),
        KeyDef("", "", 0, 1.0f, KeyType.CONSUMER, consumerCode = 0x00E9, icon = "vol_up", fnLabel = "F5", fnHid = S.KEY_F5, isMediaRow = true),
        KeyDef("", "", 0, 1.0f, KeyType.CONSUMER, consumerCode = 0x00B6, icon = "skip_back", fnLabel = "F6", fnHid = S.KEY_F6, isMediaRow = true),
        KeyDef("", "", 0, 1.0f, KeyType.CONSUMER, consumerCode = 0x00CD, icon = "play_pause", fnLabel = "F7", fnHid = S.KEY_F7, isMediaRow = true),
        KeyDef("", "", 0, 1.0f, KeyType.CONSUMER, consumerCode = 0x00B5, icon = "skip_fwd", fnLabel = "F8", fnHid = S.KEY_F8, isMediaRow = true)
    )

    val COMPACT_QWERTY = KeyboardLayout(
        rows = listOf(
            // Row 0: Media strip
            COMPACT_MEDIA_ROW,
            // Row 1: QWERTY (long-press = numbers Q=1..P=0)
            listOf(
                KeyDef("q", "Q", S.KEY_Q, altLabel = "1", altHid = S.KEY_1),
                KeyDef("w", "W", S.KEY_W, altLabel = "2", altHid = S.KEY_2),
                KeyDef("e", "E", S.KEY_E, altLabel = "3", altHid = S.KEY_3),
                KeyDef("r", "R", S.KEY_R, altLabel = "4", altHid = S.KEY_4),
                KeyDef("t", "T", S.KEY_T, altLabel = "5", altHid = S.KEY_5),
                KeyDef("y", "Y", S.KEY_Y, altLabel = "6", altHid = S.KEY_6),
                KeyDef("u", "U", S.KEY_U, altLabel = "7", altHid = S.KEY_7),
                KeyDef("i", "I", S.KEY_I, altLabel = "8", altHid = S.KEY_8),
                KeyDef("o", "O", S.KEY_O, altLabel = "9", altHid = S.KEY_9),
                KeyDef("p", "P", S.KEY_P, altLabel = "0", altHid = S.KEY_0)
            ),
            // Row 1: ASDF row
            listOf(
                KeyDef("a", "A", S.KEY_A), KeyDef("s", "S", S.KEY_S),
                KeyDef("d", "D", S.KEY_D), KeyDef("f", "F", S.KEY_F),
                KeyDef("g", "G", S.KEY_G), KeyDef("h", "H", S.KEY_H),
                KeyDef("j", "J", S.KEY_J), KeyDef("k", "K", S.KEY_K),
                KeyDef("l", "L", S.KEY_L)
            ),
            // Row 2: ZXCV row
            listOf(
                KeyDef("⇧", "⇧", 0, 1.3f, KeyType.SHIFT),
                KeyDef("z", "Z", S.KEY_Z), KeyDef("x", "X", S.KEY_X),
                KeyDef("c", "C", S.KEY_C), KeyDef("v", "V", S.KEY_V),
                KeyDef("b", "B", S.KEY_B), KeyDef("n", "N", S.KEY_N),
                KeyDef("m", "M", S.KEY_M),
                KeyDef("⌫", "⌫", S.KEY_BACKSPACE, 1.3f, KeyType.CHAR)
            ),
            // Row 3: Bottom modifiers
            listOf(
                KeyDef("Fn", "Fn", 0, 1.2f, KeyType.FN),
                KeyDef("Ctrl", "Ctrl", 0, 1.2f, KeyType.MODIFIER, S.MOD_LCTRL),
                KeyDef("Alt", "Alt", 0, 1.0f, KeyType.MODIFIER, S.MOD_LALT),
                KeyDef("", "", S.KEY_SPACE, 3.0f, KeyType.CHAR),
                KeyDef(".", ">", S.KEY_PERIOD, 1.0f),
                KeyDef("Enter", "Enter", S.KEY_ENTER, 1.6f, KeyType.CHAR)
            )
        ),
        keyGapDp = 1.5f,
        rowGapDp = 2f
    )

    // Symbol layer for compact mode: same row structure, different labels
    val COMPACT_SYMBOLS = KeyboardLayout(
        rows = listOf(
            // Row 0: Media strip (same)
            COMPACT_MEDIA_ROW,
            // Row 1: Symbols (shift+number)
            listOf(
                KeyDef("!", "!", S.KEY_1, autoShift = true),
                KeyDef("@", "@", S.KEY_2, autoShift = true),
                KeyDef("#", "#", S.KEY_3, autoShift = true),
                KeyDef("$", "$", S.KEY_4, autoShift = true),
                KeyDef("%", "%", S.KEY_5, autoShift = true),
                KeyDef("^", "^", S.KEY_6, autoShift = true),
                KeyDef("&", "&", S.KEY_7, autoShift = true),
                KeyDef("*", "*", S.KEY_8, autoShift = true),
                KeyDef("(", "(", S.KEY_9, autoShift = true),
                KeyDef(")", ")", S.KEY_0, autoShift = true)
            ),
            // Row 1: More symbols
            listOf(
                KeyDef("-", "_", S.KEY_MINUS), KeyDef("=", "+", S.KEY_EQUALS),
                KeyDef("[", "{", S.KEY_LBRACKET), KeyDef("]", "}", S.KEY_RBRACKET),
                KeyDef("\\", "|", S.KEY_BACKSLASH), KeyDef(";", ":", S.KEY_SEMICOLON),
                KeyDef("'", "\"", S.KEY_APOSTROPHE), KeyDef(",", "<", S.KEY_COMMA),
                KeyDef("/", "?", S.KEY_SLASH)
            ),
            // Row 2: Grave, Tab, Esc, arrows + Backspace
            listOf(
                KeyDef("⇧", "⇧", 0, 1.3f, KeyType.SHIFT),
                KeyDef("`", "~", S.KEY_GRAVE), KeyDef("Tab", "Tab", S.KEY_TAB),
                KeyDef("Esc", "Esc", S.KEY_ESCAPE),
                KeyDef("←", "←", S.KEY_ARROW_LEFT), KeyDef("↑", "↑", S.KEY_ARROW_UP),
                KeyDef("↓", "↓", S.KEY_ARROW_DOWN), KeyDef("→", "→", S.KEY_ARROW_RIGHT),
                KeyDef("⌫", "⌫", S.KEY_BACKSPACE, 1.3f, KeyType.CHAR)
            ),
            // Row 3: Same modifiers
            listOf(
                KeyDef("Fn", "Fn", 0, 1.2f, KeyType.FN),
                KeyDef("Ctrl", "Ctrl", 0, 1.2f, KeyType.MODIFIER, S.MOD_LCTRL),
                KeyDef("Alt", "Alt", 0, 1.0f, KeyType.MODIFIER, S.MOD_LALT),
                KeyDef("", "", S.KEY_SPACE, 3.0f, KeyType.CHAR),
                KeyDef(".", ">", S.KEY_PERIOD, 1.0f),
                KeyDef("Enter", "Enter", S.KEY_ENTER, 1.6f, KeyType.CHAR)
            )
        ),
        keyGapDp = 1.5f,
        rowGapDp = 2f
    )

    val FULL_QWERTY = KeyboardLayout(
        rows = listOf(
            // Row 0: Media/Function row (icons, shorter height)
            listOf(
                KeyDef("Esc", "Esc", S.KEY_ESCAPE, 1.0f, KeyType.CHAR, isMediaRow = true),
                KeyDef("", "", 0, 1.0f, KeyType.CONSUMER, consumerCode = 0x0070, icon = "bright_down", fnLabel = "F1", fnHid = S.KEY_F1, isMediaRow = true),
                KeyDef("", "", 0, 1.0f, KeyType.CONSUMER, consumerCode = 0x006F, icon = "bright_up", fnLabel = "F2", fnHid = S.KEY_F2, isMediaRow = true),
                KeyDef("", "", 0, 1.0f, KeyType.CONSUMER, consumerCode = 0x029F, icon = "grid", fnLabel = "F3", fnHid = S.KEY_F3, isMediaRow = true),
                KeyDef("", "", 0, 1.0f, KeyType.CONSUMER, consumerCode = 0x0221, icon = "search", fnLabel = "F4", fnHid = S.KEY_F4, isMediaRow = true),
                KeyDef("", "", 0, 1.0f, KeyType.CONSUMER, consumerCode = 0x019C, icon = "mic", fnLabel = "F5", fnHid = S.KEY_F5, isMediaRow = true),
                KeyDef("", "", 0, 1.0f, KeyType.CONSUMER, consumerCode = 0x00E2, icon = "mute", fnLabel = "F6", fnHid = S.KEY_F6, isMediaRow = true),
                KeyDef("", "", 0, 1.0f, KeyType.CONSUMER, consumerCode = 0x00EA, icon = "vol_down", fnLabel = "F7", fnHid = S.KEY_F7, isMediaRow = true),
                KeyDef("", "", 0, 1.0f, KeyType.CONSUMER, consumerCode = 0x00E9, icon = "vol_up", fnLabel = "F8", fnHid = S.KEY_F8, isMediaRow = true),
                KeyDef("", "", 0, 1.0f, KeyType.CONSUMER, consumerCode = 0x00B6, icon = "skip_back", fnLabel = "F9", fnHid = S.KEY_F9, isMediaRow = true),
                KeyDef("", "", 0, 1.0f, KeyType.CONSUMER, consumerCode = 0x00CD, icon = "play_pause", fnLabel = "F10", fnHid = S.KEY_F10, isMediaRow = true),
                KeyDef("", "", 0, 1.0f, KeyType.CONSUMER, consumerCode = 0x00B5, icon = "skip_fwd", fnLabel = "F11", fnHid = S.KEY_F11, isMediaRow = true),
                KeyDef("Del", "Del", S.KEY_DELETE, 1.0f, KeyType.CHAR, fnLabel = "F12", fnHid = S.KEY_F12, isMediaRow = true)
            ),
            // Row 1: Number row
            listOf(
                KeyDef("`", "~", S.KEY_GRAVE),
                KeyDef("1", "!", S.KEY_1), KeyDef("2", "@", S.KEY_2),
                KeyDef("3", "#", S.KEY_3), KeyDef("4", "$", S.KEY_4),
                KeyDef("5", "%", S.KEY_5), KeyDef("6", "^", S.KEY_6),
                KeyDef("7", "&", S.KEY_7), KeyDef("8", "*", S.KEY_8),
                KeyDef("9", "(", S.KEY_9), KeyDef("0", ")", S.KEY_0),
                KeyDef("-", "_", S.KEY_MINUS), KeyDef("=", "+", S.KEY_EQUALS),
                KeyDef("⌫", "⌫", S.KEY_BACKSPACE, 1.5f, KeyType.CHAR)
            ),
            // Row 2: QWERTY
            listOf(
                KeyDef("Tab", "Tab", S.KEY_TAB, 1.25f, KeyType.CHAR),
                KeyDef("q", "Q", S.KEY_Q), KeyDef("w", "W", S.KEY_W),
                KeyDef("e", "E", S.KEY_E), KeyDef("r", "R", S.KEY_R),
                KeyDef("t", "T", S.KEY_T), KeyDef("y", "Y", S.KEY_Y),
                KeyDef("u", "U", S.KEY_U), KeyDef("i", "I", S.KEY_I),
                KeyDef("o", "O", S.KEY_O), KeyDef("p", "P", S.KEY_P),
                KeyDef("[", "{", S.KEY_LBRACKET), KeyDef("]", "}", S.KEY_RBRACKET),
                KeyDef("\\", "|", S.KEY_BACKSLASH, 1.25f)
            ),
            // Row 3: Home row
            listOf(
                KeyDef("Caps", "Caps", S.KEY_CAPS_LOCK, 1.5f, KeyType.CHAR),
                KeyDef("a", "A", S.KEY_A), KeyDef("s", "S", S.KEY_S),
                KeyDef("d", "D", S.KEY_D), KeyDef("f", "F", S.KEY_F),
                KeyDef("g", "G", S.KEY_G), KeyDef("h", "H", S.KEY_H),
                KeyDef("j", "J", S.KEY_J), KeyDef("k", "K", S.KEY_K),
                KeyDef("l", "L", S.KEY_L), KeyDef(";", ":", S.KEY_SEMICOLON),
                KeyDef("'", "\"", S.KEY_APOSTROPHE),
                KeyDef("Enter", "Enter", S.KEY_ENTER, 1.75f, KeyType.CHAR)
            ),
            // Row 4: Bottom row
            listOf(
                KeyDef("⇧", "⇧", 0, 2.0f, KeyType.SHIFT),
                KeyDef("z", "Z", S.KEY_Z), KeyDef("x", "X", S.KEY_X),
                KeyDef("c", "C", S.KEY_C), KeyDef("v", "V", S.KEY_V),
                KeyDef("b", "B", S.KEY_B), KeyDef("n", "N", S.KEY_N),
                KeyDef("m", "M", S.KEY_M), KeyDef(",", "<", S.KEY_COMMA),
                KeyDef(".", ">", S.KEY_PERIOD), KeyDef("/", "?", S.KEY_SLASH),
                KeyDef("⇧", "⇧", 0, 2.0f, KeyType.SHIFT)
            ),
            // Row 5: Space row
            listOf(
                KeyDef("Fn", "Fn", 0, 1.0f, KeyType.FN),
                KeyDef("Ctrl", "Ctrl", 0, 1.3f, KeyType.MODIFIER, S.MOD_LCTRL),
                KeyDef("Win", "Win", 0, 1.0f, KeyType.ACTION, actionId = "win"),
                KeyDef("Alt", "Alt", 0, 1.2f, KeyType.MODIFIER, S.MOD_LALT),
                KeyDef("", "", S.KEY_SPACE, 5.5f, KeyType.CHAR),
                KeyDef("Alt", "Alt", 0, 1.0f, KeyType.MODIFIER, S.MOD_RALT),
                KeyDef("←", "←", S.KEY_ARROW_LEFT),
                KeyDef("↑", "↑", S.KEY_ARROW_UP),
                KeyDef("↓", "↓", S.KEY_ARROW_DOWN),
                KeyDef("→", "→", S.KEY_ARROW_RIGHT),
                KeyDef("🖱", "🖱", 0, 1.0f, KeyType.ACTION, actionId = "trackpad")
            )
        )
    )
}

// ──────────────────────────────────────────────────────────────────────────────
// Keyboard Rendering Engine (Milestone 2.0–2.3)
// ──────────────────────────────────────────────────────────────────────────────

@Composable
fun MediaKeyIcon(icon: String, tint: Color, size: Float = 14f) {
    val sizeDp = size.dp
    Canvas(modifier = Modifier.size(sizeDp)) {
        val c = this.size / 2f
        val cx = c.width; val cy = c.height
        val s = this.size.minDimension
        val stroke = Stroke(width = 1.4f * (s / 40f), cap = StrokeCap.Round)
        when (icon) {
            "bright_down" -> {
                drawCircle(tint, s * 0.18f, center = Offset(cx, cy), style = stroke)
                val rayLen = s * 0.15f; val gap = s * 0.28f
                for (i in 0 until 8) {
                    val a = Math.toRadians(i * 45.0)
                    drawLine(tint, Offset(cx + (gap * cos(a)).toFloat(), cy + (gap * sin(a)).toFloat()),
                        Offset(cx + ((gap + rayLen) * cos(a)).toFloat(), cy + ((gap + rayLen) * sin(a)).toFloat()), strokeWidth = stroke.width)
                }
            }
            "bright_up" -> {
                drawCircle(tint, s * 0.22f, center = Offset(cx, cy), style = stroke)
                val rayLen = s * 0.18f; val gap = s * 0.32f
                for (i in 0 until 8) {
                    val a = Math.toRadians(i * 45.0)
                    drawLine(tint, Offset(cx + (gap * cos(a)).toFloat(), cy + (gap * sin(a)).toFloat()),
                        Offset(cx + ((gap + rayLen) * cos(a)).toFloat(), cy + ((gap + rayLen) * sin(a)).toFloat()), strokeWidth = stroke.width)
                }
            }
            "grid" -> {
                val d = s * 0.12f; val sp = s * 0.32f
                for (r in -1..1) for (col in -1..1) drawCircle(tint, d, Offset(cx + col * sp, cy + r * sp))
            }
            "search" -> {
                drawCircle(tint, s * 0.22f, Offset(cx - s * 0.05f, cy - s * 0.05f), style = stroke)
                drawLine(tint, Offset(cx + s * 0.12f, cy + s * 0.12f), Offset(cx + s * 0.35f, cy + s * 0.35f), strokeWidth = stroke.width * 1.3f)
            }
            "mic" -> {
                val mw = s * 0.15f; val mh = s * 0.25f
                drawRoundRect(tint, Offset(cx - mw, cy - mh - s * 0.05f), androidx.compose.ui.geometry.Size(mw * 2, mh * 2), CornerRadius(mw), style = stroke)
                drawArc(tint, 0f, 180f, false, Offset(cx - s * 0.22f, cy - s * 0.15f), androidx.compose.ui.geometry.Size(s * 0.44f, s * 0.44f), style = stroke)
                drawLine(tint, Offset(cx, cy + s * 0.3f), Offset(cx, cy + s * 0.42f), strokeWidth = stroke.width)
            }
            "mute" -> {
                val bx = cx - s * 0.15f
                drawRect(tint, Offset(bx - s * 0.08f, cy - s * 0.1f), androidx.compose.ui.geometry.Size(s * 0.16f, s * 0.2f))
                val path = Path().apply {
                    moveTo(bx + s * 0.08f, cy - s * 0.1f); lineTo(bx + s * 0.3f, cy - s * 0.3f)
                    lineTo(bx + s * 0.3f, cy + s * 0.3f); lineTo(bx + s * 0.08f, cy + s * 0.1f); close()
                }
                drawPath(path, tint)
                drawLine(tint, Offset(cx + s * 0.15f, cy - s * 0.15f), Offset(cx + s * 0.35f, cy + s * 0.15f), strokeWidth = stroke.width * 1.2f)
                drawLine(tint, Offset(cx + s * 0.35f, cy - s * 0.15f), Offset(cx + s * 0.15f, cy + s * 0.15f), strokeWidth = stroke.width * 1.2f)
            }
            "vol_down" -> {
                val bx = cx - s * 0.1f
                drawRect(tint, Offset(bx - s * 0.08f, cy - s * 0.1f), androidx.compose.ui.geometry.Size(s * 0.16f, s * 0.2f))
                val path = Path().apply {
                    moveTo(bx + s * 0.08f, cy - s * 0.1f); lineTo(bx + s * 0.25f, cy - s * 0.25f)
                    lineTo(bx + s * 0.25f, cy + s * 0.25f); lineTo(bx + s * 0.08f, cy + s * 0.1f); close()
                }
                drawPath(path, tint)
                drawArc(tint, -40f, 80f, false, Offset(cx + s * 0.05f, cy - s * 0.15f), androidx.compose.ui.geometry.Size(s * 0.2f, s * 0.3f), style = stroke)
            }
            "vol_up" -> {
                val bx = cx - s * 0.15f
                drawRect(tint, Offset(bx - s * 0.08f, cy - s * 0.1f), androidx.compose.ui.geometry.Size(s * 0.16f, s * 0.2f))
                val path = Path().apply {
                    moveTo(bx + s * 0.08f, cy - s * 0.1f); lineTo(bx + s * 0.25f, cy - s * 0.25f)
                    lineTo(bx + s * 0.25f, cy + s * 0.25f); lineTo(bx + s * 0.08f, cy + s * 0.1f); close()
                }
                drawPath(path, tint)
                drawArc(tint, -40f, 80f, false, Offset(cx + s * 0.02f, cy - s * 0.15f), androidx.compose.ui.geometry.Size(s * 0.2f, s * 0.3f), style = stroke)
                drawArc(tint, -45f, 90f, false, Offset(cx + s * 0.08f, cy - s * 0.25f), androidx.compose.ui.geometry.Size(s * 0.3f, s * 0.5f), style = stroke)
            }
            "skip_back" -> {
                drawLine(tint, Offset(cx - s * 0.3f, cy - s * 0.2f), Offset(cx - s * 0.3f, cy + s * 0.2f), strokeWidth = stroke.width * 1.2f)
                val p = Path().apply { moveTo(cx - s * 0.2f, cy); lineTo(cx + s * 0.05f, cy - s * 0.2f); lineTo(cx + s * 0.05f, cy + s * 0.2f); close() }
                drawPath(p, tint)
                val p2 = Path().apply { moveTo(cx + s * 0.05f, cy); lineTo(cx + s * 0.3f, cy - s * 0.2f); lineTo(cx + s * 0.3f, cy + s * 0.2f); close() }
                drawPath(p2, tint)
            }
            "play_pause" -> {
                val p = Path().apply { moveTo(cx - s * 0.2f, cy - s * 0.22f); lineTo(cx + s * 0.05f, cy); lineTo(cx - s * 0.2f, cy + s * 0.22f); close() }
                drawPath(p, tint)
                drawLine(tint, Offset(cx + s * 0.15f, cy - s * 0.2f), Offset(cx + s * 0.15f, cy + s * 0.2f), strokeWidth = stroke.width * 1.5f)
                drawLine(tint, Offset(cx + s * 0.28f, cy - s * 0.2f), Offset(cx + s * 0.28f, cy + s * 0.2f), strokeWidth = stroke.width * 1.5f)
            }
            "skip_fwd" -> {
                val p = Path().apply { moveTo(cx - s * 0.3f, cy - s * 0.2f); lineTo(cx - s * 0.05f, cy); lineTo(cx - s * 0.3f, cy + s * 0.2f); close() }
                drawPath(p, tint)
                val p2 = Path().apply { moveTo(cx - s * 0.05f, cy - s * 0.2f); lineTo(cx + s * 0.2f, cy); lineTo(cx - s * 0.05f, cy + s * 0.2f); close() }
                drawPath(p2, tint)
                drawLine(tint, Offset(cx + s * 0.3f, cy - s * 0.2f), Offset(cx + s * 0.3f, cy + s * 0.2f), strokeWidth = stroke.width * 1.2f)
            }
        }
    }
}

@Composable
fun KeyboardScreen(
    keyboardEngine: KeyboardReportSender,
    onSwitchToTrackpad: () -> Unit,
    onConsumerKey: (Int) -> Unit = {},
    onConsumerPress: (Int) -> Unit = {},
    onConsumerRelease: () -> Unit = {},
    doubleSpaceForPeriod: Boolean = true,
    keySoundEnabled: Boolean = false,
    layout: KeyboardLayout = KeyboardLayouts.FULL_QWERTY
) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    val view = LocalView.current
    val handler = remember { Handler(Looper.getMainLooper()) }

    DisposableEffect(Unit) {
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    var shiftState by remember { mutableStateOf(ShiftState.OFF) }
    val heldModifiers = remember { mutableStateListOf<Byte>() }
    val isShifted = shiftState != ShiftState.OFF
    var fnHeld by remember { mutableStateOf(false) }
    var lastSpaceTime by remember { mutableStateOf(0L) }

    val surfaceBg = Color(0xFF08080D)
    val keyGap = layout.keyGapDp.dp
    val rowGap = layout.rowGapDp.dp
    val accent = Color(0xFF7C6AF6)

    fun doHaptic() {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
    }

    fun doSound() {
        if (keySoundEnabled) {
            view.playSoundEffect(SoundEffectConstants.CLICK)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceBg)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 4.dp, vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(rowGap)
        ) {
            for ((rowIdx, row) in layout.rows.withIndex()) {
                val totalUnits = row.sumOf { it.widthUnits.toDouble() }.toFloat()
                val isMediaRow = row.any { it.isMediaRow }
                val rowWeight = if (isMediaRow) 0.7f else 1f

                Row(
                    modifier = Modifier.fillMaxWidth().weight(rowWeight),
                    horizontalArrangement = Arrangement.spacedBy(keyGap)
                ) {
                    for (key in row) {
                        val weight = key.widthUnits / totalUnits
                        val isModActive = key.type == KeyType.MODIFIER && heldModifiers.contains(key.modByte)
                        val isShiftKey = key.type == KeyType.SHIFT
                        val isFnKey = key.type == KeyType.FN
                        val isSpecialKey = key.type != KeyType.CHAR || key.widthUnits > 1.1f
                        val isCapsKey = key.label == "Caps"
                        val isCapsLocked = isCapsKey && shiftState == ShiftState.CAPS_LOCK
                        val isBackspace = key.hid == KeyboardReportSender.KEY_BACKSPACE
                        val isSpace = key.hid == KeyboardReportSender.KEY_SPACE
                        val isConsumer = key.type == KeyType.CONSUMER
                        val isVolumeKey = isConsumer && (key.consumerCode == 0x00E9 || key.consumerCode == 0x00EA)

                        val showFnLabel = fnHeld && key.fnLabel.isNotEmpty()
                        val displayLabel = when {
                            showFnLabel -> key.fnLabel
                            key.type == KeyType.MODIFIER || key.type == KeyType.SHIFT || key.type == KeyType.ACTION || key.type == KeyType.FN -> key.label
                            isConsumer && key.label.isEmpty() -> ""
                            key.label.isEmpty() -> ""
                            isShifted -> key.shiftLabel
                            else -> key.label
                        }

                        var pressed by remember { mutableStateOf(false) }
                        val animScale by animateFloatAsState(
                            targetValue = if (pressed) 0.95f else 1f,
                            animationSpec = if (pressed) tween(15) else spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessMedium),
                            label = "s"
                        )
                        val animAlpha by animateFloatAsState(
                            targetValue = if (pressed) 0.7f else 1f,
                            animationSpec = tween(if (pressed) 15 else 150), label = "a"
                        )

                        val glassBg = Color.White.copy(alpha = 0.08f)
                        val glassBgSpecial = Color.White.copy(alpha = 0.05f)
                        val glassBorder = Color.White.copy(alpha = 0.12f)
                        val accentBorder = accent.copy(alpha = 0.5f)

                        val bg by animateColorAsState(
                            targetValue = when {
                                isShiftKey && shiftState == ShiftState.CAPS_LOCK -> accent.copy(alpha = 0.35f)
                                isShiftKey && shiftState == ShiftState.SHIFTED -> accent.copy(alpha = 0.2f)
                                isModActive -> accent.copy(alpha = 0.25f)
                                isFnKey && fnHeld -> accent.copy(alpha = 0.25f)
                                isSpecialKey || isMediaRow || isConsumer || isFnKey -> glassBgSpecial
                                else -> glassBg
                            },
                            animationSpec = tween(100), label = "bg"
                        )
                        val borderColor = when {
                            isModActive || (isShiftKey && isShifted) || (isFnKey && fnHeld) -> accentBorder
                            else -> glassBorder
                        }

                        Box(
                            modifier = Modifier
                                .weight(weight)
                                .fillMaxHeight()
                                .scale(animScale)
                                .alpha(animAlpha)
                                .clip(RoundedCornerShape(6.dp))
                                .background(bg)
                                .border(0.5.dp, borderColor, RoundedCornerShape(6.dp))
                                .pointerInput(key.label + key.hid + key.actionId + key.modByte + key.consumerCode + key.icon) {
                                    awaitEachGesture {
                                        awaitFirstDown(requireUnconsumed = false).also { it.consume() }
                                        pressed = true
                                        doHaptic()
                                        doSound()

                                        val pressTime = SystemClock.uptimeMillis()
                                        var repeatRunnable: Runnable? = null

                                        when {
                                            key.type == KeyType.SHIFT -> {
                                                shiftState = when (shiftState) {
                                                    ShiftState.OFF -> ShiftState.SHIFTED
                                                    ShiftState.SHIFTED -> ShiftState.CAPS_LOCK
                                                    ShiftState.CAPS_LOCK -> ShiftState.OFF
                                                }
                                                if (shiftState != ShiftState.OFF) keyboardEngine.pressModifier(KeyboardReportSender.MOD_LSHIFT)
                                                else keyboardEngine.releaseModifier(KeyboardReportSender.MOD_LSHIFT)
                                            }
                                            key.type == KeyType.MODIFIER -> {
                                                if (heldModifiers.contains(key.modByte)) {
                                                    heldModifiers.remove(key.modByte)
                                                    keyboardEngine.releaseModifier(key.modByte)
                                                } else {
                                                    heldModifiers.add(key.modByte)
                                                    keyboardEngine.pressModifier(key.modByte)
                                                }
                                            }
                                            key.type == KeyType.FN -> fnHeld = !fnHeld
                                            key.type == KeyType.ACTION -> {
                                                when (key.actionId) {
                                                    "trackpad" -> onSwitchToTrackpad()
                                                    "win" -> {
                                                        keyboardEngine.pressModifier(KeyboardReportSender.MOD_LGUI)
                                                        keyboardEngine.releaseModifier(KeyboardReportSender.MOD_LGUI)
                                                    }
                                                }
                                            }
                                            isConsumer && !fnHeld -> {
                                                onConsumerPress(key.consumerCode)
                                                if (isVolumeKey) {
                                                    val rr = object : Runnable {
                                                        override fun run() {
                                                            onConsumerRelease()
                                                            onConsumerPress(key.consumerCode)
                                                            handler.postDelayed(this, 100)
                                                        }
                                                    }
                                                    repeatRunnable = rr
                                                    handler.postDelayed(rr, 400)
                                                }
                                            }
                                            isConsumer && fnHeld && key.fnHid != 0.toByte() -> {
                                                keyboardEngine.pressKey(key.fnHid)
                                            }
                                            isBackspace -> {
                                                keyboardEngine.pressKey(key.hid)
                                                // Accelerating repeat: 250ms initial, ramp from 50ms to 33ms
                                                val bsRunnable = object : Runnable {
                                                    var count = 0
                                                    override fun run() {
                                                        keyboardEngine.releaseKey(key.hid)
                                                        keyboardEngine.pressKey(key.hid)
                                                        count++
                                                        val interval = when {
                                                            count < 8 -> 250L / (count + 1)  // ~250→31ms over 8 steps
                                                            else -> 33L
                                                        }.coerceIn(33L, 250L)
                                                        handler.postDelayed(this, interval)
                                                    }
                                                }
                                                repeatRunnable = bsRunnable
                                                handler.postDelayed(bsRunnable, 500)
                                            }
                                            isSpace -> {
                                                val now = SystemClock.uptimeMillis()
                                                if (doubleSpaceForPeriod && (now - lastSpaceTime) < 400) {
                                                    keyboardEngine.pressKey(KeyboardReportSender.KEY_BACKSPACE)
                                                    keyboardEngine.releaseKey(KeyboardReportSender.KEY_BACKSPACE)
                                                    keyboardEngine.pressKey(KeyboardReportSender.KEY_PERIOD)
                                                    keyboardEngine.releaseKey(KeyboardReportSender.KEY_PERIOD)
                                                    keyboardEngine.pressKey(KeyboardReportSender.KEY_SPACE)
                                                    lastSpaceTime = 0L
                                                } else {
                                                    keyboardEngine.pressKey(key.hid)
                                                    lastSpaceTime = now
                                                }
                                            }
                                            else -> keyboardEngine.pressKey(key.hid)
                                        }

                                        // Wait for finger lift
                                        do {
                                            val ev = awaitPointerEvent()
                                            ev.changes.forEach { it.consume() }
                                        } while (ev.changes.any { it.pressed })
                                        pressed = false

                                        // Cancel any repeat
                                        repeatRunnable?.let { handler.removeCallbacks(it) }

                                        // Release
                                        when {
                                            key.type == KeyType.SHIFT || key.type == KeyType.MODIFIER || key.type == KeyType.FN || key.type == KeyType.ACTION -> {}
                                            isConsumer && !fnHeld -> onConsumerRelease()
                                            isConsumer && fnHeld && key.fnHid != 0.toByte() -> {
                                                keyboardEngine.releaseKey(key.fnHid)
                                            }
                                            else -> {
                                                keyboardEngine.releaseKey(key.hid)
                                                if (shiftState == ShiftState.SHIFTED) {
                                                    shiftState = ShiftState.OFF
                                                    keyboardEngine.releaseModifier(KeyboardReportSender.MOD_LSHIFT)
                                                }
                                                heldModifiers.forEach { keyboardEngine.releaseModifier(it) }
                                                heldModifiers.clear()
                                            }
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            // Render icon or text
                            if (key.icon.isNotEmpty() && !showFnLabel) {
                                MediaKeyIcon(
                                    icon = key.icon,
                                    tint = Color(0xFF8888A0),
                                    size = if (isMediaRow) 14f else 12f
                                )
                            } else {
                                Text(
                                    text = displayLabel,
                                    color = when {
                                        isModActive || (isShiftKey && isShifted) || (isFnKey && fnHeld) -> Color(0xFFB8A9FB)
                                        isSpecialKey || isMediaRow || isConsumer || isFnKey -> Color(0xFF8888A0)
                                        else -> Color(0xFFE0E0E0)
                                    },
                                    fontSize = when {
                                        isMediaRow || showFnLabel -> 10.sp
                                        key.label.length > 2 -> 10.sp
                                        isSpecialKey -> 11.sp
                                        else -> 12.sp
                                    },
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                            }
                            if (isCapsLocked) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(4.dp)
                                        .size(4.dp)
                                        .clip(CircleShape)
                                        .background(accent)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Compact Keyboard for Split Mode (Milestone 3.1)
// ──────────────────────────────────────────────────────────────────────────────

@Composable
fun CompactKeyboardScreen(
    keyboardEngine: KeyboardReportSender,
    onConsumerPress: (Int) -> Unit = {},
    onConsumerRelease: () -> Unit = {},
    doubleSpaceForPeriod: Boolean = true,
    keySoundEnabled: Boolean = false,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    val handler = remember { Handler(Looper.getMainLooper()) }

    var shiftState by remember { mutableStateOf(ShiftState.OFF) }
    val heldModifiers = remember { mutableStateListOf<Byte>() }
    val isShifted = shiftState != ShiftState.OFF
    var fnHeld by remember { mutableStateOf(false) }
    var lastSpaceTime by remember { mutableStateOf(0L) }
    var popupKey by remember { mutableStateOf<KeyDef?>(null) }
    var popupOffset by remember { mutableStateOf(Offset.Zero) }

    val activeLayout = if (fnHeld) KeyboardLayouts.COMPACT_SYMBOLS else KeyboardLayouts.COMPACT_QWERTY
    val accent = Color(0xFF7C6AF6)
    val surfaceBg = Color(0xFF08080D)

    fun doHaptic() {
        view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
    }
    fun doSound() {
        if (keySoundEnabled) view.playSoundEffect(SoundEffectConstants.CLICK)
    }

    Box(modifier = modifier.fillMaxSize().background(surfaceBg)) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = 2.dp, vertical = 2.dp),
            verticalArrangement = Arrangement.spacedBy(activeLayout.rowGapDp.dp)
        ) {
            for (row in activeLayout.rows) {
                val totalUnits = row.sumOf { it.widthUnits.toDouble() }.toFloat()
                val isMediaRow = row.any { it.isMediaRow }
                val rowWeight = if (isMediaRow) 0.6f else 1f

                Row(
                    modifier = Modifier.fillMaxWidth().weight(rowWeight),
                    horizontalArrangement = Arrangement.spacedBy(activeLayout.keyGapDp.dp)
                ) {
                    for (key in row) {
                        val weight = key.widthUnits / totalUnits
                        val isModActive = key.type == KeyType.MODIFIER && heldModifiers.contains(key.modByte)
                        val isShiftKey = key.type == KeyType.SHIFT
                        val isFnKey = key.type == KeyType.FN
                        val isSpecialKey = key.type != KeyType.CHAR || key.widthUnits > 1.1f
                        val isBackspace = key.hid == KeyboardReportSender.KEY_BACKSPACE
                        val isSpace = key.hid == KeyboardReportSender.KEY_SPACE
                        val hasAlt = key.altLabel.isNotEmpty()
                        val isConsumer = key.type == KeyType.CONSUMER
                        val isVolumeKey = isConsumer && (key.consumerCode == 0x00E9 || key.consumerCode == 0x00EA)
                        val showFnLabel = fnHeld && key.fnLabel.isNotEmpty()

                        val displayLabel = when {
                            showFnLabel -> key.fnLabel
                            key.type == KeyType.MODIFIER || key.type == KeyType.SHIFT || key.type == KeyType.FN -> key.label
                            isConsumer && key.label.isEmpty() -> ""
                            key.label.isEmpty() -> ""
                            isShifted -> key.shiftLabel
                            else -> key.label
                        }

                        var pressed by remember { mutableStateOf(false) }
                        val animScale by animateFloatAsState(
                            targetValue = if (pressed) 0.93f else 1f,
                            animationSpec = if (pressed) tween(15) else spring(dampingRatio = 0.4f, stiffness = Spring.StiffnessMedium),
                            label = "s"
                        )

                        val glassBg = Color.White.copy(alpha = 0.08f)
                        val glassBgSpecial = Color.White.copy(alpha = 0.05f)
                        val glassBorder = Color.White.copy(alpha = 0.12f)
                        val accentBorder = accent.copy(alpha = 0.5f)

                        val bg by animateColorAsState(
                            targetValue = when {
                                isShiftKey && shiftState == ShiftState.CAPS_LOCK -> accent.copy(alpha = 0.35f)
                                isShiftKey && shiftState == ShiftState.SHIFTED -> accent.copy(alpha = 0.2f)
                                isModActive -> accent.copy(alpha = 0.25f)
                                isFnKey && fnHeld -> accent.copy(alpha = 0.25f)
                                isSpecialKey || isFnKey || isMediaRow || isConsumer -> glassBgSpecial
                                else -> glassBg
                            },
                            animationSpec = tween(100), label = "bg"
                        )
                        val borderColor = when {
                            isModActive || (isShiftKey && isShifted) || (isFnKey && fnHeld) -> accentBorder
                            else -> glassBorder
                        }

                        Box(
                            modifier = Modifier
                                .weight(weight)
                                .fillMaxHeight()
                                .scale(animScale)
                                .clip(RoundedCornerShape(5.dp))
                                .background(bg)
                                .border(0.5.dp, borderColor, RoundedCornerShape(5.dp))
                                .pointerInput(key.label + key.hid + key.modByte + fnHeld) {
                                    awaitEachGesture {
                                        awaitFirstDown(requireUnconsumed = false).also { it.consume() }
                                        pressed = true
                                        doHaptic()
                                        doSound()

                                        val pressTime = SystemClock.uptimeMillis()
                                        var repeatRunnable: Runnable? = null
                                        var altFired = false

                                        // Long-press popup timer for keys with alt characters
                                        var altPopupRunnable: Runnable? = null
                                        if (hasAlt && key.type == KeyType.CHAR) {
                                            altPopupRunnable = Runnable {
                                                popupKey = key
                                                view.performHapticFeedback(HapticFeedbackConstants.KEYBOARD_TAP)
                                            }
                                            handler.postDelayed(altPopupRunnable, 300)
                                        }

                                        when {
                                            key.type == KeyType.SHIFT -> {
                                                shiftState = when (shiftState) {
                                                    ShiftState.OFF -> ShiftState.SHIFTED
                                                    ShiftState.SHIFTED -> ShiftState.CAPS_LOCK
                                                    ShiftState.CAPS_LOCK -> ShiftState.OFF
                                                }
                                                if (shiftState != ShiftState.OFF) keyboardEngine.pressModifier(KeyboardReportSender.MOD_LSHIFT)
                                                else keyboardEngine.releaseModifier(KeyboardReportSender.MOD_LSHIFT)
                                            }
                                            key.type == KeyType.MODIFIER -> {
                                                if (heldModifiers.contains(key.modByte)) {
                                                    heldModifiers.remove(key.modByte)
                                                    keyboardEngine.releaseModifier(key.modByte)
                                                } else {
                                                    heldModifiers.add(key.modByte)
                                                    keyboardEngine.pressModifier(key.modByte)
                                                }
                                            }
                                            key.type == KeyType.FN -> fnHeld = !fnHeld
                                            isConsumer && !fnHeld -> {
                                                onConsumerPress(key.consumerCode)
                                                if (isVolumeKey) {
                                                    val volRunnable = object : Runnable {
                                                        override fun run() {
                                                            onConsumerRelease()
                                                            onConsumerPress(key.consumerCode)
                                                            handler.postDelayed(this, 100)
                                                        }
                                                    }
                                                    repeatRunnable = volRunnable
                                                    handler.postDelayed(volRunnable, 400)
                                                }
                                            }
                                            isConsumer && fnHeld && key.fnHid != 0.toByte() -> {
                                                keyboardEngine.pressKey(key.fnHid)
                                            }
                                            isBackspace -> {
                                                keyboardEngine.pressKey(key.hid)
                                                val bsRunnable = object : Runnable {
                                                    var count = 0
                                                    override fun run() {
                                                        keyboardEngine.releaseKey(key.hid)
                                                        keyboardEngine.pressKey(key.hid)
                                                        count++
                                                        val interval = (250L / (count + 1)).coerceIn(33L, 250L)
                                                        handler.postDelayed(this, interval)
                                                    }
                                                }
                                                repeatRunnable = bsRunnable
                                                handler.postDelayed(bsRunnable, 500)
                                            }
                                            isSpace -> {
                                                val now = SystemClock.uptimeMillis()
                                                if (doubleSpaceForPeriod && (now - lastSpaceTime) < 400) {
                                                    keyboardEngine.pressKey(KeyboardReportSender.KEY_BACKSPACE)
                                                    keyboardEngine.releaseKey(KeyboardReportSender.KEY_BACKSPACE)
                                                    keyboardEngine.pressKey(KeyboardReportSender.KEY_PERIOD)
                                                    keyboardEngine.releaseKey(KeyboardReportSender.KEY_PERIOD)
                                                    keyboardEngine.pressKey(KeyboardReportSender.KEY_SPACE)
                                                    lastSpaceTime = 0L
                                                } else {
                                                    keyboardEngine.pressKey(key.hid)
                                                    lastSpaceTime = now
                                                }
                                            }
                                            key.autoShift -> {
                                                keyboardEngine.pressModifier(KeyboardReportSender.MOD_LSHIFT)
                                                keyboardEngine.pressKey(key.hid)
                                            }
                                            hasAlt -> {
                                                // Don't press yet — wait for long-press decision
                                            }
                                            else -> keyboardEngine.pressKey(key.hid)
                                        }

                                        // Wait for finger lift
                                        do {
                                            val ev = awaitPointerEvent()
                                            ev.changes.forEach { it.consume() }
                                        } while (ev.changes.any { it.pressed })
                                        pressed = false
                                        popupKey = null

                                        // Cancel timers
                                        repeatRunnable?.let { handler.removeCallbacks(it) }
                                        altPopupRunnable?.let { handler.removeCallbacks(it) }

                                        val holdTime = SystemClock.uptimeMillis() - pressTime

                                        // Release logic
                                        when {
                                            key.type == KeyType.SHIFT || key.type == KeyType.MODIFIER || key.type == KeyType.FN -> {}
                                            isConsumer && !fnHeld -> {
                                                onConsumerRelease()
                                            }
                                            isConsumer && fnHeld && key.fnHid != 0.toByte() -> {
                                                keyboardEngine.releaseKey(key.fnHid)
                                            }
                                            key.autoShift -> {
                                                keyboardEngine.releaseKey(key.hid)
                                                keyboardEngine.releaseModifier(KeyboardReportSender.MOD_LSHIFT)
                                                if (shiftState == ShiftState.SHIFTED) {
                                                    shiftState = ShiftState.OFF
                                                }
                                                heldModifiers.forEach { keyboardEngine.releaseModifier(it) }
                                                heldModifiers.clear()
                                            }
                                            hasAlt && holdTime >= 300 -> {
                                                // Long-press: type the alt character
                                                keyboardEngine.pressKey(key.altHid)
                                                keyboardEngine.releaseKey(key.altHid)
                                                if (shiftState == ShiftState.SHIFTED) {
                                                    shiftState = ShiftState.OFF
                                                    keyboardEngine.releaseModifier(KeyboardReportSender.MOD_LSHIFT)
                                                }
                                                heldModifiers.forEach { keyboardEngine.releaseModifier(it) }
                                                heldModifiers.clear()
                                            }
                                            hasAlt -> {
                                                // Short tap: type the letter
                                                keyboardEngine.pressKey(key.hid)
                                                keyboardEngine.releaseKey(key.hid)
                                                if (shiftState == ShiftState.SHIFTED) {
                                                    shiftState = ShiftState.OFF
                                                    keyboardEngine.releaseModifier(KeyboardReportSender.MOD_LSHIFT)
                                                }
                                                heldModifiers.forEach { keyboardEngine.releaseModifier(it) }
                                                heldModifiers.clear()
                                            }
                                            else -> {
                                                keyboardEngine.releaseKey(key.hid)
                                                if (shiftState == ShiftState.SHIFTED) {
                                                    shiftState = ShiftState.OFF
                                                    keyboardEngine.releaseModifier(KeyboardReportSender.MOD_LSHIFT)
                                                }
                                                heldModifiers.forEach { keyboardEngine.releaseModifier(it) }
                                                heldModifiers.clear()
                                            }
                                        }
                                    }
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            // Key label — icon for media keys, text otherwise
                            if (key.icon.isNotEmpty() && !showFnLabel) {
                                MediaKeyIcon(
                                    icon = key.icon,
                                    tint = if (isMediaRow) Color(0xFF8888A0) else Color(0xFFE0E0E0),
                                    size = 14f
                                )
                            } else {
                                Text(
                                    text = displayLabel,
                                    color = when {
                                        isModActive || (isShiftKey && isShifted) || (isFnKey && fnHeld) -> Color(0xFFB8A9FB)
                                        isMediaRow || isConsumer -> Color(0xFF8888A0)
                                        isSpecialKey || isFnKey -> Color(0xFF8888A0)
                                        else -> Color(0xFFE0E0E0)
                                    },
                                    fontSize = when {
                                        key.label.length > 3 -> 8.sp
                                        key.label.length > 1 -> 9.sp
                                        else -> 11.sp
                                    },
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center
                                )
                            }
                            // Alt character hint (top-right corner)
                            if (hasAlt && !fnHeld) {
                                Text(
                                    text = key.altLabel,
                                    color = Color(0xFF666680),
                                    fontSize = 7.sp,
                                    modifier = Modifier.align(Alignment.TopEnd).padding(2.dp)
                                )
                            }
                            // Caps Lock dot
                            if (key.label == "Caps" && shiftState == ShiftState.CAPS_LOCK) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(3.dp)
                                        .size(3.dp)
                                        .clip(CircleShape)
                                        .background(accent)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Long-press popup overlay
        popupKey?.let { pk ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.TopCenter)
                    .padding(top = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(accent.copy(alpha = 0.9f))
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = pk.altLabel,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ──────────────────────────────────────────────────────────────────────────────
// Split Screen Container (Milestone 3.0)
// ──────────────────────────────────────────────────────────────────────────────

@Composable
fun SplitScreen(
    trackpadContent: @Composable (Modifier) -> Unit,
    keyboardContent: @Composable (Modifier) -> Unit
) {
    val surfaceBg = Color(0xFF08080D)
    val glassBorder = Color.White.copy(alpha = 0.12f)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(surfaceBg)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        // Top: Trackpad (65%)
        trackpadContent(
            Modifier
                .weight(0.65f)
                .fillMaxWidth()
        )

        // Horizontal divider
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(glassBorder)
        )

        // Bottom: Compact Keyboard (35%)
        keyboardContent(
            Modifier
                .weight(0.35f)
                .fillMaxWidth()
        )
    }
}

@Composable
fun ModePopup(
    currentMode: AppScreen,
    isVisible: Boolean,
    onModeSelected: (AppScreen) -> Unit,
    onToggle: () -> Unit,
    showTrigger: Boolean = true,
    dropDown: Boolean = false,
    modifier: Modifier = Modifier
) {
    val accent = Color(0xFF7C6AF6)
    val modes = listOf(
        Triple(AppScreen.TRACKPAD, "Trackpad", "trackpad"),
        Triple(AppScreen.KEYBOARD, "Keyboard", "keyboard"),
        Triple(AppScreen.SPLIT, "Split", "split")
    )

    @Composable
    fun PopupOptions() {
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(150)) + slideInVertically(tween(200)) { if (dropDown) -it / 2 else it / 2 },
            exit = fadeOut(tween(150)) + slideOutVertically(tween(150)) { if (dropDown) -it / 2 else it / 2 }
        ) {
            Column(
                modifier = Modifier
                    .then(if (dropDown) Modifier.padding(top = 8.dp) else Modifier.padding(bottom = 8.dp))
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF1A1A24))
                    .border(0.5.dp, Color.White.copy(alpha = 0.12f), RoundedCornerShape(16.dp))
                    .padding(6.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                modes.forEach { (mode, label, _) ->
                    val isActive = mode == currentMode
                    val bg by animateColorAsState(
                        if (isActive) accent else Color.Transparent,
                        animationSpec = tween(200), label = "mode-bg"
                    )
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(bg)
                            .clickable { onModeSelected(mode) }
                            .padding(horizontal = 14.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Canvas(modifier = Modifier.size(18.dp)) {
                            val w = size.width; val h = size.height
                            val iconColor = if (isActive) Color.White else Color.White.copy(alpha = 0.5f)
                            val stroke = if (isActive) 2f else 1.5f
                            when (mode) {
                                AppScreen.TRACKPAD -> {
                                    drawRoundRect(iconColor, Offset(w * 0.12f, h * 0.08f), Size(w * 0.76f, h * 0.84f), CornerRadius(w * 0.12f), style = Stroke(stroke))
                                    if (isActive) drawCircle(Color.White, w * 0.06f, Offset(w * 0.55f, h * 0.45f))
                                }
                                AppScreen.KEYBOARD -> {
                                    for (r in 0..2) for (c in 0..2) drawRoundRect(iconColor, Offset(w * (0.12f + c * 0.3f), h * (0.12f + r * 0.3f)), Size(w * 0.18f, h * 0.14f), CornerRadius(w * 0.03f), style = if (isActive) Fill else Stroke(stroke))
                                }
                                AppScreen.SPLIT -> {
                                    drawRoundRect(iconColor, Offset(w * 0.12f, h * 0.08f), Size(w * 0.76f, h * 0.84f), CornerRadius(w * 0.1f), style = Stroke(stroke))
                                    drawLine(iconColor, Offset(w * 0.18f, h * 0.5f), Offset(w * 0.82f, h * 0.5f), strokeWidth = stroke)
                                }
                                else -> {}
                            }
                        }
                        Text(
                            text = label,
                            color = if (isActive) Color.White else Color.White.copy(alpha = 0.6f),
                            fontSize = 13.sp,
                            fontWeight = if (isActive) FontWeight.SemiBold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun TriggerButton() {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(
                    if (isVisible) accent.copy(alpha = 0.2f)
                    else Color.White.copy(alpha = 0.06f)
                )
                .border(
                    0.5.dp,
                    if (isVisible) accent.copy(alpha = 0.5f)
                    else Color.White.copy(alpha = 0.08f),
                    RoundedCornerShape(12.dp)
                )
                .clickable { onToggle() },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(22.dp)) {
                val w = size.width; val h = size.height
                val c = if (isVisible) Color.White else Color.White.copy(alpha = 0.45f)
                // Mouse icon: body + scroll wheel + buttons divider
                val bodyTop = h * 0.08f; val bodyBot = h * 0.92f
                val bodyLeft = w * 0.2f; val bodyRight = w * 0.8f
                val bodyW = bodyRight - bodyLeft; val bodyH = bodyBot - bodyTop
                drawRoundRect(c, Offset(bodyLeft, bodyTop), Size(bodyW, bodyH), CornerRadius(bodyW * 0.45f), style = Stroke(1.6f))
                // Center divider line (top half only)
                drawLine(c, Offset(w * 0.5f, bodyTop + bodyH * 0.05f), Offset(w * 0.5f, bodyTop + bodyH * 0.35f), strokeWidth = 1.2f)
                // Scroll wheel
                drawRoundRect(c, Offset(w * 0.44f, bodyTop + bodyH * 0.12f), Size(w * 0.12f, bodyH * 0.16f), CornerRadius(w * 0.03f), style = Stroke(1.2f))
            }
        }
    }

    Box(modifier = modifier) {
        if (dropDown) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                if (showTrigger) TriggerButton()
                PopupOptions()
            }
        } else {
            Column(horizontalAlignment = Alignment.End) {
                PopupOptions()
                if (showTrigger) TriggerButton()
            }
        }
    }

}
