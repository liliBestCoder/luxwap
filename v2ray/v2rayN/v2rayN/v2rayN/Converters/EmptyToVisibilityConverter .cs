using System.Globalization;
using System.Windows;
using System.Windows.Data;

namespace v2rayN.Converters;

public class EmptyToVisibilityConverter : IValueConverter
{
    // Text 为空 → 显示占位符
    public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
    {
        var text = value as string;
        return string.IsNullOrEmpty(text) ? Visibility.Visible : Visibility.Collapsed;
    }

    public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
    {
        throw new NotImplementedException();
    }
}
