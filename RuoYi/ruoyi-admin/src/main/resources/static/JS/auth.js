// 共享：显示错误
function showError(fieldId, message) {
  const field = document.getElementById(fieldId);
  if (!field) return;
  const formGroup = field.closest('.form-group');
  formGroup?.classList.add('error');
  const msg = formGroup?.querySelector('.error-message');
  if (msg) msg.textContent = message;
}

// 登录第三方
function socialLogin(provider) {
  console.log('第三方登录:', provider);
  alert(`正在跳转到${provider}登录...`);
}

// 注册第三方
function socialRegister(provider) {
  console.log('第三方注册:', provider);
  alert(`正在跳转到${provider}注册...`);
}

// 模拟已存在邮箱
const existingEmails = ['test@example.com', 'admin@luxwap.com'];

document.addEventListener('DOMContentLoaded', function () {
  // 登录表单
  const loginForm = document.getElementById('loginForm');
  if (loginForm) {
    loginForm.addEventListener('submit', function (e) {
      e.preventDefault();
      const email = document.getElementById('loginEmail').value;
      const password = document.getElementById('loginPassword').value;

      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(email)) {
        showError('loginEmail', '请输入有效的邮箱地址');
        return;
      }
      if (password.length < 6) {
        showError('loginPassword', '密码长度至少6位');
        return;
      }

      console.log('登录请求:', { email, password });
      alert('登录成功！');
      window.location.href = '../index.html';
    });

    document.querySelectorAll('.form-control').forEach(input => {
      input.addEventListener('input', function () {
        this.closest('.form-group')?.classList.remove('error');
      });
    });
  }

  // 注册表单
  const registerForm = document.getElementById('registerForm');
  if (registerForm) {
    registerForm.addEventListener('submit', function (e) {
      e.preventDefault();

      const email = document.getElementById('registerEmail').value;
      const password = document.getElementById('registerPassword').value;
      const confirmPassword = document.getElementById('confirmPassword').value;
      const country = document.getElementById('country').value;

      document.querySelectorAll('.form-group').forEach(group => {
        group.classList.remove('error');
      });

      let isValid = true;
      const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
      if (!emailRegex.test(email)) {
        showError('registerEmail', '请输入有效的邮箱地址');
        isValid = false;
      } else if (existingEmails.includes(email)) {
        showError('registerEmail', '该邮箱已被注册');
        isValid = false;
      }

      if (password.length < 8) {
        showError('registerPassword', '密码长度至少8位');
        isValid = false;
      } else if (!/(?=.*[a-z])(?=.*[A-Z])(?=.*\d)/.test(password)) {
        showError('registerPassword', '密码必须包含大小写字母和数字');
        isValid = false;
      }

      if (password !== confirmPassword) {
        showError('confirmPassword', '两次输入的密码不一致');
        isValid = false;
      }

      if (!country) {
        showError('country', '请选择所在国家');
        isValid = false;
      }

      if (isValid) {
        console.log('注册请求:', { email, password, country });
        alert('注册成功！请登录您的邮箱激活账户。');
        window.location.href = 'login.html';
      }
    });

    const pwd = document.getElementById('registerPassword');
    const strengthBar = document.getElementById('passwordStrengthBar');
    if (pwd && strengthBar) {
      pwd.addEventListener('input', function () {
        const password = this.value;
        let strength = 0;
        if (password.length >= 8) strength++;
        if (/[a-z]/.test(password) && /[A-Z]/.test(password)) strength++;
        if (/\d/.test(password)) strength++;
        if (/[^A-Za-z0-9]/.test(password)) strength++;

        strengthBar.className = 'password-strength-bar';
        if (strength === 1) strengthBar.classList.add('password-strength-weak');
        else if (strength === 2 || strength === 3) strengthBar.classList.add('password-strength-medium');
        else if (strength >= 4) strengthBar.classList.add('password-strength-strong');
      });
    }

    document.querySelectorAll('.form-control').forEach(input => {
      input.addEventListener('input', function () {
        this.closest('.form-group')?.classList.remove('error');
      });
    });
  }
});