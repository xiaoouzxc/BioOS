(function () {
  var pendingHref = null;
  var modalEl = null;
  var msgEl = null;
  var titleEl = null;
  var descEl = null;
  var fullNameRowEl = null;
  var submitBtnEl = null;
  var switchBtnEl = null;
  var authMode = "login";

  function qs(selector) {
    return document.querySelector(selector);
  }

  function qsa(selector) {
    return Array.prototype.slice.call(document.querySelectorAll(selector));
  }

  function parseJsonSafe(text) {
    try {
      return JSON.parse(text);
    } catch (e) {
      return null;
    }
  }

  function request(url, options) {
    return fetch(url, options).then(function (res) {
      return res.text().then(function (text) {
        return {
          ok: res.ok,
          status: res.status,
          data: parseJsonSafe(text),
          raw: text
        };
      });
    });
  }

  function getCurrentUser() {
    return request("/api/auth/me", {
      method: "GET",
      credentials: "same-origin"
    }).then(function (ret) {
      if (!ret.ok || !ret.data || !ret.data.username) return null;
      return ret.data;
    }).catch(function () {
      return null;
    });
  }

  function setMsg(text) {
    if (msgEl) msgEl.textContent = text || "";
  }

  function ensureModal() {
    if (modalEl) return modalEl;

    modalEl = document.createElement("div");
    modalEl.className = "lab-auth-modal-wrap";
    modalEl.innerHTML = [
      '<div class="lab-auth-modal-backdrop"></div>',
      '<div class="lab-auth-modal" role="dialog" aria-modal="true" aria-label="认证">',
      '  <h3 id="labAuthTitle">系统登录</h3>',
      '  <p class="lab-auth-modal-desc" id="labAuthDesc">请先登录后再进入该模块。</p>',
      '  <form id="labAuthForm">',
      '    <label>用户名<input id="labAuthUsername" type="text" required /></label>',
      '    <label id="labAuthFullNameRow">姓名<input id="labAuthFullName" type="text" /></label>',
      '    <label>密码<input id="labAuthPassword" type="password" required /></label>',
      '    <p class="lab-auth-modal-msg" id="labAuthMsg"></p>',
      '    <div class="lab-auth-modal-actions">',
      '      <button type="button" class="lab-button" id="labAuthCancel">取消</button>',
      '      <button type="submit" class="lab-button lab-button-primary" id="labAuthSubmit">登录</button>',
      '    </div>',
      '    <button type="button" class="lab-auth-switch" id="labAuthSwitch">没有账号？注册新用户</button>',
      '  </form>',
      '</div>'
    ].join("");

    document.body.appendChild(modalEl);
    msgEl = qs("#labAuthMsg");
    titleEl = qs("#labAuthTitle");
    descEl = qs("#labAuthDesc");
    fullNameRowEl = qs("#labAuthFullNameRow");
    submitBtnEl = qs("#labAuthSubmit");
    switchBtnEl = qs("#labAuthSwitch");

    qs("#labAuthCancel").addEventListener("click", closeModal);
    qs(".lab-auth-modal-backdrop").addEventListener("click", closeModal);
    switchBtnEl.addEventListener("click", function () {
      setMode(authMode === "login" ? "register" : "login");
    });
    qs("#labAuthForm").addEventListener("submit", function (e) {
      e.preventDefault();
      submitAuth();
    });

    return modalEl;
  }

  function setMode(mode) {
    authMode = mode === "register" ? "register" : "login";
    setMsg("");
    if (authMode === "register") {
      titleEl.textContent = "注册新用户";
      descEl.textContent = "创建新账号后可登录并使用系统。";
      fullNameRowEl.style.display = "";
      submitBtnEl.textContent = "注册";
      switchBtnEl.textContent = "已有账号？去登录";
    } else {
      titleEl.textContent = "系统登录";
      descEl.textContent = "请先登录后再进入该模块。";
      fullNameRowEl.style.display = "none";
      submitBtnEl.textContent = "登录";
      switchBtnEl.textContent = "没有账号？注册新用户";
    }
  }

  function openModal(mode) {
    ensureModal();
    setMode(mode || "login");
    modalEl.classList.add("is-open");
    qs("#labAuthUsername").focus();
  }

  function closeModal() {
    if (!modalEl) return;
    modalEl.classList.remove("is-open");
    pendingHref = null;
  }

  function renderUserArea(username) {
    qsa("[data-auth-user]").forEach(function (node) {
      var userText = node.querySelector("[data-auth-user-text]");
      if (!userText) {
        var directSpans = Array.prototype.filter.call(node.children, function (child) {
          return child.tagName && child.tagName.toLowerCase() === "span";
        });
        userText = directSpans.length ? directSpans[0] : null;
      }
      if (!userText) {
        userText = document.createElement("span");
        node.insertBefore(userText, node.firstChild);
      }
      userText.setAttribute("data-auth-user-text", "1");
      userText.textContent = username ? ("当前用户：" + username) : "当前用户：未登录";

      var actionWrap = node.querySelector(".lab-auth-inline-actions");
      if (!actionWrap) {
        actionWrap = document.createElement("div");
        actionWrap.className = "lab-auth-inline-actions";
        actionWrap.innerHTML = [
          '<button type="button" class="lab-button lab-auth-login-btn">登录</button>',
          '<button type="button" class="lab-button lab-auth-register-btn">注册新用户</button>',
          '<button type="button" class="lab-button lab-button-danger lab-auth-logout-btn">退出登录</button>'
        ].join("");
        node.appendChild(actionWrap);
      }

      var loginBtn = actionWrap.querySelector(".lab-auth-login-btn");
      var registerBtn = actionWrap.querySelector(".lab-auth-register-btn");
      var logoutBtn = actionWrap.querySelector(".lab-auth-logout-btn");
      loginBtn.style.display = username ? "none" : "";
      registerBtn.style.display = username ? "none" : "";
      logoutBtn.style.display = username ? "" : "none";

      if (!loginBtn.dataset.bound) {
        loginBtn.dataset.bound = "1";
        loginBtn.addEventListener("click", function () {
          openModal("login");
        });
      }
      if (!registerBtn.dataset.bound) {
        registerBtn.dataset.bound = "1";
        registerBtn.addEventListener("click", function () {
          openModal("register");
        });
      }
      if (!logoutBtn.dataset.bound) {
        logoutBtn.dataset.bound = "1";
        logoutBtn.addEventListener("click", function () {
          request("/api/auth/logout", {
            method: "POST",
            credentials: "same-origin"
          }).then(function () {
            renderUserArea("");
            alert("已退出登录");
          }).catch(function () {
            alert("退出登录失败");
          });
        });
      }
    });
  }

  function submitAuth() {
    var username = qs("#labAuthUsername").value.trim();
    var password = qs("#labAuthPassword").value;
    var fullName = qs("#labAuthFullName").value.trim();
    if (!username || !password) {
      setMsg("请输入用户名和密码");
      return;
    }

    if (authMode === "register") {
      setMsg("注册中...");
      request("/api/auth/register", {
        method: "POST",
        credentials: "same-origin",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({
          username: username,
          password: password,
          fullName: fullName || username
        })
      }).then(function (ret) {
        if (!ret.ok) {
          setMsg((ret.data && ret.data.message) ? ret.data.message : "注册失败");
          return;
        }
        setMsg("注册成功，请登录");
        setMode("login");
        qs("#labAuthUsername").value = username;
        qs("#labAuthPassword").value = "";
        qs("#labAuthPassword").focus();
      }).catch(function () {
        setMsg("注册请求失败");
      });
      return;
    }

    setMsg("登录中...");
    request("/api/auth/login", {
      method: "POST",
      credentials: "same-origin",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ username: username, password: password })
    }).then(function (ret) {
      if (ret.data && ret.data.status === "duo") {
        var takeover = confirm("检测到该账号已在其他终端登录，是否挤掉旧会话并继续登录？");
        if (!takeover) {
          setMsg("已取消本次登录");
          return;
        }
        return request("/api/auth/logout", {
          method: "POST",
          credentials: "same-origin",
          headers: { "Content-Type": "application/x-www-form-urlencoded;charset=UTF-8" },
          body: "deleteusername=" + encodeURIComponent(ret.data.username || username)
        }).then(function () {
          return request("/api/auth/login", {
            method: "POST",
            credentials: "same-origin",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({ username: username, password: password })
          });
        }).then(function (retryRet) {
          if (!retryRet.ok) {
            setMsg((retryRet.data && retryRet.data.message) ? retryRet.data.message : "登录失败");
            return;
          }
          var retryUser = retryRet.data && retryRet.data.user ? retryRet.data.user.username : username;
          renderUserArea(retryUser);
          closeModal();
          if (pendingHref) window.location.href = pendingHref;
        });
      }

      if (!ret.ok) {
        setMsg((ret.data && ret.data.message) ? ret.data.message : "登录失败");
        return;
      }
      var realUser = ret.data && ret.data.user ? ret.data.user.username : username;
      renderUserArea(realUser);
      closeModal();
      if (pendingHref) window.location.href = pendingHref;
    }).catch(function () {
      setMsg("登录请求失败");
    });
  }

  function guardNavClicks() {
    qsa(".lab-nav .lab-nav-item[href]").forEach(function (link) {
      link.addEventListener("click", function (e) {
        var href = link.getAttribute("href");
        if (!href || href === "#") return;
        var linkUrl = new URL(href, window.location.origin);
        if (linkUrl.pathname === window.location.pathname) return;
        e.preventDefault();
        getCurrentUser().then(function (user) {
          if (user && user.username) {
            renderUserArea(user.username);
            window.location.href = href;
            return;
          }
          pendingHref = href;
          openModal("login");
        });
      });
    });
  }

  function refreshAuthState() {
    getCurrentUser().then(function (user) {
      if (user && user.username) {
        renderUserArea(user.username);
      } else {
        renderUserArea("");
      }
    });
  }

  function initLimsAutoImport() {
    var STORAGE_PREFIX = "limsAutoImport.";
    var DEFAULT_INTERVAL_MINUTES = 30;
    var endpoint = "/api/starlims/auto-import/run";
    var statusEndpoint = "/api/starlims/auto-import/status";
    var configEndpoint = "/api/starlims/auto-import/config";
    var topbars = qsa(".lab-topbar");
    if (!topbars.length) return;

    function readNumber(key, fallback) {
      var value = Number(localStorage.getItem(STORAGE_PREFIX + key));
      return Number.isFinite(value) && value > 0 ? value : fallback;
    }

    function readBool(key, fallback) {
      var value = localStorage.getItem(STORAGE_PREFIX + key);
      if (value === "true") return true;
      if (value === "false") return false;
      return fallback;
    }

    function writeState(key, value) {
      localStorage.setItem(STORAGE_PREFIX + key, String(value));
    }

    function formatCountdown(ms) {
      var totalSeconds = Math.max(0, Math.ceil(ms / 1000));
      var hours = Math.floor(totalSeconds / 3600);
      var minutes = Math.floor((totalSeconds % 3600) / 60);
      var seconds = totalSeconds % 60;
      var mm = String(minutes).padStart(2, "0");
      var ss = String(seconds).padStart(2, "0");
      return hours > 0 ? (hours + ":" + mm + ":" + ss) : (mm + ":" + ss);
    }

    function getMessage(data, raw) {
      if (!data) return raw || "请求完成";
      if (data.importResult && data.importResult.message) return data.importResult.message;
      if (data.message) return data.message;
      return "请求完成";
    }

    topbars.forEach(function (topbar) {
      if (topbar.querySelector(".lims-auto-widget")) return;

      var intervalMinutes = readNumber("intervalMinutes", DEFAULT_INTERVAL_MINUTES);
      var paused = readBool("paused", false);
      var nextRunAt = Number(localStorage.getItem(STORAGE_PREFIX + "nextRunAt"));
      if (!Number.isFinite(nextRunAt) || nextRunAt <= Date.now()) {
        nextRunAt = Date.now() + intervalMinutes * 60 * 1000;
        writeState("nextRunAt", nextRunAt);
      }

      var widget = document.createElement("div");
      widget.className = "lims-auto-widget";
      widget.innerHTML = [
        '<button type="button" class="lims-auto-trigger" aria-label="LIMS自动导入">',
        '  <span class="lims-auto-spinner" aria-hidden="true">',
        '    <span></span><span></span><span></span><span></span><span></span><span></span>',
        '    <span></span><span></span><span></span><span></span><span></span><span></span>',
        '  </span>',
        '  <span class="lims-auto-countdown">--:--</span>',
        '</button>',
        '<div class="lims-auto-panel" role="dialog" aria-label="LIMS自动导入设置">',
        '  <div class="lims-auto-panel-head">',
        '    <strong>LIMS自动导入</strong>',
        '    <span class="lims-auto-state">等待中</span>',
        '  </div>',
        '  <label class="lims-auto-field">执行间隔（分钟）',
        '    <input class="lims-auto-interval" type="number" min="1" max="1440" step="1" />',
        '  </label>',
        '  <div class="lims-auto-actions">',
        '    <button type="button" class="lab-button lab-button-primary lims-auto-run">立即请求</button>',
        '    <button type="button" class="lab-button lims-auto-toggle"></button>',
        '  </div>',
        '  <p class="lims-auto-note">服务器端半小时自动执行，当前页面只显示状态和控制开关。</p>',
        '</div>'
      ].join("");

      var userArea = topbar.querySelector(".lab-user-area");
      if (userArea) {
        topbar.insertBefore(widget, userArea);
      } else {
        topbar.appendChild(widget);
      }

      var trigger = widget.querySelector(".lims-auto-trigger");
      var countdownEl = widget.querySelector(".lims-auto-countdown");
      var stateEl = widget.querySelector(".lims-auto-state");
      var intervalInput = widget.querySelector(".lims-auto-interval");
      var runBtn = widget.querySelector(".lims-auto-run");
      var toggleBtn = widget.querySelector(".lims-auto-toggle");
      var running = false;
      var touchStarted = false;
      var statusRequesting = false;
      var lastSeenImportVersion = null;
	  
	  var manualCooldownUntil = 0;
	  var serverTimeOffset = 0;

      intervalInput.value = intervalMinutes;

      function setOpen(open) {
        widget.classList.toggle("is-open", !!open);
      }

      function setPaused(value) {
        paused = !!value;
        writeState("paused", paused);
            updateUi();
            refreshServerStatus();
      }

      function scheduleNext() {
        nextRunAt = Date.now() + intervalMinutes * 60 * 1000;
        writeState("nextRunAt", nextRunAt);
      }

	  function updateUi() {
	    widget.classList.toggle("is-running", running);
	    widget.classList.toggle("is-paused", paused);
	    toggleBtn.textContent = paused ? "恢复自动" : "暂停自动";

	    if (running) {
	      stateEl.textContent = "正在请求";
	      countdownEl.textContent = "运行中";
	    } else if (paused) {
	      stateEl.textContent = "已暂停";
	      countdownEl.textContent = "暂停";
	    } else {
	      var left = nextRunAt - Date.now();
	      countdownEl.textContent = formatCountdown(left);
	      stateEl.textContent = "下次执行 " + formatCountdown(left);
	    }

		var now = Date.now() + serverTimeOffset;
		var cooldownLeft = manualCooldownUntil - now;

	    if (cooldownLeft > 0) {
	      runBtn.disabled = true;
	      runBtn.textContent = "冷却中 " + formatCountdown(cooldownLeft);
	    } else {
	      runBtn.disabled = running;
	      runBtn.textContent = running ? "请求中" : "立即请求";
	    }
	  }

	  function applyServerStatus(data) {
	    if (!data) return;

	    if (Number.isFinite(Number(data.serverTime))) {
	      serverTimeOffset = Number(data.serverTime) - Date.now();
	    }

	    manualCooldownUntil = Number(data.manualCooldownUntil) || 0;

	    var importVersion = Number(data.importVersion);
	    if (Number.isFinite(importVersion)) {
	      if (lastSeenImportVersion !== null && importVersion !== lastSeenImportVersion) {
	        window.dispatchEvent(new CustomEvent("lims:samples-imported", { detail: data }));
	      }
	      lastSeenImportVersion = importVersion;
	    }

	    intervalMinutes = Number(data.intervalMinutes) || intervalMinutes;
	    paused = !!data.paused;
	    running = !!data.running;
	    nextRunAt = Number(data.nextRunAt) || nextRunAt;
	    intervalInput.value = intervalMinutes;
	    updateUi();
	  }

      function refreshServerStatus() {
        if (statusRequesting) return;
        statusRequesting = true;
        fetch(statusEndpoint, {
          method: "GET",
          credentials: "same-origin"
        }).then(function (res) {
          return res.json();
        }).then(applyServerStatus).catch(function () {
          stateEl.textContent = "服务器状态获取失败";
        }).finally(function () {
          statusRequesting = false;
        });
      }

      function updateServerConfig(payload) {
        return fetch(configEndpoint, {
          method: "POST",
          credentials: "same-origin",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload || {})
        }).then(function (res) {
          return res.json();
        }).then(applyServerStatus);
      }

	  function runImport(manual) {
	    if (running) return;

	    if (manual && manualCooldownUntil > Date.now()) {
	      updateUi();
	      setOpen(true);
	      return;
	    }

	    running = true;
	    updateUi();

	    fetch(endpoint, {
	      method: "POST",
	      credentials: "same-origin",
	      headers: { "Content-Type": "application/json" },
	      body: JSON.stringify({ manual: !!manual })
	    }).then(function (res) {
	      return res.text().then(function (text) {
	        return { ok: res.ok, status: res.status, raw: text, data: parseJsonSafe(text) };
	      });
	    }).then(function (ret) {
	      running = false;

	      if (ret.data && Number(ret.data.manualCooldownUntil)) {
	        manualCooldownUntil = Number(ret.data.manualCooldownUntil);
	      }

	      stateEl.textContent = ret.ok ? getMessage(ret.data, ret.raw) : ("请求失败 " + ret.status);
	      updateUi();
	      refreshServerStatus();

	      if (manual) setOpen(true);
	    }).catch(function () {
	      running = false;
	      stateEl.textContent = "请求异常";
	      updateUi();
	      refreshServerStatus();
	      if (manual) setOpen(true);
	    });
	  }

      trigger.addEventListener("click", function (e) {
        e.stopPropagation();
        if (touchStarted) {
          touchStarted = false;
          return;
        }
        setOpen(!widget.classList.contains("is-open"));
      });

      widget.addEventListener("mouseenter", function () {
        setOpen(true);
      });

      widget.addEventListener("mouseleave", function () {
        setOpen(false);
      });

      trigger.addEventListener("touchstart", function (e) {
        e.stopPropagation();
        touchStarted = true;
        setOpen(!widget.classList.contains("is-open"));
      }, { passive: true });

      document.addEventListener("click", function (e) {
        if (!widget.contains(e.target)) setOpen(false);
      });

      document.addEventListener("touchstart", function (e) {
        if (!widget.contains(e.target)) setOpen(false);
      }, { passive: true });

      intervalInput.addEventListener("change", function () {
        var nextInterval = Math.max(1, Math.min(1440, Number(intervalInput.value) || DEFAULT_INTERVAL_MINUTES));
        intervalInput.value = nextInterval;
        updateServerConfig({ intervalMinutes: nextInterval }).catch(function () {
          stateEl.textContent = "间隔设置失败";
        });
      });

      toggleBtn.addEventListener("click", function () {
        updateServerConfig({ paused: !paused }).catch(function () {
          stateEl.textContent = "暂停设置失败";
        });
      });

	  runBtn.addEventListener("click", function () {
	    if (runBtn.disabled) return;
	    runImport(true);
	  });

      window.setInterval(function () {
        updateUi();
      }, 1000);

      window.setInterval(refreshServerStatus, 1000);
      refreshServerStatus();
      updateUi();
    });
  }

  document.addEventListener("DOMContentLoaded", function () {
    ensureModal();
    initLimsAutoImport();
    guardNavClicks();
    refreshAuthState();
  });
})();
