// 线路模块：查看详情、编辑、删除、模态框控制等（导出功能已迁移到 common.js）
window.menu = 'lines';

(function initLinesPage() {
  document.addEventListener('DOMContentLoaded', () => {
    searchLines();
    updateOverviewMetrics();
    initLineFormSubmit();
    initLineModalOutsideClick();
  });
})();


async function updateOverviewMetrics() {
  // const rows = Array.from(document.querySelectorAll('#usersTableBody tr')).filter(
  //   (tr) => tr.style.display !== 'none'
  // );

  const response = await fetch(`/system/lines/stats`, {
    method: 'GET',
    headers: {
      'Content-Type': 'application/x-www-form-urlencoded',
    },
    credentials: 'include',
  });
  const data = await response.json();

  if(data.code !== 0){
    toast(data.msg, 'error');
    return;
  }

  let onlineCnt = data.data.online | 0;
  let maintenanceCnt = data.data.maintenance | 0;
  let offlineCnt = data.data.offline | 0;
  let errorCnt = data.data.error | 0;

  const setMetric = (selector, value) => {
    const el = document.querySelector(selector);
    if (el) el.textContent = String(value);
  };

  // 对齐 HTML 中卡片的顺序和含义
  setMetric('#card-total-lines .overview-metric', onlineCnt + maintenanceCnt + offlineCnt + errorCnt); // 第一名人数
  setMetric('#card-maintenance-lines .overview-metric', maintenanceCnt); // 第二名人数
  setMetric('#card-online-lines .overview-metric', onlineCnt); // 无效人数
  setMetric('#card-fault-lines .overview-metric', errorCnt); // 已参加人数（当前可见）
}

// async function fetchLines() {
//   try {
//     const data = await apiRequest('/api/lines');
//     const tbody = document.getElementById('linesTableBody');
//     if (!tbody) return;
//     tbody.innerHTML = (data || []).map(l => `
//       <tr>
//         <td>${l.id}</td>
//         <td>${l.region || ''}</td>
//         <td>${l.name || ''}</td>
//         <td>${l.type || ''}</td>
//         <td>${l.bandwidth || ''}</td>
//         <td>${l.total_traffic ?? ''}</td>
//         <td>${l.remain_traffic ?? ''}</td>
//         <td>${l.latency ?? ''}</td>
//         <td><span class="status ${l.status === 'online' ? 'status-active' : (l.status === 'maintenance' ? 'status-pending' : (l.status === 'error' ? 'status-inactive' : 'status-secondary'))}">${l.status || ''}</span></td>
//         <td>
//           <button class="btn btn-secondary" style="padding: 5px 10px; margin-right: 5px;" onclick="viewLine('${l.id}')"><i class="fas fa-eye"></i></button>
//         </td>
//       </tr>
//     `).join('');
//   } catch (err) {
//     console.error(err);
//     toast('获取线路列表失败', 'error');
//   }
// }

function openLineModal() {
  const modal = document.getElementById('lineModal');
  if (modal) modal.style.display = 'flex';
}

function closeLineModal() {
  const modal = document.getElementById('lineModal');
  if (modal) modal.style.display = 'none';
}

function addLine() {
  const titleEl = document.getElementById('lineModalTitle');
  const form = document.getElementById('lineForm');
  if (titleEl) titleEl.textContent = '添加线路';
  if (form) form.reset();
  openLineModal();
}

// 表单提交：创建线路
function initLineFormSubmit() {
  const form = document.getElementById('lineForm');
  if (!form) return;
  form.addEventListener('submit', async function(e) {
    e.preventDefault();
    const payload = {
      id: document.getElementById('lineId')?.value?.trim(),
      region: document.getElementById('lineRegionInput')?.value?.trim() || null,
      name: document.getElementById('lineNameInput')?.value?.trim(),
      type: document.getElementById('lineTypeInput')?.value?.trim() || null,
      ip: document.getElementById('lineIp')?.value?.trim() || null,
      bandwidth: document.getElementById('lineBandwidth')?.value?.trim() || null,
      total_traffic: document.getElementById('lineTotal')?.value ? Number(document.getElementById('lineTotal').value) : 0,
      remain_traffic: document.getElementById('lineRemain')?.value ? Number(document.getElementById('lineRemain').value) : 0,
      latency: document.getElementById('lineLatency')?.value?.trim() || null,
      offset: document.getElementById('lineOffset')?.value?.trim() || null,
      status: document.getElementById('lineStatus')?.value || 'online',
    };
    try {
      await apiRequest('/api/lines', { method: 'POST', body: payload });
      toast('线路保存成功', 'success');
      closeLineModal();
      fetchLines();
    } catch (err) {
      console.error(err);
      toast('保存失败：' + err.message, 'error');
    }
  });
}

/**
 * 查看线路详情（保留原能力，若需要可改造为从 API 获取）
 * @param {string} lineId - 线路ID
 */
function viewLine(lineId) {
  toast(`查看线路 ${lineId}`, 'info');
}

// 点击模态框外部关闭
function initLineModalOutsideClick() {
  const modal = document.getElementById('lineModal');
  if (!modal) return;
  modal.addEventListener('click', function(e) {
    if (e.target === this) {
      closeLineModal();
    }
  });
}

// 暴露到全局（便于 HTML onclick 调用）
window.addLine = addLine;
window.viewLine = viewLine;
window.closeLineModal = closeLineModal;