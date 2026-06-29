using System.IO;
using System.Reactive.Disposables;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Controls.Primitives;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Threading;
using MaterialDesignThemes.Wpf;
using Newtonsoft.Json.Linq;
using ReactiveUI;
using Splat;
using v2rayN.Base;
using v2rayN.util;
using v2rayN.ViewModels;
using Point = System.Windows.Point;
using RoutingStrategy = v2rayN.ViewModels.RoutingStrategy;

namespace v2rayN.Views;

public partial class Settings
{
 
    public Settings()
    {
        InitializeComponent();

        ViewModel = new SettingsViewModel();

        DataContext = ViewModel;


        this.WhenActivated(d =>
        {
            // 这里可做额外绑定或命令参数绑定
        });

        Loaded += UserControl_Loaded;
    }

    private void UserControl_Loaded(object sender, RoutedEventArgs e)
    {

        Application.Current.Dispatcher.Invoke(async() =>
        {
            var routeItems = await AppHandler.Instance.RoutingItems();
            var lstRules = JsonUtils.Deserialize<List<RulesItem>>(routeItems[0].RuleSet);

            ViewModel.RoutingStrategy = Enum.Parse<RoutingStrategy>((string)routeItems[0].DomainStrategy);

            foreach (var item in lstRules)
            {
                if (item.Remarks == "passByIp")
                {
                    ViewModel.PassByIp = item.Enabled;
                }
                else if (item.Remarks == "passByDomain")
                {
                    ViewModel.PassByDomain = item.Enabled;
                }
                else if (item.Remarks == "passByVlanIp")
                {
                    ViewModel.PassByVlanIp = item.Enabled;
                }
                else if (item.Remarks == "passByVlanDomain")
                {
                    ViewModel.PassByVlanDomain = item.Enabled;
                }
                else if (item.Remarks == "blockAds")
                {
                    ViewModel.BlockAds = item.Enabled;
                }
            }

            await SettingsViewModel._fileLock.WaitAsync();
            try
            {
                var dnsItem = await AppHandler.Instance.GetDNSItem(ECoreType.Xray);
                var dnfconfigObj = JObject.Parse(dnsItem.NormalDNS);
                ViewModel.VpnDns = (bool)dnfconfigObj["vpnDns"];
                ViewModel.VpnDnsPort = (int)dnfconfigObj["vpnDnsPort"];
                ViewModel.ForeignDns = (string)dnfconfigObj["foreignDns"];
                ViewModel.InnerCountryDns = (string)dnfconfigObj["innerCountryDns"];
                ViewModel.GlobalDns = (string)dnfconfigObj["globalDns"];
            }
            finally
            {
                SettingsViewModel._fileLock.Release();
            }
        });
    }


    private void ForeignDnsTextBox_KeyDown(object sender, KeyEventArgs e)
    {
        if (e.Key == Key.Enter)
        {
            if (DataContext is SettingsViewModel vm)
            {
                vm.ExitEditModeForeignDnsCommand.Execute().Subscribe();
            }
        }
    }

    private void ForeignDnsTextBox_LostFocus(object sender, RoutedEventArgs e)
    {
        if (DataContext is SettingsViewModel vm)
        {
            vm.ExitEditModeForeignDnsCommand.Execute().Subscribe();
        }
    }

    private void InnerCountryDnsTextBox_KeyDown(object sender, KeyEventArgs e)
    {
        if (e.Key == Key.Enter)
        {
            if (DataContext is SettingsViewModel vm)
            {
                vm.ExitEditModeInnerCountryDnsCommand.Execute().Subscribe();
            }
        }
    }

    private void InnerCountryDnsTextBox_LostFocus(object sender, RoutedEventArgs e)
    {
        if (DataContext is SettingsViewModel vm)
        {
            vm.ExitEditModeInnerCountryDnsCommand.Execute().Subscribe();
        }
    }

    private void GlobalDnsTextBox_KeyDown(object sender, KeyEventArgs e)
    {
        if (e.Key == Key.Enter)
        {
            if (DataContext is SettingsViewModel vm)
            {
                vm.ExitEditModeGlobalDnsCommand.Execute().Subscribe();
            }
        }
    }

    private void GlobalDnsTextBox_LostFocus(object sender, RoutedEventArgs e)
    {
        if (DataContext is SettingsViewModel vm)
        {
            vm.ExitEditModeGlobalDnsCommand.Execute().Subscribe();
        }
    }

    private void Image_MouseLeftButtonUp(object sender, MouseButtonEventArgs e)
    {
        if (DataContext is SettingsViewModel vm)
        {
            vm.UpdateGeoCommand.Execute().Subscribe();
        }
    }


}
