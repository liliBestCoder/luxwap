// ---------------- 权限管理（角色与模块权限） ----------------
// 角色与模块定义
const ROLES = [
  { value: "support", label: "客服" },
  { value: "reseller", label: "分销商" },
  { value: "admin-slave", label: "管理员" },
  { value: "admin", label: "超级管理员" },
];

const ROLES_KEY_MAPPING = {
  "support": "客服",
  "reseller":"分销商",
  "admin-slave":"管理员",
  "admin":"超级管理员"
};

const MODULE_KEYS = [ {
  "key" : "system:xray-user:view",
  "label" : "用户信息"
}, {
  "key" : "system:distributors:view",
  "label" : "分销商信息"
}, {
  "key" : "system:lines:view",
  "label" : "线路信息"
}, {
  "key" : "system:bills:view",
  "label" : "账单"
}, {
  "key" : "system:activity:view",
  "label" : "活动管理"
}, {
  "key" : "system:adminsettings:view",
  "label" : "管理员配置"
}, {
  "key" : "system:adminsettings:payment",
  "label" : "支付配置"
}, {
  "key" : "system:adminsettings:users",
  "label" : "用户管理"
}, {
  "key" : "system:adminsettings:lines",
  "label" : "线路管理"
}, {
  "key" : "system:adminsettings:board",
  "label" : "财务报表看板"
}, {
  "key" : "system:adminsettings:activity",
  "label" : "活动设置"
}, {
  "key" : "system:adminsettings:distributors",
  "label" : "经销商政策"
}, {
  "key" : "system:adminsettings:perms",
  "label" : "权限管理"
}];


// 初始化入口（加载角色权限并渲染表格）
function initAccessTab() {
  renderAccessTable();
}

// 渲染“角色”的表格：（角色、模块权限）
function renderAccessTable() {
  const tbody = document.getElementById("accessUsersTableBody");
  if (!tbody) return;
  tbody.innerHTML = "";

  ROLES.forEach(r => {
    const tr = document.createElement("tr");

    // 角色名
    const tdRole = document.createElement("td");
    tdRole.textContent = r.label;

    // 模块权限（复选）
    const tdPerms = document.createElement("td");
    const permWrap = document.createElement("div");
    permWrap.style.display = "flex";
    permWrap.style.flexWrap = "wrap";
    permWrap.style.gap = "8px";
    permWrap.id = `perms__${r.value}`;

    MODULE_KEYS.forEach(m => {
      const checkboxId = `perm__${m.key}__${r.value}`;
      const label = document.createElement("label");
      label.setAttribute("for", checkboxId);
      label.style.display = "inline-flex";
      label.style.alignItems = "center";
      label.style.gap = "6px";

      const cb = document.createElement("input");
      cb.type = "checkbox";
      cb.id = checkboxId;
      cb.checked = !!(rolePerms[r.value] && rolePerms[r.value][m.key]);

      const span = document.createElement("span");
      span.textContent = m.label;

      label.appendChild(cb);
      label.appendChild(span);
      permWrap.appendChild(label);
    });

    tdPerms.appendChild(permWrap);

    tr.appendChild(tdRole);
    tr.appendChild(tdPerms);

    tbody.appendChild(tr);
  });
}

// 保存（按角色保存到 role_perms）
async function savePermissions() {
  const savePermissionReq = [];
  ROLES.forEach(r => {
    const checkList = $('#perms__' + r.value).find("input[type='checkbox']");
    const rolePermissions = [];
    checkList.each(function() {
      if (this.checked) {
        // 从 checkbox 的 id 中提取 key 部分
        const checkboxId = this.id;
        // id 格式为: perm-${m.key}-${r.value}
        // 需要提取中间的 key 部分
        const parts = checkboxId.split('__');
        // 移除第一部分 'perm' 和最后一部分角色名
        const key = parts[1];
        rolePermissions.push(key);
      }
    });
    savePermissionReq.push({
      roleKey: r.value,
      perms: rolePermissions.join(",")
    });
  });

  const response = await fetch(`/system/adminsettings/save-perms`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    credentials: 'include',
    body:JSON.stringify(savePermissionReq)
  });

  // if(response.status !== 200){
  //   toast(response.message, 'error');
  //   return;
  // }

  const data = await response.json();
  if(data.code !== 0){
    toast(data.msg, 'error');
    return;
  }

  toast("权限保存成功!", 'success');
}

// 重置（清空存储并按默认模板还原）
function resetPermissions() {
   renderAccessTable();
}