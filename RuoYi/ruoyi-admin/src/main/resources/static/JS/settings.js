// ---------------- 常规设置功能 ----------------

// 用户名编辑功能
function editUsername() {
    const currentUsername = document.getElementById('current-username').value;
    document.getElementById('new-username').value = currentUsername;
    document.getElementById('username-edit-form').style.display = 'block';
}

function saveUsername() {
    const newUsername = document.getElementById('new-username').value.trim();
    if (!newUsername) {
        toast('用户名不能为空', 'error');
        return;
    }
    if (newUsername.length < 3) {
        toast('用户名至少需要3个字符', 'error');
        return;
    }
    
    // 模拟保存到后端
    document.getElementById('current-username').value = newUsername;
    localStorage.setItem('current_username', newUsername);
    document.getElementById('username-edit-form').style.display = 'none';
    toast('用户名已更新', 'success');
}

function cancelUsernameEdit() {
    document.getElementById('username-edit-form').style.display = 'none';
    document.getElementById('new-username').value = '';
}

// 邮箱编辑功能
function editEmail() {
    const currentEmail = document.getElementById('current-email').value;
    document.getElementById('new-email').value = currentEmail;
    document.getElementById('email-edit-form').style.display = 'block';
}

function saveEmail() {
    const newEmail = document.getElementById('new-email').value.trim();
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    
    if (!newEmail) {
        toast('邮箱地址不能为空', 'error');
        return;
    }
    if (!emailRegex.test(newEmail)) {
        toast('请输入有效的邮箱地址', 'error');
        return;
    }
    
    // 模拟保存到后端
    document.getElementById('current-email').value = newEmail;
    localStorage.setItem('current_email', newEmail);
    document.getElementById('email-edit-form').style.display = 'none';
    toast('邮箱地址已更新', 'success');
}

function cancelEmailEdit() {
    document.getElementById('email-edit-form').style.display = 'none';
    document.getElementById('new-email').value = '';
}

// 密码修改功能
function editPassword() {
    document.getElementById('password-edit-form').style.display = 'block';
}

function logout() {
    fetch(`/logout`, {
        method: 'POST',
        credentials: 'include',
        redirect: 'manual' // 禁用自动重定向
    })
        .then(response => {
            if (response.status === 302) {
                // 获取重定向位置（需要服务器在响应头中设置）
                const location = response.headers.get('Location');
                if (location) {
                    window.top.location.href = location;
                } else {
                    window.top.location.href = '/login';
                }
            } else {
                window.top.location.href = '/login';
            }
        })
        .catch(error => {
            console.error('注销请求失败:', error);
            window.location.href = '/login';
        });
}

async function savePassword() {
    const currentPassword = document.getElementById('current-password').value;
    const newPassword = document.getElementById('new-password').value;
    const confirmPassword = document.getElementById('confirm-password').value;
    
    if (!currentPassword || !newPassword || !confirmPassword) {
        toast('请填写所有密码字段', 'error');
        return;
    }
    if (newPassword.length < 6) {
        toast('新密码至少需要6个字符', 'error');
        return;
    }
    if (newPassword !== confirmPassword) {
        toast('新密码和确认密码不匹配', 'error');
        return;
    }

    const res = await fetch("/system/user/profile/resetPwd", {
        method: "POST",
        headers: {
            'Content-Type': 'application/x-www-form-urlencoded',
        },
        credentials: 'include',
        body: `oldPassword=${currentPassword}&newPassword=${newPassword}`
    });

    const data = await res.json();
    if(data.code !== 0){
        toast(data.msg, 'error');
        return;
    }

    toast('密码已成功更新', 'success');
}

function cancelPasswordEdit() {
    document.getElementById('password-edit-form').style.display = 'none';
    document.getElementById('current-password').value = '';
    document.getElementById('new-password').value = '';
    document.getElementById('confirm-password').value = '';
}

// // TOTP双因素认证功能
// function toggleTOTP() {
//     const statusText = document.getElementById('totp-status-text').textContent;
//
//     if (statusText === '未启用') {
//         // 开始设置TOTP
//         document.getElementById('totp-setup-form').style.display = 'block';
//         generateTOTPSecret();
//     } else {
//         // 开始禁用TOTP
//         document.getElementById('totp-disable-form').style.display = 'block';
//     }
// }
//
// function generateTOTPSecret() {
//     // 生成随机密钥（实际应用中应该使用加密库）
//     const chars = 'ABCDEFGHIJKLMNOPQRSTUVWXYZ234567';
//     let secret = '';
//     for (let i = 0; i < 16; i++) {
//         secret += chars.charAt(Math.floor(Math.random() * chars.length));
//     }
//
//     document.getElementById('totp-secret').textContent = secret;
//
//     // 模拟二维码生成（实际应用中需要使用QR库）
//     const qrContainer = document.getElementById('qr-code-container').firstElementChild;
//     qrContainer.innerHTML = `
//         <div style="background: url('data:image/svg+xml,${encodeURIComponent(`
//             <svg xmlns="http://www.w3.org/2000/svg" width="200" height="200" viewBox="0 0 200 200">
//                 <rect width="200" height="200" fill="white"/>
//                 <text x="100" y="100" text-anchor="middle" dy=".3em" font-size="12" fill="black">
//                     QR Code for:<br/>${secret}
//                 </text>
//             </svg>
//         `)}'); width: 200px; height: 200px; background-size: contain;"></div>
//     `;
// }
//
// function confirmTOTPSetup() {
//     const verifyCode = document.getElementById('totp-verify-code').value;
//
//     if (!verifyCode || verifyCode.length !== 6) {
//         toast('请输入6位验证码', 'error');
//         return;
//     }
//
//     // 模拟验证码验证（实际应用中需要后端验证）
//     const isValid = /^\d{6}$/.test(verifyCode);
//
//     if (isValid) {
//         // 启用TOTP
//         localStorage.setItem('totp_enabled', 'true');
//         localStorage.setItem('totp_secret', document.getElementById('totp-secret').textContent);
//
//         document.getElementById('totp-status-text').textContent = '已启用';
//         document.getElementById('totp-status-text').style.color = 'var(--success-color)';
//         document.getElementById('totp-toggle-btn').innerHTML = '<i class="fas fa-times"></i> 禁用2FA';
//
//         document.getElementById('totp-setup-form').style.display = 'none';
//         document.getElementById('totp-verify-code').value = '';
//
//         toast('双因素认证已启用', 'success');
//     } else {
//         toast('验证码格式不正确，请重试', 'error');
//     }
// }
//
// function cancelTOTPSetup() {
//     document.getElementById('totp-setup-form').style.display = 'none';
//     document.getElementById('totp-verify-code').value = '';
// }
//
// function confirmTOTPDisable() {
//     const password = document.getElementById('totp-disable-password').value;
//
//     if (!password) {
//         toast('请输入当前密码', 'error');
//         return;
//     }
//
//     // 模拟密码验证（实际应用中需要后端验证）
//     if (password.length >= 6) {
//         // 禁用TOTP
//         localStorage.removeItem('totp_enabled');
//         localStorage.removeItem('totp_secret');
//
//         document.getElementById('totp-status-text').textContent = '未启用';
//         document.getElementById('totp-status-text').style.color = 'var(--danger-color)';
//         document.getElementById('totp-toggle-btn').innerHTML = '<i class="fas fa-qrcode"></i> 启用2FA';
//
//         document.getElementById('totp-disable-form').style.display = 'none';
//         document.getElementById('totp-disable-password').value = '';
//
//         toast('双因素认证已禁用', 'warning');
//     } else {
//         toast('密码不正确', 'error');
//     }
// }
//
// function cancelTOTPDisable() {
//     document.getElementById('totp-disable-form').style.display = 'none';
//     document.getElementById('totp-disable-password').value = '';
// }

// 显示设置功能
function updatePageSize(size) {
    // 实时更新显示，无需点击保存
    localStorage.setItem('page_size', size);
    toast(`每页显示记录数已设置为 ${size} 条`, 'info');
}

function saveDisplaySettings() {
    const el = document.getElementById('page-size-setting');
    if (el) {
        localStorage.setItem('page_size', el.value);
        toast('显示设置已保存', 'success');
    }
}

function resetDisplaySettings() {
    const el = document.getElementById('page-size-setting');
    if (el) {
        el.value = '20';
    }
    localStorage.setItem('page_size', '20');
    toast('显示设置已重置为默认值', 'info');
}

// 初始化常规设置
function initGeneralSettings() {
    const savedPageSize = localStorage.getItem('page_size') || '20';
    const pageSizeEl = document.getElementById('page-size-setting');
    if (pageSizeEl) {
        pageSizeEl.value = savedPageSize;
    }
}

document.addEventListener('DOMContentLoaded', () => {
    initGeneralSettings();
});