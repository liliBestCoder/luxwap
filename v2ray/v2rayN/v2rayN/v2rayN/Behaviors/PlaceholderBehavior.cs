using System.Windows;
using System.Windows.Controls;
using System.Windows.Media;

namespace v2rayN.Behaviors;

public static class PlaceholderBehavior
{
    public static readonly DependencyProperty PlaceholderProperty =
        DependencyProperty.RegisterAttached(
            "Placeholder",
            typeof(string),
            typeof(PlaceholderBehavior),
            new PropertyMetadata(string.Empty, OnPlaceholderChanged));

    public static string GetPlaceholder(DependencyObject control) => (string)control.GetValue(PlaceholderProperty);
    public static void SetPlaceholder(DependencyObject control, string value) => control.SetValue(PlaceholderProperty, value);

    private static void OnPlaceholderChanged(DependencyObject d, DependencyPropertyChangedEventArgs e)
    {
        if (d is TextBox textBox)
        {
            AttachToTextBox(textBox, (string)e.NewValue);
        }
        else if (d is PasswordBox passwordBox)
        {
            AttachToPasswordBox(passwordBox, (string)e.NewValue);
        }
    }

    private static void AttachToTextBox(TextBox textBox, string placeholderText)
    {
        textBox.Loaded += (_, __) =>
        {
            var parentGrid = EnsureParentIsGrid(textBox);
            if (parentGrid == null)
                return;

            var placeholder = CreatePlaceholderTextBlock(placeholderText);
            Panel.SetZIndex(placeholder, 1);
            parentGrid.Children.Add(placeholder);

            void Update() =>
                placeholder.Visibility = string.IsNullOrEmpty(textBox.Text) && !textBox.IsFocused
                    ? Visibility.Visible
                    : Visibility.Collapsed;

            textBox.GotFocus += (_, __) => Update();
            textBox.LostFocus += (_, __) => Update();
            textBox.TextChanged += (_, __) => Update();

            Update();
        };
    }

    private static void AttachToPasswordBox(PasswordBox passwordBox, string placeholderText)
    {
        passwordBox.Loaded += (_, __) =>
        {
            var parentGrid = EnsureParentIsGrid(passwordBox);
            if (parentGrid == null)
                return;

            var placeholder = CreatePlaceholderTextBlock(placeholderText);
            Panel.SetZIndex(placeholder, 1);
            parentGrid.Children.Add(placeholder);

            void Update() =>
                placeholder.Visibility = string.IsNullOrEmpty(passwordBox.Password) && !passwordBox.IsFocused
                    ? Visibility.Visible
                    : Visibility.Collapsed;

            passwordBox.GotFocus += (_, __) => Update();
            passwordBox.LostFocus += (_, __) => Update();
            passwordBox.PasswordChanged += (_, __) => Update();

            Update();
        };
    }

    private static Grid EnsureParentIsGrid(Control control)
    {
        if (control.Parent is Grid grid)
            return grid;

        if (control.Parent is Panel panel)
        {
            var newGrid = new Grid();
            int index = panel.Children.IndexOf(control);
            panel.Children.RemoveAt(index);
            newGrid.Children.Add(control);
            panel.Children.Insert(index, newGrid);
            return newGrid;
        }

        return null; // 不支持的父容器
    }

    private static TextBlock CreatePlaceholderTextBlock(string text)
    {
        return new TextBlock
        {
            Text = text,
            Foreground = Brushes.Gray,
            Margin = new Thickness(12, 0, 0, 0),
            VerticalAlignment = VerticalAlignment.Center,
            IsHitTestVisible = false
        };
    }
}
