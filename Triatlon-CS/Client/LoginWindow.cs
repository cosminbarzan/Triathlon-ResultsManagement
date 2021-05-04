using System;
using System.Windows.Forms;

namespace Client
{
    public partial class LoginWindow : Form
    {
        private ClientController ctrl;

        public LoginWindow(ClientController ctrl)
        {
            InitializeComponent();
            this.ctrl = ctrl;
        }

        private void loginBtn_Click(object sender, System.EventArgs e)
        {
            string username = textUsername.Text;
            string password = textPassword.Text;

            try
            {
                ctrl.login(username, password);

                DashboardWindow dashWin = new DashboardWindow(ctrl, ctrl.getCurrentUser());

                dashWin.Show();
                this.Hide();
            }
            catch (Exception ex)
            {
                Console.WriteLine(ex.Message);
                MessageBox.Show(this, "Login Error " + ex.Message, "Error", MessageBoxButtons.OK, MessageBoxIcon.Error);
                return;
            }
        }
    }
}