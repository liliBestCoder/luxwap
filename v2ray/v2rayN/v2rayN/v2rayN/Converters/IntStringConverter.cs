using System;
using System.Globalization;
using System.Windows.Data;


namespace v2rayN.Converters;

public class IntStringConverter : IValueConverter
{
    public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
    {
        return value?.ToString() ?? "0";
    }

    public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
    {
        if (value == null)
            return 0;
        var s = value.ToString();
        if (int.TryParse(s, out var v))
            return v;
        return 0;
    }
}
