using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Newtonsoft.Json.Linq;

namespace v2rayN.util;

public static class TokenManager
{
    private static string _token;
    private static JObject _userinfo;
    private static string _subId;


    private static readonly string TokenFilePath = Path.Combine(
        Environment.GetFolderPath(Environment.SpecialFolder.ApplicationData),
        "Luxwap", "token.txt");

    // 内存中获取 Token
    public static string Token => _token;

    public static JObject UserInfo => _userinfo;

    public static string SubId => _subId;

    public static void SetUserInfo(JObject userinfo) {
        _userinfo = userinfo;
    }

    public static void SetSubId(string subId)
    {
        _subId = subId;
    }

    // 设置 Token，同时写入文件
    public static void SetToken(string token)
    {
        _token = token;

        // 确保目录存在
        Directory.CreateDirectory(Path.GetDirectoryName(TokenFilePath)!);

        File.WriteAllText(TokenFilePath, token);
    }

    // 从文件读取 Token
    public static void LoadTokenFromFile()
    {
        if (File.Exists(TokenFilePath))
        {
            _token = File.ReadAllText(TokenFilePath).Trim();
        }
    }

    // 清空 Token（比如登出时）
    public static void ClearToken()
    {
        _token = null;
        if (File.Exists(TokenFilePath))
            File.Delete(TokenFilePath);
    }
}

