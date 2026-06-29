using System.Globalization;
using System.Windows;
using System.Windows.Data;

namespace v2rayN.Converters;

public class BoolToVisibilityConverter : IValueConverter
{
    // Text 为空 → 显示占位符
    public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
    {
        var v = (bool)value;

        if (parameter is string str && str == "Invert")
        {
            v = !v;
        }
        return v ? Visibility.Visible : Visibility.Collapsed;
    }

    public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
    {
        throw new NotImplementedException();
    }
}
