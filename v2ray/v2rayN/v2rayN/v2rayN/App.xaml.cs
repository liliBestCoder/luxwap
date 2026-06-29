using System.Diagnostics;
using System.Text.Json.Nodes;
using System.Windows;
using System.Windows.Threading;
using Newtonsoft.Json.Linq;
using Newtonsoft.Json.Linq;
using ServiceLib.Handler.SysProxy;
using v2rayN.service;
using v2rayN.util;
using v2rayN.Views;

namespace v2rayN;

/// <summary>
/// Interaction logic for App.xaml
/// </summary>
public partial class App : Application
{
    public static EventWaitHandle ProgramStarted;
    public static ApiService apiService;

    public App()
    {
        this.DispatcherUnhandledException += App_DispatcherUnhandledException;
        AppDomain.CurrentDomain.UnhandledException += CurrentDomain_UnhandledException;
        TaskScheduler.UnobservedTaskException += TaskScheduler_UnobservedTaskException;
        apiService = new ApiService();
    }

    /// <summary>
    /// Open only one process
    /// </summary>
    /// <param name="e"></param>
    protected override  void OnStartup(StartupEventArgs e)
    {
        var exePathKey = Utils.GetMd5(Utils.GetExePath());

        var rebootas = (e.Args ?? Array.Empty<string>()).Any(t => t == Global.RebootAs);
        ProgramStarted = new EventWaitHandle(false, EventResetMode.AutoReset, exePathKey, out bool bCreatedNew);
        if (!rebootas && !bCreatedNew)
        {
            ProgramStarted.Set();
            Environment.Exit(0);
            return;
        }

        if (!AppHandler.Instance.InitApp())
        {
            UI.Show($"Loading GUI configuration file is abnormal,please restart the application{Environment.NewLine}加载GUI配置文件异常,请重启应用");
            Environment.Exit(0);
            return;
        }

        AppHandler.Instance.InitComponents();    
        base.OnStartup(e);

        TokenManager.LoadTokenFromFile();

        _ = InitUserInfoAsync();

    }

    private async Task InitUserInfoAsync()
    {
        if (!string.IsNullOrEmpty(TokenManager.Token))
        {
            var userInfo = await App.apiService.GetUserInfoAsync(TokenManager.Token);

            if (userInfo != null && userInfo.Code == "0")
            {
                var jsonObject = JObject.Parse(userInfo.Data.ToString());
                TokenManager.SetUserInfo(jsonObject);

                Application.Current.Dispatcher.Invoke(() =>
                {
                    var mainWindow = new MainWindowNew();
                    Application.Current.MainWindow = mainWindow;
                    mainWindow.Show();
                });

                return;
            }
        }
        else
        {
            // Token 无效或获取用户信息失败，显示登录窗口
            Application.Current.Dispatcher.Invoke(() =>
            {
                var loginWindow = new Login();
                Application.Current.MainWindow = loginWindow;
                loginWindow.Show();
            });

        }


    }

    private void App_DispatcherUnhandledException(object sender, DispatcherUnhandledExceptionEventArgs e)
    {
        Logging.SaveLog("App_DispatcherUnhandledException", e.Exception);
        e.Handled = true;
    }

    private void CurrentDomain_UnhandledException(object sender, UnhandledExceptionEventArgs e)
    {
        if (e.ExceptionObject != null)
        {
            Logging.SaveLog("CurrentDomain_UnhandledException", (Exception)e.ExceptionObject);
        }
    }

    private void TaskScheduler_UnobservedTaskException(object? sender, UnobservedTaskExceptionEventArgs e)
    {
        Logging.SaveLog("TaskScheduler_UnobservedTaskException", e.Exception);
    }

    protected override void OnExit(ExitEventArgs e)
    {
        Logging.SaveLog("OnExit");
        base.OnExit(e);
        Process.GetCurrentProcess().Kill();
    }
}
