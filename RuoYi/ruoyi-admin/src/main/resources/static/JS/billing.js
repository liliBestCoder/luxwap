// 账单模块：搜索、筛选、查看、编辑、删除、模态框控制等（导出功能已迁移到 common.js）

function searchBills() {
  const searchTerm = (document.getElementById('billSearch')?.value || '').toLowerCase();
  const statusFilter = document.getElementById('billStatus')?.value || '';
  const typeFilter = document.getElementById('billType')?.value || '';
  const startDate = document.getElementById('startDate')?.value || '';
  const endDate = document.getElementById('endDate')?.value || '';
  
  console.log('搜索账单:', { searchTerm, statusFilter, typeFilter, startDate, endDate });
  
  const rows = document.querySelectorAll('#billsTableBody tr');
  rows.forEach(row => {
    const text = row.textContent.toLowerCase();
    const status = row.querySelector('.status')?.textContent.toLowerCase() || '';
    
    let showRow = true;
    
    if (searchTerm && !text.includes(searchTerm)) {
      showRow = false;
    }
    
    if (statusFilter && !status.includes(statusFilter)) {
      showRow = false;
    }
    
    row.style.display = showRow ? '' : 'none';
  });
}

function clearBillFilter() {
  const billSearch = document.getElementById('billSearch');
  const billStatus = document.getElementById('billStatus');
  const billType = document.getElementById('billType');
  const startDate = document.getElementById('startDate');
  const endDate = document.getElementById('endDate');

  if (billSearch) billSearch.value = '';
  if (billStatus) billStatus.value = '';
  if (billType) billType.value = '';
  if (startDate) startDate.value = '';
  if (endDate) endDate.value = '';
  
  const rows = document.querySelectorAll('#billsTableBody tr');
  rows.forEach(row => {
    row.style.display = '';
  });
}

function viewBill(billNumber) {
  const billDetails = {
    'B001': {
      number: 'B001',
      date: '2024-03-01',
      amount: '¥1,250.00',
      status: '已付款',
      type: '月费',
      user: 'user1@example.com',
      description: '2024年3月月费账单',
      paymentMethod: '银行转账',
      dueDate: '2024-03-15'
    },
    'B002': {
      number: 'B002',
      date: '2024-03-15',
      amount: '¥850.00',
      status: '待付款',
      type: '流量费',
      user: 'user2@example.com',
      description: '额外流量使用费',
      paymentMethod: '支付宝',
      dueDate: '2024-03-30'
    }
  };

  const bill = billDetails[billNumber];
  if (bill) {
    document.getElementById('billModalTitle').textContent = `账单详情 - ${bill.number}`;
    document.getElementById('billDetails').innerHTML = `
      <div style="display: grid; grid-template-columns: 1fr 1fr; gap: 20px;">
        <div>
          <h4 style="color: var(--accent-color); margin-bottom: 10px;">基本信息</h4>
          <p><strong>账单号:</strong> ${bill.number}</p>
          <p><strong>日期:</strong> ${bill.date}</p>
          <p><strong>金额:</strong> ${bill.amount}</p>
          <p><strong>状态:</strong> <span class="status">${bill.status}</span></p>
          <p><strong>类型:</strong> ${bill.type}</p>
        </div>
        <div>
          <h4 style="color: var(--accent-color); margin-bottom: 10px;">详细信息</h4>
          <p><strong>用户:</strong> ${bill.user}</p>
          <p><strong>支付方式:</strong> ${bill.paymentMethod}</p>
          <p><strong>截止日期:</strong> ${bill.dueDate}</p>
          <p><strong>描述:</strong> ${bill.description}</p>
        </div>
      </div>
    `;
    document.getElementById('billModal').style.display = 'flex';
  }
}

function editBill(billNumber) {
  alert(`编辑账单: ${billNumber}`);
}

function deleteBill(billNumber) {
  if (confirm(`确定要删除账单 ${billNumber} 吗？`)) {
    console.log('删除账单:', billNumber);
    alert('账单删除成功！');
  }
}

function closeBillModal() {
  document.getElementById('billModal').style.display = 'none';
}

function setDefaultDateRange() {
  const today = new Date();
  const lastMonth = new Date(today.getFullYear(), today.getMonth() - 1, 1);
  const startDate = document.getElementById('startDate');
  const endDate = document.getElementById('endDate');
  
  if (startDate) startDate.value = lastMonth.toISOString().split('T')[0];
  if (endDate) endDate.value = today.toISOString().split('T')[0];
}

document.addEventListener('DOMContentLoaded', function () {
  setDefaultDateRange();
  
  const billSearch = document.getElementById('billSearch');
  if (billSearch) {
    billSearch.addEventListener('keypress', function(e) {
      if (e.key === 'Enter') {
        searchBills();
      }
    });
  }
  
  const billModal = document.getElementById('billModal');
  if (billModal) {
    billModal.addEventListener('click', function(e) {
      if (e.target === this) {
        closeBillModal();
      }
    });
  }
});