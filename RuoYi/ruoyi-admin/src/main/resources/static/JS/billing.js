// 账单管理模块：动态对接后端 API、分页、图表展示与详情弹窗

let currentPage = 1;
const pageSize = 10;
let totalRecords = 0;
let currentTrendPeriod = 'month';

// 页面加载完成后初始化
document.addEventListener('DOMContentLoaded', function () {
    loadBills(1);
    loadStats(currentTrendPeriod);

    // 绑定回车搜索事件
    const billNumberInput = document.getElementById('billNumber');
    const customerNameInput = document.getElementById('customerName');
    if (billNumberInput) {
        billNumberInput.addEventListener('keypress', function (e) {
            if (e.key === 'Enter') searchBills();
        });
    }
    if (customerNameInput) {
        customerNameInput.addEventListener('keypress', function (e) {
            if (e.key === 'Enter') searchBills();
        });
    }
});

// 加载账单列表
async function loadBills(page = 1) {
    currentPage = page;
    const tbody = document.getElementById('billsTableBody');
    if (tbody) {
        tbody.innerHTML = `
            <tr>
                <td colspan="9" style="text-align: center; padding: 40px; color: var(--text-secondary);">
                    <i class="fas fa-spinner fa-spin" style="margin-right: 8px;"></i> 正在加载账单数据...
                </td>
            </tr>
        `;
    }

    const billNumber = (document.getElementById('billNumber')?.value || '').trim();
    const customerName = (document.getElementById('customerName')?.value || '').trim();
    const isFirstCharge = document.getElementById('billStatus')?.value || '';
    const startDate = document.getElementById('startDate')?.value || '';
    const endDate = document.getElementById('endDate')?.value || '';

    const params = new URLSearchParams();
    params.append('pageNum', currentPage);
    params.append('pageSize', pageSize);
    if (billNumber) params.append('orderNo', billNumber);
    if (customerName) params.append('customerName', customerName);
    if (isFirstCharge) params.append('isFirstCharge', isFirstCharge);
    if (startDate) params.append('startDate', startDate);
    if (endDate) params.append('endDate', endDate);

    try {
        const response = await fetch('/system/bill/list', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded'
            },
            body: params.toString()
        });

        const data = await response.json();
        totalRecords = data.total || 0;
        const rows = data.rows || [];

        renderTable(rows);
        renderPagination();
    } catch (err) {
        console.error('加载账单失败:', err);
        if (tbody) {
            tbody.innerHTML = `
                <tr>
                    <td colspan="9" style="text-align: center; padding: 40px; color: var(--danger-color, #ff4d4f);">
                        <i class="fas fa-exclamation-circle" style="margin-right: 8px;"></i> 加载账单数据失败，请重试
                    </td>
                </tr>
            `;
        }
    }
}

// 渲染表格
function renderTable(rows) {
    const tbody = document.getElementById('billsTableBody');
    if (!tbody) return;

    if (!rows || rows.length === 0) {
        tbody.innerHTML = `
            <tr>
                <td colspan="9" style="text-align: center; padding: 40px; color: var(--text-secondary);">
                    <i class="fas fa-inbox" style="font-size: 24px; display: block; margin-bottom: 8px; opacity: 0.5;"></i>
                    暂无账单记录
                </td>
            </tr>
        `;
        return;
    }

    tbody.innerHTML = rows.map(r => `
        <tr>
            <td>${r.paymentDateShort || r.paymentDate || '-'}</td>
            <td><code style="font-weight: 600; color: var(--primary-color, #1890ff);">${r.orderNo}</code></td>
            <td>${r.customerName || '-'}</td>
            <td style="font-weight: 600;">${r.amount}</td>
            <td>${r.validPeriod || '-'}</td>
            <td>
                <span class="badge" style="padding: 2px 8px; border-radius: 4px; font-size: 12px; ${r.firstCharge === '是' ? 'background: #e6f7ff; color: #1890ff;' : 'background: #f5f5f5; color: #8c8c8c;'}">
                    ${r.firstCharge}
                </span>
            </td>
            <td><span style="color: var(--warning-color, #faad14); font-weight: 500;">${r.commission}</span></td>
            <td><span class="belongto ${r.belongClass}">${r.belong}</span></td>
            <td>
                <button class="btn btn-secondary" style="padding: 3px 10px; font-size: 12px;" onclick="viewBill('${r.orderNo}')">
                    <i class="fas fa-eye"></i> 详情
                </button>
            </td>
        </tr>
    `).join('');
}

// 渲染分页控件
function renderPagination() {
    const info = document.getElementById('pagination-info');
    const prevBtn = document.getElementById('btn-prev-page');
    const nextBtn = document.getElementById('btn-next-page');
    const display = document.getElementById('page-display');

    const totalPages = Math.max(1, Math.ceil(totalRecords / pageSize));
    const startRow = totalRecords === 0 ? 0 : (currentPage - 1) * pageSize + 1;
    const endRow = Math.min(currentPage * pageSize, totalRecords);

    if (info) {
        info.textContent = `显示 ${startRow}-${endRow} 条，共 ${totalRecords} 条记录`;
    }
    if (display) {
        display.textContent = `第 ${currentPage} / ${totalPages} 页`;
    }
    if (prevBtn) {
        prevBtn.disabled = currentPage <= 1;
    }
    if (nextBtn) {
        nextBtn.disabled = currentPage >= totalPages;
    }
}

function prevPage() {
    if (currentPage > 1) {
        loadBills(currentPage - 1);
    }
}

function nextPage() {
    const totalPages = Math.ceil(totalRecords / pageSize);
    if (currentPage < totalPages) {
        loadBills(currentPage + 1);
    }
}

// 搜索账单
function searchBills() {
    loadBills(1);
}

// 重置筛选
function clearBillFilter() {
    const ids = ['billNumber', 'customerName', 'billStatus', 'startDate', 'endDate'];
    ids.forEach(id => {
        const el = document.getElementById(id);
        if (el) el.value = '';
    });
    loadBills(1);
}

// 切换趋势周期
function switchTrendPeriod(period, btn) {
    currentTrendPeriod = period;
    document.querySelectorAll('.btn-period').forEach(b => {
        b.classList.remove('btn-primary');
        b.classList.add('btn-secondary');
    });
    if (btn) {
        btn.classList.remove('btn-secondary');
        btn.classList.add('btn-primary');
    }
    loadStats(period);
}

// 切换分布周期
function switchSourcePeriod(period, btn) {
    document.querySelectorAll('.btn-source-period').forEach(b => {
        b.classList.remove('btn-primary');
        b.classList.add('btn-secondary');
    });
    if (btn) {
        btn.classList.remove('btn-secondary');
        btn.classList.add('btn-primary');
    }
    loadStats(currentTrendPeriod);
}

// 加载图表统计数据
async function loadStats(period = 'month') {
    const trendContainer = document.getElementById('chart-trend-container');
    const sourceContainer = document.getElementById('chart-source-container');

    try {
        const res = await fetch(`/system/bill/stats?period=${period}`);
        const result = await res.json();
        if (result.code !== 0 && result.code !== 200) return;

        const data = result.data || {};
        const categories = data.trendCategories || [];
        const values = data.trendValues || [];

        // 渲染柱状图
        if (trendContainer) {
            if (categories.length === 0) {
                trendContainer.innerHTML = `<div style="text-align: center; color: var(--text-secondary); width: 100%;">暂无营收统计数据</div>`;
            } else {
                const maxVal = Math.max(...values, 10);
                const barsHtml = categories.map((cat, idx) => {
                    const val = values[idx] || 0;
                    const percent = Math.min(100, Math.round((val / maxVal) * 85));
                    return `
                        <div style="flex: 1; display: flex; flex-direction: column; align-items: center; justify-content: flex-end; height: 100%; gap: 6px;">
                            <div style="font-size: 11px; font-weight: 600; color: var(--text-primary);">$${val}</div>
                            <div style="width: 28px; height: ${Math.max(6, percent)}%; background: linear-gradient(180deg, var(--primary-color, #1890ff), #096dd9); border-radius: 4px 4px 0 0; transition: height 0.4s ease;" title="${cat}: $${val}"></div>
                            <div style="font-size: 11px; color: var(--text-secondary); white-space: nowrap;">${cat}</div>
                        </div>
                    `;
                }).join('');

                trendContainer.innerHTML = `
                    <div style="display: flex; align-items: flex-end; justify-content: space-around; width: 100%; height: 220px; border-bottom: 1px solid var(--border-color, #eee); padding-bottom: 6px;">
                        ${barsHtml}
                    </div>
                    <div style="display: flex; justify-content: space-between; align-items: center; padding-top: 10px; font-size: 12px; color: var(--text-secondary);">
                        <span>总营收: <strong style="color: var(--primary-color); font-size: 14px;">$${data.totalAmount || '0.00'}</strong></span>
                        <span>统计周期: ${period === 'day' ? '按日' : (period === 'year' ? '按年' : '按月')}</span>
                    </div>
                `;
            }
        }

        // 渲染收入分布（环形占比）
        if (sourceContainer) {
            const firstRatio = data.firstChargeRatio != null ? data.firstChargeRatio : 30;
            const renewRatio = data.renewalRatio != null ? data.renewalRatio : 70;
            const firstAmt = data.firstChargeTotal || '0.00';
            const renewAmt = data.renewalTotal || '0.00';

            sourceContainer.innerHTML = `
                <div style="display: flex; flex-direction: column; align-items: center; gap: 20px; width: 100%;">
                    <!-- 环形比例展示 -->
                    <div style="position: relative; width: 140px; height: 140px; display: flex; align-items: center; justify-content: center;">
                        <svg viewBox="0 0 36 36" style="width: 100%; height: 100%; transform: rotate(-90deg);">
                            <circle cx="18" cy="18" r="15.915" fill="transparent" stroke="#f0f2f5" stroke-width="3.5"></circle>
                            <circle cx="18" cy="18" r="15.915" fill="transparent" stroke="#52c41a" stroke-width="3.5"
                                stroke-dasharray="${firstRatio} ${100 - firstRatio}" stroke-dashoffset="0"></circle>
                            <circle cx="18" cy="18" r="15.915" fill="transparent" stroke="#1890ff" stroke-width="3.5"
                                stroke-dasharray="${renewRatio} ${100 - renewRatio}" stroke-dashoffset="-${firstRatio}"></circle>
                        </svg>
                        <div style="position: absolute; text-align: center;">
                            <div style="font-size: 12px; color: var(--text-secondary);">首充 / 续费</div>
                            <div style="font-size: 16px; font-weight: 700; color: var(--text-primary);">${firstRatio}% / ${renewRatio}%</div>
                        </div>
                    </div>
                    <!-- 图例与金额 -->
                    <div style="display: flex; justify-content: space-around; width: 100%; font-size: 13px;">
                        <div style="display: flex; align-items: center; gap: 8px;">
                            <span style="width: 12px; height: 12px; border-radius: 2px; background: #52c41a; display: inline-block;"></span>
                            <div>
                                <div style="color: var(--text-secondary);">首充客户</div>
                                <div style="font-weight: 600;">$${firstAmt} (${firstRatio}%)</div>
                            </div>
                        </div>
                        <div style="display: flex; align-items: center; gap: 8px;">
                            <span style="width: 12px; height: 12px; border-radius: 2px; background: #1890ff; display: inline-block;"></span>
                            <div>
                                <div style="color: var(--text-secondary);">续费客户</div>
                                <div style="font-weight: 600;">$${renewAmt} (${renewRatio}%)</div>
                            </div>
                        </div>
                    </div>
                </div>
            `;
        }
    } catch (e) {
        console.error('加载统计数据异常:', e);
    }
}

// 查看账单详情
async function viewBill(orderNo) {
    const modal = document.getElementById('billModal');
    const detailsContainer = document.getElementById('billDetails');
    const title = document.getElementById('billModalTitle');

    if (title) title.textContent = `账单详情 - ${orderNo}`;
    if (detailsContainer) {
        detailsContainer.innerHTML = `<div style="text-align: center; padding: 20px;"><i class="fas fa-spinner fa-spin"></i> 正在加载详情...</div>`;
    }
    if (modal) modal.style.display = 'flex';

    try {
        const res = await fetch(`/system/bill/detail/${orderNo}`);
        const result = await res.json();
        if (result.code !== 0 && result.code !== 200) {
            if (detailsContainer) detailsContainer.innerHTML = `<div style="color: red;">获取账单详情失败: ${result.msg || '未知错误'}</div>`;
            return;
        }

        const data = result.data || {};
        const o = data.order || {};
        if (detailsContainer) {
            detailsContainer.innerHTML = `
                <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 15px; font-size: 13px;">
                    <div><strong>账单编号:</strong> <code style="color: #1890ff;">${o.orderNo || '-'}</code></div>
                    <div><strong>交易状态:</strong> <span style="font-weight: 600; color: ${o.status === 'SUCCESS' ? '#52c41a' : '#faad14'};">${o.status || '-'}</span></div>
                    <div><strong>付款金额:</strong> <span style="font-size: 16px; font-weight: bold; color: #52c41a;">$${o.amount || '0.00'} ${o.currency || 'USD'}</span></div>
                    <div><strong>支付渠道:</strong> ${o.type || '-'}</div>
                    <div><strong>客户用户名:</strong> ${data.username || '-'}</div>
                    <div><strong>客户邮箱:</strong> ${data.email || '-'}</div>
                    <div><strong>第三方交易号:</strong> <span style="word-break: break-all;">${o.tradeNo || '-'}</span></div>
                    <div><strong>客户类别:</strong> ${data.userType === 'outer' ? '分销商客户' : '直营内部客户'}</div>
                    <div style="grid-column: span 2;"><strong>支付时间:</strong> ${o.paidAt || o.createdAt || '-'}</div>
                    <div style="grid-column: span 2;"><strong>到期时间:</strong> ${o.expiredAt || '-'}</div>
                </div>
            `;
        }
    } catch (e) {
        console.error('加载账单详情失败:', e);
        if (detailsContainer) detailsContainer.innerHTML = `<div style="color: red;">请求详情失败，请检查网络</div>`;
    }
}

function closeBillModal() {
    const modal = document.getElementById('billModal');
    if (modal) modal.style.display = 'none';
}