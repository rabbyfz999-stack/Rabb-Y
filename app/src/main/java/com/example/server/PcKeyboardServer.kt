package com.example.server

import android.content.Context
import android.net.wifi.WifiManager
import android.text.format.Formatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Inet4Address
import java.net.NetworkInterface
import java.net.ServerSocket
import java.net.Socket
import java.net.URLDecoder

data class PcServerState(
    val isRunning: Boolean = false,
    val port: Int = 8888,
    val localIp: String = "127.0.0.1",
    val url: String = "http://127.0.0.1:8888",
    val lastInputText: String = "",
    val connectedClients: Int = 0
)

class PcKeyboardServer(
    private val context: Context,
    private val onTextReceived: (text: String, action: String) -> Unit
) {
    private var serverSocket: ServerSocket? = null
    private var serverJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.IO)

    private val _serverState = MutableStateFlow(PcServerState())
    val serverState: StateFlow<PcServerState> = _serverState.asStateFlow()

    fun startServer(port: Int = 8888) {
        if (_serverState.value.isRunning) return

        val ip = getLocalIpAddress()
        val url = "http://$ip:$port"

        serverJob = scope.launch {
            try {
                val socket = ServerSocket(port)
                serverSocket = socket
                _serverState.value = PcServerState(
                    isRunning = true,
                    port = port,
                    localIp = ip,
                    url = url
                )

                while (isActive && !socket.isClosed) {
                    try {
                        val clientSocket = socket.accept()
                        launch { handleClient(clientSocket) }
                    } catch (_: Exception) {
                        break
                    }
                }
            } catch (e: Exception) {
                _serverState.value = _serverState.value.copy(isRunning = false)
            }
        }
    }

    fun stopServer() {
        try {
            serverSocket?.close()
        } catch (_: Exception) {}
        serverJob?.cancel()
        serverSocket = null
        _serverState.value = _serverState.value.copy(isRunning = false)
    }

    private suspend fun handleClient(socket: Socket) = withContext(Dispatchers.IO) {
        try {
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val writer = PrintWriter(socket.getOutputStream(), true)

            val requestLine = reader.readLine() ?: return@withContext
            val parts = requestLine.split(" ")
            val method = parts.getOrNull(0) ?: "GET"
            val path = parts.getOrNull(1) ?: "/"

            // Read headers to determine Content-Length
            var contentLength = 0
            var line: String?
            while (reader.readLine().also { line = it } != null) {
                if (line.isNullOrBlank()) break
                if (line!!.startsWith("Content-Length:", ignoreCase = true)) {
                    contentLength = line!!.substringAfter(":").trim().toIntOrNull() ?: 0
                }
            }

            // Read POST body if present
            var body = ""
            if (method == "POST" && contentLength > 0) {
                val charArray = CharArray(contentLength)
                var readTotal = 0
                while (readTotal < contentLength) {
                    val read = reader.read(charArray, readTotal, contentLength - readTotal)
                    if (read == -1) break
                    readTotal += read
                }
                body = String(charArray, 0, readTotal)
            }

            when {
                path == "/" || path.startsWith("/?") -> {
                    serveHtmlPage(writer)
                }
                path == "/api/type" && method == "POST" -> {
                    val text = parseParam(body, "text")
                    onTextReceived(text, "type")
                    _serverState.value = _serverState.value.copy(lastInputText = text)
                    sendJsonResponse(writer, """{"status":"ok","type":"live"}""")
                }
                path == "/api/send_chat" && method == "POST" -> {
                    val text = parseParam(body, "text")
                    onTextReceived(text, "chat")
                    sendJsonResponse(writer, """{"status":"ok","type":"sent_chat"}""")
                }
                path == "/api/send_studio" && method == "POST" -> {
                    val text = parseParam(body, "text")
                    onTextReceived(text, "studio")
                    sendJsonResponse(writer, """{"status":"ok","type":"sent_studio"}""")
                }
                path == "/api/free_fire" && method == "POST" -> {
                    val key = parseParam(body, "key").ifEmpty { parseParam(body, "text") }
                    onTextReceived(key, "free_fire")
                    _serverState.value = _serverState.value.copy(lastInputText = "PC Key: $key")
                    sendJsonResponse(writer, """{"status":"ok","game":"free_fire","key":"$key"}""")
                }
                path == "/api/status" -> {
                    sendJsonResponse(writer, """{"status":"running","mobile":"AI Mobile","specs":{"ram":"8GB","storage":"128GB"}}""")
                }
                else -> {
                    writer.println("HTTP/1.1 404 Not Found")
                    writer.println("Content-Type: text/plain")
                    writer.println("Content-Length: 9")
                    writer.println()
                    writer.println("Not Found")
                }
            }
        } catch (_: Exception) {
        } finally {
            try { socket.close() } catch (_: Exception) {}
        }
    }

    private fun parseParam(body: String, paramName: String): String {
        return try {
            val pairs = body.split("&")
            for (p in pairs) {
                val kv = p.split("=")
                if (kv.size == 2 && kv[0] == paramName) {
                    return URLDecoder.decode(kv[1], "UTF-8")
                }
            }
            // If body is raw JSON or plaintext
            if (body.startsWith("{") && body.contains("\"text\"")) {
                val start = body.indexOf("\"text\"") + 6
                val quoteStart = body.indexOf("\"", start)
                val quoteEnd = body.indexOf("\"", quoteStart + 1)
                if (quoteStart != -1 && quoteEnd != -1) {
                    return body.substring(quoteStart + 1, quoteEnd)
                }
            }
            body
        } catch (_: Exception) {
            body
        }
    }

    private fun sendJsonResponse(writer: PrintWriter, json: String) {
        val bytes = json.toByteArray(Charsets.UTF_8)
        writer.println("HTTP/1.1 200 OK")
        writer.println("Content-Type: application/json; charset=utf-8")
        writer.println("Access-Control-Allow-Origin: *")
        writer.println("Content-Length: ${bytes.size}")
        writer.println()
        writer.println(json)
    }

    private fun serveHtmlPage(writer: PrintWriter) {
        val html = """
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>AI Mobile PC Setup & Gaming Controller</title>
  <style>
    * { box-sizing: border-box; margin: 0; padding: 0; font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; }
    body { background: #0B0E14; color: #F1F5F9; min-height: 100vh; display: flex; flex-direction: column; align-items: center; justify-content: center; padding: 16px; }
    .card { background: #141923; border: 1px solid #2B3447; border-radius: 16px; width: 100%; max-width: 760px; padding: 24px; box-shadow: 0 10px 30px rgba(0,0,0,0.5); }
    .header { display: flex; align-items: center; justify-content: space-between; margin-bottom: 16px; }
    .title { font-size: 19px; font-weight: 700; color: #B388FF; display: flex; align-items: center; gap: 8px; }
    .specs-badge { background: rgba(124, 77, 255, 0.2); color: #D1C4E9; border: 1px solid #7C4DFF; font-size: 11px; font-weight: 700; padding: 4px 8px; border-radius: 6px; }
    .badge { background: rgba(0, 230, 118, 0.15); color: #00E676; border: 1px solid #00E676; font-size: 11px; font-weight: 700; padding: 4px 10px; border-radius: 20px; }
    
    .tabs { display: flex; gap: 8px; margin-bottom: 16px; background: #0F141F; padding: 4px; border-radius: 12px; }
    .tab-btn { flex: 1; padding: 10px; border-radius: 8px; border: none; font-size: 13px; font-weight: 700; cursor: pointer; color: #94A3B8; background: transparent; transition: all 0.2s; }
    .tab-btn.active { background: #7C4DFF; color: white; }
    .tab-btn.ff-active { background: #FF5722; color: white; }

    .tab-content { display: none; }
    .tab-content.active { display: block; }

    /* Typing Area */
    textarea { width: 100%; height: 160px; background: #1D2433; border: 1px solid #2B3447; border-radius: 12px; color: #FFFFFF; font-size: 15px; padding: 12px; resize: vertical; outline: none; transition: border-color 0.2s; }
    textarea:focus { border-color: #7C4DFF; box-shadow: 0 0 0 2px rgba(124, 77, 255, 0.2); }
    .actions { display: flex; flex-wrap: wrap; gap: 10px; margin-top: 14px; }
    button.act-btn { flex: 1; min-width: 130px; padding: 12px 16px; border-radius: 10px; border: none; font-size: 13px; font-weight: 600; cursor: pointer; transition: all 0.2s; }
    .btn-primary { background: #7C4DFF; color: white; }
    .btn-secondary { background: #1D2433; color: #F1F5F9; border: 1px solid #2B3447; }
    
    /* Free Fire PC Game Controller */
    .ff-banner { background: linear-gradient(90deg, #FF3D00, #FF9100); padding: 10px 14px; border-radius: 10px; color: white; font-weight: bold; font-size: 13px; display: flex; justify-content: space-between; align-items: center; margin-bottom: 14px; }
    .game-grid { display: grid; grid-template-columns: 1fr 1fr; gap: 16px; }
    .ctrl-box { background: #1D2433; border: 1px solid #2B3447; border-radius: 12px; padding: 14px; }
    .ctrl-title { font-size: 12px; font-weight: 700; color: #FF9100; text-transform: uppercase; margin-bottom: 10px; letter-spacing: 0.5px; }
    
    .wasd-cluster { display: flex; flex-direction: column; align-items: center; gap: 6px; }
    .wasd-row { display: flex; gap: 6px; }
    .key-cap { width: 50px; height: 50px; background: #2B3447; border: 2px solid #3E4C66; border-radius: 8px; color: white; font-size: 16px; font-weight: bold; display: flex; flex-direction: column; align-items: center; justify-content: center; cursor: pointer; user-select: none; box-shadow: 0 4px 0 #1A202C; transition: transform 0.05s, background 0.1s; }
    .key-cap:active, .key-cap.pressed { transform: translateY(4px); box-shadow: 0 0 0 transparent; background: #FF5722; border-color: #FF8A65; }
    .key-cap span { font-size: 9px; color: #94A3B8; margin-top: -2px; }

    .action-keys { display: grid; grid-template-columns: repeat(3, 1fr); gap: 8px; }
    .act-key { padding: 12px 6px; background: #2B3447; border: 2px solid #3E4C66; border-radius: 8px; color: white; font-weight: bold; font-size: 13px; text-align: center; cursor: pointer; box-shadow: 0 4px 0 #1A202C; transition: all 0.05s; }
    .act-key:active, .act-key.pressed { transform: translateY(4px); box-shadow: none; background: #FF3D00; }
    .act-key.fire-key { background: #D50000; border-color: #FF1744; grid-column: span 3; font-size: 15px; padding: 14px; }
    .act-key.fire-key:active, .act-key.fire-key.pressed { background: #FF1744; }

    .log-box { margin-top: 14px; font-size: 12px; color: #00E5FF; min-height: 18px; font-family: monospace; }
    .tips { margin-top: 16px; font-size: 12px; color: #64748B; border-top: 1px solid #1D2433; padding-top: 12px; display: flex; justify-content: space-between; }
  </style>
</head>
<body>
  <div class="card">
    <div class="header">
      <div class="title">⌨️ AI Mobile PC Bridge <span class="specs-badge">RAM 8GB • 128GB ROM</span></div>
      <div class="badge">LIVE LINKED</div>
    </div>

    <div class="tabs">
      <button class="tab-btn active" id="tabTypingBtn" onclick="switchTab('typing')">⌨️ PC Keyboard Typing</button>
      <button class="tab-btn" id="tabGameBtn" onclick="switchTab('game')">🔥 Free Fire PC Setup (WASD + Mouse)</button>
    </div>

    <!-- TAB 1: TYPING -->
    <div id="tabTyping" class="tab-content active">
      <textarea id="inputText" placeholder="Type on PC keyboard... (Press Enter or Ctrl+Enter to send to AI Mobile)" autofocus></textarea>
      <div class="actions">
        <button class="act-btn btn-primary" onclick="sendToChat()">🚀 Send to Mobile Chat (Enter)</button>
        <button class="act-btn btn-secondary" onclick="sendToStudio()">📝 Send to Studio</button>
        <button class="act-btn btn-secondary" onclick="clearText()">🗑️ Clear</button>
      </div>
    </div>

    <!-- TAB 2: FREE FIRE PC SETUP -->
    <div id="tabGame" class="tab-content">
      <div class="ff-banner">
        <span>🎮 Free Fire PC Keymap Active</span>
        <span>Press Physical PC Keys directly!</span>
      </div>

      <div class="game-grid">
        <!-- Movement Cluster (WASD) -->
        <div class="ctrl-box">
          <div class="ctrl-title">Movement (WASD / Shift)</div>
          <div class="wasd-cluster">
            <div class="key-cap" id="keyW" onclick="sendGameKey('MOVE_FORWARD')">W<span>UP</span></div>
            <div class="wasd-row">
              <div class="key-cap" id="keyA" onclick="sendGameKey('MOVE_LEFT')">A<span>LEFT</span></div>
              <div class="key-cap" id="keyS" onclick="sendGameKey('MOVE_BACKWARD')">S<span>DOWN</span></div>
              <div class="key-cap" id="keyD" onclick="sendGameKey('MOVE_RIGHT')">D<span>RIGHT</span></div>
            </div>
          </div>
          <div style="margin-top: 10px; display: flex; gap: 8px;">
            <div class="act-key" style="flex: 1;" id="keyShift" onclick="sendGameKey('SPRINT')">SHIFT [Sprint]</div>
            <div class="act-key" style="flex: 1;" id="keyC" onclick="sendGameKey('CROUCH')">C [Crouch]</div>
          </div>
        </div>

        <!-- Combat & Action Cluster -->
        <div class="ctrl-box">
          <div class="ctrl-title">Combat & Weapons</div>
          <div class="action-keys">
            <div class="act-key fire-key" id="keyFire" onclick="sendGameKey('SHOOT')">🔥 LEFT CLICK / ENTER (FIRE)</div>
            <div class="act-key" id="keySpace" onclick="sendGameKey('JUMP')">SPACE [Jump]</div>
            <div class="act-key" id="keyScope" onclick="sendGameKey('SCOPE')">R-CLICK [Scope]</div>
            <div class="act-key" id="keyR" onclick="sendGameKey('RELOAD')">R [Reload]</div>
            <div class="act-key" id="key1" onclick="sendGameKey('WEAPON_1')">[1] AK47</div>
            <div class="act-key" id="key2" onclick="sendGameKey('WEAPON_2')">[2] MP40</div>
            <div class="act-key" id="key3" onclick="sendGameKey('WEAPON_3')">[3] AWM</div>
            <div class="act-key" id="keyG" onclick="sendGameKey('GLOO_WALL')">G [Gloo Wall]</div>
            <div class="act-key" id="keyH" onclick="sendGameKey('MEDKIT')">H [Medkit]</div>
            <div class="act-key" id="keyF" onclick="sendGameKey('LOOT')">F [Loot]</div>
          </div>
        </div>
      </div>
    </div>

    <div class="log-box" id="statusMsg">Connected to AI Mobile. Press keys on your keyboard!</div>

    <div class="tips">
      <span>💡 PC Keyboard Hotkeys: <b>WASD</b> Move | <b>Space</b> Jump | <b>Enter/Click</b> Shoot | <b>1/2/3</b> Weapons</span>
      <span>📱 AI Mobile (8GB RAM / 128GB ROM)</span>
    </div>
  </div>

  <script>
    let activeTab = 'typing';
    const statusMsg = document.getElementById('statusMsg');
    const textarea = document.getElementById('inputText');

    function switchTab(tab) {
      activeTab = tab;
      document.getElementById('tabTyping').className = tab === 'typing' ? 'tab-content active' : 'tab-content';
      document.getElementById('tabGame').className = tab === 'game' ? 'tab-content active' : 'tab-content';
      document.getElementById('tabTypingBtn').className = tab === 'typing' ? 'tab-btn active' : 'tab-btn';
      document.getElementById('tabGameBtn').className = tab === 'game' ? 'tab-btn ff-active' : 'tab-btn';
      statusMsg.innerText = tab === 'game' ? '🎮 Free Fire PC Keymap Active! Press WASD, Space, Click to play!' : '⌨️ Typing mode active';
    }

    function sendGameKey(actionKey) {
      flashKey(actionKey);
      statusMsg.innerText = '🎮 PC Sent: ' + actionKey;
      fetch('/api/free_fire', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'key=' + encodeURIComponent(actionKey)
      });
    }

    function flashKey(actionKey) {
      const map = {
        'MOVE_FORWARD': 'keyW', 'MOVE_LEFT': 'keyA', 'MOVE_BACKWARD': 'keyS', 'MOVE_RIGHT': 'keyD',
        'JUMP': 'keySpace', 'SHOOT': 'keyFire', 'SCOPE': 'keyScope', 'RELOAD': 'keyR',
        'WEAPON_1': 'key1', 'WEAPON_2': 'key2', 'WEAPON_3': 'key3', 'GLOO_WALL': 'keyG',
        'MEDKIT': 'keyH', 'SPRINT': 'keyShift', 'CROUCH': 'keyC', 'LOOT': 'keyF'
      };
      const elem = document.getElementById(map[actionKey]);
      if (elem) {
        elem.classList.add('pressed');
        setTimeout(() => elem.classList.remove('pressed'), 120);
      }
    }

    // Physical PC Keyboard Listener
    window.addEventListener('keydown', (e) => {
      if (activeTab === 'game') {
        const k = e.code;
        if (k === 'KeyW' || k === 'ArrowUp') { e.preventDefault(); sendGameKey('MOVE_FORWARD'); }
        else if (k === 'KeyA' || k === 'ArrowLeft') { e.preventDefault(); sendGameKey('MOVE_LEFT'); }
        else if (k === 'KeyS' || k === 'ArrowDown') { e.preventDefault(); sendGameKey('MOVE_BACKWARD'); }
        else if (k === 'KeyD' || k === 'ArrowRight') { e.preventDefault(); sendGameKey('MOVE_RIGHT'); }
        else if (k === 'Space') { e.preventDefault(); sendGameKey('JUMP'); }
        else if (k === 'Enter') { e.preventDefault(); sendGameKey('SHOOT'); }
        else if (k === 'KeyR') { e.preventDefault(); sendGameKey('RELOAD'); }
        else if (k === 'KeyC') { e.preventDefault(); sendGameKey('CROUCH'); }
        else if (k === 'ShiftLeft' || k === 'ShiftRight') { e.preventDefault(); sendGameKey('SPRINT'); }
        else if (k === 'Digit1') { e.preventDefault(); sendGameKey('WEAPON_1'); }
        else if (k === 'Digit2') { e.preventDefault(); sendGameKey('WEAPON_2'); }
        else if (k === 'Digit3') { e.preventDefault(); sendGameKey('WEAPON_3'); }
        else if (k === 'KeyG') { e.preventDefault(); sendGameKey('GLOO_WALL'); }
        else if (k === 'KeyH') { e.preventDefault(); sendGameKey('MEDKIT'); }
        else if (k === 'KeyE') { e.preventDefault(); sendGameKey('SCOPE'); }
        else if (k === 'KeyF') { e.preventDefault(); sendGameKey('LOOT'); }
      } else {
        if ((e.ctrlKey || e.metaKey || !e.shiftKey) && e.key === 'Enter') {
          e.preventDefault();
          sendToChat();
        }
      }
    });

    textarea.addEventListener('input', () => {
      fetch('/api/type', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'text=' + encodeURIComponent(textarea.value)
      });
    });

    function sendToChat() {
      const text = textarea.value.trim();
      if (!text) return;
      statusMsg.innerText = 'Sending to Mobile Chat...';
      fetch('/api/send_chat', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'text=' + encodeURIComponent(text)
      }).then(() => {
        statusMsg.innerText = 'Sent to Mobile Chat successfully!';
        textarea.value = '';
        setTimeout(() => { statusMsg.innerText = ''; }, 2000);
      });
    }

    function sendToStudio() {
      const text = textarea.value.trim();
      if (!text) return;
      statusMsg.innerText = 'Sending to Content Studio...';
      fetch('/api/send_studio', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'text=' + encodeURIComponent(text)
      }).then(() => {
        statusMsg.innerText = 'Sent to Document Studio!';
        setTimeout(() => { statusMsg.innerText = ''; }, 2000);
      });
    }

    function clearText() {
      textarea.value = '';
      fetch('/api/type', {
        method: 'POST',
        headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
        body: 'text='
      });
      statusMsg.innerText = 'Cleared';
    }
  </script>
</body>
</html>
""".trimIndent()

        val bytes = html.toByteArray(Charsets.UTF_8)
        writer.println("HTTP/1.1 200 OK")
        writer.println("Content-Type: text/html; charset=utf-8")
        writer.println("Content-Length: ${bytes.size}")
        writer.println()
        writer.println(html)
    }

    private fun getLocalIpAddress(): String {
        try {
            // First try WifiManager
            val wifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager
            if (wifiManager != null) {
                val ipInt = wifiManager.connectionInfo.ipAddress
                if (ipInt != 0) {
                    return Formatter.formatIpAddress(ipInt)
                }
            }

            // Fallback: iterate network interfaces for non-loopback IPv4
            val interfaces = NetworkInterface.getNetworkInterfaces()
            while (interfaces.hasMoreElements()) {
                val iface = interfaces.nextElement()
                val addresses = iface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val addr = addresses.nextElement()
                    if (!addr.isLoopbackAddress && addr is Inet4Address) {
                        return addr.hostAddress ?: "127.0.0.1"
                    }
                }
            }
        } catch (_: Exception) {}
        return "127.0.0.1"
    }
}
