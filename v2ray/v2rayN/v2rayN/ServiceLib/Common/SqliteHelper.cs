using System;
using System.Collections;
using System.Linq;
using System.Threading.Tasks;
using SQLite;

namespace ServiceLib.Common;

public sealed class SQLiteHelper
{
    private static readonly Lazy<SQLiteHelper> _instance = new(() => new());
    public static SQLiteHelper Instance => _instance.Value;

    // 硬盘数据库
    private readonly string _diskConnStr;
    private SQLiteConnection _diskDb;
    private SQLiteAsyncConnection _diskDbAsync;

    // 内存数据库
    private readonly string _memoryConnStr = ":memory:";
    private SQLiteAsyncConnection _memoryDbAsync;

    private readonly string _configDB = "guiNDB.db";

    public SQLiteHelper()
    {
        _diskConnStr = Utils.GetConfigPath(_configDB);

        // 硬盘数据库同步 + 异步连接
        _diskDb = new SQLiteConnection(_diskConnStr, false);
        _diskDbAsync = new SQLiteAsyncConnection(_diskConnStr, false);

        // 内存数据库只用异步连接（保证异步建表和查询在同一个连接）
        _memoryDbAsync = new SQLiteAsyncConnection(_memoryConnStr, false);
    }

    private bool IsDiskModel(Type type) =>
        type == typeof(RoutingItem) || type == typeof(DNSItem);

    private SQLiteConnection GetConnection(Type type) =>
        IsDiskModel(type) ? _diskDb : throw new InvalidOperationException("内存表只能用异步连接");

    private SQLiteAsyncConnection GetAsyncConnection(Type type) =>
        IsDiskModel(type) ? _diskDbAsync : _memoryDbAsync;

    // ------------------- 建表 API -------------------
    // 保留老同步建表接口
    public CreateTableResult CreateTable<T>() where T : new()
    {
        var type = typeof(T);
        if (IsDiskModel(type))
        {
            return _diskDb.CreateTable<T>();
        }
        else
        {
            // 内存表必须用异步建表，否则异步查询会报表不存在
            var task = _memoryDbAsync.CreateTableAsync<T>();
            task.Wait(); // 阻塞等待完成，保持同步接口兼容
            return task.Result;
        }
    }

    // ------------------- 公共 CRUD API -------------------
    public async Task<int> InsertAsync(object model)
        => await GetAsyncConnection(model.GetType()).InsertAsync(model);

    public async Task<int> InsertAllAsync(IEnumerable models)
    {
        if (models == null)
            return 0;

        var list = models.Cast<object>().ToList();
        if (!list.Any())
            return 0;

        var type = list[0].GetType();
        return await GetAsyncConnection(type).InsertAllAsync(list);
    }

    public async Task<int> ReplaceAsync(object model)
        => await GetAsyncConnection(model.GetType()).InsertOrReplaceAsync(model);

    public async Task<int> UpdateAsync(object model)
        => await GetAsyncConnection(model.GetType()).UpdateAsync(model);

    public async Task<int> UpdateAllAsync(IEnumerable models)
    {
        if (models == null)
            return 0;

        var list = models.Cast<object>().ToList();
        if (!list.Any())
            return 0;

        var type = list[0].GetType();
        return await GetAsyncConnection(type).UpdateAllAsync(list);
    }

    public async Task<int> DeleteAsync(object model)
        => await GetAsyncConnection(model.GetType()).DeleteAsync(model);

    public async Task<int> DeleteAllAsync<T>() where T : new()
        => await GetAsyncConnection(typeof(T)).DeleteAllAsync<T>();

    public async Task<int> ExecuteAsync(string sql)
        => await _memoryDbAsync.ExecuteAsync(sql);

    public async Task<System.Collections.Generic.List<T>> QueryAsync<T>(string sql) where T : new()
        => await GetAsyncConnection(typeof(T)).QueryAsync<T>(sql);

    public AsyncTableQuery<T> TableAsync<T>() where T : new()
        => GetAsyncConnection(typeof(T)).Table<T>();

    public async Task DisposeDbConnectionAsync()
    {
        await Task.Factory.StartNew(() =>
        {
            _diskDb?.Close();
            _diskDb?.Dispose();
            _diskDb = null;

            _diskDbAsync?.GetConnection()?.Close();
            _diskDbAsync?.GetConnection()?.Dispose();
            _diskDbAsync = null;

            _memoryDbAsync?.GetConnection()?.Close();
            _memoryDbAsync?.GetConnection()?.Dispose();
            _memoryDbAsync = null;
        });
    }
}
