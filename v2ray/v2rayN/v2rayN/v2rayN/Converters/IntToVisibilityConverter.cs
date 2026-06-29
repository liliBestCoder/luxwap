using System.Globalization;
using System.Windows;
using System.Windows.Data;

namespace v2rayN.Converters;

public class IntToVisibilityConverter : IValueConverter
{
    // Text 为空 → 显示占位符
    public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
    {
        var v = (int)value;

        if (parameter is string p)
        {
            return v == int.Parse(p) ? Visibility.Visible : Visibility.Collapsed;
        }
        return Visibility.Collapsed;
    }

    public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
    {
        throw new NotImplementedException();
    }
}
