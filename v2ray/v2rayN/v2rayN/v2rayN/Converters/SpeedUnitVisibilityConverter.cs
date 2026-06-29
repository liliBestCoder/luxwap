using System;
using System.Globalization;
using System.Windows;
using System.Windows.Data;

namespace v2rayN.Converters;


public class SpeedUnitVisibilityConverter : IValueConverter
{
    public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
    {
        var str = value?.ToString();
        return double.TryParse(str, out _) ? Visibility.Visible : Visibility.Collapsed;
    }

    public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
    {
        throw new NotImplementedException();
    }
}

