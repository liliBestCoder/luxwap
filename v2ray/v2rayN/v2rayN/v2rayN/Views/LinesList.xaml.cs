using System.Diagnostics;
using System.Reactive.Disposables;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Controls.Primitives;
using System.Windows.Input;
using System.Windows.Interop;
using Newtonsoft.Json.Linq;
using ReactiveUI;
using ServiceLib.Handler.SysProxy;
using ServiceLib.Models;
using SkiaSharp;
using Splat;
using v2rayN.util;
using v2rayN.ViewModels;

namespace v2rayN.Views;

public partial class LinesList : ReactiveUserControl<LineListViewModel>
{

    private Config _config;
    private bool enbaleProxy;
    public LinesList()
    {

        _config = AppHandler.Instance.Config;
        InitializeComponent();

        // 初始化 ViewModel
        ViewModel = new LineListViewModel(this);

        // ReactiveUI 绑定，自动管理激活和清理
        this.WhenActivated(disposables =>
        {
            this.OneWayBind(ViewModel,
                    vm => vm.GroupedNodes,
                    v => v.GroupedNodesControl.ItemsSource)
                .DisposeWith(disposables);
        });


        InitSpeedBar();

        FilterMenu.AddHandler(MenuItem.ClickEvent, new RoutedEventHandler((s, e) =>
        {
            if (e.OriginalSource is MenuItem clickedItem && clickedItem.Parent is ContextMenu m)
            {
                foreach (var i in m.Items.OfType<MenuItem>())
                {
                    i.IsChecked = i == clickedItem;
                }

                var selectedLevel = clickedItem.Header.ToString();
                ViewModel.ApplyFilter(selectedLevel);
            }
        }));
    }

    public async void InitSpeedBar()
    {
        _config.GuiItem.EnableStatistics = true;
        if (_config.GuiItem.EnableStatistics || _config.GuiItem.DisplayRealTimeSpeed)
        {
            await StatisticsHandler.Instance.Init(_config, SpeedUpdateHandler);
        }
    }



    public async void SpeedUpdateHandler(ServerSpeedItem speed) {
        App.Current.Dispatcher.Invoke(() =>
        {
            if (enbaleProxy)
            {
                StatusBar.Text = $"↑ {speed.ProxyUp}kb/s  ↓ {speed.ProxyDown}kb/s";
            }
        });
    }


    private void FilterButton_Click(object sender, RoutedEventArgs e)
    {
        if (FilterButton.ContextMenu != null)
        {
            FilterButton.ContextMenu.PlacementTarget = FilterButton;
            FilterButton.ContextMenu.Placement = System.Windows.Controls.Primitives.PlacementMode.Bottom;
            FilterButton.ContextMenu.IsOpen = true;
        }
    }


    public void TriggerSwitchAsync()
    {
        SwitchBtn.IsChecked = true;
        SwitchBtn_Click(SwitchBtn, new RoutedEventArgs());
    }

    private async void RefreshButton_Click(object sender, RoutedEventArgs e)
    {
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
        await ConfigHandler.AddBatchServers(_config, strData, TokenManager.SubId, true);
        await ViewModel.LoadFromServerAsync();
    }



    private async void SwitchBtn_Click(object sender, RoutedEventArgs e)
    {
        if (ViewModel?.GroupedNodes == null)
        {
            SwitchBtn.IsChecked = false;
            return;
        }
           

        // 获取选中的节点
        var selectedNode = ViewModel.GroupedNodes
            .SelectMany(g => g.Nodes)
            .FirstOrDefault(n => n.IsSelected);

        var defaultServer = await ConfigHandler.GetDefaultServer(_config);

        if (selectedNode == null && defaultServer == null)
        {
            SwitchBtn.IsChecked = false;
            MessageBox.Show("请选择节点!");
            return;
        }

        ProfileItem selctProfile = null;

        if (selectedNode == null)
        {
            selectedNode = ViewModel.GroupedNodes
            .SelectMany(g => g.Nodes).FirstOrDefault(n => n.IndexId == defaultServer.IndexId);
            if (selectedNode == null)
            {
                SwitchBtn.IsChecked = false;
                return;
            }
            selectedNode.IsSelected = true;
            selctProfile = defaultServer;
        }
        else
        {
            var profileList = await AppHandler.Instance.ProfileItems(TokenManager.SubId);
            selctProfile = profileList.FirstOrDefault(p => p.IndexId == selectedNode.IndexId);
        }

        await ConfigHandler.SetDefaultServerIndex(_config, selctProfile.IndexId);

        enbaleProxy = SwitchBtn.IsChecked ?? false;
        await SaveProxyConfig(enbaleProxy);

        BlReloadEnabled = true;

        if (!enbaleProxy)
        {
            await CoreHandler.Instance.CoreStop();
            await SysProxyHandler.UpdateSysProxy(_config, false);
            await Task.Delay(100);
        }
        else
        {
            await Reload();

        }      
    }


    public bool BlReloadEnabled { get; set; }

    private bool _hasNextReloadJob = false;


    public async Task SaveProxyConfig(bool enbale) {
        _config.SystemProxyItem.SysProxyType = enbale ? ESysProxyType.ForcedChange : ESysProxyType.ForcedClear;
        await ConfigHandler.SaveConfig(_config);
        if (!enbale)
        {
            StatusBar.Text = "未连接";
        }
        else
        {
            StatusBar.Text = "已连接";
        }
    }


    public async Task Reload()
    {
        //If there are unfinished reload job, marked with next job.
        if (!BlReloadEnabled)
        {
            _hasNextReloadJob = true;
            return;
        }

        BlReloadEnabled = false;

        await Task.Run(async () =>
        {
            await LoadCore();
            await SysProxyHandler.UpdateSysProxy(_config, false);
            await Task.Delay(1000);
        });
        Locator.Current.GetService<StatusBarViewModel>()?.TestServerAvailability();

        //_updateView?.Invoke(EViewAction.DispatcherReload, null);

        BlReloadEnabled = true;
        if (_hasNextReloadJob)
        {
            _hasNextReloadJob = false;
            await Reload();
        }
    }

    private async Task LoadCore()
    {
        var node = await ConfigHandler.GetDefaultServer(_config);
        await CoreHandler.Instance.LoadCore(node);
    }

    private void Window_SizeChanged(object sender, SizeChangedEventArgs e)
    {
        // 假设你要让 ScrollViewer 填满除顶部控件之外的剩余空间
        LineListScrollViewer.Height = ActualHeight - 160;
        // 40 为边距或其他控件空间
    }


}




