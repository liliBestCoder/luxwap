
  // 名次枚举值与表格中文展示的映射
  const RANK_MAP = {
    "1": '第一名',
    "2": '第二名',
    "3": '第三名',
    "4": '参与奖',
    "-1": '无效链接',
  };

  menusTab = ["promotion", "promotion-audit"];
  menusTab.forEach(menuTab => {
    const paging = window[`${menuTab}-paging`] = {
                totalItems: 0,
                pageSize: 2,
                pageNumber: 1,
    };
    $(`#pagination-container-${menuTab}`).pagination({
      items: paging.totalItems,
      itemsOnPage: paging.pageSize,
      displayedPages: 3,
      edges: 1,
      prevText: '上一页',
      nextText: '下一页',
      cssStyle: 'light-theme', // 可以自定义成你要的样式
      onPageClick: function(clickedPageNumber) {
        paging.pageNumber = clickedPageNumber;
        if(menuTab === 'promotion'){
          searchPromotions();
        }else if(menuTab === 'promotion-audit'){
          searchPromotionsAudit();
        }
      }
    });
  });

  // 安全 Toast 方法（若 common.js 未加载，则使用 alert 兜底）
  function safeToast(message, type = 'info') {
    if (typeof window.showToast === 'function') {
      window.showToast(message, type);
    } else {
      alert(message);
    }
  }

  function formatDateToBackend(date) {
    if (!date) return '';
    const pad = n => n.toString().padStart(2, '0');
    return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())} ` +
        `${pad(date.getHours())}:${pad(date.getMinutes())}:${pad(date.getSeconds())}`;
  }

  function parseDateToStartOfDay(value) {
    if (!value) return null;
    const d = new Date(value);
    return isNaN(d.getTime()) ? null : new Date(d.getFullYear(), d.getMonth(), d.getDate(), 0, 0, 0, 0);
  }

  function parseDateToEndOfDay(value) {
    if (!value) return null;
    const d = new Date(value);
    return isNaN(d.getTime()) ? null : new Date(d.getFullYear(), d.getMonth(), d.getDate(), 23, 59, 59, 999);
  }

  // 更新概览卡片的4个统计数
  async function updateOverviewMetrics() {
    const response = await fetch(`/system/activity/stats`, {
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

    let total = data.data.totalCount || 0;
    let countNo1 = data.data.firstPlaceCount || 0;
    let countNo2 = data.data.secondPlaceCount || 0;
    let countInvalid = data.data.invalidLinkCount || 0;

    const setMetric = (selector, value) => {
      const el = document.querySelector(selector);
      if (el) el.textContent = String(value);
    };

    // 对齐 HTML 中卡片的顺序和含义
    setMetric('#pendingPromotionsCard .overview-metric', countNo1); // 第一名人数
    setMetric('#approvedPromotionsCard .overview-metric', countNo2); // 第二名人数
    setMetric('#rejectedPromotionsCard .overview-metric', countInvalid); // 无效人数
    setMetric('#monthlyPromotionsCard .overview-metric', total); // 已参加人数（当前可见）
  }

  // 按筛选条件过滤“参与活动用户”表格
  async function searchPromotions() {
    const nameInput = document.getElementById('promotionName');
    const rankSelect = document.getElementById('rankingSellect');
    const belongSelect = document.getElementById('belongToStatus');
    const startDateInput = document.getElementById('promotionStartDate');
    const endDateInput = document.getElementById('promotionEndDate');

    const nameKeyword = (nameInput?.value || '').trim().toLowerCase();
    const rankValue = rankSelect?.value || ''; // 取值如 NO1/NO2/invalidPromotionLink
    const belongValue = belongSelect?.value; // 取值 belongToInner/belongToOuter
    const startDate = formatDateToBackend(parseDateToStartOfDay(startDateInput?.value)) || '';
    const endDate = formatDateToBackend(parseDateToEndOfDay(endDateInput?.value)) || '';

    const paging = window["promotion-audit-paging"];

    const response = await fetch(`/system/activity/participant-list`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      credentials: 'include',
      body:`userName=${nameKeyword}&rank=${rankValue}&type=${belongValue}&startDate=${startDate}&endDate=${endDate}&pageSize=${paging.pageSize}&pageNum=${paging.pageNumber}&isAsc=asc`
    });
    const data = await response.json();

    if(data.code !== 0){
      toast(data.msg, 'error');
      return;
    }

    const dealerTableBody = document.getElementById('dealerTableBody');
    // 模拟搜索结果过滤
    if (!dealerTableBody) return;

    dealerTableBody.innerHTML = (data.rows || []).map(l => `<tr>
              <td>#${l.userId}</td>
              <td>${l.registrationTime ? l.registrationTime.substring(0,10) : ''}</td>
              <td>${l.userEmail}</td>
              <td>${l.userName}</td>
              <td>${l.member === 0 ? "否" : "是"}</td>
              <td>${l.expiration ? l.expiration.substring(0,10) : ''}</td>
              <td>${RANK_MAP[l.rank]}</td>
              <td><span class="belongto ${l.type === 'inner' ? 'belongto-inner' : 'belongto-outer'}">
          ${l.type === 'inner' ? '内部' : '分销商'}
            </tr>`).join('');

    paging.totalItems = data.total;
    let start = (paging.pageNumber - 1) * paging.pageSize + 1;
    let end = Math.min(start + paging.pageSize - 1, paging.totalItems);
    document.getElementById(`pagination-info-promotion`).innerText =
        `显示 ${start}-${end} 条，共 ${paging.totalItems} 条记录`;

    $(`#pagination-container-promotion`).pagination('updateItems', paging.totalItems);

  }


  async function searchPromotionsAudit() {
    const paging = window["promotion-audit-paging"];

    const response = await fetch(`/system/activity/participant-list`, {
      method: 'POST',
      headers: {
        'Content-Type': 'application/x-www-form-urlencoded',
      },
      credentials: 'include',
      body:`rank=0&pageSize=${paging.pageSize}&pageNum=${paging.pageNumber}&isAsc=asc`
    });
    const data = await response.json();

    if(data.code !== 0){
      toast(data.msg, 'error');
      return;
    }

    const tbody = document.getElementById('dealerAuditTableBody');
    // 模拟搜索结果过滤
    if (!tbody) return;

    window.rank = async function rank(id, obj) {
      const rankVal = $(obj).prev().val();
      if (!rankVal) {
        safeToast('请选择一个名次后再确认', 'warning');
        return;
      }

      const response = await fetch(`/system/activity/rank`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded',
        },
        credentials: 'include',
        body:`participantsId=${id}&rank=${rankVal}`
      });

      const data = await response.json();

      if(data.code !== 0){
        toast(data.msg, 'error');
        return;
      }

      searchPromotionsAudit();
      searchPromotions();
    }

    tbody.innerHTML = (data.rows || []).map(l => `<tr>
              <td>${l.createdAt ? l.createdAt.substring(0,10) : ''}</td>
              <td>#${l.userId}</td>
              <td>${l.registrationTime ? l.registrationTime.substring(0,10) : ''}</td>
              <td>${l.userEmail}</td>
              <td>${l.userName}</td>
              <td><a href="${l.auditLink}" target="_blank">查看验证链接</a></td>
              <td>${l.member === 0 ? "否" : "是"}</td>
              <td><span class="belongto ${l.type === 'inner' ? 'belongto-inner' : 'belongto-outer'}">
          ${l.type === 'inner' ? '内部' : '分销商'}
              <td><select name="rankingSellect" class="form-control-rankeselect" id="rankingSellect2">
                <option value="">排名</option>
                <option value="1">第一名</option>
                <option value="2">第二名</option>
                <option value="3">第三名</option>
                <option value="4">参与奖</option>
                <option value="-1">无效链接</option>
              </select>
              <button class="btn btn-primary" onclick="rank(${l.id}, this)" style="margin-left: 10px;"> <i class="fas fa-check"></i> 确认 </button>
            </td>
            </tr>`).join('');

    paging.totalItems = data.total;
    let start = (paging.pageNumber - 1) * paging.pageSize + 1;
    let end = Math.min(start + paging.pageSize - 1, paging.totalItems);
    document.getElementById(`pagination-info-promotion-audit`).innerText =
        `显示 ${start}-${end} 条，共 ${paging.totalItems} 条记录`;

    $(`#pagination-container-promotion-audit`).pagination('updateItems', paging.totalItems);
  }

  // 清空筛选项并恢复表格
  function clearPromotionFilter() {
    const nameInput = document.getElementById('promotionName');
    const rankSelect = document.getElementById('rankingSellect');
    const belongSelect = document.getElementById('belongToStatus');
    const startDateInput = document.getElementById('promotionStartDate');
    const endDateInput = document.getElementById('promotionEndDate');

    if (nameInput) nameInput.value = '';
    if (rankSelect) rankSelect.value = '';
    if (belongSelect) belongSelect.value = '';
    if (startDateInput) startDateInput.value = '';
    if (endDateInput) endDateInput.value = '';

    const rows = document.querySelectorAll('#usersTableBody tr');
    rows.forEach((row) => (row.style.display = ''));

    updateOverviewMetrics();
  }

  // 绑定筛选输入事件（回车/变更自动筛选）
  function bindFilterEvents() {
    const nameInput = document.getElementById('promotionName');
    if (nameInput) {
      nameInput.addEventListener('keypress', (e) => {
        if (e.key === 'Enter') searchPromotions();
      });
    }
  }

  document.addEventListener('DOMContentLoaded', function () {
    bindFilterEvents();
    // 初始统计一次
    searchPromotionsAudit();
    searchPromotions();
    updateOverviewMetrics();
  });

  // 暴露给 HTML 的按钮
  window.searchPromotions = searchPromotions;
  window.clearPromotionFilter = clearPromotionFilter;