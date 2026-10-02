package com.phonepad.app

import android.Manifest
import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothHidDevice
import android.bluetooth.BluetoothHidDeviceAppSdpSettings
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothProfile
import android.content.res.Configuration
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.IBinder
import android.os.PowerManager
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
import androidx.compose.foundation.layout.aspectRatio
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
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.runtime.derivedStateOf
import kotlin.math.roundToInt
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.animation.core.withInfiniteAnimationFrameMillis
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
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
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
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
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.EaseOutBack
import androidx.compose.animation.core.EaseOutCubic
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.Image
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin

enum class AppScreen {
    SPLASH, SHOWCASE, CONNECT, COMPAT_FAIL, HOME, PERMISSION, PERMISSION_DENIED, TRACKPAD, KEYBOARD, SPLIT
}

data class AppThemeColors(
    val surface: Color,
    val keySurface: Color,
    val keySpecialSurface: Color,
    val keyBorder: Color,
    val accent: Color,
    val accentGlow: Color,
    val accentBorder: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val edgeGlow: Color,
    val divider: Color
)

object AppThemes {
    val Midnight = AppThemeColors(
        surface = Color(0xFF08080D),
        keySurface = Color(0x14FFFFFF),
        keySpecialSurface = Color(0x0DFFFFFF),
        keyBorder = Color(0x1FFFFFFF),
        accent = Color(0xFF7C6AF6),
        accentGlow = Color(0xFF4B4FCF),
        accentBorder = Color(0x807C6AF6),
        textPrimary = Color(0xFFE0E0E0),
        textSecondary = Color(0xFF8888A0),
        edgeGlow = Color(0x1F2B2D6E),
        divider = Color(0x1FFFFFFF)
    )
    val Graphite = AppThemeColors(
        surface = Color(0xFF0C0C0F),
        keySurface = Color(0x12FFFFFF),
        keySpecialSurface = Color(0x0AFFFFFF),
        keyBorder = Color(0x1AFFFFFF),
        accent = Color(0xFF8E8EA0),
        accentGlow = Color(0xFF5A5A70),
        accentBorder = Color(0x808E8EA0),
        textPrimary = Color(0xFFD0D0D6),
        textSecondary = Color(0xFF707080),
        edgeGlow = Color(0x14505060),
        divider = Color(0x1AFFFFFF)
    )
    val Ember = AppThemeColors(
        surface = Color(0xFF0D0908),
        keySurface = Color(0x14FFFFFF),
        keySpecialSurface = Color(0x0DFFFFFF),
        keyBorder = Color(0x1EFFC4A0),
        accent = Color(0xFFE8864A),
        accentGlow = Color(0xFFB85C2A),
        accentBorder = Color(0x80E8864A),
        textPrimary = Color(0xFFE8DCD0),
        textSecondary = Color(0xFF9A8878),
        edgeGlow = Color(0x1A6E3B2B),
        divider = Color(0x1EFFC4A0)
    )
    val Aurora = AppThemeColors(
        surface = Color(0xFF080D0C),
        keySurface = Color(0x14FFFFFF),
        keySpecialSurface = Color(0x0DFFFFFF),
        keyBorder = Color(0x1EA0FFE0),
        accent = Color(0xFF4AE8C4),
        accentGlow = Color(0xFF2AB89A),
        accentBorder = Color(0x804AE8C4),
        textPrimary = Color(0xFFD0E8E0),
        textSecondary = Color(0xFF78A098),
        edgeGlow = Color(0x1A2B6E5B),
        divider = Color(0x1EA0FFE0)
    )
    val Porcelain = AppThemeColors(
        surface = Color(0xFF141218),
        keySurface = Color(0x18FFFFFF),
        keySpecialSurface = Color(0x10FFFFFF),
        keyBorder = Color(0x22FFFFFF),
        accent = Color(0xFFC48ABA),
        accentGlow = Color(0xFF9A6890),
        accentBorder = Color(0x80C48ABA),
        textPrimary = Color(0xFFE0D8DE),
        textSecondary = Color(0xFF8A8090),
        edgeGlow = Color(0x1A6E4B60),
        divider = Color(0x22FFFFFF)
    )

    fun fromName(name: String): AppThemeColors = when (name) {
        "graphite" -> Midnight
        "indigo" -> Midnight
        "ember" -> Ember
        "aurora" -> Aurora
        "rose" -> Porcelain
        "porcelain" -> Porcelain
        "darker" -> Graphite
        "amoled" -> Midnight
        else -> Midnight
    }
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
        private const val PREF_KEYBOARD_LAYOUT = "keyboard_layout"

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
    private var trackpadTheme by mutableStateOf("midnight")
    private var uiTheme by mutableStateOf("dark") // dark, light
    private var keyboardLayoutName by mutableStateOf("qwerty")

    // Phase 1 UI navigation
    private var currentScreen by mutableStateOf(AppScreen.SPLASH)
    private var pendingModeAfterConnect: AppScreen? = null

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
            if (connectedDevice != null) {
                currentScreen = AppScreen.HOME
            } else if (hasBondedComputer()) {
                prefs.edit().putBoolean(PREF_HAS_SEEN_ONBOARDING, true).apply()
                currentScreen = AppScreen.HOME
            } else {
                currentScreen = AppScreen.CONNECT
            }
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
                    startForegroundService(Intent(this@MainActivity, HidForegroundService::class.java))
                }
                BluetoothProfile.STATE_DISCONNECTED -> {
                    cleanupDragState()
                    stopScrollOutput()
                    resetScrollState()
                    resetWheelMultiplier()
                    val wasConnected = connectedDevice != null
                    connectedDevice = null
                    try { stopService(Intent(this@MainActivity, HidForegroundService::class.java)) } catch (_: Exception) {}
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
        trackpadTheme = prefs.getString(PREF_TRACKPAD_THEME, "midnight") ?: "midnight"
        uiTheme = prefs.getString(PREF_UI_THEME, "dark") ?: "dark"
        keyboardLayoutName = prefs.getString(PREF_KEYBOARD_LAYOUT, "qwerty") ?: "qwerty"

        val lastHost = prefs.getString(PREF_LAST_HOST_ADDRESS, null)
        if (lastHost != null) {
            Log.d(TAG, "Last host restored: $lastHost")
        }

        requestBatteryOptimizationExemption()

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
        handler.postDelayed({ routeFromSplash() }, 2600L)

        setContent {
            PhonePadTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    @Suppress("UnusedContentLambdaTargetStateParameter")
                    AnimatedContent(
                        targetState = currentScreen,
                        transitionSpec = {
                            if (initialState == AppScreen.SPLASH) {
                                (fadeIn(tween(350)) + slideInVertically { -it / 16 }) togetherWith
                                    fadeOut(tween(250))
                            } else if (targetState == AppScreen.TRACKPAD) {
                                (fadeIn(tween(180)) + slideInHorizontally { it / 6 }) togetherWith
                                    (fadeOut(tween(120)) + slideOutHorizontally { -it / 6 })
                            } else {
                                (fadeIn(tween(160)) + slideInHorizontally { it / 8 }) togetherWith
                                    (fadeOut(tween(100)) + slideOutHorizontally { -it / 8 })
                            }
                        },
                        label = "screen"
                    ) { screen ->
                        when (screen) {
                            AppScreen.SPLASH -> SplashScreen(
                                modifier = Modifier.padding(innerPadding)
                            )
                            AppScreen.SHOWCASE -> ShowcaseScreen(
                                onFinish = {
                                    if (!hasBluetoothPermissions()) {
                                        currentScreen = AppScreen.PERMISSION
                                    } else if (hasBondedComputer()) {
                                        prefs.edit().putBoolean(PREF_HAS_SEEN_ONBOARDING, true).apply()
                                        ensureHidSession()
                                        currentScreen = AppScreen.HOME
                                    } else {
                                        currentScreen = AppScreen.CONNECT
                                    }
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                            AppScreen.CONNECT -> ConnectScreen(
                                isBluetoothConnected = connectedDevice != null,
                                onConnected = {
                                    prefs.edit().putBoolean(PREF_HAS_SEEN_ONBOARDING, true).apply()
                                    currentScreen = AppScreen.HOME
                                },
                                onRetryConnection = {
                                    if (hasBluetoothPermissions()) {
                                        autoReconnectAttempted = false
                                        ensureHidSession()
                                    }
                                },
                                onSetupLater = {
                                    prefs.edit().putBoolean(PREF_HAS_SEEN_ONBOARDING, true).apply()
                                    if (hasBluetoothPermissions()) {
                                        ensureHidSession()
                                    }
                                    currentScreen = AppScreen.HOME
                                },
                                modifier = Modifier.padding(innerPadding)
                            )
                            AppScreen.COMPAT_FAIL -> CompatFailScreen(
                                deviceModel = "${Build.MANUFACTURER} ${Build.MODEL}",
                                modifier = Modifier.padding(innerPadding)
                            )
                            AppScreen.HOME -> HomeScreen(
                                isBluetoothConnected = connectedDevice != null,
                                hasBondedComputer = hasBondedComputer(),
                                onRetryConnection = {
                                    if (hasBluetoothPermissions()) {
                                        autoReconnectAttempted = false
                                        ensureHidSession()
                                    }
                                },
                                onModeSelected = { mode ->
                                    if (mode == AppScreen.CONNECT) {
                                        if (!hasBluetoothPermissions()) {
                                            currentScreen = AppScreen.PERMISSION
                                        } else {
                                            ensureHidSession()
                                            val bonded = bluetoothAdapter?.bondedDevices ?: emptySet()
                                            if (bonded.isEmpty()) {
                                                currentScreen = AppScreen.CONNECT
                                            }
                                        }
                                    } else if (!hasBluetoothPermissions()) {
                                        currentScreen = AppScreen.PERMISSION
                                    } else {
                                        ensureHidSession()
                                        currentScreen = mode
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
                                                keyboardLayout = keyboardLayoutName,
                                                onKeyboardLayoutChange = { keyboardLayoutName = it; saveGlobalSettings() },
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
                                                    currentScreen = AppScreen.CONNECT
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
                                                keySoundEnabled = keySoundEnabled,
                                                layout = KeyboardLayouts.fullLayoutFromName(keyboardLayoutName),
                                                theme = AppThemes.fromName(trackpadTheme)
                                            )
                                            AppScreen.SPLIT -> {
                                                val splitTheme = AppThemes.fromName(trackpadTheme)
                                                SplitScreen(
                                                    trackpadContent = { mod ->
                                                        Box(modifier = mod) {
                                                            Box(
                                                                modifier = Modifier
                                                                    .fillMaxSize()
                                                                    .background(splitTheme.surface)
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
                                                            theme = splitTheme,
                                                            modifier = mod
                                                        )
                                                    },
                                                    theme = splitTheme
                                                )
                                            }
                                            else -> {}
                                        }
                                    }

                                    // Mode switcher popup
                                    if (!showSettings) {
                                        // Scrim / dismiss overlay (dark backdrop in keyboard mode)
                                        AnimatedVisibility(
                                            visible = showModePopup,
                                            enter = fadeIn(tween(100)),
                                            exit = fadeOut(tween(80))
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(
                                                        if (currentScreen == AppScreen.KEYBOARD)
                                                            Color.Black.copy(alpha = 0.55f)
                                                        else Color.Transparent
                                                    )
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
            currentScreen = AppScreen.HOME
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
        try { stopService(Intent(this, HidForegroundService::class.java)) } catch (_: Exception) {}
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

    @SuppressLint("BatteryLife")
    private fun requestBatteryOptimizationExemption() {
        val pm = getSystemService(PowerManager::class.java)
        if (!pm.isIgnoringBatteryOptimizations(packageName)) {
            val intent = Intent(AndroidSettings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                data = Uri.parse("package:$packageName")
            }
            try { startActivity(intent) } catch (_: Exception) {}
        }
    }

    @SuppressLint("MissingPermission")
    private fun hasBondedComputer(): Boolean {
        if (!hasBluetoothPermissions()) return false
        val bonded = bluetoothAdapter?.bondedDevices ?: return false
        return bonded.any { device ->
            val major = device.bluetoothClass?.majorDeviceClass
            major == BluetoothClass.Device.Major.COMPUTER
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
        trackpadTheme = prefs.getString(PREF_TRACKPAD_THEME, "midnight") ?: "midnight"
        uiTheme = prefs.getString(PREF_UI_THEME, "dark") ?: "dark"
        keyboardLayoutName = prefs.getString(PREF_KEYBOARD_LAYOUT, "qwerty") ?: "qwerty"
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
            .putString(PREF_KEYBOARD_LAYOUT, keyboardLayoutName)
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

        val adapter = bluetoothAdapter ?: return
        val hid = hidDevice ?: return
        val bonded = adapter.bondedDevices ?: emptySet()

        val lastAddress = prefs.getString(PREF_LAST_HOST_ADDRESS, null)
        val target = if (lastAddress != null) {
            bonded.find { it.address == lastAddress } ?: run {
                Log.d(TAG, "Auto-reconnect: last host $lastAddress not in bonded devices")
                bonded.firstOrNull { it.bluetoothClass?.majorDeviceClass == BluetoothClass.Device.Major.COMPUTER }
            }
        } else {
            Log.d(TAG, "Auto-reconnect: no last host stored, trying first bonded computer")
            bonded.firstOrNull { it.bluetoothClass?.majorDeviceClass == BluetoothClass.Device.Major.COMPUTER }
        }

        if (target == null) {
            Log.d(TAG, "Auto-reconnect: no bonded computers available, skipping")
            return
        }

        autoReconnectAttempted = true
        Log.d(TAG, "Auto-reconnect: attempting to connect to ${target.name} [${target.address}]")
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
            if (hasBluetoothPermissions() && hasBondedComputer()) {
                Log.d(TAG, "routeFromSplash: first launch but bonded computer found — skip to HOME")
                prefs.edit().putBoolean(PREF_HAS_SEEN_ONBOARDING, true).apply()
                ensureHidSession()
                currentScreen = AppScreen.HOME
            } else {
                Log.d(TAG, "routeFromSplash: first launch — SHOWCASE")
                currentScreen = AppScreen.SHOWCASE
            }
            return
        }
        if (hasBluetoothPermissions()) {
            ensureHidSession()
        }
        Log.d(TAG, "routeFromSplash: returning user — HOME")
        currentScreen = AppScreen.HOME
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
    val pieceEasing = remember { CubicBezierEasing(0.3f, 0.9f, 0.35f, 1f) }
    val bounceEasing = remember { CubicBezierEasing(0.34f, 1.56f, 0.4f, 1f) }
    val popEasing = remember { CubicBezierEasing(0.2f, 1.4f, 0.3f, 1f) }
    val stampEasing = remember { CubicBezierEasing(0.22f, 0.68f, 0.36f, 1.22f) }

    val wrapScale = remember { Animatable(0.85f) }
    val wrapAlpha = remember { Animatable(0f) }

    val cardP = remember { Animatable(0f) }
    val bgP = remember { (0 until 10).map { Animatable(0f) } }
    val mainP = remember { (0 until 4).map { Animatable(0f) } }
    val padP = remember { Animatable(0f) }
    val cursorP = remember { Animatable(0f) }

    val letters = "Mouskey"
    val lAlpha = remember { letters.map { Animatable(0f) } }
    val lScale = remember { letters.map { Animatable(1.8f) } }
    val lY = remember { letters.map { Animatable(-18f) } }

    val bgLabelPaint = remember {
        android.graphics.Paint().apply {
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.CENTER
            typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
        }
    }
    val mainLabelPaint = remember {
        android.graphics.Paint().apply {
            isAntiAlias = true
            textAlign = android.graphics.Paint.Align.CENTER
            typeface = android.graphics.Typeface.create("sans-serif", android.graphics.Typeface.BOLD)
        }
    }

    LaunchedEffect(Unit) {
        delay(50)
        launch { wrapAlpha.animateTo(1f, tween(400)) }
        launch {
            wrapScale.animateTo(1.06f, tween(390, easing = popEasing))
            wrapScale.animateTo(1f, tween(260, easing = EaseOutCubic))
        }
        launch { cardP.animateTo(1f, tween(550, easing = pieceEasing)) }
        val bgDelays = longArrayOf(90, 118, 146, 174, 202, 230, 258, 286, 314, 342)
        bgP.forEachIndexed { i, a -> launch { delay(bgDelays[i]); a.animateTo(1f, tween(550, easing = pieceEasing)) } }
        val mainDelays = longArrayOf(420, 470, 520, 570)
        mainP.forEachIndexed { i, a -> launch { delay(mainDelays[i]); a.animateTo(1f, tween(600, easing = bounceEasing)) } }
        launch { delay(660); padP.animateTo(1f, tween(550, easing = pieceEasing)) }
        launch { delay(780); cursorP.animateTo(1f, tween(600, easing = bounceEasing)) }
        letters.forEachIndexed { i, _ ->
            launch {
                delay(550L + i * 70L)
                launch { lAlpha[i].animateTo(1f, tween(80)) }
                launch {
                    lScale[i].animateTo(0.92f, tween(176, easing = stampEasing))
                    lScale[i].animateTo(1.04f, tween(74, easing = EaseOutCubic))
                    lScale[i].animateTo(1f, tween(70, easing = EaseOutCubic))
                }
                launch {
                    lY[i].animateTo(2f, tween(176, easing = stampEasing))
                    lY[i].animateTo(-1f, tween(74, easing = EaseOutCubic))
                    lY[i].animateTo(0f, tween(70, easing = EaseOutCubic))
                }
            }
        }
    }

    Box(
        modifier = modifier.fillMaxSize().background(Color(0xFFe6e0d6)),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val spacing = 26.dp.toPx()
            val dotR = 1.dp.toPx()
            val cx = size.width / 2f
            val cy = size.height * 0.45f
            val maxDist = size.minDimension * 0.34f
            val cols = (size.width / spacing).toInt() + 2
            val rows = (size.height / spacing).toInt() + 2
            for (col in -1..cols) {
                for (row in -1..rows) {
                    val px = col * spacing
                    val py = row * spacing
                    val dist = sqrt((px - cx) * (px - cx) + (py - cy) * (py - cy))
                    if (dist < maxDist) {
                        val a = (1f - dist / maxDist)
                        drawCircle(Color.Black.copy(alpha = a * a * 0.05f), dotR, Offset(px, py))
                    }
                }
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Canvas(
                modifier = Modifier
                    .size(280.dp)
                    .graphicsLayer {
                        scaleX = wrapScale.value
                        scaleY = wrapScale.value
                        alpha = wrapAlpha.value
                    }
            ) {
                val s = size.width / 680f
                val nc = drawContext.canvas.nativeCanvas

                val cp = cardP.value
                if (cp > 0f) {
                    val cs = 0.94f + 0.06f * cp
                    scale(cs, pivot = Offset(340f * s, 340f * s)) {
                        drawRoundRect(Color(0xFFe6e0d6).copy(alpha = cp), Offset(90f * s, 90f * s), Size(500f * s, 500f * s), CornerRadius(48f * s))
                    }
                }

                val bgOp = floatArrayOf(0.28f, 0.22f, 0.18f, 0.20f, 0.16f, 0.16f, 0.14f, 0.12f, 0.13f, 0.10f)
                val bgRot = floatArrayOf(-22f, 18f, 30f, -30f, 8f, -14f, 12f, 15f, -26f, 20f)
                val bgPivX = floatArrayOf(144f, 500f, 535f, 134f, 308f, 218f, 441f, 124f, 537f, 330f)
                val bgPivY = floatArrayOf(140f, 150f, 359f, 419f, 117f, 527f, 537f, 227f, 467f, 551f)
                val bgSx = floatArrayOf(108f, 460f, 500f, 95f, 280f, 190f, 410f, 95f, 510f, 305f)
                val bgSy = floatArrayOf(124f, 134f, 344f, 404f, 104f, 514f, 524f, 214f, 454f, 539f)
                val bgTx = floatArrayOf(108f, 460f, 500f, 95f, 280f, 190f, 410f, 95f, 510f, 305f)
                val bgTy = floatArrayOf(120f, 130f, 340f, 400f, 100f, 510f, 520f, 210f, 450f, 535f)
                val bgW = floatArrayOf(72f, 80f, 70f, 78f, 56f, 56f, 62f, 58f, 55f, 50f)
                val bgH = floatArrayOf(40f, 40f, 38f, 38f, 34f, 34f, 34f, 34f, 34f, 32f)
                val bgRx = floatArrayOf(10f, 10f, 10f, 10f, 9f, 9f, 9f, 9f, 9f, 9f)
                val bgLabels = arrayOf("Esc", "Tab", "Fn", "Del", "W", "A", "End", "Q", "Z", "S")
                val bgLx = floatArrayOf(144f, 500f, 535f, 134f, 308f, 218f, 441f, 124f, 537f, 330f)
                val bgLy = floatArrayOf(145f, 155f, 364f, 424f, 122f, 532f, 542f, 232f, 472f, 556f)
                val bgFs = floatArrayOf(12f, 12f, 11f, 11f, 11f, 11f, 11f, 11f, 11f, 11f)

                for (i in 0 until 10) {
                    val p = bgP[i].value
                    if (p > 0f) {
                        val ps = 0.5f + 0.5f * p
                        val tY = 6f * (1f - p) * s
                        val cX = (bgTx[i] + bgW[i] / 2f) * s
                        val cY = (bgTy[i] + bgH[i] / 2f) * s
                        translate(top = tY) {
                            scale(ps, pivot = Offset(cX, cY)) {
                                rotate(bgRot[i], pivot = Offset(bgPivX[i] * s, bgPivY[i] * s)) {
                                    val alpha = p * bgOp[i]
                                    drawRoundRect(Color(0xFFc4bfb5).copy(alpha = alpha), Offset(bgSx[i] * s, bgSy[i] * s), Size(bgW[i] * s, bgH[i] * s), CornerRadius(bgRx[i] * s))
                                    drawRoundRect(Color(0xFFdad5cb).copy(alpha = alpha), Offset(bgTx[i] * s, bgTy[i] * s), Size(bgW[i] * s, bgH[i] * s), CornerRadius(bgRx[i] * s))
                                    bgLabelPaint.textSize = bgFs[i] * s
                                    bgLabelPaint.color = android.graphics.Color.argb((alpha * 255).toInt(), 0x9a, 0x94, 0x88)
                                    nc.drawText(bgLabels[i], bgLx[i] * s, bgLy[i] * s, bgLabelPaint)
                                }
                            }
                        }
                    }
                }

                val mRot = floatArrayOf(-7f, 10f, 6f, -9f)
                val mPivX = floatArrayOf(200f, 475f, 200f, 475f)
                val mPivY = floatArrayOf(195f, 188f, 480f, 475f)
                val mSx = floatArrayOf(140f, 415f, 140f, 415f)
                val mSy = floatArrayOf(174f, 166f, 458f, 453f)
                val mTx = floatArrayOf(140f, 415f, 140f, 415f)
                val mTy = floatArrayOf(168f, 160f, 452f, 447f)
                val mW = 120f; val mH = 56f; val mRx2 = 14f
                val mIx = floatArrayOf(144f, 419f, 144f, 419f)
                val mIy = floatArrayOf(171f, 163f, 455f, 450f)
                val mIw = 112f; val mIh = 46f; val mIr = 11f
                val mLabels = arrayOf("Shift", "Enter", "Ctrl", "Alt")
                val mLx = floatArrayOf(200f, 475f, 200f, 475f)
                val mLy = floatArrayOf(202f, 194f, 486f, 481f)

                for (i in 0 until 4) {
                    val p = mainP[i].value
                    if (p > 0f) {
                        val ps = 0.3f + 0.7f * p
                        val tY = 14f * (1f - p) * s
                        val cX = (mTx[i] + mW / 2f) * s
                        val cY = (mTy[i] + mH / 2f) * s
                        translate(top = tY) {
                            scale(ps, pivot = Offset(cX, cY)) {
                                rotate(mRot[i], pivot = Offset(mPivX[i] * s, mPivY[i] * s)) {
                                    drawRoundRect(Color(0xFFb5afa3).copy(alpha = p), Offset(mSx[i] * s, mSy[i] * s), Size(mW * s, mH * s), CornerRadius(mRx2 * s))
                                    drawRoundRect(Color(0xFFd6d0c6).copy(alpha = p), Offset(mTx[i] * s, mTy[i] * s), Size(mW * s, mH * s), CornerRadius(mRx2 * s))
                                    drawRoundRect(Color(0xFFe8e3db).copy(alpha = p * 0.5f), Offset(mIx[i] * s, mIy[i] * s), Size(mIw * s, mIh * s), CornerRadius(mIr * s), style = Stroke(0.8f * s))
                                    mainLabelPaint.textSize = 17f * s
                                    mainLabelPaint.color = android.graphics.Color.argb((p * 255).toInt(), 0x5e, 0x59, 0x53)
                                    nc.drawText(mLabels[i], mLx[i] * s, mLy[i] * s, mainLabelPaint)
                                }
                            }
                        }
                    }
                }

                val pp = padP.value
                if (pp > 0f) {
                    val ps = 0.85f + 0.15f * pp
                    val padCx = 340f * s; val padCy = 342.5f * s
                    scale(ps, pivot = Offset(padCx, padCy)) {
                        drawRoundRect(Color(0xFFb5afa3).copy(alpha = pp), Offset(195f * s, 261f * s), Size(290f * s, 175f * s), CornerRadius(22f * s))
                        drawRoundRect(Color(0xFFcdc7bd).copy(alpha = pp), Offset(195f * s, 255f * s), Size(290f * s, 175f * s), CornerRadius(22f * s))
                        drawRoundRect(Color(0xFFccc6bb).copy(alpha = pp * 0.55f), Offset(208f * s, 267f * s), Size(264f * s, 150f * s), CornerRadius(15f * s))
                        drawRoundRect(Color(0xFFb0a99d).copy(alpha = pp * 0.55f), Offset(208f * s, 267f * s), Size(264f * s, 150f * s), CornerRadius(15f * s), style = Stroke(0.8f * s))
                        drawLine(Color(0xFFa9a296).copy(alpha = pp), Offset(235f * s, 398f * s), Offset(445f * s, 398f * s), 2f * s, StrokeCap.Round)
                    }
                }

                val crp = cursorP.value
                if (crp > 0f) {
                    val crY = -10f * (1f - crp) * s
                    translate(top = crY) {
                        scale(crp, pivot = Offset(315f * s, 335f * s)) {
                            translate(left = 315f * s, top = 335f * s) {
                                val cursorPath = Path().apply {
                                    moveTo(-24f * s, -45f * s)
                                    lineTo(-24f * s, 30f * s)
                                    lineTo(-8f * s, 14f * s)
                                    lineTo(10f * s, 45f * s)
                                    lineTo(26f * s, 36f * s)
                                    lineTo(8f * s, 6f * s)
                                    lineTo(28f * s, -2f * s)
                                    close()
                                }
                                drawPath(cursorPath, Color(0xFF484340).copy(alpha = crp))
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row {
                letters.forEachIndexed { i, ch ->
                    Text(
                        text = ch.toString(),
                        color = Color(0xFF3a352f),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily(Font(R.font.caveat_bold)),
                        letterSpacing = 1.sp,
                        modifier = Modifier.graphicsLayer {
                            scaleX = lScale[i].value
                            scaleY = lScale[i].value
                            translationY = lY[i].value * 2f
                            alpha = lAlpha[i].value
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun ShowcaseScreen(onFinish: () -> Unit, modifier: Modifier = Modifier) {
    val morphEasing = remember { CubicBezierEasing(0.4f, 0f, 0.12f, 1f) }
    val stampEasing = remember { CubicBezierEasing(0.22f, 0.68f, 0.36f, 1.22f) }

    var mode by remember { mutableStateOf(0) }
    var cyclesDone by remember { mutableStateOf(0) }
    val modeDurs = remember { longArrayOf(4000, 4500, 4500) }

    LaunchedEffect(mode) {
        val first = mode == 0 && cyclesDone == 0
        delay(if (first) 1800L else modeDurs[mode])
        val next = (mode + 1) % 3
        if (next == 0) cyclesDone++
        mode = next
    }

    val surfBot by animateFloatAsState(when (mode) { 0 -> 0.022f; 1 -> 0.6f; else -> 0.38f }, tween(850, easing = morphEasing))
    val surfAlpha by animateFloatAsState(when (mode) { 1 -> 0f; else -> 1f }, tween(850, easing = morphEasing))
    val keysAlpha by animateFloatAsState(when (mode) { 0 -> 0f; else -> 1f }, tween(850, easing = morphEasing))
    val keysOffY by animateFloatAsState(when (mode) { 0 -> 20f; else -> 0f }, tween(850, easing = morphEasing))
    val keysTopFrac by animateFloatAsState(when (mode) { 2 -> 0.67f; else -> 1f }, tween(850, easing = morphEasing))
    val laptopAlpha by animateFloatAsState(when (mode) { 1 -> 1f; else -> 0f }, tween(700, easing = morphEasing))
    val divAlpha by animateFloatAsState(when (mode) { 2 -> 1f; else -> 0f }, tween(700, easing = morphEasing))
    val curAlpha by animateFloatAsState(when (mode) { 1 -> 0f; else -> 1f }, tween(500))

    var curX by remember { mutableStateOf(0.45f) }
    var curY by remember { mutableStateOf(0.35f) }
    var showRipple by remember { mutableStateOf(false) }
    var ripX by remember { mutableStateOf(0f) }
    var ripY by remember { mutableStateOf(0f) }
    val ripScale = remember { Animatable(0f) }
    val ripAlpha = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        val wps = floatArrayOf(0.45f,0.35f,0f, 0.72f,0.2f,700f, 0.6f,0.55f,1500f, 0.25f,0.4f,2100f, 0.55f,0.65f,2700f, 0.45f,0.35f,3400f)
        val n = wps.size / 3
        var start = 0L
        while (true) {
            withInfiniteAnimationFrameMillis { ms ->
                if (start == 0L) start = ms
                when (mode) {
                    0 -> {
                        val el = (ms - start) % 4000
                        var si = 0
                        for (i in 0 until n - 1) { if (el >= wps[i*3+2].toLong() && el < wps[(i+1)*3+2].toLong()) { si = i; break } }
                        if (el >= wps[(n-1)*3+2].toLong()) si = n - 2
                        val ax = wps[si*3]; val ay = wps[si*3+1]; val at = wps[si*3+2]
                        val bx = wps[(si+1)*3]; val by = wps[(si+1)*3+1]; val bt = wps[(si+1)*3+2]
                        val p = ((el - at.toLong()).toFloat() / (bt - at)).coerceIn(0f, 1f)
                        val e = if (p < 0.5f) 2*p*p else 1 - (-2*p+2).pow(2)/2
                        curX = ax + (bx - ax) * e; curY = ay + (by - ay) * e
                        if (el in 2700..2740 && !showRipple) {
                            showRipple = true; ripX = curX; ripY = curY
                            launch {
                                ripScale.snapTo(0f); ripAlpha.snapTo(0.5f)
                                launch { ripScale.animateTo(1f, tween(500)) }
                                launch { ripAlpha.animateTo(0f, tween(500)) }
                                delay(500); showRipple = false
                            }
                        }
                        if (el < 2700) showRipple = false
                    }
                    2 -> {
                        val t = ms * 0.001f
                        curX = 0.5f + sin(t * 0.7f) * 0.28f
                        curY = 0.18f + cos(t * 0.5f) * 0.12f
                    }
                }
            }
        }
    }

    var typedText by remember { mutableStateOf("") }
    var litKey by remember { mutableStateOf("") }
    LaunchedEffect(mode) {
        if (mode == 1) {
            val word = "mouskey"
            while (true) {
                typedText = ""; litKey = ""
                for (ch in word) { litKey = ch.uppercase(); typedText += ch; delay(280) }
                litKey = ""; delay(280 * 6)
            }
        } else { typedText = ""; litKey = "" }
    }

    var flashKey by remember { mutableStateOf("") }
    LaunchedEffect(mode) {
        if (mode == 2) {
            val chars = "QWERTYUIOPASDFGHJKLZXCVBNM"
            while (true) {
                delay(80)
                if (kotlin.random.Random.nextFloat() < 0.1f) {
                    flashKey = chars[kotlin.random.Random.nextInt(chars.length)].toString()
                    delay(200); flashKey = ""
                }
            }
        } else { flashKey = "" }
    }

    val progFrac = remember { Animatable(0f) }
    LaunchedEffect(mode) {
        progFrac.snapTo(0f)
        progFrac.animateTo(1f, tween(modeDurs[mode].toInt(), easing = LinearEasing))
    }

    val modeLabel by remember(mode) { mutableStateOf(arrayOf("Trackpad", "Keyboard", "Split Mode")[mode]) }
    val labelAlpha = remember { Animatable(0f) }
    LaunchedEffect(mode) {
        labelAlpha.snapTo(0f)
        labelAlpha.animateTo(1f, tween(300))
    }

    val letters = "Mouskey"
    val lScale = remember { letters.map { Animatable(1.6f) } }
    val lAlpha = remember { letters.map { Animatable(0f) } }
    val lY = remember { letters.map { Animatable(-12f) } }
    LaunchedEffect(Unit) {
        letters.forEachIndexed { i, _ ->
            launch {
                delay(150L + i * 70L)
                launch { lAlpha[i].animateTo(1f, tween(80)) }
                launch {
                    lScale[i].animateTo(0.93f, tween(187, easing = stampEasing))
                    lScale[i].animateTo(1.03f, tween(75))
                    lScale[i].animateTo(1f, tween(75))
                }
                launch {
                    lY[i].animateTo(1f, tween(187, easing = stampEasing))
                    lY[i].animateTo(-1f, tween(75))
                    lY[i].animateTo(0f, tween(75))
                }
            }
        }
    }

    val headerAlpha = remember { Animatable(0f) }
    val headerY = remember { Animatable(10f) }
    val phoneAlpha = remember { Animatable(0f) }
    val phoneY = remember { Animatable(10f) }
    val progBarAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        launch { delay(100); launch { headerAlpha.animateTo(1f, tween(600)) }; launch { headerY.animateTo(0f, tween(600, easing = CubicBezierEasing(0.2f, 1f, 0.3f, 1f))) } }
        launch { delay(500); launch { phoneAlpha.animateTo(1f, tween(700)) }; launch { phoneY.animateTo(0f, tween(700, easing = CubicBezierEasing(0.2f, 1f, 0.3f, 1f))) } }
        launch { delay(1200); progBarAlpha.animateTo(1f, tween(400)) }
    }

    val bg = Color(0xFFece6dc); val ink = Color(0xFF2e2a24); val mute = Color(0xFF7d756a)
    val accent = Color(0xFF6b5e4f); val surface = Color(0xFFcac3b8); val surfHi = Color(0xFFd4cec4)
    val keyFace = Color(0xFFe8e3da); val keySide = Color(0xFFc4bdb2); val active = Color(0xFF7a6e5f)

    data class KD(val label: String, val l: String = "", val flex: Float = 1f, val sup: String = "")

    val fnRow = remember { listOf(KD("Esc","",1.3f), KD("*"), KD("*"), KD("◂"), KD("◂◂"), KD("▸▸"), KD("▸|"), KD("||"), KD("▸▸|")) }
    val qRow = remember { "qwertyuiop".mapIndexed { i, c -> KD(c.toString(), c.uppercase(), sup = if (i == 9) "0" else "${i+1}") } }
    val aRow = remember { "asdfghjkl".map { KD(it.toString(), it.uppercase()) } }
    val zRow = remember { listOf(KD("⇧","",1.3f)) + "zxcvbnm".map { KD(it.toString(), it.uppercase()) } + listOf(KD("⌫","",1.3f)) }
    val modRow = remember { listOf(KD("Fn","",1.3f), KD("Ctrl","",1.3f), KD("Alt","",1.3f), KD(""," ",3.5f), KD("."), KD("Enter","",1.3f)) }
    val allRows = remember { listOf(fnRow, qRow, aRow, zRow, modRow) }

    Box(
        modifier = modifier.fillMaxSize().background(bg)
            .clickable(indication = null, interactionSource = remember { MutableInteractionSource() }) { onFinish() }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val spacing = 28.dp.toPx(); val dotR = 0.6.dp.toPx()
            val cx = size.width / 2f; val cy = size.height * 0.4f
            val rx = size.width * 0.4f; val ry = size.height * 0.3f
            for (col in -1..(size.width / spacing).toInt() + 1) {
                for (row in -1..(size.height / spacing).toInt() + 1) {
                    val px = col * spacing; val py = row * spacing
                    val dx = (px - cx) / rx; val dy = (py - cy) / ry
                    val d = sqrt(dx * dx + dy * dy)
                    if (d < 1f) drawCircle(mute.copy(alpha = (1f - d) * 0.08f), dotR, Offset(px, py))
                }
            }
        }

        Column(
            modifier = Modifier.fillMaxSize().padding(top = 40.dp, start = 20.dp, end = 20.dp, bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.graphicsLayer { alpha = headerAlpha.value; translationY = headerY.value * 3f }
            ) {
                Row {
                    letters.forEachIndexed { i, ch ->
                        Text(
                            ch.toString(), color = ink, fontSize = 30.sp, fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily(Font(R.font.caveat_bold)), letterSpacing = 1.sp,
                            modifier = Modifier.graphicsLayer {
                                scaleX = lScale[i].value; scaleY = lScale[i].value
                                translationY = lY[i].value * 2f; alpha = lAlpha[i].value
                            }
                        )
                    }
                }
                Spacer(Modifier.height(5.dp))
                Text(
                    modeLabel, color = mute, fontSize = 12.sp, fontWeight = FontWeight.Medium,
                    letterSpacing = 0.5.sp,
                    modifier = Modifier.graphicsLayer { alpha = labelAlpha.value }
                )
            }

            Spacer(Modifier.height(14.dp))

            Box(
                modifier = Modifier.weight(1f).graphicsLayer { alpha = phoneAlpha.value; translationY = phoneY.value * 3f },
                contentAlignment = Alignment.Center
            ) {
                val phoneW = 290.dp; val phoneH = 536.dp
                val si = 11.dp
                val scrR = 27.dp
                val surfR = 16.dp

                Box(
                    modifier = Modifier.size(phoneW, phoneH)
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(surfHi, surface)
                            ),
                            shape = RoundedCornerShape(38.dp)
                        )
                        .border(1.dp, Color(0x14808080), RoundedCornerShape(38.dp))
                ) {
                    Box(
                        modifier = Modifier.padding(si)
                            .fillMaxSize()
                            .background(
                                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                    listOf(surface, Color(0xFFb6b0a6))
                                ),
                                shape = RoundedCornerShape(scrR)
                            )
                            .border(1.dp, Color(0x10000000), RoundedCornerShape(scrR))
                            .clip(RoundedCornerShape(scrR))
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(start = si, end = si, top = si)
                                .fillMaxWidth()
                                .fillMaxHeight(1f - surfBot)
                                .graphicsLayer { alpha = surfAlpha }
                                .background(
                                    brush = androidx.compose.ui.graphics.Brush.verticalGradient(listOf(surfHi, surface)),
                                    shape = RoundedCornerShape(surfR)
                                )
                                .border(1.dp, Color(0x18808080), RoundedCornerShape(surfR))
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth().fillMaxHeight(0.35f)
                                    .background(
                                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                            listOf(Color.White.copy(alpha = 0.12f), Color.Transparent)
                                        ),
                                        shape = RoundedCornerShape(topStart = surfR, topEnd = surfR)
                                    )
                            )
                            if (mode == 0) {
                                Box(
                                    modifier = Modifier.align(Alignment.BottomCenter)
                                        .padding(bottom = 11.dp)
                                        .fillMaxWidth(0.3f).height(1.5.dp)
                                        .background(Color(0x1F808080), RoundedCornerShape(1.dp))
                                )
                            }
                        }

                        if (divAlpha > 0f) {
                            Box(
                                modifier = Modifier.fillMaxWidth(0.44f)
                                    .height(3.dp)
                                    .align(Alignment.TopCenter)
                                    .offset(y = (phoneH - si * 2) * 0.64f)
                                    .graphicsLayer { alpha = divAlpha; scaleX = divAlpha }
                                    .background(Color(0x20808080), RoundedCornerShape(2.dp)),
                                contentAlignment = Alignment.Center
                            ) {
                                Box(
                                    modifier = Modifier.width(16.dp).height(3.dp)
                                        .background(Color(0x33808080), RoundedCornerShape(2.dp))
                                )
                            }
                        }

                        if (laptopAlpha > 0f) {
                            Column(
                                modifier = Modifier
                                    .padding(start = 22.dp, end = 22.dp, top = si, bottom = 179.dp)
                                    .fillMaxSize()
                                    .graphicsLayer {
                                        alpha = laptopAlpha
                                        translationY = (1f - laptopAlpha) * 36f
                                        scaleX = 0.92f + 0.08f * laptopAlpha
                                        scaleY = 0.92f + 0.08f * laptopAlpha
                                    },
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(0.88f)
                                        .aspectRatio(16f / 10f)
                                        .background(ink, RoundedCornerShape(topStart = 6.dp, topEnd = 6.dp))
                                        .padding(8.dp)
                                ) {
                                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                        Box(Modifier.size(4.dp).background(Color(0x4DFF5F57), CircleShape))
                                        Box(Modifier.size(4.dp).background(Color(0x4DFEBC2E), CircleShape))
                                        Box(Modifier.size(4.dp).background(Color(0x4D28C840), CircleShape))
                                    }
                                    Spacer(Modifier.height(5.dp))
                                    Text(
                                        typedText + "█", color = bg.copy(alpha = 0.85f),
                                        fontSize = 10.sp, fontFamily = FontFamily.Monospace
                                    )
                                }
                                Box(
                                    modifier = Modifier.fillMaxWidth().height(5.dp)
                                        .background(
                                            brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                                                listOf(Color(0xFFABA49A), surface)
                                            ),
                                            shape = RoundedCornerShape(bottomStart = 4.dp, bottomEnd = 4.dp)
                                        )
                                ) {
                                    Box(
                                        modifier = Modifier.align(Alignment.TopCenter)
                                            .fillMaxWidth(0.4f).height(1.5.dp)
                                            .background(Color(0x26808080), RoundedCornerShape(bottomStart = 2.dp, bottomEnd = 2.dp))
                                    )
                                }
                                Spacer(Modifier.height(6.dp))
                                Text(
                                    "TYPE ON PHONE, SEE IT HERE", color = mute,
                                    fontSize = 7.5.sp, fontWeight = FontWeight.Medium,
                                    letterSpacing = 0.8.sp
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.align(Alignment.BottomCenter)
                                .padding(start = si, end = si, bottom = si)
                                .fillMaxWidth()
                                .then(
                                    if (keysTopFrac < 1f) Modifier.fillMaxHeight(1f - keysTopFrac)
                                    else Modifier.height(156.dp)
                                )
                                .graphicsLayer {
                                    alpha = keysAlpha
                                    translationY = keysOffY * 3f
                                    scaleX = if (mode == 0) 0.9f + 0.1f * keysAlpha else 1f
                                    scaleY = if (mode == 0) 0.9f + 0.1f * keysAlpha else 1f
                                },
                            verticalArrangement = Arrangement.spacedBy(2.dp)
                        ) {
                            allRows.forEachIndexed { rowIdx, row ->
                                Row(
                                    modifier = Modifier.weight(1f).fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                                ) {
                                    row.forEach { key ->
                                        val isLit = (key.l.isNotEmpty() && (key.l == litKey || key.l == flashKey))
                                        Box(modifier = Modifier.weight(key.flex).fillMaxHeight()) {
                                            Box(
                                                modifier = Modifier.fillMaxSize()
                                                    .offset(y = 1.dp)
                                                    .background(if (isLit) active else keySide, RoundedCornerShape(3.dp))
                                            )
                                            Box(
                                                modifier = Modifier.fillMaxSize()
                                                    .padding(bottom = 2.dp)
                                                    .background(if (isLit) active else keyFace, RoundedCornerShape(3.dp)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Text(
                                                    key.label, color = if (isLit) bg else mute,
                                                    fontSize = if (rowIdx == 0 || key.flex > 1.1f) 5.5.sp else 7.sp,
                                                    fontWeight = if (rowIdx == 0 || key.flex > 1.1f) FontWeight.Medium else FontWeight.SemiBold,
                                                    letterSpacing = (-0.01).sp, maxLines = 1
                                                )
                                                if (key.sup.isNotEmpty()) {
                                                    Text(
                                                        key.sup, color = mute.copy(alpha = 0.45f),
                                                        fontSize = 3.5.sp, fontWeight = FontWeight.Normal,
                                                        modifier = Modifier.align(Alignment.TopEnd).padding(top = 1.dp, end = 2.dp)
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        if (curAlpha > 0f) {
                            Canvas(modifier = Modifier.fillMaxSize().graphicsLayer { alpha = curAlpha }) {
                                val sx = size.width; val sy = size.height
                                val cx2 = curX * sx; val cy2 = curY * sy
                                val cursorPath = Path().apply {
                                    moveTo(cx2 + 3f, cy2 + 1f)
                                    lineTo(cx2 + 3f, cy2 + 14f)
                                    lineTo(cx2 + 6.5f, cy2 + 10.8f)
                                    lineTo(cx2 + 10f, cy2 + 17f)
                                    lineTo(cx2 + 13f, cy2 + 15f)
                                    lineTo(cx2 + 9.2f, cy2 + 8.5f)
                                    lineTo(cx2 + 14f, cy2 + 7.5f)
                                    close()
                                }
                                drawPath(cursorPath, ink.copy(alpha = 0.85f))
                            }
                        }

                        if (showRipple && curAlpha > 0f) {
                            Canvas(modifier = Modifier.fillMaxSize()) {
                                val sx = size.width; val sy = size.height
                                val rx2 = ripX * sx; val ry2 = ripY * sy
                                val r = 10.dp.toPx() * ripScale.value
                                drawCircle(
                                    color = accent.copy(alpha = ripAlpha.value * 0.2f),
                                    radius = r, center = Offset(rx2, ry2),
                                    style = Stroke(1.dp.toPx())
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(14.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.graphicsLayer { alpha = progBarAlpha.value }
            ) {
                for (i in 0 until 3) {
                    Box(modifier = Modifier.width(30.dp).height(3.dp).background(Color(0x1F808080), RoundedCornerShape(2.dp))) {
                        if (i == mode) {
                            Box(modifier = Modifier.fillMaxHeight().fillMaxWidth(progFrac.value).background(accent, RoundedCornerShape(2.dp)))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ConnectScreen(
    isBluetoothConnected: Boolean = false,
    onConnected: () -> Unit = {},
    onRetryConnection: () -> Unit = {},
    onSetupLater: () -> Unit,
    modifier: Modifier = Modifier
) {
    LaunchedEffect(isBluetoothConnected) {
        if (isBluetoothConnected) {
            delay(800L)
            onConnected()
        }
    }

    LaunchedEffect(Unit) {
        while (true) {
            delay(3000L)
            if (!isBluetoothConnected) {
                onRetryConnection()
            }
        }
    }

    val bg = Color(0xFFece6dc)
    val ink = Color(0xFF2e2a24)
    val mute = Color(0xFF7d756a)
    val accent = Color(0xFF6b5e4f)
    val card = Color(0xFFf2ede6)
    val stepBg = Color(0x0B6B5E4F)
    val winBg = Color(0xFFf5f1eb)
    val winBorder = Color(0x1A6B5E4F)
    val winHover = Color(0x0D6B5E4F)

    val brandAlpha = remember { Animatable(0f) }
    val brandY = remember { Animatable(10f) }
    val headAlpha = remember { Animatable(0f) }
    val headY = remember { Animatable(10f) }
    val subAlpha = remember { Animatable(0f) }
    val subY = remember { Animatable(10f) }
    val devAlpha = remember { Animatable(0f) }
    val devY = remember { Animatable(10f) }
    val panelAlpha = remember { Animatable(0f) }
    val panelY = remember { Animatable(10f) }
    val step1Alpha = remember { Animatable(0f) }
    val step1Y = remember { Animatable(6f) }
    val step2Alpha = remember { Animatable(0f) }
    val step2Y = remember { Animatable(6f) }
    val step3Alpha = remember { Animatable(0f) }
    val step3Y = remember { Animatable(6f) }
    val skipAlpha = remember { Animatable(0f) }
    val skipY = remember { Animatable(10f) }

    val easeOut = CubicBezierEasing(0.2f, 1f, 0.3f, 1f)

    LaunchedEffect(Unit) {
        launch { delay(150); launch { brandAlpha.animateTo(1f, tween(600, easing = easeOut)) }; launch { brandY.animateTo(0f, tween(600, easing = easeOut)) } }
        launch { delay(350); launch { headAlpha.animateTo(1f, tween(600, easing = easeOut)) }; launch { headY.animateTo(0f, tween(600, easing = easeOut)) } }
        launch { delay(550); launch { subAlpha.animateTo(1f, tween(500)) }; launch { subY.animateTo(0f, tween(500)) } }
        launch { delay(600); launch { devAlpha.animateTo(1f, tween(700, easing = easeOut)) }; launch { devY.animateTo(0f, tween(700, easing = easeOut)) } }
        launch { delay(850); launch { panelAlpha.animateTo(1f, tween(600, easing = easeOut)) }; launch { panelY.animateTo(0f, tween(600, easing = easeOut)) } }
        launch { delay(1200); launch { step1Alpha.animateTo(1f, tween(400, easing = easeOut)) }; launch { step1Y.animateTo(0f, tween(400, easing = easeOut)) } }
        launch { delay(1450); launch { step2Alpha.animateTo(1f, tween(400, easing = easeOut)) }; launch { step2Y.animateTo(0f, tween(400, easing = easeOut)) } }
        launch { delay(1700); launch { step3Alpha.animateTo(1f, tween(400, easing = easeOut)) }; launch { step3Y.animateTo(0f, tween(400, easing = easeOut)) } }
        launch { delay(2000); launch { skipAlpha.animateTo(1f, tween(400)) }; launch { skipY.animateTo(0f, tween(400)) } }
    }

    // Dot travel animation
    val dotPhase = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            dotPhase.snapTo(0f)
            dotPhase.animateTo(1f, tween(2000, easing = LinearEasing))
        }
    }

    // Cursor drift animation
    val cursorTime = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            cursorTime.snapTo(0f)
            cursorTime.animateTo(1f, tween(3000, easing = LinearEasing))
        }
    }

    // BT ring pulse
    val ring1Scale = remember { Animatable(0.8f) }
    val ring1Alpha = remember { Animatable(0.3f) }
    val ring2Scale = remember { Animatable(0.8f) }
    val ring2Alpha = remember { Animatable(0.3f) }
    LaunchedEffect(Unit) {
        launch {
            while (true) {
                ring1Scale.snapTo(0.8f); ring1Alpha.snapTo(0.3f)
                launch { ring1Scale.animateTo(1.35f, tween(2800)) }
                launch { ring1Alpha.animateTo(0f, tween(2800)) }
                delay(2800)
            }
        }
        launch {
            delay(900)
            while (true) {
                ring2Scale.snapTo(0.8f); ring2Alpha.snapTo(0.3f)
                launch { ring2Scale.animateTo(1.35f, tween(2800)) }
                launch { ring2Alpha.animateTo(0f, tween(2800)) }
                delay(2800)
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize().background(bg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 52.dp, start = 28.dp, end = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Brand
            Text(
                "Mouskey", color = ink, fontSize = 34.sp, fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.caveat_bold)), letterSpacing = 1.sp,
                modifier = Modifier.graphicsLayer { alpha = brandAlpha.value; translationY = brandY.value * 3f }
            )

            Spacer(Modifier.height(8.dp))

            // Headline
            Text(
                text = buildAnnotatedString {
                    append("Your phone, your\n")
                    withStyle(SpanStyle(color = accent)) {
                        append("trackpad & keyboard")
                    }
                },
                color = ink, fontSize = 21.sp, fontWeight = FontWeight.Bold,
                lineHeight = 26.sp, textAlign = TextAlign.Center,
                letterSpacing = (-0.02).sp,
                modifier = Modifier.graphicsLayer { alpha = headAlpha.value; translationY = headY.value * 3f }
            )

            Spacer(Modifier.height(6.dp))

            // Subtitle
            Text(
                "Control your computer wirelessly — no apps to install, no cables needed.",
                color = mute, fontSize = 12.5.sp, fontWeight = FontWeight.Normal,
                lineHeight = 19.sp, textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.75f)
                    .graphicsLayer { alpha = subAlpha.value; translationY = subY.value * 3f }
            )
        }

        Spacer(Modifier.height(20.dp))

        // Device illustration row
        Row(
            modifier = Modifier.height(100.dp)
                .graphicsLayer { alpha = devAlpha.value; translationY = devY.value * 3f },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            // Phone device
            Box(
                modifier = Modifier.size(46.dp, 78.dp)
                    .border(2.5.dp, accent, RoundedCornerShape(11.dp))
                    .background(card, RoundedCornerShape(11.dp))
                    .padding(6.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    // Surface
                    Box(
                        modifier = Modifier.size(26.dp, 18.dp)
                            .background(stepBg, RoundedCornerShape(3.dp))
                            .border(1.dp, Color(0x146B5E4F), RoundedCornerShape(3.dp))
                    )
                    // Keys grid
                    Column(verticalArrangement = Arrangement.spacedBy(1.dp)) {
                        for (r in 0 until 3) {
                            Row(horizontalArrangement = Arrangement.spacedBy(1.dp)) {
                                for (c in 0 until 5) {
                                    Box(
                                        modifier = Modifier.size(4.8.dp, 3.dp)
                                            .background(accent.copy(alpha = 0.2f), RoundedCornerShape(1.dp))
                                    )
                                }
                            }
                        }
                    }
                }
                // Home indicator
                Box(
                    modifier = Modifier.align(Alignment.BottomCenter)
                        .offset(y = 2.dp)
                        .size(12.dp, 2.dp)
                        .background(mute.copy(alpha = 0.25f), RoundedCornerShape(2.dp))
                )
            }

            // Connection segment 1
            Box(modifier = Modifier.width(32.dp).height(4.dp)) {
                Box(modifier = Modifier.fillMaxWidth().height(2.dp).align(Alignment.Center)
                    .background(accent.copy(alpha = 0.1f), RoundedCornerShape(1.dp)))
                // Animated dots
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val p1 = dotPhase.value
                    val a1 = if (p1 < 0.15f || p1 > 0.85f) 0f else 0.6f
                    drawCircle(accent.copy(alpha = a1), 2.dp.toPx(), Offset(p1 * (w - 4.dp.toPx()), size.height / 2f))
                    val p2 = (dotPhase.value - 0.3f).coerceIn(0f, 1f) / 0.7f
                    val a2 = if (p2 <= 0f || p2 >= 1f || dotPhase.value < 0.3f) 0f else 0.6f
                    drawCircle(accent.copy(alpha = a2), 2.dp.toPx(), Offset(p2 * (w - 4.dp.toPx()), size.height / 2f))
                }
            }

            // Bluetooth badge
            Box(
                modifier = Modifier.size(36.dp),
                contentAlignment = Alignment.Center
            ) {
                // Pulsing rings
                Box(
                    modifier = Modifier.size(46.dp)
                        .graphicsLayer { scaleX = ring1Scale.value; scaleY = ring1Scale.value; alpha = ring1Alpha.value }
                        .border(1.5.dp, accent, RoundedCornerShape(15.dp))
                )
                Box(
                    modifier = Modifier.size(46.dp)
                        .graphicsLayer { scaleX = ring2Scale.value; scaleY = ring2Scale.value; alpha = ring2Alpha.value }
                        .border(1.5.dp, accent, RoundedCornerShape(15.dp))
                )
                // Badge
                Box(
                    modifier = Modifier.size(36.dp)
                        .background(
                            brush = androidx.compose.ui.graphics.Brush.linearGradient(
                                listOf(accent, Color(0xFF47403a)),
                                start = Offset(0f, 0f), end = Offset(36f, 36f)
                            ),
                            shape = RoundedCornerShape(11.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    // BT icon
                    Canvas(modifier = Modifier.size(16.dp)) {
                        val s = size.width
                        val path = Path().apply {
                            moveTo(s * 0.27f, s * 0.27f)
                            lineTo(s * 0.73f, s * 0.73f)
                            lineTo(s * 0.5f, s * 0.96f)
                            lineTo(s * 0.5f, s * 0.04f)
                            lineTo(s * 0.73f, s * 0.27f)
                            lineTo(s * 0.27f, s * 0.73f)
                        }
                        drawPath(path, Color.White, style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                    }
                }
            }

            // Connection segment 2
            Box(modifier = Modifier.width(32.dp).height(4.dp)) {
                Box(modifier = Modifier.fillMaxWidth().height(2.dp).align(Alignment.Center)
                    .background(accent.copy(alpha = 0.1f), RoundedCornerShape(1.dp)))
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val p1 = dotPhase.value
                    val a1 = if (p1 < 0.15f || p1 > 0.85f) 0f else 0.6f
                    drawCircle(accent.copy(alpha = a1), 2.dp.toPx(), Offset(p1 * (w - 4.dp.toPx()), size.height / 2f))
                    val p2 = (dotPhase.value - 0.3f).coerceIn(0f, 1f) / 0.7f
                    val a2 = if (p2 <= 0f || p2 >= 1f || dotPhase.value < 0.3f) 0f else 0.6f
                    drawCircle(accent.copy(alpha = a2), 2.dp.toPx(), Offset(p2 * (w - 4.dp.toPx()), size.height / 2f))
                }
            }

            // Laptop device
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Lid
                Box(
                    modifier = Modifier.size(66.dp, 44.dp)
                        .border(2.5.dp, accent, RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp, bottomStart = 0.dp, bottomEnd = 0.dp))
                        .background(card, RoundedCornerShape(topStart = 5.dp, topEnd = 5.dp, bottomStart = 0.dp, bottomEnd = 0.dp))
                        .padding(6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Screen
                    Box(
                        modifier = Modifier.size(48.dp, 28.dp)
                            .background(stepBg, RoundedCornerShape(3.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        // Cursor dot
                        val cx = 6f * sin(cursorTime.value * 2f * 3.14159f * 0.25f + 0.5f)
                        val cy = 4f * cos(cursorTime.value * 2f * 3.14159f * 0.5f)
                        Box(
                            modifier = Modifier.size(6.dp)
                                .offset(x = cx.dp, y = cy.dp)
                                .background(accent.copy(alpha = 0.4f), CircleShape)
                        )
                    }
                }
                // Base
                Box(
                    modifier = Modifier.size(76.dp, 4.dp)
                        .background(accent.copy(alpha = 0.15f), RoundedCornerShape(bottomStart = 3.dp, bottomEnd = 3.dp))
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // OS-style panel
        Column(
            modifier = Modifier.fillMaxWidth().weight(1f)
                .padding(horizontal = 28.dp)
                .graphicsLayer { alpha = panelAlpha.value; translationY = panelY.value * 3f }
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .background(winBg, RoundedCornerShape(14.dp))
                    .border(1.dp, winBorder, RoundedCornerShape(14.dp))
            ) {
                // Titlebar
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Window dots
                    Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                        Box(Modifier.size(7.dp).background(mute.copy(alpha = 0.2f), CircleShape))
                        Box(Modifier.size(7.dp).background(mute.copy(alpha = 0.2f), CircleShape))
                        Box(Modifier.size(7.dp).background(mute.copy(alpha = 0.2f), CircleShape))
                    }
                    Text(
                        "Bluetooth setup", color = mute, fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold, letterSpacing = 0.02.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(36.dp))
                }

                // Divider
                Box(Modifier.fillMaxWidth().height(1.dp).background(winBorder))

                // Body
                Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 14.dp)) {
                    // Bluetooth toggle row
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(bottom = 12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Bluetooth", color = ink, fontSize = 13.5.sp, fontWeight = FontWeight.SemiBold)
                        // Toggle (static, always on)
                        Box(
                            modifier = Modifier.size(38.dp, 22.dp)
                                .background(accent, RoundedCornerShape(11.dp)),
                            contentAlignment = Alignment.CenterEnd
                        ) {
                            Box(
                                modifier = Modifier.padding(3.dp).size(16.dp)
                                    .background(winBg, CircleShape)
                            )
                        }
                    }

                    // Divider
                    Box(Modifier.fillMaxWidth().height(1.dp).background(winBorder))

                    Spacer(Modifier.height(14.dp))

                    // Steps
                    // Step 1
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .graphicsLayer { alpha = step1Alpha.value; translationY = step1Y.value * 3f }
                            .background(winHover, RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(26.dp)
                                .border(1.5.dp, accent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("1", color = accent, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily(Font(R.font.caveat_bold)))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Open Bluetooth settings", color = ink, fontSize = 12.5.sp,
                                fontWeight = FontWeight.SemiBold, lineHeight = 16.sp)
                            Text("Settings → Bluetooth & devices", color = mute, fontSize = 10.sp,
                                lineHeight = 13.sp)
                        }
                    }

                    Spacer(Modifier.height(6.dp))

                    // Step 2
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .graphicsLayer { alpha = step2Alpha.value; translationY = step2Y.value * 3f }
                            .background(winHover, RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(26.dp)
                                .border(1.5.dp, accent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("2", color = accent, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily(Font(R.font.caveat_bold)))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Add a new device", color = ink, fontSize = 12.5.sp,
                                fontWeight = FontWeight.SemiBold, lineHeight = 16.sp)
                            Text("Choose Bluetooth from the list", color = mute, fontSize = 10.sp,
                                lineHeight = 13.sp)
                        }
                    }

                    Spacer(Modifier.height(6.dp))

                    // Step 3
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .graphicsLayer { alpha = step3Alpha.value; translationY = step3Y.value * 3f }
                            .background(winHover, RoundedCornerShape(10.dp))
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier.size(26.dp)
                                .border(1.5.dp, accent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("3", color = accent, fontSize = 14.sp, fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily(Font(R.font.caveat_bold)))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column {
                            Text("Select your phone", color = ink, fontSize = 12.5.sp,
                                fontWeight = FontWeight.SemiBold, lineHeight = 16.sp)
                            Text("It shows by its Bluetooth name", color = mute, fontSize = 10.sp,
                                lineHeight = 13.sp)
                        }
                    }
                }

                Spacer(Modifier.weight(1f))

                // Skip button
                Box(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "I'll set up later", color = mute, fontSize = 12.sp,
                        fontWeight = FontWeight.Medium, letterSpacing = 0.02.sp,
                        modifier = Modifier
                            .graphicsLayer { alpha = skipAlpha.value; translationY = skipY.value * 3f }
                            .border(1.5.dp, winBorder, RoundedCornerShape(20.dp))
                            .background(winBg, RoundedCornerShape(20.dp))
                            .clickable { onSetupLater() }
                            .padding(horizontal = 28.dp, vertical = 9.dp)
                    )
                }
            }
        }

        Spacer(Modifier.height(28.dp))
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
fun HomeScreen(
    isBluetoothConnected: Boolean,
    hasBondedComputer: Boolean = false,
    onRetryConnection: () -> Unit = {},
    onModeSelected: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    val bg = Color(0xFFece6dc)
    val ink = Color(0xFF2e2a24)
    val mute = Color(0xFF7d756a)
    val accent = Color(0xFF6b5e4f)
    val winBg = Color(0xFFf5f1eb)
    val winBorder = Color(0x1A6B5E4F)
    val tileBg = Color(0xFFe4ded4)
    val tileBorder = Color(0x0D000000)
    val keyFace = Color(0xFFf0ebe3)
    val keySide = Color(0xFFbfb8ac)
    val keyText = Color(0x8C3C3428)
    val keyStroke = Color(0x14000000)
    val tpSurface = Color(0xFFd6d0c5)
    val tpBorder = Color(0x12000000)

    var showBtDialog by remember { mutableStateOf(false) }
    var pendingMode by remember { mutableStateOf<AppScreen?>(null) }
    var showConnecting by remember { mutableStateOf(false) }

    val easeOut = CubicBezierEasing(0.2f, 1f, 0.3f, 1f)

    val brandAlpha = remember { Animatable(0f) }
    val brandY = remember { Animatable(10f) }
    val headAlpha = remember { Animatable(0f) }
    val headY = remember { Animatable(10f) }
    val subAlpha = remember { Animatable(0f) }
    val subY = remember { Animatable(10f) }
    val panelAlpha = remember { Animatable(0f) }
    val panelY = remember { Animatable(10f) }
    val mod1Alpha = remember { Animatable(0f) }
    val mod1Y = remember { Animatable(8f) }
    val mod2Alpha = remember { Animatable(0f) }
    val mod2Y = remember { Animatable(8f) }
    val mod3Alpha = remember { Animatable(0f) }
    val mod3Y = remember { Animatable(8f) }

    LaunchedEffect(Unit) {
        launch { delay(150); launch { brandAlpha.animateTo(1f, tween(600, easing = easeOut)) }; launch { brandY.animateTo(0f, tween(600, easing = easeOut)) } }
        launch { delay(350); launch { headAlpha.animateTo(1f, tween(600, easing = easeOut)) }; launch { headY.animateTo(0f, tween(600, easing = easeOut)) } }
        launch { delay(550); launch { subAlpha.animateTo(1f, tween(500)) }; launch { subY.animateTo(0f, tween(500)) } }
        launch { delay(700); launch { panelAlpha.animateTo(1f, tween(600, easing = easeOut)) }; launch { panelY.animateTo(0f, tween(600, easing = easeOut)) } }
        launch { delay(1000); launch { mod1Alpha.animateTo(1f, tween(500, easing = easeOut)) }; launch { mod1Y.animateTo(0f, tween(500, easing = easeOut)) } }
        launch { delay(1100); launch { mod2Alpha.animateTo(1f, tween(500, easing = easeOut)) }; launch { mod2Y.animateTo(0f, tween(500, easing = easeOut)) } }
        launch { delay(1200); launch { mod3Alpha.animateTo(1f, tween(500, easing = easeOut)) }; launch { mod3Y.animateTo(0f, tween(500, easing = easeOut)) } }
    }

    // Trackpad cursor animation
    val cursorTime = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            cursorTime.snapTo(0f)
            cursorTime.animateTo(1f, tween(4600, easing = LinearEasing))
        }
    }

    // Split cursor animation
    val splitTime = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        while (true) {
            splitTime.snapTo(0f)
            splitTime.animateTo(1f, tween(3000, easing = LinearEasing))
        }
    }

    // Keyboard typing animation
    val kbTypingIndex = remember { mutableStateOf(0) }
    val kbCaretVisible = remember { mutableStateOf(true) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(300)
            kbTypingIndex.value = (kbTypingIndex.value + 1) % 14
        }
    }
    LaunchedEffect(Unit) {
        while (true) {
            delay(530)
            kbCaretVisible.value = !kbCaretVisible.value
        }
    }

    LaunchedEffect(isBluetoothConnected) {
        if (isBluetoothConnected) {
            showConnecting = false
            showBtDialog = false
            if (pendingMode != null) {
                val mode = pendingMode!!
                pendingMode = null
                delay(300L)
                onModeSelected(mode)
            }
        }
    }

    LaunchedEffect(isBluetoothConnected) {
        if (!isBluetoothConnected) {
            while (true) {
                delay(1500L)
                onRetryConnection()
            }
        }
    }

    fun handleModeTap(mode: AppScreen) {
        if (isBluetoothConnected) {
            onModeSelected(mode)
        } else {
            pendingMode = mode
            if (hasBondedComputer) {
                showConnecting = true
                onRetryConnection()
            } else {
                showBtDialog = true
            }
        }
    }

    Column(
        modifier = modifier.fillMaxSize().background(bg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(top = 52.dp, start = 28.dp, end = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Mouskey", color = ink, fontSize = 34.sp, fontWeight = FontWeight.Bold,
                fontFamily = FontFamily(Font(R.font.caveat_bold)), letterSpacing = 1.sp,
                modifier = Modifier.graphicsLayer { alpha = brandAlpha.value; translationY = brandY.value * 3f }
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = buildAnnotatedString {
                    append("Your phone, your\n")
                    withStyle(SpanStyle(color = accent)) { append("trackpad & keyboard") }
                },
                color = ink, fontSize = 21.sp, fontWeight = FontWeight.Bold,
                lineHeight = 26.sp, textAlign = TextAlign.Center, letterSpacing = (-0.02).sp,
                modifier = Modifier.graphicsLayer { alpha = headAlpha.value; translationY = headY.value * 3f }
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "Control your computer wirelessly — no apps to install, no cables needed.",
                color = mute, fontSize = 12.5.sp, fontWeight = FontWeight.Normal,
                lineHeight = 19.sp, textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(0.75f)
                    .graphicsLayer { alpha = subAlpha.value; translationY = subY.value * 3f }
            )
        }

        Spacer(Modifier.height(20.dp))

        // OS-style panel
        Column(
            modifier = Modifier.fillMaxWidth().weight(1f)
                .padding(horizontal = 28.dp)
                .graphicsLayer { alpha = panelAlpha.value; translationY = panelY.value * 3f }
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
                    .background(winBg, RoundedCornerShape(14.dp))
                    .border(1.dp, winBorder, RoundedCornerShape(14.dp))
            ) {
                // Titlebar
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "Choose a mode", color = mute, fontSize = 10.5.sp,
                        fontWeight = FontWeight.SemiBold, letterSpacing = 0.02.sp
                    )
                }
                Box(Modifier.fillMaxWidth().height(1.dp).background(winBorder))

                // Body — mode grid
                Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Top row: Trackpad + Split
                        Row(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            horizontalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Trackpad tile
                            Column(
                                modifier = Modifier.weight(1f).fillMaxHeight()
                                    .graphicsLayer { alpha = mod1Alpha.value; translationY = mod1Y.value * 3f }
                                    .background(tileBg, RoundedCornerShape(12.dp))
                                    .border(1.dp, tileBorder, RoundedCornerShape(12.dp))
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { handleModeTap(AppScreen.TRACKPAD) }
                            ) {
                                // Illustration area — matches SVG viewBox 120x150
                                Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                                    Canvas(modifier = Modifier.fillMaxSize()) {
                                        val cw = size.width; val ch = size.height
                                        val vbW = 120f; val vbH = 150f
                                        val sc = minOf(cw / vbW, ch / vbH)
                                        val ox = (cw - vbW * sc) / 2f
                                        val oy = (ch - vbH * sc) / 2f
                                        fun sx(x: Float) = ox + x * sc
                                        fun sy(y: Float) = oy + y * sc
                                        fun ss(s: Float) = s * sc

                                        val t = cursorTime.value
                                        // Cursor waypoints in viewBox coords
                                        val pts = listOf(
                                            0f to Offset(60f, 60f),
                                            0.195f to Offset(85f, 35f),
                                            0.39f to Offset(78f, 90f),
                                            0.54f to Offset(35f, 70f),
                                            0.695f to Offset(68f, 110f),
                                            0.87f to Offset(60f, 60f)
                                        )
                                        var si = 0
                                        for (i in 0 until pts.size - 1) {
                                            if (t >= pts[i].first && t < pts[i + 1].first) { si = i; break }
                                        }
                                        if (t >= pts.last().first) si = pts.size - 2
                                        val a = pts[si]; val b = pts[si + 1]
                                        val p = ((t - a.first) / (b.first - a.first)).coerceIn(0f, 1f)
                                        val ep = if (p < 0.5f) 2f * p * p else 1f - (-2f * p + 2f).pow(2) / 2f
                                        val cx = a.second.x + (b.second.x - a.second.x) * ep
                                        val cy = a.second.y + (b.second.y - a.second.y) * ep

                                        val cursorPath = Path().apply {
                                            moveTo(sx(cx - 3.8f), sy(cy - 5.2f))
                                            lineTo(sx(cx - 3.8f), sy(cy + 3.8f))
                                            lineTo(sx(cx - 1.1f), sy(cy + 1.9f))
                                            lineTo(sx(cx + 1.4f), sy(cy + 6.6f))
                                            lineTo(sx(cx + 3f), sy(cy + 5.5f))
                                            lineTo(sx(cx + 0.9f), sy(cy + 0.9f))
                                            lineTo(sx(cx + 3.6f), sy(cy + 0.5f))
                                            close()
                                        }
                                        drawPath(cursorPath, ink.copy(alpha = 0.7f))

                                        // 3 dots at bottom
                                        drawCircle(keySide.copy(alpha = 0.4f), ss(1.2f), Offset(sx(54f), sy(140f)))
                                        drawCircle(accent.copy(alpha = 0.5f), ss(1.2f), Offset(sx(60f), sy(140f)))
                                        drawCircle(keySide.copy(alpha = 0.4f), ss(1.2f), Offset(sx(66f), sy(140f)))
                                    }
                                }
                                // Label strip
                                Box(
                                    modifier = Modifier.fillMaxWidth().height(30.dp)
                                        .drawBehind { drawLine(tileBorder, Offset(0f, 0f), Offset(size.width, 0f), 1f) }
                                        .padding(horizontal = 10.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Trackpad", color = accent, fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold, letterSpacing = 0.02.sp)
                                        Box(
                                            modifier = Modifier.size(16.dp)
                                                .background(accent.copy(alpha = 0.12f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Canvas(modifier = Modifier.size(7.dp)) {
                                                val path = Path().apply {
                                                    moveTo(size.width * 0.25f, size.height * 0.1f)
                                                    lineTo(size.width * 0.75f, size.height * 0.5f)
                                                    lineTo(size.width * 0.25f, size.height * 0.9f)
                                                }
                                                drawPath(path, accent, style = Stroke(width = 2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                                            }
                                        }
                                    }
                                }
                            }

                            // Split Mode tile
                            Column(
                                modifier = Modifier.weight(1f).fillMaxHeight()
                                    .graphicsLayer { alpha = mod2Alpha.value; translationY = mod2Y.value * 3f }
                                    .background(tileBg, RoundedCornerShape(12.dp))
                                    .border(1.dp, tileBorder, RoundedCornerShape(12.dp))
                                    .clip(RoundedCornerShape(12.dp))
                                    .clickable { handleModeTap(AppScreen.SPLIT) }
                            ) {
                                // Split illustration
                                Column(
                                    modifier = Modifier.weight(1f).fillMaxWidth().padding(5.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    // Trackpad portion (62%) — matches SVG viewBox 100x100
                                    Box(
                                        modifier = Modifier.fillMaxWidth().weight(0.62f)
                                            .background(tpSurface, RoundedCornerShape(6.dp))
                                            .border(0.8.dp, tpBorder, RoundedCornerShape(6.dp))
                                    ) {
                                        Canvas(modifier = Modifier.fillMaxSize()) {
                                            val cw = size.width; val ch = size.height
                                            val vbS = 100f
                                            val sc = minOf(cw / vbS, ch / vbS)
                                            val osx = (cw - vbS * sc) / 2f
                                            val osy = (ch - vbS * sc) / 2f
                                            fun lx(x: Float) = osx + x * sc
                                            fun ly(y: Float) = osy + y * sc

                                            val st = splitTime.value * 2f * 3.14159f
                                            val cx = 50f + sin(st) * 32f
                                            val cy = 50f + cos(st * 0.7f) * 28f
                                            val cursorPath = Path().apply {
                                                moveTo(lx(cx - 3f), ly(cy - 4.2f))
                                                lineTo(lx(cx - 3f), ly(cy + 3f))
                                                lineTo(lx(cx - 0.8f), ly(cy + 1.5f))
                                                lineTo(lx(cx + 1.1f), ly(cy + 5.2f))
                                                lineTo(lx(cx + 2.4f), ly(cy + 4.3f))
                                                lineTo(lx(cx + 0.7f), ly(cy + 0.7f))
                                                lineTo(lx(cx + 2.8f), ly(cy + 0.4f))
                                                close()
                                            }
                                            drawPath(cursorPath, ink.copy(alpha = 0.7f))
                                        }
                                    }
                                    // Keyboard portion (38%)
                                    Column(
                                        modifier = Modifier.fillMaxWidth().weight(0.38f)
                                            .drawBehind {
                                                drawRoundRect(keySide, topLeft = Offset(0f, 1.5.dp.toPx()),
                                                    size = size.copy(), cornerRadius = CornerRadius(6.dp.toPx()))
                                            }
                                            .background(keyFace, RoundedCornerShape(6.dp))
                                            .border(0.8.dp, keyStroke, RoundedCornerShape(6.dp))
                                            .padding(horizontal = 5.dp, vertical = 4.dp),
                                        verticalArrangement = Arrangement.spacedBy(2.dp),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                    ) {
                                        repeat(4) {
                                            Box(
                                                modifier = Modifier.fillMaxWidth().height(2.5.dp)
                                                    .background(keySide.copy(alpha = 0.45f), RoundedCornerShape(1.2.dp))
                                            )
                                        }
                                        Box(
                                            modifier = Modifier.fillMaxWidth(0.55f).height(2.5.dp)
                                                .background(keySide.copy(alpha = 0.35f), RoundedCornerShape(1.2.dp))
                                        )
                                    }
                                }
                                // Label strip
                                Box(
                                    modifier = Modifier.fillMaxWidth().height(30.dp)
                                        .drawBehind { drawLine(tileBorder, Offset(0f, 0f), Offset(size.width, 0f), 1f) }
                                        .padding(horizontal = 10.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text("Split Mode", color = accent, fontSize = 10.5.sp,
                                            fontWeight = FontWeight.Bold, letterSpacing = 0.02.sp)
                                        Box(
                                            modifier = Modifier.size(16.dp)
                                                .background(accent.copy(alpha = 0.12f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Canvas(modifier = Modifier.size(7.dp)) {
                                                val path = Path().apply {
                                                    moveTo(size.width * 0.25f, size.height * 0.1f)
                                                    lineTo(size.width * 0.75f, size.height * 0.5f)
                                                    lineTo(size.width * 0.25f, size.height * 0.9f)
                                                }
                                                drawPath(path, accent, style = Stroke(width = 2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Bottom row: Keyboard (full width)
                        Column(
                            modifier = Modifier.fillMaxWidth().weight(1f)
                                .graphicsLayer { alpha = mod3Alpha.value; translationY = mod3Y.value * 3f }
                                .background(tileBg, RoundedCornerShape(12.dp))
                                .border(1.dp, tileBorder, RoundedCornerShape(12.dp))
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { handleModeTap(AppScreen.KEYBOARD) }
                        ) {
                            // Keyboard illustration — matches SVG viewBox 200x107
                            Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                                Canvas(modifier = Modifier.fillMaxSize()) {
                                    val cw = size.width
                                    val ch = size.height
                                    // Scale from 200x107 viewBox to fill
                                    val vbW = 200f; val vbH = 107f
                                    val sc = minOf(cw / vbW, ch / vbH)
                                    val ox = (cw - vbW * sc) / 2f
                                    val oy = (ch - vbH * sc) / 2f

                                    fun sx(x: Float) = ox + x * sc
                                    fun sy(y: Float) = oy + y * sc
                                    fun ss(s: Float) = s * sc

                                    val W = 14f; val H = 11.5f; val G = 2f; val R = 3f
                                    val r10 = 10f * W + 9f * G
                                    val kx10 = (200f - r10) / 2f
                                    val r9 = 9f * W + 8f * G
                                    val kx9 = (200f - r9) / 2f
                                    val KY0 = 10f; val KRH = 15f

                                    val word = "mouskey"
                                    val typedCount = if (kbTypingIndex.value < 7) kbTypingIndex.value else 7
                                    val litChar = if (kbTypingIndex.value < 7) word.getOrNull(kbTypingIndex.value)?.uppercaseChar() else if (kbTypingIndex.value == 7) ' ' else null

                                    fun drawKey(x: Float, y: Float, w: Float, h: Float, r: Float, isLit: Boolean, label: String = "", fs: Float = 5f) {
                                        val d = maxOf(2.2f, r * 0.75f)
                                        // Shadow/side
                                        drawRoundRect(
                                            if (isLit) Color(0xFF564a3d) else keySide,
                                            topLeft = Offset(sx(x), sy(y + d)),
                                            size = Size(ss(w), ss(h)),
                                            cornerRadius = CornerRadius(ss(r))
                                        )
                                        // Face
                                        drawRoundRect(
                                            if (isLit) accent else keyFace,
                                            topLeft = Offset(sx(x), sy(y)),
                                            size = Size(ss(w), ss(h)),
                                            cornerRadius = CornerRadius(ss(r))
                                        )
                                        drawRoundRect(
                                            keyStroke,
                                            topLeft = Offset(sx(x), sy(y)),
                                            size = Size(ss(w), ss(h)),
                                            cornerRadius = CornerRadius(ss(r)),
                                            style = Stroke(ss(0.5f))
                                        )
                                        // Label
                                        if (label.isNotEmpty() && fs > 0f) {
                                            val paint = android.graphics.Paint().apply {
                                                color = if (isLit) android.graphics.Color.argb(216, 255, 255, 255)
                                                else android.graphics.Color.argb(140, 60, 52, 40)
                                                textSize = ss(fs)
                                                typeface = android.graphics.Typeface.create("sans-serif-medium", android.graphics.Typeface.NORMAL)
                                                isAntiAlias = true
                                                textAlign = android.graphics.Paint.Align.CENTER
                                            }
                                            val dispLabel = if (label.length <= 2) label.lowercase() else label
                                            drawContext.canvas.nativeCanvas.drawText(
                                                dispLabel, sx(x + w / 2f), sy(y + h * 0.68f), paint
                                            )
                                        }
                                    }

                                    // Row 0 — Esc (lit) + 9 symbol keys
                                    drawKey(kx10, KY0, W, H, R, true, "Esc", 3.8f)
                                    val symRow = listOf("×","*","·","·","|","·","|","·","·")
                                    symRow.forEachIndexed { i, c ->
                                        drawKey(kx10 + (i + 1) * (W + G), KY0, W, H, R, false, c, 5f)
                                    }

                                    // Row 1 — QWERTY
                                    "QWERTYUIOP".forEachIndexed { i, c ->
                                        drawKey(kx10 + i * (W + G), KY0 + KRH, W, H, R, c == litChar, c.toString(), 5f)
                                    }

                                    // Row 2 — ASDF (9 keys, centered)
                                    "ASDFGHJKL".forEachIndexed { i, c ->
                                        drawKey(kx9 + i * (W + G), KY0 + KRH * 2, W, H, R, c == litChar, c.toString(), 5f)
                                    }

                                    // Row 3 — Shift + ZXCVBNM + Backspace
                                    val ksw = W * 1.5f
                                    val kz7 = 7f * W + 6f * G
                                    val kzt = ksw + G + kz7 + G + ksw
                                    val kzx = (200f - kzt) / 2f
                                    drawKey(kzx, KY0 + KRH * 3, ksw, H, R, false, "⇧", 5f)
                                    "ZXCVBNM".forEachIndexed { i, c ->
                                        drawKey(kzx + ksw + G + i * (W + G), KY0 + KRH * 3, W, H, R, c == litChar, c.toString(), 5f)
                                    }
                                    drawKey(kzx + kzt - ksw, KY0 + KRH * 3, ksw, H, R, false, "⌫", 4.5f)

                                    // Row 4 — Fn, Ctrl, Alt, Space, Enter
                                    val kbY4 = KY0 + KRH * 4
                                    val fnW = W * 1.1f; val ctrlW = W * 1.1f; val altW = W * 1.1f; val enterW = W * 1.6f
                                    drawKey(kx10, kbY4, fnW, H, R, false, "Fn", 4f)
                                    drawKey(kx10 + fnW + G, kbY4, ctrlW, H, R, false, "Ctrl", 3.5f)
                                    drawKey(kx10 + fnW + G + ctrlW + G, kbY4, altW, H, R, false, "Alt", 4f)
                                    val kAfter = kx10 + fnW + G + ctrlW + G + altW + G
                                    val kEnd = kx10 + r10
                                    val kSpW = kEnd - enterW - G - kAfter
                                    drawKey(kAfter, kbY4, kSpW, H, R, litChar == ' ', "", 0f)
                                    // Dot on spacebar
                                    drawCircle(
                                        keyText.copy(alpha = 0.4f), ss(0.8f),
                                        Offset(sx(kAfter + kSpW / 2f), sy(kbY4 + H / 2f + 0.5f))
                                    )
                                    drawKey(kEnd - enterW, kbY4, enterW, H, R, false, "Enter", 3.5f)

                                    // Text field
                                    drawRoundRect(
                                        tileBorder,
                                        topLeft = Offset(sx(16f), sy(90f)),
                                        size = Size(ss(168f), ss(12f)),
                                        cornerRadius = CornerRadius(ss(3f))
                                    )
                                    val typed = word.take(typedCount)
                                    val tfPaint = android.graphics.Paint().apply {
                                        color = android.graphics.Color.argb(140, 60, 52, 40)
                                        textSize = ss(4.5f)
                                        typeface = android.graphics.Typeface.create("monospace", android.graphics.Typeface.NORMAL)
                                        isAntiAlias = true
                                    }
                                    drawContext.canvas.nativeCanvas.drawText(
                                        typed, sx(22f), sy(98.5f), tfPaint
                                    )
                                    // Caret
                                    if (kbCaretVisible.value) {
                                        val caretX = 22f + typed.length * 4.7f
                                        drawRoundRect(
                                            accent.copy(alpha = 0.6f),
                                            topLeft = Offset(sx(caretX), sy(93f)),
                                            size = Size(ss(0.7f), ss(7f)),
                                            cornerRadius = CornerRadius(ss(0.4f))
                                        )
                                    }
                                }
                            }
                            // Label strip
                            Box(
                                modifier = Modifier.fillMaxWidth().height(30.dp)
                                    .drawBehind { drawLine(tileBorder, Offset(0f, 0f), Offset(size.width, 0f), 1f) }
                                    .padding(horizontal = 10.dp),
                                contentAlignment = Alignment.CenterStart
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Keyboard", color = accent, fontSize = 10.5.sp,
                                        fontWeight = FontWeight.Bold, letterSpacing = 0.02.sp)
                                    Box(
                                        modifier = Modifier.size(16.dp)
                                            .background(accent.copy(alpha = 0.12f), CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Canvas(modifier = Modifier.size(7.dp)) {
                                            val path = Path().apply {
                                                moveTo(size.width * 0.25f, size.height * 0.1f)
                                                lineTo(size.width * 0.75f, size.height * 0.5f)
                                                lineTo(size.width * 0.25f, size.height * 0.9f)
                                            }
                                            drawPath(path, accent, style = Stroke(width = 2.5f, cap = StrokeCap.Round, join = StrokeJoin.Round))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(28.dp))
    }

    // Bluetooth not connected dialog
    if (showBtDialog) {
        Box(
            modifier = Modifier.fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { showBtDialog = false },
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.82f)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {}
                    .background(winBg, RoundedCornerShape(20.dp))
                    .border(1.dp, winBorder, RoundedCornerShape(20.dp))
                    .padding(28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // BT icon with accent background
                Box(
                    modifier = Modifier.size(52.dp)
                        .background(
                            brush = Brush.linearGradient(
                                listOf(accent, Color(0xFF47403a)),
                                start = Offset(0f, 0f), end = Offset(52f, 52f)
                            ),
                            shape = RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(22.dp)) {
                        val s = size.width
                        val path = Path().apply {
                            moveTo(s * 0.27f, s * 0.27f)
                            lineTo(s * 0.73f, s * 0.73f)
                            lineTo(s * 0.5f, s * 0.96f)
                            lineTo(s * 0.5f, s * 0.04f)
                            lineTo(s * 0.73f, s * 0.27f)
                            lineTo(s * 0.27f, s * 0.73f)
                        }
                        drawPath(path, Color.White, style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round))
                    }
                }

                Spacer(Modifier.height(18.dp))

                Text(
                    "No device connected", color = ink, fontSize = 18.sp,
                    fontWeight = FontWeight.Bold, textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    "Pair your phone with a computer via Bluetooth first to use this mode.",
                    color = mute, fontSize = 13.sp, lineHeight = 19.sp,
                    textAlign = TextAlign.Center, fontWeight = FontWeight.Normal
                )

                Spacer(Modifier.height(22.dp))

                // Pair now button
                Text(
                    "Set up Bluetooth", color = winBg, fontSize = 13.5.sp,
                    fontWeight = FontWeight.SemiBold, letterSpacing = 0.02.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(accent, RoundedCornerShape(12.dp))
                        .clickable {
                            showBtDialog = false
                            onModeSelected(AppScreen.CONNECT)
                        }
                        .padding(vertical = 13.dp),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(10.dp))

                // Dismiss
                Text(
                    "Maybe later", color = mute, fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.clickable { showBtDialog = false }
                        .padding(vertical = 6.dp)
                )
            }
        }
    }

    if (showConnecting) {
        Box(
            modifier = Modifier.fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.72f)
                    .background(winBg, RoundedCornerShape(20.dp))
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(36.dp),
                    color = accent,
                    strokeWidth = 3.dp
                )

                Spacer(Modifier.height(18.dp))

                Text(
                    "Connecting…", color = ink, fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(6.dp))

                Text(
                    "Establishing connection with your computer",
                    color = mute, fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
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
    keyboardLayout: String,
    onKeyboardLayoutChange: (String) -> Unit,
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
    val themeColors = AppThemes.fromName(trackpadTheme)
    val darkSurface = themeColors.surface
    val accentGlow = themeColors.accentGlow
    val edgeGlow = themeColors.edgeGlow

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
        if (accentGlow != Color.Transparent) {
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
            enter = fadeIn(tween(120)),
            exit = fadeOut(tween(100))
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
                trackpadTheme = trackpadTheme,
                onTrackpadThemeChange = onTrackpadThemeChange,
                keyboardLayout = keyboardLayout,
                onKeyboardLayoutChange = onKeyboardLayoutChange,
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
// Phase 4 — Settings Panel
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
    trackpadTheme: String,
    onTrackpadThemeChange: (String) -> Unit,
    keyboardLayout: String,
    onKeyboardLayoutChange: (String) -> Unit,
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

    val bg = if (isDark) Color(0xFF0C0C0E) else Color(0xFFF8F7F4)
    val ink = if (isDark) Color(0xFFF2F2F4) else Color(0xFF16161A)
    val mute = if (isDark) Color(0xFF85858F) else Color(0xFF74747C)
    val line = if (isDark) Color.White.copy(alpha = 0.09f) else Color.Black.copy(alpha = 0.10f)
    val ok = if (isDark) Color(0xFF3DDC97) else Color(0xFF1FA971)
    val sliderTrack = if (isDark) Color.White.copy(alpha = 0.16f) else Color.Black.copy(alpha = 0.16f)
    val thumbCol = if (isDark) Color(0xFF0C0C0E) else Color.White

    val accentOptions = listOf(
        Triple("graphite", "Graphite", ink),
        Triple("indigo", "Indigo", Color(0xFF7A83FF)),
        Triple("ember", "Ember", Color(0xFFF27A3A)),
        Triple("aurora", "Aurora", Color(0xFF1FBF9A)),
        Triple("rose", "Rose", Color(0xFFE56FA5))
    )
    val acc = accentOptions.firstOrNull { it.first == trackpadTheme }?.third ?: ink
    val accentName = accentOptions.firstOrNull { it.first == trackpadTheme }?.second ?: "Graphite"

    val slideAnim = remember { Animatable(1f) }
    LaunchedEffect(Unit) {
        slideAnim.animateTo(0f, tween(250, easing = EaseOutCubic))
    }

    val scrollState = rememberScrollState()
    val headerHasBorder by remember { derivedStateOf { scrollState.value > 6 } }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset(y = (slideAnim.value * 1200).dp)
            .background(bg)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .drawBehind {
                        if (headerHasBorder) drawLine(line, Offset(0f, size.height), Offset(size.width, size.height), 1f)
                    }
                    .padding(start = 24.dp, end = 24.dp, top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Settings", fontSize = 19.sp, fontWeight = FontWeight.SemiBold, color = ink, letterSpacing = (-0.3).sp)
                Text(
                    "Done",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium,
                    color = ink,
                    modifier = Modifier.clickable { onDismiss() }.padding(start = 12.dp, top = 8.dp, bottom = 8.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
                    .padding(horizontal = 24.dp)
                    .padding(bottom = 40.dp)
            ) {
                // ── Connected device ──
                Column(
                    modifier = Modifier.padding(top = 20.dp, bottom = 22.dp)
                ) {
                    Text("CONNECTED DEVICE", fontSize = 11.5.sp, fontWeight = FontWeight.Medium, color = mute, letterSpacing = 1.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text(
                                connectedHostName ?: "No device",
                                fontSize = 23.sp, fontWeight = FontWeight.SemiBold, color = ink,
                                letterSpacing = (-0.5).sp, lineHeight = 26.sp
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(if (isConnected) ok else mute))
                                Spacer(modifier = Modifier.width(7.dp))
                                Text(if (isConnected) "Connected" else "Not connected", fontSize = 13.5.sp, color = mute)
                            }
                        }
                        Text(
                            "Change", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = ink,
                            textDecoration = TextDecoration.Underline,
                            modifier = Modifier.clickable { onOpenDeviceManager() }.padding(bottom = 4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(22.dp))
                    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(line))
                }

                // ── Pointer ──
                Text("POINTER", fontSize = 11.5.sp, fontWeight = FontWeight.Medium, color = mute, letterSpacing = 1.sp, modifier = Modifier.padding(top = 18.dp, bottom = 8.dp))

                val speedLabel = when {
                    sensitivity <= 0.7f -> "Precise"
                    sensitivity <= 1.4f -> "Balanced"
                    else -> "Fast"
                }
                Column(modifier = Modifier.padding(top = 15.dp, bottom = 20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text("Cursor speed", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = ink)
                            Text(speedLabel, fontSize = 13.sp, color = mute)
                        }
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                "${"%.1f".format(sensitivity)}", fontSize = 28.sp, fontWeight = FontWeight.Medium,
                                color = ink, letterSpacing = (-0.5).sp
                            )
                            Text("×", fontSize = 15.sp, color = mute, modifier = Modifier.padding(start = 1.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    var sliderFraction by remember { mutableStateOf((sensitivity - 0.5f) / 2.5f) }
                    LaunchedEffect(sensitivity) { sliderFraction = ((sensitivity - 0.5f) / 2.5f).coerceIn(0f, 1f) }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(30.dp)
                            .pointerInput(Unit) {
                                awaitEachGesture {
                                    val down = awaitFirstDown(requireUnconsumed = false)
                                    val w = size.width.toFloat()
                                    fun update(x: Float) {
                                        val f = (x / w).coerceIn(0f, 1f)
                                        sliderFraction = f
                                        val raw = 0.5f + f * 2.5f
                                        val snapped = (raw * 10).roundToInt() / 10f
                                        onSensitivityChange(snapped.coerceIn(0.5f, 3.0f))
                                    }
                                    update(down.position.x); down.consume()
                                    while (true) {
                                        val ev = awaitPointerEvent()
                                        if (ev.changes.all { !it.pressed }) break
                                        ev.changes.forEach { update(it.position.x); it.consume() }
                                    }
                                }
                            }
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val trackH = 2.dp.toPx()
                            val trackY = size.height / 2 - trackH / 2
                            val thumbR = 10.dp.toPx()
                            val fillW = sliderFraction * size.width

                            drawRoundRect(sliderTrack, Offset(0f, trackY), Size(size.width, trackH), CornerRadius(trackH / 2))
                            if (fillW > 0f) drawRoundRect(acc, Offset(0f, trackY), Size(fillW, trackH), CornerRadius(trackH / 2))

                            val thumbX = fillW.coerceIn(thumbR, size.width - thumbR)
                            val cy = size.height / 2
                            drawCircle(Color.Black.copy(alpha = 0.3f), thumbR, Offset(thumbX, cy + 1.dp.toPx()))
                            drawCircle(ink, thumbR, Offset(thumbX, cy))
                            drawCircle(bg, thumbR - 2.dp.toPx(), Offset(thumbX, cy))
                        }
                    }

                    Canvas(modifier = Modifier.fillMaxWidth().height(12.dp).padding(horizontal = 9.dp)) {
                        val count = 26
                        for (i in 0 until count) {
                            val x = if (count > 1) size.width * i / (count - 1) else 0f
                            val h = if (i % 5 == 0) 8.dp.toPx() else 5.dp.toPx()
                            drawRect(sliderTrack, Offset(x, 0f), Size(1.dp.toPx(), h))
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("0.5×", fontSize = 12.sp, color = mute)
                        Text("3×", fontSize = 12.sp, color = mute)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(horizontalArrangement = Arrangement.spacedBy(22.dp)) {
                        listOf(0.8f to "Precise", 1.3f to "Balanced", 2.2f to "Fast").forEach { (value, label) ->
                            val isOn = speedLabel == label
                            Column(modifier = Modifier.clickable { onSensitivityChange(value) }) {
                                Text(
                                    label, fontSize = 14.sp, fontWeight = FontWeight.Medium,
                                    color = if (isOn) ink else mute,
                                    modifier = Modifier.padding(top = 4.dp, bottom = 6.dp)
                                )
                                Box(modifier = Modifier.width(if (label == "Balanced") 64.dp else 52.dp).height(2.dp).background(if (isOn) acc else Color.Transparent))
                            }
                        }
                    }
                }
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(line))

                SettingsToggleRow("Tap to click", "Tap anywhere to left-click", tapToClick, onTapToClickChange, ink, mute, line, acc, sliderTrack, thumbCol)
                SettingsToggleRow("Natural scroll", "Content follows your finger", naturalScroll, onNaturalScrollChange, ink, mute, line, acc, sliderTrack, thumbCol)
                SettingsToggleRow("Haptic feedback", "Vibrate on tap and gesture", hapticsEnabled, onHapticsChange, ink, mute, line, acc, sliderTrack, thumbCol)

                // ── Display ──
                Text("DISPLAY", fontSize = 11.5.sp, fontWeight = FontWeight.Medium, color = mute, letterSpacing = 1.sp, modifier = Modifier.padding(top = 18.dp, bottom = 8.dp))

                SettingsToggleRow("Touch ripple", "Show a ripple where you touch", rippleEnabled, onRippleChange, ink, mute, line, acc, sliderTrack, thumbCol)
                SettingsToggleRow("Auto-hide status bar", "Fades after 3 seconds", statusBarAutoHide, onStatusBarAutoHideChange, ink, mute, line, acc, sliderTrack, thumbCol)

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 15.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Appearance", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = ink)
                    Row(
                        modifier = Modifier.border(1.dp, line, RoundedCornerShape(10.dp)).padding(2.dp)
                    ) {
                        listOf("Dark" to "dark", "Light" to "light").forEach { (label, value) ->
                            val isSel = uiTheme == value
                            Text(
                                label, fontSize = 13.5.sp, fontWeight = FontWeight.Medium,
                                color = if (isSel) bg else mute,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) ink else Color.Transparent)
                                    .clickable { onUiThemeChange(value) }
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }
                    }
                }
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(line))

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 15.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Accent", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = ink)
                        Text(accentName, fontSize = 13.sp, color = mute, modifier = Modifier.padding(top = 1.dp))
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(11.dp)) {
                        accentOptions.forEach { (id, _, color) ->
                            val isSel = trackpadTheme == id
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .then(
                                        if (isSel) Modifier.drawBehind {
                                            drawCircle(color, radius = size.width / 2 + 3.5.dp.toPx(), style = Stroke(2.dp.toPx()))
                                        } else Modifier
                                    )
                                    .clip(CircleShape)
                                    .background(color)
                                    .clickable { onTrackpadThemeChange(id) }
                            )
                        }
                    }
                }
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(line))

                // ── Keyboard ──
                Text("KEYBOARD", fontSize = 11.5.sp, fontWeight = FontWeight.Medium, color = mute, letterSpacing = 1.sp, modifier = Modifier.padding(top = 18.dp, bottom = 8.dp))

                val layoutMeta = mapOf(
                    "qwerty" to Pair("English (US, UK)", listOf("QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM")),
                    "azerty" to Pair("French", listOf("AZERTYUIOP", "QSDFGHJKLM", "WXCVBN")),
                    "qwertz" to Pair("German", listOf("QWERTZUIOP", "ASDFGHJKL", "YXCVBNM")),
                    "dvorak" to Pair("Ergonomic", listOf("',.PYFGCRL", "AOEUIDHTNS", ";QJKXBMWVZ")),
                    "colemak" to Pair("Ergonomic", listOf("QWFPGJLUY;", "ARSTDHNEIO", "ZXCVBKM"))
                )
                val currentMeta = layoutMeta[keyboardLayout] ?: layoutMeta["qwerty"]!!

                Column(modifier = Modifier.padding(top = 15.dp, bottom = 4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text("Layout", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = ink)
                        Text(currentMeta.first, fontSize = 13.sp, color = mute)
                    }
                    Spacer(modifier = Modifier.height(12.dp))

                    // Pill chips — horizontally scrollable
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .horizontalScroll(rememberScrollState())
                            .padding(bottom = 8.dp)
                    ) {
                        KeyboardLayouts.LAYOUT_OPTIONS.forEach { (id, label) ->
                            val isSel = keyboardLayout == id
                            val pillBg by animateColorAsState(if (isSel) ink else Color.Transparent, tween(220))
                            val pillBorder by animateColorAsState(if (isSel) ink else line, tween(220))
                            val pillText by animateColorAsState(if (isSel) bg else mute, tween(220))
                            Text(
                                label, fontSize = 14.sp, fontWeight = FontWeight.Medium,
                                color = pillText,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(50))
                                    .background(pillBg)
                                    .border(1.dp, pillBorder, RoundedCornerShape(50))
                                    .clickable { onKeyboardLayoutChange(id) }
                                    .padding(horizontal = 18.dp, vertical = 9.dp)
                            )
                        }
                    }

                    // Mini keyboard preview
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp, bottom = 10.dp)
                            .border(1.dp, line, RoundedCornerShape(14.dp))
                            .padding(horizontal = 8.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        currentMeta.second.forEach { row ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.Center
                            ) {
                                row.forEachIndexed { idx, ch ->
                                    if (idx > 0) Spacer(modifier = Modifier.width(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .weight(1f, fill = false)
                                            .size(29.dp, 30.dp)
                                            .border(1.dp, line, RoundedCornerShape(7.dp)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            ch.toString(), fontSize = 12.sp, fontWeight = FontWeight.Medium,
                                            color = ink.copy(alpha = 0.85f)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(line))

                // ── Gestures ──
                Text("GESTURES", fontSize = 11.5.sp, fontWeight = FontWeight.Medium, color = mute, letterSpacing = 1.sp, modifier = Modifier.padding(top = 18.dp, bottom = 8.dp))

                val gestureGroups = listOf(
                    Triple("One finger", 1, gestureData["1-finger"] ?: emptyList()),
                    Triple("Two fingers", 2, gestureData["2-finger"] ?: emptyList()),
                    Triple("Three fingers", 3, gestureData["3-finger"] ?: emptyList()),
                    Triple("Four fingers", 4, gestureData["4-finger"] ?: emptyList())
                )
                gestureGroups.forEach { (label, fingerCount, entries) ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(top = 20.dp, bottom = 2.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(label, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = mute)
                        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            repeat(fingerCount) {
                                Box(modifier = Modifier.size(5.dp).clip(CircleShape).background(mute))
                            }
                        }
                    }
                    entries.forEach { entry ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 13.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(entry.gesture, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = ink)
                            Text(entry.action, fontSize = 15.sp, color = mute)
                        }
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(line))
                    }
                }

                // ── Devices ──
                Text("DEVICES", fontSize = 11.5.sp, fontWeight = FontWeight.Medium, color = mute, letterSpacing = 1.sp, modifier = Modifier.padding(top = 18.dp, bottom = 8.dp))

                if (bondedDevices.isEmpty()) {
                    Text("No paired devices", fontSize = 14.sp, color = mute, modifier = Modifier.padding(vertical = 15.dp))
                } else {
                    bondedDevices.forEach { device ->
                        val nickname = deviceNicknames[device.address]
                        val displayName = nickname ?: device.name ?: device.address
                        val isThisConnected = device.address == connectedDeviceAddress
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenDeviceManager() }
                                .padding(vertical = 15.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(displayName, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = ink)
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                                if (isThisConnected) Box(modifier = Modifier.size(7.dp).clip(CircleShape).background(ok))
                                Text(if (isThisConnected) "Connected" else "Paired", fontSize = 13.5.sp, color = mute)
                            }
                        }
                        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(line))
                    }
                }

                Text(
                    "+ Add new device", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = ink,
                    modifier = Modifier.fillMaxWidth().clickable { onNavigateToPairingGuide() }.padding(vertical = 16.dp)
                )
                Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(line))

                Text(
                    "Reset to defaults", fontSize = 14.sp, color = mute, textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().clickable {
                        onSensitivityChange(1.3f); onTapToClickChange(true); onNaturalScrollChange(false)
                        onHapticsChange(true); onRippleChange(true); onStatusBarAutoHideChange(true)
                        onUiThemeChange("dark"); onTrackpadThemeChange("graphite"); onKeyboardLayoutChange("qwerty")
                    }.padding(vertical = 16.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Version", fontSize = 15.sp, color = mute)
                    Text(appVersion, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = ink)
                }
            }
        }
    }
}

@Composable
private fun SettingsToggleRow(
    title: String,
    description: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    ink: Color,
    mute: Color,
    line: Color,
    acc: Color,
    trackBg: Color,
    thumbCol: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontSize = 15.sp, fontWeight = FontWeight.Medium, color = ink)
            Text(description, fontSize = 13.sp, color = mute, lineHeight = 18.sp, modifier = Modifier.padding(top = 1.dp))
        }
        Spacer(modifier = Modifier.width(16.dp))
        val toggleBg by animateColorAsState(if (checked) acc else trackBg, tween(300), label = "tb")
        val thumbOff by animateFloatAsState(
            if (checked) 1f else 0f,
            spring(dampingRatio = 0.6f, stiffness = Spring.StiffnessMediumLow), label = "to"
        )
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(toggleBg)
                .clickable { onCheckedChange(!checked) }
        ) {
            Box(
                modifier = Modifier
                    .padding(3.dp)
                    .size(18.dp)
                    .offset(x = (16 * thumbOff).dp)
                    .clip(CircleShape)
                    .background(if (checked) thumbCol else Color.White)
            )
        }
    }
    Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(line))
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
    // ── AZERTY (French) — full keyboard ──
    val FULL_AZERTY = KeyboardLayout(
        rows = listOf(
            FULL_QWERTY.rows[0], // Media row (same)
            FULL_QWERTY.rows[1], // Number row (same HID, labels match host)
            // Row 2: AZERTY
            listOf(
                KeyDef("Tab", "Tab", S.KEY_TAB, 1.25f, KeyType.CHAR),
                KeyDef("a", "A", S.KEY_Q), KeyDef("z", "Z", S.KEY_W),
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
                KeyDef("q", "Q", S.KEY_A), KeyDef("s", "S", S.KEY_S),
                KeyDef("d", "D", S.KEY_D), KeyDef("f", "F", S.KEY_F),
                KeyDef("g", "G", S.KEY_G), KeyDef("h", "H", S.KEY_H),
                KeyDef("j", "J", S.KEY_J), KeyDef("k", "K", S.KEY_K),
                KeyDef("l", "L", S.KEY_L), KeyDef("m", "M", S.KEY_SEMICOLON),
                KeyDef("'", "\"", S.KEY_APOSTROPHE),
                KeyDef("Enter", "Enter", S.KEY_ENTER, 1.75f, KeyType.CHAR)
            ),
            // Row 4: Bottom row
            listOf(
                KeyDef("⇧", "⇧", 0, 2.0f, KeyType.SHIFT),
                KeyDef("w", "W", S.KEY_Z), KeyDef("x", "X", S.KEY_X),
                KeyDef("c", "C", S.KEY_C), KeyDef("v", "V", S.KEY_V),
                KeyDef("b", "B", S.KEY_B), KeyDef("n", "N", S.KEY_N),
                KeyDef(",", "<", S.KEY_M), KeyDef(";", ":", S.KEY_COMMA),
                KeyDef(".", ">", S.KEY_PERIOD), KeyDef("/", "?", S.KEY_SLASH),
                KeyDef("⇧", "⇧", 0, 2.0f, KeyType.SHIFT)
            ),
            FULL_QWERTY.rows[5] // Space row (same)
        )
    )

    // ── QWERTZ (German) — full keyboard ──
    val FULL_QWERTZ = KeyboardLayout(
        rows = listOf(
            FULL_QWERTY.rows[0],
            FULL_QWERTY.rows[1],
            // Row 2: QWERTZ
            listOf(
                KeyDef("Tab", "Tab", S.KEY_TAB, 1.25f, KeyType.CHAR),
                KeyDef("q", "Q", S.KEY_Q), KeyDef("w", "W", S.KEY_W),
                KeyDef("e", "E", S.KEY_E), KeyDef("r", "R", S.KEY_R),
                KeyDef("t", "T", S.KEY_T), KeyDef("z", "Z", S.KEY_Y),
                KeyDef("u", "U", S.KEY_U), KeyDef("i", "I", S.KEY_I),
                KeyDef("o", "O", S.KEY_O), KeyDef("p", "P", S.KEY_P),
                KeyDef("ü", "Ü", S.KEY_LBRACKET), KeyDef("+", "*", S.KEY_RBRACKET),
                KeyDef("#", "'", S.KEY_BACKSLASH, 1.25f)
            ),
            // Row 3: Home row
            listOf(
                KeyDef("Caps", "Caps", S.KEY_CAPS_LOCK, 1.5f, KeyType.CHAR),
                KeyDef("a", "A", S.KEY_A), KeyDef("s", "S", S.KEY_S),
                KeyDef("d", "D", S.KEY_D), KeyDef("f", "F", S.KEY_F),
                KeyDef("g", "G", S.KEY_G), KeyDef("h", "H", S.KEY_H),
                KeyDef("j", "J", S.KEY_J), KeyDef("k", "K", S.KEY_K),
                KeyDef("l", "L", S.KEY_L), KeyDef("ö", "Ö", S.KEY_SEMICOLON),
                KeyDef("ä", "Ä", S.KEY_APOSTROPHE),
                KeyDef("Enter", "Enter", S.KEY_ENTER, 1.75f, KeyType.CHAR)
            ),
            // Row 4: Bottom row
            listOf(
                KeyDef("⇧", "⇧", 0, 2.0f, KeyType.SHIFT),
                KeyDef("y", "Y", S.KEY_Z), KeyDef("x", "X", S.KEY_X),
                KeyDef("c", "C", S.KEY_C), KeyDef("v", "V", S.KEY_V),
                KeyDef("b", "B", S.KEY_B), KeyDef("n", "N", S.KEY_N),
                KeyDef("m", "M", S.KEY_M), KeyDef(",", ";", S.KEY_COMMA),
                KeyDef(".", ":", S.KEY_PERIOD), KeyDef("-", "_", S.KEY_SLASH),
                KeyDef("⇧", "⇧", 0, 2.0f, KeyType.SHIFT)
            ),
            FULL_QWERTY.rows[5]
        )
    )

    // ── Dvorak — full keyboard ──
    val FULL_DVORAK = KeyboardLayout(
        rows = listOf(
            FULL_QWERTY.rows[0],
            FULL_QWERTY.rows[1],
            // Row 2: Dvorak top
            listOf(
                KeyDef("Tab", "Tab", S.KEY_TAB, 1.25f, KeyType.CHAR),
                KeyDef("'", "\"", S.KEY_Q), KeyDef(",", "<", S.KEY_W),
                KeyDef(".", ">", S.KEY_E), KeyDef("p", "P", S.KEY_R),
                KeyDef("y", "Y", S.KEY_T), KeyDef("f", "F", S.KEY_Y),
                KeyDef("g", "G", S.KEY_U), KeyDef("c", "C", S.KEY_I),
                KeyDef("r", "R", S.KEY_O), KeyDef("l", "L", S.KEY_P),
                KeyDef("/", "?", S.KEY_LBRACKET), KeyDef("=", "+", S.KEY_RBRACKET),
                KeyDef("\\", "|", S.KEY_BACKSLASH, 1.25f)
            ),
            // Row 3: Home row
            listOf(
                KeyDef("Caps", "Caps", S.KEY_CAPS_LOCK, 1.5f, KeyType.CHAR),
                KeyDef("a", "A", S.KEY_A), KeyDef("o", "O", S.KEY_S),
                KeyDef("e", "E", S.KEY_D), KeyDef("u", "U", S.KEY_F),
                KeyDef("i", "I", S.KEY_G), KeyDef("d", "D", S.KEY_H),
                KeyDef("h", "H", S.KEY_J), KeyDef("t", "T", S.KEY_K),
                KeyDef("n", "N", S.KEY_L), KeyDef("s", "S", S.KEY_SEMICOLON),
                KeyDef("-", "_", S.KEY_APOSTROPHE),
                KeyDef("Enter", "Enter", S.KEY_ENTER, 1.75f, KeyType.CHAR)
            ),
            // Row 4: Bottom row
            listOf(
                KeyDef("⇧", "⇧", 0, 2.0f, KeyType.SHIFT),
                KeyDef(";", ":", S.KEY_Z), KeyDef("q", "Q", S.KEY_X),
                KeyDef("j", "J", S.KEY_C), KeyDef("k", "K", S.KEY_V),
                KeyDef("x", "X", S.KEY_B), KeyDef("b", "B", S.KEY_N),
                KeyDef("m", "M", S.KEY_M), KeyDef("w", "W", S.KEY_COMMA),
                KeyDef("v", "V", S.KEY_PERIOD), KeyDef("z", "Z", S.KEY_SLASH),
                KeyDef("⇧", "⇧", 0, 2.0f, KeyType.SHIFT)
            ),
            FULL_QWERTY.rows[5]
        )
    )

    // ── Colemak — full keyboard ──
    val FULL_COLEMAK = KeyboardLayout(
        rows = listOf(
            FULL_QWERTY.rows[0],
            FULL_QWERTY.rows[1],
            // Row 2: Colemak top
            listOf(
                KeyDef("Tab", "Tab", S.KEY_TAB, 1.25f, KeyType.CHAR),
                KeyDef("q", "Q", S.KEY_Q), KeyDef("w", "W", S.KEY_W),
                KeyDef("f", "F", S.KEY_E), KeyDef("p", "P", S.KEY_R),
                KeyDef("g", "G", S.KEY_T), KeyDef("j", "J", S.KEY_Y),
                KeyDef("l", "L", S.KEY_U), KeyDef("u", "U", S.KEY_I),
                KeyDef("y", "Y", S.KEY_O), KeyDef(";", ":", S.KEY_P),
                KeyDef("[", "{", S.KEY_LBRACKET), KeyDef("]", "}", S.KEY_RBRACKET),
                KeyDef("\\", "|", S.KEY_BACKSLASH, 1.25f)
            ),
            // Row 3: Home row
            listOf(
                KeyDef("Caps", "Caps", S.KEY_CAPS_LOCK, 1.5f, KeyType.CHAR),
                KeyDef("a", "A", S.KEY_A), KeyDef("r", "R", S.KEY_S),
                KeyDef("s", "S", S.KEY_D), KeyDef("t", "T", S.KEY_F),
                KeyDef("d", "D", S.KEY_G), KeyDef("h", "H", S.KEY_H),
                KeyDef("n", "N", S.KEY_J), KeyDef("e", "E", S.KEY_K),
                KeyDef("i", "I", S.KEY_L), KeyDef("o", "O", S.KEY_SEMICOLON),
                KeyDef("'", "\"", S.KEY_APOSTROPHE),
                KeyDef("Enter", "Enter", S.KEY_ENTER, 1.75f, KeyType.CHAR)
            ),
            // Row 4: Bottom row
            listOf(
                KeyDef("⇧", "⇧", 0, 2.0f, KeyType.SHIFT),
                KeyDef("z", "Z", S.KEY_Z), KeyDef("x", "X", S.KEY_X),
                KeyDef("c", "C", S.KEY_C), KeyDef("v", "V", S.KEY_V),
                KeyDef("b", "B", S.KEY_B), KeyDef("k", "K", S.KEY_N),
                KeyDef("m", "M", S.KEY_M), KeyDef(",", "<", S.KEY_COMMA),
                KeyDef(".", ">", S.KEY_PERIOD), KeyDef("/", "?", S.KEY_SLASH),
                KeyDef("⇧", "⇧", 0, 2.0f, KeyType.SHIFT)
            ),
            FULL_QWERTY.rows[5]
        )
    )

    fun fullLayoutFromName(name: String): KeyboardLayout = when (name) {
        "azerty" -> FULL_AZERTY
        "qwertz" -> FULL_QWERTZ
        "dvorak" -> FULL_DVORAK
        "colemak" -> FULL_COLEMAK
        else -> FULL_QWERTY
    }

    val LAYOUT_OPTIONS = listOf(
        "qwerty" to "QWERTY",
        "azerty" to "AZERTY",
        "qwertz" to "QWERTZ",
        "dvorak" to "Dvorak",
        "colemak" to "Colemak"
    )
}

// Keyboard Shortcuts data for overlay (Milestone 5.1)
object KeyboardShortcuts {
    data class Shortcut(val keys: String, val description: String)

    val CTRL_SHORTCUTS = listOf(
        Shortcut("Ctrl + C", "Copy"),
        Shortcut("Ctrl + V", "Paste"),
        Shortcut("Ctrl + X", "Cut"),
        Shortcut("Ctrl + Z", "Undo"),
        Shortcut("Ctrl + Y", "Redo"),
        Shortcut("Ctrl + A", "Select all"),
        Shortcut("Ctrl + S", "Save"),
        Shortcut("Ctrl + F", "Find"),
        Shortcut("Ctrl + P", "Print"),
        Shortcut("Ctrl + N", "New"),
        Shortcut("Ctrl + W", "Close tab"),
        Shortcut("Ctrl + T", "New tab")
    )

    val WIN_SHORTCUTS = listOf(
        Shortcut("Win + D", "Show desktop"),
        Shortcut("Win + E", "File explorer"),
        Shortcut("Win + L", "Lock screen"),
        Shortcut("Win + Tab", "Task view"),
        Shortcut("Win + I", "Settings"),
        Shortcut("Win + R", "Run dialog"),
        Shortcut("Win + S", "Search"),
        Shortcut("Win + V", "Clipboard"),
        Shortcut("Win + ←/→", "Snap window"),
        Shortcut("Win + ↑/↓", "Maximize/Minimize")
    )

    val ALT_SHORTCUTS = listOf(
        Shortcut("Alt + Tab", "Switch apps"),
        Shortcut("Alt + F4", "Close window"),
        Shortcut("Alt + Enter", "Properties"),
        Shortcut("Alt + ←", "Back"),
        Shortcut("Alt + →", "Forward")
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
    layout: KeyboardLayout = KeyboardLayouts.FULL_QWERTY,
    theme: AppThemeColors = AppThemes.Midnight
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
    var shortcutsOverlayModifier by remember { mutableStateOf("") }

    val surfaceBg = theme.surface
    val keyGap = layout.keyGapDp.dp
    val rowGap = layout.rowGapDp.dp
    val accent = theme.accent

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

                        val glassBg = theme.keySurface
                        val glassBgSpecial = theme.keySpecialSurface
                        val glassBorder = theme.keyBorder
                        val accentBorder = theme.accentBorder

                        val bg by animateColorAsState(
                            targetValue = when {
                                isShiftKey && shiftState == ShiftState.CAPS_LOCK -> accent.copy(alpha = 0.35f)
                                isShiftKey && shiftState == ShiftState.SHIFTED -> accent.copy(alpha = 0.2f)
                                isModActive -> accent.copy(alpha = 0.25f)
                                isFnKey && fnHeld -> accent.copy(alpha = 0.25f)
                                pressed -> glassBg.copy(alpha = glassBg.alpha * 1.8f)
                                isSpecialKey || isMediaRow || isConsumer || isFnKey -> glassBgSpecial
                                else -> glassBg
                            },
                            animationSpec = tween(100), label = "bg"
                        )
                        val borderColor by animateColorAsState(
                            targetValue = when {
                                isModActive || (isShiftKey && isShifted) || (isFnKey && fnHeld) -> accentBorder
                                pressed -> glassBorder.copy(alpha = glassBorder.alpha * 1.5f)
                                else -> glassBorder
                            },
                            animationSpec = tween(80), label = "brd"
                        )

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
                                                val overlayName = when (key.label) {
                                                    "Ctrl" -> "Ctrl"
                                                    "Win" -> "Win"
                                                    "Alt" -> "Alt"
                                                    else -> ""
                                                }
                                                if (overlayName.isNotEmpty()) {
                                                    val overlayRunnable = Runnable { shortcutsOverlayModifier = overlayName }
                                                    handler.postDelayed(overlayRunnable, 1000)
                                                    repeatRunnable = overlayRunnable
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
                                            key.type == KeyType.SHIFT || key.type == KeyType.MODIFIER || key.type == KeyType.FN || key.type == KeyType.ACTION -> {
                                                if (key.type == KeyType.MODIFIER) shortcutsOverlayModifier = ""
                                            }
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
                                    tint = theme.textSecondary,
                                    size = if (isMediaRow) 14f else 12f
                                )
                            } else {
                                Text(
                                    text = displayLabel,
                                    color = when {
                                        isModActive || (isShiftKey && isShifted) || (isFnKey && fnHeld) -> accent.copy(alpha = 0.85f)
                                        isSpecialKey || isMediaRow || isConsumer || isFnKey -> theme.textSecondary
                                        else -> theme.textPrimary
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
                            // Active modifier bottom-edge glow
                            if (isModActive || (isShiftKey && isShifted) || (isFnKey && fnHeld)) {
                                Canvas(modifier = Modifier.fillMaxWidth().height(2.dp).align(Alignment.BottomCenter)) {
                                    drawRoundRect(
                                        brush = Brush.horizontalGradient(
                                            listOf(Color.Transparent, accent.copy(alpha = 0.6f), Color.Transparent)
                                        ),
                                        cornerRadius = CornerRadius(2f)
                                    )
                                }
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
        ShortcutsOverlay(
            modifierName = shortcutsOverlayModifier,
            isVisible = shortcutsOverlayModifier.isNotEmpty(),
            theme = theme
        )
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
    theme: AppThemeColors = AppThemes.Midnight,
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
    val accent = theme.accent
    val surfaceBg = theme.surface

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

                        val glassBg = theme.keySurface
                        val glassBgSpecial = theme.keySpecialSurface
                        val glassBorder = theme.keyBorder
                        val accentBorder = theme.accentBorder

                        val bg by animateColorAsState(
                            targetValue = when {
                                isShiftKey && shiftState == ShiftState.CAPS_LOCK -> accent.copy(alpha = 0.35f)
                                isShiftKey && shiftState == ShiftState.SHIFTED -> accent.copy(alpha = 0.2f)
                                isModActive -> accent.copy(alpha = 0.25f)
                                isFnKey && fnHeld -> accent.copy(alpha = 0.25f)
                                pressed -> glassBg.copy(alpha = glassBg.alpha * 1.8f)
                                isSpecialKey || isFnKey || isMediaRow || isConsumer -> glassBgSpecial
                                else -> glassBg
                            },
                            animationSpec = tween(100), label = "bg"
                        )
                        val borderColor by animateColorAsState(
                            targetValue = when {
                                isModActive || (isShiftKey && isShifted) || (isFnKey && fnHeld) -> accentBorder
                                pressed -> glassBorder.copy(alpha = glassBorder.alpha * 1.5f)
                                else -> glassBorder
                            },
                            animationSpec = tween(80), label = "brd"
                        )

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
                                    tint = theme.textSecondary,
                                    size = 14f
                                )
                            } else {
                                Text(
                                    text = displayLabel,
                                    color = when {
                                        isModActive || (isShiftKey && isShifted) || (isFnKey && fnHeld) -> accent.copy(alpha = 0.85f)
                                        isMediaRow || isConsumer -> theme.textSecondary
                                        isSpecialKey || isFnKey -> theme.textSecondary
                                        else -> theme.textPrimary
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
    keyboardContent: @Composable (Modifier) -> Unit,
    theme: AppThemeColors = AppThemes.Midnight
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(theme.surface)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        // Top: Trackpad (65%)
        trackpadContent(
            Modifier
                .weight(0.65f)
                .fillMaxWidth()
        )

        // Divider with drag handle
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(12.dp),
            contentAlignment = Alignment.Center
        ) {
            // Divider line
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(0.5.dp)
                    .background(theme.divider)
            )
            // Drag handle (3 short horizontal lines)
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(theme.surface)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                repeat(3) {
                    Box(
                        modifier = Modifier
                            .width(16.dp)
                            .height(1.dp)
                            .background(theme.divider)
                    )
                }
            }
        }

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
    fun ModeIcon(mode: AppScreen, isActive: Boolean) {
        Canvas(modifier = Modifier.size(20.dp)) {
            val w = size.width; val h = size.height
            val iconColor = if (isActive) Color.White else Color.White.copy(alpha = 0.45f)
            val stroke = if (isActive) 2f else 1.5f
            when (mode) {
                AppScreen.TRACKPAD -> {
                    drawRoundRect(iconColor, Offset(w * 0.12f, h * 0.08f), Size(w * 0.76f, h * 0.84f), CornerRadius(w * 0.12f), style = Stroke(stroke))
                    if (isActive) drawCircle(Color.White, w * 0.06f, Offset(w * 0.55f, h * 0.45f))
                }
                AppScreen.KEYBOARD -> {
                    drawRoundRect(iconColor, Offset(w * 0.1f, h * 0.15f), Size(w * 0.8f, h * 0.7f), CornerRadius(w * 0.08f), style = Stroke(stroke))
                    for (r in 0..2) for (c in 0..(if (r == 2) 4 else 5)) {
                        val cols = if (r == 2) 5 else 6
                        val kw = w * 0.08f; val kh = h * 0.1f
                        val startX = w * 0.5f - (cols * kw + (cols - 1) * w * 0.04f) / 2f
                        drawRoundRect(iconColor, Offset(startX + c * (kw + w * 0.04f), h * (0.24f + r * 0.2f)), Size(kw, kh), CornerRadius(w * 0.015f), style = if (isActive) Fill else Stroke(stroke * 0.6f))
                    }
                }
                AppScreen.SPLIT -> {
                    drawRoundRect(iconColor, Offset(w * 0.12f, h * 0.08f), Size(w * 0.76f, h * 0.84f), CornerRadius(w * 0.1f), style = Stroke(stroke))
                    drawLine(iconColor, Offset(w * 0.18f, h * 0.5f), Offset(w * 0.82f, h * 0.5f), strokeWidth = stroke)
                }
                else -> {}
            }
        }
    }

    @Composable
    fun PillBar() {
        val expandFrom = if (dropDown) Alignment.CenterHorizontally else Alignment.End
        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(80)) + expandHorizontally(
                animationSpec = spring(dampingRatio = 0.7f, stiffness = 1200f),
                expandFrom = expandFrom
            ),
            exit = fadeOut(tween(80)) + shrinkHorizontally(
                animationSpec = tween(120, easing = FastOutSlowInEasing),
                shrinkTowards = expandFrom
            )
        ) {
            Row(
                modifier = Modifier
                    .then(if (dropDown) Modifier.padding(top = 8.dp) else Modifier.padding(bottom = 10.dp))
                    .clip(RoundedCornerShape(50))
                    .background(Color(0xFF1C1C28).copy(alpha = 0.95f))
                    .border(0.5.dp, Color.White.copy(alpha = 0.07f), RoundedCornerShape(50))
                    .padding(5.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                modes.forEach { (mode, label, _) ->
                    val isActive = mode == currentMode
                    val pillBg by animateColorAsState(
                        if (isActive) accent else Color.Transparent,
                        animationSpec = spring(dampingRatio = 0.8f, stiffness = 1500f), label = "pill"
                    )
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(50))
                            .background(pillBg)
                            .clickable { onModeSelected(mode) }
                            .padding(
                                start = if (isActive) 14.dp else 13.dp,
                                end = if (isActive) 18.dp else 13.dp,
                                top = 10.dp,
                                bottom = 10.dp
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ModeIcon(mode, isActive)
                        AnimatedVisibility(
                            visible = isActive,
                            enter = fadeIn(tween(100)) + expandHorizontally(tween(100), expandFrom = Alignment.Start),
                            exit = fadeOut(tween(60)) + shrinkHorizontally(tween(80), shrinkTowards = Alignment.Start)
                        ) {
                            Text(
                                text = label,
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }

    @Composable
    fun TriggerButton() {
        AnimatedVisibility(
            visible = !isVisible,
            enter = fadeIn(tween(100)) + scaleIn(tween(100), initialScale = 0.8f),
            exit = fadeOut(tween(60)) + scaleOut(tween(60), targetScale = 0.8f)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color.White.copy(alpha = 0.07f))
                    .border(0.5.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(14.dp))
                    .clickable { onToggle() },
                contentAlignment = Alignment.Center
            ) {
                ModeIcon(currentMode, true)
            }
        }
    }

    Box(modifier = modifier) {
        Column(
            horizontalAlignment = if (dropDown) Alignment.CenterHorizontally else Alignment.End
        ) {
            if (dropDown) {
                if (showTrigger) TriggerButton()
                PillBar()
            } else {
                PillBar()
                if (showTrigger) TriggerButton()
            }
        }
    }
}

@Composable
fun ShortcutsOverlay(
    modifierName: String,
    isVisible: Boolean,
    theme: AppThemeColors = AppThemes.Midnight
) {
    val shortcuts = when (modifierName) {
        "Ctrl" -> KeyboardShortcuts.CTRL_SHORTCUTS
        "Win" -> KeyboardShortcuts.WIN_SHORTCUTS
        "Alt" -> KeyboardShortcuts.ALT_SHORTCUTS
        else -> emptyList()
    }
    AnimatedVisibility(
        visible = isVisible && shortcuts.isNotEmpty(),
        enter = fadeIn(tween(200)),
        exit = fadeOut(tween(150))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.75f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .clip(RoundedCornerShape(20.dp))
                    .background(theme.surface.copy(alpha = 0.95f))
                    .border(0.5.dp, theme.keyBorder, RoundedCornerShape(20.dp))
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    "$modifierName Shortcuts",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = theme.accent,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                shortcuts.forEach { shortcut ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            shortcut.keys,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = theme.textPrimary
                        )
                        Text(
                            shortcut.description,
                            fontSize = 13.sp,
                            color = theme.textSecondary
                        )
                    }
                }
            }
        }
    }
}

class HidForegroundService : Service() {
    companion object {
        const val CHANNEL_ID = "hid_service_channel"
        const val NOTIFICATION_ID = 1
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onCreate() {
        super.onCreate()
        val channel = NotificationChannel(
            CHANNEL_ID, "Bluetooth HID",
            NotificationManager.IMPORTANCE_LOW
        ).apply { description = "Keeps Bluetooth connection alive" }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val notification = Notification.Builder(this, CHANNEL_ID)
            .setContentTitle("Mouskey")
            .setContentText("Connected as keyboard & trackpad")
            .setSmallIcon(android.R.drawable.stat_sys_data_bluetooth)
            .setOngoing(true)
            .build()
        startForeground(NOTIFICATION_ID, notification)
        return START_STICKY
    }
}
