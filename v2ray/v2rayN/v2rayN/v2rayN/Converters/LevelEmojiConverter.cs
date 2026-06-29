using System;
using System.Globalization;
using System.Windows.Data;

namespace v2rayN.Converters;

public class LevelEmojiConverter : IValueConverter
{
    public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
    {
        if (value == null)
            return "";

        var stars = System.Convert.ToInt32(value);

        var moons = stars / 6;
        stars %= 6;

        var suns = moons / 6;
        moons %= 6;

        var crowns = suns / 6;
        suns %= 6;

        string result =
                string.Concat(Enumerable.Repeat("👑", crowns)) +
                string.Concat(Enumerable.Repeat("☀️", suns)) +
                string.Concat(Enumerable.Repeat("🌙", moons)) +
                string.Concat(Enumerable.Repeat("⭐", stars));

        // 如果全是 0，可以显示 “无等级”
        return string.IsNullOrEmpty(result) ? "无" : result;
    }

    public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
    {
        throw new NotImplementedException();
    }
}

