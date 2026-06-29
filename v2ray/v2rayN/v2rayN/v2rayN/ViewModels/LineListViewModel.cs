using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Linq;
using System.Threading.Tasks;
using System.Windows;
using Newtonsoft.Json.Linq;
using ReactiveUI;
using ServiceLib.Models;
using ServiceLib.Services;
using v2rayN.util;
using v2rayN.Views;
using Windows.ApplicationModel.Resources;
using static v2rayN.Views.ShareGift;

namespace v2rayN.ViewModels;

// 单条线路信息
public class NodeInfo : ReactiveObject
{
    public string Name { get; set; } = "";
    public string Keyword { get; set; } = "";
    public string Region { get; set; } = "";

    public string IndexId { get; set; } = "";

    private bool _isSelected;
    public bool IsSelected
    {
        get => _isSelected;
        set => this.RaiseAndSetIfChanged(ref _isSelected, value);
    }

    private string _speed = "";
    public string Speed
    {
        get => _speed;
        set => this.RaiseAndSetIfChanged(ref _speed, value);
    }

    private string _speedUnit = "";
    public string SpeedUnit
    {
        get => _speedUnit;
        set => this.RaiseAndSetIfChanged(ref _speedUnit, value);
    }
}

// 按区域分组的线路
public class NodeGroup
{
    public string Region { get; set; } = "";
    public ObservableCollection<NodeInfo> Nodes { get; set; } = new();
}

public class LineListViewModel : ReactiveObject
{
    // 所有分组后的线路
    private ObservableCollection<NodeGroup> _groupedNodes = new();
    public ObservableCollection<NodeGroup> GroupedNodes
    {
        get => _groupedNodes;
        set => this.RaiseAndSetIfChanged(ref _groupedNodes, value);
    }

    private List<NodeInfo> _allNodes = new();
    private readonly Config _config;
    private readonly LinesList _view;
    private SpeedtestService _speedtestService;

    public LineListViewModel(LinesList view)
    {
        _view = view;
        _config = AppHandler.Instance.Config;
        // 构造函数中可以直接加载测试数据
        _ = LoadFromServerAsync();
    }


    private string _currentFilter = "all"; // 当前筛选等级（默认全部）

    public void ApplyFilter(string level)
    {
        _currentFilter = level.ToLower();

        IEnumerable<NodeInfo> filteredNodes = _allNodes;

        if (_currentFilter == "high")
        {
            filteredNodes = _allNodes.Where(n => n.Keyword.Contains("high", StringComparison.OrdinalIgnoreCase));
        }
        else if (_currentFilter == "medium")
        {
            filteredNodes = _allNodes.Where(n => n.Keyword.Contains("medium", StringComparison.OrdinalIgnoreCase));
        }
        else if (_currentFilter == "low")
        {
            filteredNodes = _allNodes.Where(n => n.Keyword.Contains("low", StringComparison.OrdinalIgnoreCase));
        }
        // "all" 就显示全部

        var grouped = filteredNodes
            .GroupBy(n => n.Region)
            .Select(g => new NodeGroup
            {
                Region = g.Key,
                Nodes = new ObservableCollection<NodeInfo>(g)
            });

        App.Current.Dispatcher.Invoke(() =>
        {
            GroupedNodes = new ObservableCollection<NodeGroup>(grouped);
        });
    }


    public async void SpeedUpdateHandler(SpeedTestResult result)
    {
        await Task.Delay(1000);
        await App.Current.Dispatcher.InvokeAsync(() =>
        {
            var node = GroupedNodes
                .SelectMany(g => g.Nodes)
                .FirstOrDefault(n => n.IndexId == result.IndexId);

            if (node != null)
            {
                node.Speed = result.Delay.ToString();
                if (node.Speed.Contains("测试"))
                {
                    node.SpeedUnit = "";
                }
                else
                {
                    node.SpeedUnit = " /ms";
                }
            }
        });
    }



    // 这里可以再加异步加载方法，例如从服务器获取线路信息
    public async Task LoadFromServerAsync()
    {
        var profileList = await AppHandler.Instance.ProfileItems(TokenManager.SubId);
        foreach (var item in profileList)
        {
            item.CoreType = ECoreType.Xray;
        }

        _speedtestService ??= new SpeedtestService(_config, SpeedUpdateHandler);
        _speedtestService?.RunLoop(ESpeedActionType.Realping, profileList);

        var defaultServer = await ConfigHandler.GetDefaultServer(_config);

        var nodes = profileList.Select(p =>
        {
            var parts = p.Remarks.Split(new[] { "@split@" }, StringSplitOptions.None);
            var regionCode = parts.ElementAtOrDefault(2) ?? "";
            var region = "中国";
            if (regionCode == "asia")
            {
                region = "亚洲";
            }
            else if(regionCode == "europe")
            {
                region = "欧洲";
            }
            else if (regionCode == "north_america")
            {
                region = "北美洲";
            }
            else if (regionCode == "south_america")
            {
                region = "南美洲";
            }

            var selected = false;

            if (defaultServer != null && defaultServer.IndexId == p.IndexId)
            {
                selected = true;
            }

            return new NodeInfo
                {
                    Name = parts.ElementAtOrDefault(0) ?? "",
                    Keyword = parts.ElementAtOrDefault(1) ?? "",
                    Region = region,
                    Speed = "0",
                    SpeedUnit = " /ms",
                    IndexId = p.IndexId,
                    IsSelected = selected
                };
        }).ToList();

        _allNodes = nodes;

        var grouped = nodes
            .GroupBy(n => n.Region)
            .Select(g => new NodeGroup
            {
                Region = g.Key,
                Nodes = new ObservableCollection<NodeInfo>(g)
            });

        // 注意 WPF 绑定必须在 UI 线程
        App.Current.Dispatcher.Invoke(() =>
        {
            GroupedNodes = new ObservableCollection<NodeGroup>(grouped);

            if (defaultServer != null)
            {
                _view.TriggerSwitchAsync();
            }
        });
    }
}
