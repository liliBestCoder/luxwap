using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;

namespace v2rayN.Behaviors;
public static class PasswordBoxAssistant
{
    public static readonly DependencyProperty BoundPassword =
        DependencyProperty.RegisterAttached(
            "BoundPassword",
            typeof(string),
            typeof(PasswordBoxAssistant),
            new PropertyMetadata(string.Empty, OnBoundPasswordChanged)
        );

    public static string GetBoundPassword(DependencyObject obj) => (string)obj.GetValue(BoundPassword);
    public static void SetBoundPassword(DependencyObject obj, string value) => obj.SetValue(BoundPassword, value);

    private static void OnBoundPasswordChanged(DependencyObject d, DependencyPropertyChangedEventArgs e)
    {
        if (d is PasswordBox pb)
        {
            pb.PasswordChanged -= Pb_PasswordChanged;
            if (pb.Password != (string)e.NewValue)
                pb.Password = (string)e.NewValue;
            pb.PasswordChanged += Pb_PasswordChanged;
        }
    }

    private static void Pb_PasswordChanged(object sender, RoutedEventArgs e)
    {
        if (sender is PasswordBox pb)
            SetBoundPassword(pb, pb.Password);
    }
}
