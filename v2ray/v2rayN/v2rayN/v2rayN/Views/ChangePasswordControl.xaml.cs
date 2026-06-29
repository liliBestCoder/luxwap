using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Runtime.CompilerServices;
using System.Text;
using System.Threading.Tasks;
using System.Windows;
using System.Windows.Controls;
using MaterialDesignThemes.Wpf;
using ServiceLib.ViewModels;

namespace v2rayN.Views;
public partial class ChangePasswordControl : UserControl
{
    public event Action<string, string> PasswordChanged;
    public ChangePasswordControl()
    {
        InitializeComponent();
    }

    private void Cancel_Click(object sender, RoutedEventArgs e)
    {
        var session = DialogHost.GetDialogSession("DialogHostMain");
        if (session != null)
            session.Close();
    }

    private void Save_Click(object sender, RoutedEventArgs e)
    {
        var oldPwd = OldPasswordBox?.Password;
        var newPwd = NewPasswordBox?.Password;
        var confirmPwd = ConfirmPasswordBox?.Password;

        if (string.IsNullOrWhiteSpace(oldPwd))
        {
            MessageBox.Show("旧密码不能为空!");
            return;
        }

        if (string.IsNullOrWhiteSpace(newPwd))
        {
            MessageBox.Show("新密码不能为空!");
            return;
        }

        if (string.IsNullOrWhiteSpace(confirmPwd))
        {
            MessageBox.Show("确认密码不能为空!");
            return;
        }

        if (newPwd != confirmPwd)
        {
            MessageBox.Show("新密码和确认密码不一致");
            return;
        }

        PasswordChanged?.Invoke(oldPwd, newPwd);

        var session = DialogHost.GetDialogSession("DialogHostMain");
        if (session != null)
            session.Close();
    }
}

