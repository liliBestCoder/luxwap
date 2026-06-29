using System.Reactive.Disposables;
using System.Windows;
using System.Windows.Controls;
using System.Windows.Controls.Primitives;
using System.Windows.Input;
using System.Windows.Media;
using System.Windows.Threading;
using MaterialDesignThemes.Wpf;
using ReactiveUI;
using Splat;
using v2rayN.Base;
using Point = System.Windows.Point;

namespace v2rayN.Views;

public partial class About
{
 
    public About()
    {
        InitializeComponent();
    }

    private void TextBox_TextChanged(object sender, TextChangedEventArgs e)
    {

    }
}
