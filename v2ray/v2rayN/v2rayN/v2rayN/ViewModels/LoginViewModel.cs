using System;
using System.ComponentModel;
using System.Diagnostics;
using System.Net.NetworkInformation;
using System.Runtime.CompilerServices;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Input;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using v2rayN.util;
using v2rayN.Views;

namespace v2rayN.ViewModels;
public class LoginViewModel : INotifyPropertyChanged
{
    private Login _login;
    public class RelayCommand : ICommand
    {
        private readonly Action<object?> _execute;
        private readonly Func<object?, bool>? _canExecute;

        public RelayCommand(Action<object?> execute, Func<object?, bool>? canExecute = null)
        {
            _execute = execute;
            _canExecute = canExecute;
        }

        public bool CanExecute(object? parameter) => _canExecute?.Invoke(parameter) ?? true;

        public void Execute(object? parameter) => _execute(parameter);

        public event EventHandler? CanExecuteChanged
        {
            add => CommandManager.RequerySuggested += value;
            remove => CommandManager.RequerySuggested -= value;
        }
    }

    public LoginViewModel(Login login) {
        _login = login;
    }

    // 登录/注册切换
    private int _mode = 1;
    public int Mode
    {
        get => _mode;
        set
        {
            _mode = value;
            OnPropertyChanged();
        }
    }

    private string _loginEmail;

    public string LoginEmail
    {
        get => _loginEmail;
        set
        {
            if (_loginEmail != value)
            {
                _loginEmail = value;
                OnPropertyChanged(); // 通知UI更新
            }
        }
    }

    private string _loginPassword;

    public string LoginPassword
    {
        get => _loginPassword;
        set
        {
            if (_loginPassword != value)
            {
                _loginPassword = value;
                OnPropertyChanged(); // 通知UI更新
            }
        }
    }


    private string _registerEmail;

    public string RegisterEmail
    {
        get => _registerEmail;
        set
        {
            if (_registerEmail != value)
            {
                _registerEmail = value;
                OnPropertyChanged(); // 通知UI更新
            }
        }
    }

    private string _registerPassword;

    public string RegisterPassword
    {
        get => _registerPassword;
        set
        {
            if (_registerPassword != value)
            {
                _registerPassword = value;
                OnPropertyChanged(); // 通知UI更新
            }
        }
    }

    private string _registerConfirmPassword;

    public string RegisterConfirmPassword
    {
        get => _registerConfirmPassword;
        set
        {
            if (_registerConfirmPassword != value)
            {
                _registerConfirmPassword = value;
                OnPropertyChanged(); // 通知UI更新
            }
        }
    }

    private string _registerCode;

    public string RegisterCode
    {
        get => _registerCode;
        set
        {
            if (_registerCode != value)
            {
                _registerCode = value;
                OnPropertyChanged(); // 通知UI更新
            }
        }
    }


    private string _resetEmail;

    public string ResetEmail
    {
        get => _resetEmail;
        set
        {
            if (_resetEmail != value)
            {
                _resetEmail = value;
                OnPropertyChanged(); // 通知UI更新
            }
        }
    }

    private string _resetPassword;

    public string ResetPassword
    {
        get => _resetPassword;
        set
        {
            if (_resetPassword != value)
            {
                _resetPassword = value;
                OnPropertyChanged(); // 通知UI更新
            }
        }
    }

    private string _resetConfirmPassword;

    public string ResetConfirmPassword
    {
        get => _resetConfirmPassword;
        set
        {
            if (_resetConfirmPassword != value)
            {
                _resetConfirmPassword = value;
                OnPropertyChanged(); // 通知UI更新
            }
        }
    }


    private string _resetCode;

    public string ResetCode
    {
        get => _resetCode;
        set
        {
            if (_resetCode != value)
            {
                _resetCode = value;
                OnPropertyChanged(); // 通知UI更新
            }
        }
    }



    private bool _isCodeVisible = false;
    public bool IsCodeVisible { get => _isCodeVisible; set { _isCodeVisible = value; OnPropertyChanged(); } }


    private bool _isResetCodeVisible = false;
    public bool IsResetCodeVisible { get => _isResetCodeVisible; set { _isResetCodeVisible = value; OnPropertyChanged(); } }

    private int _countdown = 0;
    public string CodeCountdown => _countdown > 0 ? $"{_countdown}s" : "验证码";

    private int _resetCodeCountdown = 0;
    public string ResetCodeCountdown => _resetCodeCountdown > 0 ? $"{_resetCodeCountdown}s" : "验证码";

    private bool _canSendCode = true;
    public bool CanSendCode { get => _canSendCode; set { _canSendCode = value; OnPropertyChanged(); } }

    private bool _resetCanSendCode = true;
    public bool ResetCanSendCode { get => _resetCanSendCode; set { _resetCanSendCode = value; OnPropertyChanged(); } }


    private ICommand? _switchToRegisterCommand;
    public ICommand SwitchToRegisterCommand
        => _switchToRegisterCommand ??= new RelayCommand(_ => Mode = 2);


    private ICommand? _switchToResetPwdCommand;
    public ICommand SwitchToResetPwdCommand
        => _switchToResetPwdCommand ??= new RelayCommand(_ => Mode = 3);


    private ICommand? _backToLoginCommand;
    public ICommand BackToLoginCommand
        => _backToLoginCommand ??= new RelayCommand(_ => Mode = 1);

    public ICommand SendCodeCommand => new RelayCommand(async _ =>
    {
        if (!CanSendCode)
            return;
        var email = RegisterEmail?.Trim();
        if (string.IsNullOrWhiteSpace(email))
        {
            MessageBox.Show("请输入新的邮箱");
            return;
        }

        var emailPattern = @"^[^@\s]+@[^@\s]+\.[^@\s]+$";
        if (!Regex.IsMatch(email, emailPattern))
        {
            MessageBox.Show("邮箱格式不正确!");
            return;
        }
        var result = await App.apiService.SendCodeAsync(email, TokenManager.Token);
        if (result.Code == "0")
        {
            _countdown = 300;
            CanSendCode = false;
            OnPropertyChanged(nameof(CodeCountdown));

            // 倒计时
            await Task.Run(async () =>
            {
                while (_countdown > 0)
                {
                    await Task.Delay(1000);
                    _countdown--;
                    OnPropertyChanged(nameof(CodeCountdown));
                }
                CanSendCode = true;
                _countdown = 0;
                OnPropertyChanged(nameof(CodeCountdown));
            });
        }
        else
        {
            MessageBox.Show(result.Msg);
            CanSendCode = true;
            _countdown = 0;
            OnPropertyChanged(nameof(CodeCountdown));
        }
    });


    public ICommand ResetSendCodeCommand => new RelayCommand(async _ =>
    {
        if (!ResetCanSendCode)
            return;
        var email = ResetEmail?.Trim();

        if (string.IsNullOrWhiteSpace(email))
        {
            MessageBox.Show("请输入新的邮箱");
            return;
        }

        var emailPattern = @"^[^@\s]+@[^@\s]+\.[^@\s]+$";
        if (!Regex.IsMatch(email, emailPattern))
        {
            MessageBox.Show("邮箱格式不正确!");
            return;
        }

        var result = await App.apiService.SendCodeAsync(email, TokenManager.Token);
        if (result.Code == "0")
        {
            _resetCodeCountdown = 300;
            ResetCanSendCode = false;
            OnPropertyChanged(nameof(ResetCanSendCode));

            // 倒计时
            await Task.Run(async () =>
            {
                while (_resetCodeCountdown > 0)
                {
                    await Task.Delay(1000);
                    _resetCodeCountdown--;
                    OnPropertyChanged(nameof(ResetCodeCountdown));
                }
                ResetCanSendCode = true;
                _resetCodeCountdown = 0;
                OnPropertyChanged(nameof(ResetCodeCountdown));
            });
        }
        else
        {
            MessageBox.Show(result.Msg);
            ResetCanSendCode = true;
            _resetCodeCountdown = 0;
            OnPropertyChanged(nameof(ResetCodeCountdown));
        }
    });

    private string GetMacAddress()
    {
        var nic = NetworkInterface.GetAllNetworkInterfaces()
                  .FirstOrDefault(n => n.NetworkInterfaceType != NetworkInterfaceType.Loopback
                                       && n.OperationalStatus == OperationalStatus.Up);
        if (nic == null)
            return "UnknownDevice";

        return string.Join(":", nic.GetPhysicalAddress().GetAddressBytes().Select(b => b.ToString("X2")));
    }


    public ICommand LoginCommand => new RelayCommand(async _ =>
    {
        IsOauthLoading = true;
        var username = LoginEmail?.Trim();
        var password = LoginPassword?.Trim();

        if (string.IsNullOrEmpty(username) || string.IsNullOrEmpty(password))
        {
            MessageBox.Show("用户名或密码不能为空");
            return;
        }

        // 使用网卡 MAC 作为设备 ID
        var deviceId = GetMacAddress();
        var os = Environment.OSVersion.ToString();
        var deviceType = "PC";
        var deviceName = Environment.MachineName;

        try
        {
            var result = await App.apiService.LoginAsync(username, password, deviceId, os, deviceType, deviceName);
            if (result.Code == "0")
            {
                if (!string.IsNullOrEmpty(result.Data.ToString()))
                {
                    TokenManager.SetToken(result.Data.ToString());

                    IsOauthLoading = false;

                    Application.Current.Dispatcher.Invoke(() =>
                    {
                        var mainWindow = new MainWindowNew();
                        mainWindow.Show();

                        Application.Current.MainWindow = mainWindow;

                        _login.Close();
                    });
                }
            }
            else
            {
                MessageBox.Show(result.Msg);
            }
        }
        catch (Exception ex)
        {
            MessageBox.Show("登录异常：" + ex.Message);
        }
        finally {
            IsOauthLoading = false;
        }

    });


    public ICommand RegsiterCommand => new RelayCommand(async _ =>
    {
        var email = RegisterEmail?.Trim();
        var pwd = RegisterPassword?.Trim();
        var pwdc = RegisterConfirmPassword?.Trim();
        var rc = RegisterCode?.Trim();

        var pattern = @"^[^@\s]+@[^@\s]+\.[^@\s]+$";
        if (string.IsNullOrEmpty(email))
        {
            MessageBox.Show("邮箱不能为空!");
            return;
        }

        if (!Regex.IsMatch(email, pattern))
        {
            MessageBox.Show("邮箱格式不正确!");
            return;
        }
        if (string.IsNullOrWhiteSpace(pwd) || string.IsNullOrWhiteSpace(pwdc))
        {
            MessageBox.Show("密码不能为空!");
            return;
        }

        if(pwd != pwdc){
            MessageBox.Show("两次上输入密码不一致!");
            return;
        }

        if (string.IsNullOrWhiteSpace(rc))
        {
            MessageBox.Show("验证码不能为空!");
            return;
        }

        var codePattern = @"^\d+$";
        if (rc.Length != 6 || !Regex.IsMatch(rc, codePattern))
        {
            MessageBox.Show("验证码格式不正确!");
            return;
        }

        var deviceId = GetMacAddress();

        var result = await App.apiService.RegisterAsync(email, pwdc, deviceId, email, null, rc);
        if (result.Code == "0")
        {
            MessageBox.Show("注册成功!");
            LoginEmail = email;
            _login.PwdBox_Password_Set(pwd);
            RegisterEmail = null;
            _login.RegisterPwdBox_Password_clear();
            _login.RegisterConfirmPwdBox_Password_Clear();
            RegisterCode = null;
            CanSendCode = false;
            _countdown = 0;
            OnPropertyChanged(nameof(CodeCountdown));
            Mode = 1;
        }
        else
        {
            MessageBox.Show(result.Msg);
            CanSendCode = true;
            _countdown = 0;
            OnPropertyChanged(nameof(CodeCountdown));
        }
    });


    public ICommand ResetPasswordCommand => new RelayCommand(async _ =>
    {
        var email = ResetEmail?.Trim();
        var pwd = ResetPassword?.Trim();
        var pwdc = ResetConfirmPassword?.Trim();
        var rc = ResetCode?.Trim();

        var pattern = @"^[^@\s]+@[^@\s]+\.[^@\s]+$";
        if (string.IsNullOrEmpty(email))
        {
            MessageBox.Show("邮箱不能为空!");
            return;
        }

        if (!Regex.IsMatch(email, pattern))
        {
            MessageBox.Show("邮箱格式不正确!");
            return;
        }
        if (string.IsNullOrWhiteSpace(pwd) || string.IsNullOrWhiteSpace(pwdc))
        {
            MessageBox.Show("密码不能为空!");
            return;
        }

        if (pwd != pwdc)
        {
            MessageBox.Show("两次上输入密码不一致!");
            return;
        }

        if (string.IsNullOrWhiteSpace(rc))
        {
            MessageBox.Show("验证码不能为空!");
            return;
        }

        var codePattern = @"^\d+$";
        if (rc.Length != 6 || !Regex.IsMatch(rc, codePattern))
        {
            MessageBox.Show("验证码格式不正确!");
            return;
        }

        var deviceId = GetMacAddress();

        var result = await App.apiService.ResetPasswordAsync(email, pwdc, rc);
        if (result.Code == "0")
        {
            MessageBox.Show("密码重置成功!");
            LoginEmail = email;
            _login.PwdBox_Password_Set(pwd);
            ResetEmail = null;
            _login.ResetPwdBox_Password_Clear();
            _login.ResetConfirmPwdBox_Password_Clear();
            ResetCode = null;
            ResetCanSendCode = false;
            _resetCodeCountdown = 0;
            OnPropertyChanged(nameof(ResetCodeCountdown));
            Mode = 1;
        }
        else
        {
            MessageBox.Show(result.Msg);
            ResetCanSendCode = true;
            _resetCodeCountdown = 0;
            OnPropertyChanged(nameof(ResetCodeCountdown));
        }
    });

    //public ICommand RegisterCommand => new RelayCommand(_ =>
    //{
    //    // TODO: 校验邮箱、密码、验证码，提交注册
    //});

    public event PropertyChangedEventHandler? PropertyChanged;
    protected void OnPropertyChanged([CallerMemberName] string? name = null) =>
        PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(name));



    private bool _isOauthLoading;
    public bool IsOauthLoading
    {
        get => _isOauthLoading;
        set { _isOauthLoading = value; OnPropertyChanged(); }
    }

    private ICommand? _googleLoginCommand;
    public ICommand GoogleLoginCommand => _googleLoginCommand ??= new RelayCommand(async _ =>
    {
        await StartOauthLogin("google");
    });

    private ICommand? _xLoginCommand;
    public ICommand XLoginCommand => _xLoginCommand ??= new RelayCommand(async _ =>
    {
        await StartOauthLogin("x");
    });

    private ICommand? _facebookLoginCommand;
    public ICommand FacebookLoginCommand => _facebookLoginCommand ??= new RelayCommand(async _ =>
    {
        await StartOauthLogin("facebook");
    });

    private async Task StartOauthLogin(string provider)
    {
        try
        {
            IsOauthLoading = true;

            var deviceId = GetMacAddress();
            var os = Environment.OSVersion.ToString();
            var deviceType = "PC";
            var deviceName = Environment.MachineName;

            // 1. 请求创建授权登录任务
            var createResult = await App.apiService.CreateOauthLoginTaskAsync(provider, deviceId, os, deviceType, deviceName);
            if (createResult.Code != "0")
            {
                MessageBox.Show(createResult.Msg);
                IsOauthLoading = false;
                return;
            }


            var dataObj = createResult.Data as JObject;

            // 2. 打开浏览器访问授权登录页面
            var authUrl = dataObj["authUrl"]?.ToString();
            if (!string.IsNullOrWhiteSpace(authUrl))
            {
                Process.Start(new ProcessStartInfo
                {
                    FileName = authUrl,
                    UseShellExecute = true
                });
            }

            var taskId = dataObj["taskId"]?.ToString();
            if (string.IsNullOrEmpty(taskId))
            {
                MessageBox.Show("授权任务异常");
                IsOauthLoading = false;
                return;
            }

            // 3. 异步轮询授权结果
            bool isAuthorized = false;
            while (!isAuthorized)
            {
                await Task.Delay(2000); // 每2秒轮询一次
                var pollResult = await App.apiService.PollOauthLoginAsync(taskId);
                var pollObj = pollResult.Data as JObject;
                if (pollResult.Code == "0" && pollObj["status"]?.ToString() == "success")
                {
                    var token = pollObj["token"]?.ToString();
                    if (!string.IsNullOrEmpty(token))
                    {
                        TokenManager.SetToken(token);
                        Application.Current.Dispatcher.Invoke(() =>
                        {
                            var mainWindow = new MainWindowNew();
                            mainWindow.Show();
                            Application.Current.MainWindow = mainWindow;
                            _login.Close();
                        });
                        isAuthorized = true;
                    }
                }
                else if (pollObj["status"]?.ToString() == "failed")
                {
                    MessageBox.Show(pollObj["msg"]?.ToString());
                    break;
                }
            }
        }
        catch (Exception ex)
        {
            MessageBox.Show("授权登录异常：" + ex.Message);
        }
        finally
        {
            IsOauthLoading = false;
        }
    }
}
