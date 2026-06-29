using System.Linq;
using System.Net.Http;
using System.Net.NetworkInformation;
using System.Reactive.Disposables;
using System.Text;
using System.Text.RegularExpressions;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Controls.Primitives;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Threading;
using MaterialDesignThemes.Wpf;
using Newtonsoft.Json;
using ReactiveUI;
using Splat;
using v2rayN.Base;
using v2rayN.service;
using v2rayN.util;
using v2rayN.ViewModels;
using Windows.Services.Maps;
using static v2rayN.Views.ShareGift;
using Point = System.Windows.Point;

namespace v2rayN.Views;

public partial class Login
{

    private readonly LoginViewModel _vm;

    public Login()
    {
        InitializeComponent();
        ViewModel = new LoginViewModel(this);
        DataContext = ViewModel;
    }

    // 如果你保留 PasswordBox，需要手动同步密码到 ViewModel
    private void PwdBox_PasswordChanged(object sender, RoutedEventArgs e)
    {
        if (DataContext is LoginViewModel vm)
        {
            vm.LoginPassword = ((PasswordBox)sender).Password;
        }
    }

    public void PwdBox_Password_Set(string password)
    {
        LoginPassword.Password = password;
    }

    // 注册面板的密码同步（如果使用 PasswordBox 而不是绑定 Password）
    private void RegisterPwdBox_PasswordChanged(object sender, RoutedEventArgs e)
    {
        if (DataContext is LoginViewModel vm)
        {
            vm.RegisterPassword = ((PasswordBox)sender).Password;
        }
    }

    public void RegisterPwdBox_Password_clear()
    {
        RegisterPassword.Password = null;
    }

    private void RegisterConfirmPwdBox_PasswordChanged(object sender, RoutedEventArgs e)
    {
        if (DataContext is LoginViewModel vm)
        {
            vm.RegisterConfirmPassword = ((PasswordBox)sender).Password;
        }
    }

    public void RegisterConfirmPwdBox_Password_Clear()
    {
        RegisterConfirmPassword.Password = null;
    }

    private void ResetPwdBox_PasswordChanged(object sender, RoutedEventArgs e)
    {
        if (DataContext is LoginViewModel vm)
        {
            vm.ResetPassword = ((PasswordBox)sender).Password;
        }

    }

    public void ResetPwdBox_Password_Clear()
    {
        ResetPassword.Password = null;
    }

    private void ResetConfirmPwdBox_PasswordChanged(object sender, RoutedEventArgs e)
    {
        if (DataContext is LoginViewModel vm)
        {
            vm.ResetConfirmPassword = ((PasswordBox)sender).Password;
        }

    }

    public void ResetConfirmPwdBox_Password_Clear()
    {
        ResetConfirmPassword.Password = null;
    }

    private void RegisterEmail_LostFocus(object sender, RoutedEventArgs e)
    {
        if (DataContext is LoginViewModel vm)
        {
            var email = vm.RegisterEmail?.Trim();

            // 邮箱为空或格式不对
            string pattern = @"^[^@\s]+@[^@\s]+\.[^@\s]+$";
            if (string.IsNullOrEmpty(email) || !Regex.IsMatch(email, pattern))
            {
                vm.IsCodeVisible = false;
                return;
            }

            // 邮箱非空且格式正确，显示验证码输入框
            vm.IsCodeVisible = true;
        }
    }

    private void ResettEmail_LostFocus(object sender, RoutedEventArgs e)
    {
        if (DataContext is LoginViewModel vm)
        {
            var email = vm.ResetEmail?.Trim();

            // 邮箱为空或格式不对
            string pattern = @"^[^@\s]+@[^@\s]+\.[^@\s]+$";
            if (string.IsNullOrEmpty(email) || !Regex.IsMatch(email, pattern))
            {
                vm.IsResetCodeVisible = false;
                return;
            }

            // 邮箱非空且格式正确，显示验证码输入框
            vm.IsResetCodeVisible = true;
        }
    }
}
