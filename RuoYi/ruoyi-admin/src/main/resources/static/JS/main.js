// 导航：切换二级菜单显示/隐藏
window.toggleSubmenu = function (submenuId, triggerEl) {
    const submenu = document.getElementById(submenuId);
    if (!submenu) return;
    submenu.classList.toggle('active');

    // 更新箭头图标方向
    const arrow = triggerEl?.querySelector('.fa-chevron-down');
    if (arrow) {
        arrow.style.transform = submenu.classList.contains('active') ? 'rotate(180deg)' : 'rotate(0deg)';
    }
};

// 导航：设置当前活动页面
window.setActive = function (element) {
    document.querySelectorAll('.nav-link').forEach(link => {
        link.classList.remove('active');
    });

    if (element.classList.contains('submenu-item')) {
        element.parentElement.previousElementSibling.classList.add('active');
    } else {
        element.classList.add('active');
    }
};

// 适配：调整iframe高度
window.adjustFrameHeight = function () {
    const frame = document.getElementById('content-frame');
    if (frame) {
        frame.style.minHeight = '600px';
    }
};

// 页面默认状态：展开客户管理并激活用户管理
document.addEventListener('DOMContentLoaded', function () {
    const customerSubmenu = document.getElementById('customer-submenu');
    if (customerSubmenu) {
        customerSubmenu.classList.add('active');
    }

    const userLink = document.querySelector('a[href="pages/users.html"]');
    if (userLink) {
        window.setActive(userLink);
    }
});

// 移动端菜单切换
window.toggleSidebar = function () {
    document.querySelector('.sidebar')?.classList.toggle('active');
};