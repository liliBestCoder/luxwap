using System.ComponentModel;
using System.Reactive.Disposables;
using System.Runtime.InteropServices.JavaScript;
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
using static MaterialDesignThemes.Wpf.Theme.ToolBar;
using Point = System.Windows.Point;

namespace v2rayN.Views;

public partial class ShareGift
{

    public ShareGift()
    {
        InitializeComponent();
        DataContext = new ShareGiftViewModel();
        RankList();
    }

    private async void RankList()
    {
        var result = await App.apiService.ActivityRankListAsync(TokenManager.Token);
        if (result.Code == "0")
        {
            var jsonObject = JObject.Parse(result.Data.ToString());
            TotalCnt.Text = jsonObject["total"]?.ToString();
            RestCnt.Text = jsonObject["rest"]?.ToString();


            var itemList = new List<Item>();
            // 遍历数组
            foreach (var rankOne in jsonObject["rankList"])
            {
                // item 是 JObject，可以取字段
                var username = rankOne["userName"]?.ToString();
                var expiration = rankOne["expiration"]?.ToString();
                var rank = rankOne["rank"]?.ToString();
                var rankLable = "幸运奖";

                if (rank == "1")
                {
                    rankLable = "一等奖";
                }
                else if (rank == "2")
                {
                    rankLable = "二等奖";
                }
                else if (rank == "3")
                {
                    rankLable = "三等奖";
                }
                else if (rank == "4")
                {
                    rankLable = "四等奖";
                }

                var item = new Item
                {
                    UserName = username,
                    ExpireDate = expiration,
                    Reward = rankLable
                };


                itemList.Add(item);
            }

            UserList.ItemsSource = itemList;
        }
        else
        {
            MessageBox.Show(result.Msg);
        }
    }

    public class Item
    {
        public string UserName { get; set; }
        public string ExpireDate { get; set; }
        public string Reward { get; set; }

    }


    public class ShareGiftViewModel : INotifyPropertyChanged
    {
        private string _inputText;
        public string InputText
        {
            get => _inputText;
            set
            {
                if (_inputText != value)
                {
                    _inputText = value;
                    OnPropertyChanged(nameof(InputText));
                }
            }
        }

        public event PropertyChangedEventHandler? PropertyChanged;
        protected void OnPropertyChanged(string propertyName)
        {
            PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(propertyName));
        }
    }

    public async void Send_MouseLeftButtonDown(object sender, MouseButtonEventArgs e) {

        var auditLink = AuditLink.Text;
        // ✅ 校验 auditLink 是否为合法 URL
        if (!Uri.TryCreate(auditLink, UriKind.Absolute, out var uriResult)
            || (uriResult.Scheme != Uri.UriSchemeHttp && uriResult.Scheme != Uri.UriSchemeHttps))
        {
           MessageBox.Show("无效的 auditLink URL，请检查格式。");
           return;
        }
        var result = await App.apiService.JoinActivityAsync(auditLink, TokenManager.Token);
        if (result.Code == "0")
        {
            MessageBox.Show("提交成功!");

            RankList();
        }
        else
        {
            MessageBox.Show(result.Msg);
        }

    }

}
