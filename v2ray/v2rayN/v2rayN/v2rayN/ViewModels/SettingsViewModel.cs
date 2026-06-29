using System;
using System.Collections;
using System.Collections.ObjectModel;
using System.IO;
using System.Net;
using System.Net.Sockets;
using System.Reactive;
using System.Reactive.Linq;
using System.Security.Cryptography.X509Certificates;
using System.Windows;
using Downloader;
using Newtonsoft.Json.Linq;
using ReactiveUI;
using ServiceLib.Common;
using SkiaSharp;
using v2rayN.util;

namespace v2rayN.ViewModels;

public enum RoutingStrategy
{
    AsIs,
    IPIfNonMatch,
    IPOnDemand,
}

public enum UiLanguage
{
    ChineseSimplified,
    ChineseTraditional,
    English
}


public class SettingsViewModel : ReactiveObject
{

    public static readonly SemaphoreSlim _fileLock = new SemaphoreSlim(1, 1);
    // ----- Commands -----
    public ReactiveCommand<Unit, Unit> UpdateGeoCommand { get; }
    public ReactiveCommand<Unit, Unit> EnterEditModeForeignDnsCommand { get; }
    public ReactiveCommand<Unit, Unit> ExitEditModeForeignDnsCommand { get; }

    public ReactiveCommand<Unit, Unit> EnterEditModeInnerCountryDnsCommand { get; }
    public ReactiveCommand<Unit, Unit> ExitEditModeInnerCountryDnsCommand { get; }

    public ReactiveCommand<Unit, Unit> EnterEditModeGlobalDnsCommand { get; }
    public ReactiveCommand<Unit, Unit> ExitEditModeGlobalDnsCommand { get; }

// ----- 1. 更新 Geo 按钮（命令已上） -----
// UpdateGeoCommand

    // ----- 2. 路由策略 -----
    private RoutingStrategy _routingStrategy = RoutingStrategy.AsIs;
    public RoutingStrategy RoutingStrategy
    {
        get => _routingStrategy;
        set => this.RaiseAndSetIfChanged(ref _routingStrategy, value);
    }

    // 可用于下拉绑定的数据源
    public ReadOnlyObservableCollection<RoutingStrategy> RoutingStrategyOptions { get; }

    // ----- 3. 绕过中国 IP 开关 -----
    private bool _passByIp = true;
    public bool PassByIp
    {
        get => _passByIp;
        set => this.RaiseAndSetIfChanged(ref _passByIp, value);
    }

    // ----- 4. 绕过中国域名 开关 -----
    private bool  _passByDomain = true;
    public bool PassByDomain
    {
        get => _passByDomain;
        set => this.RaiseAndSetIfChanged(ref _passByDomain, value);
    }

    // ----- 5. 绕过局域网 IP 开关 -----
    private bool _passByVlanIp = true;
    public bool PassByVlanIp
    {
        get => _passByVlanIp;
        set => this.RaiseAndSetIfChanged(ref _passByVlanIp, value);
    }

    // ----- 6. 绕过局域网域名 开关 -----
    private bool _passByVlanDomain = true;
    public bool PassByVlanDomain
    {
        get => _passByVlanDomain;
        set => this.RaiseAndSetIfChanged(ref _passByVlanDomain, value);
    }

    // ----- 7. 阻断广告 开关 -----
    private bool _blockAds = true;
    public bool BlockAds
    {
        get => _blockAds;
        set => this.RaiseAndSetIfChanged(ref _blockAds, value);
    }

    private bool _vpnDns = true;
    public bool VpnDns
    {
        get => _vpnDns;
        set => this.RaiseAndSetIfChanged(ref _vpnDns, value);
    }

    private string _geoDownProgress = "";
    public string GeoDownProgress
    {
        get => _geoDownProgress;
        set => this.RaiseAndSetIfChanged(ref _geoDownProgress, value);
    }

    // ----- 8. VPN 内置 DNS 端口号 -----
    private int _vpnDnsPort = 10853;
    public int VpnDnsPort
    {
        get => _vpnDnsPort;
        set
        {
            if (value < 1)
                value = 1;
            if (value > 65535)
                value = 65535;
            this.RaiseAndSetIfChanged(ref _vpnDnsPort, value);
        }
    }

    // ----- 9. 境外流量 DNS -----
    private string _foreignDns = "8.8.8.8";
    public string ForeignDns
    {
        get => _foreignDns;
        set => this.RaiseAndSetIfChanged(ref _foreignDns, value);
    }


    private bool _isEditingForeignDns = false;

    public bool IsEditingForeignDns
    {
        get => _isEditingForeignDns;
        set => this.RaiseAndSetIfChanged(ref _isEditingForeignDns, value);
    }

    private string _innerCountryDns = "233.5.5.5";
    public string InnerCountryDns
    {
        get => _innerCountryDns;
        set => this.RaiseAndSetIfChanged(ref _innerCountryDns, value);
    }

    private bool _isEditingInnerCountryDns = false;

    public bool IsEditingInnerCountryDns
    {
        get => _isEditingInnerCountryDns;
        set => this.RaiseAndSetIfChanged(ref _isEditingInnerCountryDns, value);
    }

    private string _globalDns = "8.8.8.8";
    public string GlobalDns
    {
        get => _globalDns;
        set => this.RaiseAndSetIfChanged(ref _globalDns, value);
    }

    private bool _isEditingGlobalDns = false;

    public bool IsEditingGlobalDns
    {
        get => _isEditingGlobalDns;
        set => this.RaiseAndSetIfChanged(ref _isEditingGlobalDns, value);
    }
    // ----- 10. 语言下拉 -----
    private UiLanguage _language = UiLanguage.ChineseSimplified;
    public UiLanguage Language
    {
        get => _language;
        set => this.RaiseAndSetIfChanged(ref _language, value);
    }

    public ReadOnlyObservableCollection<UiLanguage> LanguageOptions { get; }


    async Task<bool> SafeDownloadAsync(string url, string destFile, string tmpFile, IWebProxy? proxy, IProgress<double>? progress = null, int timeoutSeconds = 30)
    {
        try
        {
            await DownloaderHelper.Instance.DownloadFileAsync(proxy, url, tmpFile, progress, timeoutSeconds);

            if (File.Exists(destFile))
                File.Delete(destFile);

            File.Move(tmpFile, destFile);
            return true;
        }
        catch
        {
            GeoDownProgress = "error";
            if (File.Exists(tmpFile))
                File.Delete(tmpFile);
            return false;
        }
    }

    private bool IsPortOpen(int port)
    {
        try
        {
            using (var client = new TcpClient())
            {
                var task = client.ConnectAsync("127.0.0.1", port);
                if (task.Wait(500)) // 等 500ms，看是否能连上
                    return true;
            }
        }
        catch
        {
            // 端口不可用
        }
        return false;
    }


    // ctor
    public SettingsViewModel()
    {
        // Build readonly list wrappers for combo boxes
        var routingList = new ObservableCollection<RoutingStrategy>((RoutingStrategy[])Enum.GetValues(typeof(RoutingStrategy)));
        RoutingStrategyOptions = new ReadOnlyObservableCollection<RoutingStrategy>(routingList);

        var langList = new ObservableCollection<UiLanguage>((UiLanguage[])Enum.GetValues(typeof(UiLanguage)));
        LanguageOptions = new ReadOnlyObservableCollection<UiLanguage>(langList);

        // Commands
        UpdateGeoCommand = ReactiveCommand.CreateFromTask(async () =>
        {
            var ipProgress = new Progress<double>(p =>
            {
                if (p <= 100)
                {
                    GeoDownProgress = $"geoip {p}%";
                }
            });

            var sitProgress = new Progress<double>(p =>
            {
                if (p <= 100)
                    GeoDownProgress = $"geosit {p}%";
                else {
                    MessageBox.Show("geo更新完成");
                    GeoDownProgress = "";
                }
            });

            var port = AppHandler.Instance.Config.Inbound[0].LocalPort;
            IWebProxy? proxy = IsPortOpen(port) ? new WebProxy($"http://127.0.0.1:{port}") : null;
            var geoipFile = Utils.GetBinPath("geoip.dat");
            var geositeFile = Utils.GetBinPath("geosite.dat");

            var tempgeoipFile = Utils.GetBinPath($"geoip.dat.{Guid.NewGuid()}.tmp");
            var tempgeositeFile = Utils.GetBinPath($"geosite.dat.{Guid.NewGuid()}.tmp");

            await SafeDownloadAsync("https://github.com/Loyalsoldier/v2ray-rules-dat/releases/latest/download/geoip.dat", geoipFile, tempgeoipFile, proxy, ipProgress);
            await SafeDownloadAsync("https://github.com/Loyalsoldier/v2ray-rules-dat/releases/latest/download/geosite.dat", geositeFile, tempgeositeFile, proxy, sitProgress);
           
        });

        EnterEditModeForeignDnsCommand = ReactiveCommand.Create(() => { IsEditingForeignDns = true; });
        ExitEditModeForeignDnsCommand = ReactiveCommand.Create(() => { IsEditingForeignDns = false; });

        EnterEditModeInnerCountryDnsCommand = ReactiveCommand.Create(() => { IsEditingInnerCountryDns = true; });
        ExitEditModeInnerCountryDnsCommand = ReactiveCommand.Create(() => { IsEditingInnerCountryDns = false; });

        EnterEditModeGlobalDnsCommand = ReactiveCommand.Create(() => { IsEditingGlobalDns = true; });
        ExitEditModeGlobalDnsCommand = ReactiveCommand.Create(() => { IsEditingGlobalDns = false; });


        this.WhenAnyValue(
              vm => vm.RoutingStrategy,
              vm => vm.PassByIp,
              vm => vm.PassByDomain,
              vm => vm.PassByVlanIp,
              vm => vm.PassByVlanDomain,
              vm => vm.BlockAds)
              .Throttle(TimeSpan.FromMilliseconds(400))
              .Subscribe(async _ =>
              {
                  var routeItems = await AppHandler.Instance.RoutingItems();
                  var lstRules = JsonUtils.Deserialize<List<RulesItem>>(routeItems[0].RuleSet);
                  routeItems[0].DomainStrategy = RoutingStrategy.ToString();

                  foreach (var item in lstRules)
                  {
                      if (item.Remarks == "passByIp")
                      {
                          item.Enabled = PassByIp;
                      }
                      else if (item.Remarks == "passByDomain")
                      {
                          item.Enabled = PassByDomain;
                      }
                      else if (item.Remarks == "passByVlanIp")
                      {
                          item.Enabled = PassByVlanIp;
                      }
                      else if (item.Remarks == "passByVlanDomain")
                      {
                         item.Enabled = PassByVlanDomain;
                      }
                      else if (item.Remarks == "blockAds")
                      {
                          item.Enabled = BlockAds;
                      }
                  }  
                  routeItems[0].RuleSet = JsonUtils.Serialize(lstRules);
                  await ConfigHandler.SaveRoutingItem(AppHandler.Instance.Config, routeItems[0]);

              });


        this.WhenAnyValue(
               vm => vm.VpnDns,
               vm => vm.VpnDnsPort,
               vm => vm.ForeignDns,
               vm => vm.InnerCountryDns,
               vm => vm.GlobalDns)
               .Throttle(TimeSpan.FromMilliseconds(400))
               .Subscribe(async _ =>
               {
                   var dnsItem = await AppHandler.Instance.GetDNSItem(ECoreType.Xray);
                   var region = TokenManager.UserInfo["country"]?.ToString().ToLower();

                   var geoip = $"geoip:{region}";
                   var geosite = "cn" == region ? $"geosite:{region}" : "";

                   var geoipNon = $"geoip:!{region}";
                   var geositeNon = "cn" == region ? $"geosite:!{region}" : "";


                   if (VpnDns)
                   {
                       var dnsLit = new JArray();
                  
                       var foreignDnsJson = new JObject
                       {
                           ["address"] = ForeignDns,
                           ["skipFallback"] = true,
                           ["expectIPs"] = new JArray(geoipNon),
                           ["outboundTag"] = "proxy" // 排斥国内 IP
                       };

                       if (!string.IsNullOrEmpty(geositeNon))
                       {
                           foreignDnsJson["domains"] = new JArray(geositeNon);
                       }

                       dnsLit.Add(foreignDnsJson);

                       var innerDnsJson = new JObject
                       {
                           ["address"] = InnerCountryDns,
                           ["skipFallback"] = true,
                           ["expectIPs"] = new JArray(geoip),
                           ["outboundTag"] = "direct"
                       };

                       if (!string.IsNullOrEmpty(geosite))
                       {
                           innerDnsJson["domains"] = new JArray(geosite);
                       }

                       dnsLit.Add(innerDnsJson);


                       dnsLit.Add(GlobalDns);
                       dnsLit.Add("https://dns.google/dns-query");
                       dnsLit.Add("https://doh.pub/dns-query");

                       var dns = new JObject
                       {
                           ["server"] = dnsLit,
                           ["vpnDns"] = VpnDns,
                           ["vpnDnsPort"] = VpnDnsPort,
                           ["foreignDns"] = ForeignDns,
                           ["innerCountryDns"] = InnerCountryDns,
                           ["globalDns"] = GlobalDns
                       };

                       dnsItem.NormalDNS = dns.ToString();
                   }
                   else
                   {
                       dnsItem.NormalDNS = "{}";
                   }

                   await ConfigHandler.SaveDNSItems(AppHandler.Instance.Config, dnsItem);
               });
    }

    // 可按需添加将 ViewModel 转为配置模型的方法
}


