using System.ComponentModel;
using System.Runtime.CompilerServices;

namespace v2rayN.ViewModels;


public class ChangePasswordViewModel : INotifyPropertyChanged
{
    private string _oldPassword;
    private string _newPassword;
    private string _confirmPassword;

    public string OldPassword
    {
        get => _oldPassword;
        set { _oldPassword = value; OnPropertyChanged(); }
    }

    public string NewPassword
    {
        get => _newPassword;
        set { _newPassword = value; OnPropertyChanged(); }
    }

    public string ConfirmPassword
    {
        get => _confirmPassword;
        set { _confirmPassword = value; OnPropertyChanged(); }
    }

    public event PropertyChangedEventHandler PropertyChanged;
    protected void OnPropertyChanged([CallerMemberName] string name = null) =>
        PropertyChanged?.Invoke(this, new PropertyChangedEventArgs(name));
}

