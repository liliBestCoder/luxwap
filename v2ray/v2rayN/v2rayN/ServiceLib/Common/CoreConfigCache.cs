using System;
using System.Collections.Concurrent;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace ServiceLib.Common;
public static class CoreConfigCache
{
    // key = 配置文件名（比如 config.json / configTest4672289042186329807.json）
    // value = 配置文件内容（完整 JSON）
    public static readonly ConcurrentDictionary<string, (string Content, DateTime CreatedAt)> Configs = new();

    private static readonly TimeSpan CleanupInterval = TimeSpan.FromMinutes(1); // 每分钟检查一次
    private static readonly TimeSpan ExpireTime = TimeSpan.FromMinutes(10);      // 10分钟过期

    static CoreConfigCache()
    {
        // 类加载时启动清理线程
        Task.Run(CleanupLoop);
    }

    private static async Task CleanupLoop()
    {
        while (true)
        {
            try
            {
                var now = DateTime.Now;
                foreach (var kv in Configs)
                {
                    if (kv.Key.Contains("configTest") && now - kv.Value.CreatedAt > ExpireTime)
                    {
                        Configs.TryRemove(kv.Key, out _);
                    }
                }
            }
            catch
            {
                // 忽略异常，保证循环持续运行
            }

            await Task.Delay(CleanupInterval);
        }
    }
}

