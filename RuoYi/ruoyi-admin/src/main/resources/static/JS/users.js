// 用户模块：新增、编辑、删除、模态框控制等
window.menu = 'users';

(function initUsersPage() {
  document.addEventListener('DOMContentLoaded', () => {
    searchUsers();
    initUserFormSubmit();
    initUserModalOutsideClick();
  });
})();

function addUser() {
  const title = document.getElementById('modalTitle');
  const form = document.getElementById('userForm');
  if (title) title.textContent = '添加用户';
  if (form) form.reset();
  openUserModal();
}

// 表单提交处理（调用后端API）
function initUserFormSubmit() {
  const form = document.getElementById('userForm');
  if (!form) return;
  form.addEventListener('submit', async function(e) {
    e.preventDefault();
    const payload = {
      username: document.getElementById('username')?.value?.trim(),
      email: document.getElementById('email')?.value?.trim(),
      register_date: document.getElementById('registerDate')?.value || null,
      expiry_date: document.getElementById('expiryDate')?.value || null,
      monthly_usage: document.getElementById('monthlyUsage')?.value ? Number(document.getElementById('monthlyUsage').value) : null,
      belongto: document.getElementById('belongto')?.value || 'inner',
    };
    try {
      await apiRequest('/api/users', { method: 'POST', body: payload });
      toast('用户保存成功', 'success');
      closeUserModal();
      fetchUsers();
    } catch (err) {
      console.error(err);
      toast('保存失败：' + err.message, 'error');
    }
  });
}

// 点击模态框外部关闭
function initUserModalOutsideClick() {
  const modal = document.getElementById('userModal');
  if (!modal) return;
  modal.addEventListener('click', function(e) {
    if (e.target === this) {
      closeUserModal();
    }
  });
}
