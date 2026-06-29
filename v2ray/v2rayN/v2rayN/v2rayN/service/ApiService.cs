using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Http;
using System.Threading.Tasks;
using System.Windows;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using ServiceLib.Handler.SysProxy;
using ServiceLib.Models;
using v2rayN.Views;

namespace v2rayN.service;

public class ApiService
{
    private readonly HttpClient _httpClient;

    // ✅ 公共基础 URL，一改全改
    private const string BaseUrl = "http://127.0.0.1:8080";

    public ApiService()
    {
        _httpClient = new HttpClient(new AuthHandler
        {
            InnerHandler = new HttpClientHandler()
        })
        {
            BaseAddress = new Uri(BaseUrl)
        };
    }

    // ===== 通用请求方法 =====
    private async Task<T> SendAsync<T>(HttpMethod method, string path, string token = null, Dictionary<string, string> parameters = null)
    {
        var request = new HttpRequestMessage(method, path);

        if (parameters != null)
            request.Content = new FormUrlEncodedContent(parameters);

        if (!string.IsNullOrEmpty(token))
            request.Headers.Add("token", token);

        var response = await _httpClient.SendAsync(request);
        var json = await response.Content.ReadAsStringAsync();

        return JsonConvert.DeserializeObject<T>(json);
    }

    // ===== 接口封装 =====

    public Task<AjaxResult> LoginAsync(string username, string password, string deviceId, string os, string deviceType, string deviceName)
    {
        var parameters = new Dictionary<string, string>
        {
            { "username", username },
            { "password", password },
            { "deviceId", deviceId },
            { "os", os },
            { "deviceType", deviceType },
            { "deviceName", deviceName }
        };
        return SendAsync<AjaxResult>(HttpMethod.Post, "/api/client/login", null, parameters);
    }

    public Task<AjaxResult> RegisterAsync(string username, string password, string deviceId, string email, string inviteCode = null, string verifyCode = null)
    {
        var parameters = new Dictionary<string, string>
        {
            { "username", username },
            { "password", password },
            { "deviceId", deviceId },
            { "email", email }
        };
        if (!string.IsNullOrEmpty(inviteCode))
            parameters["inviteCode"] = inviteCode;
        if (!string.IsNullOrEmpty(verifyCode))
            parameters["verifyCode"] = verifyCode;

        return SendAsync<AjaxResult>(HttpMethod.Post, "/api/client/register", null, parameters);
    }

    public Task<AjaxResult> GetUserInfoAsync(string token)
        => SendAsync<AjaxResult>(HttpMethod.Get, "/api/client/user-info", token);

    public Task<AjaxResult> ChangePasswordAsync(string oldPassword, string newPassword, string token)
    {
        var parameters = new Dictionary<string, string>
        {
            { "oldPassword", oldPassword },
            { "newPassword", newPassword }
        };
        return SendAsync<AjaxResult>(HttpMethod.Post, "/api/client/change-password", token, parameters);
    }

    public Task<AjaxResult> ResetPasswordAsync(string email, string newPassword, string verifyCode)
    {
        var parameters = new Dictionary<string, string>
        {
            { "email", email },
            { "newPassword", newPassword },
            { "verifyCode", verifyCode }
        };
        return SendAsync<AjaxResult>(HttpMethod.Post, "/api/client/reset-password", null, parameters);
    }

    public Task<AjaxResult> JoinActivityAsync(string auditLink, string token)
    {
        var parameters = new Dictionary<string, string> { { "auditLink", auditLink } };
        return SendAsync<AjaxResult>(HttpMethod.Post, "/api/client/activity-join", token, parameters);
    }

    // ✅ 重新加上 SendCode
    public Task<AjaxResult> SendCodeAsync(string email, string token)
    {
        var parameters = new Dictionary<string, string>
        {
            { "email", email }
        };
        return SendAsync<AjaxResult>(HttpMethod.Post, "/api/client/send-code", token, parameters);
    }

    public Task<AjaxResult> UpdateUserInfoAsync(string nick, string country, string username, string verifyCode, string token)
    {
        var parameters = new Dictionary<string, string>();
        if (!string.IsNullOrEmpty(nick))
            parameters["nick"] = nick;
        if (!string.IsNullOrEmpty(country))
            parameters["country"] = country;
        if (!string.IsNullOrEmpty(username))
            parameters["username"] = username;
        if (!string.IsNullOrEmpty(verifyCode))
            parameters["verifyCode"] = verifyCode;

        return SendAsync<AjaxResult>(HttpMethod.Post, "/api/client/update-user-info", token, parameters);
    }

    public Task<AjaxResult> ActivityRankListAsync(string token)
        => SendAsync<AjaxResult>(HttpMethod.Get, "/api/client/activity-rank-list", token);

    public Task<AjaxResult> LineListAsync(string token)
        => SendAsync<AjaxResult>(HttpMethod.Get, "/api/client/line-list", token);


    public Task<AjaxResult> CreateOauthLoginTaskAsync(
        string provider,
        string deviceId,
        string os,
        string deviceType,
        string deviceName,
        string redirectUri = null)
    {
        var parameters = new Dictionary<string, string>
    {
        { "provider", provider },
        { "deviceId", deviceId },
        { "os", os },
        { "deviceType", deviceType },
        { "deviceName", deviceName }
    };

        if (!string.IsNullOrEmpty(redirectUri))
        {
            parameters["redirectUri"] = redirectUri;
        }

        return SendAsync<AjaxResult>(HttpMethod.Post, "/api/client/task/create", null, parameters);
    }

    public Task<AjaxResult> PollOauthLoginAsync(string taskId)
    {
        var parameters = new Dictionary<string, string>
            {
                { "taskId", taskId }
            };

        // GET 请求通常不带 Content，参数放到 query string
        var path = "/api/client/task/result";
        if (parameters != null && parameters.Count > 0)
        {
            var query = System.Web.HttpUtility.ParseQueryString(string.Empty);
            foreach (var kv in parameters)
            {
                query[kv.Key] = kv.Value;
            }
            path += "?" + query.ToString();
        }

        return SendAsync<AjaxResult>(HttpMethod.Get, path);
    }
}

// ===== 全局401拦截器 =====
public class AuthHandler : DelegatingHandler
{
    private static bool _isShowingLogin;

    protected override async Task<HttpResponseMessage> SendAsync(HttpRequestMessage request, System.Threading.CancellationToken cancellationToken)
    {
        var response = await base.SendAsync(request, cancellationToken);

        try
        {
            // 如果是 JSON 响应，读取内容
            if (response.Content.Headers.ContentType?.MediaType == "application/json")
            {
                var content = await response.Content.ReadAsStringAsync(cancellationToken);

                // 尝试简单检测 Code=401
                // ⚠️ 不需要反序列化成对象，防止性能损耗
                if (content.Contains("\"Code\":401") || content.Contains("\"code\":401"))
                {
                    HandleUnauthorized();
                }
            }
            else if (response.StatusCode == HttpStatusCode.Unauthorized)
            {
                // 如果后端真的是返回 HTTP 401，也一起处理
                HandleUnauthorized();
            }
        }
        catch (Exception ex)
        {
            Console.WriteLine("401 检测出错：" + ex.Message);
        }

        return response;
    }


    private async Task<int> Stop() {
        var _config = AppHandler.Instance.Config;
        _config.SystemProxyItem.SysProxyType = ESysProxyType.ForcedClear;
        await ConfigHandler.SaveConfig(_config);

        await CoreHandler.Instance.CoreStop();
        await SysProxyHandler.UpdateSysProxy(_config, false);
        await Task.Delay(100);
        return 1;
    }

    private void HandleUnauthorized()
    {
        if (_isShowingLogin)
            return;

        _isShowingLogin = true;

        Application.Current.Dispatcher.Invoke(async () =>
        {
            bool found = false;
            foreach (Window win in Application.Current.Windows)
            {
                if (win is Login)
                {
                    found = true;
                    win.Activate();
                    break;
                }
            }

            if (!found)
            {
                var login = new Login();
                login.Show();

                // 可选：关闭其他窗口
                foreach (Window win in Application.Current.Windows)
                {
                    if (win != login)
                        win.Close();
                }
            }

            await Stop();
        });

        _isShowingLogin = false;
    }
}


// ===== 统一返回模型 =====
public class AjaxResult
{
    [JsonProperty("code")]
    public string Code { get; set; }

    [JsonProperty("msg")]
    public string Msg { get; set; }

    [JsonProperty("data")]
    public object Data { get; set; }
}
