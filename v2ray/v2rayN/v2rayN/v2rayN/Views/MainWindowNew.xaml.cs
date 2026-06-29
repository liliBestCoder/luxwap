using System;
using System.Collections.ObjectModel;
using System.ComponentModel;
using System.Diagnostics;
using System.Linq;
using System.Reactive.Disposables;
using System.Runtime.ConstrainedExecution;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Input;
using System.Windows.Interop;
using System.Windows.Media;
using System.Windows.Threading;
using Hardcodet.Wpf.TaskbarNotification;
using MaterialDesignThemes.Wpf;
using Newtonsoft.Json.Linq;
using ReactiveUI;
using ServiceLib.Handler.SysProxy;
using Splat;
using Typography.OpenFont.Tables;
using v2rayN.Handler;
using v2rayN.util;
using v2rayN.ViewModels;
using static QRCoder.PayloadGenerator;

namespace v2rayN.Views;

public partial class MainWindowNew
{
    private static Config _config;

    private static string _subId;

    private LinesList _linesListPage;
    private PersonalCenter _personalPage;
    private Settings _settingsPage;
    private About _aboutPage;
    private ShareGift _activityPage;
    private TradeManager _tradeManagerPage;

    public MainWindowNew()
    {
        InitializeComponent();
        _config = AppHandler.Instance.Config;

        ShowUserInfoContainer();

        InitApp();

        Loaded += MainWindowNew_Loaded;
    }

    private void MainWindowNew_Loaded(object sender, RoutedEventArgs e) {
        Closing += Window_Closing;
    }

    private void UpdateHandler(bool notify, string msg)
    {

        Debug.WriteLine(msg);
    }

    public async void InitApp()
    {
        await InitUserInfo();
        await ConfigHandler.InitBuiltinRouting(config:_config, region: TokenManager.UserInfo["country"].ToString().ToLower());
        await ConfigHandler.InitBuiltinDNS(_config);
        await ProfileExHandler.Instance.Init();
        await CoreHandler.Instance.Init(_config, UpdateHandler);
        TaskHandler.Instance.RegUpdateTask(_config, UpdateHandler);

        App.Current.Dispatcher.Invoke(() =>
        {
            if (_linesListPage == null)
            {
                _linesListPage = new LinesList();
            }
            ContentFrame.Navigate(_linesListPage);
        });
    }

    public async Task InitSub(){
        _subId = Utils.GetGuid(false);
        TokenManager.SetSubId(_subId);

        var lineResult = await App.apiService.LineListAsync(TokenManager.Token);
        if (lineResult.Code != "0")
        {
            MessageBox.Show(lineResult.Msg);
            return;
        }

        var lineList = JArray.Parse(lineResult.Data.ToString());
        var strData = "";
        foreach (var line in lineList)
        {
            strData += line.ToString().Replace("${uuid}", TokenManager.UserInfo["uuid"]?.ToString()) + "\n";
        }


        var sub = new SubItem()
        {
            Id = _subId,
            Remarks = "我的订阅",
            Url = "",
            AutoUpdateInterval = 0,
            Enabled = true
        };

        await ConfigHandler.AddSubItem(_config, sub);

        await ConfigHandler.AddBatchServers(_config, strData, _subId, true);
    }

    public async Task InitUserInfo()
    {
        var userInfo = await App.apiService.GetUserInfoAsync(TokenManager.Token);

        // 检查返回的用户信息
        if (userInfo != null && userInfo.Code == "0")
        {
            var jsonObject = JObject.Parse(userInfo.Data.ToString());
            TokenManager.SetUserInfo(jsonObject);
        }

        DataContext = TokenManager.UserInfo;
        await InitSub();
    }


    private void NavListBox_SelectionChanged(object sender, EventArgs e)
    {
        string selected = null;
        if (sender is ListBox listBox && listBox.SelectedItem is ListBoxItem selectedItem)
        {
            selected = selectedItem.Tag.ToString();
        }
        else if (sender is Button btn)
        {
            selected = btn.Tag.ToString();
        }

        switch (selected)
        {
            case "map":
                ShowUserInfoContainer();
                if (_linesListPage == null)
                    _linesListPage = new LinesList();
                ContentFrame.Navigate(_linesListPage);
                break;
            case "personal":
                ShowUserInfoContainer();
                if (_personalPage == null)
                    _personalPage = new PersonalCenter();
                ContentFrame.Navigate(_personalPage);
                break;
            case "settings":
                ShowUserInfoContainer();
                if (_settingsPage == null)
                    _settingsPage = new Settings();
                ContentFrame.Navigate(_settingsPage);
                break;
            case "help":
                new DNSSettingWindow().Show();
                break;
            case "about":
                HideUserInfoContainer();
                if (_aboutPage == null)
                    _aboutPage = new About();
                ContentFrame.Navigate(_aboutPage);
                break;
            case "activity":
                HideUserInfoContainer();
                if (_activityPage == null)
                    _activityPage = new ShareGift();
                ContentFrame.Navigate(_activityPage);
                break;
            case "tradeManager":
                ShowUserInfoContainer();
                if (_tradeManagerPage == null)
                    _tradeManagerPage = new TradeManager();
                ContentFrame.Navigate(_tradeManagerPage);
                break;
        }
    }

    private void HideUserInfoContainer() {
        UserInfoContainer.Visibility = Visibility.Collapsed;
    }

    private void ShowUserInfoContainer()
    {
        UserInfoContainer.Visibility = Visibility.Visible;
    }


    private bool _isExit;


    private void Window_Closing(object sender, System.ComponentModel.CancelEventArgs e)
    {
        if (!_isExit)
        {
            e.Cancel = true; 
            Hide();      
        }
    }

    private void Menu_Open_Click(object sender, RoutedEventArgs e)
    {
        Show();
        WindowState = WindowState.Normal;
        Activate();
    }

    private async void Menu_Logout_Click(object sender, RoutedEventArgs e)
    {
        TokenManager.ClearToken();
        TokenManager.SetUserInfo(null);
        TokenManager.SetSubId(null);
        await Stop();
        var loginWindow = new Login();
        loginWindow.Show();
        Hide();
    }


    private async Task<int> Stop()
    {
        var _config = AppHandler.Instance.Config;
        _config.SystemProxyItem.SysProxyType = ESysProxyType.ForcedClear;
        await ConfigHandler.SaveConfig(_config);

        await CoreHandler.Instance.CoreStop();
        await SysProxyHandler.UpdateSysProxy(_config, false);
        await Task.Delay(100);
        return 1;
    }

    private async void Menu_Exit_Click(object sender, RoutedEventArgs e)
    {
        _isExit = true;
        TrayIcon.Dispose();   // 移除托盘图标
        await Stop();
        Application.Current.Shutdown();
    }
}
