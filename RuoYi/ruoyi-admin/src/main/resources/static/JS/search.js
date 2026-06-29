const typeLabels = {
    "fiber": "光纤",
    "cable": "电缆",
    "wireless": "无线",
    "satellite": "卫星"
};

const statusLabels = {
    "online": "在线",
    "maintenance": "维护中",
    "offline": "离线",
    "error": "故障"
};

const regionLabels = {
    "asia": "亚洲",
    "europe": "欧洲",
    "north_america": "北美洲",
    "south_america": "南美洲"
};

const DISTRIBUTOR_LEVEL = {
    diamond: "钻石级",
    gold: "黄金级",
    silver: "白银级",
    normal: "普通级",
};

// 地区字典
const DISTRIBUTOR_REGION = {
    CN: "中国",
    TW: "台湾",
    VN: "越南",
    RU: "俄罗斯",
    KR: "韩国",
    US: "美国",
    JP: "日本",
};

const pageSize = localStorage.getItem('page_size');


async function searchLines() {
    const name = (document.getElementById('lineName')?.value || '').toLowerCase();
    const status = document.getElementById('lineStatus')?.value || '';
    const type = document.getElementById('lineType')?.value || '';
    const region = document.getElementById('lineRegion')?.value || '';
    const paging = window[`${menu}-paging`];

    const response = await fetch(`/system/lines/list`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        credentials: 'include',
        body:`name=${name}&status=${status}&type=${type}&region=${region}&pageSize=${paging.pageSize}&pageNum=${paging.pageNumber}&isAsc=asc`
    });
    const data = await response.json();

    if(data.code !== 0){
        toast(data.msg, 'error');
        return;
    }

    const tbody = document.getElementById('linesTableBody');
    
    // 模拟搜索结果过滤
    if (!tbody) return;
    const colspan = $(tbody).prev().find("tr th").length;
    tbody.innerHTML = (data.rows || []).map(l => { let row = `
      <tr>
        <td>#${l.id}</td>
        <td>${regionLabels[l.region]}</td>
        <td>${l.name || ''}</td>
        <td>${typeLabels[l.type]}</td>`;
        if(colspan > 9){
            row += `<td>${l.ip}</td>`;
        }
        row += `
        <td>${l.bandwidth || ''}</td>
        <td>${l.totalTraffic}</td>
        <td class="traffic-cell" data-total="${l.totalTraffic}" data-remain="${l.remainingTraffic}">
            <div class="traffic-progress" aria-label="剩余流量进度">
              <div class="traffic-progress__fill"></div>
            </div>
        <span>${l.remainingTraffic}</span>
      </td>`
        if(colspan > 9){
            row += `<td class="conn-cell" data-current="${l.connectionCnt}" data-max="${l.maxConnectionCnt}">
                        <div class="traffic-progress" aria-label="连接占用进度">
                          <div class="traffic-progress__fill"></div>
                        </div>
                        <span>${l.connectionCnt} / ${l.maxConnectionCnt}</span>
                      </td>`;
        }
        row += `<td>${l.pingDelay}</td>`;
        if(colspan > 9){
            row += `<td><input type="number" class="form-control" style="width: 60px; padding: 2px 5px;" value="${l.pingOffset}" min="-2000">ms</td>`;
        }
        if (colspan <= 9){
            row += `<td><span class="status ${l.status === 'online' ? 'status-active' : (l.status === 'maintenance' ? 'status-pending' : (l.status === 'error' ? 'status-inactive' : 'status-secondary'))}">${statusLabels[l.status]}</span></td>
      </tr>
    `;
        }else{
            row += `<td>
        <select class="form-control" style="width: auto; padding: 2px 5px;">
          <option class="spangreen" value="online" ${l.status == 'online' ? 'selected' : ''}>在线</option>
          <option class="spanyellow" value="maintenance"  ${l.status == 'maintenance' ? 'selected' : ''}>维护中</option>
          <option class="spangray" value="offline"  ${l.status == 'offline' ? 'selected' : ''}>离线</option>
          <option class="spanred" value="error"  ${l.status == 'error' ? 'selected' : ''}>故障</option>
        </select>
      </td>
      <td><button class="btn btn-secondary" style="padding: 5px 10px; margin-right: 5px;" onclick="viewLine('L001')"> <i class="fas fa-eye"></i> </button>
      </td>
      <td>
        <button class="btn btn-primary" style="padding: 5px 10px; margin-right: 5px;" onclick="editLine(this, '${l.id}')"> <i class="fas fa-check"></i> </button>
        <button class="btn btn-secondary" style="padding: 5px 10px; background: rgba(255, 71, 87, 0.2); color: var(--danger-color);" onclick="deleteLine('L001')"> <i class="fas fa-trash"></i> </button>
      </td></tr>`;
        }
       return row;}).join('');

    initTrafficProgress('#linesTableBody');
    initConnectionProgress('#linesTableBody');

    paging.totalItems = data.total;
    let start = (paging.pageNumber - 1) * paging.pageSize + 1;
    let end = Math.min(start + paging.pageSize - 1, paging.totalItems);
    document.getElementById(`pagination-info-${menu}`).innerText =
        `显示 ${start}-${end} 条，共 ${paging.totalItems} 条记录`;

    $(`#pagination-container-${menu}`).pagination('updateItems', paging.totalItems);
}

async function searchUsers() {
    // 获取搜索框的值
    const search = document.getElementById('searchInput').value;
    // 获取归属下拉框的值
    const belong = document.getElementById('statusFilter').value;
    // 获取注册时间下拉框的值
    const registerTime = document.getElementById('timeFilter').value;

    const paging = window[`${menu}-paging`];

    try {

        const response = await fetch('/system/xray-user/list', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/x-www-form-urlencoded',
            },
            credentials: 'include',
            body: `pageSize=${paging.pageSize}&pageNum=${paging.pageNumber}&search=${search}&belong=${belong}&registerTime=${registerTime}&isAsc=asc`  // 直接字符串
        });
        const data = await response.json();
        const tbody = document.getElementById('usersTableBody');
        const usersTable = document.getElementById('usersTable');
        const columnCount  = usersTable.querySelectorAll('thead th').length;
        if (!tbody) return;
        tbody.innerHTML = data.rows.map(u => {let row = `
      <tr>
        <td>#${u.id}</td>
        <td>${u.username}</td>
        <td>${u.email}</td>`;
            if(columnCount > 8){
                row += `<td>${u.cumulativeMonths}</td>`;
            }

            row += `
        <td>${u.createdAt ? u.createdAt.substring(0,10) : ''}</td>
        <td>${u.expiration ? u.expiration.substring(0,10) : ''}</td>
        <td>${u.cumulativeMonths ?? ''}</td>
        <td><span class="belongto ${u.type === 'inner' ? 'belongto-inner' : 'belongto-outer'}">
          ${u.type === 'inner' ? '内部' : '分销商'}
        </span></td>
    `;
            if(columnCount > 8){
                row += `<td><button class="btn btn-secondary" style="padding: 5px 10px; margin-right: 5px;" onclick="editUser(${u.id})"> <i class="fas fa-edit"></i> </button>
                    </td></tr>`
            }
            return row; }).join('');

        // 更新左下角信息
        paging.totalItems = data.total;
        let start = (paging.pageNumber - 1) * paging.pageSize + 1;
        let end = Math.min(start + paging.pageSize - 1, paging.totalItems);
        document.getElementById(`pagination-info-${menu}`).innerText =
            `显示 ${start}-${end} 条，共 ${paging.totalItems} 条记录`;

        $(`#pagination-container-${menu}`).pagination('updateItems', paging.totalItems);

    } catch (err) {
        console.error(err);
        toast('获取用户列表失败', 'error');
    }
}

async function searchDistributors() {
    window.menu = 'dealer'
    const name = (document.getElementById('distributorName')?.value || '').toLowerCase();
    const status = document.getElementById('distributorStatus')?.value || '';
    const level = document.getElementById('distributorLevel')?.value || '';
    // 删除：status
    const region = document.getElementById('distributorRegion')?.value || '';
    const paging = window[`${menu}-paging`];

    const response = await fetch(`/system/distributors/list`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        credentials: 'include',
        body:`contactPerson=${name}&level=${level}&status=${status}&region=${region}&pageSize=${paging.pageSize}&pageNum=${paging.pageNumber}&isAsc=asc`
    });
    const data = await response.json();

    if(data.code !== 0){
        toast(data.msg, 'error');
        return;
    }

    const tbody = document.getElementById('distributorsTableBody');
    // 模拟搜索结果过滤
    if (!tbody) return;
    const table = document.getElementById('distributorsTable');

    const columnCount  = table.querySelectorAll('thead th').length;

    tbody.innerHTML = (data.rows || []).map(l => {let row = `<tr>
                        <td>#${l.id}</td>
                        <td>${l.createdAt ? l.createdAt.substring(0,10) : ''}</td>
                        <td>${l.email}</td>
                        <td>${l.contactPerson}</td>
                        <td><span style="color: var(--accent-color);">${DISTRIBUTOR_LEVEL[l.level]}</span></td>
                        <td>${l.monthlySales}</td>
                        <td>${l.commissionRate}</td>
                        <td>${l.firstChargeBonus}</td>
                        <td>${l.paypalAccount}</td>
                        <td><code>${l.bindingCode}</code></td>
                        <td>${l.exclusiveSuffix}</td>
                        <td>${DISTRIBUTOR_REGION[l.region]}</td>`;
                        if(columnCount > 12){
                        row += ` <td>
                                <button class="btn btn-secondary btn-sm" onclick="editDistributor('${l.id}')">
                                    <i class="fas fa-edit"></i>
                                </button>
                            </td>`
                        }
                        row += `</tr>`; return row;}).join('');

    paging.totalItems = data.total;
    let start = (paging.pageNumber - 1) * paging.pageSize + 1;
    let end = Math.min(start + paging.pageSize - 1, paging.totalItems);
    document.getElementById(`pagination-info-${menu}`).innerText =
        `显示 ${start}-${end} 条，共 ${paging.totalItems} 条记录`;

    $(`#pagination-container-${menu}`).pagination('updateItems', paging.totalItems);
}

async function searchDistributorsApply() {
    const paging = window[`${menu}-apply-paging`];

    const response = await fetch(`/system/distributors/list`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        credentials: 'include',
        body:`status=init&pageSize=${paging.pageSize}&pageNum=${paging.pageNumber}&isAsc=asc`
    });
    const data = await response.json();

    if(data.code !== 0){
        toast(data.msg, 'error');
        return;
    }

    const tbody = document.getElementById('distributorsTableBodyApply');
    // 模拟搜索结果过滤
    if (!tbody) return;

    tbody.innerHTML = (data.rows || []).map(l => ` <tr>
                        <td>#${l.id}</td>
                        <td>${l.createdAt ? l.createdAt.substring(0,10) : ''}</td>
                        <td>${l.email}</td>
                        <td>${l.contactPerson}</td>
                        <td><a href="https://example.com/verify/124" target="_blank">查看验证链接</a></td>
                        <td><code>${l.bindingCode}</code></td>
                        <td>${l.exclusiveSuffix}</td>
                        <td>${l.paypalAccount}</td>
                        <td>${DISTRIBUTOR_REGION[l.region]}</td>
                        <td>
                            <button class="btn btn-success btn-sm" onclick="approveDealer('${l.id}', '${l.email}')">
                                <i class="fas fa-check"></i> 通过
                            </button>
                            <button class="btn btn-danger btn-sm" onclick="rejectDealer('${l.id}', '${l.email}')">
                                <i class="fas fa-times"></i> 拒绝
                            </button>
                        </td>
                    </tr>`).join('');

    paging.totalItems = data.total;
    let start = (paging.pageNumber - 1) * paging.pageSize + 1;
    let end = Math.min(start + paging.pageSize - 1, paging.totalItems);
    document.getElementById(`pagination-info-${menu}-apply`).innerText =
        `显示 ${start}-${end} 条，共 ${paging.totalItems} 条记录`;

    $(`#pagination-container-${menu}-apply`).pagination('updateItems', paging.totalItems);
}

async function searchInnerUsers(){
    const paging = window[`inner-users-paging`];

    const innerUserName = document.getElementById('innerSearchInput').value || "";
    const innerRole = document.getElementById('roleFilter').value || "";

    const response = await fetch(`/system/adminsettings//list-inner-users`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        credentials: 'include',
        body:`userName=${innerUserName}&type=${innerRole}&pageSize=${paging.pageSize}&pageNum=${paging.pageNumber}&isAsc=asc`
    });
    const data = await response.json();

    if(data.code !== 0){
        toast(data.msg, 'error');
        return;
    }

    const tbody = document.getElementById('innerUsersTableBody');
    // 模拟搜索结果过滤
    if (!tbody) return;

    tbody.innerHTML = (data.rows || []).map(l => {let row = ` <tr>
                        <td>#${l.id}</td>
                        <td>${l.userName}</td>
                        <td>${l.password}</td>
                        <td>${ROLES_KEY_MAPPING[l.type]}</td>
                        <td><span class="status ${l.status === 0 ? 'status-inactive' : 'status-active'}">${l.status == 0 ? "禁用" : "启用"}</span></td>
                        <td>`

                        if(l.status == 1){
                            row += `<button class="btn btn-error btn-sm" onclick="disableInnerUser('${l.id}', '${l.status}')">
                                    <i class="fas fa-ban"></i> 禁用
                                </button>`;
                        }else{
                            row += `<button class="btn btn-success btn-sm" onclick="disableInnerUser('${l.id}', '${l.status}')">
                                <i class="fas fa-check"></i> 启用
                            </button>`;
                        }

                       row += `<button class="btn btn-danger btn-sm" onclick="delInnerUser('${l.id}')">
                            <i class="fas fa-times"></i> 删除
                            </button>
                        </td>
                    </tr>`; return row; }).join('');

    paging.totalItems = data.total;
    let start = (paging.pageNumber - 1) * paging.pageSize + 1;
    let end = Math.min(start + paging.pageSize - 1, paging.totalItems);
    document.getElementById(`pagination-info-inner-users`).innerText =
        `显示 ${start}-${end} 条，共 ${paging.totalItems} 条记录`;

    $("#pagination-container-inner-users").pagination('updateItems', paging.totalItems);
}

const tableList = $(".main-tab-content");

if(tableList.length <= 0){
    const paging = window[`${menu}-paging`] = {
        totalItems: 0,
        pageSize: pageSize || 2,
        pageNumber: 1
    };

    $(`#pagination-container-${menu}`).pagination({
        items: paging.totalItems,
        itemsOnPage: paging.pageSize,
        displayedPages: 3,
        edges: 1,
        prevText: '上一页',
        nextText: '下一页',
        cssStyle: 'light-theme', // 可以自定义成你要的样式
        onPageClick: function(clickedPageNumber) {
            paging.pageNumber = clickedPageNumber;
            if(menu === 'users'){
                searchUsers();
            }else if(menu === 'lines'){
                searchLines();
            }else if (menu === 'dealer'){
                searchDistributors();
            }
        }
    });
}else{
    const innerPaging = window[`inner-users-paging`] = {
        totalItems: 0,
        pageSize: pageSize || 2,
        pageNumber: 1
    }
    $("#pagination-container-inner-users").pagination({
        items: innerPaging.totalItems,
        itemsOnPage: innerPaging.pageSize,
        displayedPages: 3,
        edges: 1,
        prevText: '上一页',
        nextText: '下一页',
        cssStyle: 'light-theme', // 可以自定义成你要的样式
        onPageClick: function(clickedPageNumber) {
            innerPaging.pageNumber = clickedPageNumber;
            searchInnerUsers();
        }
    });
    $(".main-tab-content").each((_,obj) => {
        const menu = $(obj).attr('id').replace('-tab', '');
        const menus = [menu];
        if(menu === 'dealer'){
            menus.push('dealer-apply');
        }
        menus.forEach(menuTab => {
            const paging = window[`${menuTab}-paging`] = {
                totalItems: 0,
                pageSize: pageSize || 2,
                pageNumber: 1,
                menu:  menuTab,
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
                    if(menuTab === 'users'){
                        searchUsers();
                    }else if(menuTab === 'lines'){
                        searchLines();
                    }else if (menuTab === 'dealer') {
                        searchDistributors();
                    }else if(menuTab === 'dealer-apply'){
                        searchDistributorsApply();
                    }
                }
            });
        });
    })
}


function clearCsvFilter(){
    const csvFileInput = document.getElementById("csvFileInput")
    if (csvFileInput){
        csvFileInput.value = '';
    }
    const csvTableBody2 = document.getElementById(`csvTableBody2`);
    if(csvTableBody2){
        csvTableBody2.innerHTML = "";
        csvPreview2.style.display = 'none';
    }
}
/**
 * 清除线路筛选条件
 */
function clearLineFilter() {
    const lineName = document.getElementById('lineName');
    const lineStatus = document.getElementById('lineStatus');
    const lineType = document.getElementById('lineType');
    const lineRegion = document.getElementById('lineRegion');

    if (lineName) lineName.value = '';
    if (lineStatus) lineStatus.value = '';
    if (lineType) lineType.value = '';
    if (lineRegion) lineRegion.value = '';
}

/**
 * 清除用户筛选条件
 */
function clearFilter() {
    const searchInput = document.getElementById('searchInput');
    const statusFilter = document.getElementById('statusFilter');
    const timeFilter = document.getElementById('timeFilter');

    if (searchInput) searchInput.value = '';
    if (statusFilter) statusFilter.value = '';
    if (timeFilter) timeFilter.value = '';
}

function clearDistributorFilter() {
    const distributorName = document.getElementById('distributorName');
    const distributorLevel = document.getElementById('distributorLevel');
    const distributorStatus = document.getElementById('distributorStatus');
    const distributorRegion = document.getElementById('distributorRegion');

    if (distributorName) distributorName.value = '';
    if (distributorLevel) distributorLevel.value = '';
    if (distributorStatus) distributorStatus.value = '';
    if (distributorRegion) distributorRegion.value = '';
}

function clearInnerFilter(){
    const  innerUserName = document.getElementById('innerSearchInput');
    const  innerRole = document.getElementById('roleFilter');
    if(innerUserName)innerUserName.value = '';
    if(innerRole)innerRole.value = '';
}

// 监听输入框回车事件（用户搜索）
const searchInputEl = document.getElementById('searchInput');
if (searchInputEl) {
    searchInputEl.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            searchUsers();
        }
    });
}

// 监听输入框回车事件（线路搜索）
const innerSearchInput = document.getElementById('innerSearchInput');
if (innerSearchInput) {
    innerSearchInput.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            searchInnerUsers();
        }
    });
}

const lineNameEl = document.getElementById('lineName');
if (lineNameEl) {
    lineNameEl.addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            searchLines();
        }
    });
}

document.addEventListener('DOMContentLoaded', function () {
    const distributorName = document.getElementById('distributorName');
    if (distributorName) {
        distributorName.addEventListener('keypress', function(e) {
            if (e.key === 'Enter') {
                searchDistributors();
            }
        });
    }
});
