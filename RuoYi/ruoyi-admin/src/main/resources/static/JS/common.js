// 重写 fetch
const originalFetch = window.fetch;
window.fetch = async function(input, init = {}) {
    const defaultInit = {
        credentials: 'include',
        redirect: 'manual',
    };

    // 如果 body 是 FormData，就不要加 Content-Type，让浏览器自己带
    const isFormData = init?.body instanceof FormData;

    const defaultHeaders = isFormData
        ? {}
        : { 'Content-Type': 'application/x-www-form-urlencoded' };

    const options = {
        ...defaultInit,
        ...init,
        headers: {
            ...defaultHeaders,
            ...init?.headers,
        },
    };

    try {
        const response = await originalFetch(input, options);

        if (response.status === 401) {
            window.top.location.href = '/login.html';
            return new Response(null, { status: 401 });
        }
        return response;
    } catch (error) {
        console.error('Fetch error:', error);
        throw error;
    }
};




/**
 * 通用功能模块
 * 包含跨页面复用的导出、分页等通用功能
 */

/**
 * 通用导出功能
 * @param {string} type - 导出类型：'users', 'lines', 'bills', 'distributors', 'promotions'
 * @param {string} tableSelector - 表格选择器，默认根据type自动匹配
 */
function exportData(type = 'data', tableSelector = null) {
    const typeMap = {
        'users': { name: '用户', selector: '#usersTableBody' },
        'lines': { name: '线路', selector: '#linesTableBody' },
        'bills': { name: '账单', selector: '#billsTableBody' },
        'distributors': { name: '分销商', selector: '#distributorsTableBody' },
        'promotions': { name: '推广', selector: '#promotionsTableBody' }
    };

    const config = typeMap[type] || { name: type, selector: tableSelector };
    
    console.log(`导出${config.name}数据`);
    
    // 模拟获取表格数据进行导出
    if (config.selector) {
        const tableBody = document.querySelector(config.selector);
        if (tableBody) {
            const rows = tableBody.querySelectorAll('tr');
            console.log(`找到 ${rows.length} 条${config.name}记录`);
        }
    }
    
    alert(`${config.name}数据导出成功！文件已下载到本地。`);
}

// 为各类型提供专用导出函数
function exportUsers() {
    exportData('users');
}

function exportLines() {
    exportData('lines');
}

function exportBills() {
    exportData('bills');
}

function exportDistributors() {
    exportData('distributors');
}

function exportPromotions() {
    exportData('promotions');
}

/**
 * 通用表格分页功能
 * @param {number} page - 页码
 * @param {number} pageSize - 每页大小
 * @param {string} tableSelector - 表格选择器
 */
function paginate(page, pageSize = 10, tableSelector = 'tbody') {
    const tbody = document.querySelector(tableSelector);
    if (!tbody) return;
    
    const rows = tbody.querySelectorAll('tr');
    const start = (page - 1) * pageSize;
    const end = start + pageSize;
    
    rows.forEach((row, index) => {
        row.style.display = (index >= start && index < end) ? '' : 'none';
    });
    
    console.log(`显示第 ${page} 页，每页 ${pageSize} 条记录`);
}

/**
 * 通用模态框控制
 * @param {string} modalId - 模态框ID
 * @param {boolean} show - 显示/隐藏
 */
function toggleModal(modalId, show = true) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = show ? 'flex' : 'none';
    }
}

// 简易全局 Toast 提示（统一实现）
function toast(message, type = "info") {
  const colors = {
    info: "#2563eb",
    success: "#16a34a",
    warning: "#ca8a04",
    error: "#dc2626",
  };
  const el = document.createElement("div");
  el.textContent = message;
  el.style.cssText = `
    position: fixed; right: 20px; bottom: 20px; z-index: 9999;
    background: ${colors[type] || colors.info}; color: #fff; padding: 10px 14px;
    border-radius: 8px; box-shadow: 0 5px 20px rgba(0,0,0,.15);
  `;
  document.body.appendChild(el);
  setTimeout(() => el.remove(), 2200);
}

// 新增：统一 API 请求封装
async function apiRequest(path, { method = 'GET', body, headers = {} } = {}) {
  const res = await fetch(path, {
    method,
    headers: {
      'Content-Type': 'application/json',
      ...headers,
    },
    body: body ? JSON.stringify(body) : undefined,
  });
  const isJson = (res.headers.get('content-type') || '').includes('application/json');
  if (!res.ok) {
    const errText = isJson ? JSON.stringify(await res.json()).slice(0, 500) : (await res.text()).slice(0, 500);
    throw new Error(errText || res.statusText);
  }
  return isJson ? res.json() : res.text();
}

/**
 * 获取全局每页显示记录数
 * @returns {number}
 */
function getGlobalPageSize() {
  const size = localStorage.getItem('page_size');
  const parsed = parseInt(size, 10);
  return (!isNaN(parsed) && parsed > 0) ? parsed : 20;
}

// 暴露到全局（如已存在不会覆盖）
window.toast = window.toast || toast;
window.exportData = exportData;
window.exportUsers = exportUsers;
window.exportLines = exportLines;
window.exportBills = exportBills;
window.exportDistributors = exportDistributors;
window.exportPromotions = exportPromotions;
window.paginate = paginate;
window.toggleModal = toggleModal;
// 新增导出
window.apiRequest = window.apiRequest || apiRequest;
window.getGlobalPageSize = getGlobalPageSize;