using System;
using System.Reactive.Disposables;
using System.Text.RegularExpressions;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Controls.Primitives;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Threading;
using MaterialDesignThemes.Wpf;
using ReactiveUI;
using ServiceLib.ViewModels;
using Splat;
using v2rayN.Base;
using v2rayN.Common;
using v2rayN.service;
using v2rayN.util;
using v2rayN.ViewModels;
using static System.Windows.Forms.VisualStyles.VisualStyleElement.ListView;
using static QRCoder.PayloadGenerator;
using static v2rayN.Common.CountryHelper;
using Point = System.Windows.Point;

namespace v2rayN.Views;

public partial class PersonalCenter
{

    private List<CountryHelper.CountryItem> _countries;
    private bool _isInitialized = false;

    public PersonalCenter()
    {
        InitializeComponent();
        DataContext = TokenManager.UserInfo;
        Loaded += UserControl_Loaded;

        _countries = CountryHelper.GetCountries();
        CountryComboBox.ItemsSource = _countries;

        var userCountryCode = TokenManager.UserInfo?["country"]?.ToString();
        var countryTuple = _countries.FirstOrDefault(c =>
        c.Code.Equals(userCountryCode ?? "", StringComparison.OrdinalIgnoreCase));
        if (countryTuple != null)
            Country.Text = countryTuple.Name;

        this.PreviewMouseDown += Window_PreviewMouseDown;
    }

  
    private void UserControl_Loaded(object sender, RoutedEventArgs e)
    {
        Password.Password = TokenManager.UserInfo["password"]?.ToString();
        _isInitialized = true;
    }


    private void Copy_Image_MouseLeftButtonDown(object sender, MouseButtonEventArgs e)
    {
        var uuid = TokenManager.UserInfo["uuid"]?.ToString();
     

        // 使用 Dispatcher 强制在 UI 线程中执行
        Application.Current.Dispatcher.Invoke(() =>
        {
            Clipboard.SetDataObject(uuid);

            // 在这里处理点击事件的逻辑
            MessageBox.Show("用户ID已复制");
        });


        
    }

    // 点击“编辑昵称”按钮
    private void EditUserName_Click(object sender, RoutedEventArgs e)
    {
        // 隐藏显示的TextBlock
        UserName.Visibility = Visibility.Collapsed;

        // 显示编辑面板
        UserNameEditPanel.Visibility = Visibility.Visible;

        // 把原来的昵称放到TextBox
        UserNameTextBox.Text = UserName.Text;

        // 自动聚焦
        UserNameTextBox.Focus();
        UserNameTextBox.SelectAll();
    }

    // TextBox失去焦点
    private void UserNameTextBox_LostFocus(object sender, RoutedEventArgs e)
    {
        SaveUserName();
    }

    // 保存逻辑
    private async Task SaveUserName()
    {
        var nick = UserNameTextBox.Text;
        if (!string.IsNullOrEmpty(nick) && nick != TokenManager.UserInfo["nick"]?.ToString())
        {
            var result = await App.apiService.UpdateUserInfoAsync(nick, null, null, null, TokenManager.Token);

            if (result.Code == "0")
            {
                MessageBox.Show("昵称修改成功!");
                // 把TextBox内容赋给TextBlock
                TokenManager.UserInfo["nick"] = UserName.Text = UserNameTextBox.Text;
            }
            else
            {
                MessageBox.Show(result.Msg);

            }
        }
        // 隐藏编辑面板，显示TextBlock
        UserNameEditPanel.Visibility = Visibility.Collapsed;
        UserName.Visibility = Visibility.Visible;
    }


    // 控制显示/隐藏邮箱编辑面板
    private void EditEmail_Click(object sender, RoutedEventArgs e)
    {
        Email.Visibility = Visibility.Collapsed;            // 隐藏显示邮箱的 TextBlock
        EmailEditPanel.Visibility = Visibility.Visible;    // 显示编辑面板
    }

    private void EditRegion_Click(object sender, RoutedEventArgs e)
    {
        Country.Visibility = Visibility.Collapsed;            // 隐藏显示国家的 TextBlock
        RegionEditPanel.Visibility = Visibility.Visible;    // 显示编辑面板
    }

    private void Window_PreviewMouseDown(object sender, MouseButtonEventArgs e)
    {
        var clickedElement = e.OriginalSource as DependencyObject;

        // 如果点击的不是 CountryComboBox 自身或其子元素
        if (!IsDescendantOf(clickedElement, CountryComboBox) && !CountryComboBox.IsDropDownOpen)
        {
            Country.Visibility = Visibility.Visible;
            RegionEditPanel.Visibility = Visibility.Collapsed;
        }

        // 如果点击的不是 CountryComboBox 自身或其子元素
        if (!IsDescendantOf(clickedElement, NewEmailTextBox) && !IsDescendantOf(clickedElement, VerificationCodeTextBox) && !IsDescendantOf(clickedElement, SendCode))
        {
            Email.Visibility = Visibility.Visible;
            EmailEditPanel.Visibility = Visibility.Collapsed;
        }
    }

    // 判断 child 是否在 parent 下面（递归上溯）
    private bool IsDescendantOf(DependencyObject child, DependencyObject parent)
    {
        while (child != null)
        {
            if (child == parent)
                return true;
            child = VisualTreeHelper.GetParent(child);
        }
        return false;
    }

    private async void Region_ComboBox_Chanaged(object sender, RoutedEventArgs e) {
        if (!_isInitialized)
            return;
        var tb = sender as ComboBox;
        // ⚠️ 如果下拉框还在打开状态，就不要处理
        if (tb == CountryComboBox && !string.IsNullOrWhiteSpace(tb.SelectedValue.ToString()))
        {
            var selectedCountryCode = tb.SelectedValue.ToString();
            if (selectedCountryCode.ToLower() == TokenManager.UserInfo?["country"]?.ToString())
            {
                MessageBox.Show("国家未发生变更!");
                return;
            }else{

                var result = await App.apiService.UpdateUserInfoAsync(null, selectedCountryCode, null, null, TokenManager.Token);

                if (result.Code == "0")
                {
                    TokenManager.UserInfo["country"] = selectedCountryCode;
                    var selected = CountryComboBox.SelectedItem as CountryItem;
                    if (selected != null)
                        Country.Text = selected.Name;

                    // ✅ 提示需要重启
                    var dialogResult = MessageBox.Show("修改国家后需要重新启动程序才能生效。\n是否立即重启？",
                                                       "重启提示",
                                                       MessageBoxButton.YesNo,
                                                       MessageBoxImage.Question);

                    if (dialogResult == MessageBoxResult.Yes)
                    {
                        RestartApplication();
                    }
                }
                else
                {
                    MessageBox.Show(result.Msg);

                }

            }
        }

        Country.Visibility = Visibility.Visible;
        RegionEditPanel.Visibility = Visibility.Collapsed;
    }


    private void RestartApplication()
    {
        try
        {
            // 获取当前可执行文件路径
            string exePath = System.Diagnostics.Process.GetCurrentProcess().MainModule.FileName;

            // 启动新的进程
            System.Diagnostics.Process.Start(new System.Diagnostics.ProcessStartInfo
            {
                FileName = exePath,
                UseShellExecute = true, // 让新进程正常继承 UI 权限
                WorkingDirectory = AppDomain.CurrentDomain.BaseDirectory
            });

            // 优雅退出当前程序
            System.Windows.Application.Current.Shutdown();
        }
        catch (Exception ex)
        {
            MessageBox.Show($"重启失败: {ex.Message}");
        }
    }

    private void RegionComboBox_DropDownClosed(object sender, EventArgs e)
    {
        // 不管选没选，都恢复显示状态
        Country.Visibility = Visibility.Visible;
        RegionEditPanel.Visibility = Visibility.Collapsed;
    }


    private void TextBox_LostFocus(object sender, RoutedEventArgs e)
    {
        var tb = sender as TextBox;
        if (tb == NewEmailTextBox && string.IsNullOrWhiteSpace(tb.Text))
        {
        }
        else if (tb == VerificationCodeTextBox && string.IsNullOrWhiteSpace(tb.Text))
        {
        }

        Dispatcher.BeginInvoke(new Action(() =>
        {
            // 获取当前焦点元素
            var focusedElement = Keyboard.FocusedElement as FrameworkElement;

            // 如果焦点在文本框或按钮内，就不要隐藏
            if (focusedElement == NewEmailTextBox || focusedElement == VerificationCodeTextBox || focusedElement == SendCode)
            {
                return;
            }

            // 双文本框同时失去焦点并且不是点击按钮
            if (!NewEmailTextBox.IsFocused && !VerificationCodeTextBox.IsFocused)
            {
                var email = NewEmailTextBox.Text;
                var code = VerificationCodeTextBox.Text;

                if (!string.IsNullOrWhiteSpace(email) && !string.IsNullOrWhiteSpace(code))
                {
                    SaveEmail_Click(null, new RoutedEventArgs());
                }

                Email.Visibility = Visibility.Visible;
                EmailEditPanel.Visibility = Visibility.Collapsed;
            }
        }), System.Windows.Threading.DispatcherPriority.Background);
    }


    private bool codeDown = true;

    // 点击发送验证码按钮
    private async void SendCode_Click(object sender, RoutedEventArgs e)
    {
        var email = NewEmailTextBox.Text;
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

        if (email.ToLower() == TokenManager.UserInfo["username"]?.ToString().ToLower())
        {
            MessageBox.Show("邮箱未发生改变！");
            return;
        }

        var result = await App.apiService.SendCodeAsync(email, TokenManager.Token);
        if (result.Code == "0")
        {
            var count = 300;
            SendCode.Content = $"{count}s";
            SendCode.IsEnabled = false;

            codeDown = true;

            // 倒计时
            await Task.Run(async () =>
            {
                while (count > 0 && codeDown)
                {
                    await Task.Delay(1000);
                    count--;
                    App.Current.Dispatcher.Invoke(() =>
                    {
                        SendCode.Content = $"{count}s";
                    });
                }

                App.Current.Dispatcher.Invoke(() =>
                {
                    SendCode.IsEnabled = true;
                    SendCode.Content = "验证码";
                });
               
            });
        }
        else
        {
            MessageBox.Show(result.Msg);
        }
    }

    // 点击确认修改
    private async void SaveEmail_Click(object sender, RoutedEventArgs e)
    {
        var email = NewEmailTextBox.Text;
        var code = VerificationCodeTextBox.Text;

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

        if (email.ToLower() == TokenManager.UserInfo["username"]?.ToString().ToLower())
        {
            MessageBox.Show("邮箱未发生改变！");
            return;
        }

        if (string.IsNullOrWhiteSpace(code))
        {
            MessageBox.Show("请输入验证码");
            return;
        }

        var codePattern = @"^\d+$";
        if (code.Length != 6 || !Regex.IsMatch(code, codePattern))
        {
            MessageBox.Show("验证码格式不正确!");
            return;
        }

        var result =  await App.apiService.UpdateUserInfoAsync(null, null, email, code, TokenManager.Token);

        if (result.Code == "0")
        {
            MessageBox.Show("邮箱修改成功!");
            // 把TextBox内容赋给TextBlock
            TokenManager.UserInfo["username"] = Email.Text = email;
            Email.Visibility = Visibility.Visible;
            EmailEditPanel.Visibility = Visibility.Collapsed;
        }
        else
        {
            MessageBox.Show(result.Msg);

        }

        codeDown = false;
    }


    private async void ChangePasswordButton_Click(object sender, RoutedEventArgs e)
    {
        var _control = new ChangePasswordControl();
        var _viewModel = new ChangePasswordViewModel();
        _control.DataContext = _viewModel;

        _control.PasswordChanged += async (oldPwd, newPwd) =>
        {
            var result = await App.apiService.ChangePasswordAsync(oldPwd, newPwd, TokenManager.Token);
            if (result.Code == "0")
            {
                Password.Password = (string)result.Data;
                MessageBox.Show("密码修改成功!");
            }
            else
            {
                MessageBox.Show(result.Msg);
            }
        };
        await DialogHost.Show(_control, "DialogHostMain");
    }





}
