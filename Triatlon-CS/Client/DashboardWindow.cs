using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Data;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Windows.Forms;
using Triatlon_CSharp.Domain;

namespace Client
{
    public partial class DashboardWindow : Form
    {
        private readonly ClientController ctrl;
        private int idParticipant;
        private User currentUser;
        private DataTable table1;
        private DataTable table2;

        public DashboardWindow(ClientController ctrl, User currentUser)
        {
            InitializeComponent();
            this.ctrl = ctrl;
            this.currentUser = currentUser;
            table1 = new DataTable();
            table2 = new DataTable();
            ctrl.updateEvent += userUpdate;
        }

        private void DashboardWindow_Load(object sender, EventArgs e)
        {

            //table.Columns.Add("Id", typeof(int));
            table2.Columns.Add("First Name", typeof(string));
            table2.Columns.Add("Last Name", typeof(string));
            //table.Columns.Add("Score", typeof(double));

            Console.WriteLine(currentUser.Stage.Id);
            IList<Participant> participants = ctrl.getParticipantsByScore(currentUser.Stage.Id);

            foreach (Participant participant in participants)
            {
                table2.Rows.Add(participant.FirstName, participant.LastName);
            }

            dataGridView2.DataSource = table2;
            

            table1.Columns.Add("Id", typeof(int));
            table1.Columns.Add("First Name", typeof(string));
            table1.Columns.Add("Last Name", typeof(string));
            table1.Columns.Add("Score", typeof(double));

            IList<ParticipantDTO> participantsDTO = ctrl.getParticipantsAndScore();

            foreach (ParticipantDTO participant in participantsDTO)
            {
                table1.Rows.Add(participant.Id, participant.FirstName, participant.LastName, participant.Score);
            }

            dataGridView1.DataSource = table1;
        }

        private void dataGridView1_CellContentClick(object sender, DataGridViewCellEventArgs e)
        {
            if (e.RowIndex >= 0)
            {
                DataGridViewRow row = this.dataGridView1.Rows[e.RowIndex];

                string id = row.Cells["Id"].Value.ToString();
                string firstName = row.Cells["First Name"].Value.ToString();
                string lastName = row.Cells["Last Name"].Value.ToString();

                textName.Text = "Score for " + firstName + " " + lastName + ":";

                idParticipant = Convert.ToInt32(id);
            }
        }

        private void buttonAdd_Click(object sender, EventArgs e)
        {
            double score = Convert.ToDouble(textScore.Text);

            ctrl.addResult(score, idParticipant);
        }
        
        public void userUpdate(object sender, UserEventArgs e)
        {
            if (e.UserEventType == UserEvent.NewResult)
            {
                Console.WriteLine("[DashboardWindow] NewResult");
                dataGridView1.BeginInvoke(new UpdateDataGridViewCallback(this.updateTables), new Object[] { });
            }
        }
        //for updating the GUI

        //1. define a method for updating the ListBox
        private void updateTables()
        {
            // listBox.DataSource = null;
            // listBox.DataSource = newData;
            table1.Rows.Clear();
            IList<ParticipantDTO> participantsDTO = ctrl.getParticipantsAndScore();

            foreach (ParticipantDTO participant in participantsDTO)
            {
                table1.Rows.Add(participant.Id, participant.FirstName, participant.LastName, participant.Score);
            }

            dataGridView1.DataSource = null;
            dataGridView1.DataSource = table1;
            

            table2.Rows.Clear();
            IList<Participant> participants = ctrl.getParticipantsByScore(currentUser.Stage.Id);

            foreach (Participant participant in participants)
            {
                table2.Rows.Add(participant.FirstName, participant.LastName);
            }

            dataGridView2.DataSource = null;
            dataGridView2.DataSource = table2;
        }

        //2. define a delegate to be called back by the GUI Thread
        public delegate void UpdateDataGridViewCallback();
    }
}
