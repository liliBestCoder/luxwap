// ---------------- Tabs 切换 ----------------
function showMainTab(tab) {
  const tabs = ["payment", "users", "lines", "billing", "activity", "dealer", "access"];
  tabs.forEach((t) => {
    const content = document.getElementById(`${t}-tab`);
    const btn = Array.from(document.querySelectorAll(".settings-tab")).find((b) => b.getAttribute("onclick")?.includes(`'${t}'`));
    if (content) content.classList.remove("active");
    if (btn) btn.classList.remove("active");
  });
  const activeContent = document.getElementById(`${tab}-tab`);
  const activeBtn = Array.from(document.querySelectorAll(".settings-tab")).find((b) => b.getAttribute("onclick")?.includes(`'${tab}'`));
  if (activeContent) activeContent.classList.add("active");
  if (activeBtn) activeBtn.classList.add("active");
  window.scrollTo({ top: 0, behavior: "smooth" });

  // 仅在首次进入时初始化权限模块
  if (tab === "access" && !window.__accessInit) {
    initAccessTab();
    window.__accessInit = true;
  }

  window.menu = tab;

  if (tab === 'users') {
    searchUsers();
  } else if (tab === 'lines') {
    searchLines();
  } else if (tab === 'dealer') {
    searchDistributorsSettings();
    searchDistributors();
    searchDistributorsApply();
  } else if (tab === 'activity') {
    searchActivitySettings();
  } else if (tab === 'access') {
    searchInnerUsers();
  }
}

// ---------------- 支付配置展开/保存 ----------------
function configureGateway(name) {
  const form = document.getElementById(`${name}-form`);
  if (!form) return;

  // 如果表单已经显示，则直接隐藏
  if (form.style.display === "block") {
    form.style.display = "none";
    return;
  }

  // 显示加载状态
  toast(`正在加载 ${name} 配置...`, "info");

  // 获取当前配置
  fetch("/system/adminsettings/payment/get/" + name, {
    method: "GET",
    credentials: "include",
  })
    .then(res => res.json())
    .then(resp => {
      if (resp.code !== 0) {
        toast(resp.msg || `未找到 ${name} 配置`, "error");
        return;
      }

      const merchant = resp.data || {};
      let cfg = {};
      try {
        cfg = merchant.config ? JSON.parse(merchant.config) : {};
      } catch (e) {
        console.error("解析配置失败", e);
      }

      // 根据不同支付类型赋值表单
      switch (name) {
        case "alipay":
          form.querySelector("#alipay-status-select").value = merchant.status === 1 ? "enabled" : "disabled";
          form.querySelector("#alipay-app-id").value = cfg.appId || "";
          form.querySelector("#alipay-private-key").value = cfg.privateKey || "";
          form.querySelector("#alipay-public-key").value = cfg.alipayPublicKey || "";
          form.querySelector("#alipay-format").value = cfg.format || "json";
          form.querySelector("#alipay-charset").value = cfg.charset || "utf-8";
          form.querySelector("#alipay-sign-type").value = cfg.signType || "RSA2";
          break;
        case "wechat":
          form.querySelector("#wechat-status-select").value = merchant.status === 1 ? "enabled" : "disabled";
          form.querySelector("#wechat-appid").value = cfg.appId || "";
          form.querySelector("#wechat-mch-id").value = cfg.mchId || "";
          form.querySelector("#wechat-api-key").value = cfg.apiKey || "";
          form.querySelector("#wechat-appsecret").value = cfg.appSecret || "";
          // 文件输入不自动赋值，用户需要重新选择
          break;
        case "stripe":
          form.querySelector("#stripe-status-select").value = merchant.status === 1 ? "enabled" : "disabled";
          form.querySelector("#stripe-publishable-key").value = cfg.publishableKey || "";
          form.querySelector("#stripe-secret-key").value = cfg.secretKey || "";
          form.querySelector("#stripe-webhook-secret").value = cfg.webhookSecret || "";
          form.querySelector("#stripe-currency").value = cfg.currency || "usd";
          break;
        case "paypal":
          form.querySelector("#paypal-status-select").value = merchant.status === 1 ? "enabled" : "disabled";
          form.querySelector("#paypal-mode").value = cfg.mode || "sandbox";
          form.querySelector("#paypal-client-id").value = cfg.clientId || "";
          form.querySelector("#paypal-client-secret").value = cfg.clientSecret || "";
          form.querySelector("#paypal-webhook-id").value = cfg.webhookId || "";
          break;
        case "circle":
          form.querySelector("#circle-status-select").value = merchant.status === 1 ? "enabled" : "disabled";
          form.querySelector("#circle-api-key").value = cfg.apiKey || "";
          form.querySelector("#circle-entity-secret").value = cfg.entitySecret || "";
          form.querySelector("#circle-base-url").value = cfg.baseUrl || "";
          form.querySelector("#circle-master-wallet-id").value = cfg.masterWalletId || "";
          break;
        default:
          // 通用表单赋值
          form.querySelectorAll("input, select, textarea").forEach(el => {
            if (cfg[el.id]) {
              el.value = cfg[el.id];
            }
          });
          break;
      }

      // 显示表单
      form.style.display = "block";
      toast(`${name} 配置加载完成`, "success");
    })
    .catch(err => {
      console.error(err);
      toast("加载配置失败，请重试", "error");
    });
}


function saveGatewayConfig(name) {
  const data = {};
  const type = name;
  const status = document.getElementById(`${name}-status-select`)?.value || "disabled";

  if (!type || !status) {
    toast("支付方式类型和状态不能为空", "error");
    return;
  }

  // 校验并构造 config
  switch (name) {
    case "alipay":
      data.appId = document.getElementById("alipay-app-id").value.trim();
      data.privateKey = document.getElementById("alipay-private-key").value.trim();
      data.alipayPublicKey = document.getElementById("alipay-public-key").value.trim();
      data.notifyUrl = document.getElementById("alipay-notify-url").value.trim();
      data.format = document.getElementById("alipay-format").value.trim();
      data.signType = document.getElementById("alipay-sign-type").value.trim();
      data.charset = document.getElementById("alipay-charset").value.trim();
      if (!data.appId || !data.privateKey || !data.alipayPublicKey || !data.notifyUrl || !data.format || !data.signType || !data.charset) {
        toast("支付宝配置缺少必填字段", "error");
        return;
      }
      break;

    case "wechat":
      data.appId = document.getElementById("wechat-appid").value.trim();
      data.mchId = document.getElementById("wechat-mch-id").value.trim();
      data.apiKey = document.getElementById("wechat-api-key").value.trim();
      data.notifyUrl = document.getElementById("wechat-notify-url").value.trim();
      if (!data.appId || !data.mchId || !data.apiKey || !data.notifyUrl) {
        toast("微信支付配置缺少必填字段", "error");
        return;
      }
      break;

    case "stripe":
      data.secretKey = document.getElementById("stripe-secret-key").value.trim();
      data.publishableKey = document.getElementById("stripe-publishable-key").value.trim();
      data.webhookSecret = document.getElementById("stripe-webhook-secret").value.trim();
      if (!data.secretKey || !data.publishableKey) {
        toast("Stripe配置缺少必填字段", "error");
        return;
      }
      break;

    case "paypal":
      data.clientId = document.getElementById("paypal-client-id").value.trim();
      data.clientSecret = document.getElementById("paypal-client-secret").value.trim();
      data.webhookId = document.getElementById("paypal-webhook-id").value.trim();
      if (!data.clientId || !data.clientSecret) {
        toast("PayPal配置缺少必填字段", "error");
        return;
      }
      break;

    case "circle":
      data.apiKey = document.getElementById("circle-api-key").value.trim();
      data.accountId = document.getElementById("circle-account-id").value.trim();
      data.baseUrl = document.getElementById("circle-base-url").value.trim();
      if (!data.apiKey || !data.accountId || !data.baseUrl) {
        toast("Circle配置缺少必填字段", "error");
        return;
      }
      break;

    default:
      toast("未知支付方式", "error");
      return;
  }

  // 构造 FormData（兼容 multipart/form-data）
  const formData = new FormData();
  formData.append("type", type);
  formData.append("status", status === "enabled" ? "1" : "0");
  formData.append("config", JSON.stringify(data));

  fetch("/system/adminsettings/payment/save", {
    method: "POST",
    body: formData,
    credentials: "include",
  })
    .then((res) => res.json())
    .then((resp) => {
      if (resp.code === 0) {
        toast(`已保存 ${name} 配置`, "success");
        const formEl = document.getElementById(`${name}-form`);
        if (formEl) formEl.style.display = "none";
      } else {
        toast(resp.msg || "保存失败", "error");
      }
    })
    .catch((err) => {
      console.error(err);
      toast("网络错误，请重试", "error");
    });
}



// ---------------- CSV 上传与预览 ----------------
const csvPreview2 = document.getElementById("csvPreview2");
const csvTableBody2 = document.getElementById("csvTableBody2");
const uploadArea = document.querySelector(".file-upload-area");

function handleFileSelect(e) {
  const file = e.target.files[0];
  if (file) parseCSV(file);
}

function handleDragOver(e) {
  e.preventDefault();
  uploadArea?.classList.add("dragover");
}
function handleDragLeave(e) {
  e.preventDefault();
  uploadArea?.classList.remove("dragover");
}
function handleFileDrop(e) {
  e.preventDefault();
  uploadArea?.classList.remove("dragover");
  const file = e.dataTransfer.files[0];
  if (file) parseCSV(file);
}

async function editUser(id) {
  const title = document.getElementById('modalTitle');
  if (title) title.textContent = '编辑用户';

  try {

    const response = await fetch(`/system/xray-user/detail/${id}`, {
      method: 'GET',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      credentials: 'include'
    });
    const data = await response.json();

    if (data.code !== 0) {
      toast(data.msg, 'error');
      return;
    }

    // 模拟填充表单数据
    const userId = document.getElementById('user_id');
    const username = document.getElementById('username');
    const email = document.getElementById('email');
    const userStatus = document.getElementById('userStatus');
    const expiration = document.getElementById('expiration');
    if (userId) userId.value = data.data.id;
    if (username) username.value = data.data.username;
    if (email) email.value = data.data.email;
    if (userStatus) userStatus.value = data.data.status;
    if (expiration) expiration.value = data.data.expiration;

    openUserModal();

  } catch (err) {
    console.error(err);
    toast('获取用户详情失败', 'error');
  }
}

async function updateUser() {
  try {

    // 模拟填充表单数据
    const id = document.getElementById('user_id').value;
    const username = document.getElementById('username').value;
    const email = document.getElementById('email').value;
    const userStatus = document.getElementById('userStatus').value;
    const expiration = document.getElementById('expiration').value;

    const response = await fetch(`/system/xray-user/edit`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      credentials: 'include',
      body: `id=${id}&username=${username}&email=${email}&status=${userStatus}&expiration=${expiration}`
    });
    const data = await response.json();

    if (data.code !== 0) {
      toast(data.msg, 'error');
      return;
    }

    closeUserModal();
    searchUsers();

  } catch (err) {
    console.error(err);
    toast('修改用户失败', 'error');
  }
}


async function editDistributor(distributorId) {
  const response = await fetch(`/system/distributors/detail/${distributorId}`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    credentials: 'include',
  });
  const data = await response.json();

  if (data.code !== 0) {
    toast(data.msg, 'error');
    return;
  }

  const distributor = data.data;
  document.getElementById('distributors_id').value = distributor.id;
  document.getElementById('distributors_commissionRate').value = distributor.commissionRate;
  document.getElementById('distributors_firstChargeBonus').value = distributor.firstChargeBonus;
  openDistributorModal();
}

async function saveXrayDistributors() {
  const id = document.getElementById('distributors_id').value;
  const commissionRate = document.getElementById('distributors_commissionRate').value;
  const firstChargeBonus = document.getElementById('distributors_firstChargeBonus').value;

  if (!commissionRate) {
    toast('佣金比例不能为空!', 'error');
    return;
  }
  if (!firstChargeBonus) {
    toast('首充返佣不能为空!', 'error');
    return;
  }

  const response = await fetch(`/system/distributors/edit`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    credentials: 'include',
    body: JSON.stringify({
      id,
      commissionRate,
      firstChargeBonus
    }),
  });

  const data = await response.json();
  if (data.code === 0) {
    toast(`已保存分销商政策`, "success");
    searchDistributors();
  } else {
    toast(data.msg, 'error');
  }
}

function openDistributorModal() {
  const modal = document.getElementById('xrayDistributorsModal');
  if (modal) modal.style.display = 'flex';
}

function closeDistributorModal() {
  const modal = document.getElementById('xrayDistributorsModal');
  if (modal) {
    modal.style.display = 'none';
    document.getElementById('distributors_id').value = '';
    document.getElementById('distributors_commissionRate').value = '';
    document.getElementById('distributors_firstChargeBonus').value = '';
  }
}


function openUserModal() {
  const modal = document.getElementById('userModal');
  if (modal) modal.style.display = 'flex';
}

function openInnerUserModal() {
  const modal = document.getElementById('innerUserModal');
  if (modal) modal.style.display = 'flex';
}

function closeInnerUserModal() {
  const modal = document.getElementById('innerUserModal');
  if (modal) modal.style.display = 'none';
}

function closeUserModal() {
  const modal = document.getElementById('userModal');
  if (modal) modal.style.display = 'none';
}

let selectedCsvFile = null;

function handleFileSelect(event) {
  const file = event.target.files && event.target.files[0];
  if (file) {
    selectedCsvFile = file;
    parseCSV(file);
  }
}

function handleFileDrop(event) {
  event.preventDefault();
  const fileArea = event.currentTarget;
  if (fileArea) fileArea.classList.remove("drag-over");

  if (event.dataTransfer && event.dataTransfer.files.length > 0) {
    const file = event.dataTransfer.files[0];
    selectedCsvFile = file;
    const fileInput = document.querySelector("#csvFileInput");
    if (fileInput) {
      fileInput.files = event.dataTransfer.files;
    }
    parseCSV(file);
  }
}

function handleDragOver(event) {
  event.preventDefault();
  const fileArea = event.currentTarget;
  if (fileArea) fileArea.classList.add("drag-over");
}

function handleDragLeave(event) {
  event.preventDefault();
  const fileArea = event.currentTarget;
  if (fileArea) fileArea.classList.remove("drag-over");
}

function clearCsvFilter() {
  selectedCsvFile = null;
  const fileInput = document.querySelector("#csvFileInput");
  if (fileInput) fileInput.value = "";
  const csvTableBody2 = document.getElementById("csvTableBody2");
  if (csvTableBody2) csvTableBody2.innerHTML = "";
  const csvPreview2 = document.getElementById("csvPreview2");
  if (csvPreview2) csvPreview2.style.display = "none";
}

function parseCSV(file) {
  const reader = new FileReader();
  reader.onload = function (event) {
    const text = event.target.result;
    const rows = text.split(/\r?\n/).filter((r) => r.trim().length > 0);
    const csvTableBody2 = document.getElementById("csvTableBody2");
    const csvPreview2 = document.getElementById("csvPreview2");
    if (!csvTableBody2) return;

    csvTableBody2.innerHTML = "";
    let validCount = 0;
    rows.forEach((row) => {
      const cols = row.split(",");
      if (cols.length < 4) return;
      const tr = document.createElement("tr");
      cols.forEach((c) => {
        const td = document.createElement("td");
        td.textContent = c.trim();
        tr.appendChild(td);
      });
      csvTableBody2.appendChild(tr);
      validCount++;
    });
    if (csvPreview2) {
      csvPreview2.style.display = validCount > 0 ? "block" : "none";
    }
    toast(`解析完成，共 ${validCount} 条`, validCount > 0 ? "success" : "warning");
  };
  reader.onerror = function () {
    toast("读取文件失败，请重试", "error");
  };
  reader.readAsText(file, "utf-8");
}

async function importLines() {
  const csvTableBody2 = document.getElementById("csvTableBody2");
  const rows = csvTableBody2?.querySelectorAll("tr").length || 0;
  if (rows === 0) {
    toast("没有可导入的线路", "warning");
    return;
  }

  // 获取上传的文件对象（优先使用全局暂存文件或 fileInput 里的文件）
  const fileInput = document.querySelector("#csvFileInput");
  const file = selectedCsvFile || (fileInput && fileInput.files && fileInput.files[0]);

  if (!file) {
    toast("请先选择文件", "warning");
    return;
  }

  // 创建 FormData 对象，向后端发送文件
  const formData = new FormData();
  formData.append("file", file);

  // 向后端接口发送 POST 请求
  await fetch("/system/lines/import", {
    method: "POST",
    body: formData,
  }).then((response) => response.json())
    .then((data) => {
      if (data.code === 0 || data.code === 200) {
        toast(`已成功导入 ${rows} 条线路`, "success");
        if (typeof searchLines === 'function') {
          searchLines();
        }
        clearCsvFilter();
      } else {
        toast("导入失败：" + (data.msg || "请稍后再试"), "error");
      }
    })
    .catch((error) => {
      console.error(error);
      toast("网络错误，请重试", "error");
    });
}

window.handleFileSelect = handleFileSelect;
window.handleFileDrop = handleFileDrop;
window.handleDragOver = handleDragOver;
window.handleDragLeave = handleDragLeave;
window.clearCsvFilter = clearCsvFilter;
window.importLines = importLines;

async function editLine(obj, id) {
  const $this = $(obj);
  const pingOffset = $this.parent().prev().prev().prev().find("input").val();
  const status = $this.parent().prev().prev().find("select").val();
  // 向后端接口发送 POST 请求
  await fetch("/system/lines/edit", {
    method: "POST",
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    credentials: 'include',
    body: `id=${id}&pingOffset=${pingOffset}&status=${status}`,
  }).then((response) => response.json())
    .then((data) => {
      if (data.code === 0) {
        toast(`更新线路成功`, "success");
        searchLines();
      } else {
        toast("更新线路失败，请稍后再试", "error");
      }
    })
    .catch((error) => {
      toast("网络错误，请重试", "error");
    });
}

// ---------------- 活动与经销商设置 ----------------
function saveActivitySettings() {
  configId = Number(document.getElementById("activity-cfg-id").value || 0);
  participants = Number(document.getElementById("activity-participants").value || 0);
  adjust = Number(document.getElementById("adjust-participants").value || 0);

  fetch(`/system/activity/save-settings`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    credentials: 'include',
    body: `id=${configId}&participantsCnt=${participants}&moderatorsCnt=${adjust}`,
  }).then((response) => response.json())
    .then((data) => {
      if (data.code === 0) {
        toast(`保存成功`, "success");
        searchActivitySettings();
      } else {
        toast(data.msg, 'error');
      }
    })
}

async function searchActivitySettings() {
  const response = await fetch(`/system/activity/settings`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    credentials: 'include'
  });
  const data = await response.json();

  if (data.code !== 0) {
    toast(data.msg, 'error');
    return;
  }

  document.getElementById("activity-cfg-id").value = data.data.id;
  document.getElementById("activity-participants").value = data.data.participantsCnt;
  document.getElementById("adjust-participants").value = data.data.moderatorsCnt;

  const start = data.data.status === 1;
  const startActivityBtn = document.getElementById("startActivityBtn");
  document.getElementById("activity-participants").disabled = start;
  startActivityBtn.disabled = start;

  if (start) {
    startActivityBtn.style.opacity = '0.2';
    startActivityBtn.style.cursor = 'not-allowed';
  }
}

async function searchDistributorsSettings() {
  const response = await fetch(`/system/distributors/settings`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    credentials: 'include'
  });
  const data = await response.json();

  if (data.code !== 0) {
    toast(data.msg, 'error');
    return;
  }

  document.getElementById("dealer-commission").value = data.data.commissionRate;
  document.getElementById("dealer-discount").value = data.data.firstChargeBonus;

  window.distributorsConfig = data.data;
}

function addInnerUser() {
  openInnerUserModal();
}

function delInnerUser(id) {
  fetch(`/system/adminsettings/del-inner-user/${id}`, {
    method: 'DELETE',
    credentials: 'include',
  }).then((response) => response.json())
    .then((data) => {
      if (data.code === 0) {
        toast(`删除用户成功`, "success");
        searchInnerUsers();
      } else {
        toast(data.msg, 'error');
      }
    })
}

function disableInnerUser(id, status) {
  fetch(`/system/adminsettings/disable-inner-user/${id}`, {
    method: 'POST',
    credentials: 'include',
  }).then((response) => response.json())
    .then((data) => {
      if (data.code === 0) {
        toast(status == 0 ? `禁用用户成功` : `启用用户成功`, "success");
        searchInnerUsers();
      } else {
        toast(data.msg, 'error');
      }
    })
}

function saveInnerUser() {
  const username = document.getElementById("inner_username").value;
  const password = document.getElementById("inner_password").value;
  const role = document.getElementById("inner_type").value;

  if (!username) {
    toast("用户名不能为空", "warning");
    return;
  }

  if (!password) {
    toast("密码不能为空", "warning");
    return;
  }

  if (!role) {
    toast("请选择用户角色", "warning");
    return;
  }

  fetch(`/system/adminsettings/save-inner-user`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    credentials: 'include',
    body: JSON.stringify({
      userName: username,
      password: password,
      type: role
    }),
  }).then((response) => response.json())
    .then((data) => {
      if (data.code === 0) {
        toast(`添加用户成功`, "success");
        document.getElementById("inner_username").value = '';
        document.getElementById("inner_password").value = '';
        document.getElementById("inner_type").value = 'admin-slave';
        closeInnerUserModal();
        searchInnerUsers();
      } else {
        toast(data.msg, 'error');
      }
    })
}



function startActivity() {
  fetch(`/system/activity/startActivity`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    credentials: 'include',
  }).then((response) => response.json())
    .then((data) => {
      if (data.code === 0) {
        toast(`开启活动成功`, "success");
        searchActivitySettings();
      } else {
        toast(data.msg, 'error');
      }
    })
}

function saveDealerPolicy() {
  const commission = Number(document.getElementById("dealer-commission").value || 0);
  const discount = Number(document.getElementById("dealer-discount").value || 0);
  fetch(`/system/distributors/save-settings`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    credentials: 'include',
    body: `id=${window.distributorsConfig.id}&commissionRate=${commission}&firstChargeBonus=${discount}`,
  }).then((response) => response.json())
    .then((data) => {
      if (data.code === 0) {
        toast(`已保存经销商政策`, "success");
        searchDistributorsSettings();
      } else {
        toast(data.msg, 'error');
      }
    })
}
function resetDealerPolicy() {
  document.getElementById("dealer-commission").value = window.distributorsConfig?.commissionRate;
  document.getElementById("dealer-discount").value = window.distributorsConfig?.firstChargeBonus;
  toast("已重置经销商政策", "info");
}

// 添加支付接口模块
function showAddGatewayModal() {
  const modal = document.getElementById("addGatewayModal");
  if (modal) modal.style.display = "flex";
}
function closeAddGatewayModal() {
  const modal = document.getElementById("addGatewayModal");
  if (modal) modal.style.display = "none";
  const dyn = document.getElementById("dynamic-config-fields");
  if (dyn) dyn.innerHTML = "";
  const sel = document.getElementById("new-gateway-type");
  if (sel) sel.value = "";
}

function loadGatewayTemplate(type) {
  const dyn = document.getElementById("dynamic-config-fields");
  if (!dyn) return;
  const tpl = {
    custom: `
      <div class="form-group">
        <label class="form-label">自定义字段(JSON)</label>
        <textarea class="form-control" id="custom-json" rows="4" placeholder='{"key":"value"}'></textarea>
      </div>
    `,
    unionpay: `
      <div class="form-group"><label class="form-label">商户号</label><input class="form-control" id="unionpay-merchant-id" placeholder="UnionPay Merchant ID"></div>
      <div class="form-group"><label class="form-label">证书(pfx/pem)</label><input type="file" class="form-control" id="unionpay-cert" accept=".pfx,.pem"></div>
      <div class="form-group"><label class="form-label">证书密码</label><input type="password" class="form-control" id="unionpay-cert-pass" placeholder="证书密码"></div>
    `,
    applepay: `
      <div class="form-group"><label class="form-label">Merchant ID</label><input class="form-control" id="applepay-merchant-id" placeholder="merchant.com.xxx"></div>
      <div class="form-group"><label class="form-label">证书(cer)</label><input type="file" class="form-control" id="applepay-cert" accept=".cer"></div>
    `,
    googlepay: `
      <div class="form-group"><label class="form-label">商户ID</label><input class="form-control" id="gpay-merchant-id" placeholder="Google Pay Merchant ID"></div>
      <div class="form-group"><label class="form-label">商户名称</label><input class="form-control" id="gpay-merchant-name" placeholder="Merchant Name"></div>
    `,
    cryptocurrency: `
      <div class="form-group"><label class="form-label">币种</label>
        <select class="form-control" id="crypto-coin">
          <option value="USDT">USDT</option><option value="USDC">USDC</option><option value="BTC">BTC</option><option value="ETH">ETH</option>
        </select>
      </div>
      <div class="form-group"><label class="form-label">收款地址</label><input class="form-control" id="crypto-address" placeholder="钱包地址"></div>
    `,
  };
  dyn.innerHTML = tpl[type] || "";
}

// 新增：删除支付接口功能
function deleteGateway(name) {
  const builtInGateways = ['alipay', 'wechat', 'stripe', 'paypal', 'circle'];

  if (builtInGateways.includes(name)) {
    // 内置接口：弹窗确认后禁用
    if (confirm(`确定要删除 ${name} 支付接口吗？\n\n注意：内置接口删除后将被禁用，可通过重新配置恢复。`)) {
      // 隐藏配置表单
      const form = document.getElementById(`${name}-form`);
      if (form) {
        form.style.display = 'none';
      }

      // 设置状态为停用
      const statusSelect = document.getElementById(`${name}-status-select`);
      if (statusSelect) {
        statusSelect.value = 'suspended';
        updateGatewayStatus(name, 'suspended');
      }

      // 清空配置数据
      localStorage.removeItem(`gateway_${name}`);

      // 清空表单字段
      const inputs = document.querySelectorAll(`#${name}-form input, #${name}-form select, #${name}-form textarea`);
      inputs.forEach(input => {
        if (input.type === 'file') {
          input.value = '';
        } else if (input.tagName === 'SELECT') {
          input.selectedIndex = 0;
        } else {
          input.value = '';
        }
      });

      toast(`已删除 ${name} 接口配置`, 'warning');
    }
  } else {
    // 动态添加的接口：弹窗确认后完全移除
    if (confirm(`确定要删除此支付接口吗？\n\n此操作不可撤销，接口将被永久移除。`)) {
      const card = document.getElementById(`${name}Config`);
      if (card) {
        card.remove();
        // 清除本地存储
        localStorage.removeItem(`gateway_${name}`);
        toast('已删除支付接口', 'success');
      } else {
        toast('找不到要删除的接口', 'error');
      }
    }
  }
}

// 更新addNewGateway函数，为动态添加的接口也添加删除按钮
function updateGatewayStatus(name, value) {
  const span = document.getElementById(`${name}-status`);
  if (!span) return;
  const map = {
    enabled: { text: "启用", cls: "status-active" },
    disabled: { text: "未启用", cls: "status-pending" },
    suspended: { text: "停用", cls: "status-inactive" },
  };
  const info = map[value] || map.disabled;
  span.textContent = info.text;
  span.classList.remove("status-active", "status-pending", "status-inactive");
  span.classList.add(info.cls);
}

function addNewGateway() {
  const type = document.getElementById("new-gateway-type")?.value || "custom";
  const name = (document.getElementById("new-gateway-name")?.value || "").trim() || `${type}接口`;
  const desc = (document.getElementById("new-gateway-description")?.value || "").trim() || "自定义支付接口";
  const container = document.querySelector(".payment-gateway");
  if (!container) return;

  const slugBase = name.replace(/[^a-zA-Z0-9]+/g, "").toLowerCase() || type;
  const id = `${slugBase}-${Date.now()}`;

  const card = document.createElement("div");
  card.className = "gateway-card";
  card.id = `${id}Config`;
  card.innerHTML = `
    <div class="gateway-header">
      <div class="gateway-icon"><i class="fas fa-plug"></i></div>
      <div>
        <h4>${name} <span class="status status-pending" id="${id}-status">未启用</span></h4>
        <p style="color: var(--text-secondary); font-size: 12px;">${desc}</p>
      </div>
      <button class="btn btn-primary" style="margin-left: auto;" onclick="configureGateway('${id}')">
        <i class="fas fa-cog"></i> 配置
      </button>
    </div>
    <div id="${id}-form" style="display: none;">
      <div class="form-group">
        <label class="form-label" for="${id}-status-select">接口状态</label>
        <select class="form-control" id="${id}-status-select" onchange="updateGatewayStatus('${id}', this.value)">
          <option value="enabled">启用</option>
          <option value="disabled" selected>未启用</option>
          <option value="suspended">停用</option>
        </select>
      </div>
      ${document.getElementById("dynamic-config-fields")?.innerHTML || ""}
      <button class="btn btn-success" onclick="saveGatewayConfig('${id}')">
        <i class="fas fa-save"></i> 保存配置
      </button>
      <button class="btn btn-danger" onclick="deleteGateway('${id}')" style="margin-left: 10px;">
        <i class="fas fa-trash"></i> 删除接口
      </button>
    </div>
  `;
  container.appendChild(card);
  closeAddGatewayModal();
  toast("已添加新接口卡片", "success");
}

// 新增：经销商审批函数（adminsettings.html 上的审批按钮使用）
async function approveDealer(id, email) {
  const response = await fetch(`/system/distributors/approve`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    credentials: 'include',
    body: `id=${id}&status=approved`
  });
  const data = await response.json();

  if (data.code !== 0) {
    toast(data.msg, 'error');
    return;
  }
  toast(`已批准经销商：${email}`, 'success');
  searchDistributorsApply();
  searchDistributors();
}

async function rejectDealer(id, email) {
  const response = await fetch(`/system/distributors/approve`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    credentials: 'include',
    body: `id=${id}&status=rejected`
  });
  const data = await response.json();

  if (data.code !== 0) {
    toast(data.msg, 'error');
    return;
  }
  toast(`已拒绝经销商：${email}`, 'error');
  searchDistributorsApply();
  searchDistributors();
}

// 统一加载 Admin 页面模态框片段（只加载一次）
async function loadAdminModals() {
  try {
    if (document.getElementById('addGatewayModal') && document.getElementById('userModal')) {
      return; // 已加载
    }
    const res = await fetch('./partials/admin-modals.html', { cache: 'no-store' });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const html = await res.text();
    const mount = document.getElementById('modalMount') || document.body;
    const wrapper = document.createElement('div');
    wrapper.innerHTML = html;

    // 将片段的顶层子节点逐个移动到挂载点，避免多余层级
    while (wrapper.firstChild) {
      mount.appendChild(wrapper.firstChild);
    }
    console.log('[Admin] Modals loaded');
  } catch (err) {
    console.error('[Admin] Failed to load modals:', err);
    toast && toast('加载模态框失败，请刷新重试', 'error');
  }
}

// DOM 就绪后加载模态框
document.addEventListener('DOMContentLoaded', function () {
  loadAdminModals();
});

// 线路表-按“地区”字段排序（第2列），点击切换升/降序
function sortLinesByRegion() {
  const table = document.getElementById('linesTable');
  if (!table) return;
  const tbody = table.tBodies[0];
  if (!tbody) return;

  const rows = Array.from(tbody.rows);
  const dir = table.dataset.sortDir === 'asc' ? 'desc' : 'asc';

  rows.sort((a, b) => {
    const av = (a.cells[1]?.textContent || '').trim().toLowerCase();
    const bv = (b.cells[1]?.textContent || '').trim().toLowerCase();
    if (av < bv) return dir === 'asc' ? -1 : 1;
    if (av > bv) return dir === 'asc' ? 1 : -1;
    return 0;
  });

  rows.forEach(r => tbody.appendChild(r));
  table.dataset.sortDir = dir;
}

window.AdminSettings = {
  showMainTab,
  configureGateway,
  saveGatewayConfig,
  handleFileSelect,
  handleDragOver,
  handleDragLeave,
  handleFileDrop,
  parseCSV,
  importLines,
  saveActivitySettings,
  saveDealerPolicy,
  resetDealerPolicy,
  updateGatewayStatus,
  showAddGatewayModal,
  closeAddGatewayModal,
  loadGatewayTemplate,
  deleteGateway,
  addNewGateway,
  approveDealer,
  rejectDealer,
  // 新增：权限管理
  initAccessTab,
  savePermissions,
  resetPermissions,
};


// 统一加载 Admin 页面模态框片段（只加载一次）
async function loadAdminModals() {
  try {
    if (document.getElementById('addGatewayModal') && document.getElementById('userModal')) {
      return; // 已加载
    }
    const res = await fetch('/system/adminsettings/partials', { cache: 'no-store' });
    if (!res.ok) throw new Error(`HTTP ${res.status}`);
    const html = await res.text();
    const mount = document.getElementById('modalMount') || document.body;
    const wrapper = document.createElement('div');
    wrapper.innerHTML = html;

    // 将片段的顶层子节点逐个移动到挂载点，避免多余层级
    while (wrapper.firstChild) {
      mount.appendChild(wrapper.firstChild);
    }
    console.log('[Admin] Modals loaded');
  } catch (err) {
    console.error('[Admin] Failed to load modals:', err);
    toast && toast('加载模态框失败，请刷新重试', 'error');
  }
}

// DOM 就绪后加载模态框
document.addEventListener('DOMContentLoaded', function () {
  loadAdminModals();
});
